package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.GuidanceRecord;
import com.teaching.employment.service.GuidanceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 指导记录管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@RestController
@RequestMapping("/guidance-record")
@Api(tags = "指导记录管理")
@RequiredArgsConstructor
public class GuidanceRecordController {

    private final GuidanceRecordService guidanceRecordService;

    /**
     * 创建指导记录
     */
    @PostMapping
    @ApiOperation("创建指导记录")
    public Result<Void> createGuidance(
            @ApiParam("学员ID") @RequestParam Long studentId,
            @ApiParam("教师ID") @RequestParam Long teacherId,
            @ApiParam("指导类型") @RequestParam String guidanceType,
            @ApiParam("指导内容") @RequestParam String content,
            @ApiParam("后续跟进计划") @RequestParam(required = false) String nextAction,
            @ApiParam("指导时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime guidanceDate,
            @ApiParam("指导地点") @RequestParam(required = false) String location) {
        boolean success = guidanceRecordService.createGuidance(studentId, teacherId, guidanceType, content, nextAction, guidanceDate, location);
        return success ? Result.ok("添加成功") : Result.error("添加失败");
    }

    /**
     * 获取学员的指导记录列表（不分页）
     */
    @GetMapping("/student/{studentId}/list")
    @ApiOperation("获取学员的指导记录列表（不分页）")
    public Result<java.util.List<GuidanceRecord>> getGuidanceListByStudent(@PathVariable Long studentId) {
        java.util.List<GuidanceRecord> list = guidanceRecordService.getGuidanceListByStudent(studentId);
        return Result.ok(list);
    }

    /**
     * 获取学员的指导记录列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("获取学员的指导记录列表")
    public Result<IPage<GuidanceRecord>> getGuidancesByStudent(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<GuidanceRecord> page = guidanceRecordService.getGuidancesByStudent(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 获取教师创建的指导记录列表
     */
    @GetMapping("/teacher/{teacherId}")
    @ApiOperation("获取教师创建的指导记录列表")
    public Result<IPage<GuidanceRecord>> getGuidancesByTeacher(
            @PathVariable Long teacherId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<GuidanceRecord> page = guidanceRecordService.getGuidancesByTeacher(current, size, teacherId);
        return Result.ok(page);
    }

    /**
     * 获取指导记录详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取指导记录详情")
    public Result<GuidanceRecord> getGuidanceById(@PathVariable Long id) {
        GuidanceRecord guidance = guidanceRecordService.getGuidanceWithDetails(id);
        return Result.ok(guidance);
    }

    /**
     * 更新指导记录
     */
    @PutMapping
    @ApiOperation("更新指导记录")
    public Result<Void> updateGuidance(@RequestBody GuidanceRecord guidance) {
        boolean success = guidanceRecordService.updateById(guidance);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除指导记录
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除指导记录")
    public Result<Void> deleteGuidance(@PathVariable Long id) {
        boolean success = guidanceRecordService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }
}
