package com.knowledge.base.ai.rag.service.impl;

import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.config.RagProperties;
import com.knowledge.base.ai.rag.service.EmbeddingService;
import com.knowledge.base.ai.rag.service.RagRetrievalService;
import com.knowledge.base.ai.rag.service.VectorIndexService;
import com.knowledge.base.ai.vo.RagSearchResultVO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RAG检索服务实现
 *
 * <p>编排完整的检索流水线：
 * Query Embedding → Hybrid Search (BM25 + kNN + RRF) → LLM Reranking</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagRetrievalServiceImpl implements RagRetrievalService {

    private final EmbeddingService embeddingService;
    private final VectorIndexService vectorIndexService;
    private final ModelProvider modelProvider;
    private final RagProperties ragProperties;

    private static final Pattern RERANK_SCORE_PATTERN = Pattern.compile("\\b([1-9]|10)\\b");
    private static final int PER_CHUNK_RERANK_TIMEOUT = 10;

    /** {@inheritDoc} */
    @Override
    public List<RagSearchResultVO> retrieve(String query, int topK, boolean enableRerank) {
        // 1. 创建索引（如果不存在）
        vectorIndexService.createIndexIfNotExists();

        // 2. 生成查询向量（失败时降级为BM25-only搜索）
        float[] queryEmbedding = null;
        try {
            queryEmbedding = embeddingService.embed(query);
        } catch (Exception e) {
            log.warn("查询嵌入失败，降级为BM25-only搜索：{}", e.getMessage());
        }

        // 3. 混合搜索（BM25 + kNN + RRF融合），取2倍topK作为候选
        int hybridTopK = ragProperties.getRetrieval().getHybridTopK();
        int rrfC = ragProperties.getRetrieval().getRrfC();
        int candidateK = Math.max(topK * 2, hybridTopK);

        List<RagSearchResultVO> candidates = vectorIndexService.searchHybrid(
                query, queryEmbedding, candidateK, hybridTopK, rrfC);

        if (candidates.isEmpty()) {
            log.info("RAG检索无结果：query={}", query);
            return List.of();
        }

        // 4. LLM重排序
        if (enableRerank && ragProperties.getRerank().isEnabled() && candidates.size() > topK) {
            candidates = rerank(candidates, query, topK);
        } else if (candidates.size() > topK) {
            candidates = candidates.subList(0, topK);
        }

        log.info("RAG检索完成：query={}, results={}", query, candidates.size());
        return candidates;
    }

    /**
     * LLM重排序
     *
     * <p>对每个候选块调用LLM打分（1-10），按得分降序排列。</p>
     */
    private List<RagSearchResultVO> rerank(List<RagSearchResultVO> candidates, String query, int topK) {
        try {
            ChatLanguageModel model = modelProvider.getDefaultModel();

            List<ScoredChunk> scored = new ArrayList<>();
            for (RagSearchResultVO candidate : candidates) {
                String prompt = buildRerankPrompt(query, candidate.getContent());
                try {
                    String response = model.generate(UserMessage.from(prompt)).content().text();
                    int score = parseRelevanceScore(response);
                    scored.add(new ScoredChunk(candidate, score));
                } catch (Exception e) {
                    log.warn("重排序评分失败：chunkId={}, error={}", candidate.getChunkId(), e.getMessage());
                    // 失败时保留原RRF得分
                    scored.add(new ScoredChunk(candidate, (int) (candidate.getScore() * 10)));
                }
            }

            scored.sort(Comparator.comparingInt(ScoredChunk::score).reversed());
            return scored.stream().limit(topK).map(sc -> {
                sc.result.setScore(sc.score);
                return sc.result;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("LLM重排序失败，降级为RRF排序：{}", e.getMessage());
            return candidates.stream().limit(topK).collect(Collectors.toList());
        }
    }

    private String buildRerankPrompt(String query, String chunkContent) {
        return String.format("""
                你是一个搜索相关性评估专家。
                请根据以下"用户查询"和"文档片段"，评估该文档片段对回答用户查询的相关程度。

                用户查询：%s

                文档片段：
                %s

                请仅返回一个整数评分（1-10分），不要返回其他内容。
                10 - 直接完美回答 | 7-9 - 高度相关 | 4-6 - 部分相关 | 1-3 - 基本无关
                """, query, truncateForRerank(chunkContent));
    }

    private String truncateForRerank(String content) {
        int maxLen = 1000;
        if (content == null) return "";
        return content.length() > maxLen ? content.substring(0, maxLen) : content;
    }

    private int parseRelevanceScore(String scoreText) {
        if (scoreText == null) return 5;
        Matcher m = RERANK_SCORE_PATTERN.matcher(scoreText.trim());
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 5; // 默认中等相关
    }

    private record ScoredChunk(RagSearchResultVO result, int score) {}
}
