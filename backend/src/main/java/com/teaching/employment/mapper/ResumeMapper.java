package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

/**
 * 简历Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
}
