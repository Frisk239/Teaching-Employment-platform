package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.JobApplication;
import com.teaching.employment.entity.WrittenTest;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.WrittenTestMapper;
import com.teaching.employment.service.JobApplicationService;
import com.teaching.employment.service.WrittenTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 笔试Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class WrittenTestServiceImpl extends ServiceImpl<WrittenTestMapper, WrittenTest>
        implements WrittenTestService {

    private final WrittenTestMapper writtenTestMapper;
    private final JobApplicationService jobApplicationService;

    @Override
    public IPage<WrittenTest> getWrittenTestPage(Integer current, Integer size, Long applicationId, Long positionId,
                                                  Long studentId, String status) {
        Page<WrittenTest> page = new Page<>(current, size);
        LambdaQueryWrapper<WrittenTest> wrapper = new LambdaQueryWrapper<>();

        if (applicationId != null) {
            wrapper.eq(WrittenTest::getApplicationId, applicationId);
        }
        if (positionId != null) {
            wrapper.eq(WrittenTest::getPositionId, positionId);
        }
        if (studentId != null) {
            wrapper.eq(WrittenTest::getStudentId, studentId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(WrittenTest::getStatus, status);
        }

        wrapper.orderByDesc(WrittenTest::getCreateTime);

        return writtenTestMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<WrittenTest> getWrittenTestsByStudentId(Integer current, Integer size, Long studentId) {
        return getWrittenTestPage(current, size, null, null, studentId, null);
    }

    @Override
    public WrittenTest getWrittenTestByApplicationId(Long applicationId) {
        LambdaQueryWrapper<WrittenTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrittenTest::getApplicationId, applicationId);

        return writtenTestMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean scheduleTest(Long applicationId, String testUrl, Integer duration,
                                java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        // 检查申请是否存在且状态正确
        JobApplication application = jobApplicationService.getById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!"screened".equals(application.getStatus())) {
            throw new BusinessException("只有已筛选的申请才能安排笔试");
        }

        // 检查是否已安排笔试
        WrittenTest existingTest = getWrittenTestByApplicationId(applicationId);
        if (existingTest != null) {
            throw new BusinessException("该申请已安排笔试");
        }

        // 创建笔试
        WrittenTest test = new WrittenTest();
        test.setApplicationId(applicationId);
        test.setPositionId(application.getPositionId());
        test.setStudentId(application.getStudentId());
        test.setTestUrl(testUrl);
        test.setDuration(duration);
        test.setStartTime(startTime);
        test.setEndTime(endTime);
        test.setStatus("pending");

        boolean result = writtenTestMapper.insert(test) > 0;

        if (result) {
            // 更新申请状态和阶段
            jobApplicationService.updateApplicationStatus(applicationId, null, "test");
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startTest(Long testId) {
        WrittenTest test = writtenTestMapper.selectById(testId);
        if (test == null) {
            throw new BusinessException("笔试不存在");
        }

        if (!"pending".equals(test.getStatus())) {
            throw new BusinessException("只有待考试的笔试才能开始");
        }

        // 检查考试时间
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (test.getStartTime().isAfter(now)) {
            throw new BusinessException("考试尚未开始");
        }
        if (test.getEndTime().isBefore(now)) {
            throw new BusinessException("考试已结束");
        }

        test.setStatus("ongoing");

        return writtenTestMapper.updateById(test) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitScore(Long testId, Integer score, Integer totalScore, String comment) {
        WrittenTest test = writtenTestMapper.selectById(testId);
        if (test == null) {
            throw new BusinessException("笔试不存在");
        }

        if (!"ongoing".equals(test.getStatus()) && !"pending".equals(test.getStatus())) {
            throw new BusinessException("只有待考试或考试中的笔试才能提交成绩");
        }

        test.setScore(score);
        test.setTotalScore(totalScore);
        test.setComment(comment);
        test.setStatus("completed");

        boolean result = writtenTestMapper.updateById(test) > 0;

        if (result) {
            // 更新申请状态
            JobApplication application = jobApplicationService.getById(test.getApplicationId());
            if (application != null) {
                // 假设60分及以上为通过
                String newStatus = (score * 100 / totalScore) >= 60 ? "test_passed" : "test_failed";
                jobApplicationService.updateApplicationStatus(test.getApplicationId(), newStatus, null);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsMissed(Long testId) {
        WrittenTest test = writtenTestMapper.selectById(testId);
        if (test == null) {
            throw new BusinessException("笔试不存在");
        }

        if ("completed".equals(test.getStatus()) || "missed".equals(test.getStatus())) {
            throw new BusinessException("该笔试已完成或已标记为缺席");
        }

        test.setStatus("missed");

        boolean result = writtenTestMapper.updateById(test) > 0;

        if (result) {
            // 更新申请状态为笔试失败
            jobApplicationService.updateApplicationStatus(test.getApplicationId(), "test_failed", null);
        }

        return result;
    }
}
