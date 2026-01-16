package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Timetable;
import com.teaching.employment.service.TimetableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程表Controller
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-16
 */
@Slf4j
@RestController
@RequestMapping("/timetable")
@Api(tags = "课程表管理")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    /**
     * 获取学生的课程表列表
     */
    @GetMapping("/student/{studentId}")
    @ApiOperation("获取学生的课程表列表")
    public Result<List<Timetable>> getTimetableByStudentId(
            @ApiParam("学生ID") @PathVariable Long studentId) {
        try {
            List<Timetable> timetables = timetableService.getTimetableByStudentId(studentId);
            return Result.ok("查询成功", timetables);
        } catch (Exception e) {
            log.error("获取学生课程表失败", e);
            return Result.error("获取学生课程表失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生的课程表（按学期和学年筛选）
     */
    @GetMapping("/student/{studentId}/term")
    @ApiOperation("获取学生的课程表（按学期和学年）")
    public Result<List<Timetable>> getTimetableByStudentIdAndTerm(
            @ApiParam("学生ID") @PathVariable Long studentId,
            @ApiParam("学期") @RequestParam(required = false) String semester,
            @ApiParam("学年") @RequestParam(required = false) String academicYear) {
        try {
            List<Timetable> timetables = timetableService.getTimetableByStudentIdAndTerm(
                    studentId, semester, academicYear);
            return Result.ok("查询成功", timetables);
        } catch (Exception e) {
            log.error("获取学生课程表失败", e);
            return Result.error("获取学生课程表失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生的课程表（二维数组格式）
     */
    @GetMapping("/student/{studentId}/grid")
    @ApiOperation("获取学生的课程表（二维数组格式）")
    public Result<Map<Integer, Map<Integer, Timetable>>> getTimetableGrid(
            @ApiParam("学生ID") @PathVariable Long studentId,
            @ApiParam("学期") @RequestParam(required = false) String semester,
            @ApiParam("学年") @RequestParam(required = false) String academicYear) {
        try {
            Map<Integer, Map<Integer, Timetable>> grid = timetableService.getTimetableGrid(
                    studentId, semester, academicYear);
            return Result.ok("查询成功", grid);
        } catch (Exception e) {
            log.error("获取学生课程表失败", e);
            return Result.error("获取学生课程表失败: " + e.getMessage());
        }
    }

    /**
     * 添加课程表记录
     */
    @PostMapping
    @ApiOperation("添加课程表记录")
    public Result<Void> addTimetable(@RequestBody Timetable timetable) {
        try {
            timetableService.addTimetable(timetable);
            return Result.ok("添加成功");
        } catch (Exception e) {
            log.error("添加课程表失败", e);
            return Result.error("添加课程表失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加课程表记录
     */
    @PostMapping("/batch")
    @ApiOperation("批量添加课程表记录")
    public Result<Void> batchAddTimetable(@RequestBody List<Timetable> timetables) {
        try {
            timetableService.batchAddTimetable(timetables);
            return Result.ok("批量添加成功");
        } catch (Exception e) {
            log.error("批量添加课程表失败", e);
            return Result.error("批量添加课程表失败: " + e.getMessage());
        }
    }

    /**
     * 更新课程表记录
     */
    @PutMapping
    @ApiOperation("更新课程表记录")
    public Result<Void> updateTimetable(@RequestBody Timetable timetable) {
        try {
            timetableService.updateTimetable(timetable);
            return Result.ok("更新成功");
        } catch (Exception e) {
            log.error("更新课程表失败", e);
            return Result.error("更新课程表失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程表记录
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除课程表记录")
    public Result<Void> deleteTimetable(@ApiParam("课程表ID") @PathVariable Long id) {
        try {
            timetableService.deleteTimetable(id);
            return Result.ok("删除成功");
        } catch (Exception e) {
            log.error("删除课程表失败", e);
            return Result.error("删除课程表失败: " + e.getMessage());
        }
    }
}
