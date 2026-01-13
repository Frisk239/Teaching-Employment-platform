package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Notification;
import com.teaching.employment.entity.User;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.NotificationMapper;
import com.teaching.employment.service.NotificationService;
import com.teaching.employment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息通知Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserService userService;

    @Override
    public IPage<Notification> getNotificationPage(Integer current, Integer size, Long userId, String type, Integer isRead) {
        Page<Notification> page = new Page<>(current, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Notification::getUserId, userId);
        }
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(Notification::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }

        wrapper.orderByDesc(Notification::getCreateTime);

        return notificationMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Notification> getNotificationsByUserId(Integer current, Integer size, Long userId) {
        return getNotificationPage(current, size, userId, null, null);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getIsRead, 0);

        return notificationMapper.selectCount(wrapper);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getIsRead, 0);
        wrapper.orderByDesc(Notification::getCreateTime);
        wrapper.last("LIMIT 10"); // 最多返回10条

        return notificationMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendNotification(Long userId, String type, String title, String content, Long relatedId) {
        // 验证用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);

        return notificationMapper.insert(notification) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSendNotification(List<Long> userIds, String type, String title, String content, Long relatedId) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }

        List<Notification> notifications = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userService.getById(userId);
            if (user != null) {
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setType(type);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setRelatedId(relatedId);
                notification.setIsRead(0);
                notifications.add(notification);
            }
        }

        if (notifications.isEmpty()) {
            return false;
        }

        // 批量插入
        for (Notification notification : notifications) {
            notificationMapper.insert(notification);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }

        if (notification.getIsRead() == 1) {
            return true; // 已是已读状态
        }

        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());

        return notificationMapper.updateById(notification) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkAsRead(List<Long> notificationIds, Long userId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return false;
        }

        boolean allSuccess = true;
        for (Long notificationId : notificationIds) {
            try {
                markAsRead(notificationId, userId);
            } catch (Exception e) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getIsRead, 0);

        List<Notification> unreadNotifications = notificationMapper.selectList(wrapper);
        if (unreadNotifications.isEmpty()) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(1);
            notification.setReadTime(now);
            notificationMapper.updateById(notification);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }

        return notificationMapper.deleteById(notificationId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearReadNotifications(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getIsRead, 1);

        List<Notification> readNotifications = notificationMapper.selectList(wrapper);
        for (Notification notification : readNotifications) {
            notificationMapper.deleteById(notification.getId());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendSystemNotice(String title, String content) {
        // 获取所有用户
        List<User> allUsers = userService.list();

        if (allUsers.isEmpty()) {
            return false;
        }

        List<Long> userIds = new ArrayList<>();
        for (User user : allUsers) {
            userIds.add(user.getId());
        }

        return batchSendNotification(userIds, "notice", title, content, null);
    }
}
