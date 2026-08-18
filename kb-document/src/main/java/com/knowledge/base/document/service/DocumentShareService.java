package com.knowledge.base.document.service;

import com.knowledge.base.document.dto.ShareDTO;
import com.knowledge.base.document.vo.ShareVO;

import java.util.List;

/**
 * 文档分享服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface DocumentShareService {

    /**
     * 创建分享链接
     *
     * @param shareDTO 分享参数
     * @return 分享信息
     */
    ShareVO createShare(ShareDTO shareDTO);

    /**
     * 通过分享ID获取分享信息
     *
     * @param shareId 分享ID
     * @return 分享信息
     */
    ShareVO getShareById(String shareId);

    /**
     * 验证分享访问权限
     *
     * @param shareId 分享ID
     * @param password 访问密码（可选）
     * @return 是否有权访问
     */
    boolean verifyShareAccess(String shareId, String password);

    /**
     * 访问分享链接
     *
     * @param shareId 分享ID
     * @param password 访问密码（可选）
     * @return 文档ID
     */
    Long accessShare(String shareId, String password);

    /**
     * 获取文档的所有分享链接
     *
     * @param documentId 文档ID
     * @return 分享列表
     */
    List<ShareVO> getSharesByDocumentId(Long documentId);

    /**
     * 获取当前用户的分享列表
     *
     * @return 分享列表
     */
    List<ShareVO> getMyShares();

    /**
     * 删除分享链接
     *
     * @param shareId 分享ID
     * @return 是否成功
     */
    boolean deleteShare(String shareId);

    /**
     * 批量删除分享链接
     *
     * @param shareIds 分享ID列表
     * @return 删除数量
     */
    int batchDeleteShares(List<String> shareIds);

    /**
     * 更新分享设置
     *
     * @param shareId 分享ID
     * @param shareDTO 更新参数
     * @return 是否成功
     */
    boolean updateShare(String shareId, ShareDTO shareDTO);
}