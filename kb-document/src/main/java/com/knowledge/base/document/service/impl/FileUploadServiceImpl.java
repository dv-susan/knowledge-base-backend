package com.knowledge.base.document.service.impl;

import com.knowledge.base.document.dto.FileUploadResponse;
import com.knowledge.base.document.feign.FileServiceFeignClient;
import com.knowledge.base.document.service.FileUploadService;
import com.knowledge.base.document.util.CustomMultipartFile;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传服务实现类
 *
 * <p>通过Feign客户端调用文件服务</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private FileServiceFeignClient fileServiceFeignClient;

    /**
     * 内部域名（不需要上传的域名）
     */
    private static final Set<String> INTERNAL_DOMAINS = new HashSet<>(
            Arrays.asList("rustfs", "localhost", "127.0.0.1", "117.72.88.11"));

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();

        try {
            // 调用 kb-file 文件服务，将文件上传到 RUSTFS 存储
            var response = fileServiceFeignClient.uploadFile(
                    file,
                    "document",  // fileType
                    1,           // accessLevel: 团队可见
                    null         // teamId
            );

            if (response != null && response.getCode() == 200) {
                var uploadResponse = response.getData();
                if (uploadResponse != null) {
                    String fileUrl = uploadResponse.getFileUrl();
                    log.info("文件上传到 kb-file RUSTFS 成功：fileName={}, fileUrl={}", originalFilename, fileUrl);
                    return fileUrl;
                }
            }

            // Feign 调用返回非 200
            log.error("文件服务 Feign 调用返回非 200 (code={})，fileName={}",
                    response != null ? response.getCode() : "null", originalFilename);
            throw new RuntimeException("文件上传到文件服务器失败，响应码: " +
                    (response != null ? response.getCode() : "null"));

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传到 kb-file 失败：fileName={}", originalFilename, e);
            throw new RuntimeException("文件上传到文件服务器失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadBytes(byte[] bytes, String fileName, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("文件内容不能为空");
        }

        try {
            // 将字节数组转换为MultipartFile
            MultipartFile file = new CustomMultipartFile(
                    bytes,
                    "file",
                    fileName,
                    contentType != null ? contentType : "application/octet-stream"
            );

            return uploadFile(file);
        } catch (Exception e) {
            log.error("字节数组上传失败", e);
            throw new RuntimeException("字节数组上传失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadImageFromUrl(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            throw new IllegalArgumentException("图片URL不能为空");
        }

        // 如果是内部URL，直接返回
        if (!isExternalImageUrl(imageUrl)) {
            return imageUrl;
        }

        try {
            log.info("开始从外部URL下载图片：imageUrl={}", imageUrl);

            // 调用文件服务转换URL
            var response = fileServiceFeignClient.convertImageUrl(imageUrl);

            if (response != null && response.getCode() == 200) {
                var convertResponse = response.getData();
                if (convertResponse != null) {
                    // 优先使用newUrl字段（文件服务UrlConvertResponse返回的字段）
                    // 如果newUrl为空，再尝试convertedUrl字段
                    String newUrl = convertResponse.getNewUrl();
                    if (!StringUtils.hasText(newUrl)) {
                        newUrl = convertResponse.getConvertedUrl();
                    }
                    
                    // 如果获取到了有效的URL，直接返回
                    if (StringUtils.hasText(newUrl)) {
                        log.info("外部图片上传成功：originalUrl={}, newUrl={}", imageUrl, newUrl);
                        return newUrl;
                    }
                }
            }

            // Feign调用失败或返回URL为空
            log.error("文件服务URL转换失败或返回URL为空：imageUrl={}", imageUrl);
            throw new RuntimeException("外部图片上传到文件服务器失败");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("从URL上传图片失败：imageUrl={}", imageUrl, e);
            throw new RuntimeException("从URL上传图片失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isExternalImageUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }

        // 检查是否为HTTP(S)链接
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }

        // 检查是否为内部域名
        for (String domain : INTERNAL_DOMAINS) {
            if (url.contains(domain)) {
                return false;
            }
        }

        return true;
    }

}
