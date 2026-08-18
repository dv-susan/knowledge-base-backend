package com.knowledge.base.graph.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 图谱节点VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图谱节点")
public class GraphNodeVO {

    @Schema(description = "节点ID")
    private String id;

    @Schema(description = "节点名称")
    private String name;

    @Schema(description = "节点类型")
    private String type;

    @Schema(description = "节点标签")
    private String label;

    @Schema(description = "节点大小")
    private Integer size;

    @Schema(description = "节点颜色")
    private String color;

    @Schema(description = "节点属性")
    private Map<String, Object> properties;

    @Schema(description = "关联的文档ID（KnowledgeDocument / DocumentChunk 有值）")
    private String documentId;
}
