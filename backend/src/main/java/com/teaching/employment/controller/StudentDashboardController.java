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
 * 学员工作台控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/student-dashboard")
@Api(tags = "学员工作台")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final CourseService courseService;
    private final CourseStudentService courseStudentService;
    private final HomeworkService homeworkService;
    private final HomeworkSubmissionService homeworkSubmissionService;
    private final StudentService studentService;
    private final JobApplicationService jobApplicationService;
    private final OfferService offerService;
    private final InterviewService interviewService;
    private final PositionService positionService;

    /**
     * 获取学员工作台统计数据
     */
    @GetMapping("/stats/{studentId}")
    @ApiOperation("获取学员工作台统计数据")
    public Result<Map<String, Object>> getStudentStats(@PathVariable Long studentId) {
        Map<String, Object> data = new HashMap<>();

        // 我的课程统计
        List<Course> courses = courseService.getCoursesByStudentId(studentId);
        long totalCourses = courses.size();
        long completedCourses = courses.stream()
                .filter(c -> "completed".equals(c.getStatus()))
                .count();
        long inProgressCourses = courses.stream()
                .filter(c -> "active".equals(c.getStatus()))
                .count();

        // 计算已完成课程的总学分
        double totalCredits = courses.stream()
                .filter(c -> "completed".equals(c.getStatus()))
                .map(c -> c.getCredit() != null ? c.getCredit().doubleValue() : 0.0)
                .mapToDouble(Double::doubleValue)
                .sum();

        // 作业统计
        com.baomidou.mybatisplus.core.metadata.IPage<Homework> homeworkPage = homeworkService.getHomeworkByStudentId(1, Integer.MAX_VALUE, studentId);
        long pendingHomework = homeworkPage.getRecords().stream()
                .filter(h -> "published".equals(h.getStatus()))
                .count();

        long submittedHomework = homeworkSubmissionService.lambdaQuery()
                .eq(HomeworkSubmission::getStudentId, studentId)
                .eq(HomeworkSubmission::getStatus, "submitted")
                .count();

        long gradedHomework = homeworkSubmissionService.lambdaQuery()
                .eq(HomeworkSubmission::getStudentId, studentId)
                .eq(HomeworkSubmission::getStatus, "graded")
                .count();

        // 就业统计
        long totalApplications = jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStudentId, studentId)
                .count();

        long pendingApplications = jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStudentId, studentId)
                .eq(JobApplication::getStatus, "pending")
                .count();

        long interviewCount = interviewService.lambdaQuery()
                .eq(Interview::getStudentId, studentId)
                .count();

        long offerCount = offerService.lambdaQuery()
                .eq(Offer::getStudentId, studentId)
                .count();

        data.put("totalCourses", totalCourses);
        data.put("completedCourses", completedCourses);
        data.put("inProgressCourses", inProgressCourses);
        data.put("notStartedCourses", totalCourses - completedCourses - inProgressCourses);
        data.put("pendingHomework", pendingHomework);
        data.put("submittedHomework", submittedHomework);
        data.put("gradedHomework", gradedHomework);
        data.put("totalApplications", totalApplications);
        data.put("interviewCount", interviewCount);
        data.put("offerCount", offerCount);
        data.put("totalCredits", totalCredits);

        return Result.ok(data);
    }

    /**
     * 获取我的课程列表
     */
    @GetMapping("/courses/{studentId}")
    @ApiOperation("获取我的课程列表")
    public Result<List<Map<String, Object>>> getMyCourses(@PathVariable Long studentId) {
        // 获取学员的课程关联记录(包含进度信息)
        List<CourseStudent> courseStudents = courseStudentService.getCoursesByStudentId(studentId);

        List<Map<String, Object>> result = courseStudents.stream()
                .map(cs -> {
                    Course course = courseService.getById(cs.getCourseId());
                    if (course == null) {
                        return null;
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("name", course.getCourseName());
                    map.put("code", course.getCourseCode());
                    map.put("type", course.getCourseType());
                    map.put("credit", course.getCredit());
                    map.put("totalHours", course.getTotalHours());
                    map.put("description", course.getDescription());

                    // 获取教师信息
                    if (course.getTeacherId() != null) {
                        map.put("teacherId", course.getTeacherId());
                        // TODO: 可以关联查询教师名称
                    }

                    // 从CourseStudent表获取实际进度
                    map.put("progress", cs.getProgress() != null ? cs.getProgress() : 0);

                    // 统计课程作业
                    long homeworkCount = homeworkService.lambdaQuery()
                            .eq(Homework::getCourseId, course.getId())
                            .count();
                    map.put("homeworkCount", homeworkCount);

                    return map;
                })
                .filter(m -> m != null)
                .collect(Collectors.toList());

        return Result.ok(result);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/course/{courseId}/student/{studentId}")
    @ApiOperation("获取课程详情（学员视角）")
    public Result<Map<String, Object>> getCourseDetail(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {

        // 使用getCourseWithDetails获取包含关联信息的课程对象
        Course course = courseService.getCourseWithDetails(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", course.getId());
        // 兼容前端期望的字段名
        data.put("courseName", course.getCourseName());
        data.put("name", course.getCourseName());  // 同时保留name字段
        data.put("courseCode", course.getCourseCode());
        data.put("code", course.getCourseCode());  // 同时保留code字段
        data.put("type", course.getCourseType());
        data.put("credit", course.getCredit());
        data.put("totalHours", course.getTotalHours());
        data.put("description", course.getDescription());
        data.put("startDate", course.getStartDate());
        data.put("endDate", course.getEndDate());
        data.put("maxStudents", course.getMaxStudents());
        data.put("currentStudents", course.getCurrentStudents());
        data.put("status", course.getStatus());

        // 从Course对象的关联字段获取教师、学校、教室名称
        data.put("teacherName", course.getTeacherName() != null ? course.getTeacherName() : "-");
        data.put("schoolName", course.getSchoolName() != null ? course.getSchoolName() : "-");
        data.put("classroomName", course.getClassroomName() != null ? course.getClassroomName() : "-");

        // 从CourseStudent表获取学员的学习进度
        CourseStudent courseStudent = courseStudentService.lambdaQuery()
                .eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getStudentId, studentId)
                .one();

        if (courseStudent != null) {
            data.put("progress", courseStudent.getProgress() != null ? courseStudent.getProgress() : 0);
            data.put("enrollmentDate", courseStudent.getEnrollmentDate());
        } else {
            data.put("progress", 0);
        }

        // 获取课程的作业列表
        List<Homework> homeworks = homeworkService.lambdaQuery()
                .eq(Homework::getCourseId, courseId)
                .orderByDesc(Homework::getCreateTime)
                .list();

        List<Map<String, Object>> homeworkList = homeworks.stream()
                .map(homework -> {
                    Map<String, Object> hwMap = new HashMap<>();
                    hwMap.put("id", homework.getId());
                    hwMap.put("title", homework.getTitle());
                    hwMap.put("description", homework.getDescription());
                    hwMap.put("homeworkType", homework.getHomeworkType());
                    hwMap.put("deadline", homework.getDeadline());
                    hwMap.put("maxScore", homework.getMaxScore());
                    hwMap.put("status", homework.getStatus());

                    // 查询学员的提交情况
                    HomeworkSubmission submission = homeworkSubmissionService.lambdaQuery()
                            .eq(HomeworkSubmission::getHomeworkId, homework.getId())
                            .eq(HomeworkSubmission::getStudentId, studentId)
                            .one();

                    if (submission != null) {
                        hwMap.put("submitted", true);
                        hwMap.put("submissionId", submission.getId());
                        hwMap.put("score", submission.getScore());
                        hwMap.put("submitTime", submission.getSubmitTime());
                        hwMap.put("comment", submission.getComment());
                    } else {
                        hwMap.put("submitted", false);
                    }

                    return hwMap;
                })
                .collect(Collectors.toList());

        data.put("homeworks", homeworkList);

        return Result.ok(data);
    }

    /**
     * 获取待办作业列表
     */
    @GetMapping("/pending-homework/{studentId}")
    @ApiOperation("获取待办作业列表")
    public Result<IPage<Map<String, Object>>> getPendingHomework(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {

        // 获取学生的所有课程
        List<Course> courses = courseService.getCoursesByStudentId(studentId);
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
        Page<Homework> page = homeworkService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<Homework>()
                        .in(Homework::getCourseId, courseIds)
                        .eq(Homework::getStatus, "published")
                        .orderByDesc(Homework::getDeadline)
        );

        List<Map<String, Object>> records = page.getRecords().stream()
                .map(homework -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", homework.getId());
                    map.put("title", homework.getTitle());
                    map.put("description", homework.getDescription());
                    map.put("deadline", homework.getDeadline());
                    map.put("maxScore", homework.getMaxScore());

                    // 课程信息
                    Course course = courseService.getById(homework.getCourseId());
                    if (course != null) {
                        map.put("courseId", course.getId());
                        map.put("courseName", course.getCourseName());
                    }

                    // 检查是否已提交
                    HomeworkSubmission submission = homeworkSubmissionService.lambdaQuery()
                            .eq(HomeworkSubmission::getHomeworkId, homework.getId())
                            .eq(HomeworkSubmission::getStudentId, studentId)
                            .one();

                    if (submission != null) {
                        map.put("submitted", true);
                        map.put("score", submission.getScore());
                    } else {
                        map.put("submitted", false);
                    }

                    // 判断是否紧急（距离截止时间不足3天）
                    if (homework.getDeadline() != null) {
                        boolean urgent = homework.getDeadline().isBefore(
                                java.time.LocalDateTime.now().plusDays(3)
                        );
                        map.put("urgent", urgent);
                    }

                    return map;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> resultPage = new Page<>(current, size, page.getTotal());
        resultPage.setRecords(records);

        return Result.ok(resultPage);
    }

    /**
     * 提交作业
     */
    @PostMapping("/submit-homework")
    @ApiOperation("提交作业")
    public Result<Void> submitHomework(@RequestBody Map<String, Object> request) {
        Long homeworkId = ((Number) request.get("homeworkId")).longValue();
        Long studentId = ((Number) request.get("studentId")).longValue();
        String content = (String) request.get("content");
        String attachmentUrl = (String) request.get("attachmentUrl");

        // 检查是否已经提交过
        HomeworkSubmission existingSubmission = homeworkSubmissionService.lambdaQuery()
                .eq(HomeworkSubmission::getHomeworkId, homeworkId)
                .eq(HomeworkSubmission::getStudentId, studentId)
                .one();

        if (existingSubmission != null) {
            return Result.error("该作业已提交，如需修改请联系教师");
        }

        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setContent(content);
        submission.setAttachmentUrl(attachmentUrl);
        submission.setStatus("submitted");
        submission.setSubmitTime(java.time.LocalDateTime.now());

        boolean success = homeworkSubmissionService.save(submission);
        return success ? Result.ok("提交成功") : Result.error("提交失败");
    }

    /**
     * 获取我的作业提交记录
     */
    @GetMapping("/my-submissions/{studentId}")
    @ApiOperation("获取我的作业提交记录")
    public Result<IPage<Map<String, Object>>> getMySubmissions(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {

        Page<HomeworkSubmission> page = homeworkSubmissionService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<HomeworkSubmission>()
                        .eq(HomeworkSubmission::getStudentId, studentId)
                        .orderByDesc(HomeworkSubmission::getSubmitTime)
        );

        List<Map<String, Object>> records = page.getRecords().stream()
                .map(submission -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", submission.getId());
                    map.put("content", submission.getContent());
                    map.put("attachmentUrl", submission.getAttachmentUrl());
                    map.put("submitTime", submission.getSubmitTime());
                    map.put("score", submission.getScore());
                    map.put("comment", submission.getComment());
                    map.put("status", submission.getStatus());

                    // 获取作业信息
                    Homework homework = homeworkService.getById(submission.getHomeworkId());
                    if (homework != null) {
                        map.put("homeworkTitle", homework.getTitle());
                        map.put("maxScore", homework.getMaxScore());

                        Course course = courseService.getById(homework.getCourseId());
                        if (course != null) {
                            map.put("courseName", course.getCourseName());
                        }
                    }

                    return map;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> resultPage = new Page<>(current, size, page.getTotal());
        resultPage.setRecords(records);

        return Result.ok(resultPage);
    }

    /**
     * 获取职位列表（带搜索和筛选）
     */
    @GetMapping("/positions")
    @ApiOperation("获取职位列表")
    public Result<IPage<Map<String, Object>>> getPositions(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("工作城市") @RequestParam(required = false) String city,
            @ApiParam("职位类型") @RequestParam(required = false) String positionType,
            @ApiParam("学历要求") @RequestParam(required = false) String education) {

        Page<Position> page = positionService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .and(keyword != null, wrapper -> wrapper
                                .like(Position::getPositionName, keyword)
                                .or()
                                .like(Position::getDescription, keyword))
                        .eq(city != null, Position::getCity, city)
                        .eq(positionType != null, Position::getPositionType, positionType)
                        .eq(education != null, Position::getEducation, education)
                        .orderByDesc(Position::getCreateTime)
        );

        List<Map<String, Object>> records = page.getRecords().stream()
                .map(position -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", position.getId());
                    map.put("positionName", position.getPositionName());
                    map.put("positionType", position.getPositionType());
                    map.put("salaryMin", position.getSalaryMin());
                    map.put("salaryMax", position.getSalaryMax());
                    map.put("city", position.getCity());
                    map.put("education", position.getEducation());
                    map.put("description", position.getDescription());
                    map.put("requirements", position.getRequirements());
                    map.put("companyId", position.getCompanyId());
                    map.put("status", position.getStatus());
                    map.put("createTime", position.getCreateTime());

                    // 获取企业信息
                    // TODO: 可以关联查询企业名称

                    return map;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> resultPage = new Page<>(current, size, page.getTotal());
        resultPage.setRecords(records);

        return Result.ok(resultPage);
    }

    /**
     * 获取推荐的职位
     */
    @GetMapping("/recommended-positions/{studentId}")
    @ApiOperation("获取推荐的职位")
    public Result<List<Map<String, Object>>> getRecommendedPositions(
            @PathVariable Long studentId,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {

        Student student = studentService.getById(studentId);
        if (student == null) {
            return Result.error("学生不存在");
        }

        // 根据学生的专业推荐职位
        // TODO: 可以实现更智能的推荐算法
        Page<Position> page = positionService.page(
                new Page<>(1, limit),
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .orderByDesc(Position::getCreateTime)
        );

        List<Map<String, Object>> result = page.getRecords().stream()
                .map(position -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", position.getId());
                    map.put("positionName", position.getPositionName());
                    map.put("salaryMin", position.getSalaryMin());
                    map.put("salaryMax", position.getSalaryMax());
                    map.put("city", position.getCity());

                    // 获取企业信息
                    // TODO: 关联查询企业名称

                    return map;
                })
                .collect(Collectors.toList());

        return Result.ok(result);
    }

    /**
     * 申请职位
     */
    @PostMapping("/apply-position")
    @ApiOperation("申请职位")
    public Result<Void> applyPosition(@RequestBody Map<String, Object> request) {
        Long studentId = ((Number) request.get("studentId")).longValue();
        Long positionId = ((Number) request.get("positionId")).longValue();
        Long resumeId = request.get("resumeId") != null
                ? ((Number) request.get("resumeId")).longValue()
                : null;

        // 检查是否已经申请过
        JobApplication existingApplication = jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStudentId, studentId)
                .eq(JobApplication::getPositionId, positionId)
                .one();

        if (existingApplication != null) {
            return Result.error("您已经申请过该职位");
        }

        JobApplication application = new JobApplication();
        application.setStudentId(studentId);
        application.setPositionId(positionId);
        application.setResumeId(resumeId);
        application.setStatus("pending");
        application.setApplicationTime(java.time.LocalDateTime.now());

        boolean success = jobApplicationService.save(application);
        return success ? Result.ok("申请成功") : Result.error("申请失败");
    }

    /**
     * 获取我的申请列表
     */
    @GetMapping("/my-applications/{studentId}")
    @ApiOperation("获取我的申请列表")
    public Result<IPage<Map<String, Object>>> getMyApplications(
            @PathVariable Long studentId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {

        Page<JobApplication> page = jobApplicationService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<JobApplication>()
                        .eq(JobApplication::getStudentId, studentId)
                        .orderByDesc(JobApplication::getApplicationTime)
        );

        List<Map<String, Object>> records = page.getRecords().stream()
                .map(application -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", application.getId());
                    map.put("status", application.getStatus());
                    map.put("applyTime", application.getApplicationTime());

                    // 获取职位信息
                    Position position = positionService.getById(application.getPositionId());
                    if (position != null) {
                        map.put("positionName", position.getPositionName());
                        map.put("salaryMin", position.getSalaryMin());
                        map.put("salaryMax", position.getSalaryMax());
                        map.put("city", position.getCity());
                    }

                    // 获取面试信息
                    Interview interview = interviewService.lambdaQuery()
                            .eq(Interview::getApplicationId, application.getId())
                            .one();

                    if (interview != null) {
                        map.put("hasInterview", true);
                        map.put("interviewTime", interview.getInterviewTime());
                        map.put("interviewLocation", interview.getLocation());
                    } else {
                        map.put("hasInterview", false);
                    }

                    // 获取Offer信息
                    Offer offer = offerService.lambdaQuery()
                            .eq(Offer::getApplicationId, application.getId())
                            .one();

                    if (offer != null) {
                        map.put("hasOffer", true);
                        map.put("offerSalary", offer.getSalary());
                        map.put("offerStatus", offer.getStatus());
                    } else {
                        map.put("hasOffer", false);
                    }

                    return map;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> resultPage = new Page<>(current, size, page.getTotal());
        resultPage.setRecords(records);

        return Result.ok(resultPage);
    }
}
