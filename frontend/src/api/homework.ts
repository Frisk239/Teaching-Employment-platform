import request from '@/utils/request'
import type { Result, IPage } from './types'

// 作业接口类型定义
export interface Homework {
  id?: number
  title: string
  courseId: number
  courseName?: string
  teacherId: number
  teacherName?: string
  description?: string
  dueDate?: string
  maxScore?: number
  status?: number
  createTime?: string
  updateTime?: string
}

// 作业提交接口类型定义
export interface HomeworkSubmission {
  id?: number
  homeworkId: number
  studentId: number
  studentName?: string
  content?: string
  fileUrl?: string
  score?: number
  comment?: string
  submitDate?: string
  gradeDate?: string
  status?: string // 'DRAFT', 'SUBMITTED', 'GRADED'
}

// 分页查询参数
export interface HomeworkPageParams {
  current: number
  size: number
  title?: string
  courseId?: number
  teacherId?: number
  status?: number
}

/**
 * 获取作业分页列表
 */
export function getHomeworkPageApi(params: HomeworkPageParams) {
  return request<Result<IPage<Homework>>>({
    url: '/homework/page',
    method: 'get',
    params
  })
}

/**
 * 获取作业详情
 */
export function getHomeworkByIdApi(id: number) {
  return request<Result<Homework>>({
    url: `/homework/${id}`,
    method: 'get'
  })
}

/**
 * 创建作业
 */
export function createHomeworkApi(data: Homework) {
  return request<Result<void>>({
    url: '/homework',
    method: 'post',
    data
  })
}

/**
 * 更新作业
 */
export function updateHomeworkApi(data: Homework) {
  return request<Result<void>>({
    url: '/homework',
    method: 'put',
    data
  })
}

/**
 * 删除作业
 */
export function deleteHomeworkApi(id: number) {
  return request<Result<void>>({
    url: `/homework/${id}`,
    method: 'delete'
  })
}

/**
 * 获取作业提交列表
 */
export function getHomeworkSubmissionsApi(homeworkId: number) {
  return request<Result<HomeworkSubmission[]>>({
    url: `/homework/${homeworkId}/submissions`,
    method: 'get'
  })
}

/**
 * 批改作业
 */
export function gradeHomeworkApi(submissionId: number, score: number, comment: string) {
  return request<Result<void>>({
    url: '/homework/grade',
    method: 'post',
    data: { submissionId, score, comment }
  })
}

/**
 * 提交作业
 */
export function submitHomeworkApi(homeworkId: number, content: string, fileUrl?: string) {
  return request<Result<void>>({
    url: '/homework/submit',
    method: 'post',
    data: { homeworkId, content, fileUrl }
  })
}
