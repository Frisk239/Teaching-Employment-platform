import { http } from '@/utils/request'

/**
 * 实习经历接口类型定义
 */
export interface Internship {
  id?: number
  userId?: number
  companyName: string
  position: string
  department?: string
  startDate: string // YYYY-MM-DD
  endDate?: string // YYYY-MM-DD, 空表示至今
  description?: string
  createTime?: string
  updateTime?: string
}

/**
 * 根据用户ID获取实习经历列表
 */
export function getInternshipByUserIdApi(userId: number) {
  return http.get<Internship[]>(`/internship/user/${userId}`)
}

/**
 * 根据ID获取实习经历详情
 */
export function getInternshipByIdApi(id: number) {
  return http.get<Internship>(`/internship/${id}`)
}

/**
 * 创建实习经历
 */
export function createInternshipApi(data: Internship) {
  return http.post<void>('/internship', data)
}

/**
 * 更新实习经历
 */
export function updateInternshipApi(data: Internship) {
  return http.put<void>('/internship', data)
}

/**
 * 删除实习经历
 */
export function deleteInternshipApi(id: number) {
  return http.delete<void>(`/internship/${id}`)
}
