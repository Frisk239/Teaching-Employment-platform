package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.CourseRecord;
import com.teaching.employment.service.CourseRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程学习记录控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/course-record")
@Api(tags = "课程学习记录管理")
@RequiredArgsConstructor
public class CourseRecordController {

    private final CourseRecordService courseRecordService;

    /**
     * 分页查询学习记录
     */
    @GetMapping("/page")
    @ApiOperation("分页查询学习记录")
    public Result<IPage<CourseRecord>> getRecordPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("学员ID") @RequestParam(required = false) Long studentId,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        IPage<CourseRecord> page = courseRecordService.getRecordPage(current, size, courseId, studentId, status);
        return Result.ok(page);
    }

    /**
     * 根据ID查询学习记录
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询学习记录")
    public Result<CourseRecord> getRecordById(@PathVariable Long id) {
        CourseRecord record = courseRecordService.getById(id);
        return Result.ok(record);
    }

    /**
     * 根据学员ID查询学习记录
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学员ID查询学习记录")
    public Result<List<CourseRecord>> getRecordsByStudentId(@PathVariable Long studentId) {
        List<CourseRecord> records = courseRecordService.getRecordsByStudentId(studentId);
        return Result.ok(records);
    }

    /**
     * 根据课程ID查询学习记录
     */
    @GetMapping("/course/{courseId}")
    @ApiOperation("根据课程ID查询学习记录")
    public Result<List<CourseRecord>> getRecordsByCourseId(@PathVariable Long courseId) {
        List<CourseRecord> records = courseRecordService.getRecordsByCourseId(courseId);
        return Result.ok(records);
    }

    /**
     * 查询学员在指定课程的学习记录
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    @ApiOperation("查询学员在指定课程的学习记录")
    public Result<CourseRecord> getRecordByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        CourseRecord record = courseRecordService.getRecordByStudentAndCourse(studentId, courseId);
        return Result.ok(record);
    }

    /**
     * 更新学习进度
     */
    @PostMapping("/progress")
    @ApiOperation("更新学习进度")
    public Result<Void> updateProgress(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("课程ID") @RequestParam Long courseId,
            @ApiParam("进度(%)") @RequestParam Double progress) {
        boolean success = courseRecordService.updateProgress(studentId, courseId, progress);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 更新观看时长
     */
    @PostMapping("/watch-duration")
    @ApiOperation("更新观看时长")
    public Result<Void> updateWatchedDuration(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("课程ID") @RequestParam Long courseId,
            @ApiParam("观看时长(秒)") @RequestParam Long duration) {
        boolean success = courseRecordService.updateWatchedDuration(studentId, courseId, duration);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 更新平均作业分
     */
    @PostMapping("/homework-score")
    @ApiOperation("更新平均作业分")
    public Result<Void> updateAvgHomeworkScore(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("课程ID") @RequestParam Long courseId,
            @ApiParam("平均分") @RequestParam Double score) {
        boolean success = courseRecordService.updateAvgHomeworkScore(studentId, courseId, score);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 更新考试成绩
     */
    @PostMapping("/exam-score")
    @ApiOperation("更新考试成绩")
    public Result<Void> updateExamScore(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("课程ID") @RequestParam Long courseId,
            @ApiParam("考试分数") @RequestParam Double score) {
        boolean success = courseRecordService.updateExamScore(studentId, courseId, score);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 开始学习课程
     */
    @PostMapping("/start")
    @ApiOperation("开始学习课程")
    public Result<Void> startLearning(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("课程ID") @RequestParam Long courseId,
            @ApiParam("总章节数") @RequestParam(required = false, defaultValue = "0") Integer totalChapters) {
        boolean success = courseRecordService.startLearning(studentId, courseId, totalChapters);
        return success ? Result.ok("开始学习") : Result.error("操作失败");
    }

    /**
     * 完成课程学习
     */
    @PostMapping("/complete")
    @ApiOperation("完成课程学习")
    public Result<Void> completeCourse(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("课程ID") @RequestParam Long courseId) {
        boolean success = courseRecordService.completeCourse(studentId, courseId);
        return success ? Result.ok("已完成") : Result.error("操作失败");
    }

    /**
     * 获取课程学习进度统计
     */
    @GetMapping("/course/{courseId}/statistics")
    @ApiOperation("获取课程学习进度统计")
    public Result<Map<String, Object>> getCourseStatistics(@PathVariable Long courseId) {
        Map<String, Object> statistics = courseRecordService.getCourseProgressStatistics(courseId);
        return Result.ok(statistics);
    }

    /**
     * 获取学员学习进度统计
     */
    @GetMapping("/student/{studentId}/statistics")
    @ApiOperation("获取学员学习进度统计")
    public Result<Map<String, Object>> getStudentStatistics(@PathVariable Long studentId) {
        Map<String, Object> statistics = courseRecordService.getStudentProgressStatistics(studentId);
        return Result.ok(statistics);
    }

    /**
     * 获取学习时长排行
     */
    @GetMapping("/top-learners")
    @ApiOperation("获取学习时长排行")
    public Result<List<Map<String, Object>>> getTopLearners(
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("返回记录数") @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> topLearners = courseRecordService.getTopLearnersByDuration(courseId, limit);
        return Result.ok(topLearners);
    }
}
