package com.example.teaching.employment.platform.service;

import com.example.teaching.employment.platform.model.dto.DailyReportDTO;
import com.example.teaching.employment.platform.model.dto.DashboardStatsDTO;
import com.example.teaching.employment.platform.model.dto.StudentAttentionDTO;

import java.util.List;

/**
 * Dashboard Service
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
