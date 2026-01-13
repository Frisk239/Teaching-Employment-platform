<template>
  <div class="page-container">
    <div class="password-container">
      <el-card class="password-card">
        <template #header>
          <div class="card-header">
            <el-icon><Lock /></el-icon>
            <span>修改密码</span>
          </div>
        </template>

        <el-form
          :model="passwordForm"
          :rules="passwordRules"
          ref="passwordFormRef"
          label-width="120px"
          label-position="left"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-20位）"
              show-password
              clearable
            ></el-input>
            <div class="password-tips">
              <p>密码要求：</p>
              <ul>
                <li>长度为6-20个字符</li>
                <li>建议包含字母、数字和特殊字符</li>
              </ul>
            </div>
          </el-form-item>

          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              {{ submitting ? '提交中...' : '确认修改' }}
            </el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <el-alert
          title="安全提示"
          type="warning"
          :closable="false"
          style="margin-top: 2rem;"
        >
          <ul>
            <li>密码修改后，您需要使用新密码重新登录</li>
            <li>请妥善保管您的密码，不要告知他人</li>
            <li>建议定期更换密码以保障账户安全</li>
          </ul>
        </el-alert>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores'
import { authApi } from '@/api/auth'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const passwordFormRef = ref()

// 加载状态
const submitting = ref(false)

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value === passwordForm.oldPassword) {
          callback(new Error('新密码不能与原密码相同'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 方法
const handleSubmit = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    submitting.value = true

    // 调用后端 API
    await authApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功，请重新登录')

    // 清空表单
    passwordFormRef.value?.resetFields()

    // 退出登录并跳转到登录页
    setTimeout(() => {
      authStore.logout()
      router.push('/login')
    }, 1500)
  } catch (error: any) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.message || '密码修改失败')
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  passwordFormRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.password-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem 0;
}

.password-card {
  width: 100%;
  max-width: 600px;

  .card-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-weight: 600;
    font-size: 1.125rem;
  }

  .password-tips {
    margin-top: 0.5rem;
    padding: 0.75rem;
    background: oklch(0.99 0.005 240);
    border-radius: var(--radius-md);
    font-size: 0.875rem;

    p {
      margin: 0 0 0.5rem 0;
      color: var(--text-secondary);
      font-weight: 600;
    }

    ul {
      margin: 0;
      padding-left: 1.5rem;
      color: var(--text-muted);

      li {
        margin-bottom: 0.25rem;
      }
    }
  }

  :deep(.el-alert) {
    ul {
      margin: 0.5rem 0 0 0;
      padding-left: 1.5rem;

      li {
        margin-bottom: 0.25rem;
        color: var(--text-secondary);
      }
    }
  }
}
</style>
