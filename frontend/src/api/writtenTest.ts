/**
 * 高校教学就业平台 - 笔试相关 API
 */
import request, { http } from '@/utils/request'

export interface WrittenTest {
  id?: number
  applicationId: number
  positionId: number
  studentId: number
  testUrl?: string
  duration?: number
  startTime?: string
  endTime?: string
  score?: number
  totalScore?: number
  status: 'pending' | 'ongoing' | 'completed' | 'missed'
  comment?: string
  isDeleted?: number
  createTime?: string
  updateTime?: string
  // 关联字段
  studentName?: string
  positionName?: string
  application?: any
  position?: any
  student?: any
}

export interface WrittenTestQueryParams {
  current?: number
  size?: number
  applicationId?: number
  positionId?: number
  studentId?: number
  status?: string
}

export const writtenTestApi = {
  /**
   * 分页查询笔试列表
   */
  getPage: (params: WrittenTestQueryParams) => {
    return http.get<any>('/written-test/page', { params })
  },

  /**
   * 根据ID获取笔试
   */
  getById: (id: number) => {
    return http.get<WrittenTest>(`/written-test/${id}`)
  },

  /**
   * 根据申请ID获取笔试信息
   */
  getByApplicationId: (applicationId: number) => {
    return http.get<WrittenTest>(`/written-test/application/${applicationId}`)
  },

  /**
   * 根据学生ID查询笔试列表
   */
  getByStudentId: (studentId: number, current = 1, size = 10) => {
    return http.get<any>(`/written-test/student/${studentId}`, { params: { current, size } })
  },

  /**
   * 新增笔试
   */
  create: (data: WrittenTest) => {
    return http.post<boolean>('/written-test', data)
  },

  /**
   * 更新笔试
   */
  update: (data: WrittenTest) => {
    return http.put<boolean>('/written-test', data)
  },

  /**
   * 删除笔试
   */
  delete: (id: number) => {
    return http.delete<boolean>(`/written-test/${id}`)
  },

  /**
   * 安排笔试
   */
  schedule: (data: {
    applicationId: number
    testUrl: string
    duration: number
    startTime: string
    endTime: string
  }) => {
    return http.post<boolean>('/written-test/schedule', null, { params: data })
  },

  /**
   * 学生开始考试
   */
  start: (id: number) => {
    return http.post<boolean>(`/written-test/${id}/start`)
  },

  /**
   * 提交笔试成绩
   */
  submitScore: (data: {
    id: number
    score: number
    totalScore: number
    comment?: string
  }) => {
    return http.post<boolean>(`/written-test/${data.id}/submit-score`, null, {
      params: {
        score: data.score,
        totalScore: data.totalScore,
        comment: data.comment
      }
    })
  },

  /**
   * 标记为缺席
   */
  markMissed: (id: number) => {
    return http.post<boolean>(`/written-test/${id}/mark-missed`)
  }
}
