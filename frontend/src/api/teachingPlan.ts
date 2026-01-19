/**
 * 教学计划相关 API
 */
import { http } from '@/utils/request'

export interface TeachingPlan {
  id?: number
  courseId: number
  lessonNumber: number
  weekNumber: number
  title: string
  content?: string
  createTime?: string
  updateTime?: string
}

export interface TeachingPlanVO extends TeachingPlan {
  courseName?: string
  timetable?: {
    weekNumber: number
    dayOfWeek: number
    startPeriod: number
    endPeriod: number
  }
}

export const teachingPlanApi = {
  /**
   * 获取课程教学计划列表
   */
  getList: (courseId: number) => {
    return http.get<TeachingPlan[]>(`/teaching-plan/list/${courseId}`)
  },

  /**
   * 获取课程教学计划详情（含时间）
   */
  getDetail: (courseId: number) => {
    return http.get<TeachingPlanVO[]>(`/teaching-plan/detail/${courseId}`)
  },

  /**
   * 创建教学计划
   */
  create: (data: TeachingPlan) => {
    return http.post<void>('/teaching-plan', data)
  },

  /**
   * 更新教学计划
   */
  update: (data: TeachingPlan) => {
    return http.put<void>('/teaching-plan', data)
  },

  /**
   * 删除教学计划
   */
  delete: (id: number) => {
    return http.delete<void>(`/teaching-plan/${id}`)
  },

  /**
   * 批量保存教学计划
   */
  batchSave: (plans: TeachingPlan[]) => {
    return http.post<void>('/teaching-plan/batch', plans)
  }
}
