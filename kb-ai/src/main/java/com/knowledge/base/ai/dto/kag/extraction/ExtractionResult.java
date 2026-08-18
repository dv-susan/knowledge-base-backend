package com.knowledge.base.ai.dto.kag.extraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 抽取结果（一个文档分块的完整抽取结果）
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 来源分块ID */
    private String chunkId;

    /** 来源文档ID */
    private Long docId;

    /** 抽取的实体列表 */
    private List<ExtractedEntity> entities;

    /** 抽取的关系列表 */
    private List<ExtractedRelation> relations;

    /**
     * 是否为空结果（没有抽取到任何实体）
     */
    public boolean isEmpty() {
        return (entities == null || entities.isEmpty()) && (relations == null || relations.isEmpty());
    }
}
