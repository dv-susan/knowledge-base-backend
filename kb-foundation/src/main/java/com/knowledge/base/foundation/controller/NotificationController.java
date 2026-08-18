package com.knowledge.base.foundation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.foundation.entity.Notification;
import com.knowledge.base.foundation.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notifications")
@Tag(name = "通知管理", description = "系统通知管理接口")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "分页查询通知", description = "分页查询通知列表")
    public Result<IPage<Notification>> pageNotifications(
        @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
        @Parameter(description = "是否已读") @RequestParam(required = false) Integer isRead) {
        Long userId = UserContextUtil.getUserId();
        log.info("分页查询通知请求：current={}, size={}, userId={}, isRead={}", current, size, userId, isRead);

        IPage<Notification> page = notificationService.pageNotifications(current, size, userId, isRead);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询通知详情", description = "根据通知ID查询通知详情")
    public Result<Notification> getNotificationById(
        @Parameter(description = "通知ID", required = true)
        @PathVariable Long id) {
        log.info("查询通知详情请求：id={}", id);

        Notification notification = notificationService.getNotificationById(id);
        return Result.success(notification);
    }

    @PostMapping
    @Operation(summary = "发送通知", description = "创建新通知")
    public Result<Boolean> sendNotification(@Valid @RequestBody Notification notification) {
        log.info("发送通知请求：userId={}, title={}", notification.getUserId(), notification.getTitle());

        Boolean success = notificationService.sendNotification(notification);
        return Result.success("发送通知成功", success);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记已读", description = "标记通知为已读")
    public Result<Boolean> markAsRead(
        @Parameter(description = "通知ID", required = true)
        @PathVariable Long id) {
        log.info("标记通知已读请求：id={}", id);

        return notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记已读", description = "将用户所有未读通知标记为已读")
    public Result<Boolean> markAllAsRead() {
        Long userId = UserContextUtil.getUserId();
        log.info("全部标记已读请求：userId={}", userId);

        return notificationService.markAllAsRead(userId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知", description = "根据通知ID删除通知")
    public Result<Boolean> deleteNotification(
        @Parameter(description = "通知ID", required = true)
        @PathVariable Long id) {
        log.info("删除通知请求：id={}", id);

        return notificationService.deleteNotification(id);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取未读数量", description = "获取用户未读通知数量")
    public Result<Long> getUnreadCount() {
        Long userId = UserContextUtil.getUserId();
        log.info("获取未读数量请求：userId={}", userId);

        return notificationService.getUnreadCount(userId);
    }
}