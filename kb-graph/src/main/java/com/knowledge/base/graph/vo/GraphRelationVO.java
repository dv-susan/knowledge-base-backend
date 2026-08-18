package com.knowledge.base.graph.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图谱关系VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图谱关系")
public class GraphRelationVO {

    @Schema(description = "关系ID")
    private String id;

    @Schema(description = "源节点")
    private GraphNodeVO sourceNode;

    @Schema(description = "目标节点")
    private GraphNodeVO targetNode;

    @Schema(description = "关系类型")
    private String relationType;

    @Schema(description = "关系权重")
    private Double weight;
}
