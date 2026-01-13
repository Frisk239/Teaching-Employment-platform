<template>
  <div class="sidebar">
    <div class="logo">
      <el-icon class="logo-icon"><School /></el-icon>
      <span v-if="!appStore.sidebarCollapsed" class="logo-text">高校就业平台</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="appStore.sidebarCollapsed"
      :unique-opened="true"
      router
      class="sidebar-menu"
    >
      <template v-for="route in filteredMenuRoutes" :key="route.path">
        <!-- 没有子菜单的路由 -->
        <el-menu-item
          v-if="!route.children || route.children.length === 0"
          :index="route.path"
        >
          <el-icon v-if="route.meta?.icon">
            <component :is="route.meta.icon" />
          </el-icon>
          <template #title>{{ route.meta?.title }}</template>
        </el-menu-item>

        <!-- 有子菜单的路由 -->
        <el-sub-menu v-else :index="route.path">
          <template #title>
            <el-icon v-if="route.meta?.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <span>{{ route.meta?.title }}</span>
          </template>
          <el-menu-item
            v-for="child in route.children"
            :key="child.path"
            :index="child.path"
          >
            <el-icon v-if="child.meta?.icon">
              <component :is="child.meta.icon" />
            </el-icon>
            <template #title>{{ child.meta?.title }}</template>
          </el-menu-item>
        </el-sub-menu>
      </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore, useAuthStore } from '@/stores'
import { routes } from '@/router/routes'

const route = useRoute()
const appStore = useAppStore()
const authStore = useAuthStore()

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 获取用户角色代码
const userRole = computed(() => {
  return authStore.user?.role?.roleCode || 'user'
})

// 检查路由权限
const hasRoutePermission = (route: any): boolean => {
  if (!route.meta?.roles) return true
  return route.meta.roles.includes(userRole.value)
}

// 菜单路由（过滤掉隐藏的路由和没有权限的路由）
const filteredMenuRoutes = computed(() => {
  // 获取主布局的子路由
  const mainRoute = routes.find(r => r.path === '/')
  if (!mainRoute?.children) return []

  return mainRoute.children
    .filter(child => {
      // 过滤隐藏的路由
      if (child.meta?.hidden) return false

      // 检查路由权限
      if (!hasRoutePermission(child)) return false

      return true
    })
    .map(child => {
      if (!child.children) return child

      // 过滤子路由
      const filteredChildren = child.children.filter((subChild: any) => {
        if (subChild.meta?.hidden) return false
        if (!hasRoutePermission(subChild)) return false
        return true
      })

      return {
        ...child,
        children: filteredChildren,
      }
    })
    .filter((child: any) => {
      // 如果所有子路由都被过滤掉了,检查是否保留父路由
      if (!child.children || child.children.length === 0) {
        // 没有子路由或者子路由被全部过滤,保留父路由
        return true
      }
      return true
    })
})
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 0 1rem;
  border-bottom: 1px solid var(--sidebar-border);
  background: var(--sidebar);
  transition: all 0.3s ease;

  img {
    width: 32px;
    height: 32px;
  }

  .el-icon {
    font-size: 32px;
    color: var(--sidebar-primary);
  }

  .logo-text {
    font-size: 1rem;
    font-weight: 600;
    color: var(--sidebar-foreground);
    white-space: nowrap;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: var(--sidebar);

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    color: var(--sidebar-foreground);
    height: 50px;
    line-height: 50px;

    &:hover {
      background-color: var(--sidebar-accent);
    }
  }

  :deep(.el-menu-item.is-active) {
    color: var(--sidebar-primary);
    background-color: var(--sidebar-accent);
    border-right: 3px solid var(--sidebar-primary);
  }

  :deep(.el-sub-menu .el-menu-item) {
    background-color: oklch(0.95 0.01 240);

    &.is-active {
      background-color: var(--sidebar-accent);
    }
  }
}
</style>
