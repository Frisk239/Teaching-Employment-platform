package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.StudentResume;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学员简历Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Mapper
public interface StudentResumeMapper extends BaseMapper<StudentResume> {
}
