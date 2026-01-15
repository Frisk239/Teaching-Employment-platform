<template>
  <div class="student-dashboard">
    <!-- 页面标题和欢迎语 -->
    <el-card shadow="never" class="header-card">
      <div class="page-header">
        <div class="title-section">
          <h2>学员工作台</h2>
          <p class="subtitle">欢迎回来, {{ authStore.userName }} | 今天是 {{ currentDate }}</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="16" class="stats-row">
        <el-col :xs="12" :sm="8" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card" @click="$router.push('/teaching/courses')">
            <div class="stat-content">
              <div class="stat-icon courses">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.inProgressCourses }}</div>
                <div class="stat-label">在学课程</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card" @click="$router.push('/teaching/assignments')">
            <div class="stat-content">
              <div class="stat-icon homework">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pendingHomework }}</div>
                <div class="stat-label">待办作业</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card" @click="$router.push('/employment/applications')">
            <div class="stat-content">
              <div class="stat-icon applications">
                <el-icon><DocumentCopy /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalApplications }}</div>
                <div class="stat-label">我的申请</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card" @click="$router.push('/employment/offers')">
            <div class="stat-content">
              <div class="stat-icon offers">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.offerCount }}</div>
                <div class="stat-label">收到Offer</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 主内容区域 -->
    <el-row :gutter="20">
      <!-- 左侧: 待办作业 + 我的课程 -->
      <el-col :xs="24" :lg="16">
        <!-- 待办作业 -->
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="card-header">
              <div class="card-title-wrapper">
                <el-icon><Document /></el-icon>
                <span class="card-title">待办作业</span>
              </div>
              <el-button type="primary" size="small" text @click="$router.push('/teaching/assignments')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="pendingHomework" stripe style="width: 100%" v-loading="loadingHomework" max-height="300">
            <el-table-column prop="courseName" label="课程" width="120" show-overflow-tooltip />
            <el-table-column prop="title" label="作业标题" show-overflow-tooltip />
            <el-table-column label="截止时间" width="160">
              <template #default="{ row }">
                <span :class="{ 'text-danger': isUrgent(row.deadline) }">
                  {{ formatDateTime(row.deadline) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.submitted" type="success" size="small">已提交</el-tag>
                <el-tag v-else-if="isUrgent(row.deadline)" type="danger" size="small">紧急</el-tag>
                <el-tag v-else type="warning" size="small">待提交</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button
                  v-if="!row.submitted"
                  type="primary"
                  size="small"
                  text
                  @click="handleHomework(row)"
                >
                  {{ row.urgent ? '立即' : '提交' }}
                </el-button>
                <el-button v-else type="success" size="small" text>
                  已提交
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 我的课程 -->
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="card-header">
              <div class="card-title-wrapper">
                <el-icon><Reading /></el-icon>
                <span class="card-title">我的课程</span>
              </div>
              <el-button type="primary" size="small" text @click="$router.push('/teaching/my-courses')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="myCourses" stripe style="width: 100%" v-loading="loadingCourses" max-height="300">
            <el-table-column prop="name" label="课程名称" show-overflow-tooltip />
            <el-table-column prop="code" label="课程代码" width="120" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="credit" label="学分" width="80" align="center" />
            <el-table-column label="进度" width="150">
              <template #default="{ row }">
                <el-progress :percentage="row.progress || 0" :stroke-width="8" />
              </template>
            </el-table-column>
            <el-table-column label="作业数" width="80" align="center">
              <template #default="{ row }">
                <el-badge :value="row.homeworkCount || 0" type="primary" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧: 推荐职位 + 最近动态 -->
      <el-col :xs="24" :lg="8">
        <!-- 推荐职位 -->
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="card-header">
              <div class="card-title-wrapper">
                <el-icon><Position /></el-icon>
                <span class="card-title">推荐职位</span>
              </div>
              <el-button type="primary" size="small" text @click="$router.push('/employment/positions')">
                更多
              </el-button>
            </div>
          </template>
          <div v-loading="loadingPositions" class="position-list">
            <div
              v-for="position in recommendedPositions"
              :key="position.id"
              class="position-item"
            >
              <div class="position-header">
                <span class="position-name">{{ position.positionName }}</span>
                <span class="position-salary">{{ position.salaryMin }}k-{{ position.salaryMax }}k</span>
              </div>
              <div class="position-info">
                <el-tag size="small" type="info">{{ position.city }}</el-tag>
              </div>
              <div class="position-actions">
                <el-button type="primary" size="small" @click="applyPosition(position.id)">
                  申请
                </el-button>
                <el-button size="small" @click="viewPosition(position.id)">
                  详情
                </el-button>
              </div>
            </div>
            <el-empty v-if="!loadingPositions && recommendedPositions.length === 0" description="暂无推荐职位" :image-size="80" />
          </div>
        </el-card>

        <!-- 最近申请 -->
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="card-header">
              <div class="card-title-wrapper">
                <el-icon><List /></el-icon>
                <span class="card-title">最近申请</span>
              </div>
              <el-button type="primary" size="small" text @click="$router.push('/employment/applications')">
                全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentApplications" stripe style="width: 100%" v-loading="loadingApplications" max-height="250">
            <el-table-column prop="positionName" label="职位" show-overflow-tooltip />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="getApplicationStatusType(row.status)" size="small">
                  {{ getApplicationStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="100">
              <template #default="{ row }">
                {{ formatShortDate(row.applyTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores'
import { ElMessage } from 'element-plus'
import {
  Reading,
  Document,
  DocumentCopy,
  Trophy,
  Position,
  List
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import axios from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()

// 当前日期
const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd')
})

// 统计数据
const stats = ref({
  totalCourses: 0,
  completedCourses: 0,
  inProgressCourses: 0,
  notStartedCourses: 0,
  pendingHomework: 0,
  submittedHomework: 0,
  gradedHomework: 0,
  totalApplications: 0,
  interviewCount: 0,
  offerCount: 0
})

// 待办作业
const pendingHomework = ref<any[]>([])
const loadingHomework = ref(false)

// 我的课程
const myCourses = ref<any[]>([])
const loadingCourses = ref(false)

// 推荐职位
const recommendedPositions = ref<any[]>([])
const loadingPositions = ref(false)

// 最近申请
const recentApplications = ref<any[]>([])
const loadingApplications = ref(false)

// 获取当前学员ID
const getStudentId = () => {
  // 优先从 store 中获取
  const userInfo = authStore.user
  if (userInfo && userInfo.studentId) {
    return userInfo.studentId
  }

  // 其次尝试从 localStorage 或 sessionStorage 获取
  const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      if (user.studentId) {
        return user.studentId
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }

  // 如果还是没有,返回0表示未找到
  console.warn('无法获取学员ID,请确保已正确登录')
  return 0
}

// 加载统计数据
const loadStats = async () => {
  try {
    const studentId = getStudentId()
    console.log('loadStats - studentId:', studentId)
    const response = await axios.get(`/student-dashboard/stats/${studentId}`)
    console.log('loadStats - response:', response)
    // axios拦截器已经返回了data,所以直接使用response即可
    if (response) {
      stats.value = response
      console.log('loadStats - stats.value:', stats.value)
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
    ElMessage.warning('统计数据加载失败')
  }
}

// 加载待办作业
const loadPendingHomework = async () => {
  try {
    loadingHomework.value = true
    const studentId = getStudentId()
    const response = await axios.get(`/student-dashboard/pending-homework/${studentId}`, {
      params: { current: 1, size: 5 }
    })
    // axios拦截器已经返回了data,response直接就是data对象
    if (response && (response as any).records) {
      pendingHomework.value = (response as any).records
    }
  } catch (error: any) {
    console.error('加载待办作业失败:', error)
  } finally {
    loadingHomework.value = false
  }
}

// 加载我的课程
const loadMyCourses = async () => {
  try {
    loadingCourses.value = true
    const studentId = getStudentId()
    const response = await axios.get(`/student-dashboard/courses/${studentId}`)
    // axios拦截器已经返回了data,response直接就是课程数组
    if (response && Array.isArray(response)) {
      myCourses.value = response.slice(0, 5) // 只显示前5个
    }
  } catch (error: any) {
    console.error('加载课程失败:', error)
  } finally {
    loadingCourses.value = false
  }
}

// 加载推荐职位
const loadRecommendedPositions = async () => {
  try {
    loadingPositions.value = true
    const studentId = getStudentId()
    const response = await axios.get(`/student-dashboard/recommended-positions/${studentId}`, {
      params: { limit: 5 }
    })
    // axios拦截器已经返回了data,response直接就是职位数组
    if (response && Array.isArray(response)) {
      recommendedPositions.value = response
    }
  } catch (error: any) {
    console.error('加载推荐职位失败:', error)
  } finally {
    loadingPositions.value = false
  }
}

// 加载最近申请
const loadRecentApplications = async () => {
  try {
    loadingApplications.value = true
    const studentId = getStudentId()
    const response = await axios.get(`/student-dashboard/my-applications/${studentId}`, {
      params: { current: 1, size: 5 }
    })
    // axios拦截器已经返回了data,response直接就是data对象
    if (response && (response as any).records) {
      recentApplications.value = (response as any).records
    }
  } catch (error: any) {
    console.error('加载申请列表失败:', error)
  } finally {
    loadingApplications.value = false
  }
}

// 判断是否紧急(距离截止时间不足3天)
const isUrgent = (deadline: string) => {
  if (!deadline) return false
  const deadlineTime = new Date(deadline).getTime()
  const now = new Date().getTime()
  const threeDays = 3 * 24 * 60 * 60 * 1000
  return deadlineTime - now < threeDays
}

// 处理作业
const handleHomework = (homework: any) => {
  ElMessage.info('作业提交功能开发中...')
  // TODO: 跳转到作业详情页或打开提交对话框
}

// 申请职位
const applyPosition = async (positionId: number) => {
  try {
    const studentId = getStudentId()
    await axios.post('/student-dashboard/apply-position', {
      studentId,
      positionId
    })
    ElMessage.success('申请成功')
    await loadStats() // 刷新统计
    await loadRecentApplications() // 刷新申请列表
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '申请失败')
  }
}

// 查看职位详情
const viewPosition = (positionId: number) => {
  router.push(`/employment/positions?id=${positionId}`)
}

// 获取申请状态标签
const getApplicationStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待处理',
    'screened': '已筛选',
    'test_passed': '笔试通过',
    'test_failed': '笔试失败',
    'interview_passed': '面试通过',
    'interview_failed': '面试失败',
    'offered': '已录用',
    'rejected': '已拒绝'
  }
  return map[status] || status
}

// 获取申请状态类型
const getApplicationStatusType = (status: string) => {
  const map: Record<string, any> = {
    'pending': 'info',
    'screened': 'primary',
    'test_passed': 'success',
    'test_failed': 'danger',
    'interview_passed': 'success',
    'interview_failed': 'danger',
    'offered': 'success',
    'rejected': 'danger'
  }
  return map[status] || ''
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  return dayjs(dateTime).format('MM-DD HH:mm')
}

// 格式化短日期
const formatShortDate = (dateTime: string) => {
  return dayjs(dateTime).format('MM-DD')
}

onMounted(async () => {
  // 加载所有数据
  await Promise.all([
    loadStats(),
    loadPendingHomework(),
    loadMyCourses(),
    loadRecommendedPositions(),
    loadRecentApplications()
  ])
})
</script>

<style scoped lang="scss">
.student-dashboard {
  padding: 20px;

  .header-card {
    margin-bottom: 20px;

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .title-section {
        h2 {
          margin: 0 0 5px 0;
          font-size: 24px;
          font-weight: 600;
        }

        .subtitle {
          margin: 0;
          color: var(--el-text-color-secondary);
          font-size: 14px;
        }
      }
    }

    .stats-row {
      .stat-card {
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }

        .stat-content {
          display: flex;
          align-items: center;
          gap: 16px;

          .stat-icon {
            width: 56px;
            height: 56px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;

            &.courses {
              background: #e0f2fe;
              color: #0369a1;
            }

            &.homework {
              background: #fef3c7;
              color: #b45309;
            }

            &.applications {
              background: #dcfce7;
              color: #15803d;
            }

            &.offers {
              background: #fee2e2;
              color: #dc2626;
            }
          }

          .stat-info {
            flex: 1;

            .stat-value {
              font-size: 24px;
              font-weight: 600;
              line-height: 1;
              margin-bottom: 4px;
              color: var(--el-text-color-primary);
            }

            .stat-label {
              font-size: 14px;
              color: var(--el-text-color-secondary);
            }
          }
        }
      }
    }
  }

  .content-card {
    margin-bottom: 20px;
    border-radius: 12px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-title-wrapper {
        display: flex;
        align-items: center;
        gap: 8px;

        .el-icon {
          font-size: 18px;
          color: var(--el-color-primary);
        }
      }

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }

    .text-danger {
      color: #f56c6c;
      font-weight: 500;
    }

    .position-list {
      .position-item {
        padding: 16px;
        border-bottom: 1px solid var(--el-border-color-lighter);
        transition: background-color 0.3s;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background-color: var(--el-fill-color-light);
        }

        .position-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .position-name {
            font-weight: 600;
            color: var(--el-text-color-primary);
            font-size: 14px;
          }

          .position-salary {
            color: #f56c6c;
            font-weight: 600;
            font-size: 14px;
          }
        }

        .position-info {
          margin-bottom: 12px;
        }

        .position-actions {
          display: flex;
          gap: 8px;
          justify-content: flex-end;
        }
      }
    }
  }
}
</style>
