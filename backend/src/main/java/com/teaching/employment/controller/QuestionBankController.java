package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.QuestionBank;
import com.teaching.employment.service.QuestionBankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题库管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-21
 */
@RestController
@RequestMapping("/question-bank")
@Api(tags = "题库管理")
@RequiredArgsConstructor
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    @GetMapping("/page")
    @ApiOperation("分页查询题库")
    public Result<IPage<QuestionBank>> getQuestionPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("知识点") @RequestParam(required = false) String knowledgePoint,
            @ApiParam("题型数组") @RequestParam(required = false) String[] questionType,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("难度") @RequestParam(required = false) String difficulty,
            @ApiParam("企业ID") @RequestParam(required = false) Long companyId) {
        IPage<QuestionBank> page = questionBankService.getQuestionPage(
                current, size, knowledgePoint, questionType, keyword, difficulty, companyId);
        return Result.ok(page);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询题目")
    public Result<QuestionBank> getQuestionById(@PathVariable Long id) {
        QuestionBank question = questionBankService.getById(id);
        return Result.ok(question);
    }

    @PostMapping
    @ApiOperation("添加题目")
    public Result<Void> createQuestion(@RequestBody QuestionBank question) {
        question.setStatus(QuestionBank.STATUS_ACTIVE);
        boolean success = questionBankService.save(question);
        return success ? Result.ok("添加成功") : Result.error("添加失败");
    }

    @PutMapping
    @ApiOperation("更新题目")
    public Result<Void> updateQuestion(@RequestBody QuestionBank question) {
        boolean success = questionBankService.updateById(question);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除题目")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        boolean success = questionBankService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    @GetMapping("/knowledge-points")
    @ApiOperation("获取所有知识点分类")
    public Result<List<String>> getAllKnowledgePoints() {
        List<String> knowledgePoints = questionBankService.getAllKnowledgePoints();
        return Result.ok(knowledgePoints);
    }
}
