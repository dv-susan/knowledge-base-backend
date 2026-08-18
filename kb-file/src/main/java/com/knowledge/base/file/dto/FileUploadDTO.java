package com.knowledge.base.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

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
     * 团队ID
     */
    @Schema(description = "团队ID")
    private Long teamId;
}
