package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.document.entity.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 文件管理服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface FileManagementService extends IService<FileMetadata> {

    /**
     * 上传文件并保存元数据
     *
     * @param file     文件
     * @param userId   用户ID
     * @param isPublic 是否公开
     * @return 文件元数据
     */
    FileMetadata uploadFile(MultipartFile file, Long userId, Boolean isPublic);

    /**
     * 获取文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileMetadata> getFileList(Long userId);

    /**
     * 按分类获取文件列表
     *
     * @param userId       用户ID
     * @param fileCategory 文件分类
     * @return 文件列表
     */
    List<FileMetadata> getFileListByCategory(Long userId, String fileCategory);

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @return 文件元数据
     */
    FileMetadata getFileDetail(Long fileId);

    /**
     * 重命名文件
     *
     * @param fileId     文件ID
     * @param newFileName 新文件名
     * @param userId     用户ID
     * @return 是否成功
     */
    Boolean renameFile(Long fileId, String newFileName, Long userId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteFile(Long fileId, Long userId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @param userId  用户ID
     * @return 删除数量
     */
    Integer batchDeleteFiles(List<Long> fileIds, Long userId);

    /**
     * 更新文件访问权限
     *
     * @param fileId   文件ID
     * @param isPublic 是否公开
     * @param userId   用户ID
     * @return 是否成功
     */
    Boolean updateFilePermission(Long fileId, Boolean isPublic, Long userId);

    /**
     * 增加下载次数
     *
     * @param fileId 文件ID
     */
    void incrementDownloadCount(Long fileId);

    /**
     * 更新最后访问时间
     *
     * @param fileId 文件ID
     */
    void updateLastAccessTime(Long fileId);

    /**
     * 获取文件统计信息
     *
     * @param userId 用户ID
     * @return 统计信息Map
     */
    Map<String, Object> getFileStatistics(Long userId);

    /**
     * 复制文件
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 新的文件元数据
     */
    FileMetadata copyFile(Long fileId, Long userId);

    /**
     * 搜索文件
     *
     * @param userId  用户ID
     * @param keyword 搜索关键词
     * @return 文件列表
     */
    List<FileMetadata> searchFiles(Long userId, String keyword);

    /**
     * 流式传输文件内容（从RUSTFS代理到浏览器）
     * 支持 HTTP Range 请求，返回 206 Partial Content 供浏览器音频/视频元素播放
     *
     * @param fileId   文件ID
     * @param request  HTTP请求（用于读取Range头）
     * @param response HTTP响应
     */
    void streamFile(Long fileId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 将 PPTX 文件每页幻灯片渲染为 PNG 图片（Base64 编码）
     *
     * @param fileId 文件ID
     * @return 每页幻灯片的 data:image/png;base64 列表
     */
    List<String> getPptxSlideImages(Long fileId);
}
