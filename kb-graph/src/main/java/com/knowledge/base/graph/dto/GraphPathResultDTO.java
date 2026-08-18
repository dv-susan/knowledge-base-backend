package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图谱路径查询结果
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphPathResultDTO {

    /** 路径节点列表 */
    private List<NodeInfo> nodes;

    /** 路径关系类型列表 */
    private List<String> relationships;

    /** 跳数 */
    private Long hops;
}
