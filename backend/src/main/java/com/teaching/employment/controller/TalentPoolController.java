package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.TalentPool;
import com.teaching.employment.service.TalentPoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 人才库管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/talent-pool")
@Api(tags = "人才库管理")
@RequiredArgsConstructor
public class TalentPoolController {

    private final TalentPoolService talentPoolService;

    /**
     * 分页查询人才库列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询人才库列表")
    public Result<IPage<TalentPool>> getTalentPoolPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("企业ID") @RequestParam(required = false) Long companyId,
            @ApiParam("人才分类") @RequestParam(required = false) String category,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("优先级") @RequestParam(required = false) String priority,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<TalentPool> page = talentPoolService.getTalentPoolPage(current, size, companyId,
                category, status, priority, keyword);
        return Result.ok(page);
    }

    /**
     * 添加候选人到人才库
     */
    @PostMapping
    @ApiOperation("添加候选人到人才库")
    public Result<Void> addToTalentPool(@RequestBody TalentPool talentPool) {
        boolean success = talentPoolService.addToTalentPool(talentPool);
        return success ? Result.ok("添加成功") : Result.error("添加失败");
    }

    /**
     * 从求职申请添加到人才库
     */
    @PostMapping("/add-from-application")
    @ApiOperation("从求职申请添加到人才库")
    public Result<Void> addFromApplication(
            @ApiParam("申请ID") @RequestParam Long applicationId,
            @ApiParam("企业ID") @RequestParam Long companyId,
            @ApiParam("备注") @RequestParam(required = false) String remark) {
        boolean success = talentPoolService.addFromApplication(applicationId, companyId, remark);
        return success ? Result.ok("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新人才库信息
     */
    @PutMapping
    @ApiOperation("更新人才库信息")
    public Result<Void> updateTalentPool(@RequestBody TalentPool talentPool) {
        boolean success = talentPoolService.updateTalentPool(talentPool);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除人才库记录
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除人才库记录")
    public Result<Void> deleteFromTalentPool(@PathVariable Long id) {
        boolean success = talentPoolService.deleteFromTalentPool(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除人才库记录
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除人才库记录")
    public Result<Void> batchDelete(@RequestBody Long[] ids) {
        boolean success = talentPoolService.batchDelete(ids);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新人才标签
     */
    @PutMapping("/{id}/tags")
    @ApiOperation("更新人才标签")
    public Result<Void> updateTags(
            @PathVariable Long id,
            @ApiParam("标签（逗号分隔）") @RequestParam String tags) {
        boolean success = talentPoolService.updateTags(id, tags);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 更新评分
     */
    @PutMapping("/{id}/rating")
    @ApiOperation("更新评分")
    public Result<Void> updateRating(
            @PathVariable Long id,
            @ApiParam("评分（1-5）") @RequestParam Integer rating) {
        boolean success = talentPoolService.updateRating(id, rating);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 更新优先级
     */
    @PutMapping("/{id}/priority")
    @ApiOperation("更新优先级")
    public Result<Void> updatePriority(
            @PathVariable Long id,
            @ApiParam("优先级") @RequestParam String priority) {
        boolean success = talentPoolService.updatePriority(id, priority);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 标记为已联系
     */
    @PostMapping("/{id}/mark-contacted")
    @ApiOperation("标记为已联系")
    public Result<Void> markAsContacted(
            @PathVariable Long id,
            @ApiParam("联系方式") @RequestParam String contactMethod,
            @ApiParam("下次联系日期") @RequestParam(required = false) String nextContactDate) {
        boolean success = talentPoolService.markAsContacted(id, contactMethod, nextContactDate);
        return success ? Result.ok("标记成功") : Result.error("标记失败");
    }

    /**
     * 检查候选人是否已在人才库中
     */
    @GetMapping("/exists")
    @ApiOperation("检查候选人是否已在人才库中")
    public Result<Boolean> existsInTalentPool(
            @ApiParam("企业ID") @RequestParam Long companyId,
            @ApiParam("学员ID") @RequestParam Long studentId) {
        boolean exists = talentPoolService.existsInTalentPool(companyId, studentId);
        return Result.ok(exists);
    }

    /**
     * 获取人才库统计信息
     */
    @GetMapping("/stats/company/{companyId}")
    @ApiOperation("获取人才库统计信息")
    public Result<Map<String, Object>> getTalentPoolStats(@PathVariable Long companyId) {
        Map<String, Object> stats = talentPoolService.getTalentPoolStats(companyId);
        return Result.ok(stats);
    }

    /**
     * 根据ID查询人才库信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询人才库信息")
    public Result<TalentPool> getTalentPoolById(@PathVariable Long id) {
        TalentPool talentPool = talentPoolService.getById(id);
        return Result.ok(talentPool);
    }
}
