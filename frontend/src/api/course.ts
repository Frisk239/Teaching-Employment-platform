import request from '@/utils/request'
import type { Result, IPage } from './types'

// 课程接口类型定义
export interface Course {
  id?: number
  name: string
  code: string
  schoolId: number
  schoolName?: string
  teacherId?: number
  teacherName?: string
  classroomId?: number
  classroomName?: string
  description?: string
  credits?: number
  hours?: number
  capacity?: number
  enrolledCount?: number
  startDate?: string
  endDate?: string
  status?: number
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface CoursePageParams {
  current: number
  size: number
  name?: string
  code?: string
  schoolId?: number
  teacherId?: number
  status?: number
}

/**
 * 获取课程分页列表
 */
export function getCoursePageApi(params: CoursePageParams) {
  return request<Result<IPage<Course>>>({
    url: '/course/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有课程列表
 */
export function getCourseListApi() {
  return request<Result<Course[]>>({
    url: '/course/list',
    method: 'get'
  })
}

/**
 * 根据ID获取课程详情
 */
export function getCourseByIdApi(id: number) {
  return request<Result<Course>>({
    url: `/course/${id}`,
    method: 'get'
  })
}

/**
 * 创建课程
 */
export function createCourseApi(data: Course) {
  return request<Result<void>>({
    url: '/course',
    method: 'post',
    data
  })
}

/**
 * 更新课程
 */
export function updateCourseApi(data: Course) {
  return request<Result<void>>({
    url: '/course',
    method: 'put',
    data
  })
}

/**
 * 删除课程
 */
export function deleteCourseApi(id: number) {
  return request<Result<void>>({
    url: `/course/${id}`,
    method: 'delete'
  })
}

/**
 * 学生选课
 */
export function enrollCourseApi(courseId: number, studentId: number) {
  return request<Result<void>>({
    url: `/course/enroll`,
    method: 'post',
    data: { courseId, studentId }
  })
}

/**
 * 学生退课
 */
export function dropCourseApi(courseId: number, studentId: number) {
  return request<Result<void>>({
    url: `/course/drop`,
    method: 'post',
    data: { courseId, studentId }
  })
}

/**
 * 获取课程学生列表
 */
export function getCourseStudentsApi(courseId: number) {
  return request<Result<any[]>>({
    url: `/course/students/${courseId}`,
    method: 'get'
  })
}
