/**
 * 高校教学就业平台 - 招聘职位相关 API
 */
import request, { http } from '@/utils/request'
import type { Recruitment, RecruitmentQueryParams, PageResult } from './types'

export const recruitmentApi = {
  /**
   * 获取所有职位
   */
  getAll: () => {
    return http.get<Recruitment[]>('/position/list')
  },

  /**
   * 根据 ID 获取职位
   */
  getById: (id: number) => {
    return http.get<Recruitment>(`/position/${id}`)
  },

  /**
   * 创建职位
   */
  create: (data: Recruitment) => {
    return http.post<Recruitment>('/position', data)
  },

  /**
   * 更新职位
   */
  update: (data: Recruitment) => {
    return http.put<Recruitment>('/position', data)
  },

  /**
   * 删除职位
   */
  delete: (id: number) => {
    return http.delete<boolean>(`/position/${id}`)
  },

  /**
   * 分页查询职位
   */
  getPage: (params: RecruitmentQueryParams) => {
    return http.get<PageResult<Recruitment>>('/position/page', { params })
  },
}
