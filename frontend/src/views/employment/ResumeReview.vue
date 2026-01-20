<template>
  <div class="resume-review-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">简历浏览</h1>
      <p class="page-subtitle">查看各岗位收到的简历投递</p>
    </div>

    <!-- 加载状态 -->
    <div v-loading="loading" class="content-container">
      <!-- 空状态 -->
      <el-empty v-if="!loading && positionApplications.length === 0" description="暂无简历投递">
        <el-button type="primary" @click="loadData">刷新数据</el-button>
      </el-empty>

      <!-- 岗位卡片列表 -->
      <div v-else class="position-cards">
        <div
          v-for="item in positionApplications"
          :key="item.positionId"
          class="position-card"
        >
          <!-- 卡片头部 -->
          <div class="card-header">
            <div class="position-info">
              <h3 class="position-name">{{ item.positionName }}</h3>
              <el-tag type="success" size="small">招聘中</el-tag>
            </div>
            <div class="application-count">
              <el-badge :value="item.count" :max="99" type="primary">
                <el-icon><Document /></el-icon>
              </el-badge>
              <span class="count-text">收到 {{ item.count }} 份简历</span>
            </div>
          </div>

          <!-- 简历列表 -->
          <div class="applications-list">
            <div
              v-for="application in item.applications"
              :key="application.id"
              class="application-item"
            >
              <div class="applicant-info">
                <el-avatar :size="40" :icon="UserFilled" />
                <div class="info-content">
                  <div class="applicant-name">{{ application.studentName }}</div>
                  <div class="apply-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatTime(application.applicationTime) }}
                  </div>
                </div>
              </div>

              <div class="application-actions">
                <el-button type="primary" size="small" @click="handleViewResume(application)">
                  <el-icon><View /></el-icon>
                  查看简历
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Document,
  UserFilled,
  Clock,
  View,
} from '@element-plus/icons-vue'
import { applicationApi } from '@/api/application'
import { useAuthStore } from '@/stores'
import { getPositionPageApi } from '@/api/position'
import dayjs from 'dayjs'

interface Application {
  id?: number
  studentId?: number
  positionId?: number
  studentName?: string
  positionName?: string
  companyName?: string
  status?: string
  applicationTime?: string
  resumeUrl?: string
}

interface PositionApplications {
  positionId: number
  positionName: string
  count: number
  applications: Application[]
}

const authStore = useAuthStore()
const loading = ref(false)
const positionApplications = ref<PositionApplications[]>([])

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 获取企业的所有职位
    const positionsRes: any = await getPositionPageApi({
      current: 1,
      size: 1000,
      companyId: authStore.companyId,
      status: 'active',
    })

    const positions = positionsRes.records || []

    if (positions.length === 0) {
      positionApplications.value = []
      return
    }

    // 获取所有职位的投递申请
    const applicationsPromises = positions.map((position: any) =>
      applicationApi.getPage({
        current: 1,
        size: 100,
        positionId: position.id,
        companyId: authStore.companyId,
      })
    )

    const results = await Promise.all(applicationsPromises)

    // 组装数据
    positionApplications.value = positions
      .map((position: any, index: number) => {
        const appResult = results[index] as any
        const applications = appResult?.records || []

        return {
          positionId: position.id,
          positionName: position.positionName,
          count: applications.length,
          applications: applications,
        }
      })
      .filter((item: PositionApplications) => item.count > 0)
      .sort((a: PositionApplications, b: PositionApplications) => b.count - a.count)
  } catch (error: any) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 查看简历 - 直接新窗口打开
const handleViewResume = (application: Application) => {
  if (application.resumeUrl) {
    window.open(application.resumeUrl, '_blank')
  } else {
    ElMessage.warning('该学生暂未上传简历')
  }
}

// 格式化时间
const formatTime = (time?: string): string => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status?: string) => {
  const map: Record<string, string> = {
    pending: 'info',
    screened: 'warning',
    test_passed: 'success',
    test_failed: 'danger',
    interview_passed: 'success',
    interview_failed: 'danger',
    offered: 'success',
    hired: 'success',
    rejected: 'danger',
  }
  return map[status || ''] || 'info'
}

// 获取状态文本
const getStatusText = (status?: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    screened: '已筛选',
    test_passed: '笔试通过',
    test_failed: '笔试未通过',
    interview_passed: '面试通过',
    interview_failed: '面试未通过',
    offered: '已发Offer',
    hired: '已录用',
    rejected: '已拒绝',
  }
  return map[status || ''] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.resume-review-page {
  padding: 2rem;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 2rem;

  .page-title {
    font-size: 2rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: var(--el-text-color-primary);
  }

  .page-subtitle {
    font-size: 1rem;
    color: var(--el-text-color-secondary);
    margin: 0;
  }
}

.content-container {
  min-height: 400px;
}

.position-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 1.5rem;
}

.position-card {
  background: white;
  border: 1px solid var(--el-border-color);
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid var(--el-border-color-lighter);

    .position-info {
      display: flex;
      align-items: center;
      gap: 0.75rem;

      .position-name {
        font-size: 1.25rem;
        font-weight: 600;
        margin: 0;
        color: var(--el-text-color-primary);
      }
    }

    .application-count {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      color: var(--el-text-color-secondary);

      .count-text {
        font-size: 0.875rem;
      }
    }
  }

  .applications-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .application-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background: var(--el-fill-color-lighter);
    border-radius: 8px;
    transition: background 0.2s;

    &:hover {
      background: var(--el-fill-color-light);
    }

    .applicant-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex: 1;

      .info-content {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;

        .applicant-name {
          font-size: 1rem;
          font-weight: 500;
          color: var(--el-text-color-primary);
        }

        .apply-time {
          display: flex;
          align-items: center;
          gap: 0.25rem;
          font-size: 0.875rem;
          color: var(--el-text-color-secondary);
        }
      }
    }

    .application-actions {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
  }
}
</style>
