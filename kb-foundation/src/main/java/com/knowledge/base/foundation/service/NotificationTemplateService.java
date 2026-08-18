package com.knowledge.base.foundation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.foundation.entity.NotificationTemplate;

import java.util.List;

/**
 * 通知模板Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface NotificationTemplateService {

    /**
     * 分页查询模板列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param notificationType 通知类型（可选）
     * @return 分页结果
     */
    IPage<NotificationTemplate> pageTemplates(Long current, Long size, String notificationType);

    /**
     * 获取所有启用的模板
     *
     * @return 模板列表
     */
    List<NotificationTemplate> listActiveTemplates();

    /**
     * 根据ID获取模板
     *
     * @param id 模板ID
     * @return 模板信息
     */
    NotificationTemplate getTemplateById(Long id);

    /**
     * 创建模板
     *
     * @param template 模板实体
     * @return 是否成功
     */
    Boolean createTemplate(NotificationTemplate template);

    /**
     * 更新模板
     *
     * @param template 模板实体
     * @return 是否成功
     */
    Boolean updateTemplate(NotificationTemplate template);

    /**
     * 删除模板
     *
     * @param id 模板ID
     * @return 是否成功
     */
    Boolean deleteTemplate(Long id);

    /**
     * 测试发送
     *
     * @param id 模板ID
     * @param target 测试目标（邮箱/手机号）
     * @return 是否成功
     */
    Boolean testTemplate(Long id, String target);
}
