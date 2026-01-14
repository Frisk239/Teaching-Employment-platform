import request from '@/utils/request'
import type { Result, IPage } from './types'

// 公司接口类型定义
export interface Company {
  id?: number
  companyName: string
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
  verifyStatus?: string
  status?: number
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface CompanyPageParams {
  current: number
  size: number
  name?: string
  industry?: string
  status?: number
}

/**
 * 获取公司分页列表
 */
export function getCompanyPageApi(params: CompanyPageParams): Promise<IPage<Company>> {
  return request({
    url: '/company/page',
    method: 'get',
    params
  }) as Promise<IPage<Company>>
}

/**
 * 获取所有公司列表
 */
export function getCompanyListApi(): Promise<Company[]> {
  return request({
    url: '/company/list',
    method: 'get'
  }) as Promise<Company[]>
}

/**
 * 根据ID获取公司详情
 */
export function getCompanyByIdApi(id: number) {
  return request<Result<Company>>({
    url: `/company/${id}`,
    method: 'get'
  })
}

/**
 * 创建公司
 */
export function createCompanyApi(data: Company) {
  return request<Result<void>>({
    url: '/company',
    method: 'post',
    data
  })
}

/**
 * 更新公司
 */
export function updateCompanyApi(data: Company) {
  return request<Result<void>>({
    url: '/company',
    method: 'put',
    data
  })
}

/**
 * 删除公司
 */
export function deleteCompanyApi(id: number) {
  return request<Result<void>>({
    url: `/company/${id}`,
    method: 'delete'
  })
}
