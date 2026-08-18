package com.knowledge.base.graph.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图谱路径VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图谱路径")
public class GraphPathVO {

    @Schema(description = "路径节点列表")
    private List<GraphNodeVO> nodes;

    @Schema(description = "路径边列表")
    private List<GraphEdgeVO> edges;

    @Schema(description = "路径长度")
    private Integer length;

    @Schema(description = "路径权重")
    private Double weight;
}
