package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Notification;

import java.util.List;

/**
 * 消息通知Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 分页查询通知列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param userId   用户ID
     * @param type     通知类型
     * @param isRead   是否已读
     * @return 通知分页数据
     */
    IPage<Notification> getNotificationPage(Integer current, Integer size, Long userId, String type, Integer isRead);

    /**
     * 根据用户ID查询通知列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param userId  用户ID
     * @return 通知分页数据
     */
    IPage<Notification> getNotificationsByUserId(Integer current, Integer size, Long userId);

    /**
     * 获取用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 获取用户未读通知列表
     *
     * @param userId 用户ID
     * @return 未读通知列表
     */
    List<Notification> getUnreadNotifications(Long userId);

    /**
     * 发送通知
     *
     * @param userId   接收用户ID
     * @param type     通知类型
     * @param title    标题
     * @param content  内容
     * @param relatedId 关联对象ID
     * @return 是否成功
     */
    boolean sendNotification(Long userId, String type, String title, String content, Long relatedId);

    /**
     * 批量发送通知
     *
     * @param userIds  接收用户ID列表
     * @param type     通知类型
     * @param title    标题
     * @param content  内容
     * @param relatedId 关联对象ID
     * @return 是否成功
     */
    boolean batchSendNotification(List<Long> userIds, String type, String title, String content, Long relatedId);

    /**
     * 标记通知为已读
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     * @return 是否成功
     */
    boolean markAsRead(Long notificationId, Long userId);

    /**
     * 批量标记通知为已读
     *
     * @param notificationIds 通知ID列表
     * @param userId          用户ID
     * @return 是否成功
     */
    boolean batchMarkAsRead(List<Long> notificationIds, Long userId);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long userId);

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     * @return 是否成功
     */
    boolean deleteNotification(Long notificationId, Long userId);

    /**
     * 清空所有已读通知
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearReadNotifications(Long userId);

    /**
     * 发送系统公告
     *
     * @param title   标题
     * @param content 内容
     * @return 是否成功
     */
    boolean sendSystemNotice(String title, String content);
}
