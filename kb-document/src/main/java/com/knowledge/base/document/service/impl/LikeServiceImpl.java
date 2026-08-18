package com.knowledge.base.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.document.entity.Like;
import com.knowledge.base.document.mapper.LikeMapper;
import com.knowledge.base.document.service.LikeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 点赞服务实现
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private LikeMapper likeMapper;

    @Override
    public void like(Long targetId, Long userId, Integer targetType) {
        Like like = new Like();
        like.setTargetId(targetId);
        like.setUserId(userId);
        like.setTargetType(targetType);

        try {
            likeMapper.insert(like);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已经点赞过了");
        }
    }

    @Override
    public void unlike(Long targetId, Long userId, Integer targetType) {
        int count = likeMapper.deleteByTargetAndUser(targetId, userId, targetType);
        if (count == 0) {
            throw new BusinessException("尚未点赞，无法取消");
        }
    }

    @Override
    public boolean isLiked(Long targetId, Long userId, Integer targetType) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getTargetId, targetId)
               .eq(Like::getUserId, userId)
               .eq(Like::getTargetType, targetType);
        return likeMapper.selectCount(wrapper) > 0;
    }
}
