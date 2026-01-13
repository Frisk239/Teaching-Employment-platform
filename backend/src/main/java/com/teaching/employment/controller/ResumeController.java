package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Resume;
import com.teaching.employment.service.ResumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 简历管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/resume")
@Api(tags = "简历管理")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    /**
     * 分页查询简历列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询简历列表")
    public Result<IPage<Resume>> getResumePage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学生ID") @RequestParam(required = false) Long studentId,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("期望城市") @RequestParam(required = false) String city) {
        IPage<Resume> page = resumeService.getResumePage(current, size, studentId, status, keyword, city);
        return Result.ok(page);
    }

    /**
     * 根据学生ID查询简历
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学生ID查询简历")
    public Result<Resume> getResumeByStudentId(@PathVariable Long studentId) {
        Resume resume = resumeService.getResumeByStudentId(studentId);
        return Result.ok(resume);
    }

    /**
     * 根据ID查询简历
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询简历")
    public Result<Resume> getResumeById(@PathVariable Long id) {
        Resume resume = resumeService.getById(id);
        return Result.ok(resume);
    }

    /**
     * 新增简历
     */
    @PostMapping
    @ApiOperation("新增简历")
    public Result<Void> createResume(@RequestBody Resume resume) {
        // 默认状态为草稿
        if (resume.getStatus() == null) {
            resume.setStatus("draft");
        }
        boolean success = resumeService.save(resume);
        if (success) {
            // 自动计算完整度
            resumeService.updateCompleteness(resume.getId());
        }
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新简历
     */
    @PutMapping
    @ApiOperation("更新简历")
    public Result<Void> updateResume(@RequestBody Resume resume) {
        boolean success = resumeService.updateById(resume);
        if (success) {
            // 自动更新完整度
            resumeService.updateCompleteness(resume.getId());
        }
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除简历
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除简历")
    public Result<Void> deleteResume(@PathVariable Long id) {
        boolean success = resumeService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 公开简历
     */
    @PostMapping("/{id}/publish")
    @ApiOperation("公开简历")
    public Result<Void> publishResume(@PathVariable Long id) {
        boolean success = resumeService.publishResume(id);
        return success ? Result.ok("公开成功") : Result.error("公开失败");
    }

    /**
     * 隐藏简历
     */
    @PostMapping("/{id}/hide")
    @ApiOperation("隐藏简历")
    public Result<Void> hideResume(@PathVariable Long id) {
        boolean success = resumeService.hideResume(id);
        return success ? Result.ok("隐藏成功") : Result.error("隐藏失败");
    }

    /**
     * 计算简历完整度
     */
    @GetMapping("/{id}/completeness")
    @ApiOperation("计算简历完整度")
    public Result<Integer> calculateCompleteness(@PathVariable Long id) {
        Integer completeness = resumeService.calculateCompleteness(id);
        return Result.ok(completeness);
    }

    /**
     * 更新简历完整度
     */
    @PostMapping("/{id}/update-completeness")
    @ApiOperation("更新简历完整度")
    public Result<Void> updateCompleteness(@PathVariable Long id) {
        boolean success = resumeService.updateCompleteness(id);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }
}
