package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.School;
import com.teaching.employment.mapper.SchoolMapper;
import com.teaching.employment.service.SchoolService;
import org.springframework.stereotype.Service;

/**
 * 学校Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {
}
