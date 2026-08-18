package com.knowledge.base.file.dto;

import com.knowledge.base.common.result.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件查询请求")
public class FileQueryDTO extends PageParam {

    private static final long serialVersionUID = 1L;

    /**
     * 原始文件名（模糊查询）
     */
    @Schema(description = "文件名")
    private String originalName;

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
     * 访问级别
     */
    @Schema(description = "访问级别")
    private Integer accessLevel;
}
