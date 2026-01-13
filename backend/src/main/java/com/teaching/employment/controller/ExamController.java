package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Exam;
import com.teaching.employment.service.ExamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/exam")
@Api(tags = "考试管理")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    /**
     * 分页查询考试列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询考试列表")
    public Result<IPage<Exam>> getExamPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("考试类型") @RequestParam(required = false) String examType,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Exam> page = examService.getExamPage(current, size, courseId, examType, status, keyword);
        return Result.ok(page);
    }

    /**
     * 根据ID查询考试
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询考试")
    public Result<Exam> getExamById(@PathVariable Long id) {
        Exam exam = examService.getById(id);
        return Result.ok(exam);
    }

    /**
     * 新增考试
     */
    @PostMapping
    @ApiOperation("新增考试")
    public Result<Void> createExam(@RequestBody Exam exam) {
        boolean success = examService.save(exam);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新考试
     */
    @PutMapping
    @ApiOperation("更新考试")
    public Result<Void> updateExam(@RequestBody Exam exam) {
        boolean success = examService.updateById(exam);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除考试
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除考试")
    public Result<Void> deleteExam(@PathVariable Long id) {
        boolean success = examService.deleteExam(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 根据课程ID查询考试列表
     */
    @GetMapping("/course/{courseId}")
    @ApiOperation("根据课程ID查询考试列表")
    public Result<List<Exam>> getExamsByCourseId(@PathVariable Long courseId) {
        List<Exam> exams = examService.getExamsByCourseId(courseId);
        return Result.ok(exams);
    }

    /**
     * 查询已发布的考试列表
     */
    @GetMapping("/published")
    @ApiOperation("查询已发布的考试列表")
    public Result<List<Exam>> getPublishedExams() {
        List<Exam> exams = examService.getPublishedExams();
        return Result.ok(exams);
    }

    /**
     * 查询进行中的考试列表
     */
    @GetMapping("/ongoing")
    @ApiOperation("查询进行中的考试列表")
    public Result<List<Exam>> getOngoingExams() {
        List<Exam> exams = examService.getOngoingExams();
        return Result.ok(exams);
    }

    /**
     * 发布考试
     */
    @PostMapping("/{id}/publish")
    @ApiOperation("发布考试")
    public Result<Void> publishExam(@PathVariable Long id) {
        boolean success = examService.publishExam(id);
        return success ? Result.ok("发布成功") : Result.error("发布失败");
    }

    /**
     * 开始考试
     */
    @PostMapping("/{id}/start")
    @ApiOperation("开始考试")
    public Result<Void> startExam(@PathVariable Long id) {
        boolean success = examService.startExam(id);
        return success ? Result.ok("考试已开始") : Result.error("开始失败");
    }

    /**
     * 结束考试
     */
    @PostMapping("/{id}/end")
    @ApiOperation("结束考试")
    public Result<Void> endExam(@PathVariable Long id) {
        boolean success = examService.endExam(id);
        return success ? Result.ok("考试已结束") : Result.error("结束失败");
    }
}
