package com.knowledge.base.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "搜索请求")
public class SearchRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词")
    @NotBlank(message = "搜索关键词不能为空")
    private String keyword;

    /**
     * 分类ID列表
     */
    @Schema(description = "分类ID列表")
    private List<Long> categoryIds;

    /**
     * 标签ID列表
     */
    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    /**
     * 团队ID列表
     */
    @Schema(description = "团队ID列表")
    private List<Long> teamIds;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long creatorId;

    /**
     * 文档状态
     */
    @Schema(description = "文档状态")
    private Integer docStatus;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField;

    /**
     * 排序方式
     */
    @Schema(description = "排序方式")
    private String sortOrder;

    /**
     * 页码
     */
    @Schema(description = "页码")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer size = 10;

    /**
     * 搜索模式：keyword(关键词搜索) / hybrid(混合智能搜索)
     */
    @Schema(description = "搜索模式：keyword / hybrid", example = "hybrid")
    private String searchMode = "keyword";

    /**
     * 混合搜索返回Top-K结果数（searchMode=hybrid时生效）
     */
    @Schema(description = "混合搜索Top-K")
    private int topK = 10;

    /**
     * 是否启用LLM重排序（searchMode=hybrid时生效）
     */
    @Schema(description = "是否启用LLM重排序")
    private boolean enableRerank = true;
}
