package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.document.dto.AutoSaveHistoryQueryDTO;
import com.knowledge.base.document.vo.AutoSaveHistoryVO;

/**
 * 自动保存历史服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface AutoSaveHistoryService {

    /**
     * 保存自动保存快照到MongoDB
     *
     * @param documentId 文档ID
     * @param title      文档标题
     * @param content    文档内容
     * @param authorId   用户ID
     */
    void saveSnapshot(Long documentId, String title, String content, Long authorId);

    /**
     * 分页查询指定文档的自动保存历史
     *
     * @param query 查询参数
     * @return 分页历史记录
     */
    IPage<AutoSaveHistoryVO> pageHistory(AutoSaveHistoryQueryDTO query);

    /**
     * 获取单个快照详情（含完整内容）
     *
     * @param snapshotId 快照ID
     * @param documentId 文档ID
     * @return 快照详情VO
     */
    AutoSaveHistoryVO getSnapshot(String snapshotId, Long documentId);

    /**
     * 软删除指定文档的所有自动保存历史
     *
     * @param documentId 文档ID
     */
    void deleteByDocumentId(Long documentId);
}
