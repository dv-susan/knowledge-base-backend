package com.knowledge.base.graph.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图谱社区VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图谱社区")
public class GraphCommunityVO {

    @Schema(description = "社区ID")
    private String id;

    @Schema(description = "社区名称")
    private String name;

    @Schema(description = "社区成员数量")
    private Integer memberCount;

    @Schema(description = "社区成员列表")
    private List<GraphNodeVO> members;

    @Schema(description = "社区密度")
    private Double density;
}
