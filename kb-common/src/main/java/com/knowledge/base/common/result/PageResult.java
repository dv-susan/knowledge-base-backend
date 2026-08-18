package com.knowledge.base.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装类
 *
 * @param <T> 数据类型
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Long current;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Long size;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Long pages;

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long current, Long size, Long total, List<T> records) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setCurrent(current);
        pageResult.setSize(size);
        pageResult.setTotal(total);
        pageResult.setRecords(records);
        pageResult.setPages((total + size - 1) / size);
        return pageResult;
    }

    /**
     * 空分页结果
     */
    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .current(1L)
                .size(10L)
                .total(0L)
                .records(Collections.emptyList())
                .build();
    }

    /**
     * 判断是否有数据
     */
    public boolean hasData() {
        return records != null && !records.isEmpty();
    }
}
