package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Question;
import com.teaching.employment.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/question")
@Api(tags = "题目管理")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 根据考试ID查询题目列表
     */
    @GetMapping("/exam/{examId}")
    @ApiOperation("根据考试ID查询题目列表")
    public Result<List<Question>> getQuestionsByExamId(@PathVariable Long examId) {
        List<Question> questions = questionService.getQuestionsByExamId(examId);
        return Result.ok(questions);
    }

    /**
     * 随机获取题目
     */
    @GetMapping("/random")
    @ApiOperation("随机获取题目")
    public Result<List<Question>> getRandomQuestions(
            @ApiParam("考试ID") @RequestParam Long examId,
            @ApiParam("题目数量") @RequestParam Integer count) {
        List<Question> questions = questionService.getRandomQuestions(examId, count);
        return Result.ok(questions);
    }

    /**
     * 统计考试题目数量
     */
    @GetMapping("/count/{examId}")
    @ApiOperation("统计考试题目数量")
    public Result<Integer> countByExamId(@PathVariable Long examId) {
        Integer count = questionService.countByExamId(examId);
        return Result.ok(count);
    }

    /**
     * 新增题目
     */
    @PostMapping
    @ApiOperation("新增题目")
    public Result<Void> createQuestion(@RequestBody Question question) {
        boolean success = questionService.save(question);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 批量添加题目
     */
    @PostMapping("/batch")
    @ApiOperation("批量添加题目")
    public Result<Void> batchAddQuestions(@RequestBody List<Question> questions) {
        boolean success = questionService.batchAddQuestions(questions);
        return success ? Result.ok("批量添加成功") : Result.error("批量添加失败");
    }

    /**
     * 更新题目
     */
    @PutMapping
    @ApiOperation("更新题目")
    public Result<Void> updateQuestion(@RequestBody Question question) {
        boolean success = questionService.updateById(question);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除题目")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        boolean success = questionService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除题目(根据考试ID)
     */
    @DeleteMapping("/exam/{examId}")
    @ApiOperation("批量删除题目")
    public Result<Void> deleteQuestionsByExamId(@PathVariable Long examId) {
        boolean success = questionService.deleteQuestionsByExamId(examId);
        return success ? Result.ok("批量删除成功") : Result.error("批量删除失败");
    }
}
