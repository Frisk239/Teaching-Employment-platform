package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Notification;
import com.teaching.employment.model.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息通知Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 分页查询通知列表（关联用户表）
     */
    IPage<NotificationDTO> selectNotificationPageWithUser(
            Page<NotificationDTO> page,
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("isRead") Integer isRead
    );
}
