/**
 * 学生答案 API
 */
import { http } from '@/utils/request'
import type { IPage } from './types'

export interface StudentAnswer {
  id?: number
  examRecordId?: number
  questionId?: number
  questionType?: 'single_choice' | 'multiple_choice' | 'true_false' | 'short_answer' | 'fill_blank'
  questionText?: string
  options?: string
  studentAnswer?: string
  correctAnswer?: string
  score?: number
  fullScore?: number
  comment?: string
  isCorrect?: boolean
  graded?: boolean
  createTime?: string
  updateTime?: string
}

export interface StudentAnswerQueryParams {
  current?: number
  size?: number
  examRecordId?: number
  questionId?: number
  questionType?: string
  graded?: boolean
}

/**
 * 分页查询学生答案列表
 */
export function getStudentAnswerPageApi(params: StudentAnswerQueryParams) {
  return http.get<IPage<StudentAnswer>>('/student-answer/page', { params })
}

/**
 * 根据考试记录ID获取学生答案列表
 */
export function getStudentAnswersByExamRecordApi(examRecordId: number) {
  return http.get<StudentAnswer[]>(`/student-answer/exam-record/${examRecordId}`)
}

/**
 * 根据ID查询学生答案
 */
export function getStudentAnswerByIdApi(id: number) {
  return http.get<StudentAnswer>(`/student-answer/${id}`)
}

/**
 * 更新学生答案（评分和批语）
 */
export function updateStudentAnswerApi(data: StudentAnswer) {
  return http.put<void>('/student-answer', data)
}

/**
 * 批量更新学生答案
 */
export function batchUpdateStudentAnswersApi(answers: StudentAnswer[]) {
  return http.put<void>('/student-answer/batch', answers)
}
