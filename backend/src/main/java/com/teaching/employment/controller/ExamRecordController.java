package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.dto.StudentAnswerRequest;
import com.teaching.employment.entity.ExamRecord;
import com.teaching.employment.service.ExamRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考试记录控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@RestController
@RequestMapping("/exam-record")
@Api(tags = "考试记录管理")
@RequiredArgsConstructor
public class ExamRecordController {

    private final ExamRecordService examRecordService;

    /**
     * 分页查询考试记录列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询考试记录列表")
    public Result<IPage<ExamRecord>> getExamRecordPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("试卷ID") @RequestParam(required = false) Long examId,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("评阅状态") @RequestParam(required = false) String gradingStatus,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        IPage<ExamRecord> page = examRecordService.getExamRecordPage(current, size, examId, studentId, gradingStatus, status);
        return Result.ok(page);
    }

    /**
     * 根据ID查询考试记录
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询考试记录")
    public Result<ExamRecord> getExamRecordById(@PathVariable Long id) {
        ExamRecord examRecord = examRecordService.getById(id);
        return Result.ok(examRecord);
    }

    /**
     * 开始考试
     */
    @PostMapping("/start")
    @ApiOperation("开始考试")
    public Result<ExamRecord> startExam(
            @ApiParam("试卷ID") @RequestParam Long examId,
            @ApiParam("学生ID") @RequestParam Long studentId) {
        ExamRecord examRecord = examRecordService.startExam(examId, studentId);
        return Result.ok("开始考试成功", examRecord);
    }

    /**
     * 提交试卷
     */
    @PostMapping("/{id}/submit")
    @ApiOperation("提交试卷")
    public Result<Void> submitExam(@PathVariable Long id) {
        boolean success = examRecordService.submitExam(id);
        return success ? Result.ok("提交成功") : Result.error("提交失败");
    }

    /**
     * 自动评阅客观题
     */
    @PostMapping("/{id}/auto-grade")
    @ApiOperation("自动评阅客观题")
    public Result<Void> autoGradeObjectiveQuestions(@PathVariable Long id) {
        boolean success = examRecordService.autoGradeObjectiveQuestions(id);
        return success ? Result.ok("评阅成功") : Result.error("评阅失败");
    }

    /**
     * 教师评阅主观题
     */
    @PostMapping("/{id}/grade")
    @ApiOperation("教师评阅主观题")
    public Result<Void> gradeSubjectiveQuestions(
            @PathVariable Long id,
            @ApiParam("评阅人ID") @RequestParam Long graderId) {
        boolean success = examRecordService.gradeSubjectiveQuestions(id, graderId);
        return success ? Result.ok("评阅成功") : Result.error("评阅失败");
    }

    /**
     * 批量保存学生答案
     */
    @PostMapping("/{recordId}/answers")
    @ApiOperation("批量保存学生答案")
    public Result<Integer> saveAnswers(
            @PathVariable Long recordId,
            @RequestBody Map<String, List<StudentAnswerRequest>> request) {
        List<StudentAnswerRequest> answers = request.get("answers");
        int count = examRecordService.saveAnswers(recordId, answers);
        return Result.ok("保存成功，共保存" + count + "条答案", count);
    }
}
