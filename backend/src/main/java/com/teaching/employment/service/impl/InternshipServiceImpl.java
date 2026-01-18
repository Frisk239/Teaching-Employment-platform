package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Internship;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.InternshipMapper;
import com.teaching.employment.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实习经历Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
@Service
@RequiredArgsConstructor
public class InternshipServiceImpl extends ServiceImpl<InternshipMapper, Internship> implements InternshipService {

    private final InternshipMapper internshipMapper;

    @Override
    public List<Internship> getInternshipByUserId(Long userId) {
        LambdaQueryWrapper<Internship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Internship::getUserId, userId)
                .orderByDesc(Internship::getStartDate);
        return internshipMapper.selectList(wrapper);
    }

    @Override
    public Internship getInternshipById(Long id) {
        return internshipMapper.selectById(id);
    }

    @Override
    public boolean createInternship(Internship internship) {
        return internshipMapper.insert(internship) > 0;
    }

    @Override
    public boolean updateInternship(Internship internship) {
        return internshipMapper.updateById(internship) > 0;
    }

    @Override
    public boolean deleteInternship(Long id) {
        return internshipMapper.deleteById(id) > 0;
    }

    @Override
    public boolean validateInternshipOwnership(Long id, Long userId) {
        Internship internship = internshipMapper.selectById(id);
        if (internship == null) {
            throw new BusinessException("实习经历不存在");
        }
        return internship.getUserId().equals(userId);
    }
}
