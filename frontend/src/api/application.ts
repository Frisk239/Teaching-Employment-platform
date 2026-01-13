/**
 * 高校教学就业平台 - 求职申请相关 API
 */
import request, { http } from '@/utils/request'
import type { Application, ApplicationSubmitForm, ApplicationQueryParams, PageResult } from './types'

export const applicationApi = {
  /**
   * 获取所有申请
   */
  getAll: () => {
    return http.get<Application[]>('/job-application/list')
  },

  /**
   * 根据学生ID获取申请
   */
  getByStudentId: (studentId: number) => {
    return http.get<Application[]>(`/job-application/student/${studentId}`)
  },

  /**
   * 提交申请
   */
  submit: (data: ApplicationSubmitForm) => {
    return http.post<boolean>('/job-application', data)
  },

  /**
   * 更新申请
   */
  update: (data: Application) => {
    return http.put<boolean>('/job-application', data)
  },

  /**
   * 根据 ID 获取申请
   */
  getById: (id: number) => {
    return http.get<Application>(`/job-application/${id}`)
  },

  /**
   * 删除申请
   */
  delete: (id: number) => {
    return http.delete<boolean>(`/job-application/${id}`)
  },

  /**
   * 分页查询申请
   */
  getPage: (params: ApplicationQueryParams) => {
    return http.get<PageResult<Application>>('/job-application/page', { params })
  },
}
