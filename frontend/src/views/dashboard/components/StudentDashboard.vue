<template>
  <div class="student-dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-banner">
          <h2>👋 欢迎回来,{{ authStore.userName }}</h2>
          <p class="date">{{ currentDate }}</p>
        </div>
      </el-col>
    </el-row>

    <!-- 学习进度卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCourses }}</div>
              <div class="stat-label">我的课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon><DocumentChecked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingHomework }}</div>
              <div class="stat-label">待办作业</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon><Briefcase /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalApplications }}</div>
              <div class="stat-label">我的申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
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

    <!-- 推荐职位 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🎯 推荐职位</span>
              <el-button type="primary" text @click="$router.push('/positions')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recommendedPositions" style="width: 100%" v-loading="loadingPositions">
            <el-table-column prop="positionName" label="职位名称" />
            <el-table-column prop="city" label="工作城市" width="120" />
            <el-table-column label="薪资范围" width="150">
              <template #default="{ row }">
                {{ row.salaryMin }}k - {{ row.salaryMax }}k
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="applyPosition(row.id)">申请</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近课程 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>📚 最近课程</span>
              <el-button type="primary" text @click="$router.push('/courses')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="myCourses" style="width: 100%" v-loading="loadingCourses">
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="code" label="课程代码" width="120" />
            <el-table-column prop="type" label="课程类型" width="120" />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column label="进度" width="150">
              <template #default="{ row }">
                <el-progress :percentage="row.progress || 0" />
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
import { useAuthStore } from '@/stores'
import { studentDashboardApi } from '@/api/student'
import { ElMessage } from 'element-plus'
import { Reading, DocumentChecked, Briefcase, Trophy } from '@element-plus/icons-vue'

const authStore = useAuthStore()

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  return now.toLocaleDateString('zh-CN', options)
})

// 统计数据
const stats = ref({
  totalCourses: 0,
  completedCourses: 0,
  inProgressCourses: 0,
  pendingHomework: 0,
  submittedHomework: 0,
  gradedHomework: 0,
  totalApplications: 0,
  interviewCount: 0,
  offerCount: 0
})

// 推荐职位
const recommendedPositions = ref<any[]>([])
const loadingPositions = ref(false)

// 我的课程
const myCourses = ref<any[]>([])
const loadingCourses = ref(false)

// 获取学生ID (假设从用户信息中获取)
const studentId = computed(() => authStore.user?.studentId || authStore.user?.id || 1)

// 加载统计数据
const loadStats = async () => {
  try {
    const data = await studentDashboardApi.getStats(studentId.value)
    stats.value = data
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载推荐职位
const loadRecommendedPositions = async () => {
  try {
    loadingPositions.value = true
    const data = await studentDashboardApi.getRecommendedPositions(studentId.value, 5)
    recommendedPositions.value = data
  } catch (error: any) {
    console.error('加载推荐职位失败:', error)
  } finally {
    loadingPositions.value = false
  }
}

// 加载我的课程
const loadMyCourses = async () => {
  try {
    loadingCourses.value = true
    const data = await studentDashboardApi.getMyCourses(studentId.value)
    myCourses.value = data.slice(0, 5) // 只显示前5个
  } catch (error: any) {
    console.error('加载课程失败:', error)
  } finally {
    loadingCourses.value = false
  }
}

// 申请职位
const applyPosition = async (positionId: number) => {
  try {
    await studentDashboardApi.applyPosition({
      studentId: studentId.value,
      positionId
    })
    ElMessage.success('申请成功')
    await loadStats() // 刷新统计
  } catch (error: any) {
    ElMessage.error(error.message || '申请失败')
  }
}

onMounted(() => {
  loadStats()
  loadRecommendedPositions()
  loadMyCourses()
})
</script>

<style lang="scss" scoped>
.student-dashboard {
  padding: 20px;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;

  h2 {
    margin: 0 0 10px 0;
    font-size: 24px;
    font-weight: 600;
  }

  .date {
    margin: 0;
    opacity: 0.9;
    font-size: 14px;
  }
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    gap: 15px;

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 5px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.content-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
</style>
