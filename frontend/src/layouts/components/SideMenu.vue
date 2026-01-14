<template>
  <el-menu
    :default-active="activeMenu"
    mode="vertical"
    router
    :collapse="isCollapse"
    class="side-menu"
  >
    <!-- 一级菜单 -->
    <el-menu-item
      v-for="menu in firstLevelMenus"
      :key="menu.id"
      :index="menu.path"
    >
      <el-icon v-if="menu.icon">
        <component :is="menu.icon" />
      </el-icon>
      <template #title>{{ menu.name }}</template>
    </el-menu-item>

    <!-- 目录（有子菜单） -->
    <el-sub-menu
      v-for="menu in directories"
      :key="menu.id"
      :index="menu.path"
    >
      <template #title>
        <el-icon v-if="menu.icon">
          <component :is="menu.icon" />
        </el-icon>
        <span>{{ menu.name }}</span>
      </template>

      <!-- 二级菜单 -->
      <el-menu-item
        v-for="child in menu.children"
        :key="child.id"
        :index="child.path"
      >
        <el-icon v-if="child.icon">
          <component :is="child.icon" />
        </el-icon>
        <template #title>{{ child.name }}</template>
      </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores'
import {
  House,
  Setting,
  User,
  Lock,
  Menu,
  School,
  House as HouseIcon,
  Collection,
  Avatar,
  Checked,
  DocumentAdd,
  DocumentCopy,
  OfficeBuilding,
  Management,
  Briefcase,
  Edit,
  DataAnalysis,
  ChatDotRound,
  Bell,
} from '@element-plus/icons-vue'

interface MenuItem {
  id: number
  name: string
  path: string
  icon?: any
  parentId: number
  sort: number
  status: string
  type: string
  children?: MenuItem[]
}

const props = defineProps<{
  isCollapse: boolean
}>()

const route = useRoute()
const authStore = useAuthStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 所有菜单配置
const allMenus: MenuItem[] = [
  // 一级菜单
  {
    id: 1,
    name: '首页',
    path: '/dashboard',
    icon: House,
    parentId: 0,
    sort: 1,
    status: '1',
    type: '2',
  },
  // 系统管理目录
  {
    id: 2,
    name: '系统管理',
    path: '/system',
    icon: Setting,
    parentId: 0,
    sort: 2,
    status: '1',
    type: '1',
  },
  // 用户管理
  {
    id: 3,
    name: '用户管理',
    path: '/system/users',
    icon: User,
    parentId: 2,
    sort: 1,
    status: '1',
    type: '2',
  },
  // 角色管理
  {
    id: 4,
    name: '角色管理',
    path: '/system/roles',
    icon: Management,
    parentId: 2,
    sort: 2,
    status: '1',
    type: '2',
  },
  // 权限管理
  {
    id: 5,
    name: '权限管理',
    path: '/system/permissions',
    icon: Lock,
    parentId: 2,
    sort: 3,
    status: '1',
    type: '2',
  },
  // 菜单管理
  {
    id: 6,
    name: '菜单管理',
    path: '/system/menus',
    icon: Menu,
    parentId: 2,
    sort: 4,
    status: '1',
    type: '2',
  },
  // 教学管理目录
  {
    id: 7,
    name: '教学管理',
    path: '/teaching',
    icon: Edit,
    parentId: 0,
    sort: 3,
    status: '1',
    type: '1',
  },
  // 学校管理
  {
    id: 8,
    name: '学校管理',
    path: '/teaching/schools',
    icon: School,
    parentId: 7,
    sort: 1,
    status: '1',
    type: '2',
  },
  // 教室管理
  {
    id: 9,
    name: '教室管理',
    path: '/teaching/classrooms',
    icon: HouseIcon,
    parentId: 7,
    sort: 2,
    status: '1',
    type: '2',
  },
  // 课程管理
  {
    id: 10,
    name: '课程管理',
    path: '/teaching/courses',
    icon: Collection,
    parentId: 7,
    sort: 3,
    status: '1',
    type: '2',
  },
  // 学员管理
  {
    id: 11,
    name: '学员管理',
    path: '/teaching/students',
    icon: Avatar,
    parentId: 7,
    sort: 4,
    status: '1',
    type: '2',
  },
  // 教师管理
  {
    id: 12,
    name: '教师管理',
    path: '/teaching/teachers',
    icon: User,
    parentId: 7,
    sort: 5,
    status: '1',
    type: '2',
  },
  // 作业批改管理
  {
    id: 13,
    name: '作业批改',
    path: '/teaching/homework-grading',
    icon: Checked,
    parentId: 7,
    sort: 6,
    status: '1',
    type: '2',
  },
  // 作业考试发布
  {
    id: 14,
    name: '作业发布',
    path: '/teaching/homework-publish',
    icon: DocumentAdd,
    parentId: 7,
    sort: 7,
    status: '1',
    type: '2',
  },
  // 日报管理
  {
    id: 15,
    name: '日报管理',
    path: '/teaching/daily-reports',
    icon: DocumentCopy,
    parentId: 7,
    sort: 8,
    status: '1',
    type: '2',
  },
  // 就业管理目录
  {
    id: 16,
    name: '就业管理',
    path: '/employment',
    icon: OfficeBuilding,
    parentId: 0,
    sort: 4,
    status: '1',
    type: '1',
  },
  // 企业管理
  {
    id: 17,
    name: '企业管理',
    path: '/employment/companies',
    icon: Management,
    parentId: 16,
    sort: 1,
    status: '1',
    type: '2',
  },
  // 招聘岗位管理
  {
    id: 18,
    name: '岗位管理',
    path: '/employment/positions',
    icon: Briefcase,
    parentId: 16,
    sort: 2,
    status: '1',
    type: '2',
  },
  // 学员求职管理
  {
    id: 19,
    name: '求职管理',
    path: '/employment/applications',
    icon: User,
    parentId: 16,
    sort: 3,
    status: '1',
    type: '2',
  },
  // 笔试管理
  {
    id: 20,
    name: '笔试管理',
    path: '/employment/written-tests',
    icon: Edit,
    parentId: 16,
    sort: 4,
    status: '1',
    type: '2',
  },
  // 面试管理
  {
    id: 21,
    name: '面试管理',
    path: '/employment/interviews',
    icon: ChatDotRound,
    parentId: 16,
    sort: 5,
    status: '1',
    type: '2',
  },
  // 统计
  {
    id: 22,
    name: '统计',
    path: '/employment/statistics',
    icon: DataAnalysis,
    parentId: 16,
    sort: 6,
    status: '1',
    type: '2',
  },
]

// 角色菜单权限配置
// 定义每个角色可以访问哪些菜单（使用菜单路径）
const roleMenuPermissions: Record<string, string[]> = {
  admin: [
    '/dashboard',
    '/system',
    '/system/users',
    '/system/roles',
    '/system/permissions',
    '/system/menus',
    '/teaching',
    '/teaching/schools',
    '/teaching/classrooms',
    '/teaching/courses',
    '/teaching/students',
    '/teaching/teachers',
    '/teaching/daily-reports',
    '/employment',
    '/employment/companies',
    '/employment/positions',
    '/employment/applications',
    '/employment/written-tests',
    '/employment/interviews',
    '/employment/statistics',
  ],
  college_head: [
    '/dashboard',
    '/teaching',
    '/teaching/schools',
    '/teaching/classrooms',
    '/teaching/courses',
    '/teaching/students',
    '/teaching/teachers',
    '/teaching/homework-grading',
    '/teaching/homework-publish',
    '/teaching/daily-reports',
    '/employment',
    '/employment/statistics',
  ],
  teacher: [
    '/dashboard',
    '/teaching',
    '/teaching/classrooms',
    '/teaching/courses',
    '/teaching/students',
    '/teaching/homework-grading',
    '/teaching/homework-publish',
    '/teaching/daily-reports',
    '/employment',
    '/employment/applications',
    '/employment/interviews',
  ],
  user: [
    '/dashboard',
    '/teaching',
    '/teaching/courses',
    '/teaching/homework-grading',
    '/teaching/daily-reports',
    '/employment',
    '/employment/positions',
    '/employment/applications',
    '/employment/written-tests',
    '/employment/interviews',
  ],
  enterprise_contact: [
    '/dashboard',
    '/employment',
    '/employment/companies',
    '/employment/positions',
    '/employment/applications',
    '/employment/written-tests',
    '/employment/interviews',
    '/employment/statistics',
  ],
}

// 构建菜单树
const treeMenus = computed(() => {
  const userRole = authStore.userRole
  const allowedMenus = roleMenuPermissions[userRole] || []

  const tree: MenuItem[] = []
  const map: Record<number, MenuItem> = {}

  // 先将所有菜单存入map（只包含当前角色有权限的菜单）
  allMenus.forEach((menu) => {
    // 检查菜单是否启用
    if (menu.status !== '1') return

    // 检查当前角色是否有权限访问该菜单
    const hasAccess = allowedMenus.includes(menu.path)

    if (!hasAccess) return

    map[menu.id] = { ...menu, children: [] }
  })

  // 构建树形结构
  allMenus.forEach((menu) => {
    if (!map[menu.id]) return

    if (menu.parentId === 0) {
      tree.push(map[menu.id])
    } else {
      if (map[menu.parentId]) {
        map[menu.parentId].children!.push(map[menu.id])
      }
    }
  })

  // 按sort排序
  tree.forEach((menu) => {
    if (menu.children && menu.children.length > 0) {
      menu.children.sort((a, b) => {
        const sortA = Number(a.sort) || 0
        const sortB = Number(b.sort) || 0
        return sortA - sortB
      })
    }
  })

  return tree
})

// 一级菜单（type=2且parentId=0）
const firstLevelMenus = computed(() => {
  return treeMenus.value.filter((menu) => menu.type === '2')
})

// 目录（type=1）
const directories = computed(() => {
  return treeMenus.value.filter((menu) => menu.type === '1')
})
</script>

<style lang="scss" scoped>
.side-menu {
  height: calc(100vh - 50px);
  overflow-y: auto;
  border-right: none;

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    text-align: left;
  }

  /* 滚动条样式 */
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: #dcdfe6;
    border-radius: 3px;

    &:hover {
      background-color: #c0c4cc;
    }
  }
}
</style>
