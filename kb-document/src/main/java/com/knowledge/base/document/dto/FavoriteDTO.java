package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 收藏操作DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "收藏操作请求参数")
public class FavoriteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID", required = true)
    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    /**
     * 操作类型（add-添加收藏，remove-取消收藏）
     */
    @Schema(description = "操作类型（add-添加收藏，remove-取消收藏）")
    private String action;
}
