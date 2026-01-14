import request from '@/utils/request'
import type { Result, IPage } from './types'

export interface User {
  id?: number
  username: string
  realName?: string
  email?: string
  phone?: string
  roleId?: number
  schoolId?: number
  schoolName?: string
  status?: number
  avatar?: string
  roleCode?: string
  createTime?: string
  updateTime?: string
}

export interface UpdatePasswordData {
  userId: number
  oldPassword: string
  newPassword: string
}

export interface UserPageParams {
  current: number
  size: number
  username?: string
  realName?: string
  roleId?: number
  schoolId?: number
  keyword?: string
}

/**
 * 获取用户分页列表
 */
export function getUserPageApi(params: UserPageParams) {
  return request<Result<IPage<User>>>({
    url: '/user/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取用户详情
 */
export function getUserByIdApi(id: number) {
  return request<Result<User>>({
    url: `/user/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户资料
 */
export function updateProfileApi(data: User) {
  return request<Result<void>>({
    url: '/user/profile',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function updatePasswordApi(data: UpdatePasswordData) {
  return request<Result<void>>({
    url: '/user/password',
    method: 'put',
    data
  })
}

/**
 * 上传头像
 */
export function uploadAvatarApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request<Result<string>>({
    url: '/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
