package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.*;
import com.teaching.employment.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师工作台控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/teacher-dashboard")
@Api(tags = "教师工作台")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final CourseService courseService;
    private final HomeworkService homeworkService;
    private final HomeworkSubmissionService homeworkSubmissionService;
    private final StudentService studentService;
    private final CourseStudentService courseStudentService;

    /**
     * 获取教师工作台统计数据
     */
    @GetMapping("/stats/{teacherId}")
    @ApiOperation("获取教师工作台统计数据")
    public Result<Map<String, Object>> getTeacherStats(@PathVariable Long teacherId) {
        Map<String, Object> data = new HashMap<>();

        // 我的课程数
        long myCourses = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .count();

        // 我的学生数（通过课程关联计算）
        List<Course> courses = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list();

        long myStudents = 0;
        if (!courses.isEmpty()) {
            List<Long> courseIds = courses.stream()
                    .map(Course::getId)
                    .collect(Collectors.toList());

            List<com.teaching.employment.entity.CourseStudent> courseStudents =
                    courseStudentService.lambdaQuery()
                            .in(com.teaching.employment.entity.CourseStudent::getCourseId, courseIds)
                            .list();

            myStudents = courseStudents.stream()
                    .map(com.teaching.employment.entity.CourseStudent::getStudentId)
                    .distinct()
                    .count();
        }

        // 待批改作业数
        List<Long> courseIds2 = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());

        long pendingHomework = 0;
        if (!courseIds2.isEmpty()) {
            List<Homework> homeworks = homeworkService.lambdaQuery()
                    .in(Homework::getCourseId, courseIds2)
                    .eq(Homework::getStatus, "published")
                    .list();

            if (!homeworks.isEmpty()) {
                List<Long> homeworkIds = homeworks.stream()
                        .map(Homework::getId)
                        .collect(Collectors.toList());

                pendingHomework = homeworkSubmissionService.lambdaQuery()
                        .in(HomeworkSubmission::getHomeworkId, homeworkIds)
                        .eq(HomeworkSubmission::getStatus, "submitted")
                        .isNull(HomeworkSubmission::getScore)
                        .count();
            }
        }

        // 已批改作业数
        long gradedHomework = 0;
        if (!courseIds2.isEmpty()) {
            gradedHomework = homeworkSubmissionService.lambdaQuery()
                    .in(HomeworkSubmission::getHomeworkId, courseIds2)
                    .isNotNull(HomeworkSubmission::getScore)
                    .count();
        }

        data.put("myCourses", myCourses);
        data.put("myStudents", myStudents);
        data.put("pendingHomework", pendingHomework);
        data.put("gradedHomework", gradedHomework);

        return Result.ok(data);
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/courses/{teacherId}")
    @ApiOperation("获取教师的课程列表")
    public Result<List<Map<String, Object>>> getTeacherCourses(@PathVariable Long teacherId) {
        List<Course> courses = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list();

        List<Map<String, Object>> result = courses.stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("name", course.getCourseName());
                    map.put("code", course.getCourseCode());
                    map.put("type", course.getCourseType());
                    map.put("credit", course.getCredit());
                    map.put("hours", course.getTotalHours());

                    // 统计该课程的学生数
                    long studentCount = courseStudentService.lambdaQuery()
                            .eq(com.teaching.employment.entity.CourseStudent::getCourseId, course.getId())
                            .count();

                    // 统计该课程的作业数
                    long homeworkCount = homeworkService.lambdaQuery()
                            .eq(Homework::getCourseId, course.getId())
                            .count();

                    map.put("students", studentCount);
                    map.put("homework", homeworkCount);
                    map.put("progress", 75); // TODO: 根据实际课时完成情况计算

                    return map;
                })
                .collect(Collectors.toList());

        return Result.ok(result);
    }

    /**
     * 获取待批改作业列表
     */
    @GetMapping("/pending-homework/{teacherId}")
    @ApiOperation("获取待批改作业列表")
    public Result<IPage<Map<String, Object>>> getPendingHomework(
            @PathVariable Long teacherId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {

        // 获取教师的课程ID列表
        List<Course> courses = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list();

        if (courses.isEmpty()) {
            Page<Map<String, Object>> emptyPage = new Page<>(current, size);
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(0);
            return Result.ok(emptyPage);
        }

        List<Long> courseIds = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());

        // 获取已发布的作业
        List<Homework> homeworks = homeworkService.lambdaQuery()
                .in(Homework::getCourseId, courseIds)
                .eq(Homework::getStatus, "published")
                .list();

        if (homeworks.isEmpty()) {
            Page<Map<String, Object>> emptyPage = new Page<>(current, size);
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(0);
            return Result.ok(emptyPage);
        }

        List<Long> homeworkIds = homeworks.stream()
                .map(Homework::getId)
                .collect(Collectors.toList());

        // 获取已提交但未批改的作业
        Page<HomeworkSubmission> page = homeworkSubmissionService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<HomeworkSubmission>()
                        .in(HomeworkSubmission::getHomeworkId, homeworkIds)
                        .eq(HomeworkSubmission::getStatus, "submitted")
                        .isNull(HomeworkSubmission::getScore)
                        .orderByDesc(HomeworkSubmission::getSubmitTime)
        );

        List<Map<String, Object>> records = page.getRecords().stream()
                .map(submission -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", submission.getId());

                    // 获取作业信息
                    Homework homework = homeworkService.getById(submission.getHomeworkId());
                    if (homework != null) {
                        map.put("title", homework.getTitle());
                        map.put("courseId", homework.getCourseId());

                        Course course = courseService.getById(homework.getCourseId());
                        if (course != null) {
                            map.put("courseName", course.getCourseName());
                        }
                    }

                    // 获取学生信息
                    Student student = studentService.getById(submission.getStudentId());
                    if (student != null) {
                        map.put("studentId", student.getId());
                        map.put("studentName", student.getRealName());
                    }

                    map.put("submitTime", submission.getSubmitTime());
                    map.put("attachmentUrl", submission.getAttachmentUrl());

                    return map;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> resultPage = new Page<>(current, size, page.getTotal());
        resultPage.setRecords(records);

        return Result.ok(resultPage);
    }

    /**
     * 批改作业
     */
    @PostMapping("/grade-homework")
    @ApiOperation("批改作业")
    public Result<Void> gradeHomework(@RequestBody Map<String, Object> request) {
        Long submissionId = ((Number) request.get("submissionId")).longValue();
        Integer score = ((Number) request.get("score")).intValue();
        String comment = (String) request.get("comment");

        HomeworkSubmission submission = homeworkSubmissionService.getById(submissionId);
        if (submission == null) {
            return Result.error("作业提交不存在");
        }

        submission.setScore(score);
        submission.setComment(comment);
        submission.setStatus("graded");

        boolean success = homeworkSubmissionService.updateById(submission);
        return success ? Result.ok("批改成功") : Result.error("批改失败");
    }

    /**
     * 获取我的学生列表
     */
    @GetMapping("/students/{teacherId}")
    @ApiOperation("获取我的学生列表")
    public Result<IPage<Map<String, Object>>> getMyStudents(
            @PathVariable Long teacherId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {

        // 获取教师的课程ID列表
        List<Course> courses = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list();

        if (courses.isEmpty()) {
            Page<Map<String, Object>> emptyPage = new Page<>(current, size);
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(0);
            return Result.ok(emptyPage);
        }

        List<Long> courseIds = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());

        // 获取课程-学生关联
        List<com.teaching.employment.entity.CourseStudent> courseStudents =
                courseStudentService.lambdaQuery()
                        .in(com.teaching.employment.entity.CourseStudent::getCourseId, courseIds)
                        .list();

        if (courseStudents.isEmpty()) {
            Page<Map<String, Object>> emptyPage = new Page<>(current, size);
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(0);
            return Result.ok(emptyPage);
        }

        // 分页查询学生
        Page<Student> page = studentService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<Student>()
                        .in(Student::getId, courseStudents.stream()
                                .map(com.teaching.employment.entity.CourseStudent::getStudentId)
                                .collect(Collectors.toList()))
                        .and(keyword != null, wrapper -> wrapper
                                .like(Student::getRealName, keyword)
                                .or()
                                .like(Student::getStudentNo, keyword))
        );

        List<Map<String, Object>> records = page.getRecords().stream()
                .map(student -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", student.getId());
                    map.put("studentNo", student.getStudentNo());
                    map.put("realName", student.getRealName());
                    map.put("className", student.getClassName());
                    map.put("major", student.getMajor());
                    map.put("grade", student.getGrade());
                    map.put("phone", student.getPhone());
                    map.put("email", student.getEmail());
                    return map;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> resultPage = new Page<>(current, size, page.getTotal());
        resultPage.setRecords(records);

        return Result.ok(resultPage);
    }
}
