package com.knowledge.base.foundation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageParam;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.foundation.dto.NotificationDTO;
import com.knowledge.base.foundation.dto.NotificationQueryDTO;
import com.knowledge.base.foundation.entity.Notification;
import com.knowledge.base.foundation.mapper.NotificationMapper;
import com.knowledge.base.foundation.service.NotificationService;
import com.knowledge.base.foundation.vo.NotificationVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> sendNotification(NotificationDTO notificationDTO) {
        log.info("发送通知：userId={}, type={}", notificationDTO.getUserId(), notificationDTO.getNotificationType());

        if (notificationDTO.getUserId() == null) {
            throw new BusinessException("接收用户ID不能为空");
        }
        if (!StringUtils.hasText(notificationDTO.getNotificationType())) {
            throw new BusinessException("通知类型不能为空");
        }
        if (!StringUtils.hasText(notificationDTO.getTitle())) {
            throw new BusinessException("通知标题不能为空");
        }
        if (!StringUtils.hasText(notificationDTO.getContent())) {
            throw new BusinessException("通知内容不能为空");
        }

        Notification notification = new Notification();
        BeanUtil.copyProperties(notificationDTO, notification);
        notification.setId(SnowflakeIdGenerator.getInstance().nextId());

        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        notification.setCreatedAt(LocalDateTime.now());

        int count = notificationMapper.insert(notification);
        if (count <= 0) {
            throw new BusinessException("发送通知失败");
        }

        log.info("通知发送成功：notificationId={}", notification.getId());
        return Result.success(notification.getId());
    }

    /** {@inheritDoc} */
    @Override
    public IPage<Notification> pageNotifications(Long current, Long size, Long userId, Integer isRead) {
        log.info("分页查询通知：current={}, size={}, userId={}, isRead={}", current, size, userId, isRead);

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Notification::getUserId, userId);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }

        wrapper.orderByDesc(Notification::getCreatedAt);

        Page<Notification> page = new Page<>(current, size);
        return notificationMapper.selectPage(page, wrapper);
    }

    /** {@inheritDoc} */
    @Override
    public Result<IPage<NotificationVO>> getNotifications(NotificationQueryDTO queryDTO) {
        log.info("查询通知列表：userId={}", queryDTO.getUserId());

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        wrapper.eq(Notification::getUserId, queryDTO.getUserId());

        if (StringUtils.hasText(queryDTO.getNotificationType())) {
            wrapper.eq(Notification::getNotificationType, queryDTO.getNotificationType());
        }

        if (queryDTO.getIsRead() != null) {
            wrapper.eq(Notification::getIsRead, queryDTO.getIsRead());
        }

        if (StringUtils.hasText(queryDTO.getStartTime())) {
            wrapper.ge(Notification::getCreatedAt, queryDTO.getStartTime());
        }
        if (StringUtils.hasText(queryDTO.getEndTime())) {
            wrapper.le(Notification::getCreatedAt, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(Notification::getCreatedAt);

        PageParam pageParam = queryDTO;
        Page<Notification> page = new Page<>(pageParam.getCurrent(), pageParam.getSize());
        IPage<Notification> notificationPage = notificationMapper.selectPage(page, wrapper);

        IPage<NotificationVO> voPage = notificationPage.convert(notification -> BeanUtil.copyProperties(notification, NotificationVO.class));

        return Result.success(voPage);
    }

    /** {@inheritDoc} */
    @Override
    public Notification getNotificationById(Long id) {
        log.info("查询通知详情：id={}", id);
        return notificationMapper.selectById(id);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sendNotification(Notification notification) {
        log.info("发送通知：userId={}, title={}", notification.getUserId(), notification.getTitle());

        if (notification.getUserId() == null) {
            throw new BusinessException("接收用户ID不能为空");
        }

        notification.setId(SnowflakeIdGenerator.getInstance().nextId());

        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        notification.setCreatedAt(LocalDateTime.now());

        int count = notificationMapper.insert(notification);
        return count > 0;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> markAsRead(Long id) {
        log.info("标记通知已读：notificationId={}", id);

        if (id == null) {
            throw new BusinessException("通知ID不能为空");
        }

        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, id)
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadTime, LocalDateTime.now());

        int count = notificationMapper.update(null, updateWrapper);
        return Result.success(count > 0);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> markAllAsRead(Long userId) {
        log.info("标记所有通知已读：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadTime, LocalDateTime.now());

        int count = notificationMapper.update(null, updateWrapper);
        log.info("已标记{}条通知为已读", count);
        return Result.success(true);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteNotification(Long id) {
        log.info("删除通知：notificationId={}", id);

        if (id == null) {
            throw new BusinessException("通知ID不能为空");
        }

        int count = notificationMapper.deleteById(id);
        return Result.success(count > 0);
    }

    /** {@inheritDoc} */
    @Override
    public Result<Long> getUnreadCount(Long userId) {
        log.info("获取未读通知数量：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        Long count = notificationMapper.countUnreadByUserId(userId);
        return Result.success(count);
    }
}