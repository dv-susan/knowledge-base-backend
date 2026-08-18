package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图谱节点概要信息
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeInfo {

    /** 节点名称 */
    private String name;

    /** 节点类型（KnowledgeEntity / KnowledgeDocument / DocumentChunk） */
    private String type;
}
