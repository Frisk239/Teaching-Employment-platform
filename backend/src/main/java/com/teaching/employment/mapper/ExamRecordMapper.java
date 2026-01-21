package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试记录Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
}
