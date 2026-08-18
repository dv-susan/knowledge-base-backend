package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.document.dto.DocumentVersionRestoreDTO;
import com.knowledge.base.document.vo.DocumentVersionVO;

import java.util.List;

/**
 * 文档版本管理服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface DocumentVersionService {

    /**
     * 创建文档版本
     *
     * @param documentId    文档ID
     * @param changeDescription 变更说明
     * @param userId        用户ID
     * @return 是否成功
     */
    boolean createVersion(Long documentId, String changeDescription, Long userId);

    /**
     * 获取文档版本列表
     *
     * @param documentId 文档ID
     * @param current    当前页
     * @param size       每页大小
     * @return 版本列表
     */
    IPage<DocumentVersionVO> getVersionList(Long documentId, Long current, Long size);

    /**
     * 获取版本详情
     *
     * @param versionId 版本ID
     * @return 版本详情
     */
    DocumentVersionVO getVersionDetail(Long versionId);

    /**
     * 恢复版本
     *
     * @param documentId 文档ID
     * @param restoreDTO 恢复参数
     * @param userId     用户ID
     * @return 是否成功
     */
    boolean restoreVersion(Long documentId, DocumentVersionRestoreDTO restoreDTO, Long userId);

    /**
     * 对比版本差异
     *
     * @param versionId1 版本ID1
     * @param versionId2 版本ID2
     * @return 差异内容
     */
    String compareVersions(Long versionId1, Long versionId2);

    /**
     * 删除版本
     *
     * @param versionId 版本ID
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean deleteVersion(Long versionId, Long userId);
}
