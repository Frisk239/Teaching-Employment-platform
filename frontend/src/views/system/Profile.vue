<template>
  <div class="page-container">
    <div class="profile-container">
      <!-- 左侧个人信息卡片 -->
      <el-card class="profile-card">
        <div class="profile-header">
          <div class="avatar-container">
            <el-avatar :size="100" :src="userInfo.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <el-button
              type="primary"
              size="small"
              circle
              class="avatar-edit-btn"
              @click="handleAvatarUpload"
            >
              <el-icon><Edit /></el-icon>
            </el-button>
          </div>
          <h2 class="profile-name">{{ userInfo.realName || userInfo.username }}</h2>
          <p class="profile-role">{{ getRoleLabel(userInfo.role) }}</p>
        </div>

        <el-divider />

        <div class="profile-info">
          <div class="info-item">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">真实姓名</span>
            <span class="info-value">{{ userInfo.realName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号</span>
            <span class="info-value">{{ userInfo.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">邮箱</span>
            <span class="info-value">{{ userInfo.email || '-' }}</span>
          </div>
        </div>
      </el-card>

      <!-- 右侧编辑表单 -->
      <el-card class="edit-card">
        <template #header>
          <div class="card-header">
            <el-icon><Edit /></el-icon>
            <span>编辑资料</span>
          </div>
        </template>

        <el-form
          :model="editForm"
          :rules="formRules"
          ref="editFormRef"
          label-width="100px"
        >
          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="editForm.realName"
              placeholder="请输入真实姓名"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="editForm.phone"
              placeholder="请输入手机号"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="editForm.email"
              placeholder="请输入邮箱"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              {{ submitting ? '保存中...' : '保存修改' }}
            </el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePasswordSubmit" :loading="passwordSubmitting">
          {{ passwordSubmitting ? '提交中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores'
import { authApi } from '@/api/auth'

const authStore = useAuthStore()

// 表单引用
const editFormRef = ref()
const passwordFormRef = ref()

// 加载状态
const submitting = ref(false)
const passwordSubmitting = ref(false)

// 对话框状态
const passwordDialogVisible = ref(false)

// 用户信息
const userInfo = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  role: ''
})

// 编辑表单
const editForm = reactive({
  realName: '',
  phone: '',
  email: ''
})

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    {
      validator: (_rule: any, value: string, callback: any) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (value && !emailRegex.test(value)) {
          callback(new Error('请输入正确的邮箱格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
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
const getRoleLabel = (role: string) => {
  const map: Record<string, string> = {
    'admin': '管理员',
    'teacher': '教师',
    'student': '学生'
  }
  return map[role] || role
}

const handleAvatarUpload = () => {
  ElMessage.info('头像上传功能开发中')
  // TODO: 实现头像上传
}

const handleSubmit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    submitting.value = true

    // 调用后端 API
    await authApi.updateProfile({
      realName: editForm.realName,
      phone: editForm.phone,
      email: editForm.email
    })

    // 更新本地用户信息
    userInfo.value.realName = editForm.realName
    userInfo.value.phone = editForm.phone
    userInfo.value.email = editForm.email

    // 更新store中的用户信息
    authStore.updateUser({
      realName: editForm.realName,
      phone: editForm.phone,
      email: editForm.email
    })

    ElMessage.success('保存成功!')
  } catch (error: any) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  editForm.realName = userInfo.value.realName
  editForm.phone = userInfo.value.phone
  editForm.email = userInfo.value.email
  editFormRef.value?.clearValidate()
}

const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    passwordSubmitting.value = true

    // 调用后端 API
    await authApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功，请重新登录')

    // 清空表单并关闭对话框
    passwordFormRef.value?.resetFields()
    passwordDialogVisible.value = false

    // 退出登录并跳转到登录页
    setTimeout(() => {
      authStore.logout()
    }, 1500)
  } catch (error: any) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.message || '修改密码失败')
  } finally {
    passwordSubmitting.value = false
  }
}

// 加载用户信息
const loadUserInfo = () => {
  // 从 store 加载用户信息
  const user = authStore.user
  if (user) {
    userInfo.value = {
      username: user.username || '',
      realName: user.realName || '',
      phone: user.phone || '',
      email: user.email || '',
      avatar: user.avatar || '',
      role: typeof user.role === 'string' ? user.role : user.role?.roleCode || ''
    }

    editForm.realName = user.realName || ''
    editForm.phone = user.phone || ''
    editForm.email = user.email || ''
  }
}

onMounted(() => {
  loadUserInfo()
})

// 暴露方法供父组件调用（如果需要打开修改密码对话框）
defineExpose({
  openPasswordDialog: () => {
    passwordDialogVisible.value = true
  }
})
</script>

<style scoped lang="scss">
.profile-container {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.profile-card {
  .profile-header {
    text-align: center;

    .avatar-container {
      position: relative;
      display: inline-block;
      margin-bottom: 1rem;

      .avatar-edit-btn {
        position: absolute;
        bottom: 0;
        right: 0;
      }
    }

    .profile-name {
      font-size: 1.5rem;
      font-weight: 700;
      margin: 0 0 0.5rem 0;
      color: var(--text-primary);
    }

    .profile-role {
      margin: 0;
      color: var(--text-secondary);
      font-size: 0.875rem;
    }
  }

  .profile-info {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 0;
      border-bottom: 1px solid var(--border);

      &:last-child {
        border-bottom: none;
      }

      .info-label {
        color: var(--text-secondary);
        font-size: 0.875rem;
      }

      .info-value {
        color: var(--text-primary);
        font-weight: 500;
      }
    }
  }
}

.edit-card {
  .card-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-weight: 600;
  }
}
</style>
