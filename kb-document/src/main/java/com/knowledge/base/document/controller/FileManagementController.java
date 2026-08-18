package com.knowledge.base.document.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.entity.FileMetadata;
import com.knowledge.base.document.service.FileManagementService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.FileMetadataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件管理Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/file-management")
@Tag(name = "文件管理", description = "文件管理中心接口")
public class FileManagementController {

    @Resource
    private FileManagementService fileManagementService;

    /**
     * 上传文件
     *
     * @param file     文件
     * @param isPublic 是否公开
     * @return 文件元数据
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件并保存元数据")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<FileMetadataVO> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "是否公开")
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic) {

        Long userId = UserContext.getCurrentUserId();
        log.info("上传文件请求：userId={}, fileName={}", userId, file.getOriginalFilename());

        FileMetadata metadata = fileManagementService.uploadFile(file, userId, isPublic);
        return Result.success("上传成功", convertToVO(metadata));
    }

    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取文件列表", description = "获取当前用户的文件列表")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<List<FileMetadataVO>> getFileList() {
        Long userId = UserContext.getCurrentUserId();
        log.info("获取文件列表请求：userId={}", userId);

        List<FileMetadata> files = fileManagementService.getFileList(userId);
        List<FileMetadataVO> vos = files.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(vos);
    }

    /**
     * 按分类获取文件列表
     *
     * @param category 文件分类
     * @return 文件列表
     */
    @GetMapping("/list/{category}")
    @Operation(summary = "按分类获取文件列表", description = "按文件分类获取文件列表")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<List<FileMetadataVO>> getFileListByCategory(
            @Parameter(description = "文件分类", required = true)
            @PathVariable("category") String category) {

        Long userId = UserContext.getCurrentUserId();
        log.info("按分类获取文件列表请求：userId={}, category={}", userId, category);

        List<FileMetadata> files = fileManagementService.getFileListByCategory(userId, category);
        List<FileMetadataVO> vos = files.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(vos);
    }

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @return 文件详情
     */
    @GetMapping("/detail/{fileId}")
    @Operation(summary = "获取文件详情", description = "获取文件详细信息")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<FileMetadataVO> getFileDetail(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId) {

        log.info("获取文件详情请求：fileId={}", fileId);
        FileMetadata metadata = fileManagementService.getFileDetail(fileId);
        return Result.success(convertToVO(metadata));
    }

    /**
     * 重命名文件
     *
     * @param fileId     文件ID
     * @param newFileName 新文件名
     * @return 是否成功
     */
    @PutMapping("/rename/{fileId}")
    @Operation(summary = "重命名文件", description = "重命名文件")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> renameFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId,
            @Parameter(description = "新文件名", required = true)
            @RequestParam("newFileName") String newFileName) {

        Long userId = UserContext.getCurrentUserId();
        log.info("重命名文件请求：userId={}, fileId={}, newFileName={}", userId, fileId, newFileName);

        Boolean result = fileManagementService.renameFile(fileId, newFileName, userId);
        return Result.success("重命名成功", result);
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    @DeleteMapping("/delete/{fileId}")
    @Operation(summary = "删除文件", description = "删除文件")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> deleteFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId) {

        Long userId = UserContext.getCurrentUserId();
        log.info("删除文件请求：userId={}, fileId={}", userId, fileId);

        Boolean result = fileManagementService.deleteFile(fileId, userId);
        return Result.success("删除成功", result);
    }

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @return 删除数量
     */
    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除文件", description = "批量删除文件")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Integer> batchDeleteFiles(
            @Parameter(description = "文件ID列表", required = true)
            @RequestBody List<Long> fileIds) {

        Long userId = UserContext.getCurrentUserId();
        log.info("批量删除文件请求：userId={}, fileIds={}", userId, fileIds);

        Integer count = fileManagementService.batchDeleteFiles(fileIds, userId);
        return Result.success("删除完成", count);
    }

    /**
     * 更新文件权限
     *
     * @param fileId   文件ID
     * @param isPublic 是否公开
     * @return 是否成功
     */
    @PutMapping("/permission/{fileId}")
    @Operation(summary = "更新文件权限", description = "更新文件访问权限")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<Boolean> updateFilePermission(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId,
            @Parameter(description = "是否公开", required = true)
            @RequestParam("isPublic") Boolean isPublic) {

        Long userId = UserContext.getCurrentUserId();
        log.info("更新文件权限请求：userId={}, fileId={}, isPublic={}", userId, fileId, isPublic);

        Boolean result = fileManagementService.updateFilePermission(fileId, isPublic, userId);
        return Result.success("权限更新成功", result);
    }

    /**
     * 增加下载次数
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    @PostMapping("/download/{fileId}")
    @Operation(summary = "下载文件", description = "下载文件并增加下载次数")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<Boolean> downloadFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId) {

        log.info("下载文件请求：fileId={}", fileId);
        fileManagementService.incrementDownloadCount(fileId);
        return Result.success("下载成功", true);
    }

    /**
     * 获取文件统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取文件统计信息", description = "获取文件统计信息")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<Map<String, Object>> getFileStatistics() {
        Long userId = UserContext.getCurrentUserId();
        log.info("获取文件统计信息请求：userId={}", userId);

        Map<String, Object> statistics = fileManagementService.getFileStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 复制文件
     *
     * @param fileId 文件ID
     * @return 新的文件元数据
     */
    @PostMapping("/copy/{fileId}")
    @Operation(summary = "复制文件", description = "复制文件")
    @PreAuthorize("hasAnyAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_CREATE, T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_EDIT)")
    public Result<FileMetadataVO> copyFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId) {

        Long userId = UserContext.getCurrentUserId();
        log.info("复制文件请求：userId={}, fileId={}", userId, fileId);

        FileMetadata newFile = fileManagementService.copyFile(fileId, userId);
        return Result.success("复制成功", convertToVO(newFile));
    }

    /**
     * 搜索文件
     *
     * @param keyword 搜索关键词
     * @return 文件列表
     */
    @GetMapping("/stream/{fileId}")
    @Operation(summary = "流式播放文件", description = "流式传输音视频文件内容（代理RUSTFS），支持HTTP Range请求")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public void streamFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response) {

        log.info("流式播放文件请求：fileId={}", fileId);
        fileManagementService.streamFile(fileId, request, response);
    }

    @GetMapping("/preview/{fileId}/slides")
    @Operation(summary = "获取 PPTX 幻灯片图片", description = "将 PPTX 文件渲染为每页幻灯片的 PNG 图片（Base64）")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<List<String>> getPptxSlideImages(
            @Parameter(description = "文件ID", required = true)
            @PathVariable("fileId") Long fileId) {

        log.info("获取 PPTX 幻灯片图片请求：fileId={}", fileId);
        List<String> slideImages = fileManagementService.getPptxSlideImages(fileId);
        return Result.success(slideImages);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索文件", description = "根据关键词搜索文件")
    @PreAuthorize("hasAuthority(T(com.knowledge.base.document.constants.DocumentPermissionConstants).DOCUMENT_LIST)")
    public Result<List<FileMetadataVO>> searchFiles(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam("keyword") String keyword) {

        Long userId = UserContext.getCurrentUserId();
        log.info("搜索文件请求：userId={}, keyword={}", userId, keyword);

        List<FileMetadata> files = fileManagementService.searchFiles(userId, keyword);
        List<FileMetadataVO> vos = files.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(vos);
    }

    /**
     * 转换为VO对象
     */
    private FileMetadataVO convertToVO(FileMetadata metadata) {
        FileMetadataVO vo = new FileMetadataVO();
        vo.setId(metadata.getId());
        vo.setFileName(metadata.getFileName());
        vo.setOriginalFileName(metadata.getOriginalFileName());
        vo.setFileExtension(metadata.getFileExtension());
        vo.setFileSize(metadata.getFileSize());
        vo.setFileSizeReadable(formatFileSize(metadata.getFileSize()));
        vo.setContentType(metadata.getContentType());
        vo.setAccessUrl(metadata.getAccessUrl());
        vo.setFileCategory(metadata.getFileCategory());
        vo.setUploaderId(metadata.getUploaderId());
        vo.setUploaderName(metadata.getUploaderName());
        vo.setIsPublic(metadata.getIsPublic());
        vo.setDownloadCount(metadata.getDownloadCount());
        vo.setCreatedAt(metadata.getCreatedAt());
        vo.setUpdatedAt(metadata.getUpdatedAt());
        vo.setLastAccessTime(metadata.getLastAccessTime());
        vo.setWidth(metadata.getWidth());
        vo.setHeight(metadata.getHeight());
        vo.setThumbnailUrl(metadata.getThumbnailUrl());
        return vo;
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
}
