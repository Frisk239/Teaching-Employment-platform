/**
 * 高校教学就业平台 - 企业相关 API
 */
import request, { http } from '@/utils/request'
import type { Company, CompanyQueryParams, PageResult } from './types'

export const companyApi = {
  /**
   * 获取所有企业
   */
  getAll: () => {
    return http.get<Company[]>('/company/list')
  },

  /**
   * 根据 ID 获取企业
   */
  getById: (id: number) => {
    return http.get<Company>(`/company/${id}`)
  },

  /**
   * 创建企业
   */
  create: (data: Company) => {
    return http.post<Company>('/company', data)
  },

  /**
   * 更新企业
   */
  update: (data: Company) => {
    return http.put<Company>('/company', data)
  },

  /**
   * 删除企业
   */
  delete: (id: number) => {
    return http.delete<boolean>(`/company/${id}`)
  },

  /**
   * 分页查询企业
   */
  getPage: (params: CompanyQueryParams) => {
    return http.get<PageResult<Company>>('/company/page', { params })
  },
}
