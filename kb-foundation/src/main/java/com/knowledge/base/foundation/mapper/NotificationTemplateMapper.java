package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.NotificationTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通知模板Mapper接口
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface NotificationTemplateMapper extends BaseMapper<NotificationTemplate> {

    @Select("SELECT * FROM kb_notification_template WHERE template_code = #{templateCode} AND is_active = 1")
    NotificationTemplate selectByTemplateCode(@Param("templateCode") String templateCode);

    @Select("SELECT * FROM kb_notification_template WHERE notification_type = #{notificationType} AND is_active = 1")
    List<NotificationTemplate> selectByNotificationType(@Param("notificationType") String notificationType);
}
