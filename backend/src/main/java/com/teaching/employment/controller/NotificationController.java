package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Notification;
import com.teaching.employment.model.dto.NotificationDTO;
import com.teaching.employment.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息通知管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/notification")
@Api(tags = "消息通知管理")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 分页查询通知列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询通知列表")
    public Result<IPage<NotificationDTO>> getNotificationPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("通知类型") @RequestParam(required = false) String type,
            @ApiParam("是否已读") @RequestParam(required = false) Integer isRead) {
        IPage<NotificationDTO> page = notificationService.getNotificationPageWithUser(current, size, userId, type, isRead);
        return Result.ok(page);
    }

    /**
     * 根据用户ID查询通知列表
     */
    @GetMapping("/user/{userId}")
    @ApiOperation("根据用户ID查询通知列表")
    public Result<IPage<Notification>> getNotificationsByUserId(
            @PathVariable Long userId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Notification> page = notificationService.getNotificationsByUserId(current, size, userId);
        return Result.ok(page);
    }

    /**
     * 获取用户未读通知数量
     */
    @GetMapping("/user/{userId}/unread-count")
    @ApiOperation("获取用户未读通知数量")
    public Result<Long> getUnreadCount(@PathVariable Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return Result.ok(count);
    }

    /**
     * 获取用户未读通知列表
     */
    @GetMapping("/user/{userId}/unread")
    @ApiOperation("获取用户未读通知列表")
    public Result<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return Result.ok(notifications);
    }

    /**
     * 根据ID查询通知
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询通知")
    public Result<Notification> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        return Result.ok(notification);
    }

    /**
     * 新增通知
     */
    @PostMapping
    @ApiOperation("新增通知")
    public Result<Void> createNotification(@RequestBody Notification notification) {
        // 默认未读
        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        boolean success = notificationService.save(notification);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新通知
     */
    @PutMapping
    @ApiOperation("更新通知")
    public Result<Void> updateNotification(@RequestBody Notification notification) {
        boolean success = notificationService.updateById(notification);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除通知")
    public Result<Void> deleteNotification(
            @PathVariable Long id,
            @ApiParam("用户ID") @RequestParam Long userId) {
        boolean success = notificationService.deleteNotification(id, userId);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 发送通知
     */
    @PostMapping("/send")
    @ApiOperation("发送通知")
    public Result<Void> sendNotification(
            @ApiParam("接收用户ID") @RequestParam Long userId,
            @ApiParam("通知类型") @RequestParam String type,
            @ApiParam("标题") @RequestParam String title,
            @ApiParam("内容") @RequestParam String content,
            @ApiParam("关联对象ID") @RequestParam(required = false) Long relatedId) {
        boolean success = notificationService.sendNotification(userId, type, title, content, relatedId);
        return success ? Result.ok("发送成功") : Result.error("发送失败");
    }

    /**
     * 批量发送通知
     */
    @PostMapping("/batch-send")
    @ApiOperation("批量发送通知")
    public Result<Void> batchSendNotification(
            @ApiParam("接收用户ID列表") @RequestParam List<Long> userIds,
            @ApiParam("通知类型") @RequestParam String type,
            @ApiParam("标题") @RequestParam String title,
            @ApiParam("内容") @RequestParam String content,
            @ApiParam("关联对象ID") @RequestParam(required = false) Long relatedId) {
        boolean success = notificationService.batchSendNotification(userIds, type, title, content, relatedId);
        return success ? Result.ok("批量发送成功") : Result.error("批量发送失败");
    }

    /**
     * 发送系统公告
     */
    @PostMapping("/send-notice")
    @ApiOperation("发送系统公告")
    public Result<Void> sendSystemNotice(
            @ApiParam("标题") @RequestParam String title,
            @ApiParam("内容") @RequestParam String content) {
        boolean success = notificationService.sendSystemNotice(title, content);
        return success ? Result.ok("发送成功") : Result.error("发送失败");
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/{id}/mark-read")
    @ApiOperation("标记通知为已读")
    public Result<Void> markAsRead(
            @PathVariable Long id,
            @ApiParam("用户ID") @RequestParam Long userId) {
        boolean success = notificationService.markAsRead(id, userId);
        return success ? Result.ok("标记成功") : Result.error("标记失败");
    }

    /**
     * 批量标记通知为已读
     */
    @PostMapping("/batch-mark-read")
    @ApiOperation("批量标记通知为已读")
    public Result<Void> batchMarkAsRead(
            @ApiParam("通知ID列表") @RequestParam List<Long> notificationIds,
            @ApiParam("用户ID") @RequestParam Long userId) {
        boolean success = notificationService.batchMarkAsRead(notificationIds, userId);
        return success ? Result.ok("批量标记成功") : Result.error("批量标记失败");
    }

    /**
     * 标记所有通知为已读
     */
    @PostMapping("/user/{userId}/mark-all-read")
    @ApiOperation("标记所有通知为已读")
    public Result<Void> markAllAsRead(@PathVariable Long userId) {
        boolean success = notificationService.markAllAsRead(userId);
        return success ? Result.ok("标记成功") : Result.error("标记失败");
    }

    /**
     * 清空所有已读通知
     */
    @PostMapping("/user/{userId}/clear-read")
    @ApiOperation("清空所有已读通知")
    public Result<Void> clearReadNotifications(@PathVariable Long userId) {
        boolean success = notificationService.clearReadNotifications(userId);
        return success ? Result.ok("清空成功") : Result.error("清空失败");
    }
}
