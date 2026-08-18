package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图谱统计信息
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphStatsDTO {

    /** 节点总数 */
    private Long nodeCount;

    /** 关系总数 */
    private Long edgeCount;

    /** 按类型统计的实体数 */
    private List<EntityTypeCountDTO> entityTypeStats;

    /** 文档节点数 */
    private Long documentCount;

    /** 分块节点数 */
    private Long chunkCount;

    /**
     * 实体类型-数量统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityTypeCountDTO {

        private String type;

        private Long count;
    }
}
