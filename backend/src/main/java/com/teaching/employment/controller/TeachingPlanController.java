package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.dto.TeachingPlanVO;
import com.teaching.employment.entity.TeachingPlan;
import com.teaching.employment.service.TeachingPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教学计划管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/teaching-plan")
@Api(tags = "教学计划管理")
@RequiredArgsConstructor
public class TeachingPlanController {

    private final TeachingPlanService teachingPlanService;

    /**
     * 根据课程ID获取教学计划列表
     */
    @GetMapping("/list/{courseId}")
    @ApiOperation("获取课程教学计划列表")
    public Result<List<TeachingPlan>> getListByCourseId(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        List<TeachingPlan> plans = teachingPlanService.getListByCourseId(courseId);
        return Result.ok(plans);
    }

    /**
     * 根据课程ID获取教学计划（包含时间信息）
     */
    @GetMapping("/detail/{courseId}")
    @ApiOperation("获取课程教学计划详情（含时间）")
    public Result<List<TeachingPlanVO>> getDetail(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        List<TeachingPlanVO> plans = teachingPlanService.getListWithTime(courseId);
        return Result.ok(plans);
    }

    /**
     * 创建教学计划
     */
    @PostMapping
    @ApiOperation("创建教学计划")
    public Result<TeachingPlan> create(@RequestBody TeachingPlan teachingPlan) {
        boolean success = teachingPlanService.save(teachingPlan);
        return success ? Result.ok(teachingPlan) : Result.error("创建失败");
    }

    /**
     * 更新教学计划
     */
    @PutMapping
    @ApiOperation("更新教学计划")
    public Result<Void> update(@RequestBody TeachingPlan teachingPlan) {
        boolean success = teachingPlanService.updateById(teachingPlan);
        return success ? Result.ok() : Result.error("更新失败");
    }

    /**
     * 删除教学计划
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除教学计划")
    public Result<Void> delete(@ApiParam("计划ID") @PathVariable Long id) {
        boolean success = teachingPlanService.removeById(id);
        return success ? Result.ok() : Result.error("删除失败");
    }

    /**
     * 批量保存教学计划
     */
    @PostMapping("/batch")
    @ApiOperation("批量保存教学计划")
    public Result<List<TeachingPlan>> batchSave(
            @RequestBody List<TeachingPlan> plans) {
        List<TeachingPlan> result = teachingPlanService.batchSave(plans);
        return Result.ok(result);
    }
}
