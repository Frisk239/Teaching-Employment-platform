<template>
  <el-container class="main-layout">
    <el-aside :width="sidebarWidth" class="sidebar">
      <Sidebar />
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <Header />
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const appStore = useAppStore()

const sidebarWidth = computed(() => {
  return appStore.sidebarCollapsed ? '64px' : '240px'
})
</script>

<style lang="scss" scoped>
.main-layout {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background: var(--sidebar);
  border-right: 1px solid var(--sidebar-border);
  transition: width 0.3s ease;
  overflow-x: hidden;
  overflow-y: auto;
  flex-shrink: 0;
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex: 1;
}

.header {
  height: 60px;
  background: var(--card);
  border-bottom: 1px solid var(--border);
  padding: 0;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  background: var(--background);
}

// 滚动条样式
.sidebar::-webkit-scrollbar,
.main-content::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track,
.main-content::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar::-webkit-scrollbar-thumb,
.main-content::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 3px;

  &:hover {
    background: var(--text-muted);
  }
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
