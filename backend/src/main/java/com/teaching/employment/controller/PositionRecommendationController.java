package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.PositionRecommendation;
import com.teaching.employment.service.PositionRecommendationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 职位推荐管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@RestController
@RequestMapping("/position-recommendation")
@Api(tags = "职位推荐管理")
@RequiredArgsConstructor
public class PositionRecommendationController {

    private final PositionRecommendationService positionRecommendationService;

    /**
     * 创建职位推荐
     */
    @PostMapping
    @ApiOperation("创建职位推荐")
    public Result<Void> createRecommendation(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("职位ID") @RequestParam Long positionId,
            @ApiParam("教师ID") @RequestParam Long teacherId,
            @ApiParam("推荐理由") @RequestParam(required = false) String reason,
            @ApiParam("备注") @RequestParam(required = false) String remark) {
        boolean success = positionRecommendationService.createRecommendation(studentId, positionId, teacherId, reason, remark);
        return success ? Result.ok("推荐成功") : Result.error("推荐失败");
    }

    /**
     * 获取学员的职位推荐列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("获取学员的职位推荐列表")
    public Result<IPage<PositionRecommendation>> getRecommendationsByStudent(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<PositionRecommendation> page = positionRecommendationService.getRecommendationsByStudent(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 获取教师创建的职位推荐列表
     */
    @GetMapping("/teacher/{teacherId}")
    @ApiOperation("获取教师创建的职位推荐列表")
    public Result<IPage<PositionRecommendation>> getRecommendationsByTeacher(
            @PathVariable Long teacherId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<PositionRecommendation> page = positionRecommendationService.getRecommendationsByTeacher(current, size, teacherId);
        return Result.ok(page);
    }

    /**
     * 标记推荐为已查看
     */
    @PutMapping("/{id}/view")
    @ApiOperation("标记推荐为已查看")
    public Result<Void> markAsViewed(@PathVariable Long id) {
        boolean success = positionRecommendationService.markAsViewed(id);
        return success ? Result.ok("标记成功") : Result.error("标记失败");
    }

    /**
     * 更新推荐状态
     */
    @PutMapping("/{id}/status")
    @ApiOperation("更新推荐状态")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @ApiParam("状态") @RequestParam String status) {
        boolean success = positionRecommendationService.updateStatus(id, status);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除职位推荐
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除职位推荐")
    public Result<Void> deleteRecommendation(@PathVariable Long id) {
        boolean success = positionRecommendationService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }
}
