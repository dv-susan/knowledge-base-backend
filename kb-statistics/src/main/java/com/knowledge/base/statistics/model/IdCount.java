package com.knowledge.base.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ID + 计数 - 用于 MyBatis 聚合查询结果映射
 *
 * <p>适用于按维度ID分组统计的场景（如按用户ID、文档ID、分类ID分组）</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdCount {

    /** 维度ID（如用户ID、文档ID、分类ID） */
    private Long id;

    /** 数量 */
    private Long count;
}
