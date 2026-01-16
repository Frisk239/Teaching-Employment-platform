/**
 * 高校教学就业平台 - 路由配置
 *
 * 路由结构：
 * - 登录/注册页面（不需要布局）
 * - 主应用（使用 MainLayout）
 *   - 系统管理（仅管理员）
 *   - 教学管理（多角色共享）
 *   - 就业管理（多角色共享）
 *   - 个人中心（所有角色）
 */
import type { RouteRecordRaw } from 'vue-router'

export const routes: RouteRecordRaw[] = [
  // ==================== 公共页面（不需要登录） ====================
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

  // ==================== 主应用（需要登录和布局） ====================
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: {
      requiresAuth: true,
    },
    children: [
      // ---------- 首页 ----------
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: {
          title: '首页',
          icon: 'House',
          affix: true,
          roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
        },
      },

      // ==================== 系统管理（仅管理员） ====================
      {
        path: '/system',
        name: 'System',
        redirect: '/system/users',
        meta: {
          title: '系统管理',
          icon: 'Setting',
          roles: ['admin'],
        },
        children: [
          {
            path: '/system/users',
            name: 'SystemUsers',
            component: () => import('@/views/system/Users.vue'),
            meta: {
              title: '用户管理',
              icon: 'User',
              roles: ['admin'],
            },
          },
          {
            path: '/system/roles',
            name: 'SystemRoles',
            component: () => import('@/views/system/Roles.vue'),
            meta: {
              title: '角色管理',
              icon: 'Management',
              roles: ['admin'],
            },
          },
          {
            path: '/system/permissions',
            name: 'SystemPermissions',
            component: () => import('@/views/system/Permissions.vue'),
            meta: {
              title: '权限管理',
              icon: 'Lock',
              roles: ['admin'],
            },
          },
          {
            path: '/system/menus',
            name: 'SystemMenus',
            component: () => import('@/views/system/Menus.vue'),
            meta: {
              title: '菜单管理',
              icon: 'Menu',
              roles: ['admin'],
            },
          },
        ],
      },

      // ==================== 教学管理（多角色共享） ====================
      {
        path: '/teaching',
        name: 'Teaching',
        redirect: '/teaching/schools',
        meta: {
          title: '教学管理',
          icon: 'Edit',
          roles: ['admin', 'college_head', 'teacher', 'user'],
        },
        children: [
          // 学校管理（管理员、学院负责人）
          {
            path: '/teaching/schools',
            name: 'TeachingSchools',
            component: () => import('@/views/teaching/SchoolList.vue'),
            meta: {
              title: '学校管理',
              icon: 'School',
              roles: ['admin', 'college_head'],
            },
          },
          // 教室管理（管理员、学院负责人、教师）
          {
            path: '/teaching/classrooms',
            name: 'TeachingClassrooms',
            component: () => import('@/views/teaching/ClassroomList.vue'),
            meta: {
              title: '教室管理',
              icon: 'House',
              roles: ['admin', 'college_head', 'teacher'],
            },
          },
          // 课程管理（所有教学角色）
          {
            path: '/teaching/courses',
            name: 'TeachingCourses',
            component: () => import('@/views/teaching/CourseList.vue'),
            meta: {
              title: '课程管理',
              icon: 'Collection',
              roles: ['admin', 'college_head', 'teacher', 'user'],
            },
          },
          // 学员管理（管理员、学院负责人、教师）
          {
            path: '/teaching/students',
            name: 'TeachingStudents',
            component: () => import('@/views/teaching/StudentList.vue'),
            meta: {
              title: '学员管理',
              icon: 'Avatar',
              roles: ['admin', 'college_head', 'teacher'],
            },
          },
          // 教师管理（管理员、学院负责人）
          {
            path: '/teaching/teachers',
            name: 'TeachingTeachers',
            component: () => import('@/views/teaching/TeacherList.vue'),
            meta: {
              title: '教师管理',
              icon: 'User',
              roles: ['admin', 'college_head'],
            },
          },
          // 作业发布（学院负责人、教师）
          {
            path: '/teaching/homework-publish',
            name: 'TeachingHomeworkPublish',
            component: () => import('@/views/teaching/HomeworkManagement.vue'),
            meta: {
              title: '作业发布',
              icon: 'DocumentAdd',
              roles: ['college_head', 'teacher'],
            },
          },
          // 日报管理（管理员、学院负责人、教师）
          {
            path: '/teaching/daily-reports',
            name: 'TeachingDailyReports',
            component: () => import('@/views/teaching/DailyReports.vue'),
            meta: {
              title: '日报管理',
              icon: 'DocumentCopy',
              roles: ['admin', 'college_head', 'teacher'],
            },
          },
          // 我的日报（学员专用）
          {
            path: '/teaching/my-daily-reports',
            name: 'TeachingMyDailyReports',
            component: () => import('@/views/teaching/MyDailyReports.vue'),
            meta: {
              title: '我的日报',
              icon: 'Document',
              roles: ['user'],
            },
          },
          // 我的作业（学员专用）
          {
            path: '/teaching/assignments',
            name: 'TeachingAssignments',
            component: () => import('@/views/teaching/MyHomework.vue'),
            meta: {
              title: '我的作业',
              icon: 'DocumentAdd',
              roles: ['user'],
            },
          },
          // 我的课程（学员专用 - 课程表）
          {
            path: '/teaching/my-courses',
            name: 'TeachingMyCourses',
            component: () => import('@/views/teaching/MyTimetable.vue'),
            meta: {
              title: '我的课程',
              icon: 'Reading',
              roles: ['user'],
            },
          },
          // 课程列表（学员专用）
          {
            path: '/teaching/course-list',
            name: 'TeachingCourseList',
            component: () => import('@/views/course/MyCourses.vue'),
            meta: {
              title: '课程列表',
              icon: 'List',
              roles: ['user'],
              hidden: true,
            },
          },
        ],
      },

      // ==================== 就业管理（多角色共享） ====================
      {
        path: '/employment',
        name: 'Employment',
        redirect: '/employment/companies',
        meta: {
          title: '就业管理',
          icon: 'OfficeBuilding',
          roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
        },
        children: [
          // 企业管理（管理员、企业对接人）
          {
            path: '/employment/companies',
            name: 'EmploymentCompanies',
            component: () => import('@/views/employment/CompanyManagement.vue'),
            meta: {
              title: '企业管理',
              icon: 'Management',
              roles: ['admin', 'enterprise_contact'],
            },
          },
          // 岗位管理（管理员）
          {
            path: '/employment/position-management',
            name: 'EmploymentPositionManagement',
            component: () => import('@/views/employment/PositionManagement.vue'),
            meta: {
              title: '岗位管理',
              icon: 'Briefcase',
              roles: ['admin'],
            },
          },
          // 岗位列表（所有就业相关角色）
          {
            path: '/employment/positions',
            name: 'EmploymentPositions',
            component: () => import('@/views/employment/PositionList.vue'),
            meta: {
              title: '岗位列表',
              icon: 'Briefcase',
              roles: ['admin', 'college_head', 'user', 'enterprise_contact'],
            },
          },
          // 求职管理（管理员、学院负责人、企业对接人、学员）
          {
            path: '/employment/applications',
            name: 'EmploymentApplications',
            component: () => import('@/views/employment/ApplicationList.vue'),
            meta: {
              title: '求职管理',
              icon: 'User',
              roles: ['admin', 'college_head', 'user', 'enterprise_contact'],
            },
          },
          // 笔试管理（管理员、企业对接人）
          {
            path: '/employment/written-tests',
            name: 'EmploymentWrittenTests',
            component: () => import('@/views/employment/WrittenTestManagement.vue'),
            meta: {
              title: '笔试管理',
              icon: 'Edit',
              roles: ['admin', 'college_head', 'enterprise_contact'],
            },
          },
          // 面试管理（管理员、学院负责人、企业对接人、学员）
          {
            path: '/employment/interviews',
            name: 'EmploymentInterviews',
            component: () => import('@/views/employment/InterviewManagement.vue'),
            meta: {
              title: '面试管理',
              icon: 'ChatDotRound',
              roles: ['admin', 'college_head', 'enterprise_contact', 'user'],
            },
          },
          // 我的Offer（学员专用）
          {
            path: '/employment/offers',
            name: 'EmploymentOffers',
            component: () => import('@/views/employment/MyOffers.vue'),
            meta: {
              title: '我的Offer',
              icon: 'Checked',
              roles: ['user'],
            },
          },
        ],
      },

      // ==================== 个人中心（所有角色） ====================
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: {
          title: '个人中心',
          icon: 'User',
          hidden: true, // 不在侧边栏显示
          roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
        },
      },
      {
        path: '/settings',
        name: 'Settings',
        component: () => import('@/views/profile/Settings.vue'),
        meta: {
          title: '设置',
          icon: 'Setting',
          hidden: true,
          roles: ['admin', 'college_head', 'teacher', 'user', 'enterprise_contact'],
        },
      },
    ],
  },

  // ==================== 404页面 ====================
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
