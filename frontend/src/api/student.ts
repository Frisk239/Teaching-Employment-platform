import { http } from '@/utils/request'
import type { IPage } from './types'

// 学生接口类型定义
export interface Student {
  id?: number
  username: string
  realName: string
  schoolId: number
  schoolName?: string
  email?: string
  phone?: string
  grade?: string
  major?: string
  className?: string
  enrollmentDate?: string
  status?: number
  avatar?: string
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface StudentPageParams {
  current: number
  size: number
  keyword?: string
  schoolId?: number
  grade?: string
  major?: string
  status?: number
}

/**
 * 获取学生分页列表
 */
export function getStudentPageApi(params: StudentPageParams) {
  return http.get<IPage<Student>>('/student/page', { params })
}

/**
 * 获取所有学生列表
 */
export function getStudentListApi(params?: { current?: number; size?: number }) {
  return http.get<IPage<Student>>('/student/page', { params: { current: 1, size: 1000, ...params } })
}

/**
 * 根据ID获取学生详情
 */
export function getStudentByIdApi(id: number) {
  return http.get<Student>(`/student/${id}`)
}

/**
 * 创建学生
 */
export function createStudentApi(data: Student) {
  return http.post<void>('/student', data)
}

/**
 * 更新学生
 */
export function updateStudentApi(data: Student) {
  return http.put<void>('/student', data)
}

/**
 * 删除学生
 */
export function deleteStudentApi(id: number) {
  return http.delete<void>(`/student/${id}`)
}

/**
 * Excel导入学生
 */
export function importStudentsApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<string>('/student/import', formData)
}

/**
 * 下载学生导入模板
 */
export function downloadStudentTemplateApi() {
  return http.get('/student/template', {
    responseType: 'blob'
  })
}

/**
 * Excel导出学生
 */
export function exportStudentsApi(params: StudentPageParams) {
  return http.get('/student/export', {
    params,
    responseType: 'blob'
  })
}
