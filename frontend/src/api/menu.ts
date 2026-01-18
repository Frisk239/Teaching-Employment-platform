import { http } from '@/utils/request'
import type { Result } from './types'

export interface Menu {
  id?: number
  parentId?: number
  menuName?: string
  path?: string
  menuType?: string
  permission?: string
  icon?: string
  sortOrder?: number
  isVisible?: number
  status?: number
  createTime?: string
  updateTime?: string
  children?: Menu[]
}

export interface AssignMenusData {
  roleId: number
  menuIds: number[]
}

/**
 * 查询所有菜单(树形结构)
 */
export function getMenuTreeApi() {
  return http.get<Menu[]>('/menu/tree')
}

/**
 * 根据角色ID查询菜单列表
 */
export function getMenusByRoleIdApi(roleId: number) {
  return http.get<Menu[]>(`/menu/role/${roleId}`)
}

/**
 * 根据父ID查询子菜单
 */
export function getMenusByParentIdApi(parentId: number) {
  return http.get<Menu[]>(`/menu/parent/${parentId}`)
}

/**
 * 根据ID查询菜单
 */
export function getMenuByIdApi(id: number) {
  return http.get<Menu>(`/menu/${id}`)
}

/**
 * 创建菜单
 */
export function createMenuApi(data: Menu) {
  return http.post<void>('/menu', data)
}

/**
 * 更新菜单
 */
export function updateMenuApi(data: Menu) {
  return http.put<void>('/menu', data)
}

/**
 * 删除菜单
 */
export function deleteMenuApi(id: number) {
  return http.delete<void>(`/menu/${id}`)
}

/**
 * 为角色分配菜单
 */
export function assignMenusApi(data: AssignMenusData) {
  return http.post<void>('/menu/assign', data)
}

/**
 * 移除角色菜单关联
 */
export function removeMenusApi(data: AssignMenusData) {
  return http.post<void>('/menu/remove', data)
}
