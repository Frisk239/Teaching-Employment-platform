package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.Student;
import com.teaching.employment.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 学员管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/student")
@Api(tags = "学员管理")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 分页查询学员列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询学员列表")
    public Result<IPage<Student>> getStudentPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Student> page = studentService.getStudentPage(current, size, schoolId, keyword);
        return Result.ok(page);
    }

    /**
     * 根据ID查询学员
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询学员")
    public Result<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentWithDetails(id);
        return Result.ok(student);
    }

    /**
     * 根据学号查询学员
     */
    @GetMapping("/studentNo/{studentNo}")
    @ApiOperation("根据学号查询学员")
    public Result<Student> getStudentByStudentNo(@PathVariable String studentNo) {
        Student student = studentService.getStudentByStudentNo(studentNo);
        return Result.ok(student);
    }

    /**
     * 新增学员
     */
    @PostMapping
    @ApiOperation("新增学员")
    public Result<Void> createStudent(@RequestBody Student student) {
        boolean success = studentService.save(student);
        return success ? Result.ok("新增成功") : Result.error("新增失败");
    }

    /**
     * 更新学员
     */
    @PutMapping
    @ApiOperation("更新学员")
    public Result<Void> updateStudent(@RequestBody Student student) {
        boolean success = studentService.updateById(student);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除学员
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除学员")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        boolean success = studentService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * Excel导入学员
     */
    @PostMapping("/import")
    @ApiOperation("Excel导入学员")
    public Result<String> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            String result = studentService.importStudents(file);
            return Result.ok(result);
        } catch (IOException e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载学员导入模板
     */
    @GetMapping("/template")
    @ApiOperation("下载学员导入模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        studentService.downloadTemplate(response);
    }
}
