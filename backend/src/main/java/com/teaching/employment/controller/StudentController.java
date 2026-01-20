package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.*;
import com.teaching.employment.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 学员管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@RestController
@RequestMapping("/student")
@Api(tags = "学员管理")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentSkillService studentSkillService;
    private final StudentProjectService studentProjectService;
    private final StudentPreferenceService studentPreferenceService;
    private final StudentResumeService studentResumeService;
    private final StudentCourseService studentCourseService;
    private final StudentEducationService studentEducationService;
    private final Environment environment;

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

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
     * 获取学员列表（不分页，用于下拉框选择）
     */
    @GetMapping("/list")
    @ApiOperation("获取学员列表（不分页）")
    public Result<List<Student>> getStudentList(
            @ApiParam("每页大小") @RequestParam(defaultValue = "1000") Integer size,
            @ApiParam("学校ID") @RequestParam(required = false) Long schoolId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Student> page = studentService.getStudentPage(1, size, schoolId, keyword);
        return Result.ok(page.getRecords());
    }

    /**
     * 获取所有专业列表（用于下拉框筛选）
     */
    @GetMapping("/majors")
    @ApiOperation("获取所有专业列表")
    public Result<List<String>> getMajorList() {
        List<String> majors = studentService.getMajorList();
        return Result.ok(majors);
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

    /**
     * Excel导出学员
     */
    @GetMapping("/export")
    @ApiOperation("Excel导出学员")
    public void exportStudents(HttpServletResponse response) throws IOException {
        studentService.exportStudents(response);
    }

    // ==================== 学员档案相关API ====================

    /**
     * 获取学员技能标签列表
     */
    @GetMapping("/{studentId}/skills")
    @ApiOperation("获取学员技能标签列表")
    public Result<List<StudentSkill>> getStudentSkills(@PathVariable Long studentId) {
        LambdaQueryWrapper<StudentSkill> wrapper = new LambdaQueryWrapper<StudentSkill>()
                .eq(StudentSkill::getStudentId, studentId);
        List<StudentSkill> skills = studentSkillService.list(wrapper);
        return Result.ok(skills);
    }

    /**
     * 获取学员项目经验列表
     */
    @GetMapping("/{studentId}/projects")
    @ApiOperation("获取学员项目经验列表")
    public Result<List<StudentProject>> getStudentProjects(@PathVariable Long studentId) {
        LambdaQueryWrapper<StudentProject> wrapper = new LambdaQueryWrapper<StudentProject>()
                .eq(StudentProject::getStudentId, studentId)
                .orderByDesc(StudentProject::getCreateTime);
        List<StudentProject> projects = studentProjectService.list(wrapper);
        return Result.ok(projects);
    }

    /**
     * 获取学员求职偏好
     */
    @GetMapping("/{studentId}/preference")
    @ApiOperation("获取学员求职偏好")
    public Result<StudentPreference> getStudentPreference(@PathVariable Long studentId) {
        LambdaQueryWrapper<StudentPreference> wrapper = new LambdaQueryWrapper<StudentPreference>()
                .eq(StudentPreference::getStudentId, studentId);
        StudentPreference preference = studentPreferenceService.getOne(wrapper);
        return Result.ok(preference);
    }

    /**
     * 更新学员求职偏好
     */
    @PutMapping("/{studentId}/preference")
    @ApiOperation("更新学员求职偏好")
    public Result<Void> updateStudentPreference(
            @PathVariable Long studentId,
            @RequestBody StudentPreference preference) {
        preference.setStudentId(studentId);
        LambdaQueryWrapper<StudentPreference> wrapper = new LambdaQueryWrapper<StudentPreference>()
                .eq(StudentPreference::getStudentId, studentId);
        StudentPreference existing = studentPreferenceService.getOne(wrapper);
        if (existing != null) {
            preference.setId(existing.getId());
            studentPreferenceService.updateById(preference);
        } else {
            studentPreferenceService.save(preference);
        }
        return Result.ok("保存成功");
    }

    /**
     * 获取学员简历列表
     */
    @GetMapping("/{studentId}/resumes")
    @ApiOperation("获取学员简历列表")
    public Result<List<StudentResume>> getStudentResumes(@PathVariable Long studentId) {
        LambdaQueryWrapper<StudentResume> wrapper = new LambdaQueryWrapper<StudentResume>()
                .eq(StudentResume::getStudentId, studentId)
                .orderByDesc(StudentResume::getUploadTime);
        List<StudentResume> resumes = studentResumeService.list(wrapper);
        return Result.ok(resumes);
    }

    /**
     * 获取学员课程成绩列表
     */
    @GetMapping("/{studentId}/courses")
    @ApiOperation("获取学员课程成绩列表")
    public Result<List<StudentCourse>> getStudentCourses(@PathVariable Long studentId) {
        LambdaQueryWrapper<StudentCourse> wrapper = new LambdaQueryWrapper<StudentCourse>()
                .eq(StudentCourse::getStudentId, studentId)
                .orderByDesc(StudentCourse::getCreateTime);
        List<StudentCourse> courses = studentCourseService.list(wrapper);
        return Result.ok(courses);
    }

    /**
     * 获取学员教育经历
     */
    @GetMapping("/{studentId}/education")
    @ApiOperation("获取学员教育经历")
    public Result<StudentEducation> getStudentEducation(@PathVariable Long studentId) {
        LambdaQueryWrapper<StudentEducation> wrapper = new LambdaQueryWrapper<StudentEducation>()
                .eq(StudentEducation::getStudentId, studentId);
        StudentEducation education = studentEducationService.getOne(wrapper);
        return Result.ok(education);
    }

    /**
     * 保存学员简历记录
     */
    @PostMapping("/{studentId}/resume")
    @ApiOperation("保存学员简历记录")
    public Result<Void> saveResume(
            @PathVariable Long studentId,
            @RequestBody StudentResume resume) {
        try {
            resume.setStudentId(studentId);
            resume.setUploadTime(java.time.LocalDateTime.now());
            boolean success = studentResumeService.save(resume);
            return success ? Result.ok("保存成功") : Result.error("保存失败");
        } catch (Exception e) {
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除学员简历
     */
    @DeleteMapping("/{studentId}/resume/{resumeId}")
    @ApiOperation("删除学员简历")
    public Result<Void> deleteResume(
            @PathVariable Long studentId,
            @PathVariable Long resumeId) {
        try {
            // 验证简历是否属于该学员
            LambdaQueryWrapper<StudentResume> wrapper = new LambdaQueryWrapper<StudentResume>()
                    .eq(StudentResume::getStudentId, studentId)
                    .eq(StudentResume::getId, resumeId);
            StudentResume resume = studentResumeService.getOne(wrapper);

            if (resume == null) {
                return Result.error("简历不存在或无权删除");
            }

            // 删除物理文件
            if (resume.getFileUrl() != null && !resume.getFileUrl().isEmpty()) {
                try {
                    // 从URL中提取相对路径
                    // URL格式: http://localhost:8080/api/uploads/resume/2026/01/19/xxx.pdf
                    String url = resume.getFileUrl();
                    int uploadsIndex = url.indexOf("/uploads/");
                    if (uploadsIndex != -1) {
                        // 去掉 "/uploads/" 之前的部分（包括 "/uploads/"）
                        String relativePath = url.substring(uploadsIndex + "/uploads/".length());

                        // uploadPath 已经包含了 uploads 目录
                        // 所以直接用 uploadPath 作为基础路径
                        java.io.File file = new java.io.File(uploadPath, relativePath);

                        log.info("准备删除文件: {}", file.getAbsolutePath());

                        if (file.exists()) {
                            boolean deleted = file.delete();
                            log.info("删除物理文件: {}, 结果: {}", file.getAbsolutePath(), deleted ? "成功" : "失败");

                            if (!deleted) {
                                log.warn("文件删除失败，可能是文件被占用或权限不足");
                            }
                        } else {
                            log.warn("物理文件不存在: {}", file.getAbsolutePath());
                        }
                    } else {
                        log.warn("无法从URL中解析文件路径: {}", url);
                    }
                } catch (Exception e) {
                    log.error("删除物理文件失败", e);
                    // 即使删除物理文件失败，也继续删除数据库记录
                }
            }

            // 删除数据库记录
            boolean success = studentResumeService.removeById(resumeId);
            return success ? Result.ok("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除简历失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
