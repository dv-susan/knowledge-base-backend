package com.knowledge.base.document.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.knowledge.base.document.dto.AutoSaveHistoryQueryDTO;
import com.knowledge.base.document.entity.mongodb.AutoSaveHistory;
import com.knowledge.base.document.repository.mongodb.AutoSaveHistoryRepository;
import com.knowledge.base.document.service.AutoSaveHistoryService;
import com.knowledge.base.document.vo.AutoSaveHistoryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.knowledge.base.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 自动保存历史服务实现
 *
 * <p>管理MongoDB中的自动保存快照记录</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class AutoSaveHistoryServiceImpl implements AutoSaveHistoryService {

    @Resource
    private AutoSaveHistoryRepository autoSaveHistoryRepository;

    @Override
    public void saveSnapshot(Long documentId, String title, String content, Long authorId) {
        if (content == null || content.isBlank()) {
            return;
        }

        try {
            String contentPreview = content.length() > 200
                    ? content.substring(0, 200)
                    : content;

            AutoSaveHistory snapshot = AutoSaveHistory.builder()
                    .documentId(documentId)
                    .title(title != null && !title.isBlank() ? title : "未命名文档")
                    .content(content)
                    .contentPreview(contentPreview)
                    .contentLength(content.length())
                    .authorId(authorId)
                    .savedAt(LocalDateTime.now())
                    .deleted(false)
                    .build();

            autoSaveHistoryRepository.save(snapshot);
            log.debug("自动保存快照已存储：documentId={}, contentLength={}", documentId, content.length());
        } catch (Exception e) {
            log.warn("保存自动保存快照失败，不影响主流程：documentId={}, error={}", documentId, e.getMessage());
        }
    }

    @Override
    public IPage<AutoSaveHistoryVO> pageHistory(AutoSaveHistoryQueryDTO query) {
        long current = query.getCurrent() != null ? query.getCurrent() : 1L;
        long size = query.getSize() != null ? query.getSize() : 20L;

        org.springframework.data.domain.Page<AutoSaveHistory> mongoPage =
                autoSaveHistoryRepository.findByDocumentIdAndDeletedFalseOrderBySavedAtDesc(
                        query.getDocumentId(),
                        PageRequest.of((int) (current - 1), (int) size, Sort.by(Sort.Direction.DESC, "savedAt"))
                );

        List<AutoSaveHistoryVO> voList = new ArrayList<>();
        for (AutoSaveHistory entity : mongoPage.getContent()) {
            AutoSaveHistoryVO vo = new AutoSaveHistoryVO();
            vo.setId(entity.getId());
            vo.setDocumentId(entity.getDocumentId());
            vo.setTitle(entity.getTitle());
            vo.setContentPreview(entity.getContentPreview());
            vo.setContentLength(entity.getContentLength());
            vo.setSavedAt(entity.getSavedAt());
            voList.add(vo);
        }

        IPage<AutoSaveHistoryVO> result = new Page<>(current, size, mongoPage.getTotalElements());
        result.setRecords(voList);
        return result;
    }

    @Override
    public AutoSaveHistoryVO getSnapshot(String snapshotId, Long documentId) {
        Optional<AutoSaveHistory> opt = autoSaveHistoryRepository.findById(snapshotId);
        AutoSaveHistory entity = opt.orElseThrow(() ->
                new BusinessException("自动保存快照不存在"));
        // 校验归属，防止跨文档越权读取
        if (!entity.getDocumentId().equals(documentId) || Boolean.TRUE.equals(entity.getDeleted())) {
            throw new BusinessException("自动保存快照不存在");
        }

        AutoSaveHistoryVO vo = new AutoSaveHistoryVO();
        vo.setId(entity.getId());
        vo.setDocumentId(entity.getDocumentId());
        vo.setTitle(entity.getTitle());
        vo.setContentPreview(entity.getContentPreview());
        vo.setContent(entity.getContent());
        vo.setContentLength(entity.getContentLength());
        vo.setSavedAt(entity.getSavedAt());
        return vo;
    }

    @Override
    public void deleteByDocumentId(Long documentId) {
        try {
            org.springframework.data.domain.Page<AutoSaveHistory> page =
                    autoSaveHistoryRepository.findByDocumentIdAndDeletedFalseOrderBySavedAtDesc(
                            documentId,
                            PageRequest.of(0, 1000)
                    );
            for (AutoSaveHistory entity : page.getContent()) {
                entity.setDeleted(true);
                autoSaveHistoryRepository.save(entity);
            }
            log.debug("已软删除文档的自动保存历史：documentId={}, count={}", documentId, page.getTotalElements());
        } catch (Exception e) {
            log.warn("删除自动保存历史失败：documentId={}, error={}", documentId, e.getMessage());
        }
    }
}
