package com.knowledge.base.graph.repository;

/**
 * 实体-分块映射（用于 KnowledgeEntityRepository.findMentioningChunks 返回）
 */
public interface EntityChunkMapping {

    String getContent();

    String getHeading();

    String getDocTitle();

    Long getDocId();

    String getEntityName();
}
