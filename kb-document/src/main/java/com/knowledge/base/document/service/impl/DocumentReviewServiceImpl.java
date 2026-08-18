package com.knowledge.base.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.StrUtil;
import com.knowledge.base.common.enums.DocumentStatus;
import com.knowledge.base.common.event.ReviewEventDTO;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.DocumentReviewDTO;
import com.knowledge.base.document.dto.ReviewQueryDTO;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.entity.DocumentReview;
import com.knowledge.base.document.feign.KAGFeignClient;
import com.knowledge.base.document.feign.RagFeignClient;
import com.knowledge.base.document.feign.SearchIndexFeignClient;
import com.knowledge.base.document.mapper.DocumentMapper;
import com.knowledge.base.document.mapper.DocumentReviewMapper;
import com.knowledge.base.document.service.DocumentReviewService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.DocumentReviewVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.knowledge.base.common.config.SystemConfigCache;
import com.knowledge.base.common.config.InstanceIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 文档审核Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现文档审核相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class DocumentReviewServiceImpl extends ServiceImpl<DocumentReviewMapper, DocumentReview> implements DocumentReviewService {

    @Resource
    private DocumentReviewMapper documentReviewMapper;

    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private SystemConfigCache systemConfigCache;

    @Resource
    private RagFeignClient ragFeignClient;

    @Resource
    private KAGFeignClient kagFeignClient;

    @Resource
    private SearchIndexFeignClient searchIndexFeignClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Resource
    private InstanceIdentifier instanceIdentifier;

    private static final String REVIEW_EXCHANGE = "kb.notification.exchange";

    /** 构建实例隔离的审核通知路由键 */
    private String reviewRoutingKey(String eventType) {
        return "notification.review." + instanceIdentifier.getId() + "." + eventType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitForReview(Long documentId) {
        log.info("提交文档审核：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 检查系统配置：如果关闭了文档审核，直接发布文档
        if (!checkRequireApproval()) {
            log.info("系统配置关闭了文档审核，直接发布文档：documentId={}", documentId);
            return directPublishDocument(document);
        }

        // 检查文档状态：草稿或已发布状态的文档可以提交审核（已发布的文档编辑后需要重新审核）
        if (!document.getStatus().equals(DocumentStatus.DRAFT.getCode())
                && !document.getStatus().equals(DocumentStatus.PUBLISHED.getCode())) {
            throw new BusinessException("只有草稿或已发布状态的文档才能提交审核");
        }

        // 获取当前轮次
        Integer currentRound = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(review_round), 0) FROM tb_document_review WHERE document_id = ?",
                Integer.class,
                documentId
        );

        // 创建审核记录，审核人尚未分配，reviewer_id 为空
        DocumentReview review = new DocumentReview();
        review.setId(SnowflakeIdGenerator.getInstance().nextId());
        review.setDocumentId(documentId);
        review.setReviewerId(null);
        review.setReviewerName(null);
        review.setReviewResult(null);
        review.setReviewComment(null);
        review.setBeforeStatus(document.getStatus());
        review.setReviewedAt(null);
        review.setReviewRound((currentRound != null ? currentRound : 0) + 1);
        review.setReviewLevel(1);
        review.setCreatedAt(LocalDateTime.now());

        int count = documentReviewMapper.insert(review);
        if (count <= 0) {
            throw new BusinessException("提交审核失败");
        }

        // 更新文档状态为待审核
        document.setStatus(DocumentStatus.PENDING_REVIEW.getCode());
        documentMapper.updateById(document);

        // 发布审核提交事件 → RabbitMQ，通知审核员
        try {
            ReviewEventDTO event = ReviewEventDTO.builder()
                    .eventType("SUBMITTED")
                    .documentId(documentId)
                    .documentTitle(document.getTitle())
                    .authorId(document.getAuthorId())
                    .authorName(document.getAuthorName())
                    .reviewRound(review.getReviewRound())
                    .reviewLevel(review.getReviewLevel())
                    .timestamp(LocalDateTime.now())
                    .build();
            String rk = reviewRoutingKey("submitted");
            CorrelationData correlationData = new CorrelationData(
                    "review-SUBMITTED-" + documentId + "-" + UUID.randomUUID().toString().substring(0, 8));
            rabbitTemplate.convertAndSend(REVIEW_EXCHANGE, rk, event, correlationData);
            log.info("已发布审核提交事件：documentId={}, title={}, authorId={}, exchange={}, routingKey={}",
                    documentId, document.getTitle(), document.getAuthorId(), REVIEW_EXCHANGE, rk);
        } catch (Exception e) {
            log.warn("发布审核提交事件失败（不影响审核流程）：documentId={}, error={}",
                    documentId, e.getMessage());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveReview(DocumentReviewDTO dto) {
        log.info("审核通过：reviewId={}", dto.getReviewId());

        if (dto.getReviewId() == null) {
            throw new BusinessException("审核记录ID不能为空");
        }

        // 检查审核记录是否存在
        DocumentReview review = documentReviewMapper.selectById(dto.getReviewId());
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }

        if (review.getReviewResult() != null) {
            throw new BusinessException("该记录已审核");
        }

        // 从上下文获取当前审核人信息
        Long reviewerId;
        String reviewerName;
        try {
            reviewerId = UserContext.getCurrentUserId();
            reviewerName = UserContext.getCurrentUserName();
        } catch (Exception e) {
            reviewerId = null;
            reviewerName = "审核员";
        }

        // 更新审核记录
        review.setReviewerId(reviewerId);
        review.setReviewerName(reviewerName);
        review.setReviewResult(1); // 1-通过
        review.setReviewComment(dto.getReviewComment());
        review.setReviewedAt(LocalDateTime.now());
        documentReviewMapper.updateById(review);

        // 更新文档状态为已发布，并设置发布时间
        Document document = documentMapper.selectById(review.getDocumentId());
        if (document != null) {
            document.setStatus(DocumentStatus.PUBLISHED.getCode());
            document.setPublishTime(LocalDateTime.now());
            documentMapper.updateById(document);

            // 触发RAG/KAG/ES索引（审核通过后才建索引）
            triggerRagReindex(document.getId());
            triggerKAGBuild(document.getId());
            triggerSearchIndex(document, null);

            // 发布审核通过事件 → RabbitMQ，通知文档作者
            try {
                ReviewEventDTO event = ReviewEventDTO.builder()
                        .eventType("APPROVED")
                        .documentId(document.getId())
                        .documentTitle(document.getTitle())
                        .authorId(document.getAuthorId())
                        .authorName(document.getAuthorName())
                        .reviewerId(reviewerId)
                        .reviewerName(reviewerName)
                        .reviewRound(review.getReviewRound())
                        .reviewLevel(review.getReviewLevel() != null ? review.getReviewLevel() : 1)
                        .timestamp(LocalDateTime.now())
                        .build();
                CorrelationData correlationData = new CorrelationData(
                        "review-APPROVED-" + document.getId() + "-" + UUID.randomUUID().toString().substring(0, 8));
                rabbitTemplate.convertAndSend(REVIEW_EXCHANGE, reviewRoutingKey("approved"), event, correlationData);
                log.info("已发布审核通过事件：documentId={}, eventType=APPROVED", document.getId());
            } catch (Exception e) {
                log.warn("发布审核通过事件失败（不影响审核流程）：documentId={}, error={}",
                        document.getId(), e.getMessage());
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rejectReview(DocumentReviewDTO dto) {
        log.info("审核驳回：reviewId={}", dto.getReviewId());

        if (dto.getReviewId() == null) {
            throw new BusinessException("审核记录ID不能为空");
        }

        if (!StringUtils.hasText(dto.getReviewComment())) {
            throw new BusinessException("驳回意见不能为空");
        }

        // 检查审核记录是否存在
        DocumentReview review = documentReviewMapper.selectById(dto.getReviewId());
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }

        if (review.getReviewResult() != null) {
            throw new BusinessException("该记录已审核");
        }

        // 从上下文获取当前审核人信息
        Long reviewerId;
        String reviewerName;
        try {
            reviewerId = UserContext.getCurrentUserId();
            reviewerName = UserContext.getCurrentUserName();
        } catch (Exception e) {
            reviewerId = null;
            reviewerName = "审核员";
        }

        // 更新审核记录
        review.setReviewerId(reviewerId);
        review.setReviewerName(reviewerName);
        review.setReviewResult(2); // 2-驳回
        review.setReviewComment(dto.getReviewComment());
        review.setReviewedAt(LocalDateTime.now());
        documentReviewMapper.updateById(review);

        // 驳回后文档状态退回草稿
        Document document = documentMapper.selectById(review.getDocumentId());
        if (document != null) {
            document.setStatus(DocumentStatus.DRAFT.getCode());
            documentMapper.updateById(document);

            // 发布审核驳回事件 → RabbitMQ，通知文档作者
            try {
                ReviewEventDTO event = ReviewEventDTO.builder()
                        .eventType("REJECTED")
                        .documentId(document.getId())
                        .documentTitle(document.getTitle())
                        .authorId(document.getAuthorId())
                        .authorName(document.getAuthorName())
                        .reviewerId(reviewerId)
                        .reviewerName(reviewerName)
                        .reviewRound(review.getReviewRound())
                        .reviewLevel(review.getReviewLevel() != null ? review.getReviewLevel() : 1)
                        .reviewComment(dto.getReviewComment())
                        .timestamp(LocalDateTime.now())
                        .build();
                CorrelationData correlationData = new CorrelationData(
                        "review-REJECTED-" + document.getId() + "-" + UUID.randomUUID().toString().substring(0, 8));
                rabbitTemplate.convertAndSend(REVIEW_EXCHANGE, reviewRoutingKey("rejected"), event, correlationData);
                log.info("已发布审核驳回事件：documentId={}, eventType=REJECTED", document.getId());
            } catch (Exception e) {
                log.warn("发布审核驳回事件失败（不影响审核流程）：documentId={}, error={}",
                        document.getId(), e.getMessage());
            }
        }

        return true;
    }

    /**
     * 自动修复：为状态是 PENDING_REVIEW 但缺少待审核记录的文档补建审核记录
     * <p>这种情况可能发生在直接操作数据库、数据迁移等场景</p>
     */
    private void syncOrphanedPendingDocuments() {
        try {
            List<Long> orphanedIds = jdbcTemplate.queryForList(
                    "SELECT d.id FROM kb_document d " +
                    "WHERE d.status = ? AND d.deleted = 0 " +
                    "AND NOT EXISTS (SELECT 1 FROM tb_document_review r WHERE r.document_id = d.id AND r.review_result IS NULL)",
                    Long.class,
                    DocumentStatus.PENDING_REVIEW.getCode()
            );

            if (!orphanedIds.isEmpty()) {
                log.info("发现 {} 个孤儿待审核文档，正在自动补建审核记录...", orphanedIds.size());
                for (Long docId : orphanedIds) {
                    try {
                        Document document = documentMapper.selectById(docId);
                        if (document == null) continue;

                        Integer currentRound = jdbcTemplate.queryForObject(
                                "SELECT COALESCE(MAX(review_round), 0) FROM tb_document_review WHERE document_id = ?",
                                Integer.class, docId
                        );

                        DocumentReview review = new DocumentReview();
                        review.setId(SnowflakeIdGenerator.getInstance().nextId());
                        review.setDocumentId(docId);
                        review.setReviewerId(null);
                        review.setReviewerName(null);
                        review.setReviewResult(null);
                        review.setReviewComment(null);
                        review.setBeforeStatus(document.getStatus());
                        review.setReviewedAt(null);
                        review.setReviewRound((currentRound != null ? currentRound : 0) + 1);
                        review.setReviewLevel(1);
                        review.setCreatedAt(LocalDateTime.now());
                        documentReviewMapper.insert(review);
                    } catch (Exception e) {
                        log.warn("补建审核记录失败：documentId={}, error={}", docId, e.getMessage());
                    }
                }
                log.info("孤儿待审核文档补建完成，共处理 {} 条", orphanedIds.size());
            }
        } catch (Exception e) {
            log.warn("检查孤儿待审核文档失败（不影响查询流程）：{}", e.getMessage());
        }
    }

    @Override
    public PageResult<DocumentReviewVO> getPendingReviews(ReviewQueryDTO dto) {
        // 自动修复：为状态是 PENDING_REVIEW 但缺少待审核记录的文档补建审核记录
        syncOrphanedPendingDocuments();

        // 构建查询条件
        LambdaQueryWrapper<DocumentReview> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选：0=待审核(null)，1=已通过，2=已驳回，null=全部
        if (dto.getStatus() == null) {
            // 不筛选
        } else if (dto.getStatus() == 0) {
            wrapper.isNull(DocumentReview::getReviewResult);
        } else {
            wrapper.eq(DocumentReview::getReviewResult, dto.getStatus());
        }

        if (dto.getReviewerId() != null) {
            wrapper.eq(DocumentReview::getReviewerId, dto.getReviewerId());
        }

        if (dto.getAuthorId() != null) {
            wrapper.exists(
                    "SELECT 1 FROM kb_document d WHERE d.id = tb_document_review.document_id AND d.author_id = {0}",
                    dto.getAuthorId()
            );
        }

        // 关键词搜索
        if (StringUtils.hasText(dto.getKeyword())) {
            // 子查询文档标题
            wrapper.exists(
                    "SELECT 1 FROM kb_document d WHERE d.id = tb_document_review.document_id AND d.title LIKE CONCAT('%', {0}, '%')",
                    dto.getKeyword()
            );
        }

        // 排序
        wrapper.orderByDesc(DocumentReview::getCreatedAt);

        // 分页查询
        Page<DocumentReview> page = new Page<>(dto.getCurrent(), dto.getSize());
        IPage<DocumentReview> reviewPage = documentReviewMapper.selectPage(page, wrapper);

        // 转换为VO
        IPage<DocumentReviewVO> voPage = reviewPage.convert(review -> {
            return buildReviewVO(review);
        });

        return PageResult.<DocumentReviewVO>builder()
                .records(voPage.getRecords())
                .total(voPage.getTotal())
                .current(voPage.getCurrent())
                .size(voPage.getSize())
                .build();
    }

    @Override
    public DocumentReviewVO getCurrentReviewTask(Long documentId) {
        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        syncOrphanedPendingDocuments();
        DocumentReview review = documentReviewMapper.selectLatestByDocumentId(documentId);
        if (review == null) {
            throw new BusinessException("未找到审核记录");
        }
        return buildReviewVO(review);
    }

    @Override
    public List<DocumentReviewVO> getDocumentReviewHistory(Long documentId) {
        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        List<DocumentReview> reviews = documentReviewMapper.selectList(
                new LambdaQueryWrapper<DocumentReview>()
                        .eq(DocumentReview::getDocumentId, documentId)
                        .orderByDesc(DocumentReview::getReviewRound)
        );

        return reviews.stream()
                .map(this::buildReviewVO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getPendingCount() {
        LambdaQueryWrapper<DocumentReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(DocumentReview::getReviewResult);
        return documentReviewMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Long> getReviewStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("pending", documentReviewMapper.selectCount(
                new LambdaQueryWrapper<DocumentReview>().isNull(DocumentReview::getReviewResult)));
        stats.put("approved", documentReviewMapper.selectCount(
                new LambdaQueryWrapper<DocumentReview>().eq(DocumentReview::getReviewResult, 1)));
        stats.put("rejected", documentReviewMapper.selectCount(
                new LambdaQueryWrapper<DocumentReview>().eq(DocumentReview::getReviewResult, 2)));
        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchReview(List<Long> taskIds, String status, String comment) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw new BusinessException("审核任务ID列表不能为空");
        }

        DocumentReviewDTO dto = new DocumentReviewDTO();
        dto.setReviewComment(comment);

        for (Long taskId : taskIds) {
            dto.setReviewId(taskId);
            if ("approved".equalsIgnoreCase(status)) {
                approveReview(dto);
            } else if ("rejected".equalsIgnoreCase(status)) {
                rejectReview(dto);
            } else {
                throw new BusinessException("无效的审核结果：" + status);
            }
        }
    }

    private DocumentReviewVO buildReviewVO(DocumentReview review) {
        Document document = documentMapper.selectById(review.getDocumentId());
        String documentTitle = document != null ? document.getTitle() : "";
        String authorName = document != null ? document.getAuthorName() : "";
        Long authorId = document != null ? document.getAuthorId() : null;

        Long categoryId = document != null ? document.getCategoryId() : null;
        String categoryName = "";
        if (categoryId != null) {
            try {
                categoryName = jdbcTemplate.queryForObject(
                        "SELECT category_name FROM kb_category WHERE id = ?",
                        String.class,
                        categoryId
                );
            } catch (Exception ignored) {
                // 分类删除或不存在时返回空字符串，不影响审核页展示
            }
        }

        return DocumentReviewVO.builder()
                .id(review.getId())
                .documentId(review.getDocumentId())
                .documentTitle(documentTitle)
                .authorId(authorId)
                .authorName(authorName)
                .reviewerId(review.getReviewerId())
                .reviewerName(review.getReviewerName())
                .reviewResult(review.getReviewResult())
                .reviewComment(review.getReviewComment())
                .beforeStatus(review.getBeforeStatus())
                .reviewedAt(review.getReviewedAt())
                .reviewRound(review.getReviewRound())
                .createdAt(review.getCreatedAt())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .build();
    }

    // ==================== 异步索引触发方法 ====================

    /**
     * 异步触发RAG重建索引
     */
    private void triggerRagReindex(Long docId) {
        CompletableFuture.runAsync(() -> {
            try {
                ragFeignClient.reindexDocument(docId);
                log.info("已触发RAG重建索引：documentId={}", docId);
            } catch (Exception e) {
                log.warn("触发RAG重建索引失败（RAG服务不可用，不影响文档操作）：documentId={}, error={}",
                        docId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 异步触发KAG图谱构建
     */
    private void triggerKAGBuild(Long docId) {
        CompletableFuture.runAsync(() -> {
            try {
                kagFeignClient.buildGraph(docId);
                log.info("已触发KAG图谱构建：documentId={}", docId);
            } catch (Exception e) {
                log.warn("触发KAG图谱构建失败（KAG服务不可用，不影响文档操作）：documentId={}, error={}",
                        docId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 异步同步到ES搜索索引
     */
    private void triggerSearchIndex(Document document, String content) {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> docData = buildSearchIndexData(document, content);
                searchIndexFeignClient.indexDocument(docData);
                log.info("已同步到ES搜索索引：documentId={}, title={}", document.getId(), document.getTitle());
            } catch (Exception e) {
                log.warn("同步ES搜索索引失败（搜索服务不可用，不影响文档操作）：documentId={}, error={}",
                        document.getId(), e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 构建ES搜索索引数据
     *
     * @param document 文档实体
     * @param content  文档内容（可为null）
     * @return 索引数据Map
     */
    private Map<String, Object> buildSearchIndexData(Document document, String content) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", document.getId());
        data.put("title", document.getTitle());
        data.put("summary", document.getSummary());
        if (StrUtil.isNotBlank(content)) {
            data.put("content", content.length() > 1000 ? content.substring(0, 1000) : content);
        } else if (StrUtil.isNotBlank(document.getContent())) {
            String c = document.getContent();
            data.put("content", c.length() > 1000 ? c.substring(0, 1000) : c);
        }
        data.put("categoryId", document.getCategoryId());
        data.put("tags", document.getTags());
        data.put("status", document.getStatus());
        data.put("isPublic", document.getIsPublic());
        data.put("viewCount", document.getViewCount());
        data.put("likeCount", document.getLikeCount());
        data.put("commentCount", document.getCommentCount());
        data.put("authorId", document.getAuthorId());
        data.put("author", Map.of("id", document.getAuthorId() != null ? document.getAuthorId() : 0,
                "name", document.getAuthorName() != null ? document.getAuthorName() : ""));
        if (document.getPublishTime() != null) {
            data.put("publishTime", document.getPublishTime().toString());
        }
        data.put("createdAt", document.getCreatedAt() != null ? document.getCreatedAt().toString() : "");
        return data;
    }

    // ==================== 直接发布方法 ====================

    /**
     * 检查系统配置：是否需要文档审核
     */
    private boolean checkRequireApproval() {
        String value = systemConfigCache.getConfig("system.requireApproval");
        return !"false".equals(value);
    }

    /**
     * 直接发布文档（无需审核）
     * <p>当 system.requireApproval 配置为 false 时调用此方法</p>
     */
    private Boolean directPublishDocument(Document document) {
        // 只有草稿、已发布或待审核状态的文档可以直接发布
        Integer status = document.getStatus();
        if (!status.equals(DocumentStatus.DRAFT.getCode())
                && !status.equals(DocumentStatus.PUBLISHED.getCode())
                && !status.equals(DocumentStatus.PENDING_REVIEW.getCode())) {
            throw new BusinessException("当前文档状态不允许发布");
        }

        // 直接设置为已发布
        document.setStatus(DocumentStatus.PUBLISHED.getCode());
        document.setPublishTime(LocalDateTime.now());
        documentMapper.updateById(document);

        // 触发RAG/KAG/ES索引
        triggerRagReindex(document.getId());
        triggerKAGBuild(document.getId());
        triggerSearchIndex(document, null);

        log.info("文档直接发布成功：documentId={}", document.getId());
        return true;
    }
}
