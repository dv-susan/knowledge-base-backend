package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    /**
     * 根据目标ID、用户ID、目标类型删除点赞记录
     *
     * @param targetId   目标ID
     * @param userId     用户ID
     * @param targetType 目标类型
     * @return 删除行数
     */
    int deleteByTargetAndUser(@Param("targetId") Long targetId,
                               @Param("userId") Long userId,
                               @Param("targetType") Integer targetType);
}
