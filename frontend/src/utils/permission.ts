/**
 * 权限工具函数
 * 用于在组件中检查权限
 */
import { useAuthStore } from '@/stores'
import { ROLE_PERMISSIONS } from '@/constants/permissions'

/**
 * 检查当前用户是否有某个权限
 * @param permission 权限码
 * @returns 是否有权限
 */
export function hasPermission(permission: string): boolean {
  const authStore = useAuthStore()

  if (!authStore.isLoggedIn) {
    return false
  }

  const userRole = authStore.userRole
  const permissions = ROLE_PERMISSIONS[userRole] || []

  // 管理员拥有所有权限
  if (permissions.includes('*')) {
    return true
  }

  return permissions.includes(permission)
}

/**
 * 检查当前用户是否有某些权限中的任意一个
 * @param permissionList 权限码列表
 * @returns 是否有权限
 */
export function hasAnyPermission(permissionList: string[]): boolean {
  if (!permissionList || permissionList.length === 0) {
    return false
  }

  return permissionList.some((permission) => hasPermission(permission))
}

/**
 * 检查当前用户是否有某些权限的全部
 * @param permissionList 权限码列表
 * @returns 是否有权限
 */
export function hasAllPermissions(permissionList: string[]): boolean {
  if (!permissionList || permissionList.length === 0) {
    return false
  }

  return permissionList.every((permission) => hasPermission(permission))
}

/**
 * 检查当前用户是否是某个角色
 * @param role 角色
 * @returns 是否是该角色
 */
export function hasRole(role: string): boolean {
  const authStore = useAuthStore()
  return authStore.userRole === role
}

/**
 * 检查当前用户是否是某些角色中的任意一个
 * @param roleList 角色列表
 * @returns 是否是这些角色中的任意一个
 */
export function hasAnyRole(roleList: string[]): boolean {
  if (!roleList || roleList.length === 0) {
    return false
  }

  const authStore = useAuthStore()
  return roleList.includes(authStore.userRole)
}

/**
 * 获取当前用户的所有权限
 * @returns 权限列表
 */
export function getCurrentUserPermissions(): string[] {
  const authStore = useAuthStore()

  if (!authStore.isLoggedIn) {
    return []
  }

  const userRole = authStore.userRole
  return ROLE_PERMISSIONS[userRole] || []
}

/**
 * 权限指令类型定义
 */
export interface PermissionDirectiveBinding {
  value: string | string[]
  modifiers?: {
    any?: boolean // 是否满足任意一个权限即可
    all?: boolean // 是否需要满足所有权限
  }
}

/**
 * Vue 权限指令
 * 用法示例：
 * v-permission="'system:user:edit'"
 * v-permission.any="['system:user:edit', 'system:user:view']"
 * v-permission.all="['system:user:edit', 'system:user:delete']"
 */
export const permissionDirective = {
  mounted(el: HTMLElement, binding: PermissionDirectiveBinding) {
    checkPermission(el, binding)
  },
  updated(el: HTMLElement, binding: PermissionDirectiveBinding) {
    checkPermission(el, binding)
  },
}

/**
 * 检查权限并控制元素显示/隐藏
 */
function checkPermission(el: HTMLElement, binding: PermissionDirectiveBinding) {
  const { value, modifiers } = binding

  if (!value) {
    return
  }

  let permitted = false

  if (typeof value === 'string') {
    // 单个权限
    permitted = hasPermission(value)
  } else if (Array.isArray(value)) {
    // 多个权限
    if (modifiers?.any) {
      // 满足任意一个即可
      permitted = hasAnyPermission(value)
    } else if (modifiers?.all) {
      // 需要满足所有
      permitted = hasAllPermissions(value)
    } else {
      // 默认：满足任意一个即可
      permitted = hasAnyPermission(value)
    }
  }

  if (!permitted) {
    // 移除元素
    el.parentNode?.removeChild(el)
  }
}

/**
 * 角色指令类型定义
 */
export interface RoleDirectiveBinding {
  value: string | string[]
}

/**
 * Vue 角色指令
 * 用法示例：
 * v-role="'admin'"
 * v-role="['admin', 'college_head']"
 */
export const roleDirective = {
  mounted(el: HTMLElement, binding: RoleDirectiveBinding) {
    checkRole(el, binding)
  },
  updated(el: HTMLElement, binding: RoleDirectiveBinding) {
    checkRole(el, binding)
  },
}

/**
 * 检查角色并控制元素显示/隐藏
 */
function checkRole(el: HTMLElement, binding: RoleDirectiveBinding) {
  const { value } = binding

  if (!value) {
    return
  }

  let hasRoleAccess = false

  if (typeof value === 'string') {
    // 单个角色
    hasRoleAccess = hasRole(value)
  } else if (Array.isArray(value)) {
    // 多个角色（满足任意一个即可）
    hasRoleAccess = hasAnyRole(value)
  }

  if (!hasRoleAccess) {
    // 移除元素
    el.parentNode?.removeChild(el)
  }
}
