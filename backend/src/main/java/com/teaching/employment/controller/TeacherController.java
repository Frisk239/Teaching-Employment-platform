package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.User;
import com.teaching.employment.mapper.TeacherMapper;
import com.teaching.employment.mapper.UserMapper;
import com.teaching.employment.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;

    /**
     * 分页查询教师列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询教师列表")
    public Result<IPage<Teacher>> getTeacherPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("部门") @RequestParam(required = false) String department) {
        IPage<Teacher> page = teacherService.getTeacherPage(current, size, schoolId, keyword, department);
        return Result.ok(page);
    }

    /**
     * 查询所有教师(用于下拉框)
     */
    @GetMapping("/list")
    @ApiOperation("查询所有教师")
    public Result<List<Teacher>> getTeacherList(
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId) {
        List<Teacher> list = teacherService.getTeacherList(schoolId);
        return Result.ok(list);
    }

    /**
     * 获取教师统计数据
     */
    @GetMapping("/stats")
    @ApiOperation("获取教师统计数据")
    public Result<Map<String, Object>> getTeacherStatistics() {
        Map<String, Object> stats = teacherService.getTeacherStatistics();
        return Result.ok(stats);
    }

    /**
     * 根据ID查询教师
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询教师")
    public Result<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        if (teacher != null) {
            // 填充关联数据(学校名称、用户姓名、手机号、邮箱)
            List<Teacher> teachers = new ArrayList<>();
            teachers.add(teacher);
            teacherService.fillRelatedData(teachers);
        }
        return Result.ok(teacher);
    }

    /**
     * 新增教师
     */
    @PostMapping
    @ApiOperation("新增教师")
    public Result<Void> createTeacher(@RequestBody Teacher teacher) {
        // 先创建用户账号
        User user = new User();
        user.setUsername(teacher.getTeacherNo()); // 使用工号作为用户名
        user.setPassword("123456"); // 默认密码
        user.setRealName(teacher.getName());
        user.setPhone(teacher.getPhone());
        user.setEmail(teacher.getEmail());
        user.setSchoolId(teacher.getSchoolId());
        user.setRoleId(3L); // 3-教师角色
        user.setStatus(1);

        int userResult = userMapper.insert(user);
        if (userResult <= 0) {
            return Result.error("创建用户账号失败");
        }

        // 设置用户ID到教师记录
        teacher.setUserId(user.getId());

        // 保存教师记录
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

    /**
     * 导出教师数据
     */
    @GetMapping("/export")
    @ApiOperation("Excel导出教师")
    public void exportTeachers(HttpServletResponse response) throws IOException {
        teacherService.exportTeachers(response);
    }

    /**
     * 导入教师数据
     */
    @PostMapping("/import")
    @ApiOperation("Excel导入教师")
    public Result<String> importTeachers(@RequestParam("file") MultipartFile file) {
        try {
            String result = teacherService.importTeachers(file);
            return Result.ok(result);
        } catch (IOException e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载教师导入模板
     */
    @GetMapping("/template")
    @ApiOperation("下载教师导入模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        teacherService.downloadTemplate(response);
    }
}
