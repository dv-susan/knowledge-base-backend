package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通知Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Select("SELECT COUNT(*) FROM kb_notification WHERE user_id = #{userId} AND is_read = 0 AND deleted = 0")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM kb_notification WHERE user_id = #{userId} AND deleted = 0 ORDER BY created_at DESC")
    List<Notification> selectByUserId(@Param("userId") Long userId);
}