import { http } from '@/utils/request'
import type { Result, IPage, Role } from './types'
import type { Permission } from './permission'

export interface RolePageParams {
  current: number
  size: number
  roleCode?: string
  roleName?: string
  status?: number
}

export interface AssignMenusData {
  roleId: number
  menuIds: number[]
}

export interface AssignPermissionsData {
  roleId: number
  permissionIds: number[]
}

/**
 * 获取角色分页列表
 */
export function getRolePageApi(params: RolePageParams) {
  return http.get<IPage<Role>>('/role/page', { params })
}

/**
 * 获取所有角色列表(下拉框)
 */
export function getRoleListApi(status?: number) {
  return http.get<Role[]>('/role/list', { params: { status } })
}

/**
 * 检查角色编码是否存在
 */
export function checkRoleCodeApi(roleCode: string, id?: number) {
  return http.get<boolean>('/role/check-code', { params: { roleCode, id } })
}

/**
 * 根据ID获取角色详情
 */
export function getRoleByIdApi(id: number) {
  return http.get<Role>(`/role/${id}`)
}

/**
 * 创建角色
 */
export function createRoleApi(data: Role) {
  return http.post<void>('/role', data)
}

/**
 * 更新角色
 */
export function updateRoleApi(data: Role) {
  return http.put<void>('/role', data)
}

/**
 * 删除角色
 */
export function deleteRoleApi(id: number) {
  return http.delete<void>(`/role/${id}`)
}

/**
 * 分配菜单权限给角色
 */
export function assignMenusApi(data: AssignMenusData) {
  return http.post<void>('/role/assign-menus', data)
}

/**
 * 分配操作权限给角色
 */
export function assignPermissionsApi(data: AssignPermissionsData) {
  return http.post<void>('/role/assign-permissions', data)
}

/**
 * 获取角色的菜单ID列表
 */
export function getRoleMenuIdsApi(roleId: number) {
  return http.get<number[]>(`/role/menus/${roleId}`)
}

/**
 * 获取角色的权限ID列表
 */
export function getRolePermissionIdsApi(roleId: number) {
  return http.get<number[]>(`/role/permissions/${roleId}`)
}

/**
 * 获取所有权限列表(树形结构)
 */
export function getAllPermissionsApi() {
  return http.get<Permission[]>('/permission/tree')
}

