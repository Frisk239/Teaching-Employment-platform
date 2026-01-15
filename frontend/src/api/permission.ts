import request from '@/utils/request'
import type { Result } from './types'

export interface Permission {
  id?: number
  key?: string
  name?: string
  description?: string
  type?: string
  status?: number
  sortOrder?: number
  createTime?: string
  updateTime?: string
}

/**
 * 删除权限
 */
export function deletePermissionApi(key: string) {
  return request<Result<void>>({
    url: `/permission/${key}`,
    method: 'delete'
  })
}
