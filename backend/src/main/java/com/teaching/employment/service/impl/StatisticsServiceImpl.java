package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.employment.entity.Company;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.CourseStudent;
import com.teaching.employment.entity.JobApplication;
import com.teaching.employment.entity.Notification;
import com.teaching.employment.entity.Offer;
import com.teaching.employment.entity.Position;
import com.teaching.employment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OfferService offerService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseStudentService courseStudentService;

    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Override
    public Map<String, Object> getPositionTypeDistribution() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有已接受的Offer
        LambdaQueryWrapper<Offer> offerWrapper = new LambdaQueryWrapper<>();
        offerWrapper.eq(Offer::getStatus, "accepted");
        List<Offer> offers = offerService.list(offerWrapper);

        // 统计不同岗位类型的数量
        Map<String, Long> typeCount = offers.stream()
                .filter(offer -> offer.getPositionId() != null)
                .map(offer -> {
                    Position position = positionService.getById(offer.getPositionId());
                    return position != null ? position.getPositionType() : "unknown";
                })
                .collect(Collectors.groupingBy(
                        type -> type != null ? type : "unknown",
                        Collectors.counting()
                ));

        // 转换为更容易理解的名称
        Map<String, Object> distribution = new HashMap<>();
        distribution.put("全职", typeCount.getOrDefault("fulltime", 0L));
        distribution.put("兼职", typeCount.getOrDefault("parttime", 0L));
        distribution.put("实习", typeCount.getOrDefault("internship", 0L));
        distribution.put("未知", typeCount.getOrDefault("unknown", 0L));

        result.put("distribution", distribution);
        result.put("total", offers.size());

        return result;
    }

    @Override
    public List<Map<String, Object>> getCompanyRanking(Integer limit) {
        // 获取所有已接受的Offer
        LambdaQueryWrapper<Offer> offerWrapper = new LambdaQueryWrapper<>();
        offerWrapper.eq(Offer::getStatus, "accepted");
        List<Offer> offers = offerService.list(offerWrapper);

        // 按企业ID分组统计录用人数
        Map<Long, Long> companyHireCount = offers.stream()
                .filter(offer -> offer.getCompanyId() != null)
                .collect(Collectors.groupingBy(
                        Offer::getCompanyId,
                        Collectors.counting()
                ));

        // 转换为排名列表并排序
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : companyHireCount.entrySet()) {
            Company company = companyService.getById(entry.getKey());
            if (company != null) {
                Map<String, Object> companyData = new HashMap<>();
                companyData.put("companyId", entry.getKey());
                companyData.put("companyName", company.getCompanyName());
                companyData.put("hireCount", entry.getValue());
                ranking.add(companyData);
            }
        }

        // 按录用人数降序排序
        ranking.sort((a, b) -> {
            Long countA = (Long) a.get("hireCount");
            Long countB = (Long) b.get("hireCount");
            return countB.compareTo(countA);
        });

        // 限制返回数量
        if (limit != null && limit > 0 && ranking.size() > limit) {
            ranking = ranking.subList(0, limit);
        }

        return ranking;
    }

    @Override
    public List<Map<String, Object>> getCourseEmploymentRate() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取所有课程
        List<Course> courses = courseService.list();

        for (Course course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("courseId", course.getId());
            courseData.put("courseName", course.getCourseName());
            courseData.put("courseCode", course.getCourseCode());

            // 获取该课程的所有学生
            LambdaQueryWrapper<CourseStudent> csWrapper = new LambdaQueryWrapper<>();
            csWrapper.eq(CourseStudent::getCourseId, course.getId());
            List<CourseStudent> courseStudents = courseStudentService.list(csWrapper);

            int totalStudents = courseStudents.size();
            if (totalStudents == 0) {
                courseData.put("totalStudents", 0);
                courseData.put("employedStudents", 0);
                courseData.put("employmentRate", BigDecimal.ZERO);
                continue;
            }

            // 获取学生ID列表
            List<Long> studentIds = courseStudents.stream()
                    .map(CourseStudent::getStudentId)
                    .collect(Collectors.toList());

            // 统计已就业的学生数(收到已接受的Offer)
            LambdaQueryWrapper<Offer> offerWrapper = new LambdaQueryWrapper<>();
            offerWrapper.eq(Offer::getStatus, "accepted");
            offerWrapper.in(Offer::getStudentId, studentIds);
            long employedCount = offerService.count(offerWrapper);

            // 计算就业率
            BigDecimal employmentRate = new BigDecimal(employedCount)
                    .divide(new BigDecimal(totalStudents), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);

            courseData.put("totalStudents", totalStudents);
            courseData.put("employedStudents", employedCount);
            courseData.put("employmentRate", employmentRate);

            result.add(courseData);
        }

        // 按就业率降序排序
        result.sort((a, b) -> {
            BigDecimal rateA = (BigDecimal) a.get("employmentRate");
            BigDecimal rateB = (BigDecimal) b.get("employmentRate");
            return rateB.compareTo(rateA);
        });

        return result;
    }

    @Override
    public Map<String, Object> getMonthlyTrend(Integer months) {
        Map<String, Object> result = new HashMap<>();
        List<String> monthsList = new ArrayList<>();
        List<Integer> applicationsData = new ArrayList<>();
        List<Integer> offersData = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            String monthLabel = yearMonth.format(DateTimeFormatter.ofPattern("M月"));
            monthsList.add(monthLabel);

            String monthStr = yearMonth.format(formatter);

            // 获取该月的职位申请数(在该月创建的申请)
            LambdaQueryWrapper<JobApplication> applicationWrapper = new LambdaQueryWrapper<>();
            applicationWrapper.apply("DATE_FORMAT(create_time, '%Y-%m') = {0}", monthStr);
            long applicationCount = jobApplicationService.count(applicationWrapper);

            // 获取该月已发送的Offer数(已接受的Offer且创建时间在该月)
            LambdaQueryWrapper<Offer> offerWrapper = new LambdaQueryWrapper<>();
            offerWrapper.eq(Offer::getStatus, "accepted");
            offerWrapper.apply("DATE_FORMAT(create_time, '%Y-%m') = {0}", monthStr);
            long offerCount = offerService.count(offerWrapper);

            applicationsData.add((int) applicationCount);
            offersData.add((int) offerCount);
        }

        result.put("months", monthsList);
        result.put("applications", applicationsData);
        result.put("offers", offersData);

        return result;
    }

    @Override
    public Map<String, Object> getEmploymentStatusDistribution() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有学生
        long totalStudents = studentService.count();

        // 已就业 - 有已接受的Offer的学生数
        LambdaQueryWrapper<Offer> employedWrapper = new LambdaQueryWrapper<>();
        employedWrapper.eq(Offer::getStatus, "accepted");
        long employedCount = offerService.count(employedWrapper);

        // 求职中 - 有pending或processing申请的学生数
        LambdaQueryWrapper<JobApplication> seekingWrapper = new LambdaQueryWrapper<>();
        seekingWrapper.in(JobApplication::getStatus, "pending", "written_test_passed", "interview_scheduled");
        List<JobApplication> seekingApplications = jobApplicationService.list(seekingWrapper);
        long seekingCount = seekingApplications.stream()
                .map(JobApplication::getStudentId)
                .distinct()
                .count();

        // 继续深造 - 简化处理,假设没有继续深造的数据
        long furtherStudyCount = 0L;

        // 未就业 - 总学生数减去已就业和求职中的
        long unemployedCount = totalStudents - employedCount - seekingCount - furtherStudyCount;
        if (unemployedCount < 0) {
            unemployedCount = 0L;
        }

        result.put("已就业", employedCount);
        result.put("求职中", seekingCount);
        result.put("继续深造", furtherStudyCount);
        result.put("未就业", unemployedCount);
        result.put("total", totalStudents);

        return result;
    }

    @Override
    public List<Map<String, Object>> getPopularCourses(Integer limit) {
        // 获取课程就业率数据并取前N个
        List<Map<String, Object>> courseEmploymentRate = getCourseEmploymentRate();

        // 转换为热门课程格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> courseData : courseEmploymentRate) {
            Map<String, Object> popularCourse = new HashMap<>();
            popularCourse.put("id", courseData.get("courseId"));
            popularCourse.put("name", courseData.get("courseName"));
            popularCourse.put("code", courseData.get("courseCode"));
            popularCourse.put("students", courseData.get("totalStudents"));
            popularCourse.put("employedStudents", courseData.get("employedStudents"));
            popularCourse.put("employmentRate", courseData.get("employmentRate"));
            popularCourse.put("progress", courseData.get("employmentRate")); // 进度条使用就业率
            result.add(popularCourse);
        }

        // 限制返回数量
        if (limit != null && limit > 0 && result.size() > limit) {
            result = result.subList(0, limit);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getAnnouncements(Integer limit) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取公告类型的系统通知(type='notice')
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getType, "notice");
        wrapper.orderByDesc(Notification::getCreateTime);

        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }

        List<Notification> notifications = notificationService.list(wrapper);

        for (Notification notification : notifications) {
            Map<String, Object> announcement = new HashMap<>();
            announcement.put("id", notification.getId());
            announcement.put("title", notification.getTitle());
            announcement.put("content", notification.getContent());
            announcement.put("type", "info"); // 默认为通知类型
            announcement.put("publishTime", notification.getCreateTime());
            result.add(announcement);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getCompanyStats() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取所有企业
        List<Company> companies = companyService.list();

        for (Company company : companies) {
            Map<String, Object> companyData = new HashMap<>();
            companyData.put("id", company.getId());
            companyData.put("name", company.getCompanyName());

            // 统计该企业的职位数
            LambdaQueryWrapper<Position> positionWrapper = new LambdaQueryWrapper<>();
            positionWrapper.eq(Position::getCompanyId, company.getId());
            long positionCount = positionService.count(positionWrapper);
            companyData.put("positionCount", positionCount);
            companyData.put("jobsCount", positionCount); // 前端使用jobsCount

            // 统计该企业的录用人数
            LambdaQueryWrapper<Offer> offerWrapper = new LambdaQueryWrapper<>();
            offerWrapper.eq(Offer::getCompanyId, company.getId());
            offerWrapper.eq(Offer::getStatus, "accepted");
            long hireCount = offerService.count(offerWrapper);
            companyData.put("hireCount", hireCount);

            result.add(companyData);
        }

        // 按职位数降序排序
        result.sort((a, b) -> {
            Long countA = (Long) a.get("positionCount");
            Long countB = (Long) b.get("positionCount");
            return countB.compareTo(countA);
        });

        return result;
    }

    @Override
    public List<Map<String, Object>> getPositionStats() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取所有职位
        List<Position> positions = positionService.list();

        for (Position position : positions) {
            Map<String, Object> positionData = new HashMap<>();
            positionData.put("id", position.getId());
            positionData.put("name", position.getPositionName());

            // 统计该职位的申请数
            LambdaQueryWrapper<JobApplication> applicationWrapper = new LambdaQueryWrapper<>();
            applicationWrapper.eq(JobApplication::getPositionId, position.getId());
            long applicationCount = jobApplicationService.count(applicationWrapper);
            positionData.put("applicationCount", applicationCount);

            result.add(positionData);
        }

        // 按申请数降序排序
        result.sort((a, b) -> {
            Long countA = (Long) a.get("applicationCount");
            Long countB = (Long) b.get("applicationCount");
            return countB.compareTo(countA);
        });

        return result;
    }

    @Override
    public List<Map<String, Object>> getFunnelData() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 1. 总申请数
        long totalApplications = jobApplicationService.count();
        Map<String, Object> step1 = new HashMap<>();
        step1.put("name", "简历投递");
        step1.put("value", totalApplications);
        result.add(step1);

        // 2. 筛选通过数(screened)
        long screened = jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStatus, "screened")
                .count()
                + jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStatus, "test_passed")
                .count()
                + jobApplicationService.lambdaQuery()
                .eq(JobApplication::getStatus, "interview_passed")
                .count();
        Map<String, Object> step2 = new HashMap<>();
        step2.put("name", "筛选通过");
        step2.put("value", screened);
        result.add(step2);

        // 3. 参加笔试数
        long writtenTests = jobApplicationService.lambdaQuery()
                .in(JobApplication::getStatus, "test_passed", "test_failed", "interview_passed", "interview_failed", "offered")
                .count();
        Map<String, Object> step3 = new HashMap<>();
        step3.put("name", "参加笔试");
        step3.put("value", writtenTests);
        result.add(step3);

        // 4. 进入面试数
        long interviews = jobApplicationService.lambdaQuery()
                .in(JobApplication::getStatus, "interview_passed", "interview_failed", "offered")
                .count();
        Map<String, Object> step4 = new HashMap<>();
        step4.put("name", "进入面试");
        step4.put("value", interviews);
        result.add(step4);

        // 5. 收到Offer数
        long offers = offerService.lambdaQuery()
                .eq(Offer::getStatus, "accepted")
                .count();
        Map<String, Object> step5 = new HashMap<>();
        step5.put("name", "收到Offer");
        step5.put("value", offers);
        result.add(step5);

        return result;
    }

    @Override
    public List<Map<String, Object>> getTopPositions(Integer limit) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取所有职位
        List<Position> positions = positionService.list();

        for (Position position : positions) {
            // 统计该职位的申请数
            LambdaQueryWrapper<JobApplication> applicationWrapper = new LambdaQueryWrapper<>();
            applicationWrapper.eq(JobApplication::getPositionId, position.getId());
            long applicationCount = jobApplicationService.count(applicationWrapper);

            // 只添加有申请的职位（申请数 > 0）
            if (applicationCount > 0) {
                Map<String, Object> positionData = new HashMap<>();
                positionData.put("name", position.getPositionName());
                positionData.put("value", applicationCount);
                result.add(positionData);
            }
        }

        // 按申请数降序排序并取前N名
        result.sort((a, b) -> {
            Long countA = (Long) a.get("value");
            Long countB = (Long) b.get("value");
            return countB.compareTo(countA);
        });

        return result.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getStatusDistribution() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 统计各种状态的申请数
        String[] statuses = {"pending", "screened", "test_passed", "test_failed",
                             "interview_passed", "interview_failed", "offered", "rejected"};
        String[] labels = {"待处理", "已筛选", "笔试通过", "笔试失败",
                           "面试通过", "面试失败", "已录用", "已拒绝"};

        for (int i = 0; i < statuses.length; i++) {
            String status = statuses[i];
            long count = jobApplicationService.lambdaQuery()
                    .eq(JobApplication::getStatus, status)
                    .count();

            Map<String, Object> statusData = new HashMap<>();
            statusData.put("name", labels[i]);
            statusData.put("value", count);
            result.add(statusData);
        }

        return result;
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 总用户数
        long totalUsers = userService.count();

        // 总学生数
        long totalStudents = studentService.count();

        // 总企业数
        long totalCompanies = companyService.count();

        // 总职位数
        long totalPositions = positionService.count();

        // 总申请数
        long totalApplications = jobApplicationService.count();

        // 计算就业率
        long employedStudents = totalStudents > 0 ? offerService.lambdaQuery()
                .eq(Offer::getStatus, "accepted")
                .count() : 0;

        double employmentRate = totalStudents > 0
                ? new BigDecimal(employedStudents)
                        .divide(new BigDecimal(totalStudents), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100))
                        .doubleValue()
                : 0.0;

        result.put("totalUsers", totalUsers);
        result.put("totalStudents", totalStudents);
        result.put("totalCompanies", totalCompanies);
        result.put("totalPositions", totalPositions);
        result.put("totalApplications", totalApplications);
        result.put("employmentRate", employmentRate);

        return result;
    }
}
