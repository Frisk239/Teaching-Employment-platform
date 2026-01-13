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
}
