<template>
  <div class="profile">
    <el-row :gutter="20">
      <!-- 左侧：用户信息卡片 -->
      <el-col :span="8">
        <el-card shadow="never">
          <div class="user-info">
            <el-avatar :size="120" :src="userInfo.avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <h2>{{ userInfo.realName || userInfo.username }}</h2>
            <el-tag type="info" size="large">{{ getRoleText(userInfo.roleCode) }}</el-tag>
            <p class="user-id">用户名: {{ userInfo.username }}</p>
            <p class="user-email" v-if="userInfo.email">{{ userInfo.email }}</p>
            <p class="user-phone" v-if="userInfo.phone">{{ userInfo.phone }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">{{ userStats.loginCount }}</div>
                <div class="stat-label">登录次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.courseCount }}</div>
                <div class="stat-label">课程数</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 快捷操作 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" link @click="$router.push('/settings')">
              <el-icon><Edit /></el-icon> 编辑资料
            </el-button>
            <el-button type="primary" link @click="changePasswordVisible = true">
              <el-icon><Lock /></el-icon> 修改密码
            </el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：详细信息 -->
      <el-col :span="16">
        <!-- 基本信息 -->
        <el-card shadow="never">
          <template #header>
            <span>基本信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ userInfo.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="真实姓名">{{ userInfo.realName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag type="info">{{ getRoleText(userInfo.roleCode) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="学校" v-if="userInfo.schoolId">
              {{ userInfo.schoolName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                {{ userInfo.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间" :span="2">
              {{ userInfo.createTime || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 最近活动 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span>最近活动</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="activity in recentActivities"
              :key="activity.id"
              :timestamp="activity.time"
              placement="top"
            >
              <el-card>
                <h4>{{ activity.title }}</h4>
                <p>{{ activity.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="changePasswordVisible" title="修改密码" width="500px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePasswordVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { UserFilled, Edit, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getUserByIdApi, updatePasswordApi } from '@/api/user'

const authStore = useAuthStore()
const userInfo = ref(authStore.user || {})

// 用户统计数据
const userStats = ref({
  loginCount: 0,
  courseCount: 0
})

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    title: '登录系统',
    description: '您刚刚登录了系统',
    time: new Date().toLocaleString()
  }
])

// 修改密码
const changePasswordVisible = ref(false)
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

// 获取角色文本
const getRoleText = (roleCode: string) => {
  const roleMap: Record<string, string> = {
    admin: '管理员',
    college_head: '学院负责人',
    teacher: '教师',
    user: '学员',
    enterprise_contact: '企业对接人'
  }
  return roleMap[roleCode] || '未知角色'
}

// 获取用户详细信息
const fetchUserInfo = async () => {
  if (!userInfo.value.id) return
  try {
    const response = await getUserByIdApi(userInfo.value.id)
    if (response.code === 200) {
      userInfo.value = response.data
      // 更新store中的用户信息
      authStore.setUser(response.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await updatePasswordApi({
          userId: userInfo.value.id!,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        if (response.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          changePasswordVisible.value = false
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

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped lang="scss">
.profile {
  padding: 20px;

  .user-info {
    text-align: center;

    h2 {
      margin: 15px 0 10px;
      font-size: 24px;
      color: #303133;
    }

    .user-id,
    .user-email,
    .user-phone {
      margin: 8px 0;
      color: #606266;
      font-size: 14px;
    }

    .user-stats {
      display: flex;
      justify-content: space-around;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;

      .stat-item {
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #409eff;
        }

        .stat-label {
          margin-top: 5px;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  .quick-actions {
    display: flex;
    flex-direction: column;
    gap: 10px;

    .el-button {
      font-size: 16px;
      padding: 15px 0;
      text-align: left;
    }
  }

  :deep(.el-timeline-item__wrapper) {
    padding-left: 30px;

    .el-card {
      h4 {
        margin: 0 0 10px;
        font-size: 16px;
        color: #303133;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: #606266;
      }
    }
  }
}
</style>
