package com.teaching.employment.service;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface StatisticsService {

    /**
     * 获取岗位类型分布统计
     *
     * @return 岗位类型分布数据
     */
    Map<String, Object> getPositionTypeDistribution();

    /**
     * 获取企业排名统计(按录用人数)
     *
     * @param limit 返回前N名
     * @return 企业排名数据
     */
    List<Map<String, Object>> getCompanyRanking(Integer limit);

    /**
     * 获取课程就业率统计
     *
     * @return 课程就业率数据
     */
    List<Map<String, Object>> getCourseEmploymentRate();

    /**
     * 获取月度就业趋势数据
     *
     * @param months 返回最近N个月的数据
     * @return 月度趋势数据
     */
    Map<String, Object> getMonthlyTrend(Integer months);

    /**
     * 获取就业状态分布数据
     *
     * @return 就业状态分布数据
     */
    Map<String, Object> getEmploymentStatusDistribution();

    /**
     * 获取热门课程列表
     *
     * @param limit 返回前N名
     * @return 热门课程列表
     */
    List<Map<String, Object>> getPopularCourses(Integer limit);

    /**
     * 获取公告列表
     *
     * @param limit 返回前N条
     * @return 公告列表
     */
    List<Map<String, Object>> getAnnouncements(Integer limit);

    /**
     * 获取企业统计数据
     *
     * @return 企业统计列表
     */
    List<Map<String, Object>> getCompanyStats();
}
