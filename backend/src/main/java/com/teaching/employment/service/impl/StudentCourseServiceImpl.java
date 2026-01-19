package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.StudentCourse;
import com.teaching.employment.mapper.StudentCourseMapper;
import com.teaching.employment.service.StudentCourseService;
import org.springframework.stereotype.Service;

/**
 * 学员课程成绩Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements StudentCourseService {
}
