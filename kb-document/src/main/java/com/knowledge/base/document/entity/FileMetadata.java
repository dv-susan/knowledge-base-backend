package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件元数据实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_file_metadata")
public class FileMetadata extends BaseEntity {

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
     * 文件类型（MIME类型）
     */
    private String contentType;

    /**
     * 文件存储路径
     */
    private String storagePath;

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
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 文件SHA256
     */
    private String fileSha256;

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

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;

    /**
     * 文件状态（uploading, completed, failed）
     */
    private String uploadStatus;

    /**
     * 错误信息
     */
    private String errorMessage;
}
