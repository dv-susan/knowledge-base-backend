package com.knowledge.base.file.controller;

import com.knowledge.base.common.annotation.OperationLog;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.file.dto.FileQueryDTO;
import com.knowledge.base.file.dto.FileUploadDTO;
import com.knowledge.base.file.service.FileService;
import com.knowledge.base.file.vo.BatchConvertResponse;
import com.knowledge.base.file.vo.FileInfoVO;
import com.knowledge.base.file.vo.UrlConvertResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件管理Controller
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件管理相关接口")
public class FileController {

    private final FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传单个文件")
    @OperationLog(module = "文件管理", operation = "上传文件", description = "上传文件")
    public Result<FileInfoVO> uploadFile(
            @RequestPart("file") MultipartFile file,
            FileUploadDTO dto) {
        FileInfoVO fileInfo = fileService.uploadFile(file, dto);
        return Result.success(fileInfo);
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/upload/batch")
    @Operation(summary = "批量上传文件", description = "批量上传文件")
    @OperationLog(module = "文件管理", operation = "批量上传", description = "批量上传文件")
    public Result<List<FileInfoVO>> uploadFiles(
            @RequestPart("files") MultipartFile[] files,
            FileUploadDTO dto) {
        List<FileInfoVO> fileInfos = fileService.uploadFiles(files, dto);
        return Result.success(fileInfos);
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{fileId}/**")
    @Operation(summary = "下载文件", description = "下载指定文件")
    @OperationLog(module = "文件管理", operation = "下载文件", description = "下载文件")
    public void downloadFile(
            @PathVariable String fileId,
            HttpServletResponse response) throws IOException {
        // 从fileId中提取真实的文件ID（去除扩展名）
        Long realFileId = extractFileId(fileId);
        fileService.downloadFile(realFileId, response);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    @Operation(summary = "删除文件", description = "删除指定文件")
    @OperationLog(module = "文件管理", operation = "删除文件", description = "删除文件")
    public Result<Boolean> deleteFile(@PathVariable Long fileId) {
        Boolean result = fileService.deleteFile(fileId);
        return Result.success(result);
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件", description = "批量删除文件")
    @OperationLog(module = "文件管理", operation = "批量删除", description = "批量删除文件")
    public Result<Boolean> batchDeleteFiles(@RequestBody List<Long> fileIds) {
        Boolean result = fileService.batchDeleteFiles(fileIds);
        return Result.success(result);
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/{fileId}")
    @Operation(summary = "获取文件详情", description = "根据ID获取文件详情")
    public Result<FileInfoVO> getFileInfo(@PathVariable Long fileId) {
        FileInfoVO fileInfo = fileService.getFileInfo(fileId);
        return Result.success(fileInfo);
    }

    /**
     * 分页查询文件
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询文件", description = "分页查询文件列表")
    public Result<PageResult<FileInfoVO>> pageFiles(@RequestBody FileQueryDTO dto) {
        PageResult<FileInfoVO> pageResult = fileService.pageFiles(dto);
        return Result.success(pageResult);
    }

    /**
     * 获取文件预览URL
     */
    @GetMapping("/preview/{fileId}")
    @Operation(summary = "获取文件预览URL", description = "获取文件预览URL")
    public Result<String> getPreviewUrl(@PathVariable String fileId) {
        // 从fileId中提取真实的文件ID（去除扩展名）
        Long realFileId = extractFileId(fileId);
        String previewUrl = fileService.getPreviewUrl(realFileId);
        return Result.success(previewUrl);
    }

    /**
     * 预览文件（直接返回文件内容）
     */
    @GetMapping("/preview/{fileId}/**")
    @Operation(summary = "预览文件", description = "预览指定文件内容")
    public void previewFile(
            @PathVariable String fileId,
            HttpServletResponse response) throws IOException {
        // 从fileId中提取真实的文件ID（去除扩展名）
        Long realFileId = extractFileId(fileId);
        fileService.previewFile(realFileId, response);
    }

    /**
     * 从URL转换图片（下载并上传到系统）
     */
    @PostMapping("/convert-url")
    @Operation(summary = "从URL转换图片", description = "下载外部图片并上传到系统")
    @OperationLog(module = "文件管理", operation = "URL转换", description = "从URL转换图片")
    public Result<UrlConvertResponse> convertFromUrl(@RequestParam String imageUrl) {
        UrlConvertResponse response = fileService.convertFromUrl(imageUrl);
        return Result.success(response);
    }

    /**
     * 批量转换图片URL
     */
    @PostMapping("/batch-convert")
    @Operation(summary = "批量转换图片URL", description = "批量下载外部图片并上传到系统")
    @OperationLog(module = "文件管理", operation = "批量转换", description = "批量转换图片URL")
    public Result<BatchConvertResponse> batchConvertUrls(@RequestBody List<String> imageUrls) {
        BatchConvertResponse response = fileService.batchConvertUrls(imageUrls);
        return Result.success(response);
    }

    /**
     * 文件格式转换
     */
    @PostMapping("/convert/{fileId}")
    @Operation(summary = "文件格式转换", description = "转换文件格式")
    @OperationLog(module = "文件管理", operation = "格式转换", description = "文件格式转换")
    public Result<Long> convertFileFormat(
            @PathVariable Long fileId,
            @RequestParam String targetFormat) {
        Long convertedFileId = fileService.convertFileFormat(fileId, targetFormat);
        return Result.success(convertedFileId);
    }

    /**
     * 流式播放HLS master播放列表
     */
    @GetMapping("/stream/{fileId}/master.m3u8")
    @Operation(summary = "HLS播放列表", description = "获取HLS master播放列表")
    public void streamMasterPlaylist(
            @PathVariable Long fileId,
            HttpServletResponse response) throws IOException {
        fileService.streamMasterPlaylist(fileId, response);
    }

    /**
     * 流式播放HLS TS分片
     */
    @GetMapping("/stream/{fileId}/{segment:.+\\.ts}")
    @Operation(summary = "HLS分片", description = "获取HLS TS分片")
    public void streamSegment(
            @PathVariable Long fileId,
            @PathVariable String segment,
            HttpServletResponse response) throws IOException {
        fileService.streamSegment(fileId, segment, response);
    }

    /**
     * 获取缩略图
     */
    @GetMapping("/thumbnail/{fileId}")
    @Operation(summary = "获取缩略图", description = "获取视频缩略图")
    public void getThumbnail(
            @PathVariable Long fileId,
            HttpServletResponse response) throws IOException {
        fileService.getThumbnail(fileId, response);
    }

    /**
     * 从文件ID中提取真实的文件ID（去除扩展名）
     *
     * @param fileId 可能包含扩展名的文件ID
     * @return 真实的文件ID
     */
    private Long extractFileId(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            throw new IllegalArgumentException("文件ID不能为空");
        }

        // 查找第一个点号，提取点号之前的部分作为真实的文件ID
        int dotIndex = fileId.indexOf('.');
        String realIdStr = dotIndex > 0 ? fileId.substring(0, dotIndex) : fileId;

        try {
            return Long.parseLong(realIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的文件ID格式: " + fileId);
        }
    }
}
