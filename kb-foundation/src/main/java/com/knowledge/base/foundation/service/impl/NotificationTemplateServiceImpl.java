package com.knowledge.base.foundation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.foundation.entity.NotificationTemplate;
import com.knowledge.base.foundation.mapper.NotificationTemplateMapper;
import com.knowledge.base.foundation.service.NotificationTemplateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知模板Service实现
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class NotificationTemplateServiceImpl extends ServiceImpl<NotificationTemplateMapper, NotificationTemplate>
        implements NotificationTemplateService {

    @Resource
    private NotificationTemplateMapper templateMapper;

    @Override
    public IPage<NotificationTemplate> pageTemplates(Long current, Long size, String notificationType) {
        Page<NotificationTemplate> page = new Page<>(current, size);
        LambdaQueryWrapper<NotificationTemplate> wrapper = new LambdaQueryWrapper<>();

        if (notificationType != null && !notificationType.isEmpty() && !"all".equals(notificationType)) {
            wrapper.eq(NotificationTemplate::getNotificationType, notificationType);
        }

        wrapper.orderByDesc(NotificationTemplate::getUpdatedAt);
        return templateMapper.selectPage(page, wrapper);
    }

    @Override
    public List<NotificationTemplate> listActiveTemplates() {
        LambdaQueryWrapper<NotificationTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationTemplate::getIsActive, 1);
        wrapper.orderByDesc(NotificationTemplate::getUpdatedAt);
        return templateMapper.selectList(wrapper);
    }

    @Override
    public NotificationTemplate getTemplateById(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createTemplate(NotificationTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        return templateMapper.insert(template) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTemplate(NotificationTemplate template) {
        template.setUpdatedAt(LocalDateTime.now());
        return templateMapper.updateById(template) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTemplate(Long id) {
        return templateMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean testTemplate(Long id, String target) {
        NotificationTemplate template = templateMapper.selectById(id);
        if (template == null) {
            log.warn("测试发送失败：模板不存在, id={}", id);
            return false;
        }

        // 简化实现：模拟发送通知
        log.info("测试发送通知模板：templateCode={}, type={}, target={}, title={}",
                template.getTemplateCode(), template.getNotificationType(), target, template.getTitle());

        // TODO: 根据通知类型实际发送（EMAIL/SMS/WECHAT/SYSTEM/BROWSER）
        return true;
    }
}
