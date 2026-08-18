package com.knowledge.base.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 搜索历史创建DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索历史创建请求")
public class SearchHistoryCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词")
    private String keyword;

    /**
     * 搜索类型（basic、advanced）
     */
    @Schema(description = "搜索类型")
    private String searchType;

    /**
     * 结果数量
     */
    @Schema(description = "结果数量")
    private Integer resultCount;

    /**
     * 搜索参数（JSON格式）
     */
    @Schema(description = "搜索参数")
    private String searchParams;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
}
