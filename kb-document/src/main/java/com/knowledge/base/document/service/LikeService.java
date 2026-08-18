package com.knowledge.base.document.service;

/**
 * 点赞服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface LikeService {

    /**
     * 点赞
     *
     * @param targetId   目标ID（文档ID或评论ID）
     * @param userId     用户ID
     * @param targetType 目标类型：1-文档，2-评论
     * @throws com.knowledge.base.common.exception.BusinessException 已经点赞过时抛出
     */
    void like(Long targetId, Long userId, Integer targetType);

    /**
     * 取消点赞
     *
     * @param targetId   目标ID（文档ID或评论ID）
     * @param userId     用户ID
     * @param targetType 目标类型：1-文档，2-评论
     * @throws com.knowledge.base.common.exception.BusinessException 尚未点赞时抛出
     */
    void unlike(Long targetId, Long userId, Integer targetType);

    /**
     * 查询是否已点赞
     *
     * @param targetId   目标ID（文档ID或评论ID）
     * @param userId     用户ID
     * @param targetType 目标类型：1-文档，2-评论
     * @return true-已点赞，false-未点赞
     */
    boolean isLiked(Long targetId, Long userId, Integer targetType);
}
