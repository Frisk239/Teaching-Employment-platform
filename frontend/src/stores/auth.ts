/**
 * 高校教学就业平台 - 认证状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginForm, RegisterForm } from '@/api/types'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)
  const isLoading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const userRole = computed(() => {
    // 如果role是对象,取roleCode,如果是字符串直接使用
    if (typeof user.value?.role === 'object') {
      return user.value?.role?.roleCode || 'student'
    }
    return user.value?.role || 'student'
  })
  const userName = computed(() => user.value?.realName || user.value?.username || '')
  const userAvatar = computed(() => user.value?.avatar || '')
  const permissions = computed(() => user.value?.permissions || [])

  // 方法

  /**
   * 登录
   */
  const login = async (form: LoginForm): Promise<boolean> => {
    try {
      isLoading.value = true
      const data = await authApi.login(form)

      // 保存 token 和用户信息
      if (data.token) {
        token.value = data.token
        localStorage.setItem('token', data.token)
      }

      if (data) {
        user.value = data
        localStorage.setItem('user', JSON.stringify(data))
      }

      ElMessage.success('登录成功')
      return true
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败')
      return false
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 注册
   */
  const register = async (form: RegisterForm): Promise<boolean> => {
    console.log('=== authStore.register 被调用 ===')
    console.log('注册表单数据:', form)

    try {
      isLoading.value = true
      console.log('开始调用 authApi.register...')

      const data = await authApi.register(form)

      console.log('注册API返回数据:', data)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error: any) {
      console.error('注册失败，错误信息:', error)
      ElMessage.error(error.message || '注册失败')
      return false
    } finally {
      isLoading.value = false
      console.log('注册流程结束，loading状态已重置')
    }
  }

  /**
   * 退出登录
   */
  const logout = async () => {
    try {
      // 调用后端API
      await authApi.logout()
    } catch (error) {
      console.error('退出登录API调用失败:', error)
      // 即使API调用失败,也清除本地状态
    } finally {
      // 清除本地状态
      token.value = ''
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('refreshToken')
      ElMessage.success('已退出登录')
      router.push('/login')
    }
  }

  /**
   * 恢复登录状态
   */
  const restoreAuth = async () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')

    if (savedToken) {
      token.value = savedToken
    }

    if (savedUser) {
      try {
        user.value = JSON.parse(savedUser)
      } catch (error) {
        console.error('恢复用户信息失败:', error)
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        token.value = ''
        user.value = null
        return
      }
    }

    // 如果有token,验证token是否有效
    if (token.value) {
      try {
        // 调用API验证token并获取最新用户信息
        const userData = await authApi.getCurrentUser()
        user.value = userData
        localStorage.setItem('user', JSON.stringify(userData))
      } catch (error) {
        console.error('Token验证失败:', error)
        // token无效,清除所有登录信息
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        token.value = ''
        user.value = null
      }
    }
  }

  /**
   * 获取当前用户信息
   */
  const getCurrentUser = async (): Promise<User | null> => {
    try {
      const data = await authApi.getCurrentUser()
      user.value = data
      localStorage.setItem('user', JSON.stringify(data))
      return data
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }

  /**
   * 检查用户名是否存在
   */
  const checkUsername = async (username: string): Promise<boolean> => {
    try {
      const data = await authApi.checkUsername(username)
      return data.exists || false
    } catch (error) {
      console.error('检查用户名失败:', error)
      return false
    }
  }

  /**
   * 刷新 Token
   */
  const refreshToken = async (): Promise<boolean> => {
    try {
      const savedRefreshToken = localStorage.getItem('refreshToken')
      if (!savedRefreshToken) {
        return false
      }

      const data = await authApi.refreshToken(savedRefreshToken)
      token.value = data.token
      localStorage.setItem('token', data.token)
      localStorage.setItem('refreshToken', data.refreshToken)
      return true
    } catch (error) {
      console.error('刷新 Token 失败:', error)
      logout()
      return false
    }
  }

  /**
   * 更新用户信息
   */
  const updateUser = (updates: Partial<User>) => {
    if (user.value) {
      user.value = { ...user.value, ...updates }
      localStorage.setItem('user', JSON.stringify(user.value))
    }
  }

  return {
    // 状态
    token,
    user,
    isLoading,

    // 计算属性
    isLoggedIn,
    userRole,
    userName,
    userAvatar,
    permissions,

    // 方法
    login,
    register,
    logout,
    restoreAuth,
    getCurrentUser,
    checkUsername,
    refreshToken,
    updateUser,
  }
})
