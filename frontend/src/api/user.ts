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

/**
 * 删除用户
 */
export function deleteUserApi(id: number) {
  return request<Result<void>>({
    url: `/user/${id}`,
    method: 'delete'
  })
}

/**
 * 创建用户
 */
export function createUserApi(data: User) {
  return request<Result<void>>({
    url: '/user',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUserApi(id: number, data: User) {
  return request<Result<void>>({
    url: `/user/${id}`,
    method: 'put',
    data
  })
}

/**
 * 批量删除用户
 */
export function batchDeleteUserApi(ids: number[]) {
  return request<Result<void>>({
    url: '/user/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 导出用户
 */
export function exportUsersApi(params?: any) {
  return request<Blob>({
    url: '/user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 重置密码
 */
export function resetPasswordApi(id: number, newPassword: string) {
  return request<Result<void>>({
    url: `/user/${id}/password`,
    method: 'put',
    data: { newPassword }
  })
}
