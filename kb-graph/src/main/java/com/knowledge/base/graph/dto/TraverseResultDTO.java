package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 多跳遍历结果
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraverseResultDTO {

    /** 实体名称 */
    private String entityName;

    /** 实体类型 */
    private String entityType;

    /** 实体描述 */
    private String description;

    /** 路径上的关系类型列表 */
    private List<String> pathRelations;

    /** 跳数 */
    private Long hops;
}
