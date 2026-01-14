import request from '@/utils/request'
import type { Result, IPage } from './types'

// 教师接口类型定义
export interface Teacher {
  id?: number
  username: string
  realName: string
  schoolId: number
  schoolName?: string
  email?: string
  phone?: string
  title?: string
  department?: string
  subject?: string
  hireDate?: string
  status?: number
  avatar?: string
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface TeacherPageParams {
  current: number
  size: number
  keyword?: string
  schoolId?: number
  department?: string
  status?: number
}

/**
 * 获取教师分页列表
 */
export function getTeacherPageApi(params: TeacherPageParams) {
  return request<Result<IPage<Teacher>>>({
    url: '/teacher/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有教师列表
 */
export function getTeacherListApi() {
  return request<Result<Teacher[]>>({
    url: '/teacher/list',
    method: 'get'
  })
}

/**
 * 根据ID获取教师详情
 */
export function getTeacherByIdApi(id: number) {
  return request<Result<Teacher>>({
    url: `/teacher/${id}`,
    method: 'get'
  })
}

/**
 * 创建教师
 */
export function createTeacherApi(data: Teacher) {
  return request<Result<void>>({
    url: '/teacher',
    method: 'post',
    data
  })
}

/**
 * 更新教师
 */
export function updateTeacherApi(data: Teacher) {
  return request<Result<void>>({
    url: '/teacher',
    method: 'put',
    data
  })
}

/**
 * 删除教师
 */
export function deleteTeacherApi(id: number) {
  return request<Result<void>>({
    url: `/teacher/${id}`,
    method: 'delete'
  })
}
