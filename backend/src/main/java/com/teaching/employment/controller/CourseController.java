package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Course;
import com.teaching.employment.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/course")
@Api(tags = "课程管理")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 分页查询课程列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询课程列表")
    public Result<IPage<Course>> getCoursePage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("教师ID") @RequestParam(required = false) Long teacherId,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Course> page = courseService.getCoursePage(current, size, schoolId, teacherId, status, keyword);
        return Result.ok(page);
    }

    /**
     * 根据ID查询课程
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询课程")
    public Result<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return Result.ok(course);
    }

    /**
     * 新增课程
     */
    @PostMapping
    @ApiOperation("新增课程")
    public Result<Void> createCourse(@RequestBody Course course) {
        boolean success = courseService.save(course);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新课程
     */
    @PutMapping
    @ApiOperation("更新课程")
    public Result<Void> updateCourse(@RequestBody Course course) {
        boolean success = courseService.updateById(course);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除课程")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        boolean success = courseService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 查询学生课程表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("查询学生课程表")
    public Result<List<Course>> getCoursesByStudentId(@PathVariable Long studentId) {
        List<Course> courses = courseService.getCoursesByStudentId(studentId);
        return Result.ok(courses);
    }

    /**
     * 查询教师的课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    @ApiOperation("查询教师的课程列表")
    public Result<List<Course>> getCoursesByTeacherId(@PathVariable Long teacherId) {
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        return Result.ok(courses);
    }

    /**
     * 添加学员到课程
     */
    @PostMapping("/{courseId}/student")
    @ApiOperation("添加学员到课程")
    public Result<Void> addStudentToCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, Long> request) {
        Long studentId = request.get("studentId");
        boolean success = courseService.addStudentToCourse(courseId, studentId);
        return success ? Result.ok("添加成功") : Result.error("添加失败");
    }

    /**
     * 批量添加学员到课程
     */
    @PostMapping("/{courseId}/students/batch")
    @ApiOperation("批量添加学员到课程")
    public Result<Void> batchAddStudentsToCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> studentIds = request.get("studentIds");
        boolean success = courseService.batchAddStudentsToCourse(courseId, studentIds);
        return success ? Result.ok("批量添加成功") : Result.error("批量添加失败");
    }

    /**
     * 从课程中移除学员
     */
    @DeleteMapping("/{courseId}/student/{studentId}")
    @ApiOperation("从课程中移除学员")
    public Result<Void> removeStudentFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        boolean success = courseService.removeStudentFromCourse(courseId, studentId);
        return success ? Result.ok("移除成功") : Result.error("移除失败");
    }

    /**
     * 为课程分配教师
     */
    @PutMapping("/{courseId}/teacher")
    @ApiOperation("为课程分配教师")
    public Result<Void> assignTeacherToCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, Long> request) {
        Long teacherId = request.get("teacherId");
        Course course = courseService.getById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }
        course.setTeacherId(teacherId);
        boolean success = courseService.updateById(course);
        return success ? Result.ok("教师分配成功") : Result.error("教师分配失败");
    }
}
