/**
 * 题库管理 API
 */
import { http } from '@/utils/request'
import type { IPage } from './types'

export interface QuestionBank {
  id?: number
  questionType?: 'single_choice' | 'multiple_choice' | 'true_false' | 'fill_blank' | 'short_answer'
  knowledgePoint?: string
  difficulty?: 'easy' | 'medium' | 'hard'
  questionText?: string
  options?: string
  correctAnswer?: string
  analysis?: string
  companyId?: number
  status?: string
  createTime?: string
  updateTime?: string
}

export interface QuestionQueryParams {
  current?: number
  size?: number
  questionType?: string | string[] // 题型：支持单个值或数组
  knowledgePoint?: string
  difficulty?: string // 难度：单个值
  keyword?: string // 关键词搜索
  companyId?: number
}

/**
 * 分页查询题库
 */
export function getQuestionPageApi(params: QuestionQueryParams) {
  return http.get<IPage<QuestionBank>>('/question-bank/page', { params })
}

/**
 * 根据ID查询题目
 */
export function getQuestionByIdApi(id: number) {
  return http.get<QuestionBank>(`/question-bank/${id}`)
}

/**
 * 添加题目
 */
export function createQuestionApi(data: QuestionBank) {
  return http.post<void>('/question-bank', data)
}

/**
 * 更新题目
 */
export function updateQuestionApi(data: QuestionBank) {
  return http.put<void>('/question-bank', data)
}

/**
 * 删除题目
 */
export function deleteQuestionApi(id: number) {
  return http.delete<void>(`/question-bank/${id}`)
}

/**
 * 获取所有知识点分类
 */
export function getKnowledgePointsApi() {
  return http.get<string[]>('/question-bank/knowledge-points')
}
