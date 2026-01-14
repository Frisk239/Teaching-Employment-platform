import { http } from '@/utils/request'
import type { IPage } from './types'

// 教师接口类型定义
export interface Teacher {
  id?: number
  userId?: number
  teacherNo?: string
  schoolId?: number
  department?: string
  title?: string
  education?: string
  specialty?: string
  entryDate?: string
  idCard?: string
  gender?: number
  birthDate?: string
  address?: string
  description?: string
  createTime?: string
  updateTime?: string
  // 关联查询字段
  user?: {
    id: number
    username: string
    realName: string
  }
  school?: {
    id: number
    schoolName: string
  }
  // 兼容字段
  realName?: string
  teacherName?: string
  schoolName?: string
}

// 分页查询参数
export interface TeacherPageParams {
  current: number
  size: number
  keyword?: string
  schoolId?: number
  department?: string
}

/**
 * 获取教师分页列表
 */
export function getTeacherPageApi(params: TeacherPageParams) {
  return http.get<IPage<Teacher>>('/teacher/page', { params })
}

/**
 * 获取所有教师列表
 */
export function getTeacherListApi(schoolId?: number) {
  return http.get<Teacher[]>('/teacher/list', {
    params: schoolId ? { schoolId } : undefined
  })
}

/**
 * 根据ID获取教师详情
 */
export function getTeacherByIdApi(id: number) {
  return http.get<Teacher>(`/teacher/${id}`)
}

/**
 * 创建教师
 */
export function createTeacherApi(data: Teacher) {
  return http.post<void>('/teacher', data)
}

/**
 * 更新教师
 */
export function updateTeacherApi(data: Teacher) {
  return http.put<void>('/teacher', data)
}

/**
 * 删除教师
 */
export function deleteTeacherApi(id: number) {
  return http.delete<void>(`/teacher/${id}`)
}

/**
 * 获取教师统计数据
 */
export function getTeacherStatsApi() {
  return http.get<{
    total: number
    schools: number
    departments: number
    courses: number
  }>('/teacher/stats')
}

/**
 * 导出教师数据
 */
export function exportTeachersApi(params: TeacherPageParams) {
  return http.get('/teacher/export', {
    params,
    responseType: 'blob'
  })
}

/**
 * 导入教师数据
 */
export function importTeachersApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<string>('/teacher/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 下载教师导入模板
 */
export function downloadTeacherTemplateApi() {
  return http.get('/teacher/template', {
    responseType: 'blob'
  })
}
