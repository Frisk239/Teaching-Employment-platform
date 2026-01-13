/**
 * 角色权限配置
 * 定义不同角色可以访问的菜单和路由
 */

export type RoleCode = 'admin' | 'college_head' | 'teacher' | 'user' | 'enterprise_contact'

export interface RouteMeta {
  title: string
  icon?: string
  roles?: RoleCode[]
  hidden?: boolean
  affix?: boolean
}

export interface RouteConfig {
  path: string
  name: string
  component?: () => Promise<any>
  redirect?: string
  meta?: RouteMeta
  children?: RouteConfig[]
}

/**
 * 角色权限映射
 * 定义每个角色可以访问的路由名称
 */
export const ROLE_PERMISSIONS: Record<RoleCode, string[]> = {
  // 管理员 - 拥有所有权限
  admin: [
    'Dashboard',
    'CompanyManagement',
    'PositionPublishing',
    'PositionManagement',
    'ApplicationManagement',
    'StudentList',
    'StudentImport',
    'CourseList',
    'PopularCourses',
    'Profile',
    'Password',
  ],

  // 学院负责人 - 教学、学生、就业相关
  college_head: [
    'Dashboard',
    'PositionManagement',
    'ApplicationManagement',
    'StudentList',
    'StudentImport',
    'CourseList',
    'PopularCourses',
    'Profile',
    'Password',
  ],

  // 教师 - 课程、学生、就业统计
  teacher: [
    'Dashboard',
    'PositionManagement',
    'ApplicationManagement',
    'StudentList',
    'CourseList',
    'PopularCourses',
    'Profile',
    'Password',
  ],

  // 学生/学员 - 仅课程和自己的就业
  user: [
    'Dashboard',
    'PositionManagement',
    'CourseList',
    'PopularCourses',
    'Profile',
    'Password',
  ],

  // 企业对接人 - 企业和职位管理
  enterprise_contact: [
    'Dashboard',
    'CompanyManagement',
    'PositionPublishing',
    'PositionManagement',
    'ApplicationManagement',
    'Profile',
    'Password',
  ],
}

/**
 * 角色名称映射
 */
export const ROLE_NAMES: Record<RoleCode, string> = {
  admin: '管理员',
  college_head: '学院负责人',
  teacher: '教师',
  user: '学生',
  enterprise_contact: '企业对接人',
}

/**
 * 检查用户是否有权限访问指定路由
 */
export function hasPermission(roleCode: RoleCode, routeName: string): boolean {
  const permissions = ROLE_PERMISSIONS[roleCode]
  return permissions.includes(routeName)
}

/**
 * 过滤路由列表,只返回用户有权限访问的路由
 */
export function filterRoutesByRole(routes: RouteConfig[], roleCode: RoleCode): RouteConfig[] {
  const permissions = ROLE_PERMISSIONS[roleCode]

  return routes
    .filter(route => {
      // 如果路由没有配置角色要求,则所有人可访问
      if (!route.meta?.roles || route.meta.roles.length === 0) {
        return true
      }
      // 检查用户角色是否在允许的角色列表中
      return route.meta.roles.includes(roleCode)
    })
    .map(route => {
      // 递归过滤子路由
      if (route.children && route.children.length > 0) {
        const filteredChildren = filterRoutesByRole(route.children, roleCode)
        return {
          ...route,
          children: filteredChildren,
        }
      }
      return route
    })
    .filter(route => {
      // 如果所有子路由都被过滤掉了,则隐藏父路由
      if (route.children && route.children.length === 0) {
        return false
      }
      return true
    })
}
