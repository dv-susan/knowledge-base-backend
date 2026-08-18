package com.knowledge.base.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 文件存储配置属性类
 * 
 * <p>集中管理 RustFS 分布式文件存储相关配置</p>
 * 
 * @author knowledge-base-team
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {

    /**
     * 存储类型: rustfs
     */
    private String type = "rustfs";

    /**
     * RustFS分布式存储配置
     */
    private Rustfs rustfs = new Rustfs();

    /**
     * 上传限制配置
     */
    private Upload upload = new Upload();

    /**
     * URL配置
     */
    private Url url = new Url();

    @Data
    public static class Rustfs {
        /**
         * RustFS服务端点地址
         */
        private String endpoint = "117.72.88.11";

        /**
         * 服务端口
         */
        private int port = 9091;

        /**
         * 访问密钥ID
         */
        private String accessKey = "IPL4GZeFumv8g9ANj3b7";

        /**
         * 秘密访问密钥
         */
        private String secretKey = "IFil9yzO520VLE8f7qoXHgBNkUG4TmWvDrSc1ReQ";

        /**
         * 是否使用HTTPS
         */
        private boolean secure = false;

        /**
         * 默认存储桶名称
         */
        private String bucketName = "mall-dev";

        /**
         * 连接超时时间（毫秒）
         */
        private int connectTimeout = 30000;

        /**
         * 读取超时时间（毫秒）
         */
        private int readTimeout = 60000;

        /**
         * 写入超时时间（毫秒）
         */
        private int writeTimeout = 60000;

        /**
         * 最大连接数
         */
        private int maxConnections = 50;

        /**
         * 是否启用RustFS
         */
        private boolean enabled = true;
    }

    /**
     * FFmpeg配置
     */
    private Ffmpeg ffmpeg = new Ffmpeg();

    @Data
    public static class Upload {
        /**
         * 最大文件大小（字节），默认2GB
         */
        private long maxSize = 2147483648L;

        /**
         * 允许的文件类型
         */
        private List<String> allowedTypes = Arrays.asList(
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
                "txt", "md",
                "png", "jpg", "jpeg", "gif", "bmp", "svg",
                "mp4", "avi", "mov", "wmv", "mkv", "webm", "flv",
                "mp3", "wav", "flac", "aac", "ogg"
        );

        /**
         * 是否启用秒传
         */
        private boolean enableFastUpload = true;

        /**
         * 是否计算文件哈希
         */
        private boolean calculateHash = true;

        /**
         * 是否启用断点续传
         */
        private boolean enableResumableUpload = true;
    }

    @Data
    public static class Ffmpeg {
        /**
         * FFmpeg可执行文件路径
         */
        private String path = "/usr/bin/ffmpeg";

        /**
         * FFprobe可执行文件路径
         */
        private String ffprobePath = "/usr/bin/ffprobe";

        /**
         * HLS分片时长（秒）
         */
        private int hlsSegmentTime = 10;

        /**
         * 缩略图截图时间点（秒）
         */
        private int thumbnailTime = 5;
    }

    @Data
    public static class Url {
        /**
         * 文件访问URL前缀
         */
        private String prefix = "http://localhost:8084/file";

        /**
         * 预览URL前缀
         */
        private String previewPrefix = "http://localhost:8084/file/preview";
    }
}