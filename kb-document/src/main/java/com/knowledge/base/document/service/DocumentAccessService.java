package com.knowledge.base.document.service;

import com.knowledge.base.document.vo.DocumentAccessVO;

import java.util.List;

/**
 * 文档访问记录服务接口
 *
 * <p>按照阿里巴巴Java开发规范设计，提供文档访问记录相关的业务操作</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface DocumentAccessService {

    /**
     * 记录文档访问
     *
     * @param userId        用户ID
     * @param documentId    文档ID
     * @param documentTitle 文档标题
     */
    void recordAccess(Long userId, Long documentId, String documentTitle);

    /**
     * 获取用户最近访问记录
     *
     * @param limit 查询数量限制
     * @return 访问记录列表
     */
    List<DocumentAccessVO> getRecentAccess(Integer limit);

    /**
     * 删除单条访问记录
     *
     * @param documentId 文档ID
     */
    void deleteAccess(Long documentId);

    /**
     * 清空用户所有访问记录
     */
    void clearAllAccess();
}
