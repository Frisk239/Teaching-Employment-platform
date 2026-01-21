package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.entity.ExamQuestion;
import com.teaching.employment.service.ExamQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 试卷题目管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@RestController
@RequestMapping("/exam-question")
@Api(tags = "试卷题目管理")
@RequiredArgsConstructor
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    /**
     * 获取试卷的所有题目
     */
    @GetMapping("/exam/{examId}")
    @ApiOperation("获取试卷的所有题目")
    public Result<List<ExamQuestion>> getQuestionsByExamId(@PathVariable Long examId) {
        List<ExamQuestion> questions = examQuestionService.getQuestionsByExamId(examId);
        return Result.ok(questions);
    }

    /**
     * 获取试卷的题目ID列表
     */
    @GetMapping("/exam-question-ids")
    @ApiOperation("获取试卷的题目ID列表")
    public Result<List<Long>> getQuestionIdsByExamId(
            @ApiParam(value = "试卷ID", required = true) @RequestParam Long examId) {
        List<Long> questionIds = examQuestionService.getQuestionIdsByExamId(examId);
        return Result.ok(questionIds);
    }

    /**
     * 添加题目到试卷
     */
    @PostMapping
    @ApiOperation("添加题目到试卷")
    public Result<Void> addQuestionToExam(@RequestBody ExamQuestion examQuestion) {
        boolean success = examQuestionService.addQuestionToExam(examQuestion);
        return success ? Result.ok("添加成功") : Result.error("添加失败");
    }

    /**
     * 批量添加题目到试卷
     */
    @PostMapping("/batch")
    @ApiOperation("批量添加题目到试卷")
    public Result<Void> batchAddQuestions(@RequestBody List<ExamQuestion> examQuestions) {
        boolean success = examQuestionService.batchAddQuestions(examQuestions);
        return success ? Result.ok("批量添加成功") : Result.error("批量添加失败");
    }

    /**
     * 删除试卷中的题目
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除试卷中的题目")
    public Result<Void> removeQuestionFromExam(@PathVariable Long id) {
        boolean success = examQuestionService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新试卷题目信息（分值、排序）
     */
    @PutMapping
    @ApiOperation("更新试卷题目信息")
    public Result<Void> updateExamQuestion(@RequestBody ExamQuestion examQuestion) {
        boolean success = examQuestionService.updateById(examQuestion);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 清空试卷的所有题目
     */
    @DeleteMapping("/exam/{examId}")
    @ApiOperation("清空试卷的所有题目")
    public Result<Void> clearQuestionsByExamId(@PathVariable Long examId) {
        boolean success = examQuestionService.clearQuestionsByExamId(examId);
        return success ? Result.ok("清空成功") : Result.error("清空失败");
    }
}
