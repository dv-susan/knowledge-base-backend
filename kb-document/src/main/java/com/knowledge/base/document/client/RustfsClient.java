package com.knowledge.base.document.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * Rustfs文件服务器客户端
 *
 * <p>负责与rustfs文件服务器通信</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class RustfsClient {

    @Value("${file.server.rustfs.base-url:}")
    private String baseUrl;

    @Value("${file.server.rustfs.upload-path:/api/upload}")
    private String uploadPath;

    @Value("${file.server.rustfs.enabled:false}")
    private boolean enabled;

    @Value("${file.server.rustfs.auth-token:}")
    private String authToken;

    private final RestTemplate restTemplate;

    public RustfsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 上传文件到rustfs
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadFile(File file) {
        if (!enabled) {
            throw new UnsupportedOperationException("Rustfs文件服务器未启用");
        }

        try {
            String url = baseUrl + uploadPath;

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            if (authToken != null && !authToken.isEmpty()) {
                headers.set("Authorization", "Bearer " + authToken);
            }

            // 构建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("上传文件到rustfs：fileName={}, url={}", file.getName(), url);

            // 发送请求
            ResponseEntity<RustfsUploadResponse> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    RustfsUploadResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String fileUrl = response.getBody().getUrl();
                log.info("rustfs上传成功：fileUrl={}", fileUrl);
                return fileUrl;
            } else {
                throw new RuntimeException("rustfs上传失败：" + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("上传到rustfs失败", e);
            throw new RuntimeException("上传到rustfs失败: " + e.getMessage());
        }
    }

    /**
     * rustfs上传响应
     */
    private static class RustfsUploadResponse {
        private String url;
        private String fileName;
        private Long fileSize;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }
    }
}
