package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Position;
import org.apache.ibatis.annotations.Mapper;

/**
 * 招聘职位Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface PositionMapper extends BaseMapper<Position> {
}
