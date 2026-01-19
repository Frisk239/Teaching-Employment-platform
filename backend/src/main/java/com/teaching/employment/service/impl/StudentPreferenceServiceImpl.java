package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.StudentPreference;
import com.teaching.employment.mapper.StudentPreferenceMapper;
import com.teaching.employment.service.StudentPreferenceService;
import org.springframework.stereotype.Service;

/**
 * 学员求职偏好Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
public class StudentPreferenceServiceImpl extends ServiceImpl<StudentPreferenceMapper, StudentPreference> implements StudentPreferenceService {
}
