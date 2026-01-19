package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.UserActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户活动记录Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Mapper
public interface UserActivityMapper extends BaseMapper<UserActivity> {
}
