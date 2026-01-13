package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教室Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface ClassroomMapper extends BaseMapper<Classroom> {
}
