<template>
  <div class="enterprise-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><Briefcase /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activePositions || 0 }}</div>
              <div class="stat-label">在招职位</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.talentPoolCount || 0 }}</div>
              <div class="stat-label">人才库</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingApplications || 0 }}</div>
              <div class="stat-label">待处理申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="28"><Checked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.offersSent || 0 }}</div>
              <div class="stat-label">已发Offer</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区 -->
    <el-row :gutter="20" class="content-row">
      <!-- 左侧：快捷操作 + 在招职位 -->
      <el-col :span="16">
        <!-- 快捷操作 -->
        <el-card shadow="hover" class="action-card">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="goToPositions">
              <el-icon><Briefcase /></el-icon>
              发布职位
            </el-button>
            <el-button type="success" @click="goToTalentPool">
              <el-icon><Star /></el-icon>
              人才库
            </el-button>
            <el-button type="warning" @click="goToApplications">
              <el-icon><Document /></el-icon>
              查看申请
            </el-button>
            <el-button type="danger" @click="goToInterviews">
              <el-icon><ChatDotRound /></el-icon>
              面试安排
            </el-button>
            <el-button type="info" @click="goToOffers">
              <el-icon><Checked /></el-icon>
              Offer管理
            </el-button>
          </div>
        </el-card>

        <!-- 在招职位列表 -->
        <el-card shadow="hover" class="position-card">
          <template #header>
            <div class="card-header">
              <span>在招职位</span>
              <el-button link type="primary" @click="goToPositions">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentPositions" style="width: 100%" v-loading="loading" stripe>
            <el-table-column prop="positionName" label="职位名称" width="180" />
            <el-table-column prop="city" label="工作地点" width="120" />
            <el-table-column label="薪资范围" width="150">
              <template #default="{ row }">
                <span v-if="row.salaryMin || row.salaryMax">
                  {{ row.salaryMin }}-{{ row.salaryMax }} / {{ row.salaryUnit === 'month' ? '月' : '年' }}
                </span>
                <span v-else style="color: #909399">面议</span>
              </template>
            </el-table-column>
            <el-table-column prop="applicationCount" label="收到申请" width="100" align="center">
              <template #default="{ row }">
                <el-tag type="primary" size="small">{{ row.applicationCount || 0 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="viewPosition(row)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：最新申请 + 待办事项 -->
      <el-col :span="8">
        <!-- 最新申请 -->
        <el-card shadow="hover" class="application-card">
          <template #header>
            <div class="card-header">
              <span>最新申请</span>
              <el-badge :value="stats.pendingApplications" :hidden="!stats.pendingApplications">
                <el-button link type="primary" @click="goToApplications">更多</el-button>
              </el-badge>
            </div>
          </template>
          <div class="application-list" v-loading="loading">
            <div v-if="recentApplications.length === 0" class="empty-state">
              <el-empty description="暂无新申请" :image-size="60" />
            </div>
            <div v-else>
              <div
                v-for="app in recentApplications"
                :key="app.id"
                class="application-item"
                @click="viewApplication(app)"
              >
                <div class="applicant-info">
                  <el-avatar :size="36">{{ app.studentName?.charAt(0) }}</el-avatar>
                  <div class="info-text">
                    <div class="name">{{ app.studentName }}</div>
                    <div class="detail">申请：{{ app.positionName }}</div>
                    <div class="time">{{ formatTime(app.createTime) }}</div>
                  </div>
                </div>
                <el-tag :type="getApplicationStatusType(app.status)" size="small">
                  {{ getApplicationStatusText(app.status) }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 待办事项 -->
        <el-card shadow="hover" class="todo-card">
          <template #header>
            <span>待办事项</span>
          </template>
          <div class="todo-list">
            <div v-if="stats.pendingApplications > 0" class="todo-item" @click="goToApplications">
              <el-icon class="todo-icon" color="#409eff"><Document /></el-icon>
              <span class="todo-text">{{ stats.pendingApplications }} 个申请待处理</span>
            </div>
            <div v-if="stats.pendingInterviews > 0" class="todo-item" @click="goToInterviews">
              <el-icon class="todo-icon" color="#e6a23c"><ChatDotRound /></el-icon>
              <span class="todo-text">{{ stats.pendingInterviews }} 个面试待安排</span>
            </div>
            <div v-if="stats.offersSent > 0" class="todo-item" @click="goToOffers">
              <el-icon class="todo-icon" color="#67c23a"><Checked /></el-icon>
              <span class="todo-text">{{ stats.offersSent }} 个Offer待确认</span>
            </div>
            <div v-if="stats.newTalents > 0" class="todo-item" @click="goToTalentPool">
              <el-icon class="todo-icon" color="#f56c6c"><Star /></el-icon>
              <span class="todo-text">{{ stats.newTalents }} 个新人才待联系</span>
            </div>
            <div v-if="!hasTodos" class="empty-todo">
              <el-icon color="#67c23a" :size="32"><Select /></el-icon>
              <span>暂无待办事项</span>
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
  Briefcase,
  Star,
  Document,
  Checked,
  ChatDotRound,
  Select
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import request from '@/utils/request'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)

// 统计数据
const stats = ref({
  activePositions: 0,
  talentPoolCount: 0,
  pendingApplications: 0,
  offersSent: 0,
  pendingInterviews: 0,
  newTalents: 0
})

// 是否有待办事项
const hasTodos = computed(() => {
  return stats.value.pendingApplications > 0 ||
         stats.value.pendingInterviews > 0 ||
         stats.value.offersSent > 0 ||
         stats.value.newTalents > 0
})

// 最近职位
const recentPositions = ref([])

// 最新申请
const recentApplications = ref([])

// 加载统计数据
const loadStats = async () => {
  try {
    const companyId = authStore.user?.companyId
    if (!companyId) {
      console.warn('未找到企业ID')
      return
    }

    loading.value = true

    // 并行请求所有统计数据
    const [positionsRes, talentPoolRes, applicationsRes] = await Promise.all([
      request.get('/position/page', {
        params: { companyId, status: 'active', current: 1, size: 1 }
      }).catch(() => ({ total: 0 })),
      request.get(`/talent-pool/stats/company/${companyId}`)
        .catch(() => ({ totalCount: 0, activeCount: 0, contactedCount: 0 })),
      request.get('/application/page', {
        params: { companyId, current: 1, size: 1 }
      }).catch(() => ({ total: 0 }))
    ])

    stats.value.activePositions = positionsRes.total || 0
    stats.value.talentPoolCount = talentPoolRes.totalCount || 0
    stats.value.pendingApplications = applicationsRes.total || 0

  } catch (error) {
    console.error('加载统计数据失败', error)
  } finally {
    loading.value = false
  }
}

// 加载最近职位
const loadRecentPositions = async () => {
  try {
    const companyId = authStore.user?.companyId
    if (!companyId) return

    const response: any = await request.get('/position/page', {
      params: {
        companyId,
        current: 1,
        size: 5
      }
    })

    recentPositions.value = response.records || []
  } catch (error) {
    console.error('加载职位失败', error)
  }
}

// 加载最新申请
const loadRecentApplications = async () => {
  try {
    const companyId = authStore.user?.companyId
    if (!companyId) return

    const response: any = await request.get('/application/page', {
      params: {
        companyId,
        current: 1,
        size: 5
      }
    })

    recentApplications.value = response.records || []
  } catch (error) {
    console.error('加载申请失败', error)
  }
}

// 状态类型映射
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    active: 'success',
    paused: 'warning',
    closed: 'info',
    draft: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    active: '招聘中',
    paused: '已暂停',
    closed: '已关闭',
    draft: '草稿'
  }
  return textMap[status] || status
}

const getApplicationStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    pending: 'warning',
    screened: 'primary',
    test_passed: 'success',
    test_failed: 'danger',
    interview_passed: 'success',
    interview_failed: 'danger',
    offered: 'success',
    hired: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getApplicationStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待处理',
    screened: '已筛选',
    test_passed: '笔试通过',
    test_failed: '笔试未过',
    interview_passed: '面试通过',
    interview_failed: '面试未过',
    offered: '已发Offer',
    hired: '已入职',
    rejected: '已拒绝'
  }
  return textMap[status] || status
}

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('MM-DD HH:mm')
}

// 导航方法
const goToPositions = () => router.push('/employment/enterprise-positions')
const goToTalentPool = () => router.push('/employment/talent-pool')
const goToApplications = () => router.push('/employment/applications')
const goToInterviews = () => router.push('/employment/interviews')
const goToOffers = () => router.push('/employment/enterprise-offers')

const viewPosition = (row: any) => {
  ElMessage.info('职位详情功能开发中')
}

const viewApplication = (app: any) => {
  router.push(`/employment/applications?id=${app.id}`)
}

onMounted(() => {
  loadStats()
  loadRecentPositions()
  loadRecentApplications()
})
</script>

<style lang="scss" scoped>
.enterprise-dashboard {
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
  .position-card,
  .application-card,
  .todo-card {
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

.application-card {
  .application-list {
    min-height: 200px;

    .empty-state {
      padding: 20px 0;
    }

    .application-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
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

      .applicant-info {
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

          .time {
            font-size: 11px;
            color: #909399;
          }
        }
      }
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
</style>
