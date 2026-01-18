import { http } from '@/utils/request'
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
  return http.get<IPage<User>>('/user/page', { params })
}

/**
 * 根据ID获取用户详情
 */
export function getUserByIdApi(id: number) {
  return http.get<User>(`/user/${id}`)
}

/**
 * 更新用户资料
 */
export function updateProfileApi(data: User) {
  return http.put<void>('/user/profile', data)
}

/**
 * 修改密码
 */
export function updatePasswordApi(data: UpdatePasswordData) {
  return http.put<void>('/user/password', data)
}

/**
 * 上传头像
 */
export function uploadAvatarApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<string>('/user/avatar', formData)
}

/**
 * 删除用户
 */
export function deleteUserApi(id: number) {
  return http.delete<void>(`/user/${id}`)
}

/**
 * 创建用户
 */
export function createUserApi(data: User) {
  return http.post<void>('/user', data)
}

/**
 * 更新用户
 */
export function updateUserApi(data: User) {
  return http.put<void>('/user', data)
}

/**
 * 批量删除用户
 */
export function batchDeleteUserApi(ids: number[]) {
  return http.delete<void>('/user/batch', { data: ids })
}

/**
 * 导出用户
 */
export function exportUsersApi(params?: UserPageParams) {
  return http.get('/user/export', {
    params,
    responseType: 'blob'
  })
}

/**
 * 重置密码
 */
export function resetPasswordApi(id: number, newPassword: string) {
  return http.put<void>(`/user/${id}/password`, { newPassword })
}
