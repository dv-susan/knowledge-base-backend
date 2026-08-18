package com.knowledge.base.graph.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图谱数据VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图谱数据")
public class GraphDataVO {

    @Schema(description = "节点列表")
    private List<GraphNodeVO> nodes;

    @Schema(description = "边列表")
    private List<GraphEdgeVO> edges;

    @Schema(description = "节点总数")
    private Integer nodeCount;

    @Schema(description = "边总数")
    private Integer edgeCount;
}
