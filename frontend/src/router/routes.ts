/**
 * 高校教学就业平台 - 路由配置
 */
import type { RouteRecordRaw } from 'vue-router'

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
      hidden: true,
    },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false,
      hidden: true,
    },
  },
  {
    path: '/',
    component: () => import('@/components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: {
          title: '仪表盘',
          icon: 'DataLine',
          affix: true,
          roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
        },
      },
      {
        path: '/employment',
        name: 'Employment',
        meta: {
          title: '就业管理',
          icon: 'Briefcase',
          roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
        },
        children: [
          {
            path: '/employment/companies',
            name: 'CompanyManagement',
            component: () => import('@/views/employment/CompanyManagement.vue'),
            meta: {
              title: '企业管理',
              icon: 'OfficeBuilding',
              roles: ['admin', 'enterprise_contact'],
            },
          },
          {
            path: '/employment/publish',
            name: 'PositionPublishing',
            component: () => import('@/views/employment/PositionPublishing.vue'),
            meta: {
              title: '发布职位',
              icon: 'Plus',
              roles: ['admin', 'enterprise_contact'],
            },
          },
          {
            path: '/employment/positions',
            name: 'PositionManagement',
            component: () => import('@/views/employment/PositionManagement.vue'),
            meta: {
              title: '职位管理',
              icon: 'List',
              roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
            },
          },
          {
            path: '/employment/applications',
            name: 'ApplicationManagement',
            component: () => import('@/views/employment/ApplicationManagement.vue'),
            meta: {
              title: '申请管理',
              icon: 'Document',
              roles: ['admin', 'college_head', 'teacher', 'enterprise_contact'],
            },
          },
        ],
      },
      {
        path: '/student',
        name: 'Student',
        meta: {
          title: '学生管理',
          icon: 'User',
          roles: ['admin', 'college_head', 'teacher'],
        },
        children: [
          {
            path: '/student/list',
            name: 'StudentList',
            component: () => import('@/views/student/StudentList.vue'),
            meta: {
              title: '学生列表',
              icon: 'UserFilled',
              roles: ['admin', 'college_head', 'teacher'],
            },
          },
          {
            path: '/student/import',
            name: 'StudentImport',
            component: () => import('@/views/student/StudentImport.vue'),
            meta: {
              title: '导入学生',
              icon: 'Upload',
              roles: ['admin', 'college_head'],
            },
          },
        ],
      },
      {
        path: '/course',
        name: 'Course',
        meta: {
          title: '课程管理',
          icon: 'Reading',
          roles: ['admin', 'college_head', 'teacher', 'user'],
        },
        children: [
          {
            path: '/course/list',
            name: 'CourseList',
            component: () => import('@/views/course/CourseList.vue'),
            meta: {
              title: '课程列表',
              icon: 'Notebook',
              roles: ['admin', 'college_head', 'teacher', 'user'],
            },
          },
          {
            path: '/course/popular',
            name: 'PopularCourses',
            component: () => import('@/views/course/PopularCourses.vue'),
            meta: {
              title: '热门课程',
              icon: 'Star',
              roles: ['admin', 'college_head', 'teacher', 'user'],
            },
          },
        ],
      },
      {
        path: '/system',
        name: 'System',
        meta: {
          title: '系统设置',
          icon: 'Setting',
          roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
        },
        children: [
          {
            path: '/system/profile',
            name: 'Profile',
            component: () => import('@/views/system/Profile.vue'),
            meta: {
              title: '个人资料',
              icon: 'User',
              roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
            },
          },
          {
            path: '/system/password',
            name: 'Password',
            component: () => import('@/views/system/Password.vue'),
            meta: {
              title: '修改密码',
              icon: 'Lock',
              roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
            },
          },
        ],
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: {
      title: '404',
      hidden: true,
    },
  },
]
