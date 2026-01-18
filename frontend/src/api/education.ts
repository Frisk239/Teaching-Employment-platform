import { http } from '@/utils/request'

/**
 * 教育经历接口类型定义
 */
export interface Education {
  id?: number
  userId?: number
  schoolName: string
  major: string
  degree: string // 本科/硕士/博士/专科
  startDate: string // YYYY-MM-DD
  endDate?: string // YYYY-MM-DD, 空表示至今
  description?: string
  createTime?: string
  updateTime?: string
}

/**
 * 根据用户ID获取教育经历列表
 */
export function getEducationByUserIdApi(userId: number) {
  return http.get<Education[]>(`/education/user/${userId}`)
}

/**
 * 根据ID获取教育经历详情
 */
export function getEducationByIdApi(id: number) {
  return http.get<Education>(`/education/${id}`)
}

/**
 * 创建教育经历
 */
export function createEducationApi(data: Education) {
  return http.post<void>('/education', data)
}

/**
 * 更新教育经历
 */
export function updateEducationApi(data: Education) {
  return http.put<void>('/education', data)
}

/**
 * 删除教育经历
 */
export function deleteEducationApi(id: number) {
  return http.delete<void>(`/education/${id}`)
}
