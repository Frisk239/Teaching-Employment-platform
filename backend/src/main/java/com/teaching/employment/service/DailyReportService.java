package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.DailyReport;

import java.time.LocalDate;

/**
 * 学员日报Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface DailyReportService extends IService<DailyReport> {

    /**
     * 分页查询日报列表
     */
    IPage<DailyReport> getDailyReportPage(Integer current, Integer size, Long studentId, LocalDate startDate,
                                          LocalDate endDate, String status);

    /**
     * 根据学生ID查询日报列表
     */
    IPage<DailyReport> getDailyReportsByStudentId(Integer current, Integer size, Long studentId);

    /**
     * 根据日期查询日报
     */
    DailyReport getDailyReportByDate(Long studentId, LocalDate reportDate);

    /**
     * 提交日报
     */
    boolean submitDailyReport(Long reportId);

    /**
     * 教师批阅日报
     */
    boolean reviewDailyReport(Long reportId, String comment, Integer rating);
}
