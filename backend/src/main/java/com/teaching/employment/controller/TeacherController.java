package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/teacher")
@Api(tags = "教师管理")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 分页查询教师列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询教师列表")
    public Result<IPage<Teacher>> getTeacherPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Teacher> page = teacherService.getTeacherPage(current, size, schoolId, keyword);
        return Result.ok(page);
    }

    /**
     * 根据ID查询教师
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询教师")
    public Result<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return Result.ok(teacher);
    }

    /**
     * 新增教师
     */
    @PostMapping
    @ApiOperation("新增教师")
    public Result<Void> createTeacher(@RequestBody Teacher teacher) {
        boolean success = teacherService.save(teacher);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新教师
     */
    @PutMapping
    @ApiOperation("更新教师")
    public Result<Void> updateTeacher(@RequestBody Teacher teacher) {
        boolean success = teacherService.updateById(teacher);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除教师")
    public Result<Void> deleteTeacher(@PathVariable Long id) {
        boolean success = teacherService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取教师工作负载统计
     */
    @GetMapping("/{id}/workload")
    @ApiOperation("获取教师工作负载统计")
    public Result<Map<String, Object>> getTeacherWorkload(@PathVariable Long id) {
        Map<String, Object> workload = teacherService.getTeacherWorkload(id);
        return Result.ok(workload);
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/{id}/courses")
    @ApiOperation("获取教师的课程列表")
    public Result<List<Course>> getTeacherCourses(@PathVariable Long id) {
        List<Course> courses = teacherService.getTeacherCourses(id);
        return Result.ok(courses);
    }
}
