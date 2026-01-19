package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.StudentEducation;
import com.teaching.employment.mapper.StudentEducationMapper;
import com.teaching.employment.service.StudentEducationService;
import org.springframework.stereotype.Service;

/**
 * 学员教育经历Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
public class StudentEducationServiceImpl extends ServiceImpl<StudentEducationMapper, StudentEducation> implements StudentEducationService {
}
