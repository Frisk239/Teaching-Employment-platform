/**
 * 高校教学就业平台 - API 类型定义
 */

// ============ 用户相关 ============
export interface Role {
  id: number
  roleCode: string
  roleName: string
  description?: string
}

export interface User {
  id?: number
  username: string
  password?: string
  realName?: string
  email?: string
  phone?: string
  roleId?: number
  role?: Role | string
  roleCode?: string
  avatar?: string
  token?: string
  permissions?: string[]
  schoolId?: number
  status?: number
  createTime?: string
  updateTime?: string
}

export interface LoginForm {
  username: string
  password: string
  remember?: boolean
}

export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  realName: string
  email: string
  phone: string
  role?: string
}

// ============ 企业相关 ============
export interface Company {
  id?: number
  name: string
  contact: string
  phone: string
  industry?: string
  scale?: string
  status?: number
  createTime?: string
  updateTime?: string
}

export interface CompanyQueryParams {
  name?: string
  contact?: string
  industry?: string
  page?: number
  pageSize?: number
}

// ============ 招聘职位相关 ============
export interface Position {
  id?: number
  companyId?: number
  companyName?: string
  positionName: string
  positionType: 'fulltime' | 'parttime' | 'internship'
  city: string
  salaryMin?: number
  salaryMax?: number
  salaryUnit?: 'month' | 'year' | 'day' | 'hour'
  education?: 'junior_college' | 'bachelor' | 'master' | 'doctor' | 'unlimited'
  experience?: string
  description?: string
  requirements?: string
  techStack?: string
  capabilityRadar?: string
  recruitCount?: number
  hiredCount?: number
  applicationCount?: number
  status: 'draft' | 'active' | 'paused' | 'closed'
  publishTime?: string
  deadline?: string
  createTime?: string
  updateTime?: string
}

export interface PositionQueryParams {
  current?: number
  size?: number
  companyId?: number
  positionType?: string
  city?: string
  education?: string
  status?: string
  keyword?: string
  salaryMin?: number
  salaryMax?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 保留旧的Recruitment类型以兼容旧代码
export interface Recruitment {
  id?: number
  companyName: string
  position: string
  jobType: string
  workLocation: string
  salaryRange: string
  educationRequirement: string
  workExperience: string
  recruitmentNumber: number
  techStack: string[]
  skills: Record<string, any>
  description?: string
  responsibilities?: string
  requirements?: string
  benefits?: string[]
  recruiterId?: number
  status?: number
  createTime?: string
  updateTime?: string
}

export interface RecruitmentQueryParams {
  companyName?: string
  position?: string
  jobType?: string
  workLocation?: string
  status?: number
  page?: number
  pageSize?: number
}

// ============ 求职申请相关 ============
export interface Application {
  id?: number
  studentName: string
  positionId: number
  companyName: string
  positionName: string
  applicationTime?: string
  resume?: Record<string, any>
  applicationStatus: number
  interviewTime?: string
  interviewResult?: string
  offerStatus?: string
  createTime?: string
  updateTime?: string
}

export interface ApplicationSubmitForm {
  studentName: string
  positionId: number
  companyName: string
  positionName: string
  resume?: Record<string, any>
}

export interface ApplicationQueryParams {
  studentName?: string
  positionName?: string
  companyName?: string
  applicationStatus?: number
  page?: number
  pageSize?: number
}

// ============ 学生相关 ============
export interface Student {
  id?: number
  name: string
  email: string
  phone: string
  avatar?: string
  classId?: number
  className?: string
  status: 'studying' | 'graduated' | 'suspended' | 'dropped'
  employmentStatus: 'unemployed' | 'seeking' | 'employed' | 'further'
  enrollmentYear?: number
  enrollmentTime?: string
  createTime?: string
  updateTime?: string
}

export interface StudentQueryParams {
  name?: string
  email?: string
  className?: string
  status?: string
  employmentStatus?: string
  page?: number
  pageSize?: number
}

// ============ 统计数据相关 ============
export interface OverviewStats {
  totalStudents: number
  totalCompanies: number
  totalPositions: number
  totalApplications: number
  employmentRate: number
  studentsSeeking: number
  companiesActive: number
  positionsOpen: number
}

export interface EmploymentTrendData {
  month: string
  employed: number
  seeking: number
  further: number
}

export interface PopularCourse {
  id: number
  name: string
  students: number
  employmentRate: number
  progress: number
}

export interface Announcement {
  id: number
  title: string
  content: string
  type: 'info' | 'warning' | 'success' | 'error'
  priority: number
  publishTime: string
}

// ============ 分页相关 ============
export interface IPage<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

export interface PageParams {
  page?: number
  pageSize?: number
}

// ============ 通用响应 ============
export interface Result<T = any> {
  code: number
  message: string
  data: T
  success?: boolean
}

// ============ 文件上传 ============
export interface UploadFile {
  name: string
  url: string
  size: number
  type: string
}

export interface UploadResponse {
  url: string
  filename: string
  size: number
}
