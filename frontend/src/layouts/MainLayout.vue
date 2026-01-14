<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="layout-aside">
      <!-- 折叠按钮 -->
      <div class="toggle-btn" @click="toggleCollapse">
        <el-icon :size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- 菜单 -->
      <SideMenu :is-collapse="isCollapse" />
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <h2>高校教学就业平台</h2>
          <el-tag v-if="authStore.user" type="info" size="small">
            {{ getRoleText(authStore.userRole) }}
          </el-tag>
        </div>
        <div class="header-right">
          <!-- 通知 -->
          <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge">
            <el-button :icon="Bell" circle @click="handleNotification" />
          </el-badge>

          <!-- 用户下拉菜单 -->
          <el-dropdown class="user-dropdown" @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="authStore.userAvatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="username">{{ authStore.userName || '用户' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Fold,
  Expand,
  Bell,
  UserFilled,
  User,
  Setting,
  SwitchButton,
  ArrowDown,
} from '@element-plus/icons-vue'
import SideMenu from './components/SideMenu.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 侧边栏折叠状态
const isCollapse = ref(false)
const notificationCount = ref(0)

// 切换折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 获取角色文本
const getRoleText = (role: string) => {
  const roleMap: Record<string, string> = {
    admin: '管理员',
    college_head: '学院负责人',
    teacher: '教师',
    user: '学员',
    enterprise_contact: '企业对接人',
  }
  return roleMap[role] || '用户'
}

// 通知点击
const handleNotification = () => {
  ElMessage.info('通知功能开发中...')
}

// 用户下拉菜单命令处理
const handleUserCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        await authStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch {
        // 用户取消
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  height: 100vh;
  width: 100vw;

  .layout-aside {
    background-color: #f5f7fa;
    border-right: 1px solid #e4e7ed;
    transition: width 0.3s;
    overflow: hidden;

    .toggle-btn {
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      padding-right: 20px;
      cursor: pointer;
      color: #606266;
      border-bottom: 1px solid #e4e7ed;

      &:hover {
        background-color: #ecf5ff;
      }
    }
  }

  .layout-header {
    background-color: #fff;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      h2 {
        margin: 0;
        font-size: 18px;
        color: #303133;
        font-weight: 600;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 20px;

      .notification-badge {
        cursor: pointer;
      }

      .user-dropdown {
        cursor: pointer;

        .user-info {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 5px 10px;
          border-radius: 4px;
          transition: background-color 0.3s;

          &:hover {
            background-color: #f5f7fa;
          }

          .username {
            font-size: 14px;
            color: #606266;
            max-width: 100px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .dropdown-icon {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }

  .layout-main {
    background-color: #f5f7fa;
    padding: 20px;
    overflow-y: auto;
    height: calc(100vh - 60px);
  }
}

// 路由切换动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>
