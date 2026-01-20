<template>
  <div class="guidance-recommendation">
    <el-row :gutter="20">
      <!-- 左侧：指导记录 -->
      <el-col :span="12">
        <el-card shadow="never" class="guidance-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><ChatDotRound /></el-icon>
                就业指导记录
              </span>
              <el-badge :value="guidanceCount" class="badge" type="primary" />
            </div>
          </template>

          <div v-loading="guidanceLoading">
            <el-timeline v-if="guidanceList.length > 0">
              <el-timeline-item
                v-for="(guidance, index) in guidanceList"
                :key="index"
                :timestamp="formatDateTime(guidance.guidanceDate)"
                placement="top"
              >
                <el-card>
                  <div class="guidance-header">
                    <el-tag :type="getGuidanceTypeColor(guidance.guidanceType)" size="small">
                      {{ guidance.guidanceTypeName || guidance.guidanceType }}
                    </el-tag>
                    <span class="guidance-author">{{ guidance.teacherName || '教师' }}</span>
                  </div>
                  <p class="guidance-content">{{ guidance.content }}</p>
                  <div v-if="guidance.nextAction" class="guidance-next">
                    <el-icon><Warning /></el-icon>
                    <span>后续跟进：{{ guidance.nextAction }}</span>
                  </div>
                  <div v-if="guidance.location" class="guidance-location">
                    <el-icon><Location /></el-icon>
                    <span>{{ guidance.location }}</span>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无指导记录" :image-size="80" />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：职位推荐 -->
      <el-col :span="12">
        <el-card shadow="never" class="recommendation-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Briefcase /></el-icon>
                职位推荐
              </span>
              <el-badge :value="recommendationCount" class="badge" type="success" />
            </div>
          </template>

          <div v-loading="recommendationLoading">
            <div v-if="recommendationList.length > 0" class="recommendation-list">
              <el-card
                v-for="recommendation in recommendationList"
                :key="recommendation.id"
                class="recommendation-item"
                :class="{ 'unread': recommendation.status === 'pending' }"
                shadow="hover"
              >
                <div class="recommendation-header">
                  <div class="position-info">
                    <h4>{{ recommendation.positionName }}</h4>
                    <p class="company-name">{{ recommendation.companyName }}</p>
                  </div>
                  <el-tag :type="getRecommendationStatusType(recommendation.status)" size="small">
                    {{ getRecommendationStatusText(recommendation.status) }}
                  </el-tag>
                </div>

                <div v-if="recommendation.reason" class="recommendation-reason">
                  <strong>推荐理由：</strong>{{ recommendation.reason }}
                </div>

                <div v-if="recommendation.remark" class="recommendation-remark">
                  <el-icon><InfoFilled /></el-icon>
                  {{ recommendation.remark }}
                </div>

                <div class="recommendation-footer">
                  <span class="recommendation-time">
                    {{ formatDateTime(recommendation.createTime) }}
                  </span>
                  <div class="recommendation-actions">
                    <el-button
                      v-if="recommendation.status === 'pending'"
                      type="primary"
                      size="small"
                      @click="markAsViewed(recommendation.id)"
                    >
                      标记已读
                    </el-button>
                    <el-button
                      v-if="recommendation.status === 'viewed'"
                      type="success"
                      size="small"
                      @click="markAsApplied(recommendation.id)"
                    >
                      已投递
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
            <el-empty v-else description="暂无职位推荐" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound,
  Briefcase,
  Warning,
  Location,
  InfoFilled
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  getRecommendationsByStudentApi,
  markRecommendationAsViewedApi,
  updateRecommendationStatusApi
} from '@/api/guidance'
import { getGuidanceListByStudentApi } from '@/api/guidance'

const authStore = useAuthStore()

const guidanceLoading = ref(false)
const recommendationLoading = ref(false)
const guidanceList = ref<any[]>([])
const recommendationList = ref<any[]>([])

const guidanceCount = computed(() => guidanceList.value.length)
const recommendationCount = computed(() => recommendationList.value.filter(r => r.status === 'pending').length)

// 加载指导记录
const loadGuidances = async () => {
  if (!authStore.studentId) {
    return
  }

  try {
    guidanceLoading.value = true
    const data: any = await getGuidanceListByStudentApi(authStore.studentId)
    guidanceList.value = data || []
  } catch (error) {
    console.error('加载指导记录失败', error)
    ElMessage.error('加载指导记录失败')
  } finally {
    guidanceLoading.value = false
  }
}

// 加载职位推荐
const loadRecommendations = async () => {
  if (!authStore.studentId) {
    return
  }

  try {
    recommendationLoading.value = true
    const data: any = await getRecommendationsByStudentApi(authStore.studentId)
    recommendationList.value = data.records || []
  } catch (error) {
    console.error('加载职位推荐失败', error)
    ElMessage.error('加载职位推荐失败')
  } finally {
    recommendationLoading.value = false
  }
}

// 标记为已查看
const markAsViewed = async (id: number) => {
  try {
    await markRecommendationAsViewedApi(id)
    ElMessage.success('标记成功')
    // 重新加载推荐列表
    await loadRecommendations()
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 标记为已投递
const markAsApplied = async (id: number) => {
  try {
    await updateRecommendationStatusApi(id, 'applied')
    ElMessage.success('恭喜！已标记为已投递')
    // 重新加载推荐列表
    await loadRecommendations()
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return minutes === 0 ? '刚刚' : `${minutes}分钟前`
    }
    return `${hours}小时前`
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

// 获取指导类型颜色
const getGuidanceTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    career_planning: 'primary',
    resume_guidance: 'success',
    interview_guidance: 'warning',
    skill_improvement: 'info',
    psychological_counseling: 'danger',
    other: ''
  }
  return colorMap[type] || ''
}

// 获取推荐状态类型
const getRecommendationStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    pending: 'danger',
    viewed: 'warning',
    applied: 'success',
    rejected: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取推荐状态文本
const getRecommendationStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待查看',
    viewed: '已查看',
    applied: '已投递',
    rejected: '已拒绝'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadGuidances()
  loadRecommendations()
})
</script>

<style lang="scss" scoped>
.guidance-recommendation {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 500;
    }
  }
}

.guidance-card,
.recommendation-card {
  min-height: 600px;

  :deep(.el-card__body) {
    max-height: 750px;
    overflow-y: auto;
  }
}

.guidance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .guidance-author {
    font-size: 12px;
    color: #909399;
  }
}

.guidance-content {
  color: #606266;
  margin: 8px 0;
  line-height: 1.6;
}

.guidance-next {
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
  color: #409eff;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
}

.guidance-location {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
}

.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recommendation-item {
  border-left: 3px solid transparent;
  transition: all 0.3s;

  &.unread {
    border-left-color: #409eff;
    background: #ecf5ff;
  }

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;

  .position-info {
    h4 {
      margin: 0 0 4px 0;
      font-size: 15px;
      color: #303133;
    }

    .company-name {
      margin: 0;
      font-size: 13px;
      color: #909399;
    }
  }
}

.recommendation-reason {
  padding: 8px 12px;
  background: #f0f9ff;
  border-left: 3px solid #409eff;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.6;
}

.recommendation-remark {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: flex-start;
  gap: 4px;
  margin-bottom: 12px;
}

.recommendation-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;

  .recommendation-time {
    font-size: 12px;
    color: #909399;
  }

  .recommendation-actions {
    display: flex;
    gap: 8px;
  }
}
</style>
