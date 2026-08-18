package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 子图搜索结果
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubgraphResultDTO {

    /** 子图节点列表 */
    private List<NodeInfo> nodes;

    /** 子图关系列表 */
    private List<String> relationships;
}
