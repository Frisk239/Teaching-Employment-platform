<template>
  <div class="my-interviews">
    <!-- 统计卡片 -->
    <el-card shadow="never" class="stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon total">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总面试</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon upcoming">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.upcoming }}</div>
            <div class="stat-label">即将到来</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon completed">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.completed }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon passed">
            <el-icon><CircleCheckFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.passed }}</div>
            <div class="stat-label">已通过</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon failed">
            <el-icon><CircleCloseFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.failed }}</div>
            <div class="stat-label">未通过</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon cancelled">
            <el-icon><RemoveFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.cancelled }}</div>
            <div class="stat-label">已取消</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 搜索和筛选 -->
    <el-card shadow="never" class="filter-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="已安排" value="scheduled" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="结果">
          <el-select
            v-model="searchForm.result"
            placeholder="全部结果"
            clearable
            style="width: 150px"
          >
            <el-option label="待面试" value="pending" />
            <el-option label="通过" value="passed" />
            <el-option label="未通过" value="failed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 面试列表 -->
    <div v-loading="loading" class="interview-list">
      <el-empty v-if="tableData.length === 0" description="暂无面试记录" />

      <el-row v-else :gutter="16" class="cards-row">
        <el-col
          v-for="interview in tableData"
          :key="interview.id"
          :xs="24"
          :sm="12"
          :md="12"
          :lg="8"
          :xl="6"
        >
          <el-card shadow="hover" class="interview-card">
            <!-- 职位信息 -->
            <div class="card-header">
              <div class="position-name">{{ interview.positionName }}</div>
              <div class="interview-badges">
                <el-tag :type="getResultType(interview.result)" size="small">
                  {{ getResultLabel(interview.result) }}
                </el-tag>
                <el-tag :type="getStatusType(interview.status)" size="small">
                  {{ getStatusLabel(interview.status) }}
                </el-tag>
              </div>
            </div>

            <!-- 公司信息 -->
            <div class="company-info">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ interview.companyName }}</span>
            </div>

            <!-- 面试轮次 -->
            <div class="interview-round">
              <el-tag v-if="interview.round === 1" type="info" size="small">
                <el-icon><Star /></el-icon> 初试
              </el-tag>
              <el-tag v-else-if="interview.round === 2" type="warning" size="small">
                <el-icon><Star /></el-icon> 复试
              </el-tag>
              <el-tag v-else-if="interview.round === 3" type="success" size="small">
                <el-icon><Star /></el-icon> 终试
              </el-tag>
              <el-tag v-else type="info" size="small">
                第{{ interview.round }}轮
              </el-tag>
            </div>

            <!-- 面试类型和时间 -->
            <div class="interview-info">
              <div class="info-item">
                <el-icon><VideoCamera /></el-icon>
                <span>{{ getInterviewTypeLabel(interview.interviewType) }}</span>
              </div>
              <div class="info-item" v-if="interview.interviewTime">
                <el-icon><Calendar /></el-icon>
                <span>{{ formatDateTime(interview.interviewTime) }}</span>
              </div>
              <div class="info-item" v-if="interview.location">
                <el-icon><Location /></el-icon>
                <span>{{ interview.location }}</span>
              </div>
              <div class="info-item" v-if="interview.meetingLink">
                <el-icon><Link /></el-icon>
                <el-tooltip :content="interview.meetingLink" placement="top">
                  <span class="meeting-link">{{ interview.meetingLink }}</span>
                </el-tooltip>
              </div>
            </div>

            <!-- 面试官信息 -->
            <div class="interviewer-info" v-if="interview.interviewer">
              <el-icon><User /></el-icon>
              <span>面试官：{{ interview.interviewer }}</span>
              <span v-if="interview.interviewerContact" class="contact">
                ({{ interview.interviewerContact }})
              </span>
            </div>

            <!-- 成绩显示（已完成且有分数） -->
            <div class="interview-score" v-if="interview.status === 'completed' && interview.score !== null && interview.score !== undefined">
              <div class="score-label">面试评分</div>
              <div class="score-value" :class="{ 'pass': interview.score >= 60, 'fail': interview.score < 60 }">
                {{ interview.score }} 分
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-button
                type="primary"
                size="small"
                :icon="View"
                @click="handleViewDetail(interview)"
              >
                查看详情
              </el-button>
              <el-button
                v-if="interview.meetingLink && interview.status === 'scheduled'"
                type="success"
                size="small"
                :icon="VideoCamera"
                @click="handleJoinMeeting(interview)"
              >
                加入会议
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 -->
      <el-pagination
        v-if="tableData.length > 0"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="面试详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentInterview" class="detail-content">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="应聘职位">
              {{ currentInterview.positionName }}
            </el-descriptions-item>
            <el-descriptions-item label="企业名称">
              {{ currentInterview.companyName }}
            </el-descriptions-item>
            <el-descriptions-item label="面试轮次">
              <el-tag v-if="currentInterview.round === 1" type="info" size="small">初试</el-tag>
              <el-tag v-else-if="currentInterview.round === 2" type="warning" size="small">复试</el-tag>
              <el-tag v-else-if="currentInterview.round === 3" type="success" size="small">终试</el-tag>
              <el-tag v-else type="info" size="small">第{{ currentInterview.round }}轮</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="面试类型">
              <el-tag v-if="currentInterview.interviewType === 'online'" type="primary" size="small">线上面试</el-tag>
              <el-tag v-else-if="currentInterview.interviewType === 'offline'" type="success" size="small">现场面试</el-tag>
              <el-tag v-else-if="currentInterview.interviewType === 'phone'" type="warning" size="small">电话面试</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="面试状态">
              <el-tag :type="getStatusType(currentInterview.status)">
                {{ getStatusLabel(currentInterview.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="面试结果">
              <el-tag :type="getResultType(currentInterview.result)">
                {{ getResultLabel(currentInterview.result) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="面试时间" :span="2">
              {{ currentInterview.interviewTime ? formatDateTime(currentInterview.interviewTime) : '未安排' }}
            </el-descriptions-item>
            <el-descriptions-item label="面试地点" :span="2" v-if="currentInterview.location">
              {{ currentInterview.location }}
            </el-descriptions-item>
            <el-descriptions-item label="会议链接" :span="2" v-if="currentInterview.meetingLink">
              <el-input v-model="currentInterview.meetingLink" readonly>
                <template #append>
                  <el-button @click="handleOpenMeetingLink(currentInterview.meetingLink!)">打开链接</el-button>
                </template>
              </el-input>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 面试官信息 -->
        <div class="detail-section" v-if="currentInterview.interviewer">
          <h4 class="section-title">面试官信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="面试官">
              {{ currentInterview.interviewer }}
            </el-descriptions-item>
            <el-descriptions-item label="联系方式" v-if="currentInterview.interviewerContact">
              {{ currentInterview.interviewerContact }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 面试成绩 -->
        <div class="detail-section" v-if="currentInterview.status === 'completed' && currentInterview.score !== null && currentInterview.score !== undefined">
          <h4 class="section-title">面试成绩</h4>
          <div class="score-detail">
            <div class="score-circle">
              <el-progress
                type="circle"
                :percentage="currentInterview.score"
                :color="getScoreColor(currentInterview.score)"
                :width="120"
              >
                <template #default>
                  <div class="score-in-circle">
                    <div class="score-num">{{ currentInterview.score }}</div>
                    <div class="score-total">分</div>
                  </div>
                </template>
              </el-progress>
            </div>
            <div class="score-info">
              <div class="score-status" :class="{ 'pass': currentInterview.score >= 60, 'fail': currentInterview.score < 60 }">
                {{ currentInterview.score >= 60 ? '通过' : '未通过' }}
              </div>
            </div>
          </div>
        </div>

        <!-- 评语 -->
        <div class="detail-section" v-if="currentInterview.comment">
          <h4 class="section-title">评语</h4>
          <el-alert type="info" :closable="false">
            {{ currentInterview.comment }}
          </el-alert>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentInterview && currentInterview.meetingLink && currentInterview.status === 'scheduled'"
          type="primary"
          @click="handleJoinMeeting(currentInterview!)"
        >
          加入会议
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Document,
  Clock,
  SuccessFilled,
  CircleCheckFilled,
  CircleCloseFilled,
  RemoveFilled,
  OfficeBuilding,
  Star,
  VideoCamera,
  Calendar,
  Location,
  Link,
  User,
  View
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import { interviewApi, type Interview } from '@/api/interview'

interface InterviewExt extends Interview {
  companyName?: string
}

interface Stats {
  total: number
  upcoming: number
  completed: number
  passed: number
  failed: number
  cancelled: number
}

const authStore = useAuthStore()
const loading = ref(false)
const tableData = ref<InterviewExt[]>([])
const detailDialogVisible = ref(false)
const currentInterview = ref<InterviewExt | null>(null)

// 学员ID
const userId = computed(() => authStore.user?.studentId)

// 统计数据
const stats = ref<Stats>({
  total: 0,
  upcoming: 0,
  completed: 0,
  passed: 0,
  failed: 0,
  cancelled: 0
})

// 搜索表单
const searchForm = reactive({
  status: '',
  result: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取数据
const fetchData = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录')
    return
  }

  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      studentId: userId.value
    }

    if (searchForm.status) {
      params.status = searchForm.status
    }
    if (searchForm.result) {
      params.result = searchForm.result
    }

    const response = await interviewApi.getPage(params) as any
    tableData.value = response.records || []
    pagination.total = response.total || 0
    updateStats()
  } catch (error) {
    console.error('获取面试列表失败:', error)
    ElMessage.error('获取面试列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.value = {
    total: tableData.value.length,
    upcoming: tableData.value.filter((i) => i.status === 'scheduled').length,
    completed: tableData.value.filter((i) => i.status === 'completed').length,
    passed: tableData.value.filter((i) => i.result === 'passed').length,
    failed: tableData.value.filter((i) => i.result === 'failed').length,
    cancelled: tableData.value.filter((i) => i.status === 'cancelled').length
  }
}

// 获取状态标签类型
const getStatusType = (status: string): string => {
  const map: Record<string, string> = {
    'scheduled': 'primary',
    'completed': 'success',
    'cancelled': 'info'
  }
  return map[status] || 'info'
}

// 获取状态标签文本
const getStatusLabel = (status: string): string => {
  const map: Record<string, string> = {
    'scheduled': '已安排',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return map[status] || status
}

// 获取结果标签类型
const getResultType = (result: string): string => {
  const map: Record<string, string> = {
    'pending': 'info',
    'passed': 'success',
    'failed': 'danger',
    'cancelled': 'warning'
  }
  return map[result] || 'info'
}

// 获取结果标签文本
const getResultLabel = (result: string): string => {
  const map: Record<string, string> = {
    'pending': '待面试',
    'passed': '通过',
    'failed': '未通过',
    'cancelled': '已取消'
  }
  return map[result] || result
}

// 获取面试类型标签
const getInterviewTypeLabel = (type: string): string => {
  const map: Record<string, string> = {
    'online': '线上面试',
    'offline': '现场面试',
    'phone': '电话面试'
  }
  return map[type] || type
}

// 格式化日期时间
const formatDateTime = (dateTime: string): string => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取成绩颜色
const getScoreColor = (score: number): string => {
  if (score >= 80) return '#67c23a'
  if (score >= 60) return '#409eff'
  return '#f56c6c'
}

// 加入会议
const handleJoinMeeting = (interview: InterviewExt) => {
  if (!interview.meetingLink) {
    ElMessage.warning('会议链接不存在')
    return
  }
  handleOpenMeetingLink(interview.meetingLink)
}

// 打开会议链接
const handleOpenMeetingLink = (url: string) => {
  window.open(url, '_blank')
}

// 查看详情
const handleViewDetail = (interview: InterviewExt) => {
  currentInterview.value = interview
  detailDialogVisible.value = true
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  searchForm.result = ''
  handleSearch()
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.my-interviews {
  padding: 20px;

  // 统计卡片
  .stats-card {
    margin-bottom: 20px;
    padding: 20px;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;

    @media (max-width: 992px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .stat-item {
    display: flex;
    align-items: center;
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    transition: all 0.3s;
    background: #fff;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
    }
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    margin-right: 16px;
    flex-shrink: 0;

    .el-icon {
      font-size: 24px;
    }

    &.total {
      background: #f0f2f5;
      color: #606266;
    }

    &.upcoming {
      background: #fef0e6;
      color: #e6a23c;
    }

    &.completed {
      background: #e1f3d8;
      color: #67c23a;
    }

    &.passed {
      background: #e1f3d8;
      color: #67c23a;
    }

    &.failed {
      background: #fee;
      color: #f56c6c;
    }

    &.cancelled {
      background: #f4f4f5;
      color: #909399;
    }
  }

  .stat-info {
    flex: 1;
    min-width: 0;
  }

  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1.2;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }

  // 筛选卡片
  .filter-card {
    margin-bottom: 20px;
  }

  .search-form {
    margin-bottom: 0;
  }

  // 面试卡片列表
  .interview-list {
    min-height: 400px;
  }

  .cards-row {
    margin-bottom: 20px;
  }

  .interview-card {
    border-radius: 8px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;

      .position-name {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-right: 10px;
      }

      .interview-badges {
        display: flex;
        flex-direction: column;
        gap: 4px;
        flex-shrink: 0;
      }
    }

    .company-info {
      display: flex;
      align-items: center;
      color: #606266;
      margin-bottom: 8px;
      font-size: 14px;

      .el-icon {
        margin-right: 5px;
      }
    }

    .interview-round {
      margin-bottom: 12px;

      .el-tag {
        .el-icon {
          margin-right: 4px;
        }
      }
    }

    .interview-info {
      display: flex;
      flex-direction: column;
      gap: 6px;
      margin-bottom: 12px;

      .info-item {
        display: flex;
        align-items: center;
        font-size: 13px;
        color: #606266;

        .el-icon {
          margin-right: 5px;
          flex-shrink: 0;
        }

        .meeting-link {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: #409eff;
        }
      }
    }

    .interviewer-info {
      display: flex;
      align-items: center;
      color: #909399;
      font-size: 13px;
      margin-bottom: 12px;
      padding: 8px;
      background: #f5f7fa;
      border-radius: 4px;

      .el-icon {
        margin-right: 5px;
      }

      .contact {
        margin-left: 8px;
        color: #606266;
      }
    }

    .interview-score {
      padding: 12px;
      background: #f5f7fa;
      border-radius: 8px;
      margin-bottom: 12px;

      .score-label {
        font-size: 12px;
        color: #909399;
        margin-bottom: 4px;
      }

      .score-value {
        font-size: 24px;
        font-weight: bold;

        &.pass {
          color: #67c23a;
        }

        &.fail {
          color: #f56c6c;
        }
      }
    }

    .card-actions {
      display: flex;
      gap: 8px;
      padding-top: 12px;
      border-top: 1px solid #ebeef5;

      .el-button {
        flex: 1;
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  // 详情对话框
  .detail-content {
    .detail-section {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      .section-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 12px;
        padding-bottom: 8px;
        border-bottom: 2px solid #409eff;
      }
    }

    .score-detail {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 40px;
      padding: 20px;

      .score-in-circle {
        text-align: center;

        .score-num {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }

        .score-total {
          font-size: 14px;
          color: #909399;
        }
      }

      .score-info {
        text-align: center;

        .score-status {
          font-size: 20px;
          font-weight: bold;

          &.pass {
            color: #67c23a;
          }

          &.fail {
            color: #f56c6c;
          }
        }
      }
    }
  }
}
</style>
