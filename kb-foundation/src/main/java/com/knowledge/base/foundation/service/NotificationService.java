package com.knowledge.base.foundation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.foundation.dto.NotificationDTO;
import com.knowledge.base.foundation.dto.NotificationQueryDTO;
import com.knowledge.base.foundation.entity.Notification;
import com.knowledge.base.foundation.vo.NotificationVO;

/**
 * 通知Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface NotificationService {

    /**
     * 发送通知（基于DTO）
     *
     * @param notificationDTO 通知DTO
     * @return 通知ID
     */
    Result<Long> sendNotification(NotificationDTO notificationDTO);

    /**
     * 分页查询通知
     *
     * @param current 当前页
     * @param size    每页大小
     * @param userId  用户ID
     * @param isRead  是否已读（可选）
     * @return 分页结果
     */
    IPage<Notification> pageNotifications(Long current, Long size, Long userId, Integer isRead);

    /**
     * 查询通知列表（基于查询DTO）
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Result<IPage<NotificationVO>> getNotifications(NotificationQueryDTO queryDTO);

    /**
     * 根据ID获取通知
     *
     * @param id 通知ID
     * @return 通知信息
     */
    Notification getNotificationById(Long id);

    /**
     * 发送通知（基于实体对象，内部调用）
     *
     * @param notification 通知实体
     * @return 是否成功
     */
    Boolean sendNotification(Notification notification);

    /**
     * 标记通知为已读
     *
     * @param id 通知ID
     * @return 是否成功
     */
    Result<Boolean> markAsRead(Long id);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    Result<Boolean> markAllAsRead(Long userId);

    /**
     * 删除通知
     *
     * @param id 通知ID
     * @return 是否成功
     */
    Result<Boolean> deleteNotification(Long id);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Result<Long> getUnreadCount(Long userId);
}
