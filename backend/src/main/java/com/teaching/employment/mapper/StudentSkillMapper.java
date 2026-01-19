package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.StudentSkill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学员技能标签Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Mapper
public interface StudentSkillMapper extends BaseMapper<StudentSkill> {
}
