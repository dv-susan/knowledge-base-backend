package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.document.entity.UserFavorite;
import com.knowledge.base.document.vo.UserFavoriteVO;

import java.util.List;

/**
 * 用户收藏Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface UserFavoriteService extends IService<UserFavorite> {

    /**
     * 添加收藏
     *
     * @param userId 用户ID
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean addFavorite(Long userId, Long documentId);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param documentId 文档ID
     * @return 是否成功
     */
    Boolean removeFavorite(Long userId, Long documentId);

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param documentId 文档ID
     * @return 是否已收藏
     */
    Boolean isFavorited(Long userId, Long documentId);

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserFavoriteVO> getUserFavorites(Long userId);

    /**
     * 获取文档收藏数量
     *
     * @param documentId 文档ID
     * @return 收藏数量
     */
    Long getFavoriteCount(Long documentId);

    /**
     * 切换收藏状态
     *
     * @param userId 用户ID
     * @param documentId 文档ID
     * @return 收藏状态（true-已收藏，false-未收藏）
     */
    Boolean toggleFavorite(Long userId, Long documentId);
}
