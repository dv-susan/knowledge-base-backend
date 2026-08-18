package com.knowledge.base.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.file.dto.FileQueryDTO;
import com.knowledge.base.file.dto.FileUploadDTO;
import com.knowledge.base.file.entity.FileInfo;
import com.knowledge.base.file.vo.FileInfoVO;
import com.knowledge.base.file.vo.UrlConvertResponse;
import com.knowledge.base.file.vo.BatchConvertResponse;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 文件Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface FileService extends IService<FileInfo> {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param dto  上传参数
     * @return 文件信息
     */
    FileInfoVO uploadFile(MultipartFile file, FileUploadDTO dto);

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @param dto   上传参数
     * @return 文件信息列表
     */
    List<FileInfoVO> uploadFiles(MultipartFile[] files, FileUploadDTO dto);

    /**
     * 下载文件
     *
     * @param fileId     文件ID
     * @param response   HTTP响应
     */
    void downloadFile(Long fileId, HttpServletResponse response) throws IOException;

    /**
     * 获取文件流
     *
     * @param fileId 文件ID
     * @return 文件流
     */
    InputStream getFileStream(Long fileId) throws IOException;

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @return 是否成功
     */
    Boolean batchDeleteFiles(List<Long> fileIds);

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfoVO getFileInfo(Long fileId);

    /**
     * 分页查询文件
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    PageResult<FileInfoVO> pageFiles(FileQueryDTO dto);

    /**
     * 获取文件预览URL
     *
     * @param fileId 文件ID
     * @return 预览URL
     */
    String getPreviewUrl(Long fileId);

    /**
     * 预览文件（直接返回文件内容）
     *
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    void previewFile(Long fileId, HttpServletResponse response) throws IOException;

    /**
     * 文件格式转换
     *
     * @param fileId      文件ID
     * @param targetFormat 目标格式
     * @return 转换后的文件ID
     */
    Long convertFileFormat(Long fileId, String targetFormat);

    /**
     * 初始化断点续传
     *
     * @param fileHash 文件哈希
     * @param fileName 文件名
     * @param totalSize 总大小
     * @param chunkCount 分块数量
     * @return 会话ID
     */
    String initResumableUpload(String fileHash, String fileName, long totalSize, int chunkCount);

    /**
     * 上传分块
     *
     * @param sessionId 会话ID
     * @param chunkIndex 分块索引
     * @param chunkFile 分块文件
     * @return 是否成功
     */
    Boolean uploadChunk(String sessionId, int chunkIndex, MultipartFile chunkFile);

    /**
     * 获取已上传分块
     *
     * @param sessionId 会话ID
     * @return 已上传分块索引列表
     */
    int[] getUploadedChunks(String sessionId);

    /**
     * 合并分块
     *
     * @param sessionId 会话ID
     * @param dto 上传参数
     * @return 文件信息
     */
    FileInfoVO mergeChunks(String sessionId, FileUploadDTO dto);

    /**
     * 从URL转换图片（下载并上传到系统）
     *
     * @param imageUrl 外部图片URL
     * @return 转换结果
     */
    UrlConvertResponse convertFromUrl(String imageUrl);

    /**
     * 批量转换图片URL
     *
     * @param imageUrls 外部图片URL列表
     * @return 批量转换结果
     */
    BatchConvertResponse batchConvertUrls(List<String> imageUrls);

    /**
     * 流式播放HLS master播放列表
     *
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    void streamMasterPlaylist(Long fileId, HttpServletResponse response) throws IOException;

    /**
     * 流式播放HLS TS分片
     *
     * @param fileId 文件ID
     * @param segment TS分片文件名
     * @param response HTTP响应
     */
    void streamSegment(Long fileId, String segment, HttpServletResponse response) throws IOException;

    /**
     * 获取缩略图
     *
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    void getThumbnail(Long fileId, HttpServletResponse response) throws IOException;
}
