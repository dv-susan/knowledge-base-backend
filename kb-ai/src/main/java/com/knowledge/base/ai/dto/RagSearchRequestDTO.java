package com.knowledge.base.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * RAG检索请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "RAG检索请求")
public class RagSearchRequestDTO {

    @Schema(description = "查询文本")
    @NotBlank(message = "查询文本不能为空")
    private String query;

    @Schema(description = "返回结果数量")
    private int topK = 5;

    @Schema(description = "是否启用重排序")
    private boolean enableRerank = true;

    @Schema(description = "限定文档ID列表（可选）")
    private List<Long> filterDocIds;

    @Schema(description = "限定分类ID（可选）")
    private Long categoryId;
}
