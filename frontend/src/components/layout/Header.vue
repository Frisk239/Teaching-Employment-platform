<template>
  <div class="header">
    <div class="header-left">
      <el-button
        :icon="appStore.sidebarCollapsed ? 'Expand' : 'Fold'"
        text
        @click="appStore.toggleSidebar"
      />
      <el-breadcrumb separator="/">
        <el-breadcrumb-item
          v-for="item in breadcrumbs"
          :key="item.path"
          :to="item.path"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <!-- 全屏切换 -->
      <el-tooltip content="全屏" placement="bottom">
        <el-button :icon="isFullscreen ? 'Aim' : 'FullScreen'" text @click="toggleFullscreen" />
      </el-tooltip>

      <!-- 通知 -->
      <el-badge :value="notificationCount" :hidden="notificationCount === 0">
        <el-button :icon="'Bell'" text @click="showNotifications" />
      </el-badge>

      <!-- 用户菜单 -->
      <el-dropdown @command="handleCommand" trigger="click" placement="bottom-end">
        <div class="user-info">
          <el-avatar :size="32" :src="authStore.userAvatar">
            {{ authStore.userName.charAt(0) }}
          </el-avatar>
          <span class="user-name">{{ authStore.userName }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              <span>个人资料</span>
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore, useAppStore } from '@/stores'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta?.title as string,
  }))
})

// 通知数量
const notificationCount = computed(() => 5)

// 全屏状态
const isFullscreen = computed(() => !!document.fullscreenElement)

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 显示通知
const showNotifications = () => {
  // TODO: 实现通知功能
  console.log('显示通知')
}

// 处理用户菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/system/profile')
      break
    case 'password':
      router.push('/system/password')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        authStore.logout()
      })
      break
  }
}
</script>

<style lang="scss" scoped>
.header {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  background: inherit;
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;

  &:hover {
    background: var(--secondary);
  }

  .user-name {
    font-size: 0.875rem;
    font-weight: 500;
    color: var(--text-primary);
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .dropdown-icon {
    font-size: 14px;
    color: var(--text-secondary);
    transition: transform 0.3s ease;
  }

  &:hover .dropdown-icon {
    transform: rotate(180deg);
  }
}

// 下拉菜单样式优化
:deep(.el-dropdown-menu) {
  min-width: 160px;
  padding: 0.25rem 0;

  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;

    .el-icon {
      font-size: 16px;
      color: var(--text-secondary);
    }

    span {
      font-size: 0.875rem;
      color: var(--text-primary);
    }

    &:hover {
      background: var(--secondary);

      .el-icon {
        color: var(--primary);
      }

      span {
        color: var(--primary);
      }
    }

    &.is-divided {
      border-top: 1px solid var(--border);
      margin-top: 0.25rem;
      padding-top: 0.5rem;
    }
  }
}

.el-breadcrumb {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: var(--text-secondary);
      font-weight: 500;

      &:hover {
        color: var(--primary);
      }
    }

    &:last-child {
      .el-breadcrumb__inner {
        color: var(--text-primary);
      }
    }
  }
}
</style>
