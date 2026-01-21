package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Exam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
}

