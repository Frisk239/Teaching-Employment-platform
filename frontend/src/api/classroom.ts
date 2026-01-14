import { http } from '@/utils/request'
import type { IPage } from './types'

// 教室接口类型定义
export interface Classroom {
  id?: number
  classroomNo?: string
  classroomName?: string
  schoolId?: number
  building?: string
  floor?: string
  capacity?: number
  classroomType?: string
  hasProjector?: number
  hasComputer?: number
  hasMultimedia?: number
  equipment?: string
  status?: number
  createTime?: string
  updateTime?: string
  // 关联查询字段
  school?: {
    id: number
    schoolName: string
  }
  schoolName?: string
}

// 分页查询参数
export interface ClassroomPageParams {
  current: number
  size: number
  schoolId?: number
  keyword?: string
}

/**
 * 获取教室分页列表
 */
export function getClassroomPageApi(params: ClassroomPageParams) {
  return http.get<IPage<Classroom>>('/classroom/page', { params })
}

/**
 * 获取所有教室列表
 */
export function getClassroomListApi(schoolId?: number) {
  return http.get<Classroom[]>('/classroom/list', {
    params: schoolId ? { schoolId } : undefined
  })
}

/**
 * 根据ID获取教室详情
 */
export function getClassroomByIdApi(id: number) {
  return http.get<Classroom>(`/classroom/${id}`)
}

/**
 * 创建教室
 */
export function createClassroomApi(data: Classroom) {
  return http.post<void>('/classroom', data)
}

/**
 * 更新教室
 */
export function updateClassroomApi(data: Classroom) {
  return http.put<void>('/classroom', data)
}

/**
 * 删除教室
 */
export function deleteClassroomApi(id: number) {
  return http.delete<void>(`/classroom/${id}`)
}

/**
 * 获取教室使用统计
 */
export function getClassroomStatisticsApi(id: number) {
  return http.get<any>(`/classroom/${id}/usage-statistics`)
}

/**
 * 查询可用教室
 */
export function getAvailableClassroomsApi(params: {
  schoolId?: number
  minCapacity?: number
  hasProjector?: number
  hasComputer?: number
}) {
  return http.get<Classroom[]>('/classroom/available', { params })
}
