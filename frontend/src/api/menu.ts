import request from '@/utils/request'
import type { Result } from './types'

export interface Menu {
  id?: number
  parentId?: number
  name?: string
  path?: string
  type?: string
  icon?: string
  sortOrder?: number
  isVisible?: number
  status?: number
  createTime?: string
  updateTime?: string
}

/**
 * 删除菜单
 */
export function deleteMenuApi(id: number) {
  return request<Result<void>>({
    url: `/menu/${id}`,
    method: 'delete'
  })
}
