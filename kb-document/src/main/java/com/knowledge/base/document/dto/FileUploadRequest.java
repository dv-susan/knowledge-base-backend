package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件上传请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传请求")
public class FileUploadRequest implements Serializable {

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
