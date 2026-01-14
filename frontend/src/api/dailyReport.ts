/**
 * 日报管理 API
 */
import { http } from '@/utils/request'
import type { IPage } from './types'

export interface DailyReport {
  id?: number
  studentId?: number
  studentName?: string
  studentNo?: string
  reportDate?: string
  todayContent?: string
  todayProgress?: string
  problems?: string
  tomorrowPlan?: string
  studyHours?: number
  codeLines?: number
  attachmentUrl?: string
  teacherComment?: string
  rating?: number
  status?: 'draft' | 'submitted' | 'reviewed'
  submitTime?: string
  reviewTime?: string
  createTime?: string
  updateTime?: string
}

export interface DailyReportQueryParams {
  current?: number
  size?: number
  studentId?: number
  startDate?: string
  endDate?: string
  status?: string
}

/**
 * 分页查询日报列表
 */
export function getDailyReportPageApi(params: DailyReportQueryParams) {
  return http.get<IPage<DailyReport>>('/daily-report/page', { params })
}

/**
 * 根据学生ID查询日报列表
 */
export function getDailyReportsByStudentApi(studentId: number, params: { current?: number; size?: number }) {
  return http.get<IPage<DailyReport>>(`/daily-report/student/${studentId}`, { params })
}

/**
 * 根据ID查询日报详情
 */
export function getDailyReportByIdApi(id: number) {
  return http.get<DailyReport>(`/daily-report/${id}`)
}

/**
 * 根据日期查询日报
 */
export function getDailyReportByDateApi(studentId: number, date: string) {
  return http.get<DailyReport>(`/daily-report/student/${studentId}/date/${date}`)
}

/**
 * 新增日报
 */
export function createDailyReportApi(data: DailyReport) {
  return http.post<void>('/daily-report', data)
}

/**
 * 更新日报
 */
export function updateDailyReportApi(data: DailyReport) {
  return http.put<void>('/daily-report', data)
}

/**
 * 删除日报
 */
export function deleteDailyReportApi(id: number) {
  return http.delete<void>(`/daily-report/${id}`)
}

/**
 * 提交日报
 */
export function submitDailyReportApi(id: number) {
  return http.post<void>(`/daily-report/${id}/submit`)
}

/**
 * 教师批阅日报
 */
export function reviewDailyReportApi(id: number, data: { teacherComment?: string; rating?: number }) {
  return http.post<void>(`/daily-report/${id}/review`, data)
}
