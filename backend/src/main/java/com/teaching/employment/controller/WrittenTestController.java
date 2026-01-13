package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.WrittenTest;
import com.teaching.employment.service.WrittenTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 笔试管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/written-test")
@Api(tags = "笔试管理")
@RequiredArgsConstructor
public class WrittenTestController {

    private final WrittenTestService writtenTestService;

    /**
     * 分页查询笔试列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询笔试列表")
    public Result<IPage<WrittenTest>> getWrittenTestPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("申请ID") @RequestParam(required = false) Long applicationId,
            @ApiParam("职位ID") @RequestParam(required = false) Long positionId,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("考试状态") @RequestParam(required = false) String status) {
        IPage<WrittenTest> page = writtenTestService.getWrittenTestPage(current, size, applicationId,
                positionId, studentId, status);
        return Result.ok(page);
    }

    /**
     * 根据学生ID查询笔试列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学生ID查询笔试列表")
    public Result<IPage<WrittenTest>> getWrittenTestsByStudentId(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<WrittenTest> page = writtenTestService.getWrittenTestsByStudentId(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 根据申请ID获取笔试信息
     */
    @GetMapping("/application/{applicationId}")
    @ApiOperation("根据申请ID获取笔试信息")
    public Result<WrittenTest> getWrittenTestByApplicationId(@PathVariable Long applicationId) {
        WrittenTest writtenTest = writtenTestService.getWrittenTestByApplicationId(applicationId);
        return Result.ok(writtenTest);
    }

    /**
     * 根据ID查询笔试
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询笔试")
    public Result<WrittenTest> getWrittenTestById(@PathVariable Long id) {
        WrittenTest writtenTest = writtenTestService.getById(id);
        return Result.ok(writtenTest);
    }

    /**
     * 新增笔试
     */
    @PostMapping
    @ApiOperation("新增笔试")
    public Result<Void> createWrittenTest(@RequestBody WrittenTest writtenTest) {
        // 默认状态为待考试
        if (writtenTest.getStatus() == null) {
            writtenTest.setStatus("pending");
        }
        boolean success = writtenTestService.save(writtenTest);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新笔试
     */
    @PutMapping
    @ApiOperation("更新笔试")
    public Result<Void> updateWrittenTest(@RequestBody WrittenTest writtenTest) {
        boolean success = writtenTestService.updateById(writtenTest);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除笔试
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除笔试")
    public Result<Void> deleteWrittenTest(@PathVariable Long id) {
        boolean success = writtenTestService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 安排笔试
     */
    @PostMapping("/schedule")
    @ApiOperation("安排笔试")
    public Result<Void> scheduleTest(
            @ApiParam("申请ID") @RequestParam Long applicationId,
            @ApiParam("考试链接") @RequestParam String testUrl,
            @ApiParam("考试时长（分钟）") @RequestParam Integer duration,
            @ApiParam("开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endTime) {
        boolean success = writtenTestService.scheduleTest(applicationId, testUrl, duration, startTime, endTime);
        return success ? Result.ok("安排成功") : Result.error("安排失败");
    }

    /**
     * 学生开始考试
     */
    @PostMapping("/{id}/start")
    @ApiOperation("学生开始考试")
    public Result<Void> startTest(@PathVariable Long id) {
        boolean success = writtenTestService.startTest(id);
        return success ? Result.ok("开始考试成功") : Result.error("开始考试失败");
    }

    /**
     * 提交笔试成绩
     */
    @PostMapping("/{id}/submit-score")
    @ApiOperation("提交笔试成绩")
    public Result<Void> submitScore(
            @PathVariable Long id,
            @ApiParam("得分") @RequestParam Integer score,
            @ApiParam("总分") @RequestParam Integer totalScore,
            @ApiParam("评语") @RequestParam(required = false) String comment) {
        boolean success = writtenTestService.submitScore(id, score, totalScore, comment);
        return success ? Result.ok("提交成功") : Result.error("提交失败");
    }

    /**
     * 标记为缺席
     */
    @PostMapping("/{id}/mark-missed")
    @ApiOperation("标记为缺席")
    public Result<Void> markAsMissed(@PathVariable Long id) {
        boolean success = writtenTestService.markAsMissed(id);
        return success ? Result.ok("标记成功") : Result.error("标记失败");
    }
}
