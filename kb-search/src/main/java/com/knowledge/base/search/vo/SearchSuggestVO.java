package com.knowledge.base.search.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 搜索建议VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索建议")
public class SearchSuggestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 建议文本
     */
    @Schema(description = "建议文本")
    private String text;

    /**
     * 建议类型
     */
    @Schema(description = "建议类型")
    private String type;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Long documentId;

    /**
     * 匹配得分
     */
    @Schema(description = "匹配得分")
    private Float score;
}
