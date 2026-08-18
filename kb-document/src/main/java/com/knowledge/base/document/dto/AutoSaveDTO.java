package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 自动保存DTO
 *
 * <p>与 DocumentDTO 的关键区别：标题非必填，无 status 字段（后端强制草稿状态）。
 * 用于前端编辑器自动保存场景，允许用户在未填写完整表单时保存进度。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "自动保存请求参数 - 允许空标题，强制草稿状态")
public class AutoSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID（更新时传入，创建时留空）
     */
    @Schema(description = "文档ID（更新时传入，创建时留空）", example = "1234567890123456789")
    private Long id;

    /**
     * 文档标题（可为空，后端自动填充"未命名文档"）
     */
    @Schema(description = "文档标题（可为空）", example = "Spring Boot使用指南")
    @Size(max = 200, message = "文档标题长度不能超过200个字符")
    private String title;

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    private String content;

    /**
     * 文档摘要
     */
    @Schema(description = "文档摘要")
    @Size(max = 500, message = "文档摘要长度不能超过500个字符")
    private String summary;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1234567890123456789")
    private Long categoryId;

    /**
     * 团队空间ID
     */
    @Schema(description = "团队空间ID", example = "1234567890123456789")
    private Long teamId;

    /**
     * 标签（逗号分隔）
     */
    @Schema(description = "标签（逗号分隔）", example = "Spring Boot,Java")
    @Size(max = 200, message = "标签长度不能超过200个字符")
    private String tags;
}
