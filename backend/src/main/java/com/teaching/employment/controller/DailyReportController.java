package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.DailyReport;
import com.teaching.employment.service.DailyReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 学员日报管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/daily-report")
@Api(tags = "学员日报管理")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService dailyReportService;

    /**
     * 分页查询日报列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询日报列表")
    public Result<IPage<DailyReport>> getDailyReportPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        IPage<DailyReport> page = dailyReportService.getDailyReportPage(current, size, studentId, startDate, endDate,
                status);
        return Result.ok(page);
    }

    /**
     * 根据学生ID查询日报列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学生ID查询日报列表")
    public Result<IPage<DailyReport>> getDailyReportsByStudentId(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<DailyReport> page = dailyReportService.getDailyReportsByStudentId(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 根据ID查询日报
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询日报")
    public Result<DailyReport> getDailyReportById(@PathVariable Long id) {
        DailyReport report = dailyReportService.getById(id);
        return Result.ok(report);
    }

    /**
     * 根据日期查询日报
     */
    @GetMapping("/student/{studentId}/date/{date}")
    @ApiOperation("根据日期查询日报")
    public Result<DailyReport> getDailyReportByDate(
            @PathVariable Long studentId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DailyReport report = dailyReportService.getDailyReportByDate(studentId, date);
        return Result.ok(report);
    }

    /**
     * 新增日报
     */
    @PostMapping
    @ApiOperation("新增日报")
    public Result<Void> createDailyReport(@RequestBody DailyReport report) {
        // 默认状态为草稿
        if (report.getStatus() == null) {
            report.setStatus("draft");
        }
        boolean success = dailyReportService.save(report);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新日报
     */
    @PutMapping
    @ApiOperation("更新日报")
    public Result<Void> updateDailyReport(@RequestBody DailyReport report) {
        boolean success = dailyReportService.updateById(report);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除日报
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除日报")
    public Result<Void> deleteDailyReport(@PathVariable Long id) {
        boolean success = dailyReportService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 提交日报
     */
    @PostMapping("/{id}/submit")
    @ApiOperation("提交日报")
    public Result<Void> submitDailyReport(@PathVariable Long id) {
        boolean success = dailyReportService.submitDailyReport(id);
        return success ? Result.ok("提交成功") : Result.error("提交失败");
    }

    /**
     * 教师批阅日报
     */
    @PostMapping("/{id}/review")
    @ApiOperation("教师批阅日报")
    public Result<Void> reviewDailyReport(
            @PathVariable Long id,
            @RequestBody DailyReport review) {
        boolean success = dailyReportService.reviewDailyReport(
                id, review.getTeacherComment(), review.getRating());
        return success ? Result.ok("批阅成功") : Result.error("批阅失败");
    }
}
