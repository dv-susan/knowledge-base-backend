package com.knowledge.base.ai.rag.kag.extraction;

import com.knowledge.base.ai.config.KAGProperties;
import com.knowledge.base.ai.dto.kag.extraction.ExtractedEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体标准化器
 *
 * <p>负责：
 * 1. 检查抽取的实体是否已存在于Neo4j图谱中
 * 2. 合并同名或高度相似的实体
 * 3. 返回去重合并后的最终实体列表</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityNormalizer {

    private final Neo4jClient neo4jClient;
    private final KAGProperties kagProperties;

    /**
     * 标准化实体列表（去重、合并、冲突解决）
     *
     * @param entities 原始抽取实体列表
     * @return 标准化后的实体列表
     */
    public List<ExtractedEntity> normalize(List<ExtractedEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        // Step 1: 合并列表内的重复实体（同名实体）
        entities = mergeDuplicateNames(entities);

        // Step 2: 检查Neo4j中已存在的实体
        entities = resolveWithExisting(entities);

        // Step 3: 内部相似度合并（同一批次中高度相似的实体）
        entities = mergeBySimilarity(entities);

        return entities;
    }

    /**
     * 合并同名实体（同一批次内）
     */
    private List<ExtractedEntity> mergeDuplicateNames(List<ExtractedEntity> entities) {
        Map<String, ExtractedEntity> merged = new LinkedHashMap<>();

        for (ExtractedEntity entity : entities) {
            String key = entity.getName().toLowerCase().trim();
            if (merged.containsKey(key)) {
                ExtractedEntity existing = merged.get(key);
                // 合并别名
                Set<String> allAliases = new LinkedHashSet<>();
                if (existing.getAliases() != null) allAliases.addAll(existing.getAliases());
                if (entity.getAliases() != null) allAliases.addAll(entity.getAliases());
                existing.setAliases(new ArrayList<>(allAliases));
                // 取更长的描述
                if (entity.getDescription() != null && entity.getDescription().length() >
                        (existing.getDescription() != null ? existing.getDescription().length() : 0)) {
                    existing.setDescription(entity.getDescription());
                }
                // 保留最多出现次数的类型
                Double entityConf = entity.getConfidence() != null ? entity.getConfidence() : 0.0;
                Double existingConf = existing.getConfidence() != null ? existing.getConfidence() : 0.0;
                if (entityConf > existingConf) {
                    existing.setType(entity.getType());
                }
            } else {
                merged.put(key, entity);
            }
        }
        return new ArrayList<>(merged.values());
    }

    /**
     * 与Neo4j中已存在的实体进行冲突解决
     */
    private List<ExtractedEntity> resolveWithExisting(List<ExtractedEntity> entities) {
        if (entities.isEmpty()) return entities;

        // 收集所有实体名用于批量查询
        List<String> names = entities.stream()
                .map(ExtractedEntity::getName)
                .collect(Collectors.toList());

        // 批量查询Neo4j中已存在的实体
        Set<String> existingNames = queryExistingEntityNames(names);
        log.debug("Found {} existing entities out of {} candidates", existingNames.size(), names.size());

        // 对于已存在的实体：标记为已存在（保留新别名）
        for (ExtractedEntity entity : entities) {
            // 确保 confidence 不为 null
            if (entity.getConfidence() == null) {
                entity.setConfidence(0.8);
            }
            if (existingNames.contains(entity.getName())) {
                entity.setConfidence(Math.min(entity.getConfidence(), 0.95)); // 降低置信度
            }
            // 同时检查别名是否匹配到已有实体
            if (entity.getAliases() != null) {
                for (String alias : entity.getAliases()) {
                    if (existingNames.contains(alias)) {
                        // 别名已作为独立实体存在，将该别名加入已有实体的别名列表
                        log.debug("Alias '{}' of entity '{}' exists as independent entity", alias, entity.getName());
                    }
                }
            }
        }

        return entities;
    }

    /**
     * 基于名称相似度合并实体
     */
    private List<ExtractedEntity> mergeBySimilarity(List<ExtractedEntity> entities) {
        if (entities.size() <= 1) return entities;

        double threshold = kagProperties.getExtraction().getSimilarityThreshold();
        List<ExtractedEntity> result = new ArrayList<>();
        boolean[] merged = new boolean[entities.size()];

        for (int i = 0; i < entities.size(); i++) {
            if (merged[i]) continue;
            ExtractedEntity base = entities.get(i);

            for (int j = i + 1; j < entities.size(); j++) {
                if (merged[j]) continue;
                ExtractedEntity other = entities.get(j);

                double similarity = computeNameSimilarity(base.getName(), other.getName());
                if (similarity >= threshold) {
                    // 合并 base 和 other
                    if (base.getAliases() == null) base.setAliases(new ArrayList<>());
                    if (!base.getAliases().contains(other.getName())) {
                        base.getAliases().add(other.getName());
                    }
                    if (other.getAliases() != null) {
                        base.getAliases().addAll(other.getAliases());
                    }
                    merged[j] = true;
                    log.debug("Merged similar entities: '{}' ← '{}' (similarity={})",
                            base.getName(), other.getName(), String.format("%.2f", similarity));
                }
            }
            result.add(base);
        }
        return result;
    }

    /**
     * 批量查询Neo4j中已存在的实体名称集合
     */
    private Set<String> queryExistingEntityNames(List<String> names) {
        if (names.isEmpty()) return Collections.emptySet();

        try {
            var result = neo4jClient.query("""
                    UNWIND $names AS name
                    MATCH (e:KnowledgeEntity)
                    WHERE e.name = name OR name IN coalesce(e.aliases, [])
                    RETURN DISTINCT e.name AS existingName
                    """)
                    .bind(names).to("names")
                    .fetch()
                    .all();

            return result.stream()
                    .map(r -> (String) r.get("existingName"))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("Failed to query existing entities from Neo4j: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * 计算两个实体名称的简单相似度（基于编辑距离归一化）
     */
    private double computeNameSimilarity(String a, String b) {
        if (a == null || b == null) return 0.0;
        String sa = a.toLowerCase().trim();
        String sb = b.toLowerCase().trim();
        if (sa.equals(sb)) return 1.0;
        if (sa.contains(sb) || sb.contains(sa)) return 0.9;

        int maxLen = Math.max(sa.length(), sb.length());
        if (maxLen == 0) return 1.0;

        int distance = levenshteinDistance(sa, sb);
        return 1.0 - (double) distance / maxLen;
    }

    /**
     * Levenshtein编辑距离
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
