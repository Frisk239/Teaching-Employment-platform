import request from '@/utils/request'
import type { Result, IPage } from './types'

export interface Position {
  id?: number
  companyId: number
  positionName: string
  companyName?: string
  positionType?: string
  city?: string
  salaryMin?: number
  salaryMax?: number
  salaryUnit?: string
  education?: string
  experience?: string
  description?: string
  requirements?: string
  techStack?: string
  capabilityRadar?: string // 能力雷达图数据（JSON 字符串）
  recruitCount?: number
  hiredCount?: number
  applicationCount?: number
  status?: string
  publishTime?: string
  deadline?: string
  createTime?: string
  updateTime?: string
}

export interface PositionPageParams {
  current: number
  size: number
  positionName?: string
  companyId?: number
  city?: string
  status?: string
}

export function getPositionPageApi(params: PositionPageParams): Promise<IPage<Position>> {
  return request({
    url: '/position/page',
    method: 'get',
    params
  }) as Promise<IPage<Position>>
}

export function getPositionListApi(): Promise<Position[]> {
  return request({
    url: '/position/list',
    method: 'get'
  }) as Promise<Position[]>
}

export function getPositionByIdApi(id: number): Promise<Position> {
  return request({
    url: `/position/${id}`,
    method: 'get'
  }) as Promise<Position>
}

export function createPositionApi(data: Position) {
  return request<Result<void>>({
    url: '/position',
    method: 'post',
    data
  })
}

export function updatePositionApi(data: Position) {
  return request<Result<void>>({
    url: '/position',
    method: 'put',
    data
  })
}

export function deletePositionApi(id: number) {
  return request<Result<void>>({
    url: `/position/${id}`,
    method: 'delete'
  })
}
