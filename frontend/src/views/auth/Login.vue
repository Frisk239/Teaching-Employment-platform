<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h1 class="login-title">{{ title }}</h1>
          <p class="login-subtitle">{{ subtitle }}</p>
        </div>
      </template>

      <el-tabs v-model="activeTab" stretch>
        <!-- 登录表单 -->
        <el-tab-pane label="登录" name="login">
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            size="large"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="'User'"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="'Lock'"
                show-password
                clearable
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <div class="login-options">
                <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
                <el-link type="primary">忘记密码？</el-link>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="authStore.isLoading"
                @click="handleLogin"
                style="width: 100%"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 注册表单 -->
        <el-tab-pane label="注册" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            size="large"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                :prefix-icon="'User'"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="'Lock'"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                :prefix-icon="'Lock'"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item prop="realName">
              <el-input
                v-model="registerForm.realName"
                placeholder="请输入真实姓名"
                :prefix-icon="'Avatar'"
                clearable
              />
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                :prefix-icon="'Message'"
                clearable
              />
            </el-form-item>

            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                :prefix-icon="'Phone'"
                clearable
              />
            </el-form-item>

            <el-form-item prop="role">
              <el-select
                v-model="registerForm.role"
                placeholder="请选择角色"
                style="width: 100%"
              >
                <el-option label="学生" value="user" />
                <el-option label="教师" value="teacher" />
                <el-option label="管理员" value="admin" />
                <el-option label="学院负责人" value="college_head" />
                <el-option label="企业对接人" value="enterprise_contact" />
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="authStore.isLoading"
                @click="handleRegister"
                style="width: 100%"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores'
import type { FormInstance, FormRules } from 'element-plus'
import type { LoginForm, RegisterForm } from '@/api/types'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 角色映射到数据库ID
const ROLE_ID_MAP: Record<string, number> = {
  admin: 1,
  college_head: 2,
  teacher: 3,
  user: 4,           // 学生角色
  enterprise_contact: 5,
}

// 标题和副标题
const title = ref('高校教学就业平台')
const subtitle = ref('欢迎登录，请选择登录或注册')

// 当前激活的标签
const activeTab = ref('login')

// 登录表单
const loginFormRef = ref<FormInstance>()
const loginForm = reactive<LoginForm>({
  username: '',
  password: '',
  remember: false,
})

// 注册表单
const registerFormRef = ref<FormInstance>()
const registerForm = reactive<RegisterForm>({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  phone: '',
  role: 'user',  // 默认学生角色
})

// 登录表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度在 6 到 30 个字符', trigger: 'blur' },
  ],
}

// 注册表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度在 6 到 30 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' },
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' },
  ],
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      const success = await authStore.login(loginForm)
      if (success) {
        const redirect = (route.query.redirect as string) || '/dashboard'
        router.push(redirect)
      }
    }
  })
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      // 转换角色字符串为 roleId
      const roleId = ROLE_ID_MAP[registerForm.role || 'user']
      console.log(`角色 ${registerForm.role} 对应的 roleId: ${roleId}`)

      // 构造请求数据，使用 roleId 而不是 role 对象
      const requestData = {
        username: registerForm.username,
        password: registerForm.password,
        realName: registerForm.realName,
        email: registerForm.email,
        phone: registerForm.phone,
        roleId: roleId,  // 发送 roleId 而不是 role 对象
      }

      console.log('发送到后端的数据:', requestData)

      const success = await authStore.register(requestData as any)
      if (success) {
        activeTab.value = 'login'
        // 清空注册表单
        registerFormRef.value?.resetFields()
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, oklch(0.98 0.005 240) 0%, oklch(0.95 0.01 240) 100%);
  padding: 2rem;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 600px;
    height: 600px;
    background: radial-gradient(circle, oklch(0.65 0.18 45 / 0.08) 0%, transparent 70%);
    top: -300px;
    right: -300px;
    border-radius: 50%;
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, oklch(0.55 0.15 220 / 0.06) 0%, transparent 70%);
    bottom: -200px;
    left: -200px;
    border-radius: 50%;
    pointer-events: none;
  }
}

.login-card {
  width: 100%;
  max-width: 480px;
  position: relative;
  z-index: 1;
  border: 1px solid var(--border);
  box-shadow: var(--shadow-xl);
  border-radius: var(--radius-lg);
  animation: fadeIn 0.5s ease-out;

  :deep(.el-card__header) {
    padding: 2rem 2rem 1rem;
    border-bottom: none;
  }

  :deep(.el-card__body) {
    padding: 1rem 2rem 2rem;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
}

.login-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 0.5rem;
}

.login-subtitle {
  color: var(--text-muted);
  font-size: 0.875rem;
  margin: 0;
}

.login-options {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

:deep(.el-tabs__nav-wrap::after) {
  background-color: var(--border);
}

:deep(.el-tabs__item) {
  color: var(--text-muted);
  font-weight: 500;

  &.is-active {
    color: var(--primary);
  }
}

:deep(.el-tabs__active-bar) {
  background-color: var(--primary);
}

:deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
}

:deep(.el-button--primary) {
  background-color: var(--primary);
  border-color: var(--primary);
  font-weight: 600;
  border-radius: var(--radius-md);
  transition: all 0.2s ease;

  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
    box-shadow: var(--shadow-md);
  }

  &:active {
    transform: translateY(0);
  }
}
</style>
