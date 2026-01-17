/**
 * 高校教学就业平台 - 学生就业情况相关 API
 */
import request, { http } from '@/utils/request'

// 学生就业情况接口
export interface StudentEmployment {
  id: number
  realName: string
  className?: string
  major?: string
  grade?: string
  phone?: string
  email?: string
  applicationCount: number
  interviewCount: number
  offerCount: number
  employmentStatus: string
  employmentStatusText: string
}

// 就业统计数据
export interface EmploymentStats {
  totalStudents: number
  appliedStudents: number
  testInProgress: number
  interviewInProgress: number
  offerReceived: number
  hiredStudents: number
  employmentRate: number
}

// 分页查询参数
export interface StudentEmploymentQueryParams {
  current?: number
  size?: number
  keyword?: string
  className?: string
  major?: string
  employmentStatus?: string
}

// 学生就业详情
export interface StudentEmploymentDetail {
  student: any
  applications: any[]
  interviews: any[]
  offers: any[]
  stats: {
    totalApplications: number
    totalInterviews: number
    totalOffers: number
    passedTests: number
    passedInterviews: number
  }
}

export const studentEmploymentApi = {
  /**
   * 获取就业统计数据
   */
  getStats: () => {
    return http.get<EmploymentStats>('/student-employment/stats')
  },

  /**
   * 分页查询学生就业情况列表
   */
  getPage: (params: StudentEmploymentQueryParams) => {
    return http.get<any>('/student-employment/page', { params })
  },

  /**
   * 获取学生就业详情
   */
  getDetail: (studentId: number) => {
    return http.get<StudentEmploymentDetail>(`/student-employment/detail/${studentId}`)
  }
}

/**
 * 获取就业状态标签类型
 */
export function getEmploymentStatusType(status: string): string {
  const typeMap: Record<string, string> = {
    'hired': 'success',        // 已入职
    'offered': 'warning',      // 已获Offer
    'interviewing': 'primary', // 面试中
    'applied': 'info',         // 求职中
    'not_started': 'info'      // 未开始
  }
  return typeMap[status] || 'info'
}

/**
 * 获取就业状态颜色
 */
export function getEmploymentStatusColor(status: string): string {
  const colorMap: Record<string, string> = {
    'hired': '#67c23a',
    'offered': '#e6a23c',
    'interviewing': '#409eff',
    'applied': '#909399',
    'not_started': '#c0c4cc'
  }
  return colorMap[status] || '#909399'
}
