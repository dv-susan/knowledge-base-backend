package com.knowledge.base.document.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件服务器配置
 *
 * <p>配置rustfs文件服务器相关信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.server.rustfs")
public class FileServerConfig {

    /**
     * 文件服务器地址
     */
    private String baseUrl = "http://localhost:8080";

    /**
     * 上传接口路径
     */
    private String uploadPath = "/api/upload";

    /**
     * 访问路径前缀
     */
    private String accessPrefix = "/files";

    /**
     * 认证Token（如果需要）
     */
    private String authToken;

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 30000;

    /**
     * 最大文件大小（字节）
     */
    private Long maxFileSize = 20971520L; // 20MB
}
