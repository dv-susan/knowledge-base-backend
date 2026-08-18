package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分块-实体映射参数
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkEntityMappingDTO {

    /** 分块ID */
    private String chunkId;

    /** 实体名称 */
    private String entityName;

    /** 置信度 */
    private Double confidence;
}
