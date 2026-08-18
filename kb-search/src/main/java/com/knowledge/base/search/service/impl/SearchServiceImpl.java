package com.knowledge.base.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.search.dto.SearchRequestDTO;
import com.knowledge.base.search.entity.DocumentIndex;
import com.knowledge.base.search.feign.RagSearchFeignClient;
import com.knowledge.base.search.feign.RagSearchItemVO;
import com.knowledge.base.search.feign.RagSearchRequest;
import com.knowledge.base.search.service.SearchService;
import com.knowledge.base.search.vo.SearchResultVO;
import com.knowledge.base.search.vo.SearchSuggestVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 搜索服务实现
 *
 * <p>提供两种搜索模式：</p>
 * <ol>
 *   <li><b>关键词搜索</b>：基于 ES multi_match 的 BM25 全文检索，支持高亮</li>
 *   <li><b>混合智能搜索</b>：通过 Feign 调用 kb-ai 的 BM25 + kNN + RRF 融合检索</li>
 * </ol>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    /** ES 索引名称 */
    private static final String INDEX_NAME = "kb_document";

    // ==================== 依赖注入 ====================

    /** Spring Data ES 操作模板（查询、索引、删除） */
    private final ElasticsearchOperations elasticsearchOperations;

    /** ES 低级客户端（原生查询、索引管理） */
    private final ElasticsearchClient esClient;

    /** kb-ai 混合搜索 Feign 客户端 */
    private final RagSearchFeignClient ragSearchFeignClient;

    /** JSON 序列化/反序列化 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 系统内部调用默认用户ID */
    private static final String SYSTEM_USER_ID = "1";

    /** kb-document 服务地址（用于索引同步） */
    @Value("${kb-document.base-url:http://localhost:8082}")
    private String kbDocumentBaseUrl;

    // ==================== 搜索入口 ====================

    /** {@inheritDoc} */
    @Override
    public PageResult<SearchResultVO> search(SearchRequestDTO dto) {
        //混合智能搜索
        if ("hybrid".equals(dto.getSearchMode())) {
            return searchHybrid(dto);
        }
        //关键词搜索
        return searchKeyword(dto);
    }

    /** {@inheritDoc} */
    @Override
    public PageResult<SearchResultVO> advancedSearch(SearchRequestDTO dto) {
        log.info("高级搜索：keyword={}", dto.getKeyword());
        return search(dto);
    }

    // ==================== 关键词搜索 ====================

    /**
     * 关键词搜索（BM25 全文检索 + ES 高亮）
     *
     * <p>通过 ES 原生客户端构建 multi_match 查询，对 title、summary、content
     * 三个字段做加权匹配，title 权重最高。同时配置 ES highlight 返回高亮片段。</p>
     *
     * @param dto 搜索请求（含关键词、分页参数）
     * @return 分页搜索结果
     */
    private PageResult<SearchResultVO> searchKeyword(SearchRequestDTO dto) {
        log.info("关键词搜索：keyword={}", dto.getKeyword());

        try {
            String keyword = dto.getKeyword();

            // 构建 ES 查询：multi_match 加权 + docStatus 过滤 + highlight
            SearchResponse<Map> response = esClient.search(s -> {
                s.index(INDEX_NAME)
                 .from((dto.getCurrent() - 1) * dto.getSize())
                 .size(dto.getSize());

                // 有关键词时：多字段加权匹配 + 已发布过滤
                if (StringUtils.hasText(keyword)) {
                    s.query(q -> q.bool(b -> b
                            .must(m -> m.multiMatch(mm -> mm
                                    .query(keyword)
                                    .fields("title^2", "summary^1.5", "content")))
                            .filter(f -> f.term(t -> t.field("docStatus").value(1)))));
                } else {
                    // 无关键词时：只过滤已发布
                    s.query(q -> q.bool(b -> b
                            .filter(f -> f.term(t -> t.field("docStatus").value(1)))));
                }

                // 高亮配置：标题和摘要返回完整字段，内容返回 3 个片段
                s.highlight(h -> h
                        .preTags("<em>").postTags("</em>")
                        .fields("title", hf -> hf.numberOfFragments(0))
                        .fields("summary", hf -> hf.numberOfFragments(0))
                        .fields("content", hf -> hf.fragmentSize(150).numberOfFragments(3)));

                return s;
            }, Map.class);

            // 解析搜索结果，提取高亮字段
            List<SearchResultVO> results = new ArrayList<>();
            for (Hit<Map> hit : response.hits().hits()) {
                Map<String, Object> source = hit.source();

                // 从 ES hit 中提取高亮字段
                List<String> titleHighlights = hit.highlight() != null ? hit.highlight().get("title") : null;
                List<String> summaryHighlights = hit.highlight() != null ? hit.highlight().get("summary") : null;
                List<String> contentHighlights = hit.highlight() != null ? hit.highlight().get("content") : null;

                // 标题：优先取高亮版本，合并相邻 em 标签
                String title = (titleHighlights != null && !titleHighlights.isEmpty())
                        ? mergeAdjacentEmTags(titleHighlights.get(0))
                        : getString(source, "title");
                String summary = (summaryHighlights != null && !summaryHighlights.isEmpty())
                        ? mergeAdjacentEmTags(summaryHighlights.get(0))
                        : getString(source, "summary");

                // ES 未返回高亮时，用简单正则高亮兜底
                if (titleHighlights == null && summaryHighlights == null && StringUtils.hasText(keyword)) {
                    title = highlightSimple(title, keyword);
                    summary = highlightSimple(summary, keyword);
                }

                // 内容高亮片段
                List<String> highlights;
                if (contentHighlights != null && !contentHighlights.isEmpty()) {
                    highlights = contentHighlights.stream()
                            .map(this::mergeAdjacentEmTags)
                            .collect(Collectors.toList());
                } else {
                    highlights = new ArrayList<>();
                }

                results.add(SearchResultVO.builder()
                        .id(hit.id() != null ? Long.parseLong(hit.id()) : null)
                        .title(title)
                        .summary(summary)
                        .highlights(highlights)
                        .categoryName(getString(source, "categoryName"))
                        .tagNames(getStringList(source, "tagNames"))
                        .creatorName(getString(source, "creatorName"))
                        .teamName(getString(source, "teamName"))
                        .viewCount(toInt(source != null ? source.get("viewCount") : null))
                        .likeCount(toInt(source != null ? source.get("likeCount") : null))
                        .commentCount(toInt(source != null ? source.get("commentCount") : null))
                        .publishAt(getString(source, "publishAt"))
                        .score(hit.score() != null ? hit.score().floatValue() : 0f)
                        .build());
            }

            return PageResult.<SearchResultVO>builder()
                    .records(results)
                    .total(response.hits().total() != null ? response.hits().total().value() : 0L)
                    .current((long) dto.getCurrent())
                    .size((long) dto.getSize())
                    .build();

        } catch (Exception e) {
            log.error("关键词搜索失败：{}", e.getMessage(), e);
            throw new BusinessException("搜索失败: " + e.getMessage());
        }
    }

    // ==================== 混合智能搜索 ====================

    /**
     * 混合智能搜索（BM25 + kNN 向量 + RRF 融合 + LLM 重排序）
     *
     * <p>通过 Feign 调用 kb-ai 的 /rag/search 接口，获取 chunk 级检索结果后
     * 按 documentId 聚合为文档级结果，并从 kb_document 索引补充元数据。</p>
     *
     * <p>失败时自动回退到关键词搜索。</p>
     *
     * @param dto 搜索请求
     * @return 分页搜索结果（含 chunk 明细）
     */
    private PageResult<SearchResultVO> searchHybrid(SearchRequestDTO dto) {
        log.info("混合智能搜索：keyword={}, topK={}, enableRerank={}",
                dto.getKeyword(), dto.getTopK(), dto.isEnableRerank());

        try {
            // 构建请求 → Feign 调用 kb-ai
            RagSearchRequest request = RagSearchRequest.builder()
                    .query(dto.getKeyword())
                    .topK(dto.getTopK() > 0 ? dto.getTopK() : 10)
                    .enableRerank(dto.isEnableRerank())
                    .build();

            Result<List<RagSearchItemVO>> result = ragSearchFeignClient.search(request);
            log.debug("kb-ai 混合搜索响应：code={}, items={}",
                    result.getCode(), result.getData() != null ? result.getData().size() : 0);

            List<RagSearchItemVO> dataList = result.getData();
            if (dataList == null || dataList.isEmpty()) {
                return PageResult.<SearchResultVO>builder()
                        .records(new ArrayList<>())
                        .total(0L)
                        .current((long) dto.getCurrent())
                        .size((long) dto.getSize())
                        .build();
            }

            // 按 documentId 聚合 chunk 为文档级结果
            Map<String, SearchResultVO> docMap = new LinkedHashMap<>();
            List<SearchResultVO> standaloneResults = new ArrayList<>();

            for (RagSearchItemVO item : dataList) {
                Long docId = item.getDocumentId();

                // 构建 chunk 结果
                SearchResultVO.ChunkResult chunk = SearchResultVO.ChunkResult.builder()
                        .chunkId(item.getChunkId())
                        .content(item.getContent())
                        .heading(item.getHeading())
                        .score(item.getScore())
                        .bm25Score(item.getBm25Score())
                        .vectorScore(item.getVectorScore())
                        .build();

                if (docId == null) {
                    // 无 documentId：每个 chunk 独立展示
                    String title = item.getDocumentTitle();
                    if (title == null || title.isEmpty()) {
                        title = item.getHeading();
                    }
                    SearchResultVO vo = SearchResultVO.builder()
                            .title(title != null ? title : "未命名文档")
                            .summary(chunk.getContent().length() > 200
                                    ? chunk.getContent().substring(0, 200) + "..." : chunk.getContent())
                            .score((float) chunk.getScore())
                            .bm25Score(chunk.getBm25Score())
                            .vectorScore(chunk.getVectorScore())
                            .chunks(new ArrayList<>(List.of(chunk)))
                            .build();
                    standaloneResults.add(vo);
                    continue;
                }

                // 有 documentId：按 docId 聚合 chunk
                String docIdStr = String.valueOf(docId);
                if (docMap.containsKey(docIdStr)) {
                    docMap.get(docIdStr).getChunks().add(chunk);
                } else {
                    String publishTime = formatPublishTime(item.getPublishTime());
                    SearchResultVO vo = SearchResultVO.builder()
                            .id(docId.longValue())
                            .title(item.getDocumentTitle())
                            .summary(chunk.getContent().length() > 200
                                    ? chunk.getContent().substring(0, 200) + "..." : chunk.getContent())
                            .publishAt(publishTime)
                            .score((float) chunk.getScore())
                            .bm25Score(chunk.getBm25Score())
                            .vectorScore(chunk.getVectorScore())
                            .chunks(new ArrayList<>(List.of(chunk)))
                            .build();
                    docMap.put(docIdStr, vo);
                }
            }

            List<SearchResultVO> records = new ArrayList<>(docMap.values());
            records.addAll(standaloneResults);

            // 对混合搜索结果做关键字高亮（kb-ai 返回的文本无高亮标记）
            if (StringUtils.hasText(dto.getKeyword())) {
                for (SearchResultVO vo : records) {
                    vo.setTitle(highlightSimple(vo.getTitle(), dto.getKeyword()));
                    vo.setSummary(highlightSimple(vo.getSummary(), dto.getKeyword()));
                    if (vo.getChunks() != null) {
                        for (SearchResultVO.ChunkResult chunk : vo.getChunks()) {
                            chunk.setContent(highlightSimple(chunk.getContent(), dto.getKeyword()));
                        }
                    }
                }
            }

            // 从 kb_document ES 索引补充文档元数据（分类、作者、浏览数等）
            enrichDocumentMetadata(records);

            log.info("混合智能搜索完成：{} 个文档（{} 聚合, {} 独立）, {} chunks",
                    records.size(), docMap.size(), standaloneResults.size(), dataList.size());

            return PageResult.<SearchResultVO>builder()
                    .records(records)
                    .total((long) records.size())
                    .current((long) dto.getCurrent())
                    .size((long) dto.getSize())
                    .build();

        } catch (Exception e) {
            log.error("混合智能搜索失败，回退到关键词搜索：{}", e.getMessage(), e);
            return searchKeyword(dto);
        }
    }

    /**
     * 从 kb_document ES 索引批量补充文档元数据
     *
     * <p>混合搜索结果中 kb-ai 只返回 chunk 级字段（标题、内容、得分），
     * 缺少分类、作者、浏览数等元数据。此方法通过文档 ID 批量查询 ES 索引补齐。</p>
     *
     * @param records 待补充的搜索结果列表
     */
    private void enrichDocumentMetadata(List<SearchResultVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        // 收集所有有 ID 的文档
        List<String> ids = records.stream()
                .filter(r -> r.getId() != null)
                .map(r -> String.valueOf(r.getId()))
                .distinct()
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return;
        }

        try {
            // 用 terms 查询批量获取文档元数据
            String idsJson = ids.stream()
                    .map(id -> "\"" + id + "\"")
                    .collect(Collectors.joining(","));
            String queryJson = "{\"query\":{\"terms\":{\"_id\":[" + idsJson + "]}},\"size\":" + ids.size() + "}";

            Query searchQuery = new StringQuery(queryJson);
            SearchHits<DocumentIndex> searchHits = elasticsearchOperations.search(searchQuery, DocumentIndex.class);

            Map<String, DocumentIndex> docIndexMap = searchHits.getSearchHits().stream()
                    .collect(Collectors.toMap(
                            SearchHit::getId,
                            SearchHit::getContent,
                            (a, b) -> a));

            // 逐个填充缺失字段
            for (SearchResultVO vo : records) {
                if (vo.getId() == null) continue;
                DocumentIndex doc = docIndexMap.get(String.valueOf(vo.getId()));
                if (doc == null) continue;

                if (vo.getSummary() == null || vo.getSummary().isEmpty()) {
                    vo.setSummary(doc.getSummary());
                }
                if (vo.getPublishAt() == null && doc.getPublishAt() != null) {
                    vo.setPublishAt(doc.getPublishAt());
                }
                if (vo.getCategoryName() == null) {
                    vo.setCategoryName(doc.getCategoryName());
                }
                if (vo.getCreatorName() == null) {
                    vo.setCreatorName(doc.getCreatorName());
                }
                if (vo.getViewCount() == null) {
                    vo.setViewCount(doc.getViewCount());
                }
                if (vo.getLikeCount() == null) {
                    vo.setLikeCount(doc.getLikeCount());
                }
                if (vo.getCommentCount() == null) {
                    vo.setCommentCount(doc.getCommentCount());
                }
                if (vo.getTagNames() == null) {
                    vo.setTagNames(doc.getTagNames());
                }
            }
        } catch (Exception e) {
            log.warn("补充文档元数据失败：{}", e.getMessage());
        }
    }

    // ==================== 搜索建议 ====================

    /** {@inheritDoc} */
    @Override
    public List<SearchSuggestVO> suggest(String keyword, Integer size) {
        log.info("搜索建议：keyword={}, size={}", keyword, size);

        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        if (size == null || size <= 0) {
            size = 10;
        }

        try {
            // 用 prefix 查询匹配标题前缀
            String queryJson = objectMapper.writeValueAsString(
                    Map.of("prefix", Map.of("title.keyword", keyword)));
            Query searchQuery = new StringQuery(queryJson);
            searchQuery.setPageable(PageRequest.of(0, size));

            SearchHits<DocumentIndex> searchHits = elasticsearchOperations.search(searchQuery, DocumentIndex.class);

            return searchHits.getSearchHits().stream()
                    .map(hit -> SearchSuggestVO.builder()
                            .text(hit.getContent().getTitle())
                            .type("title")
                            .documentId(Long.parseLong(hit.getId()))
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("搜索建议失败：{}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // ==================== 索引管理 ====================

    /** {@inheritDoc} */
    @Override
    public void indexDocument(Long documentId) {
        if (documentId == null) {
            return;
        }
        log.info("索引文档：documentId={}", documentId);

        try {
            // 通过 REST 调用 kb-document 获取文档详情
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", SYSTEM_USER_ID);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = kbDocumentBaseUrl + "/documents/" + documentId;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            String body = response.getBody();
            if (body == null) {
                log.warn("kb-document 返回空响应：documentId={}", documentId);
                return;
            }

            Map<String, Object> respMap = objectMapper.readValue(body,
                    new TypeReference<Map<String, Object>>() {});
            @SuppressWarnings("unchecked")
            Map<String, Object> docData = (Map<String, Object>) respMap.get("data");
            if (docData == null) {
                log.warn("文档数据为空：documentId={}", documentId);
                return;
            }

            // 写入 ES
            DocumentIndex docIndex = buildDocumentIndexFromMap(docData);
            elasticsearchOperations.save(docIndex);
            log.info("文档索引成功：documentId={}, title={}", documentId, docIndex.getTitle());

        } catch (Exception e) {
            log.error("索引文档失败：documentId={}, error={}", documentId, e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void indexDocumentData(Map<String, Object> docData) {
        try {
            DocumentIndex docIndex = buildDocumentIndexFromMap(docData);
            elasticsearchOperations.save(docIndex);
            log.info("文档数据索引成功：id={}, title={}", docIndex.getId(), docIndex.getTitle());
        } catch (Exception e) {
            log.error("文档数据索引失败：{}", e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void batchIndexDocuments(List<Long> documentIds) {
        log.info("批量索引文档：documentCount={}", documentIds != null ? documentIds.size() : 0);

        if (documentIds == null || documentIds.isEmpty()) {
            return;
        }

        for (Long documentId : documentIds) {
            try {
                indexDocument(documentId);
            } catch (Exception e) {
                log.error("批量索引文档失败：documentId={}, error={}", documentId, e.getMessage(), e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteDocument(Long documentId) {
        log.info("删除文档索引：documentId={}", documentId);

        if (documentId == null) {
            return;
        }

        try {
            elasticsearchOperations.delete(String.valueOf(documentId), DocumentIndex.class);
            log.info("文档索引删除成功：documentId={}", documentId);
        } catch (Exception e) {
            log.error("删除文档索引失败：documentId={}, error={}", documentId, e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void batchDeleteDocuments(List<Long> documentIds) {
        log.info("批量删除文档索引：documentCount={}", documentIds != null ? documentIds.size() : 0);

        if (documentIds == null || documentIds.isEmpty()) {
            return;
        }

        try {
            for (Long documentId : documentIds) {
                elasticsearchOperations.delete(String.valueOf(documentId), DocumentIndex.class);
            }
            log.info("批量删除文档索引成功：documentCount={}", documentIds.size());
        } catch (Exception e) {
            log.error("批量删除文档索引失败：error={}", e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        log.info("重建索引开始：从 kb-document 同步所有已发布文档");

        try {
            // 删除旧索引
            if (esClient.indices().exists(ExistsRequest.of(e -> e.index(INDEX_NAME))).value()) {
                esClient.indices().delete(DeleteIndexRequest.of(d -> d.index(INDEX_NAME)));
                log.info("旧索引删除成功");
            }

            // 创建新索引（动态映射，字符串字段使用 keyword 避免日期解析问题）
            CreateIndexRequest createRequest = CreateIndexRequest.of(c -> c
                    .index(INDEX_NAME)
                    .settings(s -> s
                            .numberOfShards("1")
                            .numberOfReplicas("0")
                            .refreshInterval(ri -> ri.time("5s")))
                    .mappings(m -> m
                            .properties("title", p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k))))
                            .properties("summary", p -> p.text(t -> t))
                            .properties("content", p -> p.text(t -> t))
                            .properties("categoryId", p -> p.long_(l -> l))
                            .properties("categoryName", p -> p.keyword(k -> k))
                            .properties("tagIds", p -> p.long_(l -> l))
                            .properties("tagNames", p -> p.keyword(k -> k))
                            .properties("creatorId", p -> p.long_(l -> l))
                            .properties("creatorName", p -> p.keyword(k -> k))
                            .properties("teamId", p -> p.long_(l -> l))
                            .properties("teamName", p -> p.keyword(k -> k))
                            .properties("docStatus", p -> p.integer(i -> i))
                            .properties("viewCount", p -> p.integer(i -> i))
                            .properties("likeCount", p -> p.integer(i -> i))
                            .properties("commentCount", p -> p.integer(i -> i))
                            .properties("isPublic", p -> p.boolean_(b -> b))
                            .properties("publishAt", p -> p.keyword(k -> k))
                            .properties("createdAt", p -> p.keyword(k -> k))
                            .properties("updatedAt", p -> p.keyword(k -> k))));
            esClient.indices().create(createRequest);
            log.info("新索引创建成功：index={}", INDEX_NAME);

            // 分页获取 kb-document 的所有已发布文档并索引
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", SYSTEM_USER_ID);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            long current = 1;
            long pageSize = 50;
            long totalIndexed = 0;
            boolean hasMore = true;

            while (hasMore) {
                // status=1 表示已发布
                String pageUrl = kbDocumentBaseUrl + "/documents/page?current=" + current
                        + "&size=" + pageSize + "&status=1";
                ResponseEntity<String> response = restTemplate.exchange(pageUrl, HttpMethod.GET, entity, String.class);

                String body = response.getBody();
                if (body == null) break;

                Map<String, Object> respMap = objectMapper.readValue(body,
                        new TypeReference<Map<String, Object>>() {});
                @SuppressWarnings("unchecked")
                Map<String, Object> pageData = (Map<String, Object>) respMap.get("data");
                if (pageData == null) break;

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> records = (List<Map<String, Object>>) pageData.get("records");
                Long total = toLong(pageData.get("total"));

                if (records == null || records.isEmpty()) break;

                for (Map<String, Object> docData : records) {
                    try {
                        DocumentIndex docIndex = buildDocumentIndexFromMap(docData);
                        elasticsearchOperations.save(docIndex);
                        totalIndexed++;
                    } catch (Exception e) {
                        Long failedId = toLong(docData.get("id"));
                        log.error("索引文档失败：documentId={}, error={}", failedId, e.getMessage());
                    }
                }

                log.info("重建索引进度：已索引 {} 条，总 {} 条", totalIndexed, total);
                hasMore = records.size() >= pageSize;
                current++;
            }

            log.info("索引重建完成：共计 {} 条文档", totalIndexed);

        } catch (Exception e) {
            log.error("重建索引失败：{}", e.getMessage(), e);
            throw new BusinessException("重建索引失败: " + e.getMessage());
        }
    }

    // ==================== 文档索引对象构建 ====================

    /**
     * 从 kb-document API 响应数据构建 ES 文档索引对象
     *
     * @param docData API 返回的文档数据 Map
     * @return ES 索引实体
     */
    private DocumentIndex buildDocumentIndexFromMap(Map<String, Object> docData) {
        Long docId = toLong(docData.get("id"));

        return DocumentIndex.builder()
                .id(docId != null ? String.valueOf(docId) : null)
                .title((String) docData.get("title"))
                .summary((String) docData.get("summary"))
                .content((String) docData.get("content"))
                .categoryId(toLong(docData.get("categoryId")))
                .categoryName((String) docData.get("categoryName"))
                .tagIds(null)
                .tagNames(parseTagNames(docData))
                .creatorId(toCreatorId(docData))
                .creatorName(toCreatorName(docData))
                .teamId(null)
                .teamName(null)
                .docStatus(toInt(docData.get("status")))
                .viewCount(toInt(docData.get("viewCount")))
                .likeCount(toInt(docData.get("likeCount")))
                .commentCount(toInt(docData.get("commentCount")))
                .isPublic(toInt(docData.get("isPublic")) == 1)
                .publishAt(formatPublishTime(getString(docData, "publishTime")))
                .createdAt(formatPublishTime(getString(docData, "createdAt")))
                .updatedAt(formatPublishTime(getString(docData, "updatedAt")))
                .build();
    }

    /**
     * 解析标签名称列表（支持逗号分隔字符串或 JSON 数组）
     */
    @SuppressWarnings("unchecked")
    private List<String> parseTagNames(Map<String, Object> docData) {
        Object tags = docData.get("tags");
        if (tags instanceof String tagStr && StringUtils.hasText(tagStr)) {
            return List.of(tagStr.split(","));
        }
        if (tags instanceof List) {
            return (List<String>) tags;
        }
        return List.of();
    }

    /**
     * 获取创建者 ID（优先从 author.id 获取）
     */
    private Long toCreatorId(Map<String, Object> docData) {
        Object author = docData.get("author");
        if (author instanceof Map) {
            return toLong(((Map<?, ?>) author).get("id"));
        }
        return toLong(docData.get("authorId"));
    }

    /**
     * 获取创建者名称（优先从 author.username 获取）
     */
    private String toCreatorName(Map<String, Object> docData) {
        Object author = docData.get("author");
        if (author instanceof Map) {
            Object username = ((Map<?, ?>) author).get("username");
            return username != null ? username.toString() : null;
        }
        return (String) docData.get("authorName");
    }

    // ==================== 高亮处理 ====================

    /**
     * 简单关键字高亮：用 {@code <em>} 标签包裹匹配文本（大小写不敏感）
     *
     * @param text    原始文本
     * @param keyword 搜索关键字
     * @return 高亮后的 HTML 文本
     */
    private String highlightSimple(String text, String keyword) {
        if (text == null || keyword == null || keyword.isEmpty()) {
            return text;
        }
        String escaped = Pattern.quote(keyword);
        return text.replaceAll("(?i)" + escaped, "<em>$0</em>");
    }

    /**
     * 合并相邻的 em 标签，解决 IK 分词器 token 碎片问题
     *
     * <p>例如：{@code <em>索</em><em>引</em>} → {@code <em>索引</em>}</p>
     */
    private String mergeAdjacentEmTags(String text) {
        if (text == null) return null;
        return text.replace("</em><em>", "");
    }

    // ==================== 时间格式化 ====================

    /**
     * 格式化发布时间：{@code "2026-05-28T15:49:51"} → {@code "2026-05-28 15:49:51"}
     */
    private String formatPublishTime(String time) {
        if (time == null) return null;
        String cleaned = time.contains(".") ? time.substring(0, time.indexOf('.')) : time;
        return cleaned.replace('T', ' ');
    }

    // ==================== 类型转换工具方法 ====================

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> source, String key) {
        if (source == null) return null;
        Object val = source.get(key);
        if (val instanceof List) {
            return (List<String>) val;
        }
        return null;
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer toInt(Object value) {
        if (value instanceof Number num) {
            return num.intValue();
        }
        if (value instanceof String str) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private double toDouble(Object value) {
        if (value instanceof Number num) {
            return num.doubleValue();
        }
        return 0.0;
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number num) {
            return num.longValue();
        }
        return Long.parseLong(value.toString());
    }
}
