import request from '@/utils/request'
import type { Result, IPage } from './types'

// 学生接口类型定义
export interface Student {
  id?: number
  username: string
  realName: string
  schoolId: number
  schoolName?: string
  email?: string
  phone?: string
  grade?: string
  major?: string
  className?: string
  enrollmentDate?: string
  status?: number
  avatar?: string
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface StudentPageParams {
  current: number
  size: number
  keyword?: string
  schoolId?: number
  grade?: string
  major?: string
  status?: number
}

/**
 * 获取学生分页列表
 */
export function getStudentPageApi(params: StudentPageParams): Promise<IPage<Student>> {
  return request({
    url: '/student/page',
    method: 'get',
    params
  }) as Promise<IPage<Student>>
}

/**
 * 获取所有学生列表
 */
export function getStudentListApi(): Promise<Student[]> {
  return request({
    url: '/student/list',
    method: 'get'
  }) as Promise<Student[]>
}

/**
 * 根据ID获取学生详情
 */
export function getStudentByIdApi(id: number): Promise<Student> {
  return request({
    url: `/student/${id}`,
    method: 'get'
  }) as Promise<Student>
}

/**
 * 创建学生
 */
export function createStudentApi(data: Student) {
  return request<Result<void>>({
    url: '/student',
    method: 'post',
    data
  })
}

/**
 * 更新学生
 */
export function updateStudentApi(data: Student) {
  return request<Result<void>>({
    url: '/student',
    method: 'put',
    data
  })
}

/**
 * 删除学生
 */
export function deleteStudentApi(id: number) {
  return request<Result<void>>({
    url: `/student/${id}`,
    method: 'delete'
  })
}

/**
 * Excel导入学生
 */
export function importStudentsApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request<Result<Map<string, number>>>({
    url: '/student/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * Excel导出学生
 */
export function exportStudentsApi(params: StudentPageParams) {
  return request({
    url: '/student/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
