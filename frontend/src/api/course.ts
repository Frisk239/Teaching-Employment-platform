import { http } from '@/utils/request'
import type { IPage } from './types'

// 课程接口类型定义
export interface Course {
  id?: number
  courseName?: string
  courseCode?: string
  courseType?: string
  schoolId?: number
  teacherId?: number
  classroomId?: number
  description?: string
  credit?: number
  totalHours?: number
  startDate?: string
  endDate?: string
  maxStudents?: number
  currentStudents?: number
  status?: string
  createTime?: string
  updateTime?: string
  // 关联查询字段
  school?: {
    id: number
    schoolName: string
  }
  teacher?: {
    id: number
    teacherName: string
  }
  classroom?: {
    id: number
    classroomName: string
  }
  schoolName?: string
  teacherName?: string
  classroomName?: string
}

// 分页查询参数
export interface CoursePageParams {
  current: number
  size: number
  schoolId?: number
  teacherId?: number
  status?: string
  keyword?: string
}

/**
 * 获取课程分页列表
 */
export function getCoursePageApi(params: CoursePageParams) {
  return http.get<IPage<Course>>('/course/page', { params })
}

/**
 * 获取所有课程列表
 */
export function getCourseListApi() {
  return http.get<Course[]>('/course/list')
}

/**
 * 根据ID获取课程详情
 */
export function getCourseByIdApi(id: number) {
  return http.get<Course>(`/course/${id}`)
}

/**
 * 创建课程
 */
export function createCourseApi(data: Course) {
  return http.post<void>('/course', data)
}

/**
 * 更新课程
 */
export function updateCourseApi(data: Course) {
  return http.put<void>('/course', data)
}

/**
 * 删除课程
 */
export function deleteCourseApi(id: number) {
  return http.delete<void>(`/course/${id}`)
}

/**
 * 查询学生的课程表
 */
export function getCoursesByStudentApi(studentId: number) {
  return http.get<Course[]>(`/course/student/${studentId}`)
}

/**
 * 查询教师的课程列表
 */
export function getCoursesByTeacherApi(teacherId: number) {
  return http.get<Course[]>(`/course/teacher/${teacherId}`)
}

/**
 * 添加学员到课程
 */
export function addStudentToCourseApi(courseId: number, studentId: number) {
  return http.post<void>(`/course/${courseId}/student`, { studentId })
}

/**
 * 批量添加学员到课程
 */
export function batchAddStudentsToCourseApi(courseId: number, studentIds: number[]) {
  return http.post<void>(`/course/${courseId}/students/batch`, { studentIds })
}

/**
 * 从课程中移除学员
 */
export function removeStudentFromCourseApi(courseId: number, studentId: number) {
  return http.delete<void>(`/course/${courseId}/student/${studentId}`)
}

/**
 * 为课程分配教师
 */
export function assignTeacherToCourseApi(courseId: number, teacherId: number) {
  return http.put<void>(`/course/${courseId}/teacher`, { teacherId })
}
