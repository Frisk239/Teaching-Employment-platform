import request from '@/utils/request'
import type { Result, IPage } from './types'

// 教室接口类型定义
export interface Classroom {
  id?: number
  name: string
  schoolId: number
  schoolName?: string
  building?: string
  floor?: number
  roomNumber?: string
  capacity?: number
  equipment?: string
  remark?: string
  status?: number
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface ClassroomPageParams {
  current: number
  size: number
  name?: string
  schoolId?: number
  status?: number
}

/**
 * 获取教室分页列表
 */
export function getClassroomPageApi(params: ClassroomPageParams) {
  return request<Result<IPage<Classroom>>>({
    url: '/classroom/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有教室列表
 */
export function getClassroomListApi() {
  return request<Result<Classroom[]>>({
    url: '/classroom/list',
    method: 'get'
  })
}

/**
 * 根据ID获取教室详情
 */
export function getClassroomByIdApi(id: number) {
  return request<Result<Classroom>>({
    url: `/classroom/${id}`,
    method: 'get'
  })
}

/**
 * 创建教室
 */
export function createClassroomApi(data: Classroom) {
  return request<Result<void>>({
    url: '/classroom',
    method: 'post',
    data
  })
}

/**
 * 更新教室
 */
export function updateClassroomApi(data: Classroom) {
  return request<Result<void>>({
    url: '/classroom',
    method: 'put',
    data
  })
}

/**
 * 删除教室
 */
export function deleteClassroomApi(id: number) {
  return request<Result<void>>({
    url: `/classroom/${id}`,
    method: 'delete'
  })
}
