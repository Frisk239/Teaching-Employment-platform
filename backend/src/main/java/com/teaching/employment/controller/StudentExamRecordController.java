package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.StudentExamRecord;
import com.teaching.employment.service.StudentExamRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学员考试记录控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/student-exam-record")
@Api(tags = "学员考试记录管理")
@RequiredArgsConstructor
public class StudentExamRecordController {

    private final StudentExamRecordService studentExamRecordService;

    /**
     * 分页查询考试记录
     */
    @GetMapping("/page")
    @ApiOperation("分页查询考试记录")
    public Result<IPage<StudentExamRecord>> getRecordPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("考试ID") @RequestParam(required = false) Long examId,
            @ApiParam("学员ID") @RequestParam(required = false) Long studentId,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        IPage<StudentExamRecord> page = studentExamRecordService.getRecordPage(current, size, examId, studentId, status);
        return Result.ok(page);
    }

    /**
     * 根据ID查询考试记录
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询考试记录")
    public Result<StudentExamRecord> getRecordById(@PathVariable Long id) {
        StudentExamRecord record = studentExamRecordService.getById(id);
        return Result.ok(record);
    }

    /**
     * 根据考试ID查询所有学员成绩
     */
    @GetMapping("/exam/{examId}")
    @ApiOperation("根据考试ID查询所有学员成绩")
    public Result<List<StudentExamRecord>> getRecordsByExamId(@PathVariable Long examId) {
        List<StudentExamRecord> records = studentExamRecordService.getRecordsByExamId(examId);
        return Result.ok(records);
    }

    /**
     * 根据学员ID查询考试记录
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学员ID查询考试记录")
    public Result<List<StudentExamRecord>> getRecordsByStudentId(@PathVariable Long studentId) {
        List<StudentExamRecord> records = studentExamRecordService.getRecordsByStudentId(studentId);
        return Result.ok(records);
    }

    /**
     * 查询学员在指定考试中的记录
     */
    @GetMapping("/exam/{examId}/student/{studentId}")
    @ApiOperation("查询学员在指定考试中的记录")
    public Result<StudentExamRecord> getRecordByExamAndStudent(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        StudentExamRecord record = studentExamRecordService.getRecordByExamAndStudent(examId, studentId);
        return Result.ok(record);
    }

    /**
     * 开始考试
     */
    @PostMapping("/start")
    @ApiOperation("开始考试")
    public Result<Void> startExam(
            @ApiParam("考试ID") @RequestParam Long examId,
            @ApiParam("学员ID") @RequestParam Long studentId) {
        boolean success = studentExamRecordService.startExam(examId, studentId);
        return success ? Result.ok("考试已开始") : Result.error("开始考试失败");
    }

    /**
     * 提交答案
     */
    @PostMapping("/submit")
    @ApiOperation("提交答案")
    public Result<Void> submitAnswer(
            @ApiParam("考试ID") @RequestParam Long examId,
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("答案JSON") @RequestParam String answers) {
        boolean success = studentExamRecordService.submitAnswer(examId, studentId, answers);
        return success ? Result.ok("提交成功") : Result.error("提交失败");
    }

    /**
     * 批改试卷
     */
    @PostMapping("/{recordId}/grade")
    @ApiOperation("批改试卷")
    public Result<Void> gradeExam(@PathVariable Long recordId) {
        boolean success = studentExamRecordService.gradeExam(recordId);
        return success ? Result.ok("批改完成") : Result.error("批改失败");
    }

    /**
     * 批量批改试卷
     */
    @PostMapping("/exam/{examId}/grade-batch")
    @ApiOperation("批量批改试卷")
    public Result<Void> batchGradeExams(@PathVariable Long examId) {
        boolean success = studentExamRecordService.batchGradeExams(examId);
        return success ? Result.ok("批量批改完成") : Result.error("批量批改失败");
    }
}
