package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.HomeworkSubmission;
import com.teaching.employment.service.HomeworkSubmissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作业提交控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/homework-submission")
@Api(tags = "作业提交管理")
@RequiredArgsConstructor
public class HomeworkSubmissionController {

    private final HomeworkSubmissionService homeworkSubmissionService;

    /**
     * 分页查询作业提交列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询作业提交列表")
    public Result<IPage<HomeworkSubmission>> getSubmissionPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("作业ID") @RequestParam(required = false) Long homeworkId,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        IPage<HomeworkSubmission> page = homeworkSubmissionService.getSubmissionPage(
                current, size, homeworkId, studentId, status);
        return Result.ok(page);
    }

    /**
     * 根据ID查询提交记录
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询提交记录")
    public Result<HomeworkSubmission> getSubmissionById(@PathVariable Long id) {
        HomeworkSubmission submission = homeworkSubmissionService.getById(id);
        return Result.ok(submission);
    }

    /**
     * 学生提交作业
     */
    @PostMapping
    @ApiOperation("学生提交作业")
    public Result<Void> submitHomework(@RequestBody Map<String, Object> request) {
        Long homeworkId = Long.valueOf(request.get("homeworkId").toString());
        Long studentId = Long.valueOf(request.get("studentId").toString());
        String content = (String) request.get("content");
        String attachmentUrl = (String) request.get("attachmentUrl");

        boolean success = homeworkSubmissionService.submitHomework(homeworkId, studentId, content, attachmentUrl);
        return success ? Result.ok("提交成功") : Result.error("提交失败");
    }

    /**
     * 教师批改作业
     */
    @PostMapping("/{id}/grade")
    @ApiOperation("教师批改作业")
    public Result<Void> gradeSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Integer score = Integer.valueOf(request.get("score").toString());
        String comment = (String) request.get("comment");

        boolean success = homeworkSubmissionService.gradeSubmission(id, score, comment);
        return success ? Result.ok("批改成功") : Result.error("批改失败");
    }

    /**
     * 教师退回作业
     */
    @PostMapping("/{id}/return")
    @ApiOperation("教师退回作业")
    public Result<Void> returnSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String comment = request.get("comment");

        boolean success = homeworkSubmissionService.returnSubmission(id, comment);
        return success ? Result.ok("退回成功") : Result.error("退回失败");
    }

    /**
     * 查询学生某作业的提交记录
     */
    @GetMapping("/homework/{homeworkId}/student/{studentId}")
    @ApiOperation("查询学生某作业的提交记录")
    public Result<HomeworkSubmission> getSubmissionByHomeworkAndStudent(
            @PathVariable Long homeworkId,
            @PathVariable Long studentId) {
        HomeworkSubmission submission = homeworkSubmissionService
                .getSubmissionByHomeworkAndStudent(homeworkId, studentId);
        return Result.ok(submission);
    }
}
