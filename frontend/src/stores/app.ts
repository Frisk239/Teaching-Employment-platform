/**
 * 高校教学就业平台 - 应用全局状态管理
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏状态
  const sidebarCollapsed = ref(false)

  // 页面加载状态
  const isLoading = ref(false)

  // 设备类型
  const device = ref<'desktop' | 'tablet' | 'mobile'>('desktop')

  // 主题
  const theme = ref<'light' | 'dark'>('light')

  // 方法

  /**
   * 切换侧边栏
   */
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  /**
   * 设置侧边栏状态
   */
  const setSidebarCollapsed = (collapsed: boolean) => {
    sidebarCollapsed.value = collapsed
  }

  /**
   * 设置加载状态
   */
  const setLoading = (loading: boolean) => {
    isLoading.value = loading
  }

  /**
   * 设置设备类型
   */
  const setDevice = (deviceType: 'desktop' | 'tablet' | 'mobile') => {
    device.value = deviceType

    // 移动端默认收起侧边栏
    if (deviceType === 'mobile') {
      sidebarCollapsed.value = true
    }
  }

  /**
   * 切换主题
   */
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    document.documentElement.classList.toggle('dark', theme.value === 'dark')
  }

  /**
   * 设置主题
   */
  const setTheme = (themeValue: 'light' | 'dark') => {
    theme.value = themeValue
    document.documentElement.classList.toggle('dark', themeValue === 'dark')
  }

  return {
    // 状态
    sidebarCollapsed,
    isLoading,
    device,
    theme,

    // 方法
    toggleSidebar,
    setSidebarCollapsed,
    setLoading,
    setDevice,
    toggleTheme,
    setTheme,
  }
})
