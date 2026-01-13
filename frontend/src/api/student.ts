/**
 * 高校教学就业平台 - 学生相关 API
 */
import request, { http } from '@/utils/request'
import type { Student, StudentQueryParams, PageResult } from './types'

export const studentApi = {
  /**
   * 获取所有学生
   */
  getAll: () => {
    return http.get<Student[]>('/student/list')
  },

  /**
   * 根据 ID 获取学生
   */
  getById: (id: number) => {
    return http.get<Student>(`/student/${id}`)
  },

  /**
   * 创建学生
   */
  create: (data: Student) => {
    return http.post<Student>('/student', data)
  },

  /**
   * 更新学生
   */
  update: (data: Student) => {
    return http.put<Student>('/student', data)
  },

  /**
   * 删除学生
   */
  delete: (id: number) => {
    return http.delete<boolean>(`/student/${id}`)
  },

  /**
   * 分页查询学生
   */
  getPage: (params: StudentQueryParams) => {
    return http.get<PageResult<Student>>('/student/page', { params })
  },

  /**
   * 批量删除学生
   */
  batchDelete: (ids: number[]) => {
    return http.post<boolean>('/student/batch-delete', { ids })
  },

  /**
   * 导入学生数据
   */
  import: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload<{ success: number; failed: number }>('/student/import', formData)
  },

  /**
   * 导出学生数据
   */
  export: (params?: StudentQueryParams) => {
    return http.download('/student/export', { params })
  },
}
