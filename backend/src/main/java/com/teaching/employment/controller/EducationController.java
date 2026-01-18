package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Education;
import com.teaching.employment.service.EducationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教育经历管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
@RestController
@RequestMapping("/education")
@Api(tags = "教育经历管理")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    /**
     * 根据用户ID查询教育经历列表
     */
    @GetMapping("/user/{userId}")
    @ApiOperation("根据用户ID查询教育经历列表")
    public Result<List<Education>> getEducationByUserId(
            @ApiParam("用户ID") @PathVariable Long userId) {
        List<Education> list = educationService.getEducationByUserId(userId);
        return Result.ok(list);
    }

    /**
     * 根据ID查询教育经历详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询教育经历详情")
    public Result<Education> getEducationById(
            @ApiParam("教育经历ID") @PathVariable Long id) {
        Education education = educationService.getEducationById(id);
        return Result.ok(education);
    }

    /**
     * 新增教育经历
     */
    @PostMapping
    @ApiOperation("新增教育经历")
    public Result<Void> createEducation(@RequestBody Education education) {
        boolean success = educationService.createEducation(education);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新教育经历
     */
    @PutMapping
    @ApiOperation("更新教育经历")
    public Result<Void> updateEducation(@RequestBody Education education) {
        boolean success = educationService.updateEducation(education);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除教育经历
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除教育经历")
    public Result<Void> deleteEducation(
            @ApiParam("教育经历ID") @PathVariable Long id) {
        boolean success = educationService.deleteEducation(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }
}
