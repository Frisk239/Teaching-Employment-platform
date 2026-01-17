package com.teaching.employment.service;

import com.teaching.employment.model.dto.DailyReportDTO;
import com.teaching.employment.model.dto.DashboardStatsDTO;
import com.teaching.employment.model.dto.StudentAttentionDTO;

import java.util.List;

/**
 * Dashboard Service接口
 */
public interface DashboardService {

    /**
     * 获取Dashboard统计数据
     */
    DashboardStatsDTO getDashboardStats();

    /**
     * 获取最新日报列表
     */
    List<DailyReportDTO> getRecentDailyReports(Integer limit);

    /**
     * 获取需要关注的学员列表
     */
    List<StudentAttentionDTO> getAttentionStudents(Integer limit);
}
