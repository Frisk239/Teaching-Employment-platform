package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Resume;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.ResumeMapper;
import com.teaching.employment.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 简历Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    private final ResumeMapper resumeMapper;

    @Override
    public IPage<Resume> getResumePage(Integer current, Integer size, Long studentId, String status, String keyword, String city) {
        Page<Resume> page = new Page<>(current, size);
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();

        if (studentId != null) {
            wrapper.eq(Resume::getStudentId, studentId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Resume::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Resume::getName, keyword)
                    .or()
                    .like(Resume::getJobIntention, keyword));
        }
        if (StrUtil.isNotBlank(city)) {
            wrapper.eq(Resume::getExpectCity, city);
        }

        wrapper.orderByDesc(Resume::getLastUpdateTime);

        return resumeMapper.selectPage(page, wrapper);
    }

    @Override
    public Resume getResumeByStudentId(Long studentId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getStudentId, studentId);

        return resumeMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishResume(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        // 检查简历完整度
        Integer completeness = resume.getCompleteness();
        if (completeness == null || completeness < 60) {
            throw new BusinessException("简历完整度需达到60%以上才能公开");
        }

        resume.setStatus("active");
        resume.setLastUpdateTime(LocalDateTime.now());

        return resumeMapper.updateById(resume) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean hideResume(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        resume.setStatus("inactive");
        resume.setLastUpdateTime(LocalDateTime.now());

        return resumeMapper.updateById(resume) > 0;
    }

    @Override
    public Integer calculateCompleteness(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        int score = 0;
        int total = 100;

        // 基本信息 (40分)
        if (StrUtil.isNotBlank(resume.getName())) score += 10;
        if (StrUtil.isNotBlank(resume.getPhone())) score += 10;
        if (StrUtil.isNotBlank(resume.getEmail())) score += 10;
        if (resume.getBirthDate() != null) score += 10;

        // 求职意向 (20分)
        if (StrUtil.isNotBlank(resume.getJobIntention())) score += 10;
        if (StrUtil.isNotBlank(resume.getExpectCity())) score += 10;

        // 期望薪资 (10分)
        if (resume.getExpectSalaryMin() != null && resume.getExpectSalaryMax() != null) score += 10;

        // 教育经历 (15分)
        if (StrUtil.isNotBlank(resume.getEducation())) score += 15;

        // 项目经历 (15分)
        if (StrUtil.isNotBlank(resume.getProjects())) score += 15;

        return score;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCompleteness(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        Integer completeness = calculateCompleteness(resumeId);
        resume.setCompleteness(completeness);
        resume.setLastUpdateTime(LocalDateTime.now());

        return resumeMapper.updateById(resume) > 0;
    }
}
