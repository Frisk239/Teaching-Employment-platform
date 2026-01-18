package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Education;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.EducationMapper;
import com.teaching.employment.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教育经历Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
@Service
@RequiredArgsConstructor
public class EducationServiceImpl extends ServiceImpl<EducationMapper, Education> implements EducationService {

    private final EducationMapper educationMapper;

    @Override
    public List<Education> getEducationByUserId(Long userId) {
        LambdaQueryWrapper<Education> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Education::getUserId, userId)
                .orderByDesc(Education::getStartDate);
        return educationMapper.selectList(wrapper);
    }

    @Override
    public Education getEducationById(Long id) {
        return educationMapper.selectById(id);
    }

    @Override
    public boolean createEducation(Education education) {
        return educationMapper.insert(education) > 0;
    }

    @Override
    public boolean updateEducation(Education education) {
        return educationMapper.updateById(education) > 0;
    }

    @Override
    public boolean deleteEducation(Long id) {
        return educationMapper.deleteById(id) > 0;
    }

    @Override
    public boolean validateEducationOwnership(Long id, Long userId) {
        Education education = educationMapper.selectById(id);
        if (education == null) {
            throw new BusinessException("教育经历不存在");
        }
        return education.getUserId().equals(userId);
    }
}
