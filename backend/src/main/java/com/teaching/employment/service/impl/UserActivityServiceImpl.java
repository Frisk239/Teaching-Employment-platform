package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.UserActivity;
import com.teaching.employment.mapper.UserActivityMapper;
import com.teaching.employment.service.UserActivityService;
import org.springframework.stereotype.Service;

/**
 * 用户活动记录Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
public class UserActivityServiceImpl extends ServiceImpl<UserActivityMapper, UserActivity> implements UserActivityService {
}
