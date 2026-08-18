package com.knowledge.base.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每日统计计数 - 用于 MyBatis 聚合查询结果映射
 *
 * <p>替代 {@code Map<String, Object>}，提供类型安全的每日聚合数据载体</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyCount {

    /** 日期（yyyy-MM-dd） */
    private String date;

    /** 数量 */
    private Long count;
}
