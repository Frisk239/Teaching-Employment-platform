import request from '@/utils/request'
import type { Result, IPage } from './types'

// 学校接口类型定义
export interface School {
  id?: number
  name: string
  code: string
  address?: string
  contactPerson?: string
  contactPhone?: string
  establishmentDate?: string
  description?: string
  status?: number
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface SchoolPageParams {
  current: number
  size: number
  name?: string
  address?: string
}

/**
 * 获取学校分页列表
 */
export function getSchoolPageApi(params: SchoolPageParams) {
  return request<Result<IPage<School>>>({
    url: '/school/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有学校列表
 */
export function getSchoolListApi() {
  return request<Result<School[]>>({
    url: '/school/list',
    method: 'get'
  })
}

/**
 * 根据ID获取学校详情
 */
export function getSchoolByIdApi(id: number) {
  return request<Result<School>>({
    url: `/school/${id}`,
    method: 'get'
  })
}

/**
 * 创建学校
 */
export function createSchoolApi(data: School) {
  return request<Result<void>>({
    url: '/school',
    method: 'post',
    data
  })
}

/**
 * 更新学校
 */
export function updateSchoolApi(data: School) {
  return request<Result<void>>({
    url: '/school',
    method: 'put',
    data
  })
}

/**
 * 删除学校
 */
export function deleteSchoolApi(id: number) {
  return request<Result<void>>({
    url: `/school/${id}`,
    method: 'delete'
  })
}
