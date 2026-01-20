import { http } from '@/utils/request'
import type { IPage } from './types'

// 职位推荐接口类型定义
export interface PositionRecommendation {
  id?: number
  studentId: number
  positionId: number
  teacherId: number
  reason?: string
  remark?: string
  status?: string
  viewTime?: string
  createTime?: string
  updateTime?: string
  studentName?: string
  positionName?: string
  companyName?: string
  teacherName?: string
}

// 指导记录接口类型定义
export interface GuidanceRecord {
  id?: number
  studentId: number
  teacherId: number
  guidanceType: string
  content: string
  nextAction?: string
  guidanceDate: string
  location?: string
  createTime?: string
  updateTime?: string
  studentName?: string
  teacherName?: string
  guidanceTypeName?: string
}

// ==================== 职位推荐相关API ====================

/**
 * 创建职位推荐
 */
export function createRecommendationApi(data: {
  studentId: number
  positionId: number
  teacherId: number
  reason?: string
  remark?: string
}) {
  return http.post<void>('/position-recommendation', null, { params: data })
}

/**
 * 获取学员的职位推荐列表
 */
export function getRecommendationsByStudentApi(studentId: number, params?: { current?: number; size?: number }) {
  return http.get<IPage<PositionRecommendation>>(`/position-recommendation/student/${studentId}`, {
    params: { current: 1, size: 10, ...params }
  })
}

/**
 * 获取教师创建的职位推荐列表
 */
export function getRecommendationsByTeacherApi(teacherId: number, params?: { current?: number; size?: number }) {
  return http.get<IPage<PositionRecommendation>>(`/position-recommendation/teacher/${teacherId}`, {
    params: { current: 1, size: 10, ...params }
  })
}

/**
 * 标记推荐为已查看
 */
export function markRecommendationAsViewedApi(id: number) {
  return http.put<void>(`/position-recommendation/${id}/view`)
}

/**
 * 更新推荐状态
 */
export function updateRecommendationStatusApi(id: number, status: string) {
  return http.put<void>(`/position-recommendation/${id}/status`, null, { params: { status } })
}

/**
 * 删除职位推荐
 */
export function deleteRecommendationApi(id: number) {
  return http.delete<void>(`/position-recommendation/${id}`)
}

// ==================== 指导记录相关API ====================

/**
 * 创建指导记录
 */
export function createGuidanceApi(data: {
  studentId: number
  teacherId: number
  guidanceType: string
  content: string
  nextAction?: string
  guidanceDate: string
  location?: string
}) {
  return http.post<void>('/guidance-record', null, { params: data })
}

/**
 * 获取学员的指导记录列表
 */
export function getGuidancesByStudentApi(studentId: number, params?: { current?: number; size?: number }) {
  return http.get<IPage<GuidanceRecord>>(`/guidance-record/student/${studentId}`, {
    params: { current: 1, size: 10, ...params }
  })
}

/**
 * 获取学员的指导记录列表（不分页）
 */
export function getGuidanceListByStudentApi(studentId: number) {
  return http.get<GuidanceRecord[]>(`/guidance-record/student/${studentId}/list`)
}

/**
 * 获取教师创建的指导记录列表
 */
export function getGuidancesByTeacherApi(teacherId: number, params?: { current?: number; size?: number }) {
  return http.get<IPage<GuidanceRecord>>(`/guidance-record/teacher/${teacherId}`, {
    params: { current: 1, size: 10, ...params }
  })
}

/**
 * 获取指导记录详情
 */
export function getGuidanceByIdApi(id: number) {
  return http.get<GuidanceRecord>(`/guidance-record/${id}`)
}

/**
 * 更新指导记录
 */
export function updateGuidanceApi(data: GuidanceRecord) {
  return http.put<void>('/guidance-record', data)
}

/**
 * 删除指导记录
 */
export function deleteGuidanceApi(id: number) {
  return http.delete<void>(`/guidance-record/${id}`)
}
