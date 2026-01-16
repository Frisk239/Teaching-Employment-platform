<template>
  <div class="my-applications">
    <!-- 统计卡片 -->
    <el-card shadow="never" class="stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon total">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总申请</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon pending">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon screening">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.screening }}</div>
            <div class="stat-label">筛选中</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon test">
            <el-icon><Edit /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.test }}</div>
            <div class="stat-label">笔试中</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon interview">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.interview }}</div>
            <div class="stat-label">面试中</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon offer">
            <el-icon><Present /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.offer }}</div>
            <div class="stat-label">已发Offer</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon hired">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.hired }}</div>
            <div class="stat-label">已入职</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon rejected">
            <el-icon><CircleCloseFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.rejected }}</div>
            <div class="stat-label">已拒绝</div>
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
            <el-option
              v-for="item in APPLICATION_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 申请列表 -->
    <div v-loading="loading" class="application-list">
      <el-empty v-if="tableData.length === 0" description="暂无申请记录" />

      <el-row v-else :gutter="16" class="cards-row">
        <el-col
          v-for="application in tableData"
          :key="application.id"
          :xs="24"
          :sm="12"
          :md="12"
          :lg="8"
          :xl="6"
        >
          <el-card shadow="hover" class="application-card" @click="handleViewDetail(application)">
            <!-- 职位信息 -->
            <div class="card-header">
              <div class="position-name">{{ application.positionName }}</div>
              <el-tag :type="getStatusType(application.status)" size="small">
                {{ getStatusLabel(application.status) }}
              </el-tag>
            </div>

            <!-- 公司信息 -->
            <div class="company-info">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ application.companyName }}</span>
            </div>

            <!-- 薪资 -->
            <div class="salary-info" v-if="application.salaryMin || application.salaryMax">
              <span class="salary-label">薪资：</span>
              <span class="salary-value">
                {{ formatSalary(application.salaryMin, application.salaryMax) }}/月
              </span>
            </div>

            <!-- 进度条 -->
            <div class="progress-section">
              <div class="progress-label">当前阶段：{{ getStageLabel(application.currentStage) }}</div>
              <el-progress
                :percentage="getStageProgress(application.currentStage)"
                :stroke-width="8"
                :color="getProgressColor(application.currentStage)"
                :show-text="false"
              />
              <div class="stage-tips">
                <span
                  v-for="stage in ['resume', 'test', 'interview', 'offer']"
                  :key="stage"
                  class="stage-tip"
                  :class="{ active: isStageActive(application.currentStage, stage) }"
                >
                  {{ getStageLabel(stage) }}
                </span>
              </div>
            </div>

            <!-- 申请时间 -->
            <div class="application-time">
              <el-icon><Clock /></el-icon>
              <span>申请时间：{{ application.applicationTime }}</span>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions" @click.stop>
              <el-button
                type="primary"
                size="small"
                :icon="View"
                @click="handleViewDetail(application)"
              >
                查看详情
              </el-button>
              <el-button
                v-if="application.status === 'pending' && application.currentStage === 'resume'"
                type="danger"
                size="small"
                :icon="Close"
                @click="handleWithdraw(application)"
              >
                撤销申请
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
      title="申请详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentApplication" class="detail-content">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="职位名称">
              {{ currentApplication.positionName }}
            </el-descriptions-item>
            <el-descriptions-item label="企业名称">
              {{ currentApplication.companyName }}
            </el-descriptions-item>
            <el-descriptions-item label="薪资范围" v-if="currentApplication.salaryMin || currentApplication.salaryMax">
              {{ formatSalary(currentApplication.salaryMin, currentApplication.salaryMax) }}/月
            </el-descriptions-item>
            <el-descriptions-item label="工作地点">
              {{ currentApplication.workCity || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="申请状态">
              <el-tag :type="getStatusType(currentApplication.status)">
                {{ getStatusLabel(currentApplication.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前阶段">
              <el-tag :type="getStageType(currentApplication.currentStage)">
                {{ getStageLabel(currentApplication.currentStage) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请时间" :span="2">
              {{ currentApplication.applicationTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 进度时间轴 -->
        <div class="detail-section">
          <h4 class="section-title">申请进度</h4>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in getProgressTimeline(currentApplication)"
              :key="index"
              :type="item.completed ? 'primary' : 'info'"
              :icon="item.completed ? 'SuccessFilled' : undefined"
              :color="item.completed ? '#409EFF' : '#E4E7ED'"
            >
              <div class="timeline-content">
                <div class="timeline-title">{{ item.title }}</div>
                <div class="timeline-time" v-if="item.time">{{ item.time }}</div>
                <div class="timeline-desc" v-if="item.description">{{ item.description }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>

        <!-- 备注 -->
        <div class="detail-section" v-if="currentApplication.hrRemark">
          <h4 class="section-title">备注信息</h4>
          <el-alert type="info" :closable="false">
            {{ currentApplication.hrRemark }}
          </el-alert>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentApplication && currentApplication.status === 'pending' && currentApplication.currentStage === 'resume'"
          type="danger"
          @click="handleWithdraw(currentApplication)"
        >
          撤销申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Clock,
  View,
  Edit,
  ChatDotRound,
  Present,
  SuccessFilled,
  CircleCloseFilled,
  OfficeBuilding,
  Close
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import { getJobApplicationPageApi, withdrawApplicationApi } from '@/api/jobApplication'
import {
  APPLICATION_STATUS_OPTIONS,
  getStatusType,
  getStatusLabel,
  getStageType,
  getStageLabel
} from '@/constants/application'

interface Application {
  id: number
  positionId: number
  positionName: string
  companyId: number
  companyName: string
  salaryMin?: number
  salaryMax?: number
  workCity?: string
  status: string
  currentStage: string
  applicationTime: string
  hrRemark?: string
}

interface Stats {
  total: number
  pending: number
  screening: number
  test: number
  interview: number
  offer: number
  hired: number
  rejected: number
}

const authStore = useAuthStore()
const loading = ref(false)
const tableData = ref<Application[]>([])
const detailDialogVisible = ref(false)
const currentApplication = ref<Application | null>(null)

// 学员ID
const userId = computed(() => authStore.user?.studentId)

// 统计数据
const stats = ref<Stats>({
  total: 0,
  pending: 0,
  screening: 0,
  test: 0,
  interview: 0,
  offer: 0,
  hired: 0,
  rejected: 0
})

// 搜索表单
const searchForm = reactive({
  status: '',
  dateRange: [] as string[]
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
      studentId: userId.value,
      status: searchForm.status || undefined
    }

    const response = await getJobApplicationPageApi(params)
    tableData.value = response.records || []
    pagination.total = response.total || 0
    updateStats()
  } catch (error) {
    console.error('获取求职申请列表失败:', error)
    ElMessage.error('获取求职申请列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.value = {
    total: tableData.value.length,
    pending: tableData.value.filter((a) => a.status === 'pending' && a.currentStage === 'resume').length,
    screening: tableData.value.filter((a) => a.status === 'pending' && a.currentStage === 'resume').length,
    test: tableData.value.filter((a) => a.currentStage === 'test').length,
    interview: tableData.value.filter((a) => a.currentStage === 'interview').length,
    offer: tableData.value.filter((a) => a.currentStage === 'offer' || a.status === 'offered').length,
    hired: tableData.value.filter((a) => a.status === 'hired').length,
    rejected: tableData.value.filter((a) =>
      ['rejected', 'test_failed', 'interview_failed'].includes(a.status)
    ).length
  }
}

// 格式化薪资
const formatSalary = (min?: number, max?: number): string => {
  if (!min && !max) return '面议'
  const minK = min ? Math.floor(min / 1000) + 'k' : ''
  const maxK = max ? Math.floor(max / 1000) + 'k' : ''
  if (minK && maxK) return `${minK}-${maxK}`
  return minK || maxK || '面议'
}

// 获取阶段进度百分比
const getStageProgress = (stage: string): number => {
  const stages = ['resume', 'test', 'interview', 'offer', 'hired']
  const index = stages.indexOf(stage)
  return index >= 0 ? ((index + 1) / stages.length) * 100 : 0
}

// 获取进度条颜色
const getProgressColor = (stage: string): string => {
  const colors: Record<string, string> = {
    resume: '#409EFF',
    test: '#67C23A',
    interview: '#E6A23C',
    offer: '#F56C6C',
    hired: '#909399'
  }
  return colors[stage] || '#409EFF'
}

// 判断阶段是否激活
const isStageActive = (currentStage: string, checkStage: string): boolean => {
  const stages = ['resume', 'test', 'interview', 'offer', 'hired']
  const currentIndex = stages.indexOf(currentStage)
  const checkIndex = stages.indexOf(checkStage)
  return checkIndex <= currentIndex
}

// 获取进度时间轴
const getProgressTimeline = (application: Application) => {
  const timeline = [
    { title: '提交申请', completed: true, time: application.applicationTime },
    { title: '简历筛选', completed: application.currentStage !== 'resume' || application.status !== 'pending' },
    { title: '笔试', completed: ['test', 'interview', 'offer', 'hired'].includes(application.currentStage) },
    { title: '面试', completed: ['interview', 'offer', 'hired'].includes(application.currentStage) },
    { title: 'Offer', completed: ['offer', 'hired'].includes(application.currentStage) || application.status === 'offered' }
  ]

  if (application.status === 'hired') {
    timeline.push({ title: '已入职', completed: true })
  }

  return timeline
}

// 查看详情
const handleViewDetail = async (application: Application) => {
  currentApplication.value = application
  detailDialogVisible.value = true
}

// 撤销申请
const handleWithdraw = async (application: Application) => {
  try {
    await ElMessageBox.confirm('确定要撤销该申请吗？撤销后无法恢复。', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    await withdrawApplicationApi(application.id, userId.value!)
    ElMessage.success('撤销成功')
    detailDialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销失败:', error)
      ElMessage.error('撤销失败')
    }
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  searchForm.dateRange = []
  handleSearch()
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.my-applications {
  padding: 20px;

  // 统计卡片
  .stats-card {
    margin-bottom: 20px;
    padding: 20px;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(3, 1fr);
    }

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

    &.pending {
      background: #fef0e6;
      color: #e6a23c;
    }

    &.screening {
      background: #e8f4ff;
      color: #409eff;
    }

    &.test {
      background: #e1f3d8;
      color: #67c23a;
    }

    &.interview {
      background: #fdf2ec;
      color: #f56c6c;
    }

    &.offer {
      background: #fff2e8;
      color: #ff6b00;
    }

    &.hired {
      background: #f0f9ff;
      color: #00d4ff;
    }

    &.rejected {
      background: #fee;
      color: #f56c6c;
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

  // 申请卡片列表
  .application-list {
    min-height: 400px;
  }

  .cards-row {
    margin-bottom: 20px;
  }

  .application-card {
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
      align-items: center;
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

    .salary-info {
      margin-bottom: 12px;
      font-size: 14px;

      .salary-label {
        color: #909399;
      }

      .salary-value {
        color: #f56c6c;
        font-weight: bold;
        font-size: 16px;
      }
    }

    .progress-section {
      margin-bottom: 12px;

      .progress-label {
        font-size: 13px;
        color: #606266;
        margin-bottom: 8px;
      }

      .stage-tips {
        display: flex;
        justify-content: space-between;
        margin-top: 8px;
        font-size: 12px;
        color: #909399;

        .stage-tip {
          padding: 2px 6px;
          border-radius: 4px;
          transition: all 0.3s;

          &.active {
            color: #409eff;
            background: #ecf5ff;
          }
        }
      }
    }

    .application-time {
      display: flex;
      align-items: center;
      color: #909399;
      font-size: 13px;
      margin-bottom: 12px;

      .el-icon {
        margin-right: 5px;
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

    .timeline-content {
      .timeline-title {
        font-size: 14px;
        font-weight: bold;
        color: #303133;
      }

      .timeline-time {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
      }

      .timeline-desc {
        font-size: 13px;
        color: #606266;
        margin-top: 4px;
      }
    }
  }
}
</style>
