/**
 * 高校教学就业平台 - 认证相关 API
 */
import request, { http } from '@/utils/request'
import type { LoginForm, RegisterForm, User, Result } from './types'

// 登录响应类型
export interface LoginResponse {
  token: string
  user: User
  roleCode: string
  studentId?: number  // 学员ID,仅学员角色有此字段
}

export const authApi = {
  /**
   * 用户登录
   */
  login: (data: LoginForm) => {
    return http.post<LoginResponse>('/auth/login', data)
  },

  /**
   * 用户注册
   */
  register: (data: RegisterForm) => {
    return http.post<User>('/auth/register', data)
  },

  /**
   * 检查用户名是否存在
   */
  checkUsername: (username: string) => {
    return http.get<{ exists: boolean }>('/auth/check-username', {
      params: { username },
    })
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser: () => {
    return http.get<{ user: User; roleCode: string; studentId?: number }>('/auth/current')
  },

  /**
   * 退出登录
   */
  logout: () => {
    return http.post('/auth/logout')
  },

  /**
   * 修改密码
   */
  changePassword: (data: { oldPassword: string; newPassword: string }) => {
    return http.put('/user/password', data)
  },

  /**
   * 更新个人资料
   */
  updateProfile: (data: { realName?: string; phone?: string; email?: string }) => {
    return http.put('/user/profile', data)
  },

  /**
   * 更新学员个人信息（学号、简历等）
   */
  updateStudentProfile: (data: {
    studentNumber?: string
    resumeUrl?: string
    realName?: string
    phone?: string
    email?: string
  }) => {
    return http.put('/auth/update-student-profile', data)
  },

  /**
   * 刷新 Token
   */
  refreshToken: (refreshToken: string) => {
    return http.post<{ token: string; refreshToken: string }>('/auth/refresh', {
      refreshToken,
    })
  },
}
