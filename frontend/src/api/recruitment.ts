/**
 * 高校教学就业平台 - 招聘职位相关 API
 */
import request from '@/utils/request'
import type { Result, IPage } from './types'
import type { Position, PositionQueryParams } from './types'

/**
 * 分页查询职位列表
 */
export function getPositionPageApi(params: PositionQueryParams) {
  return request<Result<IPage<Position>>>({
    url: '/position/page',
    method: 'get',
    params
  })
}

/**
 * 根据企业ID查询职位列表
 */
export function getPositionsByCompanyIdApi(companyId: number, current = 1, size = 10) {
  return request<Result<IPage<Position>>>({
    url: `/position/company/${companyId}`,
    method: 'get',
    params: { current, size }
  })
}

/**
 * 根据ID获取职位详情
 */
export function getPositionByIdApi(id: number) {
  return request<Result<Position>>({
    url: `/position/${id}`,
    method: 'get'
  })
}

/**
 * 新增职位
 */
export function createPositionApi(data: Position) {
  return request<Result<void>>({
    url: '/position',
    method: 'post',
    data
  })
}

/**
 * 更新职位
 */
export function updatePositionApi(data: Position) {
  return request<Result<void>>({
    url: '/position',
    method: 'put',
    data
  })
}

/**
 * 删除职位
 */
export function deletePositionApi(id: number) {
  return request<Result<void>>({
    url: `/position/${id}`,
    method: 'delete'
  })
}

/**
 * 发布职位
 */
export function publishPositionApi(id: number) {
  return request<Result<void>>({
    url: `/position/${id}/publish`,
    method: 'post'
  })
}

/**
 * 暂停职位招聘
 */
export function pausePositionApi(id: number) {
  return request<Result<void>>({
    url: `/position/${id}/pause`,
    method: 'post'
  })
}

/**
 * 关闭职位
 */
export function closePositionApi(id: number) {
  return request<Result<void>>({
    url: `/position/${id}/close`,
    method: 'post'
  })
}

/**
 * 增加投递数量
 */
export function incrementApplicationCountApi(id: number) {
  return request<Result<void>>({
    url: `/position/${id}/increment-application`,
    method: 'post'
  })
}

/**
 * 增加已招人数
 */
export function incrementHiredCountApi(id: number) {
  return request<Result<void>>({
    url: `/position/${id}/increment-hired`,
    method: 'post'
  })
}

/**
 * 获取职位统计信息
 */
export function getPositionStatsApi(id: number) {
  return request<Result<Record<string, any>>>({
    url: `/position/${id}/stats`,
    method: 'get'
  })
}

// 兼容旧API
export const recruitmentApi = {
  getAll: () => request<Result<Position[]>>({ url: '/position/list', method: 'get' }),
  getById: (id: number) => getPositionByIdApi(id),
  create: (data: Position) => createPositionApi(data),
  update: (data: Position) => updatePositionApi(data),
  delete: (id: number) => deletePositionApi(id),
  getPage: (params: PositionQueryParams) => getPositionPageApi(params),
}
