package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.employment.entity.Company;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.CourseStudent;
import com.teaching.employment.entity.Offer;
import com.teaching.employment.entity.Position;
import com.teaching.employment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
}
