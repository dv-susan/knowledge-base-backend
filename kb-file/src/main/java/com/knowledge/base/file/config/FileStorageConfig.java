package com.knowledge.base.file.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件存储配置类
 *
 * <p>配置本地文件存储服务</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class FileStorageConfig {

    @Value("${file.upload.path:/tmp/knowledge-base/uploads}")
    private String uploadPath;

    /**
     * 初始化时创建上传目录
     */
    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("创建文件上传目录: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建文件上传目录失败: {}", e.getMessage(), e);
            throw new RuntimeException("无法创建文件上传目录", e);
        }
    }
}