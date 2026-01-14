import { http } from '@/utils/request'
import type { IPage } from './types'

// 学校接口类型定义
export interface School {
  id?: number
  schoolName?: string
  schoolCode?: string
  licenseNumber?: string
  licenseIssueDate?: string
  licenseExpiryDate?: string
  licenseImageUrl?: string
  province?: string
  city?: string
  address?: string
  website?: string
  contactPerson?: string
  contactPhone?: string
  email?: string
  description?: string
  status?: number
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface SchoolPageParams {
  current: number
  size: number
  schoolName?: string
  province?: string
  city?: string
}

/**
 * 获取学校分页列表
 */
export function getSchoolPageApi(params: SchoolPageParams) {
  return http.get<IPage<School>>('/school/page', { params })
}

/**
 * 获取所有学校列表
 */
export function getSchoolListApi() {
  return http.get<School[]>('/school/list')
}

/**
 * 根据ID获取学校详情
 */
export function getSchoolByIdApi(id: number) {
  return http.get<School>(`/school/${id}`)
}

/**
 * 创建学校
 */
export function createSchoolApi(data: School) {
  return http.post<void>('/school', data)
}

/**
 * 更新学校
 */
export function updateSchoolApi(data: School) {
  return http.put<void>('/school', data)
}

/**
 * 删除学校
 */
export function deleteSchoolApi(id: number) {
  return http.delete<void>(`/school/${id}`)
}
