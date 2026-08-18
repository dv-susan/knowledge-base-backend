package com.knowledge.base.document.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Apache Tika / POI 文档解析器 —— XLSX → Markdown 表格
 *
 * <p>PDF / DOCX / PPTX 由 Java PDFBox / POI 直接解析，效果更好。
 * XLSX 走此处生成 Markdown 表格格式。</p>
 *
 * @author 苏三
 * @since 1.1.0
 */
@Slf4j
@Service
public class TikaConverterService {

    /**
     * 解析 XLSX → Markdown 表格
     */
    public String parseXlsx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                sb.append("## ").append(sheetName).append("\n\n");

                // 计算列数（取最大列宽）
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

                // 构建表头和数据行
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

            return cleanMarkdown(sb.toString());
        }
    }

    // ── 工具方法 ─────────────────────────────────────────

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

    private String cleanMarkdown(String text) {
        if (text == null) return "";
        return text.replaceAll("\\n{4,}", "\n\n\n").trim();
    }
}
