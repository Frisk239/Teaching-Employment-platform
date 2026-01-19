package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.StudentSkill;
import com.teaching.employment.mapper.StudentSkillMapper;
import com.teaching.employment.service.StudentSkillService;
import org.springframework.stereotype.Service;

/**
 * 学员技能标签Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
public class StudentSkillServiceImpl extends ServiceImpl<StudentSkillMapper, StudentSkill> implements StudentSkillService {
}
