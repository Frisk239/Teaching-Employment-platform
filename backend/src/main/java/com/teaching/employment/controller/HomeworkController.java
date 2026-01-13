package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Homework;
import com.teaching.employment.service.HomeworkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作业管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/homework")
@Api(tags = "作业管理")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    /**
     * 分页查询作业列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询作业列表")
    public Result<IPage<Homework>> getHomeworkPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("教师ID") @RequestParam(required = false) Long teacherId,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Homework> page = homeworkService.getHomeworkPage(current, size, courseId, teacherId, status, keyword);
        return Result.ok(page);
    }

    /**
     * 根据课程ID查询作业列表
     */
    @GetMapping("/course/{courseId}")
    @ApiOperation("根据课程ID查询作业列表")
    public Result<IPage<Homework>> getHomeworkByCourseId(
            @PathVariable Long courseId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Homework> page = homeworkService.getHomeworkByCourseId(current, size, courseId);
        return Result.ok(page);
    }

    /**
     * 根据学生ID查询作业列表（学生端）
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("根据学生ID查询作业列表")
    public Result<IPage<Homework>> getHomeworkByStudentId(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Homework> page = homeworkService.getHomeworkByStudentId(current, size, studentId);
        return Result.ok(page);
    }

    /**
     * 根据ID查询作业
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询作业")
    public Result<Homework> getHomeworkById(@PathVariable Long id) {
        Homework homework = homeworkService.getById(id);
        return Result.ok(homework);
    }

    /**
     * 新增作业
     */
    @PostMapping
    @ApiOperation("新增作业")
    public Result<Void> createHomework(@RequestBody Homework homework) {
        // 默认状态为草稿
        if (homework.getStatus() == null) {
            homework.setStatus("draft");
        }
        boolean success = homeworkService.save(homework);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新作业
     */
    @PutMapping
    @ApiOperation("更新作业")
    public Result<Void> updateHomework(@RequestBody Homework homework) {
        boolean success = homeworkService.updateById(homework);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除作业")
    public Result<Void> deleteHomework(@PathVariable Long id) {
        boolean success = homeworkService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 发布作业
     */
    @PostMapping("/{id}/publish")
    @ApiOperation("发布作业")
    public Result<Void> publishHomework(@PathVariable Long id) {
        boolean success = homeworkService.publishHomework(id);
        return success ? Result.ok("发布成功") : Result.error("发布失败");
    }

    /**
     * 关闭作业
     */
    @PostMapping("/{id}/close")
    @ApiOperation("关闭作业")
    public Result<Void> closeHomework(@PathVariable Long id) {
        boolean success = homeworkService.closeHomework(id);
        return success ? Result.ok("关闭成功") : Result.error("关闭失败");
    }

    /**
     * 获取作业统计信息
     */
    @GetMapping("/{id}/stats")
    @ApiOperation("获取作业统计信息")
    public Result<Map<String, Object>> getHomeworkStats(@PathVariable Long id) {
        Map<String, Object> stats = homeworkService.getHomeworkStats(id);
        return Result.ok(stats);
    }
}
