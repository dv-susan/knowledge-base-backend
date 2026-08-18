package com.knowledge.base.document.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件元数据VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataVO implements Serializable {

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件原始名称
     */
    private String originalFileName;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件大小（可读格式）
     */
    private String fileSizeReadable;

    /**
     * 文件类型（MIME类型）
     */
    private String contentType;

    /**
     * 文件访问URL
     */
    private String accessUrl;

    /**
     * 文件分类（image, document, video, audio, other）
     */
    private String fileCategory;

    /**
     * 上传用户ID
     */
    private Long uploaderId;

    /**
     * 上传用户名称
     */
    private String uploaderName;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;

    /**
     * 文件宽度（图片）
     */
    private Integer width;

    /**
     * 文件高度（图片）
     */
    private Integer height;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
}
