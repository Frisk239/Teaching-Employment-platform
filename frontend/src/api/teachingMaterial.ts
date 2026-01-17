/**
 * 高校教学就业平台 - 教学资料相关 API
 */
import request, { http } from '@/utils/request'

export interface TeachingMaterial {
  id?: number
  materialName: string
  materialType: string
  fileUrl: string
  fileSize?: number
  courseId?: number
  teacherId: number
  category?: string
  tags?: string
  description?: string
  isPublic?: number
  downloadCount?: number
  viewCount?: number
  status?: string
  createTime?: string
  updateTime?: string
  // 关联字段
  courseName?: string
  teacherName?: string
  fileExtension?: string
}

export interface TeachingMaterialQueryParams {
  current?: number
  size?: number
  teacherId?: number
  courseId?: number
  materialType?: string
  category?: string
  keyword?: string
}

export const teachingMaterialApi = {
  /**
   * 分页查询教学资料列表
   */
  getPage: (params: TeachingMaterialQueryParams) => {
    return http.get<any>('/teaching-material/page', { params })
  },

  /**
   * 根据ID获取教学资料详情
   */
  getById: (id: number) => {
    return http.get<TeachingMaterial>(`/teaching-material/${id}`)
  },

  /**
   * 上传教学资料
   */
  upload: (data: FormData) => {
    return http.post<any>('/teaching-material/upload', data)
  },

  /**
   * 更新教学资料信息
   */
  update: (data: TeachingMaterial) => {
    return http.put<boolean>('/teaching-material', data)
  },

  /**
   * 删除教学资料
   */
  delete: (id: number) => {
    return http.delete<boolean>(`/teaching-material/${id}`)
  },

  /**
   * 批量删除教学资料
   */
  batchDelete: (ids: number[]) => {
    return http.delete<boolean>('/teaching-material/batch', { data: ids })
  },

  /**
   * 记录下载
   */
  recordDownload: (id: number) => {
    return http.post<boolean>(`/teaching-material/${id}/download`)
  },

  /**
   * 记录查看
   */
  recordView: (id: number) => {
    return http.post<boolean>(`/teaching-material/${id}/view`)
  }
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

/**
 * 获取资料类型图标
 */
export function getMaterialTypeIcon(type: string): string {
  const iconMap: Record<string, string> = {
    'ppt': '📊',
    'video': '🎥',
    'document': '📄',
    'image': '🖼️',
    'other': '📁'
  }
  return iconMap[type] || iconMap['other']
}

/**
 * 获取资料类型标签
 */
export function getMaterialTypeLabel(type: string): string {
  const labelMap: Record<string, string> = {
    'ppt': 'PPT课件',
    'video': '视频资料',
    'document': '文档资料',
    'image': '图片资料',
    'other': '其他资料'
  }
  return labelMap[type] || '其他资料'
}
