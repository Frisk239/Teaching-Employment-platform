/**
 * 试卷管理 API
 */
import { http } from '@/utils/request'
import type { IPage } from './types'

export interface Exam {
  id?: number
  examName?: string
  examType?: 'course' | 'company'
  refId?: number
  duration?: number
  startTime?: string
  endTime?: string
  passScore?: number
  totalScore?: number
  shuffleQuestions?: number
  shuffleOptions?: number
  status?: 'draft' | 'published' | 'ended'
  createTime?: string
  updateTime?: string
  refName?: string
}

export interface ExamQueryParams {
  current?: number
  size?: number
  examType?: string
  refId?: number
  status?: string
  keyword?: string
}

/**
 * 分页查询试卷列表
 */
export function getExamPageApi(params: ExamQueryParams) {
  return http.get<IPage<Exam>>('/exam/page', { params })
}

/**
 * 根据ID查询试卷
 */
export function getExamByIdApi(id: number) {
  return http.get<Exam>(`/exam/${id}`)
}

/**
 * 新增试卷
 */
export function createExamApi(data: Exam) {
  return http.post<void>('/exam', data)
}

/**
 * 更新试卷
 */
export function updateExamApi(data: Exam) {
  return http.put<void>('/exam', data)
}

/**
 * 删除试卷
 */
export function deleteExamApi(id: number) {
  return http.delete<void>(`/exam/${id}`)
}

/**
 * 根据关联ID查询试卷列表
 */
export function getExamsByRefIdApi(examType: string, refId: number) {
  return http.get<Exam[]>(`/exam/ref/${examType}/${refId}`)
}

/**
 * 查询已发布的试卷列表
 */
export function getPublishedExamsApi() {
  return http.get<Exam[]>('/exam/published')
}

/**
 * 查询进行中的试卷列表
 */
export function getOngoingExamsApi() {
  return http.get<Exam[]>('/exam/ongoing')
}

/**
 * 发布试卷
 */
export function publishExamApi(id: number) {
  return http.post<void>(`/exam/${id}/publish`)
}

/**
 * 结束考试
 */
export function endExamApi(id: number) {
  return http.post<void>(`/exam/${id}/end`)
}
