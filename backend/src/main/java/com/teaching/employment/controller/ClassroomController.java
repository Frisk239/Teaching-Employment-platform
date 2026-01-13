package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Classroom;
import com.teaching.employment.entity.Course;
import com.teaching.employment.service.ClassroomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教室管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/classroom")
@Api(tags = "教室管理")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    /**
     * 分页查询教室列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询教室列表")
    public Result<IPage<Classroom>> getClassroomPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Classroom> page = classroomService.getClassroomPage(current, size, schoolId, keyword);
        return Result.ok(page);
    }

    /**
     * 查询所有教室(用于下拉框)
     */
    @GetMapping("/list")
    @ApiOperation("查询所有教室")
    public Result<List<Classroom>> getClassroomList(
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId) {
        List<Classroom> list = classroomService.getClassroomList(schoolId);
        return Result.ok(list);
    }

    /**
     * 根据ID查询教室
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询教室")
    public Result<Classroom> getClassroomById(@PathVariable Long id) {
        Classroom classroom = classroomService.getById(id);
        return Result.ok(classroom);
    }

    /**
     * 新增教室
     */
    @PostMapping
    @ApiOperation("新增教室")
    public Result<Void> createClassroom(@RequestBody Classroom classroom) {
        boolean success = classroomService.save(classroom);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新教室
     */
    @PutMapping
    @ApiOperation("更新教室")
    public Result<Void> updateClassroom(@RequestBody Classroom classroom) {
        boolean success = classroomService.updateById(classroom);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除教室
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除教室")
    public Result<Void> deleteClassroom(@PathVariable Long id) {
        boolean success = classroomService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取教室使用统计
     */
    @GetMapping("/{id}/usage-statistics")
    @ApiOperation("获取教室使用统计")
    public Result<Map<String, Object>> getClassroomUsageStatistics(@PathVariable Long id) {
        Map<String, Object> statistics = classroomService.getClassroomUsageStatistics(id);
        return Result.ok(statistics);
    }

    /**
     * 获取教室的课程列表
     */
    @GetMapping("/{id}/courses")
    @ApiOperation("获取教室的课程列表")
    public Result<List<Course>> getClassroomCourses(@PathVariable Long id) {
        List<Course> courses = classroomService.getClassroomCourses(id);
        return Result.ok(courses);
    }

    /**
     * 查询可用教室
     */
    @GetMapping("/available")
    @ApiOperation("查询可用教室")
    public Result<List<Classroom>> getAvailableClassrooms(
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("最小容纳人数") @RequestParam(required = false) Integer minCapacity,
            @ApiParam("是否需要投影仪") @RequestParam(required = false) Integer hasProjector,
            @ApiParam("是否需要电脑") @RequestParam(required = false) Integer hasComputer) {
        List<Classroom> classrooms = classroomService.getAvailableClassrooms(schoolId, minCapacity, hasProjector, hasComputer);
        return Result.ok(classrooms);
    }
}
