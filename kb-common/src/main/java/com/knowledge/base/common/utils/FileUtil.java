package com.knowledge.base.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 *
 * <p>提供文件操作相关的工具方法</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class FileUtil {

    /**
     * 允许的图片扩展名
     */
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    );

    /**
     * 允许的文档扩展名
     */
    private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "html", "htm"
    );

    /**
     * 允许的视频扩展名
     */
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
            "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm"
    );

    /**
     * 允许的音频扩展名
     */
    private static final List<String> AUDIO_EXTENSIONS = Arrays.asList(
            "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma"
    );

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 判断是否为图片文件
     *
     * @param filename 文件名
     * @return 是否为图片
     */
    public static boolean isImageFile(String filename) {
        String extension = getFileExtension(filename);
        return IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * 判断是否为文档文件
     *
     * @param filename 文件名
     * @return 是否为文档
     */
    public static boolean isDocumentFile(String filename) {
        String extension = getFileExtension(filename);
        return DOCUMENT_EXTENSIONS.contains(extension);
    }

    /**
     * 判断是否为视频文件
     *
     * @param filename 文件名
     * @return 是否为视频
     */
    public static boolean isVideoFile(String filename) {
        String extension = getFileExtension(filename);
        return VIDEO_EXTENSIONS.contains(extension);
    }

    /**
     * 判断是否为音频文件
     *
     * @param filename 文件名
     * @return 是否为音频
     */
    public static boolean isAudioFile(String filename) {
        String extension = getFileExtension(filename);
        return AUDIO_EXTENSIONS.contains(extension);
    }

    /**
     * 检测文件类型
     *
     * @param filename 文件名
     * @param mimeType MIME类型
     * @return 文件类型
     */
    public static String detectFileType(String filename, String mimeType) {
        if (isImageFile(filename)) {
            return "IMAGE";
        } else if (isDocumentFile(filename)) {
            return "DOCUMENT";
        } else if (isVideoFile(filename)) {
            return "VIDEO";
        } else if (isAudioFile(filename)) {
            return "AUDIO";
        } else {
            return "OTHER";
        }
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }

    /**
     * 确保目录存在
     *
     * @param dirPath 目录路径
     * @throws IOException IO异常
     */
    public static void ensureDirExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 获取相对路径
     *
     * @param fullPath 完整路径
     * @param basePath 基础路径
     * @return 相对路径
     */
    public static String getRelativePath(String fullPath, String basePath) {
        Path full = Paths.get(fullPath).normalize();
        Path base = Paths.get(basePath).normalize();
        return base.relativize(full).toString();
    }

    /**
     * 获取文件大小的可读格式
     *
     * @param size 文件大小（字节）
     * @return 可读格式
     */
    public static String getReadableFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / 1024.0 / 1024);
        } else {
            return String.format("%.2f GB", size / 1024.0 / 1024 / 1024);
        }
    }

    /**
     * 验证文件名是否合法
     *
     * @param filename 文件名
     * @return 是否合法
     */
    public static boolean isValidFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        // 检查是否包含非法字符
        String[] illegalChars = {"/", "\\", ":", "*", "?", "\"", "<", ">", "|", "\0"};
        for (String illegalChar : illegalChars) {
            if (filename.contains(illegalChar)) {
                return false;
            }
        }

        // 检查是否为保留名称
        String[] reservedNames = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4",
                "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4",
                "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
        String nameWithoutExt = filename.contains(".")
                ? filename.substring(0, filename.lastIndexOf("."))
                : filename;
        for (String reserved : reservedNames) {
            if (reserved.equalsIgnoreCase(nameWithoutExt)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 从MultipartFile读取字节数组
     *
     * @param file MultipartFile
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] readBytes(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }
}
