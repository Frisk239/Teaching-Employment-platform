/**
 * 高校教学就业平台 - 面试相关 API
 */
import request, { http } from '@/utils/request'

export interface Interview {
  id?: number
  applicationId: number
  positionId: number
  studentId: number
  round?: number
  interviewType: 'online' | 'offline' | 'phone'
  interviewTime?: string
  location?: string
  meetingLink?: string
  interviewer?: string
  interviewerContact?: string
  result: 'pending' | 'passed' | 'failed' | 'cancelled'
  score?: number
  comment?: string
  status: 'scheduled' | 'completed' | 'cancelled'
  isDeleted?: number
  createTime?: string
  updateTime?: string
  // 关联字段
  studentName?: string
  positionName?: string
  companyName?: string
  application?: any
  position?: any
  student?: any
}

export interface InterviewQueryParams {
  current?: number
  size?: number
  applicationId?: number
  positionId?: number
  studentId?: number
  status?: string
}

export const interviewApi = {
  /**
   * 分页查询面试列表
   */
  getPage: (params: InterviewQueryParams) => {
    return http.get<any>('/interview/page', { params })
  },

  /**
   * 根据ID获取面试
   */
  getById: (id: number) => {
    return http.get<Interview>(`/interview/${id}`)
  },

  /**
   * 根据学生ID查询面试列表
   */
  getByStudentId: (studentId: number, current = 1, size = 10) => {
    return http.get<any>(`/interview/student/${studentId}`, { params: { current, size } })
  },

  /**
   * 获取学生即将到来的面试
   */
  getUpcoming: (studentId: number) => {
    return http.get<Interview[]>(`/interview/student/${studentId}/upcoming`)
  },

  /**
   * 新增面试
   */
  create: (data: Interview) => {
    return http.post<boolean>('/interview', data)
  },

  /**
   * 更新面试
   */
  update: (data: Interview) => {
    return http.put<boolean>('/interview', data)
  },

  /**
   * 删除面试
   */
  delete: (id: number) => {
    return http.delete<boolean>(`/interview/${id}`)
  },

  /**
   * 安排面试
   */
  schedule: (data: {
    applicationId: number
    round: number
    interviewType: string
    interviewTime: string
    location?: string
    meetingLink?: string
    interviewer: string
    interviewerContact?: string
  }) => {
    return http.post<boolean>('/interview/schedule', null, { params: data })
  },

  /**
   * 取消面试
   */
  cancel: (id: number) => {
    return http.post<boolean>(`/interview/${id}/cancel`)
  },

  /**
   * 提交面试结果
   */
  submitResult: (data: {
    id: number
    result: string
    score?: number
    comment?: string
  }) => {
    return http.post<boolean>(`/interview/${data.id}/submit-result`, null, {
      params: {
        result: data.result,
        score: data.score,
        comment: data.comment
      }
    })
  },

  /**
   * 重新安排面试
   */
  reschedule: (data: {
    id: number
    interviewTime: string
    location?: string
    meetingLink?: string
  }) => {
    return http.post<boolean>(`/interview/${data.id}/reschedule`, null, {
      params: {
        interviewTime: data.interviewTime,
        location: data.location,
        meetingLink: data.meetingLink
      }
    })
  }
}
