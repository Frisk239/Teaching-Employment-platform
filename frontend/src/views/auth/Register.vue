<template>
  <div class="register-page">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h1 class="register-title">用户注册</h1>
          <p class="register-subtitle">创建您的账户</p>
        </div>
      </template>

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

        <el-form-item>
          <div class="register-footer">
            已有账户？
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores'
import type { FormInstance, FormRules } from 'element-plus'
import type { RegisterForm } from '@/api/types'

const router = useRouter()
const authStore = useAuthStore()

// 角色映射到数据库ID
const ROLE_ID_MAP: Record<string, number> = {
  admin: 1,
  college_head: 2,
  teacher: 3,
  user: 4,      // 学生角色
  enterprise_contact: 5,
}

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

// 处理注册
const handleRegister = async () => {
  console.log('=== 注册按钮被点击 ===')
  console.log('表单数据:', registerForm)

  if (!registerFormRef.value) {
    console.error('表单引用不存在')
    return
  }

  console.log('开始验证表单...')

  await registerFormRef.value.validate(async (valid) => {
    console.log('表单验证结果:', valid)

    if (valid) {
      console.log('表单验证通过，准备调用注册API...')

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
      console.log('注册结果:', success)
      if (success) {
        console.log('注册成功，跳转到登录页')
        router.push('/login')
      }
    } else {
      console.log('表单验证失败')
    }
  }).catch((error) => {
    console.error('表单验证异常:', error)
  })
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, oklch(0.98 0.005 240) 0%, oklch(0.95 0.01 240) 100%);
  padding: 2rem;
}

.register-card {
  width: 100%;
  max-width: 480px;
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

.register-header {
  text-align: center;
}

.register-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 0.5rem;
}

.register-subtitle {
  color: var(--text-muted);
  font-size: 0.875rem;
  margin: 0;
}

.register-footer {
  width: 100%;
  text-align: center;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.login-link {
  color: var(--primary);
  font-weight: 500;
  margin-left: 0.25rem;

  &:hover {
    color: var(--primary-hover);
  }
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
