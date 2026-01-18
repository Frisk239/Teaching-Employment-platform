package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Education;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教育经历Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
@Mapper
public interface EducationMapper extends BaseMapper<Education> {
}
