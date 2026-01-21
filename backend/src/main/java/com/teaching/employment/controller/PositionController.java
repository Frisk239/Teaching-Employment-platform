package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Position;
import com.teaching.employment.service.PositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 招聘职位管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/position")
@Api(tags = "招聘职位管理")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * 获取所有职位列表（用于推荐职位下拉框）
     */
    @GetMapping("/list")
    @ApiOperation("获取所有职位列表")
    public Result<java.util.List<Position>> getPositionList() {
        java.util.List<Position> list = positionService.getPositionsWithCompany();
        return Result.ok(list);
    }

    /**
     * 分页查询职位列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询职位列表")
    public Result<IPage<Position>> getPositionPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("企业ID") @RequestParam(required = false) Long companyId,
            @ApiParam("职位类型") @RequestParam(required = false) String positionType,
            @ApiParam("工作城市") @RequestParam(required = false) String city,
            @ApiParam("学历要求") @RequestParam(required = false) String education,
            @ApiParam("职位状态") @RequestParam(required = false) String status,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("最低薪资") @RequestParam(required = false) BigDecimal salaryMin,
            @ApiParam("最高薪资") @RequestParam(required = false) BigDecimal salaryMax) {
        IPage<Position> page = positionService.getPositionPage(current, size, companyId, positionType,
                city, education, status, keyword, salaryMin, salaryMax);
        return Result.ok(page);
    }

    /**
     * 根据企业ID查询职位列表
     */
    @GetMapping("/company/{companyId}")
    @ApiOperation("根据企业ID查询职位列表")
    public Result<IPage<Position>> getPositionsByCompanyId(
            @PathVariable Long companyId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Position> page = positionService.getPositionsByCompanyId(current, size, companyId);
        return Result.ok(page);
    }

    /**
     * 根据ID查询职位
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询职位")
    public Result<Position> getPositionById(@PathVariable Long id) {
        Position position = positionService.getPositionByIdWithCompany(id);
        return Result.ok(position);
    }

    /**
     * 新增职位
     */
    @PostMapping
    @ApiOperation("新增职位")
    public Result<Void> createPosition(@RequestBody Position position) {
        // 默认状态为草稿
        if (position.getStatus() == null) {
            position.setStatus("draft");
        }
        // 默认已招人数为0
        if (position.getHiredCount() == null) {
            position.setHiredCount(0);
        }
        // 默认投递数为0
        if (position.getApplicationCount() == null) {
            position.setApplicationCount(0);
        }
        boolean success = positionService.save(position);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新职位
     */
    @PutMapping
    @ApiOperation("更新职位")
    public Result<Void> updatePosition(@RequestBody Position position) {
        boolean success = positionService.updateById(position);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除职位
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除职位")
    public Result<Void> deletePosition(@PathVariable Long id) {
        boolean success = positionService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 发布职位
     */
    @PostMapping("/{id}/publish")
    @ApiOperation("发布职位")
    public Result<Void> publishPosition(@PathVariable Long id) {
        boolean success = positionService.publishPosition(id);
        return success ? Result.ok("发布成功") : Result.error("发布失败");
    }

    /**
     * 暂停职位招聘
     */
    @PostMapping("/{id}/pause")
    @ApiOperation("暂停职位招聘")
    public Result<Void> pausePosition(@PathVariable Long id) {
        boolean success = positionService.pausePosition(id);
        return success ? Result.ok("暂停成功") : Result.error("暂停失败");
    }

    /**
     * 关闭职位
     */
    @PostMapping("/{id}/close")
    @ApiOperation("关闭职位")
    public Result<Void> closePosition(@PathVariable Long id) {
        boolean success = positionService.closePosition(id);
        return success ? Result.ok("关闭成功") : Result.error("关闭失败");
    }

    /**
     * 增加投递数量
     */
    @PostMapping("/{id}/increment-application")
    @ApiOperation("增加投递数量")
    public Result<Void> incrementApplicationCount(@PathVariable Long id) {
        boolean success = positionService.incrementApplicationCount(id);
        return success ? Result.ok("操作成功") : Result.error("操作失败");
    }

    /**
     * 增加已招人数
     */
    @PostMapping("/{id}/increment-hired")
    @ApiOperation("增加已招人数")
    public Result<Void> incrementHiredCount(@PathVariable Long id) {
        boolean success = positionService.incrementHiredCount(id);
        return success ? Result.ok("操作成功") : Result.error("操作失败");
    }

    /**
     * 获取职位统计信息
     */
    @GetMapping("/{id}/stats")
    @ApiOperation("获取职位统计信息")
    public Result<Map<String, Object>> getPositionStats(@PathVariable Long id) {
        Map<String, Object> stats = positionService.getPositionStats(id);
        return Result.ok(stats);
    }
}
