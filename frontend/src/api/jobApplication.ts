/**
 * 高校教学就业平台 - 求职申请相关 API
 */
import request from '@/utils/request'

// 求职申请接口
export interface JobApplication {
  id?: number
  studentId?: number
  studentName?: string
  positionId?: number
  positionName?: string
  companyId?: number
  companyName?: string
  // 状态: pending-待处理 screened-已筛选 test_passed-笔试通过
  // test_failed-笔试失败 interview_passed-面试通过 interview_failed-面试失败
  // rejected-已拒绝 offered-已发offer hired-已入职 declined-已拒绝offer
  status?: string
  // 当前阶段: resume-简历筛选 test-笔试 interview-面试 offer-offer发放 hired-入职
  currentStage?: string
  resumeId?: number
  applicationTime?: string
  hrRemark?: string
  createTime?: string
  updateTime?: string
}

// 分页查询参数
export interface JobApplicationPageParams {
  current: number
  size: number
  positionId?: number
  studentId?: number
  companyId?: number
  status?: string
  currentStage?: string
}

// 分页结果
export interface JobApplicationPageResult {
  records: JobApplication[]
  total: number
  current: number
  size: number
}

/**
 * 分页查询求职申请列表
 */
export function getJobApplicationPageApi(params: JobApplicationPageParams): Promise<JobApplicationPageResult> {
  return request({
    url: '/job-application/page',
    method: 'get',
    params
  }) as Promise<JobApplicationPageResult>
}

/**
 * 根据ID查询求职申请
 */
export function getJobApplicationByIdApi(id: number) {
  return request({
    url: `/job-application/${id}`,
    method: 'get'
  })
}

/**
 * 更新求职申请状态
 */
export function updateJobApplicationStatusApi(id: number, status: string, remark?: string) {
  return request({
    url: `/job-application/${id}/status`,
    method: 'put',
    data: { status, remark }
  })
}

/**
 * 批量更新求职申请状态
 */
export function batchUpdateJobApplicationStatusApi(ids: number[], status: string) {
  return request({
    url: '/job-application/batch-status',
    method: 'put',
    data: { ids, status }
  })
}

/**
 * 删除求职申请
 */
export function deleteJobApplicationApi(id: number) {
  return request({
    url: `/job-application/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除求职申请
 */
export function batchDeleteJobApplicationApi(ids: number[]) {
  return request({
    url: '/job-application/batch',
    method: 'delete',
    data: { ids }
  })
}

/**
 * 新增求职申请
 */
export function createJobApplicationApi(data: JobApplication) {
  return request({
    url: '/job-application',
    method: 'post',
    data
  })
}

/**
 * 更新求职申请
 */
export function updateJobApplicationApi(data: JobApplication) {
  return request({
    url: '/job-application',
    method: 'put',
    data
  })
}

/**
 * 筛选简历
 */
export function screenResumeApi(id: number, passed: boolean, remark?: string) {
  return request({
    url: `/job-application/${id}/screen`,
    method: 'post',
    params: { passed, remark }
  })
}

/**
 * 更新申请状态和阶段
 */
export function updateApplicationStatusApi(
  id: number,
  status: string,
  currentStage: string
) {
  return request({
    url: `/job-application/${id}/update-status`,
    method: 'post',
    params: { status, currentStage }
  })
}

/**
 * 撤销申请
 */
export function withdrawApplicationApi(id: number, studentId: number) {
  return request({
    url: `/job-application/${id}/withdraw`,
    method: 'post',
    params: { studentId }
  })
}
