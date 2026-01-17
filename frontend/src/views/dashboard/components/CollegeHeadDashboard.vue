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

      <!-- 右侧：待办事项 + 需要关注的学员 -->
      <el-col :span="8">
        <!-- 待办事项 -->
        <el-card shadow="hover" class="todo-card">
          <template #header>
            <span>待办事项</span>
          </template>
          <div class="todo-list">
            <div v-if="stats.pendingReports > 0" class="todo-item" @click="goToDailyReports">
              <el-icon class="todo-icon" color="#409eff"><DocumentCopy /></el-icon>
              <span class="todo-text">{{ stats.pendingReports }} 份日报待审阅</span>
            </div>
            <div v-if="stats.seekingStudents > 0" class="todo-item" @click="goToStudentProfile">
              <el-icon class="todo-icon" color="#67c23a"><User /></el-icon>
              <span class="todo-text">{{ stats.seekingStudents }} 名学员正在求职</span>
            </div>
            <div v-if="stats.pendingOffers > 0" class="todo-item" @click="goToStatistics">
              <el-icon class="todo-icon" color="#e6a23c"><Checked /></el-icon>
              <span class="todo-text">{{ stats.pendingOffers }} 个Offer待确认</span>
            </div>
            <div v-if="stats.newStudents > 0" class="todo-item" @click="goToStudents">
              <el-icon class="todo-icon" color="#f56c6c"><Avatar /></el-icon>
              <span class="todo-text">{{ stats.newStudents }} 名新学员待安排</span>
            </div>
            <div v-if="!hasTodos" class="empty-todo">
              <el-icon color="#67c23a" :size="32"><Select /></el-icon>
              <span>暂无待办事项</span>
            </div>
          </div>
        </el-card>

        <!-- 需要关注的学员 -->
        <el-card shadow="hover" class="attention-card">
          <template #header>
            <div class="card-header">
              <span>需要关注的学员</span>
              <el-button link type="primary" @click="goToStudentProfile">更多</el-button>
            </div>
          </template>
          <div class="student-list" v-loading="loading">
            <div v-if="attentionStudents.length === 0" class="empty-state">
              <el-empty description="暂无数据" :image-size="60" />
            </div>
            <div v-else>
              <div
                v-for="student in attentionStudents"
                :key="student.id"
                class="student-item"
                @click="viewStudentDetail(student)"
              >
                <div class="student-info">
                  <el-avatar :size="36">{{ student.realName?.charAt(0) }}</el-avatar>
                  <div class="info-text">
                    <div class="name">{{ student.realName }}</div>
                    <div class="detail">{{ student.major }}</div>
                    <div class="status">
                      <el-tag :type="getStudentStatusType(student.seekingStatus)" size="small">
                        {{ getStudentStatusText(student.seekingStatus) }}
                      </el-tag>
                    </div>
                  </div>
                </div>
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

// 是否有待办事项
const hasTodos = computed(() => {
  return stats.value.pendingReports > 0 ||
         stats.value.seekingStudents > 0 ||
         stats.value.pendingOffers > 0 ||
         stats.value.newStudents > 0
})

// 最新日报
const recentReports = ref([])

// 需要关注的学员
const attentionStudents = ref([])

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

// 加载需要关注的学员
const loadAttentionStudents = async () => {
  try {
    const response: any = await request.get('/dashboard/college/students/attention', {
      params: { limit: 10 }
    })
    attentionStudents.value = response || []
  } catch (error) {
    console.error('加载学员失败', error)
    attentionStudents.value = []
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

// 学员状态类型
const getStudentStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    employed: 'success',
    seeking: 'warning',
    actively_seeking: 'danger',
    admitted: 'info'
  }
  return typeMap[status] || 'info'
}

const getStudentStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    employed: '已就业',
    seeking: '求职中',
    actively_seeking: '正在求职',
    admitted: '已录取'
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

const viewStudentDetail = (student: any) => {
  router.push('/teaching/student-profile')
  ElMessage.info('查看学员详情')
}

onMounted(() => {
  loadStats()
  loadRecentReports()
  loadAttentionStudents()
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
  .todo-card,
  .attention-card {
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

.todo-card {
  .todo-list {
    min-height: 150px;

    .todo-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background-color: #f5f7fa;
      }

      &:not(:last-child) {
        border-bottom: 1px solid #ebeef5;
      }

      .todo-icon {
        font-size: 20px;
        flex-shrink: 0;
      }

      .todo-text {
        flex: 1;
        color: #606266;
        font-size: 14px;
      }
    }

    .empty-todo {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      padding: 30px 0;
      color: #67c23a;

      span {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.attention-card {
  .student-list {
    min-height: 200px;

    .empty-state {
      padding: 20px 0;
    }

    .student-item {
      display: flex;
      align-items: center;
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

      .student-info {
        display: flex;
        align-items: center;
        gap: 12px;
        flex: 1;
        min-width: 0;

        .info-text {
          flex: 1;
          min-width: 0;

          .name {
            font-weight: 500;
            color: #303133;
            margin-bottom: 4px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .detail {
            font-size: 12px;
            color: #606266;
            margin-bottom: 4px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .status {
            font-size: 11px;
          }
        }
      }
    }
  }
}
</style>
