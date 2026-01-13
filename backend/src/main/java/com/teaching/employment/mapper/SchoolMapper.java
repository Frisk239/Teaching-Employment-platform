package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.School;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学校Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {
}
