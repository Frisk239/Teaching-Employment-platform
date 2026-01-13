package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Exam;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.ExamMapper;
import com.teaching.employment.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 考试Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    private final ExamMapper examMapper;

    @Override
    public IPage<Exam> getExamPage(Integer current, Integer size, Long courseId, String examType, String status, String keyword) {
        Page<Exam> page = new Page<>(current, size);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();

        if (courseId != null) {
            wrapper.eq(Exam::getCourseId, courseId);
        }
        if (StrUtil.isNotBlank(examType)) {
            wrapper.eq(Exam::getExamType, examType);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Exam::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Exam::getExamName, keyword);
        }

        wrapper.orderByDesc(Exam::getCreateTime);
        return examMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Exam> getExamsByCourseId(Long courseId) {
        return examMapper.selectByCourseId(courseId);
    }

    @Override
    public List<Exam> getPublishedExams() {
        return examMapper.selectPublishedExams();
    }

    @Override
    public List<Exam> getOngoingExams() {
        return examMapper.selectOngoingExams();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        if (!"draft".equals(exam.getStatus())) {
            throw new BusinessException("只有草稿状态的考试才能发布");
        }

        exam.setStatus("published");
        return examMapper.updateById(exam) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        if (!"published".equals(exam.getStatus())) {
            throw new BusinessException("只有已发布的考试才能开始");
        }

        exam.setStatus("started");
        return examMapper.updateById(exam) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean endExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        exam.setStatus("ended");
        return examMapper.updateById(exam) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        if ("started".equals(exam.getStatus())) {
            throw new BusinessException("进行中的考试不能删除");
        }

        return examMapper.deleteById(examId) > 0;
    }
}
