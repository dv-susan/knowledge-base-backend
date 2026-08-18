package com.knowledge.base.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件信息实体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode
@TableName("tb_file")
@Schema(description = "文件信息实体")
public class FileInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文件ID")
    private Long id;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String originalName;

    /**
     * 存储文件名
     */
    @Schema(description = "存储文件名")
    private String storedName;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * MIME类型
     */
    @Schema(description = "MIME类型")
    private String mimeType;

    /**
     * 文件哈希
     */
    @Schema(description = "文件哈希")
    private String fileHash;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID")
    private Long uploaderId;

    /**
     * 访问级别：0-私有，1-团队可见，2-公开
     */
    @Schema(description = "访问级别")
    private Integer accessLevel;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /**
     * 状态：0-删除，1-正常
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    /**
     * 创建时间（数据库字段：created_at）
     */
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间（数据库字段：updated_at）
     */
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private Long createBy;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateBy;

    /**
     * 时长（秒），音视频文件专用
     */
    @Schema(description = "时长(秒)")
    private Integer duration;

    /**
     * 分辨率，如 "1920x1080"
     */
    @Schema(description = "分辨率")
    private String resolution;

    /**
     * 码率（kbps）
     */
    @Schema(description = "码率(kbps)")
    private Integer bitrate;

    /**
     * 转码状态：PENDING/PROCESSING/DONE/FAILED
     */
    @Schema(description = "转码状态")
    private String transcodeStatus;

    /**
     * HLS播放列表路径（相对于bucket）
     */
    @Schema(description = "HLS播放列表路径")
    private String hlsPath;

    /**
     * 缩略图路径（相对于bucket）
     */
    @Schema(description = "缩略图路径")
    private String thumbnailPath;
}
