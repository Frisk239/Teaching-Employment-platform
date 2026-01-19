package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.StudentProject;
import com.teaching.employment.mapper.StudentProjectMapper;
import com.teaching.employment.service.StudentProjectService;
import org.springframework.stereotype.Service;

/**
 * 学员项目经验Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
public class StudentProjectServiceImpl extends ServiceImpl<StudentProjectMapper, StudentProject> implements StudentProjectService {
}
