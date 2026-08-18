package com.knowledge.base.document.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.enums.DocumentStatus;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.result.ResultCode;
import com.knowledge.base.document.service.LikeService;

import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.AutoSaveDTO;
import com.knowledge.base.document.dto.DocumentDTO;
import com.knowledge.base.document.dto.FileUploadResponse;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.entity.Category;
import com.knowledge.base.document.entity.mongodb.DocumentContent;
import com.knowledge.base.document.event.StatisticsEventPublisher;
import com.knowledge.base.document.feign.FileServiceFeignClient;
import com.knowledge.base.document.feign.GraphFeignClient;
import com.knowledge.base.document.feign.KAGFeignClient;
import com.knowledge.base.document.feign.RagFeignClient;
import com.knowledge.base.document.feign.SearchIndexFeignClient;
import com.knowledge.base.document.mapper.DocumentMapper;
import com.knowledge.base.document.mapper.CategoryMapper;
import com.knowledge.base.document.service.DocumentAccessService;
import com.knowledge.base.document.service.DocumentContentService;
import com.knowledge.base.document.service.DocumentReviewService;
import com.knowledge.base.document.service.DocumentService;
import com.knowledge.base.document.service.FileParserService;
import com.knowledge.base.document.service.AutoSaveHistoryService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.client.UserServiceClient;
import com.knowledge.base.document.vo.AuthorVO;
import com.knowledge.base.document.vo.DocumentNeighborVO;
import com.knowledge.base.document.vo.DocumentVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.knowledge.base.common.config.SystemConfigCache;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 文档Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现文档相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private DocumentContentService documentContentService;

    @Resource
    private FileServiceFeignClient fileServiceFeignClient;

    @Resource
    private RagFeignClient ragFeignClient;

    @Resource
    private KAGFeignClient kagFeignClient;

    @Resource
    private SearchIndexFeignClient searchIndexFeignClient;

    @Resource
    private GraphFeignClient graphFeignClient;

    @Resource
    private DocumentAccessService documentAccessService;

    @Resource
    private LikeService likeService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private SystemConfigCache systemConfigCache;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private StatisticsEventPublisher statisticsEventPublisher;

    @Resource
    private DocumentReviewService documentReviewService;

    @Resource
    private FileParserService fileParserService;

    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Resource
    private AutoSaveHistoryService autoSaveHistoryService;

    /** 头像缓存，避免对同一用户重复调用HTTP */
    private final Map<Long, String> avatarCache = new ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocument(DocumentDTO documentDTO) {
        log.info("创建文档：title={}", documentDTO.getTitle());

        // 构建文档实体
        Document document = new Document();
        // 复制属性时排除content字段，content存储在MongoDB中
        BeanUtil.copyProperties(documentDTO, document, "content");

        // 生成ID
        document.setId(SnowflakeIdGenerator.getInstance().nextId());

        // 设置默认值
        if (document.getDocumentType() == null) {
            document.setDocumentType(1);
        }
        if (document.getStatus() == null) {
            document.setStatus(0);
        }
        if (document.getIsTop() == null) {
            document.setIsTop(0);
        }
        if (document.getIsRecommend() == null) {
            document.setIsRecommend(0);
        }
        if (document.getSource() == null) {
            document.setSource(1);
        }
        if (document.getAllowComment() == null) {
            document.setAllowComment(1);
        }
        if (document.getSort() == null) {
            document.setSort(0);
        }
        if (document.getViewCount() == null) {
            document.setViewCount(0L);
        }
        if (document.getLikeCount() == null) {
            document.setLikeCount(0L);
        }
        if (document.getFavoriteCount() == null) {
            document.setFavoriteCount(0L);
        }
        if (document.getCommentCount() == null) {
            document.setCommentCount(0L);
        }

        // 导入的文档（文件类型）设置默认分类和可见性
        if (Objects.equals(document.getDocumentType(), 2)) {
            if (document.getCategoryId() == null) {
                Category techCategory = categoryMapper.selectOne(
                        new LambdaQueryWrapper<Category>()
                                .eq(Category::getCategoryName, "技术文档")
                );
                if (techCategory != null) {
                    document.setCategoryId(techCategory.getId());
                    log.info("导入文档设置默认分类：categoryId={}, categoryName=技术文档", techCategory.getId());
                }
            }
            if (document.getIsPublic() == null) {
                document.setIsPublic(0); // 团队可见
            }
        }

        // 从上下文中获取当前登录用户
        document.setAuthorId(UserContext.getCurrentUserId());
        document.setAuthorName(UserContext.getCurrentUserName());

        // 如果是发布状态，设置发布时间
        if (Objects.equals(document.getStatus(), 1)) {
            document.setPublishTime(LocalDateTime.now());
        }

        // 先保存MySQL文档记录（不包含content字段）
        int count = documentMapper.insert(document);
        if (count <= 0) {
            throw new BusinessException("创建文档失败");
        }

        // 增加分类的文档数量
        if (document.getCategoryId() != null) {
            categoryMapper.incrementDocumentCount(document.getCategoryId());
        }

        // 同步保存内容到MongoDB
        boolean hasContent = StrUtil.isNotBlank(documentDTO.getContent());
        if (hasContent) {
            try {
                String contentId = documentContentService.saveContent(document.getId(), documentDTO.getContent());
                // 计算内容长度（字符数）和文件大小（UTF-8字节数）
                String content = documentDTO.getContent();
                int contentLength = content.length();
                long fileSize = content.getBytes(StandardCharsets.UTF_8).length;
                // 更新MySQL记录的contentId、contentLength、fileSize
                Document doc = new Document();
                doc.setId(document.getId());
                doc.setContentId(contentId);
                doc.setContentLength(contentLength);
                doc.setFileSize(fileSize);
                documentMapper.updateById(doc);
                log.info("文档内容保存成功：documentId={}, contentId={}, contentLength={}, fileSize={}", document.getId(), contentId, contentLength, fileSize);
            } catch (Exception e) {
                log.error("保存文档内容到MongoDB失败：documentId={}, error={}", document.getId(), e.getMessage());
                throw new BusinessException("保存文档内容失败：" + e.getMessage());
            }
        }

        // 异步触发RAG索引（仅已发布且有内容的文档）
        if (Objects.equals(document.getStatus(), 1) && hasContent) {
            triggerRagReindex(document.getId());
            triggerKAGBuild(document.getId());
        }

        // 异步同步到ES搜索索引（已发布的文档）
        if (Objects.equals(document.getStatus(), 1)) {
            triggerSearchIndex(document, documentDTO.getContent());
        }

        return document.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long autoSaveDocument(AutoSaveDTO autoSaveDTO) {
        // 空标题自动填充为"未命名文档"
        String title = StrUtil.isBlank(autoSaveDTO.getTitle())
                ? "未命名文档" : autoSaveDTO.getTitle().trim();

        // 提前捕获用户信息和内容，用于异步快照保存（ThreadLocal不传播到异步线程）
        final Long currentUserId = UserContext.getCurrentUserId();
        final String currentContent = autoSaveDTO.getContent();

        if (autoSaveDTO.getId() != null) {
            // -- 更新已有文档 --
            log.info("自动保存-更新：documentId={}", autoSaveDTO.getId());
            Document existDocument = documentMapper.selectById(autoSaveDTO.getId());
            if (existDocument == null) {
                throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
            }

            // 更新MongoDB内容（如果提供了content）
            if (StrUtil.isNotBlank(currentContent)) {
                try {
                    if (StrUtil.isNotBlank(existDocument.getContentId())) {
                        documentContentService.updateContent(autoSaveDTO.getId(), currentContent);
                    } else {
                        String contentId = documentContentService.saveContent(autoSaveDTO.getId(), currentContent);
                        Document upd = new Document();
                        upd.setId(autoSaveDTO.getId());
                        upd.setContentId(contentId);
                        documentMapper.updateById(upd);
                    }
                    // 同步内容长度和大小
                    Document sizeUpd = new Document();
                    sizeUpd.setId(autoSaveDTO.getId());
                    sizeUpd.setContentLength(currentContent.length());
                    sizeUpd.setFileSize((long) currentContent.getBytes(StandardCharsets.UTF_8).length);
                    documentMapper.updateById(sizeUpd);
                } catch (Exception e) {
                    log.warn("自动保存-更新MongoDB内容失败：documentId={}, error={}",
                            autoSaveDTO.getId(), e.getMessage());
                }
            }

            // 仅更新MySQL中有值的字段，避免将已有数据覆盖为null
            Document updateDoc = new Document();
            updateDoc.setId(autoSaveDTO.getId());
            updateDoc.setTitle(title);
            updateDoc.setStatus(0); // 强制草稿状态
            if (autoSaveDTO.getSummary() != null) {
                updateDoc.setSummary(autoSaveDTO.getSummary());
            }
            if (autoSaveDTO.getCategoryId() != null) {
                updateDoc.setCategoryId(autoSaveDTO.getCategoryId());
            }
            if (autoSaveDTO.getTeamId() != null) {
                updateDoc.setTeamId(autoSaveDTO.getTeamId());
            }
            if (autoSaveDTO.getTags() != null) {
                updateDoc.setTags(autoSaveDTO.getTags());
            }

            documentMapper.updateById(updateDoc);

            // 异步保存自动保存历史快照
            final Long updatedDocId = autoSaveDTO.getId();
            CompletableFuture.runAsync(() -> {
                autoSaveHistoryService.saveSnapshot(updatedDocId, title, currentContent, currentUserId);
            }, asyncTaskExecutor);

            return autoSaveDTO.getId();
        } else {
            // -- 创建新草稿 --
            // 去重：检查当前用户是否已有近期自动保存的草稿（5分钟内）
            Document recentDraft = documentMapper.selectOne(
                    new LambdaQueryWrapper<Document>()
                            .eq(Document::getAuthorId, currentUserId)
                            .eq(Document::getStatus, 0)
                            .ge(Document::getCreatedAt, LocalDateTime.now().minusMinutes(5))
                            .orderByDesc(Document::getCreatedAt)
                            .last("LIMIT 1")
            );

            if (recentDraft != null) {
                // 复用已有草稿而不是创建新的
                log.info("自动保存-去重复用草稿：documentId={}", recentDraft.getId());
                Document updateDoc = new Document();
                updateDoc.setId(recentDraft.getId());
                updateDoc.setTitle(title);
                updateDoc.setStatus(0);
                updateDoc.setAutoSaveDismissed(0); // 标记为自动保存交互过的草稿
                if (autoSaveDTO.getSummary() != null) {
                    updateDoc.setSummary(autoSaveDTO.getSummary());
                }
                if (autoSaveDTO.getCategoryId() != null) {
                    updateDoc.setCategoryId(autoSaveDTO.getCategoryId());
                }
                if (autoSaveDTO.getTeamId() != null) {
                    updateDoc.setTeamId(autoSaveDTO.getTeamId());
                }
                if (autoSaveDTO.getTags() != null) {
                    updateDoc.setTags(autoSaveDTO.getTags());
                }
                documentMapper.updateById(updateDoc);

                // 更新MongoDB内容
                if (StrUtil.isNotBlank(currentContent)) {
                    try {
                        if (StrUtil.isNotBlank(recentDraft.getContentId())) {
                            documentContentService.updateContent(recentDraft.getId(), currentContent);
                        } else {
                            String contentId = documentContentService.saveContent(recentDraft.getId(), currentContent);
                            Document upd = new Document();
                            upd.setId(recentDraft.getId());
                            upd.setContentId(contentId);
                            documentMapper.updateById(upd);
                        }
                        // 同步内容长度和大小（MySQL），无论 contentId 是否已存在都需要更新
                        Document sizeUpd = new Document();
                        sizeUpd.setId(recentDraft.getId());
                        sizeUpd.setContentLength(currentContent.length());
                        sizeUpd.setFileSize((long) currentContent.getBytes(StandardCharsets.UTF_8).length);
                        documentMapper.updateById(sizeUpd);
                    } catch (Exception e) {
                        log.warn("自动保存-去重后更新MongoDB失败：documentId={}, error={}",
                                recentDraft.getId(), e.getMessage());
                    }
                }

                // 异步保存自动保存历史快照
                final Long reusedId = recentDraft.getId();
                CompletableFuture.runAsync(() -> {
                    autoSaveHistoryService.saveSnapshot(reusedId, title, currentContent, currentUserId);
                }, asyncTaskExecutor);

                return recentDraft.getId();
            }

            log.info("自动保存-创建新草稿：title={}", title);

            Document document = new Document();
            document.setId(SnowflakeIdGenerator.getInstance().nextId());
            document.setTitle(title);
            document.setContent(currentContent);
            document.setSummary(autoSaveDTO.getSummary());
            document.setCategoryId(autoSaveDTO.getCategoryId());
            document.setTeamId(autoSaveDTO.getTeamId());
            document.setTags(autoSaveDTO.getTags());
            document.setStatus(0); // 草稿
            document.setDocumentType(1);
            document.setIsTop(0);
            document.setIsRecommend(0);
            document.setSource(1);
            document.setAllowComment(1);
            document.setSort(0);
            document.setViewCount(0L);
            document.setLikeCount(0L);
            document.setFavoriteCount(0L);
            document.setCommentCount(0L);
            document.setAuthorId(currentUserId);
            document.setAuthorName(UserContext.getCurrentUserName());
            document.setAutoSaveDismissed(0); // 标记为自动保存创建的草稿

            int count = documentMapper.insert(document);
            if (count <= 0) {
                throw new BusinessException("创建草稿失败");
            }

            // 增加分类文档计数
            if (document.getCategoryId() != null) {
                categoryMapper.incrementDocumentCount(document.getCategoryId());
            }

            // 保存内容到MongoDB（如果有）
            if (StrUtil.isNotBlank(currentContent)) {
                try {
                    String contentId = documentContentService.saveContent(document.getId(), currentContent);
                    Document upd = new Document();
                    upd.setId(document.getId());
                    upd.setContentId(contentId);
                    upd.setContentLength(currentContent.length());
                    upd.setFileSize((long) currentContent.getBytes(StandardCharsets.UTF_8).length);
                    documentMapper.updateById(upd);
                } catch (Exception e) {
                    log.error("自动保存-MongoDB保存失败：documentId={}, error={}", document.getId(), e.getMessage());
                }
            }

            // 异步保存自动保存历史快照
            final Long newDocId = document.getId();
            CompletableFuture.runAsync(() -> {
                autoSaveHistoryService.saveSnapshot(newDocId, title, currentContent, currentUserId);
            }, asyncTaskExecutor);

            // 不触发RAG/KAG/ES索引 — 草稿不需要索引
            return document.getId();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDocument(DocumentDTO documentDTO) {
        log.info("更新文档：documentId={}", documentDTO.getId());

        if (documentDTO.getId() == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 检查文档是否存在
        Document existDocument = documentMapper.selectById(documentDTO.getId());
        if (existDocument == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        // 如果有内容更新，更新MongoDB中的内容（如果MongoDB不可用，跳过内容更新）
        String contentId = existDocument.getContentId();
        if (StrUtil.isNotBlank(documentDTO.getContent())) {
            try {
                if (StrUtil.isNotBlank(existDocument.getContentId())) {
                    // 更新现有内容
                    documentContentService.updateContent(documentDTO.getId(), documentDTO.getContent());
                } else {
                    // 创建新内容
                    contentId = documentContentService.saveContent(documentDTO.getId(), documentDTO.getContent());
                }
            } catch (Exception e) {
                // MongoDB连接失败时，记录警告但继续执行
                log.warn("更新文档内容到MongoDB失败，文档元数据已更新到MySQL：documentId={}, error={}",
                        documentDTO.getId(), e.getMessage());
            }
        }

        // 构建更新实体（不包含content字段）
        Document document = new Document();
        // 复制属性时排除content字段，content存储在MongoDB中
        BeanUtil.copyProperties(documentDTO, document, "content");
        // 设置contentId
        document.setContentId(contentId);

        // 如果内容变更，同步更新内容长度和文件大小
        if (StrUtil.isNotBlank(documentDTO.getContent())) {
            String updatedContent = documentDTO.getContent();
            document.setContentLength(updatedContent.length());
            document.setFileSize((long) updatedContent.getBytes(StandardCharsets.UTF_8).length);
        }

        // 如果状态从草稿变为发布，设置发布时间
        if (Objects.equals(existDocument.getStatus(), 0)
            && Objects.equals(documentDTO.getStatus(), 1)) {
            document.setPublishTime(LocalDateTime.now());
        }

        // 如果状态变为待审核（PENDING_REVIEW=3），触发审核流程
        Integer newStatus = documentDTO.getStatus();
        boolean toPendingReview = !Objects.equals(existDocument.getStatus(), newStatus)
                && Objects.equals(newStatus, 3);

        int count = documentMapper.updateById(document);

        // 如果分类发生了变更，更新对应分类的文档数量
        Long oldCategoryId = existDocument.getCategoryId();
        Long newCategoryId = document.getCategoryId();
        if (count > 0 && !Objects.equals(oldCategoryId, newCategoryId)) {
            if (oldCategoryId != null) {
                categoryMapper.decrementDocumentCount(oldCategoryId);
            }
            if (newCategoryId != null) {
                categoryMapper.incrementDocumentCount(newCategoryId);
            }
        }

        // 状态变为待审核时，触发审核流程（推送WebSocket通知）
        if (count > 0 && toPendingReview) {
            try {
                documentReviewService.submitForReview(documentDTO.getId());
                log.info("文档编辑后自动提交审核：documentId={}", documentDTO.getId());
            } catch (Exception e) {
                log.error("提交审核失败：documentId={}, error={}", documentDTO.getId(), e.getMessage(), e);
            }
        }

        // 异步触发RAG索引更新
        Long docId = documentDTO.getId();
        boolean contentChanged = StrUtil.isNotBlank(documentDTO.getContent());
        Integer oldStatus = existDocument.getStatus();
        boolean statusToPublished = Objects.equals(oldStatus, 0) && Objects.equals(newStatus, 1);
        boolean statusFromPublished = Objects.equals(oldStatus, 1) && !Objects.equals(newStatus, 1);

        if ((contentChanged || statusToPublished) && Objects.equals(newStatus, 1)) {
            // 内容变更 或 从草稿发布 → 重建RAG索引
            triggerRagReindex(docId);
            triggerKAGBuild(docId);
            // 同步ES搜索索引
            Document syncDoc = new Document();
            BeanUtil.copyProperties(documentDTO, syncDoc, "content");
            syncDoc.setContent(documentDTO.getContent());
            triggerSearchIndex(syncDoc, documentDTO.getContent());
        } else if (statusFromPublished) {
            // 从发布状态变为非发布（下架/归档） → 从RAG中删除
            triggerRagDelete(docId);
            triggerKAGDelete(docId);
            triggerGraphDelete(docId);
            // 从ES搜索索引删除
            triggerSearchDelete(docId);
        } else if (Objects.equals(newStatus, 1)) {
            // 已发布的文档有其他更新 → 同步ES搜索索引
            Document syncDoc = new Document();
            BeanUtil.copyProperties(documentDTO, syncDoc, "content");
            triggerSearchIndex(syncDoc, documentDTO.getContent());
        }

        return count > 0;
    }

    @Override
    public Boolean updateSummary(Long documentId, String summary) {
        log.info("更新文档摘要：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        Document existDocument = documentMapper.selectById(documentId);
        if (existDocument == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        Document document = new Document();
        document.setId(documentId);
        document.setSummary(summary);
        int count = documentMapper.updateById(document);

        log.info("文档摘要更新成功：documentId={}", documentId);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDocument(Long documentId) {
        log.info("删除文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 删除前获取分类ID，用于更新分类文档数量
        Document existDocument = documentMapper.selectById(documentId);
        Long categoryId = existDocument != null ? existDocument.getCategoryId() : null;

        int count = documentMapper.deleteById(documentId);

        // 异步从RAG中删除该文档的向量索引
        if (count > 0) {
            // 减少对应分类的文档数量
            if (categoryId != null) {
                categoryMapper.decrementDocumentCount(categoryId);
            }
            triggerRagDelete(documentId);
            triggerKAGDelete(documentId);
            triggerSearchDelete(documentId);
            triggerGraphDelete(documentId);
        }

        return count > 0;
    }

    /**
     * 构建作者信息VO
     */
    private AuthorVO buildAuthorVO(Long authorId, String authorName) {
        if (authorId == null) {
            return null;
        }
        AuthorVO authorVO = new AuthorVO();
        authorVO.setId(authorId);
        authorVO.setUsername(authorName);
        // 从用户服务获取头像，使用缓存避免重复调用
        String avatar = avatarCache.computeIfAbsent(authorId, id -> {
            String fetched = userServiceClient.getUserAvatar(id);
            return fetched != null ? fetched : "";
        });
        authorVO.setAvatar(avatar);
        authorVO.setEmail("");
        authorVO.setPosition("员工");
        return authorVO;
    }

    @Override
    public DocumentVO getDocumentById(Long documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        DocumentVO documentVO = BeanUtil.copyProperties(document, DocumentVO.class);

        // 构建作者信息
        documentVO.setAuthor(buildAuthorVO(document.getAuthorId(), document.getAuthorName()));

        // 从MongoDB获取文档内容
        if (StrUtil.isNotBlank(document.getContentId())) {
            try {
                DocumentContent documentContent = documentContentService.getContentById(document.getContentId());
                if (documentContent != null) {
                    documentVO.setContent(documentContent.getContent());
                }
            } catch (Exception e) {
                log.error("获取文档内容失败：documentId={}, contentId={}", documentId, document.getContentId(), e);
                documentVO.setContent(null);
            }
        }

        // 查询当前用户是否已点赞
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId != null) {
                documentVO.setIsLiked(likeService.isLiked(documentId, userId, 1));
            }
        } catch (Exception e) {
            log.debug("获取点赞状态失败（可能是匿名访问）：documentId={}", documentId);
        }

        return documentVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO viewDocument(Long documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        // 增加浏览次数
        documentMapper.incrementViewCount(documentId);

        DocumentVO documentVO = BeanUtil.copyProperties(document, DocumentVO.class);

        // 构建作者信息
        documentVO.setAuthor(buildAuthorVO(document.getAuthorId(), document.getAuthorName()));

        // 从MongoDB获取文档内容
        if (StrUtil.isNotBlank(document.getContentId())) {
            try {
                DocumentContent documentContent = documentContentService.getContentById(document.getContentId());
                if (documentContent != null) {
                    documentVO.setContent(documentContent.getContent());
                }
            } catch (Exception e) {
                log.error("获取文档内容失败：documentId={}, contentId={}", documentId, document.getContentId(), e);
                documentVO.setContent(null);
            }
        }

        // 异步记录访问记录，不影响主流程响应时间
        // 注意：必须在主线程中获取用户ID，因为ThreadLocal不能跨线程传递
        // 公开分享访问时用户未登录，跳过访问记录
        final Long currentUserId;
        try {
            currentUserId = UserContext.getCurrentUserId();
            // 查询当前用户是否已点赞
            documentVO.setIsLiked(likeService.isLiked(documentId, currentUserId, 1));
        } catch (IllegalStateException e) {
            log.debug("匿名用户访问，跳过记录：documentId={}", documentId);
            return documentVO;
        }
        final String documentTitle = document.getTitle();
        final String currentUserName = UserContext.getCurrentUserName();
        CompletableFuture.runAsync(() -> {
            try {
                documentAccessService.recordAccess(currentUserId, documentId, documentTitle);
                log.debug("异步记录访问记录成功：userId={}, documentId={}, documentTitle={}", currentUserId, documentId, documentTitle);
            } catch (Exception e) {
                log.error("异步记录访问记录失败：userId={}, documentId={}, documentTitle={}", currentUserId, documentId, documentTitle, e);
            }
        }, asyncTaskExecutor);

        // 发布浏览统计事件到 RabbitMQ
        statisticsEventPublisher.publishViewEvent(currentUserId, currentUserName, documentId, documentTitle);

        return documentVO;
    }

    @Override
    public IPage<DocumentVO> pageDocuments(Long current, Long size, Long categoryId, Long teamId, String keyword, Integer status, String sortBy, String sortOrder, Long authorId) {
        // 构建查询条件
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            List<Long> categoryIds = collectCategoryIds(categoryId);
            wrapper.in(Document::getCategoryId, categoryIds);
        }

        if (teamId != null) {
            wrapper.eq(Document::getTeamId, teamId);
        }

        if (status != null) {
            wrapper.eq(Document::getStatus, status);
        }

        if (authorId != null) {
            wrapper.eq(Document::getAuthorId, authorId);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Document::getTitle, keyword)
                .or()
                .like(Document::getSummary, keyword)
                // 注意：content字段存储在MongoDB中，这里只搜索MySQL中的字段
                // 如果需要全文搜索，需要从MongoDB中查询
                .or()
                .like(Document::getTags, keyword));
        }

        // 动态排序逻辑
        if (StringUtils.hasText(sortBy) && StringUtils.hasText(sortOrder)) {
            // 根据前端传递的排序参数进行排序
            boolean isAsc = "asc".equalsIgnoreCase(sortOrder);

            switch (sortBy) {
                case "updatedAt":
                    if (isAsc) {
                        wrapper.orderByAsc(Document::getUpdatedAt);
                    } else {
                        wrapper.orderByDesc(Document::getUpdatedAt);
                    }
                    // 二级排序：按创建时间倒序
                    wrapper.orderByDesc(Document::getCreatedAt);
                    break;
                case "createdAt":
                    if (isAsc) {
                        wrapper.orderByAsc(Document::getCreatedAt);
                    } else {
                        wrapper.orderByDesc(Document::getCreatedAt);
                    }
                    // 二级排序：按修改时间倒序
                    wrapper.orderByDesc(Document::getUpdatedAt);
                    break;
                case "publishTime":
                    if (isAsc) {
                        wrapper.orderByAsc(Document::getPublishTime);
                    } else {
                        wrapper.orderByDesc(Document::getPublishTime);
                    }
                    break;
                case "title":
                    if (isAsc) {
                        wrapper.orderByAsc(Document::getTitle);
                    } else {
                        wrapper.orderByDesc(Document::getTitle);
                    }
                    break;
                case "viewCount":
                    if (isAsc) {
                        wrapper.orderByAsc(Document::getViewCount);
                    } else {
                        wrapper.orderByDesc(Document::getViewCount);
                    }
                    break;
                default:
                    // 默认排序：按置顶、排序、发布时间倒序
                    wrapper.orderByDesc(Document::getIsTop)
                        .orderByDesc(Document::getSort)
                        .orderByDesc(Document::getPublishTime);
                    break;
            }
        } else {
            // 默认排序：按置顶、排序、发布时间倒序
            wrapper.orderByDesc(Document::getIsTop)
                .orderByDesc(Document::getSort)
                .orderByDesc(Document::getPublishTime);
        }

        // 分页查询
        Page<Document> page = new Page<>(current, size);
        IPage<Document> documentPage = documentMapper.selectPage(page, wrapper);

        // 转换为VO
        return documentPage.convert(document -> {
            DocumentVO documentVO = BeanUtil.copyProperties(document, DocumentVO.class);
            // 构建作者信息
            documentVO.setAuthor(buildAuthorVO(document.getAuthorId(), document.getAuthorName()));
            // 查询分类名称
            if (document.getCategoryId() != null) {
                Category category = categoryMapper.selectById(document.getCategoryId());
                if (category != null) {
                    documentVO.setCategoryName(category.getCategoryName());
                }
            }
            // 查询团队空间名称
            if (document.getTeamId() != null) {
                try {
                    String teamName = jdbcTemplate.queryForObject(
                        "SELECT team_name FROM kb_team WHERE id = ? AND deleted = 0",
                        String.class, document.getTeamId());
                    documentVO.setTeamName(teamName);
                } catch (Exception e) {
                    log.warn("Failed to query team name for teamId={}", document.getTeamId());
                }
            }
            return documentVO;
        });
    }

    @Override
    public DocumentNeighborVO getDocumentNeighbors(Long documentId) {
        Document currentDoc = getById(documentId);
        if (currentDoc == null) {
            return new DocumentNeighborVO();
        }

        Integer isTop = currentDoc.getIsTop() != null ? currentDoc.getIsTop() : 0;
        Integer sort = currentDoc.getSort() != null ? currentDoc.getSort() : 0;
        LocalDateTime publishTime = currentDoc.getPublishTime() != null ? currentDoc.getPublishTime() : LocalDateTime.now();

        DocumentNeighborVO prevDoc = documentMapper.selectPrev(isTop, sort, publishTime);
        DocumentNeighborVO nextDoc = documentMapper.selectNext(isTop, sort, publishTime);

        DocumentNeighborVO result = new DocumentNeighborVO();
        if (prevDoc != null) {
            result.setPrevId(prevDoc.getPrevId());
            result.setPrevTitle(prevDoc.getPrevTitle());
        }
        if (nextDoc != null) {
            result.setNextId(nextDoc.getNextId());
            result.setNextTitle(nextDoc.getNextTitle());
        }
        return result;
    }

    @Override
    public String uploadDocumentFile(MultipartFile file) {
        log.info("上传文档文件到文件服务：fileName={}", file.getOriginalFilename());

        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小（从系统配置读取）
        long maxSize = getMaxFileSizeFromConfig();
        if (file.getSize() > maxSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename);

        // 检查文件类型（从系统配置读取）
        if (!StrUtil.isNotBlank(extension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED);
        }
        List<String> allowedTypes = getAllowedFileTypesFromConfig();
        if (!allowedTypes.contains(extension.toLowerCase())) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED);
        }

        try {
            // 调用文件服务上传文件
            log.info("调用文件服务上传文件：fileName={}", originalFilename);

            Result<FileUploadResponse> result = fileServiceFeignClient.uploadFile(
                    file, "document", 1, null);

            log.info("文件服务返回结果：result={}", result);

            if (result == null) {
                log.error("文件服务返回结果为null");
                throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
            }

            log.info("文件服务返回码：code={}, message={}", result.getCode(), result.getMessage());

            if (result.getCode() != 200) {
                log.error("文件服务返回错误码：code={}, message={}", result.getCode(), result.getMessage());
                throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
            }

            FileUploadResponse response = result.getData();

            if (response == null) {
                log.error("文件服务返回数据为null");
                throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
            }

            log.info("文件服务返回数据：response={}", response);

            // 获取文件URL，优先使用fileUrl字段（与FileInfoVO保持一致）
            String fileUrl = response.getFileUrl();

            // 如果fileUrl为空，尝试其他可能的字段
            if (StrUtil.isBlank(fileUrl)) {
                fileUrl = response.getPreviewUrl();
            }
            if (StrUtil.isBlank(fileUrl)) {
                fileUrl = response.getConvertedUrl();
            }

            // 如果所有URL字段都为空，但有文件ID，则构造URL
            if (StrUtil.isBlank(fileUrl) && response.getId() != null) {
                fileUrl = constructFileUrl(response.getId());
            }

            if (StrUtil.isBlank(fileUrl)) {
                log.error("文件服务返回的URL为空：response={}", response);
                throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
            }

            log.info("文件上传成功：fileName={}, url={}", originalFilename, fileUrl);

            // 返回文件访问URL
            return fileUrl;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败：fileName={}", originalFilename, e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 构造文件访问URL
     *
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    private String constructFileUrl(Long fileId) {
        // 这里可以根据实际需求构造URL，目前返回文件服务的下载接口
        return String.format("http://localhost:8084/files/download/%d", fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> uploadAndCreateDocument(MultipartFile file) {
        // 1. 参数校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename);

        if (!fileParserService.isSupported(extension)) {
            throw new BusinessException("不支持的文件格式: " + extension + "，支持的格式: pdf, docx, xlsx, pptx, txt, md");
        }

        log.info("上传并解析文件：name={}, size={}, ext={}", originalFilename, file.getSize(), extension);

        // 2. 解析文件内容（必须在上传之前：上传会通过 Feign 消费 InputStream）
        String parsedContent;
        try {
            parsedContent = fileParserService.parse(file);
        } catch (Exception e) {
            log.error("文件解析失败：name={}, error={}", originalFilename, e.getMessage(), e);
            throw new BusinessException("文件解析失败: " + e.getMessage());
        }

        // 3. 上传文件到 kb-file 服务
        String fileUrl = uploadDocumentFile(file);

        if (StrUtil.isBlank(parsedContent)) {
            throw new BusinessException("文件解析结果为空，请确认文件包含可提取的文本内容");
        }

        // 4. 构建文档标题（去掉扩展名）
        String title = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(0, originalFilename.lastIndexOf('.'))
                : originalFilename;

        // 5. 构建 DocumentDTO 并创建文档
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setTitle(title);
        documentDTO.setContent(parsedContent);
        documentDTO.setDocumentType(2);           // 文件类型
        documentDTO.setFileSize(file.getSize());
        documentDTO.setFileExtension(extension);
        documentDTO.setFilePath(fileUrl);
        documentDTO.setStatus(0);                 // 初始为草稿状态

        Long documentId = createDocument(documentDTO);

        // 6. 构建返回结果
        int previewLen = Math.min(200, parsedContent.length());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("documentId", documentId);
        result.put("title", title);
        result.put("fileUrl", fileUrl);
        result.put("fileSize", file.getSize());
        result.put("contentLength", parsedContent.length());
        result.put("contentPreview", parsedContent.substring(0, previewLen));

        log.info("文件解析并创建文档成功：documentId={}, title={}, ext={}, chars={}",
                documentId, title, extension, parsedContent.length());

        return result;
    }

    // ==================== RAG 自动索引 ====================

    /**
     * 异步触发RAG重建索引（不阻塞主流程）
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
     * 异步触发RAG删除索引（不阻塞主流程）
     */
    private void triggerRagDelete(Long docId) {
        CompletableFuture.runAsync(() -> {
            try {
                ragFeignClient.removeFromIndex(docId);
                log.info("已触发RAG删除索引：documentId={}", docId);
            } catch (Exception e) {
                log.warn("触发RAG删除索引失败（RAG服务不可用，不影响文档操作）：documentId={}, error={}",
                        docId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 异步触发KAG图谱构建（不阻塞主流程）
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
     * 异步触发KAG图谱删除（不阻塞主流程）
     */
    private void triggerKAGDelete(Long docId) {
        CompletableFuture.runAsync(() -> {
            try {
                kagFeignClient.deleteGraph(docId);
                log.info("已触发KAG图谱删除：documentId={}", docId);
            } catch (Exception e) {
                log.warn("触发KAG图谱删除失败（KAG服务不可用，不影响文档操作）：documentId={}, error={}",
                        docId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 同步触发Neo4j图谱删除（不阻塞主流程）
     *
     * <p>直接调用 kb-graph 服务的删除接口清理 Neo4j 中的文档图谱数据，
     * 作为 KAG RabbitMQ 异步删除的同步备份路径，确保文档删除后图谱数据被及时清理。</p>
     */
    private void triggerGraphDelete(Long docId) {
        CompletableFuture.runAsync(() -> {
            try {
                graphFeignClient.deleteDocumentGraph(docId);
                log.info("已触发Neo4j图谱删除：documentId={}", docId);
            } catch (Exception e) {
                log.warn("触发Neo4j图谱删除失败（kb-graph服务不可用，不影响文档操作）：documentId={}, error={}",
                        docId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    // ==================== ES搜索索引同步 ====================

    /**
     * 异步同步文档到ES搜索索引（不阻塞主流程）
     *
     * @param document 文档实体
     * @param content  文档内容（可选，从MongoDB获取）
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
     * 异步从ES搜索索引删除文档（不阻塞主流程）
     */
    private void triggerSearchDelete(Long docId) {
        CompletableFuture.runAsync(() -> {
            try {
                searchIndexFeignClient.deleteDocumentIndex(docId);
                log.info("已从ES搜索索引删除：documentId={}", docId);
            } catch (Exception e) {
                log.warn("从ES搜索索引删除失败（搜索服务不可用，不影响文档操作）：documentId={}, error={}",
                        docId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 构建搜索索引需要的文档数据Map
     */
    private Map<String, Object> buildSearchIndexData(Document document, String content) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", document.getId());
        data.put("title", document.getTitle());
        data.put("summary", document.getSummary());
        // 部分内容（前1000字符用于全文搜索）
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
                "username", document.getAuthorName() != null ? document.getAuthorName() : ""));
        data.put("publishTime", document.getPublishTime() != null ? document.getPublishTime().toString() : null);
        data.put("createdAt", document.getCreatedAt() != null ? document.getCreatedAt().toString() : null);
        data.put("updatedAt", document.getUpdatedAt() != null ? document.getUpdatedAt().toString() : null);

        // 查询分类名称
        if (document.getCategoryId() != null) {
            try {
                Category category = categoryMapper.selectById(document.getCategoryId());
                if (category != null) {
                    data.put("categoryName", category.getCategoryName());
                }
            } catch (Exception e) {
                // 忽略分类查询失败
            }
        }

        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeDocument(Long documentId) {
        log.info("点赞文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        Long userId = UserContext.getCurrentUserId();

        // 点赞（利用 tb_like 唯一约束防止并发重复点赞）
        likeService.like(documentId, userId, 1);

        // 增加点赞次数
        int updated = documentMapper.incrementLikeCount(documentId);

        // 发布点赞统计事件到 RabbitMQ
        statisticsEventPublisher.publishLikeEvent(userId, UserContext.getCurrentUserName(), documentId, document.getTitle());

        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlikeDocument(Long documentId) {
        log.info("取消点赞文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        Long userId = UserContext.getCurrentUserId();

        // 取消点赞
        likeService.unlike(documentId, userId, 1);

        // 减少点赞次数
        documentMapper.decrementLikeCount(documentId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean favoriteDocument(Long documentId) {
        log.info("收藏文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        // TODO: 检查用户是否已收藏

        // 增加收藏次数
        int count = documentMapper.incrementFavoriteCount(documentId);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publishDocument(Long documentId) {
        log.info("发布文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 检查系统配置：是否需要审核
        if (!checkRequireApproval()) {
            log.info("系统配置关闭了文档审核，直接发布文档：documentId={}", documentId);
            return directPublishDocument(documentId);
        }

        // 需要审核，提交审核流程
        log.info("提交文档审核：documentId={}", documentId);
        return documentReviewService.submitForReview(documentId);
    }

    /**
     * 检查系统配置：是否需要文档审核
     *
     * @return true-需要审核，false-直接发布
     */
    private boolean checkRequireApproval() {
        String value = systemConfigCache.getConfig("system.requireApproval");
        return !"false".equals(value);
    }

    /**
     * 从 kb_system_config 读取最大文件大小
     */
    private long getMaxFileSizeFromConfig() {
        String value = systemConfigCache.getConfig("file.upload.max.size");
        if (value != null) {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return 20971520L; // 默认20MB
    }

    /**
     * 从 kb_system_config 读取允许的文件类型列表
     */
    private List<String> getAllowedFileTypesFromConfig() {
        String value = systemConfigCache.getConfig("file.upload.allowed.types");
        if (value != null && !value.isBlank()) {
            return Arrays.asList(value.toLowerCase().split(","));
        }
        return Arrays.asList("pdf", "doc", "docx", "xlsx", "pptx", "txt", "md", "jpg", "png", "gif");
    }

    /**
     * 直接发布文档（无需审核）
     * <p>当 system.requireApproval 配置为 false 时调用此方法</p>
     */
    private Boolean directPublishDocument(Long documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

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
        triggerRagReindex(documentId);
        triggerKAGBuild(documentId);
        triggerSearchIndex(document, null);

        log.info("文档直接发布成功：documentId={}", documentId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean archiveDocument(Long documentId) {
        log.info("归档文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        Document document = new Document();
        document.setId(documentId);
        document.setStatus(2);

        int count = documentMapper.updateById(document);

        // 异步从RAG中删除该文档的向量索引（归档后不再可搜索）
        if (count > 0) {
            triggerRagDelete(documentId);
            triggerKAGDelete(documentId);
            triggerSearchDelete(documentId);
            triggerGraphDelete(documentId);
        }

        return count > 0;
    }

    @Override
    public int rebuildAllGraphs() {
        log.info("开始批量重建知识图谱...");
        // 查询所有已发布且未软删除的文档
        List<Document> publishedDocs = documentMapper.selectList(
                new LambdaQueryWrapper<Document>()
                        .eq(Document::getStatus, 1));
        log.info("找到 {} 篇已发布文档，开始逐篇重建图谱", publishedDocs.size());

        for (Document doc : publishedDocs) {
            try {
                triggerKAGBuild(doc.getId());
                log.info("已触发图谱重建：documentId={}, title={}", doc.getId(), doc.getTitle());
            } catch (Exception e) {
                log.warn("触发图谱重建失败：documentId={}, title={}, error={}",
                        doc.getId(), doc.getTitle(), e.getMessage());
            }
        }

        log.info("批量重建知识图谱请求已全部发送，共 {} 篇", publishedDocs.size());
        return publishedDocs.size();
    }

    @Override
    public int cleanupGraphGhostNodes() {
        log.info("开始清理知识图谱脏节点...");
        try {
            // 1. 从MySQL查询所有有效文档ID
            List<Document> allDocs = documentMapper.selectList(
                    new LambdaQueryWrapper<Document>().select(Document::getId));
            List<Long> validDocIds = allDocs.stream().map(Document::getId).collect(Collectors.toList());
            log.info("MySQL有效文档数量：{}", validDocIds.size());

            // 2. 调用kb-graph服务清理脏节点
            Map<String, List<Long>> body = Map.of("validDocIds", validDocIds);
            Result<String> result = graphFeignClient.cleanupDocumentGraph(body);
            log.info("图谱脏节点清理结果：{}", result != null ? result.getData() : null);
            return validDocIds.size();
        } catch (Exception e) {
            log.error("清理图谱脏节点失败：{}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 递归收集分类ID及其所有子分类ID
     */
    private List<Long> collectCategoryIds(Long parentId) {
        List<Long> ids = new ArrayList<>();
        ids.add(parentId);
        List<Category> children = categoryMapper.selectByParentId(parentId);
        for (Category child : children) {
            ids.addAll(collectCategoryIds(child.getId()));
        }
        return ids;
    }

    @Override
    public void dismissAutoSaveDrafts() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            log.warn("用户未登录，无法确认自动保存草稿");
            return;
        }

        int updated = documentMapper.update(null,
                new LambdaUpdateWrapper<Document>()
                        .eq(Document::getAuthorId, userId)
                        .eq(Document::getStatus, 0)
                        .set(Document::getAutoSaveDismissed, 1));
        log.info("用户 {} 确认放弃自动保存草稿，更新了 {} 条记录", userId, updated);
    }
}
