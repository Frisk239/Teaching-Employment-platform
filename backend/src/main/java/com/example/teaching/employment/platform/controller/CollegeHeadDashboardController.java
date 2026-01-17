package com.example.teaching.employment.platform.controller;

import com.teaching.employment.common.Result;
import com.example.teaching.employment.platform.model.dto.DashboardStatsDTO;
import com.example.teaching.employment.platform.model.dto.DailyReportDTO;
import com.example.teaching.employment.platform.model.dto.StudentAttentionDTO;
import com.example.teaching.employment.platform.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学院负责人Dashboard Controller
 */
@Tag(name = "学院负责人Dashboard", description = "学院负责人Dashboard相关接口")
@RestController
@RequestMapping("/dashboard/college")
@RequiredArgsConstructor
public class CollegeHeadDashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取Dashboard统计数据
     */
    @Operation(summary = "获取Dashboard统计数据")
    @GetMapping("/stats")
    public Result<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return Result.ok(stats);
    }

    /**
     * 获取最新日报列表
     */
    @Operation(summary = "获取最新日报列表")
    @GetMapping("/daily-reports/recent")
    public Result<List<DailyReportDTO>> getRecentDailyReports(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<DailyReportDTO> reports = dashboardService.getRecentDailyReports(limit);
        return Result.ok(reports);
    }

    /**
     * 获取需要关注的学员列表
     */
    @Operation(summary = "获取需要关注的学员列表")
    @GetMapping("/students/attention")
    public Result<List<StudentAttentionDTO>> getAttentionStudents(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<StudentAttentionDTO> students = dashboardService.getAttentionStudents(limit);
        return Result.ok(students);
    }
}
