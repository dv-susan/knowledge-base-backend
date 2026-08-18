package com.knowledge.base.graph.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图谱边VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图谱边")
public class GraphEdgeVO {

    @Schema(description = "边ID")
    private String id;

    @Schema(description = "源节点ID")
    private String source;

    @Schema(description = "目标节点ID")
    private String target;

    @Schema(description = "关系类型")
    private String relation;

    @Schema(description = "边标签")
    private String label;

    @Schema(description = "边权重")
    private Double weight;
}
