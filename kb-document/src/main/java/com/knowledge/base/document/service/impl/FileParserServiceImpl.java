package com.knowledge.base.document.service.impl;

import com.knowledge.base.document.service.FileParserService;
import com.knowledge.base.document.service.FileUploadService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 文件解析Service实现
 *
 * <p>PDF/DOCX/PPTX/XLSX → Java 原生解析（PDFBox / Apache POI）。</p>
 * <p>纯文本(.txt)和 Markdown(.md) 直接读取。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class FileParserServiceImpl implements FileParserService {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            "pdf", "docx", "xlsx", "pptx", "txt", "md"
    );

    @Resource
    private TikaConverterService tikaConverter;

    @Resource
    private FileUploadService fileUploadService;

    @Override
    public boolean isSupported(String extension) {
        return extension != null && SUPPORTED_EXTENSIONS.contains(extension.toLowerCase());
    }

    @Override
    public String parse(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        if (!isSupported(extension)) {
            throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }

        // 防御性检查：确保文件流可读（Feign 上传后流可能已被消费）
        if (file.getSize() > 0) {
            try (InputStream testStream = file.getInputStream()) {
                byte[] firstBytes = new byte[16];
                int read = testStream.read(firstBytes);
                log.debug("文件可读性检查：name={}, size={}, firstRead={}bytes",
                        originalFilename, file.getSize(), read);
            } catch (Exception e) {
                log.error("文件流不可读：name={}, error={}", originalFilename, e.getMessage());
                throw new IllegalArgumentException("文件流不可读，无法解析: " + e.getMessage());
            }
        }

        long startTime = System.currentTimeMillis();

        String result;
        switch (extension.toLowerCase()) {
            case "docx":
                // DOCX: Java POI 直接解析，保留标题层级 # Heading 格式
                result = parseDocx(file);
                break;
            case "pdf":
                // PDF: Java PDFBox 直接解析
                result = parsePdf(file);
                break;
            case "pptx":
                // PPTX: Java POI 直接解析，保留 ## 幻灯片 N 结构
                result = parsePptx(file);
                break;
            case "xlsx":
                // XLSX: Tika 优先（Markdown 表格 + 分隔行），失败降级到 Java POI
                result = withFallback(file, extension,
                        () -> tikaConverter.parseXlsx(file),
                        this::parseXlsx);
                break;
            case "txt":
            case "md":
                result = parsePlainText(file);
                break;
            default:
                throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("文件解析完成: name={}, format={}, chars={}, elapsed={}ms",
                originalFilename, extension, result.length(), elapsed);

        return result;
    }

    /**
     * 优先使用 Tika 转换，失败时降级到 Java 解析。
     */
    @FunctionalInterface
    private interface Converter {
        String convert() throws Exception;
    }

    @FunctionalInterface
    private interface Fallback {
        String parse(MultipartFile file) throws Exception;
    }

    private String withFallback(MultipartFile file, String extension,
                                 Converter converter, Fallback fallback) throws Exception {
        try {
            long start = System.currentTimeMillis();
            String result = converter.convert();
            long elapsed = System.currentTimeMillis() - start;
            log.info("Tika 解析成功: format={}, chars={}, elapsed={}ms",
                    extension, result.length(), elapsed);
            return result;
        } catch (Exception e) {
            log.warn("Tika 转换失败，降级到 Java 解析: format={}, error={}",
                    extension, e.getMessage());
        }

        return fallback.parse(file);
    }

    // ── PDF 解析 ──────────────────────────────────────────

    /**
     * 带字体元数据的行信息
     */
    /** 图片在页面上的位置信息 */
    private static class ImagePlacement {
        final float y;       // Y 坐标（页面顶部向下，与 getYDirAdj 一致）
        final String url;    // 图片 URL

        ImagePlacement(float y, String url) {
            this.y = y;
            this.url = url;
        }
    }

    private static class PdfLine {
        final String text;
        final float fontSize;
        final boolean isBold;
        final boolean isMono;
        /** Y 坐标（页面顶部向下），Float.NaN 表示非文本行（如 PAGE_BREAK 标记） */
        final float y;
        /**
         * 非 null 时表示此行检测到了表格列，存储各列文本。
         * 同时存储列起始 X 坐标用于跨行匹配对齐。
         */
        String[] columns;
        float[] columnX;
        /** 原始按 X 排序后的位置 X 坐标与文本（用于模板列重分配） */
        float[] posXs;
        String[] posTexts;

        PdfLine(String text, float fontSize, boolean isBold, boolean isMono) {
            this(text, fontSize, isBold, isMono, Float.NaN);
        }

        PdfLine(String text, float fontSize, boolean isBold, boolean isMono, float y) {
            this.text = text;
            this.fontSize = fontSize;
            this.isBold = isBold;
            this.isMono = isMono;
            this.y = y;
        }
    }

    /**
     * 自定义 PDFTextStripper：通过覆盖 {@code writeString(text, positions)}
     * 拦截 PDFBox 已经按 Y 坐标分好行的文本，从 TextPosition 提取字体元数据，
     * 最后由 {@code buildMarkdown()} 统一格式化为 Markdown。
     *
     * <p><b>核心设计：</b>不覆盖 {@code processTextPosition} ——
     * 让父类完整执行行检测逻辑（基于 Y 坐标变化），
     * 只在 {@code writeString / writeLineSeparator} 出口拦截结果。
     * 如果覆盖 {@code processTextPosition} 且不调用 super，
     * 父类将无法检测换行，所有文本合并为一个 blob。</p>
     */
    private static class CollectingPDFStripper extends PDFTextStripper {
        private final List<PdfLine> lines = new ArrayList<>();
        /** 连续 writeLineSeparator 计数 → 段落间隔 */
        private int consecutiveSeparators = 0;
        /** 上一行（已 flush 的行）的 Y 坐标，用于段落间距检测 */
        private float prevLineY = Float.NaN;
        /** 行间距的指数移动平均 */
        private float runningLineGap = 0;
        /** 各页图片的位置信息，由 parsePdf 通过 setImagePlacements 注入 */
        private Map<Integer, List<ImagePlacement>> pageImages;

        // ── 当前行累积器（PDFBox writeString 是逐词调用的，需要累积完整行后再做列检测） ──
        private final StringBuilder curText = new StringBuilder();
        private final List<TextPosition> curPositions = new ArrayList<>();
        private float curMaxFontSize = 0;
        private boolean curIsBold = false;
        private boolean curIsMono = false;
        private float curY = Float.NaN;

        CollectingPDFStripper() throws IOException {
            super();
            setSortByPosition(true);
            setAddMoreFormatting(false);
            setSuppressDuplicateOverlappingText(true);
        }

        void setImagePlacements(Map<Integer, List<ImagePlacement>> pageImages) {
            this.pageImages = pageImages;
        }

        /**
         * PDFBox 对同一行内的每个「词」（由空格/大间隙分隔的连续文本）调用一次此方法。
         * 我们不直接生成 PdfLine —— 先累积文本和 TextPosition，
         * 等 writeLineSeparator() 通知行结束时再 flush，这样才能在完整行的基础上做列检测。
         */
        @Override
        protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
            String cleaned = text.stripTrailing();
            if (cleaned.isEmpty()) return;

            // 防御：如果 Y 坐标明显变化（> 2pt），说明 PDFBox 未插入 writeLineSeparator 就开始了新行
            float maxY = Float.NaN;
            for (TextPosition tp : textPositions) {
                float y = tp.getYDirAdj();
                if (Float.isNaN(maxY) || y > maxY) maxY = y;
            }
            if (!Float.isNaN(curY) && !Float.isNaN(maxY) && Math.abs(maxY - curY) > 2f) {
                flushCurrentLine();
            }

            // 累积文本（词间加空格）
            if (curText.length() > 0) curText.append(' ');
            curText.append(cleaned);
            curPositions.addAll(textPositions);

            // 累积字体元数据
            for (TextPosition tp : textPositions) {
                float fs = tp.getFontSizeInPt();
                if (fs > curMaxFontSize) curMaxFontSize = fs;
                float y = tp.getYDirAdj();
                if (Float.isNaN(curY) || y > curY) curY = y;
                if (tp.getFont() != null) {
                    String fn = tp.getFont().getName().toLowerCase();
                    if (fn.contains("bold") || fn.contains("semibold")
                            || fn.contains("heavy") || fn.contains("black")) {
                        curIsBold = true;
                    }
                    if (isMonospacePdfFont(fn)) {
                        curIsMono = true;
                    }
                }
            }
        }

        /**
         * 基于 TextPosition 的 X 坐标检测表格列。
         * 如果在同一行中检测到多个明显的水平间隙（> 12pt），
         * 则判定为表格行，拆分出各列文本。
         */
        private void detectTableColumns(PdfLine line, List<TextPosition> positions) {
            if (positions.size() < 3) return; // 太少片段，不足以形成表格

            // 按 X 坐标排序
            List<TextPosition> sorted = new ArrayList<>(positions);
            sorted.sort(Comparator.comparingDouble(TextPosition::getXDirAdj));

            // 始终存储原始排序后的位置数据，供后续 deteTableGroups 做模板列重分配
            line.posXs = new float[sorted.size()];
            line.posTexts = new String[sorted.size()];
            for (int i = 0; i < sorted.size(); i++) {
                line.posXs[i] = sorted.get(i).getXDirAdj();
                line.posTexts[i] = sorted.get(i).getUnicode();
            }

            // 第一遍：找出列间隙（X 间距 > 12pt 的位置）
            List<Integer> gapBefore = new ArrayList<>();
            float prevEndX = -Float.MAX_VALUE;
            for (int i = 0; i < sorted.size(); i++) {
                TextPosition tp = sorted.get(i);
                float x = tp.getXDirAdj();
                if (i > 0 && prevEndX > 0 && x - prevEndX > 12f) {
                    gapBefore.add(i);
                }
                prevEndX = Math.max(prevEndX, x + tp.getWidthDirAdj());
            }

            // 需要至少 1 个间隙 → 至少 2 列
            if (gapBefore.isEmpty()) return;

            // 第二遍：提取各列文本
            int colCount = gapBefore.size() + 1;
            StringBuilder[] colTexts = new StringBuilder[colCount];
            float[] colX = new float[colCount];
            Arrays.setAll(colTexts, i -> new StringBuilder());

            int col = 0;
            for (int i = 0; i < sorted.size(); i++) {
                while (col < gapBefore.size() && i == gapBefore.get(col)) {
                    col++;
                }
                if (colTexts[col].length() == 0) {
                    colX[col] = sorted.get(i).getXDirAdj();
                }
                colTexts[col].append(sorted.get(i).getUnicode());
            }

            // 清洗各列文本
            String[] cols = new String[colCount];
            for (int c = 0; c < colCount; c++) {
                cols[c] = colTexts[c].toString().strip();
            }

            line.columns = cols;
            line.columnX = colX;
        }

        /**
         * 检查上一个非空行是否以中文句子结束标点结尾（。！？）
         */
        private boolean prevLineEndsWithSentencePunct() {
            for (int i = lines.size() - 1; i >= 0; i--) {
                String t = lines.get(i).text;
                if (t.isEmpty() || t.startsWith("__PAGE_BREAK__")) continue;
                return t.matches(".*[。！？]$");
            }
            return false;
        }

        /**
         * 检查当前行是否看起来像是一个新章节的开始。
         * 中文 PDF 中常见的新段落/新小节开头标记。
         */
        private boolean looksLikeSectionStart(String text) {
            if (text == null || text.isEmpty()) return false;
            // 中文编号：一、二、三...
            if (text.matches("^[一二三四五六七八九十]+[、，\\s].*")) return true;
            // 数字编号：1. 2. 3. 或 1、2、3、
            if (text.matches("^\\d+[.、)]\\s.*")) return true;
            // 常见新章节关键词
            if (text.matches("^(推荐|使用|示例|注意|提示|说明|总结|总之|因此|所以|另外|此外|同时|然而|但是|首先|其次|最后|然后|接着|接下来|配置|方案|步骤|问题|优势|特点|定义|背景|目标|需求|实现|测试|部署|运维|监控|参考).*")) return true;
            return false;
        }

        /**
         * 检查前一行是否是一个「短独立行」（≤ 15 字符 + 以。！？结尾）。
         * 这类行通常是迷你标题或独立陈述，后面应该接新段落。
         * 例如：「用工具在线建索引。」、「配置变更。」、「前言」
         */
        private boolean isPrevLineShortStandalone() {
            PdfLine prev = null;
            for (int i = lines.size() - 1; i >= 0; i--) {
                String t = lines.get(i).text;
                if (t.isEmpty() || t.startsWith("__PAGE_BREAK__")) continue;
                prev = lines.get(i);
                break;
            }
            if (prev == null) return false;
            String t = prev.text.strip();
            // 短行（≤ 15 字符）且以句子结束标点结尾 → 独立行
            if (t.length() <= 15 && t.matches(".*[。！？]$")) {
                return true;
            }
            // 短行（≤ 10 字符）即使没有标点也可能是独立行
            // （如 PDF 中因截断导致后面无标点，但内容本身是独立陈述）
            if (t.length() <= 10 && (t.matches(".*[。！？]$") || t.endsWith("）"))) {
                return true;
            }
            return false;
        }

        /**
         * PDFBox 每行结束后调用。
         * 先 flush 累积的当前行，再递增分隔计数。
         */
        @Override
        protected void writeLineSeparator() throws IOException {
            flushCurrentLine();
            consecutiveSeparators++;
        }

        /**
         * 将当前累积行（跨多个 writeString 调用的所有词 + TextPosition） flush 为一条 PdfLine。
         * 此时 curPositions 包含完整行所有字符位置 → 列检测能正确工作。
         */
        private void flushCurrentLine() {
            if (curText.length() == 0) return;

            String text = curText.toString();
            float lineY = curY;

            // ── 段落边界检测（逻辑与旧 writeString 相同，只是作用在累积完成的整行上） ──
            boolean isParagraphBreak = false;

            // 方法 A: Y 间距检测
            if (!Float.isNaN(prevLineY) && !Float.isNaN(lineY)) {
                float gap = lineY - prevLineY;
                if (gap > 0 && runningLineGap > 0) {
                    if (gap > runningLineGap * 1.8f) {
                        isParagraphBreak = true;
                    } else if (gap > runningLineGap * 1.25f && prevLineEndsWithSentencePunct()) {
                        isParagraphBreak = true;
                    }
                }
                if (gap > 0 && runningLineGap > 0 && gap <= runningLineGap * 1.5f) {
                    runningLineGap = runningLineGap * 0.7f + gap * 0.3f;
                } else if (gap > 0 && runningLineGap == 0) {
                    runningLineGap = gap;
                }
            }
            prevLineY = lineY;

            // 方法 B: 文本特征检测
            if (!isParagraphBreak && prevLineEndsWithSentencePunct()) {
                String ct = text.strip();
                if (ct.length() <= 15 && !ct.matches("^[\\d•\\-].*")) {
                    isParagraphBreak = true;
                } else if (looksLikeSectionStart(ct)) {
                    isParagraphBreak = true;
                }
            }

            // 方法 C: 前一行是短独立行
            if (!isParagraphBreak && isPrevLineShortStandalone()) {
                isParagraphBreak = true;
            }

            // 连续空行 / 段落分隔 → 插入空行
            if ((consecutiveSeparators >= 2 || isParagraphBreak) && !lines.isEmpty()) {
                lines.add(new PdfLine("", 0, false, false));
            }
            consecutiveSeparators = 0;

            // 列检测：curPositions 包含整行所有字符 → 能跨列间隙检测
            PdfLine pdfLine = new PdfLine(text, curMaxFontSize, curIsBold, curIsMono, lineY);
            detectTableColumns(pdfLine, curPositions);
            lines.add(pdfLine);

            // 重置当前行累积器
            curText.setLength(0);
            curPositions.clear();
            curMaxFontSize = 0;
            curIsBold = false;
            curIsMono = false;
            curY = Float.NaN;
        }

        /**
         * PDFBox 每页结束后调用。
         * Flush 当前行 → 插入页面边界标记 → 重置 Y 追踪。
         */
        @Override
        protected void writePageEnd() throws IOException {
            super.writePageEnd();
            flushCurrentLine();
            int pageNum = getCurrentPageNo();
            lines.add(new PdfLine("__PAGE_BREAK__" + pageNum, 0, false, false));
            prevLineY = Float.NaN;
            runningLineGap = 0;
        }

        @Override
        public String getText(PDDocument doc) throws IOException {
            lines.clear();
            consecutiveSeparators = 0;
            curText.setLength(0);
            curPositions.clear();
            curMaxFontSize = 0;
            curIsBold = false;
            curIsMono = false;
            curY = Float.NaN;

            // 使用父类的完整管道（含 processTextPosition 行检测）
            try (StringWriter sw = new StringWriter()) {
                writeText(doc, sw);
            }

            if (lines.isEmpty()) return "";

            // 始终合并页面边界：有图片时按 Y 穿插入 ![](url)，无图片时至少清理 __PAGE_BREAK__ 标记
            mergeImagesByPosition();

            // 基于收集到的行和字体元数据构建 Markdown
            return buildMarkdown();
        }

        /**
         * 将各页图片按 Y 坐标插入到对应页面的文本行之间。
         * 图片和文本统一按 Y（从上到下）排序后合并。
         */
        private void mergeImagesByPosition() {
            List<PdfLine> merged = new ArrayList<>();
            int currentPage = -1;
            int pageStart = 0;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).text.startsWith("__PAGE_BREAK__")) {
                    // 合并当前页的文本和图片
                    mergePageLines(currentPage + 1, pageStart, i, merged);
                    currentPage++;
                    pageStart = i + 1; // 跳过 PAGE_BREAK 标记
                }
            }
            // 最后一页
            if (pageStart < lines.size()) {
                mergePageLines(currentPage + 1, pageStart, lines.size(), merged);
            }

            lines.clear();
            lines.addAll(merged);
        }

        /**
         * 将单页的文本行与图片按 Y 坐标合并。
         */
        private void mergePageLines(int pageNum, int fromIdx, int toIdx, List<PdfLine> output) {
            List<ImagePlacement> images = (pageImages != null) ? pageImages.get(pageNum) : null;

            if (images == null || images.isEmpty()) {
                // 该页无图片，直接复制文本行（PAGE_BREAK 标记已在循环中被跳过）
                for (int i = fromIdx; i < toIdx; i++) {
                    output.add(lines.get(i));
                }
                return;
            }

            // 图片已按 Y 排序（在 extractAndUploadImages 中）
            int imgIdx = 0;
            for (int i = fromIdx; i < toIdx; i++) {
                PdfLine line = lines.get(i);

                // 将 Y 坐标 ≤ 当前文本行的图片全部插入
                while (imgIdx < images.size()
                        && !Float.isNaN(line.y)
                        && images.get(imgIdx).y <= line.y) {
                    ImagePlacement img = images.get(imgIdx);
                    output.add(new PdfLine("![](" + img.url + ")", 0, false, false));
                    output.add(new PdfLine("", 0, false, false)); // 图片后加空行分隔
                    imgIdx++;
                }

                output.add(line);
            }

            // 剩余图片（Y 坐标在页面所有文本之后）
            while (imgIdx < images.size()) {
                ImagePlacement img = images.get(imgIdx);
                output.add(new PdfLine("![](" + img.url + ")", 0, false, false));
                output.add(new PdfLine("", 0, false, false));
                imgIdx++;
            }
        }

        /**
         * 预处理整个 lines 列表：
         * 1. 将 PDFBox 拼合的多个列表项拆分为独立行
         * 2. 将 • 转换为 "- "（标准 markdown 列表语法）
         *
         * <p>必须在 buildMarkdown 的代码检测 / 格式化之前执行，
         * 确保拆分后的行作为独立单元参与后续处理。</p>
         */
        private void normalizeBulletLists() {
            List<PdfLine> normalized = new ArrayList<>();
            for (PdfLine line : lines) {
                if (line.text.isEmpty()) {
                    normalized.add(line);
                    continue;
                }

                // 按 • 拆分（前面至少有 1 个非 • 字符时才拆，避免开头 • 产生空串）
                String[] parts = line.text.split("(?<=[^•])(?=•)");
                if (parts.length <= 1) {
                    // 无合并项：直接转换 • → -
                    normalized.add(convertBullet(line));
                } else {
                    for (String part : parts) {
                        String trimmed = part.strip();
                        if (!trimmed.isEmpty()) {
                            normalized.add(convertBullet(
                                    new PdfLine(trimmed, line.fontSize, line.isBold, false)));
                        }
                    }
                }
            }
            lines.clear();
            lines.addAll(normalized);
        }

        /** 将 • 开头的行转换为 "- " 开头的标准 markdown 列表项 */
        private static PdfLine convertBullet(PdfLine line) {
            String t = line.text;
            if (t.startsWith("• ") || t.startsWith("•\t")) {
                return new PdfLine("- " + t.substring(2), line.fontSize, line.isBold, false);
            } else if (t.startsWith("•")) {
                return new PdfLine("- " + t.substring(1).stripLeading(), line.fontSize, line.isBold, false);
            }
            return line;
        }

        /**
         * 基于收集到的行信息和字体元数据构建格式化的 Markdown
         */
        private String buildMarkdown() {
            // Step 0: 预处理 —— 拆分 PDFBox 合并的列表项，转换 • → - markdown 列表语法
            normalizeBulletLists();

            float baseFontSize = computeBaseFontSize();

            // Step 1: 预计算每行的代码标识
            boolean[] isCodeLine = new boolean[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                PdfLine line = lines.get(i);
                if (!line.text.isEmpty()) {
                    isCodeLine[i] = line.isMono || looksLikeCodeLine(line.text);
                }
            }

            // Step 1.5: 填补代码段之间的 ≤1 行间隙
            fillCodeSegmentGaps(isCodeLine);

            // Step 2: 合并连续代码行为代码块（至少 2 行才成块）
            boolean[] inCodeBlock = new boolean[lines.size()];
            int i = 0;
            while (i < isCodeLine.length) {
                if (isCodeLine[i]) {
                    // 找到连续代码段的起止
                    int start = i;
                    while (i < isCodeLine.length && isCodeLine[i]) i++;
                    int end = i;
                    // 至少 2 行连续代码行 → 标记为代码块
                    if (end - start >= 2) {
                        for (int j = start; j < end; j++) {
                            inCodeBlock[j] = true;
                        }
                    }
                } else {
                    i++;
                }
            }

            // Step 2.5: 将强代码特征的孤立单行也提升为代码块
            // （如单行 SQL: "ALTER TABLE ... ;"）
            for (i = 0; i < lines.size(); i++) {
                if (isCodeLine[i] && !inCodeBlock[i]
                        && isStrongIsolatedCodeLine(lines.get(i).text)) {
                    inCodeBlock[i] = true;
                }
            }

            // Step 2.6: 检测并格式化表格
            // key=起始行索引, value=预格式化的 Markdown 表格文本
            Map<Integer, String> tableBlocks = detectTableGroups();

            // Step 3: 构建 Markdown 输出
            StringBuilder md = new StringBuilder();
            boolean opened = false;
            boolean prevWasBullet = false; // 上一个非空行是否为列表项（- 开头）
            int tableSkipUntil = -1;       // 跳过表格行（已预格式化）

            for (i = 0; i < lines.size(); i++) {
                // 表格块：输出预格式化的 Markdown 表格，跳过原始行
                if (tableBlocks.containsKey(i)) {
                    if (opened) { md.append("```\n"); opened = false; }
                    md.append('\n').append(tableBlocks.get(i)).append('\n');
                    // 跳到表格结束后的下一行
                    // detectTableGroups 中记录了 endIdx，我们可以从 tableBlocks 值中推断
                    // 简单方案：持续跳过有 columns 的行直到遇到非表格行
                    while (i + 1 < lines.size() && lines.get(i + 1).columns != null) {
                        i++;
                    }
                    prevWasBullet = false;
                    continue;
                }

                PdfLine line = lines.get(i);

                // 空行处理：代码块内的空行保留在 fence 内，块外的空行关闭 fence
                if (line.text.isEmpty()) {
                    if (inCodeBlock[i]) {
                        // 代码块内的空白行 → 输出空行保持代码间距，不关闭代码块
                        md.append('\n');
                        continue;
                    }
                    if (opened) {
                        md.append("```\n");
                        opened = false;
                    }
                    md.append('\n');
                    prevWasBullet = false;
                    continue;
                }

                // 代码块输出
                if (inCodeBlock[i]) {
                    // 跳过代码块内的独立语言标签行（"java", "text" 等）
                    if (isLangLabelWord(line.text)) continue;

                    if (!opened) {
                        String lang = guessCodeLanguage(inCodeBlock, i);
                        md.append("\n```").append(lang).append('\n');
                        opened = true;
                    }
                    md.append(line.text).append('\n');
                    continue;
                } else if (opened) {
                    md.append("```\n");
                    opened = false;
                }

                // 非代码行：检查是否为代码块前的独立语言标签（如 "JAVA", "XML"）
                // 如果是，跳过该行（语言标签已整合到 fence 中）
                if (isStandaloneLangLabel(line.text, i, inCodeBlock)) {
                    continue;
                }

                // 标题检测：跳过纯符号行（中文标题可能只有 2-4 字）
                int headingLevel = 0;
                if (line.text.length() >= 2 && !line.text.matches("^[\\d•\\-+\\s]+$")) {
                    headingLevel = computeHeadingLevel(line.fontSize, baseFontSize, line.isBold);
                }
                // 同字号加粗标题（level 6）：仅适用于短行（≤ 20 字符）
                // 长行加粗更可能是强调文字（如标语），不是标题
                if (headingLevel == 6 && line.text.length() > 20) {
                    headingLevel = 0;
                }
                if (headingLevel > 0) {
                    md.append("#".repeat(headingLevel)).append(' ');
                }

                // 行内样式
                String formatted = line.text;
                if (line.isBold && headingLevel == 0) {
                    formatted = "**" + formatted + "**";
                }

                // 列表项 → 非列表项转换：需要空行分隔，否则 markdown 会将后续内容吸收进列表
                boolean isBullet = formatted.startsWith("- ");
                if (prevWasBullet && !isBullet && headingLevel == 0) {
                    md.append('\n');
                }

                md.append(formatted).append('\n');
                prevWasBullet = (headingLevel == 0 && isBullet);
            }

            if (opened) {
                md.append("```\n");
            }

            return cleanMarkdown(md.toString());
        }

        /**
         * 扫描所有行，检测连续表格行组（列数相同、列 X 坐标对齐），
         * 并预格式化为 Markdown 表格。
         *
         * @return 起始行索引 → 预格式化的 Markdown 表格文本
         */
        private Map<Integer, String> detectTableGroups() {
            Map<Integer, String> result = new LinkedHashMap<>();

            for (int i = 0; i < lines.size(); i++) {
                PdfLine line = lines.get(i);
                if (line.columns == null || line.columns.length < 2) continue;
                float[] templateColX = line.columnX; // 第一行的列 X 位置作为模板

                // 找到连续表格行的结尾
                int start = i;
                int end = i + 1;
                while (end < lines.size()) {
                    PdfLine nextLine = lines.get(end);

                    // 尝试①：直接匹配（列数相同 + X 对齐）
                    if (nextLine.columns != null
                            && nextLine.columns.length == line.columns.length
                            && columnsAligned(line.columns, line.columnX,
                                              nextLine.columns, nextLine.columnX)) {
                        end++;
                        continue;
                    }

                    // 尝试②：模板重分配 —— 处理单元格内有逗号/分号等
                    // 产生小间隙误判为列边界、或空单元格导致列数不一致的行
                    if (nextLine.posXs != null && nextLine.posTexts != null && templateColX != null) {
                        applyColumnTemplate(nextLine, templateColX);
                        if (nextLine.columns != null
                                && nextLine.columns.length == line.columns.length
                                && columnsAligned(line.columns, line.columnX,
                                                  nextLine.columns, nextLine.columnX)) {
                            end++;
                            continue;
                        }
                    }

                    break;
                }

                int rowCount = end - start;
                if (rowCount < 2) { i = end - 1; continue; } // 单行不算表格

                // 确保所有行使用相同的列模板（第一行的 columnX）
                for (int r = start; r < end; r++) {
                    PdfLine row = lines.get(r);
                    if (row.columnX != templateColX) {
                        row.columnX = templateColX;
                    }
                }

                // 格式化为 Markdown 表格
                StringBuilder table = new StringBuilder();

                for (int r = start; r < end; r++) {
                    PdfLine row = lines.get(r);
                    table.append("|");
                    for (String col : row.columns) {
                        table.append(' ').append(col.isEmpty() ? " " : col).append(" |");
                    }
                    table.append('\n');

                    // 表头后添加分隔行
                    if (r == start) {
                        table.append("|");
                        for (int c = 0; c < row.columns.length; c++) {
                            table.append(" --- |");
                        }
                        table.append('\n');
                    }
                }

                result.put(start, table.toString());
                i = end - 1; // 跳到表格组末尾
            }

            return result;
        }

        /**
         * 使用第一行的列 X 坐标模板，将本行的原始 TextPosition 重新分配到各列。
         * 用于修复以下场景：
         * <ul>
         *   <li>单元格内逗号 (，) 产生的微小间隙被误判为列边界</li>
         *   <li>空单元格导致行内缺少该 X 坐标的文本片段 → 列数偏少</li>
         * </ul>
         * <p>算法：将各 TextPosition 的 X 坐标与模板列中心比较，
         * 分配到最近的列。空列自然产生空字符串。</p>
         */
        private void applyColumnTemplate(PdfLine line, float[] templateColX) {
            if (line.posXs == null || line.posTexts == null || templateColX == null) return;
            if (templateColX.length < 2) return;

            // 计算列边界中点（用于将 TextPosition 分配到最近的列）
            float[] midpoints = new float[templateColX.length - 1];
            for (int i = 0; i < templateColX.length - 1; i++) {
                midpoints[i] = (templateColX[i] + templateColX[i + 1]) / 2;
            }

            StringBuilder[] colTexts = new StringBuilder[templateColX.length];
            Arrays.setAll(colTexts, i -> new StringBuilder());

            for (int i = 0; i < line.posXs.length; i++) {
                float x = line.posXs[i];
                String text = line.posTexts[i];

                // 二分查找所属列：找到第一个中点 > x，列索引 = 中点索引 + 1
                int col = templateColX.length - 1; // 默认最后一列
                for (int m = 0; m < midpoints.length; m++) {
                    if (x <= midpoints[m]) {
                        col = m;
                        break;
                    }
                }
                colTexts[col].append(text);
            }

            String[] cols = new String[templateColX.length];
            for (int c = 0; c < templateColX.length; c++) {
                cols[c] = colTexts[c].toString().strip();
            }

            line.columns = cols;
            line.columnX = templateColX;
        }

        /**
         * 检查两行的列 X 坐标是否对齐（误差 ≤ 5pt）。
         */
        private boolean columnsAligned(String[] colsA, float[] xA, String[] colsB, float[] xB) {
            if (xA == null || xB == null) return true; // 无 X 数据时宽容处理
            if (xA.length != xB.length) return false;
            for (int i = 0; i < xA.length; i++) {
                if (Math.abs(xA[i] - xB[i]) > 5f) return false;
            }
            return true;
        }

        /**
         * 基于内容特征判断一行是否为代码行（用于字体非等宽的 PDF）
         *
         * <p>不检查包含冒号的模式（如 YAML / 任务描述），避免误判。</p>
         */
        private boolean looksLikeCodeLine(String text) {
            String t = text.strip();

            // 单独的花括号是强代码信号（必须放在长度检查之前）
            if (t.equals("{") || t.equals("}")) return true;

            if (t.length() < 3) return false;

            // Java/C# 关键字或注解开头
            if (t.startsWith("public ") || t.startsWith("private ") || t.startsWith("protected ")
                    || t.startsWith("class ") || t.startsWith("interface ") || t.startsWith("import ")
                    || t.startsWith("package ") || t.startsWith("return ") || t.startsWith("throw ")
                    || t.matches("^(@\\w+.*)")) {
                return true;
            }

            // 行末带 { } ; 是强代码信号
            if (t.endsWith("{") || t.endsWith("}") || t.endsWith(";")) {
                return true;
            }

            // 方法链式调用：以 . 开头（如 .antMatchers() / .permitAll()）
            if (t.startsWith(".") && t.length() > 2) {
                return true;
            }

            // 缩进行 + 含 ( ) = 或关键字
            if (t.matches("^\\s{2,}.*") && (t.contains("(") || t.contains("=")
                    || t.matches(".*\\b(new|if|for|while|try|catch|throw|throws|extends|implements)\\b.*"))) {
                return true;
            }

            // XML / HTML 标签
            if (t.startsWith("<") && t.contains(">")) return true;    // <tag>...</tag>
            if (t.startsWith("</")) return true;                       // </tag>
            if (t.startsWith("<?xml")) return true;                    // <?xml ...?>

            // SQL 关键词开头（大写）
            if (t.matches("^(SELECT|INSERT|UPDATE|DELETE|CREATE|ALTER|DROP|FROM|WHERE|ORDER BY|GROUP BY|HAVING|JOIN|LEFT JOIN|RIGHT JOIN)\\b.*")) {
                return true;
            }

            // 注释行
            if (t.startsWith("//") || t.startsWith("/*") || t.startsWith("* ") || t.startsWith("<!--")) {
                return true;
            }

            // 字符串字面量（代码方法的 String 参数）
            if (t.startsWith("\"") && (t.endsWith("\"") || t.endsWith("\","))) return true;
            // 枚举常量 / 静态引用（HttpMethod.GET, 等）
            if (t.matches("^\\w+\\.\\w+,?$") && t.length() > 5) return true;
            // URL 路径 / 通配符（"/job/**", "/*.html" 等代码参数）
            if (t.matches("^[ \"']*[/#.*\\-!\\w]+[ \"']*[,)]?$") && t.length() <= 60 && t.contains("/")) return true;

            // 命令行 / shell 命令：包含 --flag（CLI 长选项）或带有 = 赋值和引号的模式
            if (t.matches(".*\\s--\\w+.*")) return true;                     // pt-online-schema-change --alter "..." D=... --execute
            if (t.contains("\"") && t.contains("=") && !t.contains("：")     // 含引号+等号+无中文冒号 → 命令/配置
                    && !t.matches(".*[\\u4e00-\\u9fff].*")) return true;

            return false;
        }

        /**
         * 判断一个孤立的代码行是否「足够强」，值得被单独包装成代码块。
         * <p>用于处理单行 SQL / 单行命令等场景（原本要求 ≥ 2 行才成块）。</p>
         */
        private boolean isStrongIsolatedCodeLine(String text) {
            String t = text.strip();

            // 语言标签行 + 代码内容混合（如 "SQL -- comment\nALTER TABLE ...;"）
            // 去除行首的语言标签后，检查剩余内容是否像代码
            String afterLabel = stripLeadingLangLabel(t);
            if (afterLabel != null && !afterLabel.isEmpty()) {
                // 剩下的内容以 SQL 关键字开头 → 强烈信号
                if (afterLabel.matches("^(SELECT|INSERT|UPDATE|DELETE|CREATE|ALTER|DROP)\\b.*")) return true;
                // 以注释开头 + 后面有 SQL 关键字
                if (afterLabel.startsWith("--") || afterLabel.startsWith("//")) return true;
            }

            // 行中含有 SQL DDL（ALTER TABLE、CREATE TABLE、ADD INDEX 等），且以 ; 结尾
            if (t.endsWith(";") && t.matches(".*\\b(ALTER TABLE|CREATE TABLE|DROP TABLE|ADD INDEX|ADD COLUMN|CREATE INDEX)\\b.*")) return true;

            // 行首是语言标签 + 行末是 ; 或代码特征
            if (afterLabel != null && (t.endsWith(";") || t.endsWith("{") || t.endsWith("}"))) return true;

            // 行中含有配置/赋值（=） + 引号 + 无中文冒号 → 命令/配置行
            if (t.contains("=") && t.contains("\"") && !t.contains("：")
                    && !t.matches(".*[\\u4e00-\\u9fff].*")) return true;

            return false;
        }

        /**
         * 如果文本以已知语言标签开头（大小写不敏感），去除标签并返回剩余内容；
         * 否则返回 null。
         */
        private String stripLeadingLangLabel(String text) {
            String t = text.strip();
            String lower = t.toLowerCase();
            for (String label : LANG_LABELS) {
                if (lower.startsWith(label)) {
                    int end = label.length();
                    // 标签后必须是空格、换行或注释符号
                    if (end < t.length()) {
                        char next = t.charAt(end);
                        if (next == ' ' || next == '\t' || next == '-' || next == '/' || next == '\n') {
                            return t.substring(end).stripLeading();
                        }
                    }
                }
            }
            return null;
        }

        /**
         * 扫描代码块的前几行，推断编程语言用于 Markdown fence。
         */
        private String guessCodeLanguage(boolean[] inCodeBlock, int fromIdx) {
            int end = fromIdx;
            while (end < inCodeBlock.length && inCodeBlock[end]) end++;

            int javaScore = 0, sqlScore = 0, xmlScore = 0;
            int checkLines = Math.min(end - fromIdx, 8);

            for (int i = fromIdx; i < fromIdx + checkLines; i++) {
                String t = lines.get(i).text.strip();
                if (t.matches(".*\\b(public|private|protected|class|void|import|package|super|this|return|static|final|new|try|catch|throw|throws|extends|implements|@Override|@Autowired|@Service|@Component|@Configuration|@Bean)\\b.*")) javaScore++;
                if (t.endsWith(";")) javaScore++;
                // SQL 关键词在行首
                if (t.matches("^\\s*(SELECT|INSERT|UPDATE|DELETE|CREATE|ALTER|DROP|FROM|WHERE|ORDER BY|GROUP BY|HAVING|JOIN|LEFT JOIN|RIGHT JOIN)\\b.*")) sqlScore += 2;
                // SQL DDL 关键词在行中（如 "SQL -- comment\nALTER TABLE ..."）
                if (t.matches(".*\\b(ALTER TABLE|CREATE TABLE|DROP TABLE|ADD INDEX|ADD COLUMN|CREATE INDEX|DROP INDEX)\\b.*")) sqlScore += 3;
                if (t.startsWith("<?xml")) xmlScore += 3;
                else if (t.startsWith("</")) xmlScore += 2;
                else if (t.startsWith("<") && t.endsWith(">") && t.length() > 5) xmlScore++;
            }

            if (xmlScore > javaScore && xmlScore > sqlScore && xmlScore >= 2) return "xml";
            if (sqlScore > javaScore && sqlScore > xmlScore && sqlScore >= 2) return "sql";
            if (javaScore > 0) return "java";
            return "text";
        }

        /**
         * 判断当前行是否是代码块前独立的语言标签（如 "JAVA", "XML", "SQL"）。
         * 满足条件时该行应被 fence 语言标签吸收，不输出为可见文本。
         */
        /** 常见编程语言标签关键词 */
        private static final Set<String> LANG_LABELS = Set.of(
                "java", "xml", "sql", "python", "yaml", "json", "text", "bash", "shell",
                "javascript", "typescript", "html", "css", "kotlin", "groovy", "scala",
                "rust", "go", "c", "cpp", "ruby", "php", "swift", "markdown", "properties"
        );

        /**
         * 判断一行文本是否为独立的语言标签词（用于代码块内过滤）。
         */
        private boolean isLangLabelWord(String text) {
            String t = text.strip().toLowerCase();
            if (t.length() < 2 || t.length() > 20) return false;
            if (!t.matches("^[a-z#]+$")) return false;
            return LANG_LABELS.contains(t.startsWith("#") ? t.substring(1) : t);
        }

        private boolean isStandaloneLangLabel(String text, int idx, boolean[] inCodeBlock) {
            // 找到下一个非空行
            int peek = idx + 1;
            while (peek < lines.size() && lines.get(peek).text.isEmpty()) peek++;
            // 下一个非空行不是代码 → 不是标签
            // 放宽条件：即使不在代码块内，若下一行看起来像代码行也接受
            if (peek >= lines.size()) return false;
            if (!inCodeBlock[peek] && !looksLikeCodeLine(lines.get(peek).text)) return false;

            // 当前行是否为独立的语言标签（大小写不敏感）
            String t = text.strip().toLowerCase();
            if (t.length() < 2 || t.length() > 20) return false;
            // 去掉可能的 # 前缀
            if (t.startsWith("#")) t = t.substring(1);
            return LANG_LABELS.contains(t);
        }

        /**
         * 填补代码段之间的间隙。
         * <ul>
         *   <li>间隙 ≤ 2 行且均为 trivial → 直接合并</li>
         *   <li>代码上下文暗示是续行（前一行以 . ( , 结尾，或后一行以 . ) 开头）
         *       → 允许合并 ≤ 20 行的间隙</li>
         *   <li>其他情况不合并</li>
         * </ul>
         */
        private void fillCodeSegmentGaps(boolean[] isCode) {
            for (int i = 0; i < isCode.length; i++) {
                if (!isCode[i] || lines.get(i).text.isEmpty()) continue;

                // 找到当前代码段的结尾
                int segmentEnd = i;
                while (segmentEnd + 1 < isCode.length && isCode[segmentEnd + 1]
                        && !lines.get(segmentEnd + 1).text.isEmpty()) {
                    segmentEnd++;
                }

                // 跳过真正的空行
                int gapStart = segmentEnd + 1;
                while (gapStart < isCode.length && lines.get(gapStart).text.isEmpty()) {
                    gapStart++;
                }
                if (gapStart >= isCode.length) break;

                // 找到下一个代码段的开始
                int nextSegmentStart = gapStart;
                while (nextSegmentStart < isCode.length && !isCode[nextSegmentStart]) {
                    nextSegmentStart++;
                }
                if (nextSegmentStart >= isCode.length) break;

                int gapLength = nextSegmentStart - gapStart;
                boolean isContinuation = isCodeContinuation(segmentEnd, nextSegmentStart);

                if (isContinuation && gapLength <= 20 && allTrivialGapLines(gapStart, nextSegmentStart)) {
                    // 代码续行：允许大幅度合并，标记间隙内所有行（包括空行）为代码
                    for (int j = segmentEnd + 1; j < nextSegmentStart; j++) {
                        isCode[j] = true;
                    }
                    i = nextSegmentStart - 1;
                } else if (!isContinuation && gapLength <= 2 && allTrivialGapLines(gapStart, nextSegmentStart)) {
                    // 短间隙合并，标记间隙内所有行（包括空行）为代码
                    for (int j = segmentEnd + 1; j < nextSegmentStart; j++) {
                        isCode[j] = true;
                    }
                    i = nextSegmentStart - 1;
                } else {
                    i = segmentEnd;
                }
            }
        }

        /**
         * 判断两段代码之间是否为代码续行关系。
         * 前段以 . ( , 结尾（方法链未完成），或后段以 . ) 开头（方法链续接）。
         */
        private boolean isCodeContinuation(int beforeIdx, int afterIdx) {
            String before = lines.get(beforeIdx).text.strip();
            String after = lines.get(afterIdx).text.strip();
            return before.endsWith(".") || before.endsWith("(") || before.endsWith(",")
                    || after.startsWith(".") || after.startsWith(")");
        }

        /**
         * 判断间隙行是否是「可合并」的 trivial 行：
         * 短文本、无中文、无自然语句标点 → 可能是代码排版产物（行号 / 分隔符等）
         * 长文本、含中文、有句号 → 真正的正文内容，不应合并
         */
        private boolean allTrivialGapLines(int from, int to) {
            for (int j = from; j < to; j++) {
                String t = lines.get(j).text.strip();
                if (t.isEmpty()) continue; // 空行放行
                // 页面边界标记 → 禁止合并到代码块
                if (t.startsWith("__PAGE_BREAK__")) return false;
                // 含中文字符 → 正文内容，禁止合并
                if (t.matches(".*[\\u4e00-\\u9fff].*")) return false;
                // 含自然语句标点 → 正文内容，禁止合并
                if (t.matches(".*[。！？；：、，…].*")) return false;
                // 超过 80 字符 → 正文内容，禁止合并
                if (t.length() > 80) return false;
                // 超过 8 个单词 → 正文语句，禁止合并
                if (t.split("\\s+").length > 8) return false;
            }
            return true;
        }

        /**
         * 计算正文基准字号：取 25 百分位字号。
         * 用中位数容易受标题大字号污染，导致正文基准偏高，标题检测不出来。
         */
        private float computeBaseFontSize() {
            List<Float> sizes = new ArrayList<>();
            for (PdfLine line : lines) {
                if (!line.text.isEmpty() && line.fontSize > 0) {
                    sizes.add(line.fontSize);
                }
            }
            if (sizes.isEmpty()) return 12f;

            Collections.sort(sizes);
            // 取 25 百分位（下四分位）：正文行占主体，标题大字号在尾部
            int p25Index = Math.max(0, sizes.size() / 4);
            return sizes.get(p25Index);
        }

        /**
         * 基于字号比值推断标题层级（1-6），0 表示非标题。
         * 中文 PDF 中常以「同字号加粗」作为标题 —— 也需要识别。
         */
        private int computeHeadingLevel(float fontSize, float baseFontSize, boolean bold) {
            if (baseFontSize <= 0) return 0;
            float ratio = fontSize / baseFontSize;

            // 字号明显更大 → 标题
            if (ratio >= 2.0) return 1;
            if (ratio >= 1.6) return 2;
            if (ratio >= 1.35) return 3;
            if (ratio >= 1.2) return bold ? 4 : 0;
            if (ratio >= 1.1 && bold) return 5;

            // 同字号但加粗 → 中文 PDF 常见的标题样式（如「前言」「一、」等）
            if (ratio >= 0.95 && bold && fontSize >= 10f) return 6;

            return 0;
        }

        private boolean isMonospacePdfFont(String fontName) {
            return fontName.contains("courier")
                    || fontName.contains("consolas")
                    || fontName.contains("monaco")
                    || fontName.contains("menlo")
                    || fontName.contains("monospace")
                    || fontName.contains("source code")
                    || fontName.contains("fira code")
                    || fontName.contains("jetbrains")
                    || fontName.contains("dejavu sans mono")
                    || fontName.contains("lucida console");
        }

        private static String cleanMarkdown(String text) {
            if (text == null) return "";
            return text.replaceAll("\\n{4,}", "\n\n\n").trim();
        }
    }

    private String parsePdf(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {

            int pageCount = document.getNumberOfPages();
            log.info("PDF加载成功：pages={}", pageCount);

            // Step 1: 提取图片并上传，同时记录每张图片的 Y 坐标位置
            Map<Integer, List<ImagePlacement>> pageImages = extractAndUploadImages(document);

            // Step 2: 提取文本（含页面边界标记 + Y 坐标），并按 Y 坐标插入图片
            CollectingPDFStripper stripper = new CollectingPDFStripper();
            stripper.setImagePlacements(pageImages);
            String text = stripper.getText(document);

            // 降级：自定义提取器为空时，用标准 PDFTextStripper 兜底
            if (text == null || text.isBlank()) {
                log.warn("自定义 PDFStripper 提取为空，降级到标准 PDFTextStripper");
                PDFTextStripper fallbackStripper = new PDFTextStripper();
                fallbackStripper.setSortByPosition(true);
                fallbackStripper.setSuppressDuplicateOverlappingText(true);
                text = fallbackStripper.getText(document);
                if (text != null) {
                    text = cleanText(text);
                }
            }

            return text != null ? text : "";
        }
    }

    /**
     * 从 PDF 文档的每一页提取图片，上传到文件服务器，同时记录图片在页面上的 Y 坐标。
     *
     * <p>跳过小于 50x50 像素的图片（通常是图标、背景图等装饰元素）。</p>
     *
     * @return 页面索引 → 图片位置列表（按 Y 坐标从上到下排序）
     */
    private Map<Integer, List<ImagePlacement>> extractAndUploadImages(PDDocument document) {
        // 先找到所有图片在每页上的 Y 坐标位置
        Map<Integer, Map<COSName, Float>> positionMap = findImagePositions(document);

        Map<Integer, List<ImagePlacement>> pageImages = new LinkedHashMap<>();

        for (int p = 0; p < document.getNumberOfPages(); p++) {
            PDPage page = document.getPage(p);
            PDResources resources;
            try {
                resources = page.getResources();
            } catch (Exception e) {
                log.debug("无法获取第 {} 页资源: {}", p + 1, e.getMessage());
                continue;
            }
            if (resources == null) continue;

            Map<COSName, Float> pagePositions = positionMap.getOrDefault(p, Collections.emptyMap());
            List<ImagePlacement> placements = new ArrayList<>();
            int imgIdx = 0;

            try {
                // 递归收集所有图片（包括嵌套在 Form XObject 中的）
                Map<COSName, PDImageXObject> allImages = new LinkedHashMap<>();
                collectImageXObjects(resources, allImages);

                for (Map.Entry<COSName, PDImageXObject> entry : allImages.entrySet()) {
                    COSName name = entry.getKey();
                    PDImageXObject image = entry.getValue();

                    // 跳过小图（图标、背景等）
                    if (image.getWidth() < 50 || image.getHeight() < 50) continue;

                    try {
                        BufferedImage bufferedImage = image.getImage();
                        if (bufferedImage == null) continue;

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "PNG", baos);
                        byte[] imageBytes = baos.toByteArray();

                        String fileName = "pdf_img_p" + (p + 1) + "_" + (imgIdx++) + ".png";
                        String url = fileUploadService.uploadBytes(imageBytes, fileName, "image/png");

                        // 获取图片 Y 坐标（从内容流解析得到），默认为页面顶部
                        float imageY = pagePositions.getOrDefault(name, 0f);
                        placements.add(new ImagePlacement(imageY, url));

                        log.debug("PDF图片已提取并上传: page={}, image={}, size={}x{}, y={}, url={}",
                                p + 1, imgIdx, image.getWidth(), image.getHeight(), imageY, url);
                    } catch (Exception e) {
                        log.warn("提取/上传第 {} 页第 {} 张图片失败: {}", p + 1, imgIdx, e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("遍历第 {} 页资源失败: {}", p + 1, e.getMessage());
            }

            if (!placements.isEmpty()) {
                // 按 Y 坐标排序（从上到下）
                placements.sort((a, b) -> Float.compare(a.y, b.y));
                pageImages.put(p, placements);
                log.info("第 {} 页提取 {} 张图片", p + 1, placements.size());
            }
        }

        if (!pageImages.isEmpty()) {
            log.info("PDF图片提取完成: 共 {} 页包含图片，总计 {} 张",
                    pageImages.size(),
                    pageImages.values().stream().mapToInt(List::size).sum());
        }
        return pageImages;
    }

    /**
     * 递归收集 PDResources 及其 Form XObject 子资源中的所有图片。
     * 许多 PDF 生成器（PowerPoint 导出、LaTeX 等）会把图片嵌套在 Form XObject 中，
     * 仅遍历页面顶层的 getXObjectNames() 会漏掉这些图片。
     */
    private void collectImageXObjects(PDResources resources, Map<COSName, PDImageXObject> images) {
        if (resources == null) return;
        try {
            for (COSName name : resources.getXObjectNames()) {
                PDXObject xobj;
                try {
                    xobj = resources.getXObject(name);
                } catch (Exception e) {
                    continue;
                }
                if (xobj instanceof PDImageXObject image) {
                    images.put(name, image);
                } else if (xobj instanceof PDFormXObject form) {
                    // 递归进入 Form XObject 的子资源
                    try {
                        collectImageXObjects(form.getResources(), images);
                    } catch (Exception ignored) {
                        // 某些 Form XObject 没有独立的 Resources 字典
                    }
                }
            }
        } catch (Exception e) {
            log.debug("递归收集图片失败: {}", e.getMessage());
        }
    }

    /**
     * 解析 PDF 每页的内容流，找到每个图片 XObject 被绘制时的 Y 坐标。
     *
     * <p>通过跟踪 cm（变换矩阵）和 Do（调用 XObject）操作符，
     * 计算图片在页面上的 Y 位置（从页面顶部向下，与 getYDirAdj 一致）。</p>
     *
     * @return 页面索引 → (图片资源名 → Y 坐标)
     */
    private Map<Integer, Map<COSName, Float>> findImagePositions(PDDocument document) {
        Map<Integer, Map<COSName, Float>> allPositions = new LinkedHashMap<>();

        for (int p = 0; p < document.getNumberOfPages(); p++) {
            PDPage page = document.getPage(p);
            float pageHeight = page.getMediaBox().getHeight();
            Map<COSName, Float> pagePositions = new LinkedHashMap<>();

            try {
                PDFStreamParser parser = new PDFStreamParser(page);
                parser.parse();
                List<Object> tokens = parser.getTokens();
                if (tokens.isEmpty()) continue;

                float tx = 0, ty = 0;
                Deque<float[]> saved = new ArrayDeque<>();

                for (int i = 0; i < tokens.size(); i++) {
                    Object token = tokens.get(i);
                    String opName = getOperatorName(token);

                    if (opName != null) {
                        switch (opName) {
                            case "q" -> saved.push(new float[]{tx, ty});
                            case "Q" -> {
                                if (!saved.isEmpty()) {
                                    float[] prev = saved.pop();
                                    tx = prev[0]; ty = prev[1];
                                }
                            }
                            case "Do" -> {
                                // 前一个 token 是 XObject 名称（COSName）
                                if (i > 0 && tokens.get(i - 1) instanceof COSName xobjName) {
                                    // 转换为页面顶部向下坐标
                                    float yDirAdj = pageHeight - ty;
                                    pagePositions.put(xobjName, yDirAdj);
                                }
                            }
                        }
                    } else if (token instanceof COSNumber) {
                        // 检查是否为 "a b c d e f cm" 变换矩阵
                        if (i + 6 < tokens.size()
                                && tokens.get(i + 1) instanceof COSNumber
                                && tokens.get(i + 2) instanceof COSNumber
                                && tokens.get(i + 3) instanceof COSNumber
                                && tokens.get(i + 4) instanceof COSNumber
                                && tokens.get(i + 5) instanceof COSNumber
                                && isOperatorName(tokens.get(i + 6), "cm")) {
                            float a = ((COSNumber) tokens.get(i + 0)).floatValue();
                            float b = ((COSNumber) tokens.get(i + 1)).floatValue();
                            float c = ((COSNumber) tokens.get(i + 2)).floatValue();
                            float d = ((COSNumber) tokens.get(i + 3)).floatValue();
                            float e = ((COSNumber) tokens.get(i + 4)).floatValue();
                            float f = ((COSNumber) tokens.get(i + 5)).floatValue();
                            // cm concatenates the matrix: CTM' = CTM × [a b c d e f]
                            float newTx = a * tx + c * ty + e;
                            float newTy = b * tx + d * ty + f;
                            tx = newTx;
                            ty = newTy;
                            i += 6; // 跳过已处理的矩阵值
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("解析第 {} 页内容流获取图片位置失败: {}", p + 1, e.getMessage());
            }

            if (!pagePositions.isEmpty()) {
                allPositions.put(p, pagePositions);
            }
        }

        return allPositions;
    }

    private static String getOperatorName(Object token) {
        if (token instanceof Operator op) return op.getName();
        return null;
    }

    private static boolean isOperatorName(Object token, String expected) {
        if (token instanceof Operator op) return expected.equals(op.getName());
        return false;
    }

    private String parseDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {

            StringBuilder sb = new StringBuilder();

            // 使用 getBodyElements() 保持段落和表格的原始顺序
            for (IBodyElement element : document.getBodyElements()) {
                if (element instanceof XWPFTable table) {
                    sb.append(formatTable(table));
                } else if (element instanceof XWPFParagraph paragraph) {
                    String style = paragraph.getStyle();
                    String styleLower = (style != null) ? style.toLowerCase() : "";

                    // 空段落
                    if (paragraph.getText() == null || paragraph.getText().isBlank()) {
                        sb.append('\n');
                        continue;
                    }

                    // 标题检测：outlineLvl > 样式名 > "heading"/"标题"
                    int headingLevel = detectHeadingLevel(paragraph, styleLower);
                    if (headingLevel > 0) {
                        sb.append("#".repeat(Math.min(headingLevel, 6)))
                                .append(' ')
                                .append(formatInlineRuns(paragraph))
                                .append('\n');
                        continue;
                    }

                    // 列表项
                    String listPrefix = getListPrefix(paragraph, styleLower);
                    if (!listPrefix.isEmpty()) {
                        sb.append(listPrefix).append(' ')
                                .append(formatInlineRuns(paragraph))
                                .append('\n');
                        continue;
                    }

                    // 代码块检测
                    if (isCodeBlock(paragraph)) {
                        sb.append("```\n")
                                .append(extractCodeText(paragraph))
                                .append("\n```\n\n");
                        continue;
                    }

                    // 普通段落
                    sb.append(formatInlineRuns(paragraph)).append("\n\n");
                }
            }

            return cleanText(sb.toString());
        }
    }

    // ── DOCX 辅助方法 ────────────────────────────────────

    /**
     * 解析段落内的 inline 格式：粗体 / 斜体 / 行内代码 / 超链接 / 换行
     *
     * <p>换行处理：对含有 w:br 的 run，用正则解析其 XML 文本，
     * 按文档顺序提取 w:t 和 w:br，避免 run.text() 拼接丢失换行位置。</p>
     */
    private String formatInlineRuns(XWPFParagraph paragraph) {
        StringBuilder sb = new StringBuilder();
        List<XWPFRun> runs = paragraph.getRuns();

        for (XWPFRun run : runs) {
            // 超链接 run
            if (run instanceof XWPFHyperlinkRun) {
                String text = run.text();
                if (text != null && !text.isEmpty()) {
                    sb.append('[').append(text).append(']').append("()");
                }
                continue;
            }

            String text = run.text();
            if (text == null || text.isEmpty()) continue;

            // 检测此 run 是否包含 w:br
            if (runHasBreak(run)) {
                appendRunWithBreaks(sb, run);
            } else {
                sb.append(applyInlineFormat(run, text));
            }
        }

        return sb.toString();
    }

    // 匹配 w:t 中的文本 或 w:br / w:cr 空元素
    private static final Pattern RUN_BREAK_PATTERN =
            Pattern.compile("<w:t[^>]*>([^<]*)</w:t>|<w:br\\s*/>|<w:cr\\s*/>");

    /**
     * 判断 XWPFRun 是否包含换行元素（w:br）
     */
    private boolean runHasBreak(XWPFRun run) {
        try {
            return !run.getCTR().getBrList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用正则解析 run 的 XML，按 w:t / w:br 原始顺序输出
     */
    private void appendRunWithBreaks(StringBuilder sb, XWPFRun run) {
        try {
            String xml = run.getCTR().xmlText();
            Matcher m = RUN_BREAK_PATTERN.matcher(xml);
            while (m.find()) {
                String seg = m.group(1);
                if (seg != null) {
                    // w:t — 文本段
                    if (!seg.isEmpty()) {
                        sb.append(applyInlineFormat(run, seg));
                    }
                } else {
                    // w:br / w:cr — 换行
                    sb.append('\n');
                }
            }
        } catch (Exception e) {
            // 退化：直接用 run.text() 拼接
            String text = run.text();
            if (text != null && !text.isEmpty()) {
                sb.append(applyInlineFormat(run, text));
            }
        }
    }

    /**
     * 对单段纯文本应用 inline 格式标记
     */
    private String applyInlineFormat(XWPFRun run, String text) {
        if (run == null) return text;

        boolean isBold = run.isBold();
        boolean isItalic = run.isItalic();
        boolean isMono = isMonospaceFont(run);

        if (isMono) return "`" + text + "`";
        if (isBold && isItalic) return "***" + text + "***";
        if (isBold) return "**" + text + "**";
        if (isItalic) return "*" + text + "*";
        return text;
    }

    /**
     * 表格 → Markdown 表格（第一行为表头 + 分隔行）
     */
    private String formatTable(XWPFTable table) {
        StringBuilder sb = new StringBuilder("\n");
        int numRows = table.getRows().size();
        if (numRows == 0) return "";

        // 计算列数（取所有行的最大列数）
        int maxCols = 0;
        for (XWPFTableRow row : table.getRows()) {
            if (row.getTableCells().size() > maxCols) {
                maxCols = row.getTableCells().size();
            }
        }
        if (maxCols == 0) return "";

        for (int r = 0; r < numRows; r++) {
            XWPFTableRow row = table.getRow(r);
            StringBuilder rowSb = new StringBuilder("|");
            for (int c = 0; c < maxCols; c++) {
                String cellText = "";
                if (c < row.getTableCells().size()) {
                    cellText = row.getCell(c).getText().trim()
                            .replace("\n", " ");
                }
                rowSb.append(' ').append(cellText).append(" |");
            }
            sb.append(rowSb).append('\n');

            // 表头后添加分隔行
            if (r == 0) {
                StringBuilder sep = new StringBuilder("|");
                for (int c = 0; c < maxCols; c++) {
                    sep.append(" --- |");
                }
                sb.append(sep).append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * 判断段落是否是列表项，返回 Markdown 列表前缀（空字符串表示不是列表）
     */
    private String getListPrefix(XWPFParagraph paragraph, String styleLower) {
        // 通过样式判断
        if (styleLower.contains("list") || styleLower.contains("bullet")) {
            int indent = getListIndent(paragraph);
            return "    ".repeat(indent) + "-";
        }
        if (styleLower.contains("number")) {
            int indent = getListIndent(paragraph);
            return "    ".repeat(indent) + "1.";
        }

        // 通过 XML 是否有 numPr 判断（无法区分编号/项目符号，默认用 -）
        if (paragraph.getCTP() != null && paragraph.getCTP().getPPr() != null) {
            var numPr = paragraph.getCTP().getPPr().getNumPr();
            if (numPr != null && numPr.getNumId() != null) {
                int indent = getListIndent(paragraph);
                return "    ".repeat(indent) + "-";
            }
        }

        return "";
    }

    /**
     * 获取列表缩进层级（0 起始）
     */
    private int getListIndent(XWPFParagraph paragraph) {
        if (paragraph.getCTP() != null
                && paragraph.getCTP().getPPr() != null
                && paragraph.getCTP().getPPr().getNumPr() != null
                && paragraph.getCTP().getPPr().getNumPr().getIlvl() != null) {
            return paragraph.getCTP().getPPr().getNumPr().getIlvl().getVal().intValue();
        }
        return 0;
    }

    /**
     * 判断段落是否是代码块：
     * 1. 样式名包含 code / html / pre / src / source / 源代码
     * 2. 段落有底色（w:shd）且所有 run 是等宽字体
     * 3. 所有 run 均为等宽字体
     */
    private boolean isCodeBlock(XWPFParagraph paragraph) {
        // 通过样式名判断
        String style = paragraph.getStyle();
        if (style != null) {
            String s = style.toLowerCase();
            if (s.contains("code") || s.contains("html")
                    || s.contains("pre") || s.contains("src")
                    || s.contains("source") || s.contains("源代码")
                    || s.contains("program")) {
                return true;
            }
        }

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) return false;

        // 通过段落底纹判断（Word 代码块通常有灰色/深色底纹）
        boolean hasShading = false;
        try {
            if (paragraph.getCTP() != null
                    && paragraph.getCTP().getPPr() != null
                    && paragraph.getCTP().getPPr().getShd() != null) {
                hasShading = true;
            }
        } catch (Exception ignored) {
        }

        // 等宽字体检测
        boolean allMono = true;
        boolean anyMono = false;
        for (XWPFRun run : runs) {
            if (run.text() != null && !run.text().isBlank()) {
                if (isMonospaceFont(run)) {
                    anyMono = true;
                } else {
                    allMono = false;
                }
            }
        }

        // 有底纹 + 至少部分是等宽字体 → 代码块
        if (hasShading && anyMono) return true;

        // 全部等宽字体 → 代码块
        return allMono && anyMono;
    }

    /**
     * 判断 run 是否使用等宽字体（Courier / Consolas / monospace / Menlo / Monaco）
     */
    private boolean isMonospaceFont(XWPFRun run) {
        String font = run.getFontFamily();
        if (font == null) return false;
        String f = font.toLowerCase();
        return f.contains("courier")
                || f.contains("consolas")
                || f.contains("monaco")
                || f.contains("menlo")
                || f.contains("monospace")
                || f.contains("source code")
                || f.contains("fira code")
                || f.contains("jetbrains");
    }

    /**
     * 提取代码块文本，保留行内换行（w:br）。对每个 run 的 XML 用正则解析。
     */
    private String extractCodeText(XWPFParagraph paragraph) {
        StringBuilder sb = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.text();
            if (text == null || text.isEmpty()) continue;

            if (runHasBreak(run)) {
                try {
                    String xml = run.getCTR().xmlText();
                    Matcher m = RUN_BREAK_PATTERN.matcher(xml);
                    while (m.find()) {
                        String seg = m.group(1);
                        if (seg != null) {
                            if (!seg.isEmpty()) sb.append(seg);
                        } else {
                            sb.append('\n');
                        }
                    }
                } catch (Exception e) {
                    sb.append(text);
                }
            } else {
                sb.append(text);
            }
        }
        return sb.toString().trim();
    }

    private String parseXlsx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                sb.append("## ").append(sheetName).append("\n\n");

                // 计算该 Sheet 最大列数
                int maxCols = 0;
                for (var row : sheet) {
                    if (row.getLastCellNum() > maxCols) {
                        maxCols = row.getLastCellNum();
                    }
                }
                if (maxCols == 0) {
                    sb.append('\n');
                    continue;
                }

                boolean firstRow = true;
                for (var row : sheet) {
                    StringBuilder rowSb = new StringBuilder("|");
                    for (int c = 0; c < maxCols; c++) {
                        var cell = row.getCell(c);
                        String cellValue = getCellStringValue(cell);
                        rowSb.append(' ').append(cellValue).append(" |");
                    }
                    sb.append(rowSb).append('\n');

                    // 表头后添加分隔行
                    if (firstRow) {
                        StringBuilder sep = new StringBuilder("|");
                        for (int c = 0; c < maxCols; c++) {
                            sep.append(" --- |");
                        }
                        sb.append(sep).append('\n');
                        firstRow = false;
                    }
                }
                sb.append('\n');
            }

            return sb.toString();
        }
    }

    private String parsePptx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XMLSlideShow ppt = new XMLSlideShow(is)) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < ppt.getSlides().size(); i++) {
                var slide = ppt.getSlides().get(i);
                sb.append("## 幻灯片 ").append(i + 1).append('\n');

                // 递归处理所有形状（包括组合中的形状）
                processPptxShapes(slide.getShapes(), sb);
            }

            return cleanText(sb.toString());
        }
    }

    /**
     * 递归处理 PPTX 形状列表
     */
    private void processPptxShapes(List<XSLFShape> shapes, StringBuilder sb) {
        for (XSLFShape shape : shapes) {
            if (shape instanceof XSLFGroupShape group) {
                // 组合形状：递归处理
                processPptxShapes(group.getShapes(), sb);
            } else if (shape instanceof XSLFTable table) {
                // 幻灯片内嵌表格
                sb.append(formatPptxTable(table));
            } else if (shape instanceof XSLFTextShape textShape) {
                String text = textShape.getText();
                if (text == null || text.isBlank()) continue;

                // 判断是否为标题（Placeholder.TITLE 或文本类型为标题）
                boolean isTitle = false;
                try {
                    var placeholder = textShape.getTextType();
                    if (placeholder != null) {
                        String phName = placeholder.name();
                        isTitle = phName.contains("TITLE") || phName.contains("CENTER");
                    }
                } catch (Exception ignored) {
                    // 某些形状没有 Placeholder
                }

                // 格式化文本（逐 run 处理粗体/斜体/等宽）
                String formatted = formatPptxParagraphs(textShape);
                if (isTitle) {
                    sb.append("### ").append(formatted).append('\n');
                } else {
                    sb.append(formatted).append("\n\n");
                }
            }
        }
    }

    /**
     * 格式化 PPTX 文本框内的段落和 run（粗体/斜体/行内代码）
     */
    private String formatPptxParagraphs(XSLFTextShape textShape) {
        StringBuilder sb = new StringBuilder();

        for (XSLFTextParagraph para : textShape.getTextParagraphs()) {
            boolean hasRuns = !para.getTextRuns().isEmpty();

            if (hasRuns) {
                for (XSLFTextRun run : para.getTextRuns()) {
                    String runText = run.getRawText();
                    if (runText == null || runText.isEmpty()) continue;

                    boolean isBold = run.isBold();
                    boolean isItalic = run.isItalic();
                    boolean isMono = isMonospaceFontPptx(run);

                    String formatted = runText;
                    if (isMono) {
                        formatted = "`" + runText + "`";
                    } else if (isBold && isItalic) {
                        formatted = "***" + runText + "***";
                    } else if (isBold) {
                        formatted = "**" + runText + "**";
                    } else if (isItalic) {
                        formatted = "*" + runText + "*";
                    }
                    sb.append(formatted);
                }
                sb.append('\n');
            } else {
                // 无 run 退化到纯文本
                String plain = para.getText();
                if (plain != null && !plain.isBlank()) {
                    sb.append(plain.trim()).append('\n');
                }
            }
        }

        return sb.toString().trim();
    }

    /**
     * PPTX 幻灯片内嵌表格 → Markdown 表格
     */
    private String formatPptxTable(XSLFTable table) {
        StringBuilder sb = new StringBuilder("\n");
        int numRows = table.getRows().size();
        if (numRows == 0) return "";

        // 计算列数
        int maxCols = 0;
        for (XSLFTableRow row : table.getRows()) {
            if (row.getCells().size() > maxCols) {
                maxCols = row.getCells().size();
            }
        }
        if (maxCols == 0) return "";

        for (int r = 0; r < numRows; r++) {
            XSLFTableRow row = table.getRows().get(r);
            StringBuilder rowSb = new StringBuilder("|");
            for (int c = 0; c < maxCols; c++) {
                String cellText = "";
                if (c < row.getCells().size()) {
                    cellText = row.getCells().get(c).getText().trim()
                            .replace("\n", " ");
                }
                rowSb.append(' ').append(cellText).append(" |");
            }
            sb.append(rowSb).append('\n');

            if (r == 0) {
                StringBuilder sep = new StringBuilder("|");
                for (int c = 0; c < maxCols; c++) {
                    sep.append(" --- |");
                }
                sb.append(sep).append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * PPTX run 等宽字体检测
     */
    private boolean isMonospaceFontPptx(XSLFTextRun run) {
        String font = run.getFontFamily();
        if (font == null) return false;
        String f = font.toLowerCase();
        return f.contains("courier")
                || f.contains("consolas")
                || f.contains("monaco")
                || f.contains("menlo")
                || f.contains("monospace")
                || f.contains("source code")
                || f.contains("fira code")
                || f.contains("jetbrains");
    }

    private String parsePlainText(MultipartFile file) throws Exception {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    // ── 工具方法 ─────────────────────────────────────────

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * 检测段落标题层级。优先级：
     * 1. 段落 outlineLvl 属性（最可靠，Word 标题样式必设此值）
     * 2. 样式名含 "heading" → 提取数字
     * 3. 样式名含 "标题"  → 提取数字（中文 Word 样式）
     * 4. 编号模式 + 加粗/大字 → 自动推断层级
     *    "5. xxx" (depth=1) → ##
     *    "5.1 xxx" (depth=2) → ###
     * 返回 1-6 表示标题层级，0 表示非标题
     */
    private int detectHeadingLevel(XWPFParagraph paragraph, String styleLower) {
        // 1. 段落属性 outlineLvl（0 基准 → 1 基准）
        try {
            if (paragraph.getCTP() != null
                    && paragraph.getCTP().getPPr() != null
                    && paragraph.getCTP().getPPr().getOutlineLvl() != null) {
                int level = paragraph.getCTP().getPPr().getOutlineLvl().getVal().intValue() + 1;
                return Math.min(level, 6);
            }
        } catch (Exception ignored) {
        }

        // 2. 样式名包含 "heading" → 提取数字
        if (styleLower.contains("heading")) {
            try {
                return Integer.parseInt(styleLower.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 1;
            }
        }

        // 3. 样式名包含 "标题" → 提取数字（中文 Word 样式）
        if (styleLower.contains("标题")) {
            try {
                return Integer.parseInt(styleLower.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 1;
            }
        }

        // 4. 编号模式推断 — 仅当文字较短时生效（标题特征：通常 < 200 字）
        String plainText = paragraph.getText().trim();
        int patternLevel = detectNumberedHeadingLevel(plainText);
        if (patternLevel > 0 && plainText.length() < 200) {
            return patternLevel;
        }

        return 0;
    }

    /**
     * 从文本的编号模式推断标题层级。
     * "5. xxx" / "一、" → depth 1 → 返回 2
     * "5.1 xxx" / "（一）" → depth 2 → 返回 3
     * "5.1.1 xxx" → depth 3 → 返回 4
     * 不匹配返回 0
     */
    private int detectNumberedHeadingLevel(String text) {
        if (text == null || text.isEmpty()) return 0;

        // 数字编号：5. / 5.1 / 5.1.1 / 5.1.1.
        if (text.matches("^\\d+\\.[\\s\\u00A0].*")) return 2;
        if (text.matches("^\\d+\\.\\d+[.\\s].*")) return 3;
        if (text.matches("^\\d+\\.\\d+\\.\\d+[.\\s].*")) return 4;

        // 中文编号：一、 / 二、 → 一级； （一） / （二） → 二级
        if (text.matches("^[一二三四五六七八九十]+[、\\s].*")) return 2;
        if (text.matches("^[（(][一二三四五六七八九十]+[）)].*")) return 3;

        return 0;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                yield val == Math.floor(val) && !Double.isInfinite(val)
                        ? String.valueOf((long) val)
                        : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    private String cleanText(String text) {
        if (text == null) return "";
        return text.replaceAll("\\n{4,}", "\n\n\n").trim();
    }
}
