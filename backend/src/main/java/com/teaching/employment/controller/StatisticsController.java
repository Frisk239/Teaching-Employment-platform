package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.service.JobApplicationService;
import com.teaching.employment.service.OfferService;
import com.teaching.employment.service.StatisticsService;
import com.teaching.employment.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 就业统计控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/statistics")
@Api(tags = "就业统计管理")
@RequiredArgsConstructor
public class StatisticsController {

    private final StudentService studentService;
    private final JobApplicationService jobApplicationService;
    private final OfferService offerService;
    private final StatisticsService statisticsService;

    /**
     * 获取就业概况统计数据
     */
    @GetMapping("/overview")
    @ApiOperation("获取就业概况统计数据")
    public Result<Map<String, Object>> getEmploymentOverview() {
        Map<String, Object> data = new HashMap<>();

        // 总学员数
        long totalStudents = studentService.count();

        // 已就业学员数(收到Offer)
        long employedStudents = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .count();

        // 就业率
        BigDecimal employmentRate = totalStudents > 0
                ? new BigDecimal(employedStudents).divide(new BigDecimal(totalStudents), 2, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal(100))
                : BigDecimal.ZERO;

        // 平均起薪
        double avgSalary = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .list()
                .stream()
                .filter(offer -> offer.getSalary() != null)
                .mapToDouble(offer -> offer.getSalary().doubleValue())
                .average()
                .orElse(0.0);

        // 本月新增就业
        long monthlyEmployment = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .apply("DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
                .count();

        data.put("totalStudents", totalStudents);
        data.put("employedStudents", employedStudents);
        data.put("employmentRate", employmentRate);
        data.put("avgSalary", avgSalary);
        data.put("monthlyEmployment", monthlyEmployment);

        return Result.ok(data);
    }

    /**
     * 获取职位申请统计数据
     */
    @GetMapping("/applications")
    @ApiOperation("获取职位申请统计数据")
    public Result<Map<String, Object>> getApplicationStatistics() {
        Map<String, Object> data = new HashMap<>();

        // 总申请数
        long totalApplications = jobApplicationService.count();

        // 待处理申请数
        long pendingApplications = jobApplicationService.lambdaQuery()
                .eq(com.teaching.employment.entity.JobApplication::getStatus, "pending")
                .count();

        // 已通过笔试
        long passedWrittenTest = jobApplicationService.lambdaQuery()
                .eq(com.teaching.employment.entity.JobApplication::getStatus, "written_test_passed")
                .count();

        // 已通过面试
        long passedInterview = jobApplicationService.lambdaQuery()
                .eq(com.teaching.employment.entity.JobApplication::getStatus, "interview_passed")
                .count();

        // 已发送Offer
        long offerSent = jobApplicationService.lambdaQuery()
                .eq(com.teaching.employment.entity.JobApplication::getStatus, "offer_sent")
                .count();

        data.put("totalApplications", totalApplications);
        data.put("pendingApplications", pendingApplications);
        data.put("passedWrittenTest", passedWrittenTest);
        data.put("passedInterview", passedInterview);
        data.put("offerSent", offerSent);

        return Result.ok(data);
    }

    /**
     * 获取薪资分布统计
     */
    @GetMapping("/salary-distribution")
    @ApiOperation("获取薪资分布统计")
    public Result<Map<String, Object>> getSalaryDistribution() {
        Map<String, Object> data = new HashMap<>();

        // 0-5k
        long range1 = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .between(com.teaching.employment.entity.Offer::getSalary, 0, 5000)
                .count();

        // 5k-10k
        long range2 = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .between(com.teaching.employment.entity.Offer::getSalary, 5001, 10000)
                .count();

        // 10k-15k
        long range3 = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .between(com.teaching.employment.entity.Offer::getSalary, 10001, 15000)
                .count();

        // 15k-20k
        long range4 = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .between(com.teaching.employment.entity.Offer::getSalary, 15001, 20000)
                .count();

        // 20k以上
        long range5 = offerService.lambdaQuery()
                .eq(com.teaching.employment.entity.Offer::getStatus, "accepted")
                .gt(com.teaching.employment.entity.Offer::getSalary, 20000)
                .count();

        data.put("0-5k", range1);
        data.put("5k-10k", range2);
        data.put("10k-15k", range3);
        data.put("15k-20k", range4);
        data.put("20k+", range5);

        return Result.ok(data);
    }

    /**
     * 获取就业状态分布数据
     */
    @GetMapping("/employment-status-distribution")
    @ApiOperation("获取就业状态分布数据")
    public Result<Map<String, Object>> getEmploymentStatusDistribution() {
        Map<String, Object> data = statisticsService.getEmploymentStatusDistribution();
        return Result.ok(data);
    }

    /**
     * 获取岗位类型分布统计
     */
    @GetMapping("/position-type-distribution")
    @ApiOperation("获取岗位类型分布统计")
    public Result<Map<String, Object>> getPositionTypeDistribution() {
        Map<String, Object> data = statisticsService.getPositionTypeDistribution();
        return Result.ok(data);
    }

    /**
     * 获取公司排名统计(录用人数最多的公司)
     */
    @GetMapping("/company-ranking")
    @ApiOperation("获取公司排名统计")
    public Result<List<Map<String, Object>>> getCompanyRanking(
            @ApiParam("返回前N名") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> data = statisticsService.getCompanyRanking(limit);
        return Result.ok(data);
    }

    /**
     * 获取课程就业率统计
     */
    @GetMapping("/course-employment")
    @ApiOperation("获取课程就业率统计")
    public Result<List<Map<String, Object>>> getCourseEmploymentRate() {
        List<Map<String, Object>> data = statisticsService.getCourseEmploymentRate();
        return Result.ok(data);
    }

    /**
     * 获取热门课程列表
     */
    @GetMapping("/popular-courses")
    @ApiOperation("获取热门课程列表")
    public Result<List<Map<String, Object>>> getPopularCourses(
            @ApiParam("返回前N名") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> data = statisticsService.getPopularCourses(limit);
        return Result.ok(data);
    }

    /**
     * 获取公告列表
     */
    @GetMapping("/announcements")
    @ApiOperation("获取公告列表")
    public Result<List<Map<String, Object>>> getAnnouncements(
            @ApiParam("返回前N条") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> data = statisticsService.getAnnouncements(limit);
        return Result.ok(data);
    }

    /**
     * 获取企业统计数据
     */
    @GetMapping("/company-stats")
    @ApiOperation("获取企业统计数据")
    public Result<List<Map<String, Object>>> getCompanyStats() {
        List<Map<String, Object>> data = statisticsService.getCompanyStats();
        return Result.ok(data);
    }

    /**
     * 获取职位统计数据
     */
    @GetMapping("/position-stats")
    @ApiOperation("获取职位统计数据")
    public Result<List<Map<String, Object>>> getPositionStats() {
        List<Map<String, Object>> data = statisticsService.getPositionStats();
        return Result.ok(data);
    }

    /**
     * 获取招聘漏斗数据
     */
    @GetMapping("/funnel-data")
    @ApiOperation("获取招聘漏斗数据")
    public Result<?> getFunnelData() {
        return Result.ok(statisticsService.getFunnelData());
    }

    /**
     * 获取热门职位
     */
    @GetMapping("/top-positions")
    @ApiOperation("获取热门职位")
    public Result<?> getTopPositions(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(statisticsService.getTopPositions(limit));
    }

    /**
     * 获取状态分布数据
     */
    @GetMapping("/status-distribution")
    @ApiOperation("获取状态分布数据")
    public Result<?> getStatusDistribution() {
        return Result.ok(statisticsService.getStatusDistribution());
    }
}
