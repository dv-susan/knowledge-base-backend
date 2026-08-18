package com.knowledge.base.document.service.impl;

import com.knowledge.base.document.entity.DocumentAccess;
import com.knowledge.base.document.mapper.DocumentAccessMapper;
import com.knowledge.base.document.service.DocumentAccessService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.DocumentAccessVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档访问记录服务实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现文档访问记录相关的业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentAccessServiceImpl implements DocumentAccessService {

    private final DocumentAccessMapper documentAccessMapper;

    /**
     * 默认查询数量限制
     */
    private static final int DEFAULT_LIMIT = 20;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordAccess(Long userId, Long documentId, String documentTitle) {
        if (userId == null) {
            log.warn("User not logged in, skipping access record");
            return;
        }

        // 先删除旧记录，确保唯一性
        documentAccessMapper.deleteByUserIdAndDocumentId(userId, documentId);

        // 插入新记录
        DocumentAccess access = new DocumentAccess();
        access.setUserId(userId);
        access.setDocumentId(documentId);
        access.setDocumentTitle(documentTitle);
        access.setAccessTime(LocalDateTime.now());
        documentAccessMapper.insert(access);

        log.debug("Recorded access for user {} to document {} ({})", userId, documentId, documentTitle);
    }

    @Override
    public List<DocumentAccessVO> getRecentAccess(Integer limit) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            log.warn("User not logged in, returning empty access list");
            return List.of();
        }

        int queryLimit = limit != null && limit > 0 ? limit : DEFAULT_LIMIT;
        List<DocumentAccess> accessList = documentAccessMapper.selectRecentAccessByUserId(userId, queryLimit);

        return accessList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccess(Long documentId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            log.warn("User not logged in, skipping delete access");
            return;
        }

        documentAccessMapper.deleteByUserIdAndDocumentId(userId, documentId);
        log.debug("Deleted access record for user {} to document {}", userId, documentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearAllAccess() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            log.warn("User not logged in, skipping clear access");
            return;
        }

        documentAccessMapper.deleteAllByUserId(userId);
        log.debug("Cleared all access records for user {}", userId);
    }

    /**
     * 将实体转换为VO
     */
    private DocumentAccessVO convertToVO(DocumentAccess access) {
        DocumentAccessVO vo = new DocumentAccessVO();
        vo.setId(access.getId());
        vo.setUserId(access.getUserId());
        vo.setDocumentId(access.getDocumentId());
        vo.setDocumentTitle(access.getDocumentTitle());
        vo.setAccessTime(access.getAccessTime());
        vo.setSummary(access.getSummary());
        vo.setCategoryName(access.getCategoryName());
        vo.setAuthorName(access.getAuthorName());
        vo.setStatus(access.getStatus());
        return vo;
    }
}
