package com.knowledge.base.document.service.impl;

import cn.hutool.crypto.digest.DigestUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.entity.FileMetadata;
import com.knowledge.base.document.mapper.FileMetadataMapper;
import com.knowledge.base.document.service.FileManagementService;
import com.knowledge.base.document.service.FileUploadService;
import com.knowledge.base.document.utils.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.knowledge.base.common.config.SystemConfigCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Base64;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/**
 * 文件管理服务实现类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class FileManagementServiceImpl extends ServiceImpl<FileMetadataMapper, FileMetadata> implements FileManagementService {

    @Resource
    private FileMetadataMapper fileMetadataMapper;

    @Resource
    private FileUploadService fileUploadService;

    @Resource
    private SystemConfigCache systemConfigCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileMetadata uploadFile(MultipartFile file, Long userId, Boolean isPublic) {
        log.info("上传文件：fileName={}, userId={}, isPublic={}", file.getOriginalFilename(), userId, isPublic);

        try {
            // 检查文件大小
            if (file.isEmpty()) {
                throw new BusinessException("文件不能为空");
            }

            // 获取原始文件名
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
                throw new BusinessException("文件名不能为空");
            }

            // 检查文件大小（从系统配置读取）
            long maxSize = getMaxFileSizeFromConfig();
            if (file.getSize() > maxSize) {
                throw new BusinessException("文件大小超过限制：最大" + (maxSize / 1048576) + "MB");
            }

            // 获取文件扩展名
            String fileExtension = getFileExtension(originalFileName);

            // 检查文件类型（从系统配置读取）
            List<String> allowedTypes = getAllowedFileTypesFromConfig();
            if (!allowedTypes.contains(fileExtension.toLowerCase())) {
                throw new BusinessException("不支持的文件类型：" + fileExtension + "，支持的类型：" + String.join(", ", allowedTypes));
            }

            // 上传文件到文件服务器
            String fileUrl = fileUploadService.uploadFile(file);
            log.info("文件上传成功：fileUrl={}", fileUrl);

            // 创建文件元数据
            FileMetadata metadata = new FileMetadata();
            metadata.setId(SnowflakeIdGenerator.getInstance().nextId());
            metadata.setFileName(getFileName(originalFileName));
            metadata.setOriginalFileName(originalFileName);
            metadata.setFileExtension(fileExtension);
            metadata.setFileSize(file.getSize());
            metadata.setContentType(file.getContentType());
            metadata.setStoragePath(fileUrl);
            metadata.setAccessUrl(fileUrl);
            metadata.setFileCategory(determineFileCategory(fileExtension));
            metadata.setUploaderId(userId);
            metadata.setUploaderName(UserContext.getCurrentUserName());
            metadata.setIsPublic(isPublic != null ? isPublic : false);
            metadata.setDownloadCount(0);
            metadata.setUploadStatus("completed");

            // 计算文件哈希
            try {
                byte[] fileBytes = file.getBytes();
                metadata.setFileMd5(DigestUtil.md5Hex(fileBytes));
                metadata.setFileSha256(DigestUtil.sha256Hex(fileBytes));
            } catch (Exception e) {
                log.warn("计算文件哈希失败", e);
            }

            // 如果是图片，获取图片尺寸
            if (isImage(fileExtension)) {
                try {
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    if (image != null) {
                        metadata.setWidth(image.getWidth());
                        metadata.setHeight(image.getHeight());
                    }
                } catch (Exception e) {
                    log.warn("获取图片尺寸失败", e);
                }
            }

            // 保存到数据库
            fileMetadataMapper.insert(metadata);
            log.info("文件元数据保存成功：fileId={}", metadata.getId());

            return metadata;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new BusinessException("上传文件失败：" + e.getMessage());
        }
    }

    @Override
    public List<FileMetadata> getFileList(Long userId) {
        log.info("获取文件列表：userId={}", userId);
        return fileMetadataMapper.findByUploaderId(userId);
    }

    @Override
    public List<FileMetadata> getFileListByCategory(Long userId, String fileCategory) {
        log.info("按分类获取文件列表：userId={}, fileCategory={}", userId, fileCategory);
        return fileMetadataMapper.findByUploaderIdAndCategory(userId, fileCategory);
    }

    @Override
    public FileMetadata getFileDetail(Long fileId) {
        log.info("获取文件详情：fileId={}", fileId);
        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata == null) {
            throw new BusinessException("文件不存在");
        }

        // 更新最后访问时间
        updateLastAccessTime(fileId);
        return metadata;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean renameFile(Long fileId, String newFileName, Long userId) {
        log.info("重命名文件：fileId={}, newFileName={}, userId={}", fileId, newFileName, userId);

        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata == null) {
            throw new BusinessException("文件不存在");
        }

        // 检查权限
        if (!metadata.getUploaderId().equals(userId)) {
            throw new BusinessException("无权限操作该文件");
        }

        // 更新文件名
        metadata.setFileName(newFileName);
        int result = fileMetadataMapper.updateById(metadata);

        log.info("重命名文件完成：result={}", result);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFile(Long fileId, Long userId) {
        log.info("删除文件：fileId={}, userId={}", fileId, userId);

        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata == null) {
            throw new BusinessException("文件不存在");
        }

        // 检查权限
        if (!metadata.getUploaderId().equals(userId)) {
            throw new BusinessException("无权限操作该文件");
        }

        // 逻辑删除
        int result = fileMetadataMapper.deleteById(fileId);
        log.info("删除文件完成：result={}", result);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchDeleteFiles(List<Long> fileIds, Long userId) {
        log.info("批量删除文件：fileIds={}, userId={}", fileIds, userId);

        int count = 0;
        for (Long fileId : fileIds) {
            try {
                if (deleteFile(fileId, userId)) {
                    count++;
                }
            } catch (Exception e) {
                log.error("删除文件失败：fileId={}", fileId, e);
            }
        }

        log.info("批量删除文件完成：count={}", count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFilePermission(Long fileId, Boolean isPublic, Long userId) {
        log.info("更新文件权限：fileId={}, isPublic={}, userId={}", fileId, isPublic, userId);

        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata == null) {
            throw new BusinessException("文件不存在");
        }

        // 检查权限
        if (!metadata.getUploaderId().equals(userId)) {
            throw new BusinessException("无权限操作该文件");
        }

        metadata.setIsPublic(isPublic);
        int result = fileMetadataMapper.updateById(metadata);

        log.info("更新文件权限完成：result={}", result);
        return result > 0;
    }

    @Override
    public void incrementDownloadCount(Long fileId) {
        log.info("增加下载次数：fileId={}", fileId);
        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata != null) {
            Integer count = metadata.getDownloadCount();
            metadata.setDownloadCount(count == null ? 1 : count + 1);
            fileMetadataMapper.updateById(metadata);
        }
    }

    @Override
    public void updateLastAccessTime(Long fileId) {
        log.info("更新最后访问时间：fileId={}", fileId);
        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata != null) {
            metadata.setLastAccessTime(LocalDateTime.now());
            fileMetadataMapper.updateById(metadata);
        }
    }

    @Override
    public Map<String, Object> getFileStatistics(Long userId) {
        log.info("获取文件统计信息：userId={}", userId);

        Map<String, Object> statistics = new HashMap<>();

        // 总文件数
        Integer totalCount = fileMetadataMapper.countByUploaderId(userId);
        statistics.put("totalCount", totalCount);

        // 总文件大小
        Long totalSize = fileMetadataMapper.sumFileSizeByUploaderId(userId);
        statistics.put("totalSize", totalSize);
        statistics.put("totalSizeReadable", formatFileSize(totalSize));

        // 按分类统计
        Map<String, Integer> categoryCount = new HashMap<>();
        List<FileMetadata> allFiles = fileMetadataMapper.findByUploaderId(userId);
        for (FileMetadata file : allFiles) {
            String category = file.getFileCategory();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }
        statistics.put("categoryCount", categoryCount);

        // 今日上传
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        long todayCount = allFiles.stream()
                .filter(f -> f.getCreatedAt() != null && f.getCreatedAt().isAfter(today))
                .count();
        statistics.put("todayCount", todayCount);

        log.info("文件统计信息：statistics={}", statistics);
        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileMetadata copyFile(Long fileId, Long userId) {
        log.info("复制文件：fileId={}, userId={}", fileId, userId);

        FileMetadata original = fileMetadataMapper.selectById(fileId);
        if (original == null) {
            throw new BusinessException("文件不存在");
        }

        // 创建副本
        FileMetadata copy = new FileMetadata();
        copy.setId(SnowflakeIdGenerator.getInstance().nextId());
        copy.setFileName("副本_" + original.getFileName());
        copy.setOriginalFileName(original.getOriginalFileName());
        copy.setFileExtension(original.getFileExtension());
        copy.setFileSize(original.getFileSize());
        copy.setContentType(original.getContentType());
        copy.setStoragePath(original.getStoragePath());
        copy.setAccessUrl(original.getAccessUrl());
        copy.setFileCategory(original.getFileCategory());
        copy.setUploaderId(userId);
        copy.setUploaderName(UserContext.getCurrentUserName());
        copy.setIsPublic(original.getIsPublic());
        copy.setDownloadCount(0);
        copy.setUploadStatus("completed");
        copy.setWidth(original.getWidth());
        copy.setHeight(original.getHeight());
        copy.setThumbnailUrl(original.getThumbnailUrl());
        copy.setFileMd5(original.getFileMd5());
        copy.setFileSha256(original.getFileSha256());

        fileMetadataMapper.insert(copy);
        log.info("文件复制完成：newFileId={}", copy.getId());

        return copy;
    }

    @Override
    public List<FileMetadata> searchFiles(Long userId, String keyword) {
        log.info("搜索文件：userId={}, keyword={}", userId, keyword);

        LambdaQueryWrapper<FileMetadata> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileMetadata::getUploaderId, userId)
                .and(wrapper -> wrapper
                        .like(FileMetadata::getFileName, keyword)
                        .or()
                        .like(FileMetadata::getOriginalFileName, keyword))
                .orderByDesc(FileMetadata::getCreatedAt);

        return fileMetadataMapper.selectList(queryWrapper);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }

    /**
     * 获取文件名（保留原始文件名）
     */
    private String getFileName(String originalFileName) {
        return originalFileName != null ? originalFileName : "未命名文件";
    }

    /**
     * 判断文件分类
     */
    private String determineFileCategory(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "other";
        }

        String ext = extension.toLowerCase();

        // 图片
        if (ext.matches("jpg|jpeg|png|gif|bmp|webp|svg")) {
            return "image";
        }

        // 文档
        if (ext.matches("doc|docx|pdf|txt|xls|xlsx|ppt|pptx|md|markdown")) {
            return "document";
        }

        // 视频
        if (ext.matches("mp4|avi|mkv|mov|wmv|flv|webm")) {
            return "video";
        }

        // 音频
        if (ext.matches("mp3|wav|flac|aac|ogg|wma")) {
            return "audio";
        }

        // 压缩文件
        if (ext.matches("zip|rar|7z|tar|gz")) {
            return "archive";
        }

        return "other";
    }

    /**
     * 判断是否为图片
     */
    private boolean isImage(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        String ext = extension.toLowerCase();
        return ext.matches("jpg|jpeg|png|gif|bmp|webp|svg");
    }

    @Override
    public void streamFile(Long fileId, HttpServletRequest request, HttpServletResponse response) {
        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata == null) {
            throw new BusinessException("文件不存在");
        }

        String accessUrl = metadata.getAccessUrl();
        if (accessUrl == null || accessUrl.isEmpty()) {
            throw new BusinessException("文件存储路径不存在");
        }

        log.info("流式传输文件：fileId={}, accessUrl={}, contentType={}", fileId, accessUrl, metadata.getContentType());

        try {
            long fileSize = metadata.getFileSize() != null ? metadata.getFileSize() : 0;
            String contentType = metadata.getContentType() != null ? metadata.getContentType() : "application/octet-stream";

            // 设置通用响应头
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", buildContentDisposition(metadata.getOriginalFileName()));
            response.setHeader("Accept-Ranges", "bytes");

            // 解析 Range 请求头
            String rangeHeader = request.getHeader("Range");
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                // 处理 Range 请求 → 返回 206 Partial Content
                handleRangeRequest(rangeHeader, fileSize, accessUrl, response, fileId);
            } else {
                // 非 Range 请求 → 返回完整文件 200 OK
                handleFullRequest(fileSize, accessUrl, response, fileId);
            }

            log.info("文件流式传输完成：fileId={}, size={}", fileId, fileSize);
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("流式传输文件失败：fileId={}", fileId, e);
            throw new BusinessException("文件传输失败: " + e.getMessage());
        }
    }

    /**
     * 处理 HTTP Range 请求，返回 206 Partial Content
     * 浏览器 audio/video 元素需要 Range 支持才能正常播放
     */
    private void handleRangeRequest(String rangeHeader, long fileSize, String accessUrl,
                                     HttpServletResponse response, Long fileId) throws IOException {
        // 解析 Range: bytes=start-end
        long start = 0;
        long end = fileSize - 1;

        String rangeValue = rangeHeader.substring("bytes=".length()).trim();
        int dashIndex = rangeValue.indexOf('-');
        if (dashIndex > 0) {
            start = Long.parseLong(rangeValue.substring(0, dashIndex));
        }
        if (dashIndex < rangeValue.length() - 1) {
            end = Long.parseLong(rangeValue.substring(dashIndex + 1));
        }

        // 边界检查
        if (start >= fileSize) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader("Content-Range", "bytes */" + fileSize);
            return;
        }
        if (end >= fileSize) {
            end = fileSize - 1;
        }

        long contentLength = end - start + 1;
        log.debug("Range请求：bytes={}-{}, contentLength={}, fileSize={}", start, end, contentLength, fileSize);

        // 设置 206 响应
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
        response.setContentLengthLong(contentLength);

        // 连接到源文件并请求指定范围
        URLConnection conn = new URL(accessUrl).openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("Range", "bytes=" + start + "-" + end);

        try (InputStream inputStream = conn.getInputStream();
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long bytesWritten = 0;

            while (bytesWritten < contentLength && (bytesRead = inputStream.read(buffer, 0,
                    (int) Math.min(buffer.length, contentLength - bytesWritten))) != -1) {
                os.write(buffer, 0, bytesRead);
                bytesWritten += bytesRead;
            }
            os.flush();
        }

        updateLastAccessTime(fileId);
    }

    /**
     * 处理完整文件请求，返回 200 OK
     */
    private void handleFullRequest(long fileSize, String accessUrl,
                                    HttpServletResponse response, Long fileId) throws IOException {
        response.setContentLengthLong(fileSize);

        URLConnection conn = new URL(accessUrl).openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        try (InputStream inputStream = conn.getInputStream();
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }

        updateLastAccessTime(fileId);
    }

    /**
     * 构建符合 RFC 5987 规范的 Content-Disposition 头值
     * 对非 ASCII 文件名使用 filename*=UTF-8''url-encoded 格式，避免 Tomcat 异常
     */
    private String buildContentDisposition(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "inline; filename=\"file\"";
        }

        // 判断文件名是否为纯 ASCII
        boolean isAscii = fileName.chars().allMatch(c -> c < 128);

        if (isAscii) {
            return "inline; filename=\"" + fileName + "\"";
        }

        // 非 ASCII：用 RFC 5987 filename* 编码，同时提供 ASCII fallback
        String asciiFallback = fileName.replaceAll("[^\\x00-\\x7F]", "_");
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "inline; filename=\"" + asciiFallback + "\"; filename*=UTF-8''" + encodedName;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size == null) {
            return "0 B";
        }

        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    @Override
    public List<String> getPptxSlideImages(Long fileId) {
        log.info("渲染 PPTX 幻灯片：fileId={}", fileId);

        FileMetadata metadata = fileMetadataMapper.selectById(fileId);
        if (metadata == null) {
            throw new BusinessException("文件不存在");
        }

        String accessUrl = metadata.getAccessUrl();
        if (accessUrl == null || accessUrl.isEmpty()) {
            throw new BusinessException("文件存储路径不存在");
        }

        // 渲染分辨率：960x540（16:9，适配前端 ~852px 预览窗口）
        Dimension pgsize = new Dimension(960, 540);

        try {
            byte[] pptxBytes = readUrlBytes(accessUrl);

            // 先尝试直接解析；若 XML 格式异常则修复后重试
            try (XMLSlideShow ppt = createSlideShow(pptxBytes)) {
                return renderSlides(ppt, pgsize, fileId);
            } catch (Exception firstAttempt) {
                log.warn("PPTX 直接解析失败，尝试修复 XML 后重试：fileId={}, error={}",
                        fileId, firstAttempt.getMessage());

                byte[] repairedBytes = repairPptxSlideXml(pptxBytes);
                try (XMLSlideShow ppt = createSlideShow(repairedBytes)) {
                    log.info("PPTX XML 修复成功：fileId={}", fileId);
                    return renderSlides(ppt, pgsize, fileId);
                } catch (Exception repairFailed) {
                    log.error("PPTX XML 修复后仍解析失败：fileId={}", fileId, repairFailed);
                    throw firstAttempt;
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("PPTX 幻灯片渲染失败：fileId={}", fileId, e);
            throw new BusinessException("PPTX 幻灯片渲染失败：" + e.getMessage());
        }
    }

    /**
     * 从 URL 读取全部字节
     */
    private byte[] readUrlBytes(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            return is.readAllBytes();
        }
    }

    /**
     * 从字节数组创建 XMLSlideShow
     */
    private XMLSlideShow createSlideShow(byte[] data) throws IOException {
        return new XMLSlideShow(new ByteArrayInputStream(data));
    }

    /**
     * 渲染所有幻灯片为 Base64 PNG 列表
     */
    private List<String> renderSlides(XMLSlideShow ppt, Dimension pgsize, Long fileId) throws IOException {
        List<XSLFSlide> slides = ppt.getSlides();
        if (slides.isEmpty()) {
            throw new BusinessException("PPTX 文件中没有幻灯片");
        }

        List<String> images = new ArrayList<>(slides.size());
        for (XSLFSlide slide : slides) {
            BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, pgsize.width, pgsize.height);

            slide.draw(graphics);
            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
            images.add(base64);
        }

        log.info("PPTX 幻灯片渲染完成：fileId={}, slideCount={}", fileId, images.size());
        return images;
    }

    /**
     * 修复 PPTX 包中 XML 文件的标签缺失问题。
     *
     * <p>某些编辑工具导出的 PPTX 文件中，XML 可能缺少闭合标签（如
     * {@code </p:txBody>}、{@code </p:nvSpPr>} 等），导致 SAX 解析抛出
     * SAXParseException。此方法遍历所有 ppt XML 文件，使用标签平衡器
     * 补全缺失的闭合标签。</p>
     */
    private byte[] repairPptxSlideXml(byte[] pptxBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(pptxBytes));
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                byte[] entryData = zis.readAllBytes();

                if (isPptXmlFile(name)) {
                    String xml = new String(entryData, StandardCharsets.UTF_8);
                    String balanced = balanceXmlTags(xml);
                    if (!balanced.equals(xml)) {
                        log.info("PPTX XML 已修复：entry={} (原始 {} bytes, 修复后 {} bytes)",
                                name, xml.length(), balanced.length());
                    }
                    entryData = balanced.getBytes(StandardCharsets.UTF_8);
                }

                ZipEntry outEntry = new ZipEntry(name);
                zos.putNextEntry(outEntry);
                zos.write(entryData);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    /**
     * 判断 ZIP 条目是否为 PPTX 包内的 XML 文件（slides、layouts、masters）
     */
    private boolean isPptXmlFile(String name) {
        return name.startsWith("ppt/") && name.endsWith(".xml");
    }

    /**
     * 平衡 XML 标签：检测并补全缺失的闭合标签。
     *
     * <p>某些编辑工具导出的 PPTX 文件中，XML 可能缺少闭合标签（常见于
     * {@code p:txBody}、{@code p:nvSpPr}、{@code a:p} 等元素）。
     * 此方法使用栈结构跟踪开放标签，在遇到父元素闭合或文档末尾时自动
     * 补全所有未闭合的标签。</p>
     */
    private String balanceXmlTags(String xml) {
        List<String> openTags = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        int i = 0;
        int len = xml.length();

        while (i < len) {
            if (xml.charAt(i) == '<') {
                int tagEnd = xml.indexOf('>', i);
                if (tagEnd < 0) {
                    // 截断的 XML，直接追加剩余内容
                    result.append(xml, i, len);
                    break;
                }
                String tagContent = xml.substring(i + 1, tagEnd);
                boolean isClosing = tagContent.startsWith("/");
                boolean isSelfClosing = tagContent.endsWith("/");

                if (isSelfClosing || tagContent.startsWith("?") || tagContent.startsWith("!")) {
                    // 自闭合标签、处理指令、注释、CDATA：直接追加
                    result.append(xml, i, tagEnd + 1);
                    i = tagEnd + 1;
                } else if (isClosing) {
                    String tagName = extractTagName(tagContent.substring(1));
                    // 在闭合前，补全栈中该标签之上的所有未闭合标签
                    int lastIdx = openTags.lastIndexOf(tagName);
                    if (lastIdx >= 0) {
                        for (int j = openTags.size() - 1; j > lastIdx; j--) {
                            result.append("</").append(openTags.get(j)).append(">");
                        }
                        openTags.subList(lastIdx, openTags.size()).clear();
                    }
                    result.append(xml, i, tagEnd + 1);
                    i = tagEnd + 1;
                } else {
                    String tagName = extractTagName(tagContent);
                    openTags.add(tagName);
                    result.append(xml, i, tagEnd + 1);
                    i = tagEnd + 1;
                }
            } else {
                result.append(xml.charAt(i));
                i++;
            }
        }

        // 文档末尾补全所有剩余未闭合标签（逆序闭合）
        for (int j = openTags.size() - 1; j >= 0; j--) {
            result.append("</").append(openTags.get(j)).append(">");
        }

        return result.toString();
    }

    /**
     * 从标签内容中提取标签名（去掉命名空间前缀后的纯名称）。
     *
     * <p>例如 {@code "p:txBody"} → {@code "p:txBody"}，整个前缀和本地名作为标签名。</p>
     */
    private String extractTagName(String tagStr) {
        // 取第一个空白字符之前的 token，如 "p:txBody" 或 "a:bodyPr wrap=\"square\""
        // → "p:txBody" 或 "a:bodyPr"
        int spaceIdx = tagStr.indexOf(' ');
        if (spaceIdx >= 0) {
            return tagStr.substring(0, spaceIdx).trim();
        }
        return tagStr.trim();
    }

    /**
     * 从 kb_system_config 读取最大文件大小
     */
    private long getMaxFileSizeFromConfig() {
        String value = systemConfigCache.getConfig("file.upload.max.size");
        if (value != null) {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return 20971520L; // 默认20MB
    }

    /**
     * 从 kb_system_config 读取允许的文件类型列表
     */
    private List<String> getAllowedFileTypesFromConfig() {
        String value = systemConfigCache.getConfig("file.upload.allowed.types");
        if (value != null && !value.isBlank()) {
            return List.of(value.toLowerCase().split(","));
        }
        return List.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm", "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma");
    }
}
