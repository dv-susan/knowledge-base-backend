package com.knowledge.base.graph.repository;

/**
 * 实体类型统计（用于 KnowledgeEntityRepository.countByType 返回）
 */
public interface EntityTypeStat {

    String getType();

    Long getCount();
}
