package com.knowledge.base.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件信息")
public class FileInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long id;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String originalName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件大小（可读）
     */
    @Schema(description = "文件大小（可读）")
    private String fileSizeReadable;

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
     * 文件URL
     */
    @Schema(description = "文件URL")
    private String fileUrl;

    /**
     * 预览URL
     */
    @Schema(description = "预览URL")
    private String previewUrl;

    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID")
    private Long uploaderId;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称")
    private String uploaderName;

    /**
     * 访问级别
     */
    @Schema(description = "访问级别")
    private Integer accessLevel;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

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
     * HLS播放URL
     */
    @Schema(description = "HLS播放URL")
    private String playUrl;

    /**
     * 缩略图URL
     */
    @Schema(description = "缩略图URL")
    private String thumbnailUrl;
}
