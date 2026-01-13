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
        List<String> labels = new ArrayList<>();
        List<Integer> employedData = new ArrayList<>();
        List<Integer> seekingData = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();

        // 获取所有学生总数
        long totalStudents = studentService.count();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            String monthLabel = yearMonth.format(DateTimeFormatter.ofPattern("M月"));
            labels.add(monthLabel);

            String monthStr = yearMonth.format(formatter);

            // 获取该月已就业的学生数(有已接受的Offer且创建时间在该月)
            LambdaQueryWrapper<Offer> employedWrapper = new LambdaQueryWrapper<>();
            employedWrapper.eq(Offer::getStatus, "accepted");
            employedWrapper.apply("DATE_FORMAT(create_time, '%Y-%m') = {0}", monthStr);
            long employedCount = offerService.count(employedWrapper);

            // 获取该月仍在求职的学生数(有pending申请且在该月创建或更新)
            LambdaQueryWrapper<JobApplication> seekingWrapper = new LambdaQueryWrapper<>();
            seekingWrapper.eq(JobApplication::getStatus, "pending");
            seekingWrapper.apply("DATE_FORMAT(create_time, '%Y-%m') = {0}", monthStr);
            long seekingCount = jobApplicationService.count(seekingWrapper);

            employedData.add((int) employedCount);
            seekingData.add((int) seekingCount);
        }

        result.put("labels", labels);
        result.put("employed", employedData);
        result.put("seeking", seekingData);

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
}
