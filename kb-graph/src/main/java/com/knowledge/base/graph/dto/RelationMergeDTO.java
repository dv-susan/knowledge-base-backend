package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系合并参数
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationMergeDTO {

    /** 源实体名称 */
    private String source;

    /** 目标实体名称 */
    private String target;

    /** 关系类型 */
    private String relationType;

    /** 关系权重 */
    private Double weight;
}
