/**
 * 考试记录 API
 */
import { http } from '@/utils/request'
import type { IPage } from './types'

export interface ExamRecord {
  id?: number
  examId?: number
  studentId?: number
  startTime?: string
  submitTime?: string
  objectiveScore?: number
  subjectiveScore?: number
  totalScore?: number
  passed?: number
  gradingStatus?: 'pending' | 'graded'
  gradingTime?: string
  graderId?: number
  questionsSnapshot?: string
  status?: 'taking' | 'submitted'
  createTime?: string
  updateTime?: string
  studentName?: string
  examName?: string
  graderName?: string
}

export interface ExamRecordQueryParams {
  current?: number
  size?: number
  examId?: number
  studentId?: number
  gradingStatus?: string
  status?: string
}

/**
 * 分页查询考试记录列表
 */
export function getExamRecordPageApi(params: ExamRecordQueryParams) {
  return http.get<IPage<ExamRecord>>('/exam-record/page', { params })
}

/**
 * 根据ID查询考试记录
 */
export function getExamRecordByIdApi(id: number) {
  return http.get<ExamRecord>(`/exam-record/${id}`)
}

/**
 * 开始考试
 */
export function startExamApi(examId: number, studentId: number) {
  return http.post<ExamRecord>('/exam-record/start', null, {
    params: { examId, studentId }
  })
}

/**
 * 提交试卷
 */
export function submitExamApi(id: number) {
  return http.post<void>(`/exam-record/${id}/submit`)
}

/**
 * 自动评阅客观题
 */
export function autoGradeObjectiveQuestionsApi(id: number) {
  return http.post<void>(`/exam-record/${id}/auto-grade`)
}

/**
 * 教师评阅主观题
 */
export function gradeSubjectiveQuestionsApi(id: number, graderId: number) {
  return http.post<void>(`/exam-record/${id}/grade`, null, {
    params: { graderId }
  })
}
