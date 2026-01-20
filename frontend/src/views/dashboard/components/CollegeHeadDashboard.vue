<template>
  <div class="college-head-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><Avatar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.teacherCount || 0 }}</div>
              <div class="stat-label">本院教师</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.studentCount || 0 }}</div>
              <div class="stat-label">本院学生</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.courseCount || 0 }}</div>
              <div class="stat-label">开设课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="28"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.employmentRate || 0 }}%</div>
              <div class="stat-label">就业率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区 -->
    <el-row :gutter="20" class="content-row">
      <!-- 左侧：快捷操作 + 最新日报 -->
      <el-col :span="16">
        <!-- 快捷操作 -->
        <el-card shadow="hover" class="action-card">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="goToTeachers">
              <el-icon><Avatar /></el-icon>
              教师管理
            </el-button>
            <el-button type="success" @click="goToCourses">
              <el-icon><Reading /></el-icon>
              课程管理
            </el-button>
            <el-button type="warning" @click="goToStudents">
              <el-icon><User /></el-icon>
              学员管理
            </el-button>
            <el-button type="info" @click="goToDailyReports">
              <el-icon><DocumentCopy /></el-icon>
              日报管理
            </el-button>
            <el-button type="danger" @click="goToStudentProfile">
              <el-icon><Document /></el-icon>
              学员档案
            </el-button>
            <el-button type="primary" plain @click="goToStatistics">
              <el-icon><DataAnalysis /></el-icon>
              就业统计
            </el-button>
          </div>
        </el-card>

        <!-- 最新日报 -->
        <el-card shadow="hover" class="report-card">
          <template #header>
            <div class="card-header">
              <span>最新日报</span>
              <el-button link type="primary" @click="goToDailyReports">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentReports" style="width: 100%" v-loading="loading" stripe>
            <el-table-column prop="studentName" label="学员姓名" width="120" />
            <el-table-column prop="reportDate" label="提交日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.reportDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="content" label="日报内容" show-overflow-tooltip />
            <el-table-column prop="studyHours" label="学习时长" width="100" align="center">
              <template #default="{ row }">
                {{ row.studyHours }}小时
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getReportStatusType(row.status)" size="small">
                  {{ getReportStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：最新职位 + 本周Offer -->
      <el-col :span="8">
        <!-- 最新职位 -->
        <el-card shadow="hover" class="positions-card">
          <template #header>
            <span>最新职位</span>
          </template>
          <div class="position-list" v-loading="loading">
            <div v-if="recentPositions.length === 0" class="empty-state">
              <el-empty description="暂无职位" :image-size="60" />
            </div>
            <div v-else>
              <div
                v-for="position in recentPositions"
                :key="position.id"
                class="position-item"
                @click="viewPositionDetail(position)"
              >
                <div class="position-header">
                  <div class="position-name">{{ position.positionName }}</div>
                  <el-tag size="small" type="success">{{ position.city }}</el-tag>
                </div>
                <div class="position-company">{{ position.companyName }}</div>
                <div class="position-salary">¥{{ position.salaryMin }}-{{ position.salaryMax }} / {{ position.salaryUnit }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 本周Offer -->
        <el-card shadow="hover" class="offers-card">
          <template #header>
            <div class="card-header">
              <span>本周Offer</span>
              <el-button link type="primary" @click="goToStatistics">就业统计</el-button>
            </div>
          </template>
          <div class="offer-list" v-loading="loading">
            <div v-if="weeklyOffers.length === 0" class="empty-state">
              <el-empty description="本周暂无新Offer" :image-size="60" />
            </div>
            <div v-else>
              <div
                v-for="offer in weeklyOffers"
                :key="offer.id"
                class="offer-item"
              >
                <div class="offer-header">
                  <el-icon color="#67c23a" :size="20"><Checked /></el-icon>
                  <div class="offer-info">
                    <div class="offer-student">{{ offer.studentName }}</div>
                    <div class="offer-company">{{ offer.companyName }}</div>
                  </div>
                </div>
                <div class="offer-salary">¥{{ offer.salary }}/月</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Avatar,
  User,
  Reading,
  DataAnalysis,
  Document,
  DocumentCopy,
  Checked,
  Select
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)

// 统计数据
const stats = ref({
  teacherCount: 0,
  studentCount: 0,
  courseCount: 0,
  employmentRate: 0,
  pendingReports: 0,
  seekingStudents: 0,
  pendingOffers: 0,
  newStudents: 0
})

// 最新日报
const recentReports = ref([])

// 最新职位
const recentPositions = ref([])

// 本周Offer
const weeklyOffers = ref([])

// 加载统计数据
const loadStats = async () => {
  try {
    loading.value = true

    // 调用Dashboard统计API
    const response: any = await request.get('/dashboard/college/stats')
    stats.value = {
      teacherCount: response.teacherCount || 0,
      studentCount: response.studentCount || 0,
      courseCount: response.courseCount || 0,
      employmentRate: response.employmentRate || 0,
      pendingReports: response.pendingReports || 0,
      seekingStudents: response.seekingStudents || 0,
      pendingOffers: response.pendingOffers || 0,
      newStudents: response.newStudents || 0
    }

  } catch (error) {
    console.error('加载统计数据失败', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 加载最新日报
const loadRecentReports = async () => {
  try {
    const response: any = await request.get('/dashboard/college/daily-reports/recent', {
      params: { limit: 5 }
    })
    recentReports.value = response || []
  } catch (error) {
    console.error('加载日报失败', error)
    recentReports.value = []
  }
}

// 加载最新职位
const loadRecentPositions = async () => {
  try {
    const response: any = await request.get('/position/page', {
      params: { current: 1, size: 5 }
    })
    recentPositions.value = response.records || []
  } catch (error) {
    console.error('加载职位失败', error)
    recentPositions.value = []
  }
}

// 加载本周Offer
const loadWeeklyOffers = async () => {
  try {
    // 获取本周的Offer
    const response: any = await request.get('/offer/page', {
      params: { current: 1, size: 10 }
    })
    weeklyOffers.value = response.records || []
  } catch (error) {
    console.error('加载Offer失败', error)
    weeklyOffers.value = []
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 日报状态类型
const getReportStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    submitted: 'warning',
    reviewed: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getReportStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    submitted: '待审阅',
    reviewed: '已审阅',
    rejected: '已退回'
  }
  return textMap[status] || status
}

// 导航方法
const goToTeachers = () => router.push('/teaching/teachers')
const goToCourses = () => router.push('/teaching/courses')
const goToStudents = () => router.push('/teaching/students')
const goToDailyReports = () => router.push('/teaching/daily-reports')
const goToStudentProfile = () => router.push('/teaching/student-profile')
const goToStatistics = () => router.push('/employment/statistics')
const goToPositions = () => router.push('/employment/position-publishing')

const viewPositionDetail = (position: any) => {
  ElMessage.info(`查看职位：${position.positionName}`)
}

onMounted(() => {
  loadStats()
  loadRecentReports()
  loadRecentPositions()
  loadWeeklyOffers()
})
</script>

<style lang="scss" scoped>
.college-head-dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;

  .stat-card {
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
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
          line-height: 1;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
}

.content-row {
  .action-card,
  .report-card,
  .positions-card,
  .offers-card {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.action-card {
  .quick-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    .el-button {
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }
}

.positions-card {
  .position-list {
    min-height: 200px;

    .empty-state {
      padding: 20px 0;
    }

    .position-item {
      padding: 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: #f5f7fa;
      }

      &:not(:last-child) {
        border-bottom: 1px solid #ebeef5;
      }

      .position-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .position-name {
          font-weight: 500;
          color: #303133;
          font-size: 14px;
        }
      }

      .position-company {
        font-size: 12px;
        color: #909399;
        margin-bottom: 6px;
      }

      .position-salary {
        font-size: 13px;
        color: #67c23a;
        font-weight: 500;
      }
    }
  }
}

.offers-card {
  .offer-list {
    min-height: 200px;

    .empty-state {
      padding: 20px 0;
    }

    .offer-item {
      padding: 12px;
      border-radius: 8px;
      transition: background-color 0.2s;

      &:not(:last-child) {
        border-bottom: 1px solid #ebeef5;
      }

      .offer-header {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 8px;

        .offer-info {
          flex: 1;

          .offer-student {
            font-weight: 500;
            color: #303133;
            font-size: 14px;
            margin-bottom: 4px;
          }

          .offer-company {
            font-size: 12px;
            color: #909399;
          }
        }
      }

      .offer-salary {
        font-size: 13px;
        color: #67c23a;
        font-weight: 500;
        text-align: right;
      }
    }
  }
}
</style>
