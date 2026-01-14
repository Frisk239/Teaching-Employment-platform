<template>
  <div class="settings">
    <el-card shadow="never">
      <template #header>
        <span>个人资料设置</span>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息标签页 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            ref="basicFormRef"
            :model="basicForm"
            :rules="basicRules"
            label-width="100px"
            style="max-width: 600px"
          >
            <el-form-item label="头像" prop="avatar">
              <el-upload
                class="avatar-uploader"
                :action="uploadAction"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="basicForm.avatar" :src="basicForm.avatar" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="用户名" prop="username">
              <el-input v-model="basicForm.username" disabled />
            </el-form-item>

            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="basicForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="basicForm.email" placeholder="请输入邮箱" />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="basicForm.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleUpdateBasic">保存修改</el-button>
              <el-button @click="handleResetBasic">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 安全设置标签页 -->
        <el-tab-pane label="安全设置" name="security">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            style="max-width: 600px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入原密码"
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
              <el-button @click="handleResetPassword">重置</el-button>
            </el-form-item>
          </el-form>

          <el-alert
            title="密码安全提示"
            type="warning"
            :closable="false"
            style="max-width: 600px; margin-top: 20px"
          >
            <ul>
              <li>密码长度至少 6 个字符</li>
              <li>建议包含大小写字母、数字和特殊字符</li>
              <li>不要使用过于简单的密码</li>
            </ul>
          </el-alert>
        </el-tab-pane>

        <!-- 通知设置标签页 -->
        <el-tab-pane label="通知设置" name="notification">
          <el-form label-width="100px" style="max-width: 600px">
            <el-form-item label="邮件通知">
              <el-switch v-model="notificationSettings.email" />
              <span class="form-item-tip">接收重要通知邮件</span>
            </el-form-item>

            <el-form-item label="短信通知">
              <el-switch v-model="notificationSettings.sms" />
              <span class="form-item-tip">接收重要短信提醒</span>
            </el-form-item>

            <el-form-item label="系统通知">
              <el-switch v-model="notificationSettings.system" />
              <span class="form-item-tip">接收系统内通知</span>
            </el-form-item>

            <el-form-item label="作业提醒">
              <el-switch v-model="notificationSettings.homework" />
              <span class="form-item-tip">作业截止前提醒</span>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleSaveNotification">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getUserByIdApi, updateProfileApi, updatePasswordApi } from '@/api/user'

const authStore = useAuthStore()
const activeTab = ref('basic')

// 上传地址
const uploadAction = computed(() => {
  return import.meta.env.VITE_API_BASE_URL + '/user/avatar'
})

// 基本信息表单
const basicFormRef = ref<FormInstance>()
const basicForm = reactive({
  id: undefined,
  username: '',
  realName: '',
  email: '',
  phone: '',
  avatar: ''
})

const basicRules: FormRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 通知设置
const notificationSettings = reactive({
  email: true,
  sms: false,
  system: true,
  homework: true
})

// 获取用户信息
const fetchUserInfo = async () => {
  const user = authStore.user
  if (!user || !user.id) return

  try {
    const response = await getUserByIdApi(user.id)
    if (response.code === 200) {
      Object.assign(basicForm, response.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 头像上传成功
const handleAvatarSuccess = (response: any) => {
  if (response.code === 200) {
    basicForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 更新基本信息
const handleUpdateBasic = async () => {
  if (!basicFormRef.value) return
  await basicFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await updateProfileApi(basicForm)
        if (response.code === 200) {
          ElMessage.success('基本信息更新成功')
          // 更新store中的用户信息
          authStore.setUser(basicForm)
        } else {
          ElMessage.error(response.message || '更新失败')
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败')
      }
    }
  })
}

// 重置基本信息
const handleResetBasic = () => {
  fetchUserInfo()
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await updatePasswordApi({
          userId: basicForm.id!,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        if (response.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          handleResetPassword()
          // 延迟跳转到登录页
          setTimeout(() => {
            authStore.logout()
            window.location.href = '/login'
          }, 1500)
        } else {
          ElMessage.error(response.message || '密码修改失败')
        }
      } catch (error) {
        console.error('密码修改失败:', error)
        ElMessage.error('密码修改失败')
      }
    }
  })
}

// 重置密码表单
const handleResetPassword = () => {
  passwordFormRef.value?.resetFields()
}

// 保存通知设置
const handleSaveNotification = () => {
  // TODO: 实际应该调用API保存通知设置
  ElMessage.success('通知设置保存成功')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped lang="scss">
.settings {
  padding: 20px;

  .avatar-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
    line-height: 120px;
  }

  .avatar {
    width: 120px;
    height: 120px;
    display: block;
  }

  .form-item-tip {
    margin-left: 10px;
    font-size: 12px;
    color: #909399;
  }

  :deep(.el-alert) {
    ul {
      margin: 10px 0 0 20px;
      padding: 0;

      li {
        margin: 5px 0;
        color: #e6a23c;
      }
    }
  }
}
</style>
