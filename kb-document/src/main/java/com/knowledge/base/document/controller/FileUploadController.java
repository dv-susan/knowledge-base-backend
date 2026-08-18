package com.knowledge.base.document.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.response.BatchConvertResponse;
import com.knowledge.base.document.dto.response.BatchUploadResponse;
import com.knowledge.base.document.dto.response.FileUploadResponse;
import com.knowledge.base.document.dto.response.ImageConvertResponse;
import com.knowledge.base.document.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传Controller
 *
 * <p>提供文件上传相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/files")
@Tag(name = "文件上传", description = "文件上传管理接口")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件并返回访问URL")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<FileUploadResponse> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file) {
        log.info("上传文件请求：fileName={}", file.getOriginalFilename());

        String fileUrl = fileUploadService.uploadFile(file);

        FileUploadResponse response = FileUploadResponse.builder()
                .url(fileUrl)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileSizeReadable(formatFileSize(file.getSize()))
                .build();

        return Result.success("上传成功", response);
    }

    /**
     * 从URL上传图片
     *
     * @param imageUrl 图片URL
     * @return 新的图片访问URL
     */
    @PostMapping("/upload-from-url")
    @Operation(summary = "从URL上传图片", description = "下载外部图片并上传到文件服务器")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<ImageConvertResponse> uploadFromUrl(
            @Parameter(description = "图片URL", required = true)
            @RequestParam("imageUrl") String imageUrl) {
        log.info("从URL上传图片：imageUrl={}", imageUrl);

        String newUrl = fileUploadService.uploadImageFromUrl(imageUrl);

        ImageConvertResponse response = ImageConvertResponse.builder()
                .originalUrl(imageUrl)
                .convertedUrl(newUrl)
                .build();

        return Result.success("上传成功", response);
    }

    /**
     * 批量上传图片
     *
     * @param files 文件列表
     * @return 文件URL列表
     */
    @PostMapping("/batch-upload")
    @Operation(summary = "批量上传文件", description = "批量上传多个文件")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<BatchUploadResponse> batchUpload(
            @Parameter(description = "文件列表", required = true)
            @RequestParam("files") MultipartFile[] files) {
        log.info("批量上传文件请求：fileCount={}", files.length);

        Map<String, String> fileUrls = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;

        for (MultipartFile file : files) {
            try {
                String url = fileUploadService.uploadFile(file);
                fileUrls.put(file.getOriginalFilename(), url);
                successCount++;
            } catch (Exception e) {
                log.error("文件上传失败：fileName={}", file.getOriginalFilename(), e);
                fileUrls.put(file.getOriginalFilename(), null);
                failureCount++;
            }
        }

        BatchUploadResponse response = BatchUploadResponse.builder()
                .fileUrls(fileUrls)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();

        return Result.success("批量上传完成", response);
    }

    /**
     * 转换图片URL
     *
     * @param imageUrl 图片URL
     * @return 转换后的URL
     */
    @PostMapping("/convert-url")
    @Operation(summary = "转换图片URL", description = "将外部图片URL转换为本站URL")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<ImageConvertResponse> convertImageUrl(
            @Parameter(description = "图片URL", required = true)
            @RequestParam("imageUrl") String imageUrl) {
        log.info("转换图片URL请求：imageUrl={}", imageUrl);

        String newUrl = fileUploadService.uploadImageFromUrl(imageUrl);

        ImageConvertResponse response = ImageConvertResponse.builder()
                .originalUrl(imageUrl)
                .convertedUrl(newUrl)
                .build();

        return Result.success("URL转换成功", response);
    }

    /**
     * 批量转换图片URL
     *
     * @param imageUrls 图片URL列表
     * @return 转换结果
     */
    @PostMapping("/batch-convert")
    @Operation(summary = "批量转换图片URL", description = "批量转换多个外部图片URL")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<BatchConvertResponse> batchConvertUrls(
            @Parameter(description = "图片URL列表", required = true)
            @RequestBody List<String> imageUrls) {
        log.info("批量转换图片URL请求：urlCount={}", imageUrls.size());

        Map<String, String> urlMappings = new HashMap<>();
        Map<String, String> errorMappings = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;

        for (String imageUrl : imageUrls) {
            try {
                String newUrl = fileUploadService.uploadImageFromUrl(imageUrl);
                urlMappings.put(imageUrl, newUrl);
                successCount++;
            } catch (Exception e) {
                log.error("图片URL转换失败：imageUrl={}", imageUrl, e);
                errorMappings.put(imageUrl, imageUrl); // 失败时保留原URL
                failureCount++;
            }
        }

        BatchConvertResponse response = BatchConvertResponse.builder()
                .urlMappings(urlMappings)
                .errorMappings(errorMappings)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();

        return Result.success("批量转换完成", response);
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
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
}
