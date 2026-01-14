/**
 * 企业管理 API
 */
import { http } from '@/utils/request'
import type { IPage } from './types'

export interface Company {
  id?: number
  companyName?: string
  creditCode?: string
  shortName?: string
  logo?: string
  industry?: string
  scale?: string
  stage?: string
  city?: string
  address?: string
  description?: string
  benefits?: string
  website?: string
  contactName?: string
  contactPosition?: string
  contactPhone?: string
  contactEmail?: string
  verifyStatus?: 'pending' | 'approved' | 'rejected'
  verifyTime?: string
  rejectReason?: string
  status?: number
  createTime?: string
  updateTime?: string
}

export interface CompanyQueryParams {
  current?: number
  size?: number
  verifyStatus?: string
  status?: number
  keyword?: string
}

/**
 * 分页查询企业列表
 */
export function getCompanyPageApi(params: CompanyQueryParams) {
  return http.get<IPage<Company>>('/company/page', { params })
}

/**
 * 查询所有企业列表(用于下拉框)
 */
export function getCompanyListApi(params?: { status?: number }) {
  return http.get<Company[]>('/company/list', { params })
}

/**
 * 根据ID查询企业详情
 */
export function getCompanyByIdApi(id: number) {
  return http.get<Company>(`/company/${id}`)
}

/**
 * 新增企业
 */
export function createCompanyApi(data: Company) {
  return http.post<void>('/company', data)
}

/**
 * 更新企业
 */
export function updateCompanyApi(data: Company) {
  return http.put<void>('/company', data)
}

/**
 * 删除企业
 */
export function deleteCompanyApi(id: number) {
  return http.delete<void>(`/company/${id}`)
}

/**
 * 企业认证审核
 */
export function verifyCompanyApi(id: number, data: { verifyStatus: string; rejectReason?: string }) {
  return http.post<void>(`/company/${id}/verify`, data)
}
