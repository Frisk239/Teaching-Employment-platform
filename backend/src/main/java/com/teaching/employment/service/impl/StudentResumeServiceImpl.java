package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.StudentResume;
import com.teaching.employment.mapper.StudentResumeMapper;
import com.teaching.employment.service.StudentResumeService;
import org.springframework.stereotype.Service;

/**
 * 学员简历Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
public class StudentResumeServiceImpl extends ServiceImpl<StudentResumeMapper, StudentResume> implements StudentResumeService {
}
