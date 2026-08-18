package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件上传响应DTO
 *
 * <p>字段名与文件服务的FileInfoVO保持一致，确保正确反序列化</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileUploadResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件大小（可读格式）")
    private String fileSizeReadable;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "预览URL")
    private String previewUrl;

    @Schema(description = "上传者ID")
    private Long uploaderId;

    @Schema(description = "访问级别")
    private Integer accessLevel;

    @Schema(description = "下载次数")
    private Integer downloadCount;

    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "转换后的URL（用于URL转换接口）")
    private String convertedUrl;

    @Schema(description = "新URL（用于URL转换接口，与UrlConvertResponse的newUrl字段对应）")
    private String newUrl;
}
