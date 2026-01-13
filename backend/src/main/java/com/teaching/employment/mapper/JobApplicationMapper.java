package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.JobApplication;
import org.apache.ibatis.annotations.Mapper;

/**
 * 求职申请Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {
}
