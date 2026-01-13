package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.DailyReport;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.DailyReportMapper;
import com.teaching.employment.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学员日报Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl extends ServiceImpl<DailyReportMapper, DailyReport>
        implements DailyReportService {

    private final DailyReportMapper dailyReportMapper;

    @Override
    public IPage<DailyReport> getDailyReportPage(Integer current, Integer size, Long studentId, LocalDate startDate,
                                                  LocalDate endDate, String status) {
        Page<DailyReport> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();

        if (studentId != null) {
            wrapper.eq(DailyReport::getStudentId, studentId);
        }
        if (startDate != null) {
            wrapper.ge(DailyReport::getReportDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(DailyReport::getReportDate, endDate);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(DailyReport::getStatus, status);
        }

        wrapper.orderByDesc(DailyReport::getReportDate);

        return dailyReportMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<DailyReport> getDailyReportsByStudentId(Integer current, Integer size, Long studentId) {
        return getDailyReportPage(current, size, studentId, null, null, null);
    }

    @Override
    public DailyReport getDailyReportByDate(Long studentId, LocalDate reportDate) {
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyReport::getStudentId, studentId);
        wrapper.eq(DailyReport::getReportDate, reportDate);

        return dailyReportMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitDailyReport(Long reportId) {
        DailyReport report = dailyReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("日报不存在");
        }

        if (!"draft".equals(report.getStatus())) {
            throw new BusinessException("只有草稿状态的日报才能提交");
        }

        report.setStatus("submitted");
        report.setSubmitTime(LocalDateTime.now());

        return dailyReportMapper.updateById(report) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewDailyReport(Long reportId, String comment, Integer rating) {
        DailyReport report = dailyReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("日报不存在");
        }

        if (!"submitted".equals(report.getStatus())) {
            throw new BusinessException("只能批阅已提交的日报");
        }

        if (rating != null && (rating < 1 || rating > 5)) {
            throw new BusinessException("评分必须在1-5分之间");
        }

        report.setTeacherComment(comment);
        report.setRating(rating);
        report.setStatus("reviewed");
        report.setReviewTime(LocalDateTime.now());

        return dailyReportMapper.updateById(report) > 0;
    }
}
