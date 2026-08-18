package com.knowledge.base.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询参数基类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "分页查询参数")
public class PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField;

    /**
     * 排序方式（asc/desc）
     */
    @Schema(description = "排序方式", example = "desc")
    private String sortOrder;

    /**
     * 获取偏移量
     */
    public long getOffset() {
        return (current - 1) * size;
    }
}
