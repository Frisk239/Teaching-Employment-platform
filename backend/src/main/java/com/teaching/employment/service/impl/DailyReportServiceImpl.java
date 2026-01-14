package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.DailyReport;
import com.teaching.employment.entity.Student;
import com.teaching.employment.entity.User;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.DailyReportMapper;
import com.teaching.employment.service.DailyReportService;
import com.teaching.employment.service.StudentService;
import com.teaching.employment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final StudentService studentService;
    private final UserService userService;

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

        IPage<DailyReport> resultPage = dailyReportMapper.selectPage(page, wrapper);

        // 填充学员信息
        fillStudentInfo(resultPage.getRecords());

        return resultPage;
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

    /**
     * 填充学员信息到日报列表
     */
    private void fillStudentInfo(List<DailyReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return;
        }

        // 获取所有不重复的学员ID
        List<Long> studentIds = reports.stream()
                .map(DailyReport::getStudentId)
                .distinct()
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            return;
        }

        // 批量查询学员信息
        List<Student> students = studentService.listByIds(studentIds);

        // 获取所有用户ID
        List<Long> userIds = students.stream()
                .map(Student::getUserId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询用户信息(用于获取姓名)
        Map<Long, User> userMap = Map.of();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
        }

        // 构建学员ID到学员的映射
        Map<Long, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, student -> student));

        // 填充学员信息到日报
        for (DailyReport report : reports) {
            Student student = studentMap.get(report.getStudentId());
            if (student != null) {
                report.setStudentNo(student.getStudentNo());

                // 从用户表中获取真实姓名
                if (student.getUserId() != null) {
                    User user = userMap.get(student.getUserId());
                    if (user != null) {
                        report.setStudentName(user.getRealName());
                    }
                }
            }
        }
    }
}
