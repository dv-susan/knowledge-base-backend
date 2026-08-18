package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.document.dto.DocumentReviewDTO;
import com.knowledge.base.document.dto.ReviewQueryDTO;
import com.knowledge.base.document.entity.DocumentReview;
import com.knowledge.base.document.vo.DocumentReviewVO;

import java.util.List;
import java.util.Map;

/**
 * 文档审核Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface DocumentReviewService extends IService<DocumentReview> {

    /**
     * 提交审核
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean submitForReview(Long documentId);

    /**
     * 审核通过
     *
     * @param dto 审核DTO
     * @return 是否成功
     */
    Boolean approveReview(DocumentReviewDTO dto);

    /**
     * 审核驳回
     *
     * @param dto 审核DTO
     * @return 是否成功
     */
    Boolean rejectReview(DocumentReviewDTO dto);

    /**
     * 获取待审核文档列表
     *
     * @param dto 查询DTO
     * @return 分页结果
     */
    PageResult<DocumentReviewVO> getPendingReviews(ReviewQueryDTO dto);

    /**
     * 获取文档当前审核任务。
     *
     * @param documentId 文档ID
     * @return 当前审核任务
     */
    DocumentReviewVO getCurrentReviewTask(Long documentId);

    /**
     * 获取文档审核历史
     *
     * @param documentId 文档ID
     * @return 审核历史列表
     */
    List<DocumentReviewVO> getDocumentReviewHistory(Long documentId);

    /**
     * 获取待审核文档数量
     *
     * @return 待审核数量
     */
    Long getPendingCount();

    /**
     * 获取审核统计数据（待审核、已通过、已驳回数量）
     *
     * @return 统计数据 Map：pending/approved/rejected
     */
    Map<String, Long> getReviewStats();

    /**
     * 批量审核（通过或驳回）
     *
     * @param taskIds 审核任务ID列表
     * @param status  审核结果：approved-通过，rejected-驳回
     * @param comment 审核意见
     */
    void batchReview(List<Long> taskIds, String status, String comment);
}
