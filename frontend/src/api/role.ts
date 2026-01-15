import request from '@/utils/request'
import type { Result } from './types'

export interface Role {
  id?: number
  name: string
  code: string
  description?: string
  status?: number
  createTime?: string
  updateTime?: string
}

/**
 * 删除角色
 */
export function deleteRoleApi(id: number) {
  return request<Result<void>>({
    url: `/role/${id}`,
    method: 'delete'
  })
}
