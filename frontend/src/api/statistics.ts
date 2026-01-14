/**
 * 高校教学就业平台 - 统计数据相关 API
 */
import request, { http } from '@/utils/request'
import type { OverviewStats, EmploymentTrendData, PopularCourse, Announcement } from './types'

export const statisticsApi = {
  /**
   * 获取概览统计数据
   */
  getOverview: () => {
    return http.get<OverviewStats>('/statistics/overview')
  },

  /**
   * 获取月度就业趋势数据
   */
  getMonthlyTrend: (months = 6) => {
    return http.get<any>('/statistics/monthly-trend', {
      params: { months },
    })
  },

  /**
   * 获取就业状态分布数据
   */
  getEmploymentStatusDistribution: () => {
    return http.get<any>('/statistics/employment-status-distribution')
  },

  /**
   * 获取热门课程
   */
  getPopularCourses: (limit = 10) => {
    return http.get<PopularCourse[]>('/statistics/popular-courses', {
      params: { limit },
    })
  },

  /**
   * 获取公告列表
   */
  getAnnouncements: (limit = 10) => {
    return http.get<Announcement[]>('/statistics/announcements', {
      params: { limit },
    })
  },

  /**
   * 获取企业统计
   */
  getCompanyStats: () => {
    return http.get<any>('/statistics/company-stats')
  },

  /**
   * 获取职位统计
   */
  getPositionStats: () => {
    return http.get<any>('/statistics/position-stats')
  },

  /**
   * 获取薪资分布统计
   */
  getSalaryDistribution: () => {
    return http.get<any>('/statistics/salary-distribution')
  },

  /**
   * 获取公司排名统计
   */
  getCompanyRanking: (limit = 10) => {
    return http.get<any>('/statistics/company-ranking', {
      params: { limit },
    })
  },
}
