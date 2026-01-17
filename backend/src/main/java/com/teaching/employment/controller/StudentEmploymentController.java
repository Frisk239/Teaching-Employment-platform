package com.teaching.employment.controller;

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
 * 学生就业情况控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/student-employment")
@Api(tags = "学生就业情况")
@RequiredArgsConstructor
public class StudentEmploymentController {

    private final StudentService studentService;
    private final JobApplicationService jobApplicationService;
    private final InterviewService interviewService;
    private final OfferService offerService;
    private final WrittenTestService writtenTestService;
    private final PositionService positionService;
    private final CompanyService companyService;

    /**
     * 获取就业统计数据
     */
    @GetMapping("/stats")
    @ApiOperation("获取就业统计数据")
    public Result<Map<String, Object>> getEmploymentStats(
            @ApiParam("教师ID") @RequestParam(required = false) Long teacherId) {
        Map<String, Object> data = new HashMap<>();

        // 获取所有学生
        List<Student> allStudents = studentService.lambdaQuery()
                .eq(Student::getIsDeleted, 0) // 只统计未删除的学生
                .list();

        int totalStudents = allStudents.size();

        // 统计各类就业状态的学生数
        int appliedStudents = 0;  // 已投递简历
        int testInProgress = 0;   // 笔试中
        int interviewInProgress = 0; // 面试中
        int offerReceived = 0;    // 已获Offer
        int hiredStudents = 0;    // 已入职

        for (Student student : allStudents) {
            // 检查是否有申请记录
            long applicationCount = jobApplicationService.lambdaQuery()
                    .eq(JobApplication::getStudentId, student.getId())
                    .count();

            if (applicationCount > 0) {
                appliedStudents++;

                // 检查最新状态
                List<JobApplication> applications = jobApplicationService.lambdaQuery()
                        .eq(JobApplication::getStudentId, student.getId())
                        .orderByDesc(JobApplication::getCreateTime)
                        .list();

                if (!applications.isEmpty()) {
                    JobApplication latestApp = applications.get(0);
                    String status = latestApp.getStatus();

                    if ("test_passed".equals(status) || "interview_passed".equals(status)
                            || "offered".equals(status) || "hired".equals(status)) {
                        testInProgress++;
                    }

                    if ("interview_passed".equals(status) || "offered".equals(status)
                            || "hired".equals(status)) {
                        interviewInProgress++;
                    }

                    if ("offered".equals(status) || "hired".equals(status)) {
                        offerReceived++;
                    }

                    if ("hired".equals(status)) {
                        hiredStudents++;
                    }
                }
            }
        }

        // 计算就业率（已入职 / 总学生数）
        double employmentRate = totalStudents > 0
                ? (double) hiredStudents / totalStudents * 100
                : 0.0;

        data.put("totalStudents", totalStudents);
        data.put("appliedStudents", appliedStudents);
        data.put("testInProgress", testInProgress);
        data.put("interviewInProgress", interviewInProgress);
        data.put("offerReceived", offerReceived);
        data.put("hiredStudents", hiredStudents);
        data.put("employmentRate", Math.round(employmentRate * 10) / 10.0);

        return Result.ok(data);
    }

    /**
     * 分页查询学生就业情况列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询学生就业情况列表")
    public Result<IPage<StudentEmploymentVO>> getStudentEmploymentPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("班级") @RequestParam(required = false) String className,
            @ApiParam("专业") @RequestParam(required = false) String major,
            @ApiParam("就业状态") @RequestParam(required = false) String employmentStatus) {

        Page<Student> page = new Page<>(current, size);
        IPage<Student> studentPage = studentService.lambdaQuery()
                .like(keyword != null, Student::getRealName, keyword)
                .like(className != null, Student::getClassName, className)
                .like(major != null, Student::getMajor, major)
                .eq(Student::getIsDeleted, 0)
                .orderByDesc(Student::getCreateTime)
                .page(page);

        // 转换为VO并填充就业信息
        IPage<StudentEmploymentVO> voPage = new Page<>(current, size, studentPage.getTotal());
        List<StudentEmploymentVO> voList = studentPage.getRecords().stream()
                .map(this::convertToVO)
                .filter(vo -> {
                    // 根据就业状态筛选
                    if (employmentStatus == null || employmentStatus.isEmpty()) {
                        return true;
                    }
                    return employmentStatus.equals(vo.getEmploymentStatus());
                })
                .collect(Collectors.toList());

        // 更新total（筛选后）
        voPage.setTotal(voList.size());
        voPage.setRecords(voList);

        return Result.ok(voPage);
    }

    /**
     * 获取学生就业详情
     */
    @GetMapping("/detail/{studentId}")
    @ApiOperation("获取学生就业详情")
    public Result<Map<String, Object>> getStudentEmploymentDetail(@PathVariable Long studentId) {
        Map<String, Object> data = new HashMap<>();

        // 获取学生信息
        Student student = studentService.getById(studentId);

        // 填充学生姓名
        if (student.getUserId() != null) {
            // 这里需要通过UserService获取用户姓名，暂时使用临时方案
            student.setRealName("学生" + studentId);
        }
        data.put("student", student);

        // 获取申请记录并填充职位名称和公司名称
        List<JobApplication> applications = jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStudentId, studentId)
                .orderByDesc(JobApplication::getCreateTime)
                .list();

        // 填充职位名称和公司名称
        applications.forEach(app -> {
            if (app.getPositionId() != null) {
                Position position = positionService.getById(app.getPositionId());
                if (position != null) {
                    app.setPositionName(position.getPositionName());
                    // 从position中获取公司ID并填充公司名称
                    if (position.getCompanyId() != null) {
                        Company company = companyService.getById(position.getCompanyId());
                        if (company != null) {
                            app.setCompanyName(company.getCompanyName());
                        }
                    }
                }
            }
        });
        data.put("applications", applications);

        // 获取面试记录并填充职位名称和公司名称
        List<Interview> interviews = interviewService.lambdaQuery()
                .eq(Interview::getStudentId, studentId)
                .orderByDesc(Interview::getInterviewTime)
                .list();

        // 填充职位名称和公司名称
        interviews.forEach(interview -> {
            if (interview.getPositionId() != null) {
                Position position = positionService.getById(interview.getPositionId());
                if (position != null) {
                    interview.setPositionName(position.getPositionName());
                    // 获取公司名称
                    if (position.getCompanyId() != null) {
                        Company company = companyService.getById(position.getCompanyId());
                        if (company != null) {
                            interview.setCompanyName(company.getCompanyName());
                        }
                    }
                }
            }
        });
        data.put("interviews", interviews);

        // 获取Offer记录并填充职位名称和公司名称
        List<Offer> offers = offerService.lambdaQuery()
                .eq(Offer::getStudentId, studentId)
                .orderByDesc(Offer::getCreateTime)
                .list();

        // 填充职位名称和公司名称
        offers.forEach(offer -> {
            if (offer.getPositionId() != null) {
                Position position = positionService.getById(offer.getPositionId());
                if (position != null) {
                    offer.setPositionName(position.getPositionName());
                    // 获取公司名称
                    if (position.getCompanyId() != null) {
                        Company company = companyService.getById(position.getCompanyId());
                        if (company != null) {
                            offer.setCompanyName(company.getCompanyName());
                        }
                    }
                }
            }
        });
        data.put("offers", offers);

        // 统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApplications", applications.size());
        stats.put("totalInterviews", interviews.size());
        stats.put("totalOffers", offers.size());

        // 计算通过率
        long passedTests = applications.stream()
                .filter(app -> "test_passed".equals(app.getStatus())
                        || "interview_passed".equals(app.getStatus())
                        || "offered".equals(app.getStatus())
                        || "hired".equals(app.getStatus()))
                .count();

        long passedInterviews = interviews.stream()
                .filter(interview -> "passed".equals(interview.getResult()))
                .count();

        stats.put("passedTests", passedTests);
        stats.put("passedInterviews", passedInterviews);

        data.put("stats", stats);

        return Result.ok(data);
    }

    /**
     * 转换Student为StudentEmploymentVO
     */
    private StudentEmploymentVO convertToVO(Student student) {
        StudentEmploymentVO vo = new StudentEmploymentVO();
        vo.setId(student.getId());

        // 获取用户真实姓名
        if (student.getUserId() != null) {
            User user = new User();
            user.setId(student.getUserId());
            // 注意：这里需要通过UserService获取，暂时使用userId作为标识
            // 实际应该查询user表获取realName
            vo.setRealName("学生" + student.getId()); // 临时方案
        } else {
            vo.setRealName("学生" + student.getId());
        }

        vo.setClassName(student.getClassName());
        vo.setMajor(student.getMajor());
        vo.setGrade(student.getGrade());
        vo.setPhone(student.getPhone());
        vo.setEmail(student.getEmail());

        // 统计申请数
        long applicationCount = jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStudentId, student.getId())
                .count();
        vo.setApplicationCount((int) applicationCount);

        // 统计面试数
        long interviewCount = interviewService.lambdaQuery()
                .eq(Interview::getStudentId, student.getId())
                .count();
        vo.setInterviewCount((int) interviewCount);

        // 统计Offer数
        long offerCount = offerService.lambdaQuery()
                .eq(Offer::getStudentId, student.getId())
                .count();
        vo.setOfferCount((int) offerCount);

        // 确定就业状态
        if (offerCount > 0) {
            List<Offer> offers = offerService.lambdaQuery()
                    .eq(Offer::getStudentId, student.getId())
                    .list();
            boolean hasAcceptedOffer = offers.stream()
                    .anyMatch(offer -> "accepted".equals(offer.getStatus()));
            boolean hasHiredOffer = offers.stream()
                    .anyMatch(offer -> "hired".equals(offer.getStatus()));

            if (hasHiredOffer) {
                vo.setEmploymentStatus("hired");
                vo.setEmploymentStatusText("已入职");
            } else if (hasAcceptedOffer) {
                vo.setEmploymentStatus("offered");
                vo.setEmploymentStatusText("已获Offer");
            } else {
                vo.setEmploymentStatus("offered");
                vo.setEmploymentStatusText("已获Offer");
            }
        } else if (interviewCount > 0) {
            vo.setEmploymentStatus("interviewing");
            vo.setEmploymentStatusText("面试中");
        } else if (applicationCount > 0) {
            vo.setEmploymentStatus("applied");
            vo.setEmploymentStatusText("求职中");
        } else {
            vo.setEmploymentStatus("not_started");
            vo.setEmploymentStatusText("未开始");
        }

        return vo;
    }

    /**
     * 学生就业情况VO
     */
    public static class StudentEmploymentVO {
        private Long id;
        private String realName;
        private String className;
        private String major;
        private String grade;
        private String phone;
        private String email;
        private Integer applicationCount;
        private Integer interviewCount;
        private Integer offerCount;
        private String employmentStatus;
        private String employmentStatusText;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }

        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Integer getApplicationCount() { return applicationCount; }
        public void setApplicationCount(Integer applicationCount) { this.applicationCount = applicationCount; }

        public Integer getInterviewCount() { return interviewCount; }
        public void setInterviewCount(Integer interviewCount) { this.interviewCount = interviewCount; }

        public Integer getOfferCount() { return offerCount; }
        public void setOfferCount(Integer offerCount) { this.offerCount = offerCount; }

        public String getEmploymentStatus() { return employmentStatus; }
        public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

        public String getEmploymentStatusText() { return employmentStatusText; }
        public void setEmploymentStatusText(String employmentStatusText) { this.employmentStatusText = employmentStatusText; }
    }
}
