package com.knowledge.base.file.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;

/**
 * RustFS S3 客户端配置类
 * 使用 AWS SDK 连接支持 S3 兼容 API 的 RustFS 分布式文件系统
 */
@Configuration
@ConditionalOnProperty(name = "file.storage.type", havingValue = "rustfs")
public class RustFsS3Config {

    private final FileStorageProperties storageProperties;

    public RustFsS3Config(FileStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Bean
    public S3Client s3Client() {
        FileStorageProperties.Rustfs config = storageProperties.getRustfs();
        
        // 构建端点 URI
        String protocol = config.isSecure() ? "https" : "http";
        URI endpointUri = URI.create(protocol + "://" + config.getEndpoint() + ":" + config.getPort());

        // 超时配置
        Duration connectTimeout = Duration.ofMillis(config.getConnectTimeout());
        Duration readTimeout = Duration.ofMillis(config.getReadTimeout());
        Duration writeTimeout = Duration.ofMillis(config.getWriteTimeout());

        // S3 客户端配置
        S3Configuration s3Config = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        // API 调用超时（避免大文件上传时无限期阻塞）
        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(readTimeout.plus(writeTimeout))
                .apiCallAttemptTimeout(readTimeout)
                .build();

        return S3Client.builder()
                // 设置自定义端点（指向 RustFS 服务器）
                .endpointOverride(endpointUri)
                // 配置凭证
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())
                ))
                // 设置区域（对于 S3 兼容存储，可以使用任意值）
                .region(Region.of("us-east-1"))
                // S3 特定配置
                .serviceConfiguration(s3Config)
                // API 超时配置
                .overrideConfiguration(overrideConfig)
                .build();
    }
}
