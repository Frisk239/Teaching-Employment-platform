<template>
  <div class="my-written-tests">
    <!-- 统计卡片 -->
    <el-card shadow="never" class="stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon total">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总笔试</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon pending">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待考试</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon ongoing">
            <el-icon><Edit /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.ongoing }}</div>
            <div class="stat-label">考试中</div>
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
          <div class="stat-icon missed">
            <el-icon><CircleCloseFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.missed }}</div>
            <div class="stat-label">已缺席</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon average">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.averageScore }}</div>
            <div class="stat-label">平均分</div>
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
            <el-option label="待考试" value="pending" />
            <el-option label="考试中" value="ongoing" />
            <el-option label="已完成" value="completed" />
            <el-option label="缺席" value="missed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 笔试列表 -->
    <div v-loading="loading" class="test-list">
      <el-empty v-if="tableData.length === 0" description="暂无笔试记录" />

      <el-row v-else :gutter="16" class="cards-row">
        <el-col
          v-for="test in tableData"
          :key="test.id"
          :xs="24"
          :sm="12"
          :md="12"
          :lg="8"
          :xl="6"
        >
          <el-card shadow="hover" class="test-card">
            <!-- 职位信息 -->
            <div class="card-header">
              <div class="position-name">{{ test.positionName }}</div>
              <el-tag :type="getStatusType(test.status)" size="small">
                {{ getStatusLabel(test.status) }}
              </el-tag>
            </div>

            <!-- 公司信息 -->
            <div class="company-info">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ test.companyName }}</span>
            </div>

            <!-- 考试时间 -->
            <div class="test-time">
              <el-icon><Calendar /></el-icon>
              <span v-if="test.startTime">{{ formatDateTime(test.startTime) }}</span>
              <span v-else>未安排</span>
            </div>

            <!-- 考试时长 -->
            <div class="test-duration" v-if="test.duration">
              <el-icon><Timer /></el-icon>
              <span>时长：{{ test.duration }} 分钟</span>
            </div>

            <!-- 成绩显示 -->
            <div class="test-score" v-if="test.status === 'completed' && test.score !== null">
              <div class="score-label">笔试成绩</div>
              <div class="score-value" :class="{ 'pass': test.score >= (test.totalScore * 0.6), 'fail': test.score < (test.totalScore * 0.6) }">
                {{ test.score }} / {{ test.totalScore }}
              </div>
              <div class="score-percent">
                得分率：{{ ((test.score / test.totalScore) * 100).toFixed(1) }}%
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-button
                v-if="test.status === 'pending' && test.testUrl"
                type="primary"
                size="small"
                :icon="Link"
                @click="handleStartTest(test)"
              >
                开始考试
              </el-button>
              <el-button
                v-else-if="test.status === 'ongoing' && test.testUrl"
                type="warning"
                size="small"
                :icon="Link"
                @click="handleContinueTest(test)"
              >
                继续考试
              </el-button>
              <el-button
                type="info"
                size="small"
                :icon="View"
                @click="handleViewDetail(test)"
              >
                查看详情
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
      title="笔试详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentTest" class="detail-content">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="应聘职位">
              {{ currentTest.positionName }}
            </el-descriptions-item>
            <el-descriptions-item label="企业名称">
              {{ currentTest.companyName }}
            </el-descriptions-item>
            <el-descriptions-item label="考试状态">
              <el-tag :type="getStatusType(currentTest.status)">
                {{ getStatusLabel(currentTest.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="考试时长">
              {{ currentTest.duration }} 分钟
            </el-descriptions-item>
            <el-descriptions-item label="开始时间" :span="2">
              {{ currentTest.startTime ? formatDateTime(currentTest.startTime) : '未安排' }}
            </el-descriptions-item>
            <el-descriptions-item label="结束时间" :span="2">
              {{ currentTest.endTime ? formatDateTime(currentTest.endTime) : '未安排' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 成绩信息 -->
        <div class="detail-section" v-if="currentTest.status === 'completed' && currentTest.score !== null">
          <h4 class="section-title">考试成绩</h4>
          <div class="score-detail">
            <div class="score-circle">
              <el-progress
                type="circle"
                :percentage="((currentTest.score / currentTest.totalScore) * 100)"
                :color="getScoreColor(currentTest.score, currentTest.totalScore)"
                :width="120"
              >
                <template #default>
                  <div class="score-in-circle">
                    <div class="score-num">{{ currentTest.score }}</div>
                    <div class="score-total">/ {{ currentTest.totalScore }}</div>
                  </div>
                </template>
              </el-progress>
            </div>
            <div class="score-info">
              <div class="score-percent-text">
                得分率：{{ ((currentTest.score / currentTest.totalScore) * 100).toFixed(1) }}%
              </div>
              <div class="score-status" :class="{ 'pass': currentTest.score >= (currentTest.totalScore * 0.6), 'fail': currentTest.score < (currentTest.totalScore * 0.6) }">
                {{ currentTest.score >= (currentTest.totalScore * 0.6) ? '及格' : '不及格' }}
              </div>
            </div>
          </div>
        </div>

        <!-- 评语 -->
        <div class="detail-section" v-if="currentTest.comment">
          <h4 class="section-title">评语</h4>
          <el-alert type="info" :closable="false">
            {{ currentTest.comment }}
          </el-alert>
        </div>

        <!-- 考试链接 -->
        <div class="detail-section" v-if="currentTest.testUrl && (currentTest.status === 'pending' || currentTest.status === 'ongoing')">
          <h4 class="section-title">考试链接</h4>
          <el-input v-model="currentTest.testUrl" readonly>
            <template #append>
              <el-button @click="handleOpenTestUrl(currentTest.testUrl)">打开链接</el-button>
            </template>
          </el-input>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentTest && (currentTest.status === 'pending' || currentTest.status === 'ongoing') && currentTest.testUrl"
          type="primary"
          @click="handleStartTest(currentTest!)"
        >
          {{ currentTest.status === 'pending' ? '开始考试' : '继续考试' }}
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
  Edit,
  SuccessFilled,
  CircleCloseFilled,
  TrendCharts,
  OfficeBuilding,
  Calendar,
  Timer,
  Link,
  View
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import { writtenTestApi, type WrittenTest } from '@/api/writtenTest'

interface Test extends WrittenTest {
  companyName?: string
}

interface Stats {
  total: number
  pending: number
  ongoing: number
  completed: number
  missed: number
  averageScore: string
}

const authStore = useAuthStore()
const loading = ref(false)
const tableData = ref<Test[]>([])
const detailDialogVisible = ref(false)
const currentTest = ref<Test | null>(null)

// 学员ID
const userId = computed(() => authStore.user?.studentId)

// 统计数据
const stats = ref<Stats>({
  total: 0,
  pending: 0,
  ongoing: 0,
  completed: 0,
  missed: 0,
  averageScore: '-'
})

// 搜索表单
const searchForm = reactive({
  status: ''
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

    const response = await writtenTestApi.getPage(params) as any
    tableData.value = response.records || []
    pagination.total = response.total || 0
    updateStats()
  } catch (error) {
    console.error('获取笔试列表失败:', error)
    ElMessage.error('获取笔试列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.value = {
    total: tableData.value.length,
    pending: tableData.value.filter((t) => t.status === 'pending').length,
    ongoing: tableData.value.filter((t) => t.status === 'ongoing').length,
    completed: tableData.value.filter((t) => t.status === 'completed').length,
    missed: tableData.value.filter((t) => t.status === 'missed').length,
    averageScore: calculateAverageScore()
  }
}

// 计算平均分
const calculateAverageScore = (): string => {
  const completedTests = tableData.value.filter((t) => t.status === 'completed' && t.score !== null && t.score !== undefined)
  if (completedTests.length === 0) return '-'

  const totalScore = completedTests.reduce((sum, t) => sum + (t.score || 0), 0)
  return (totalScore / completedTests.length).toFixed(1)
}

// 获取状态标签类型
const getStatusType = (status: string): string => {
  const map: Record<string, string> = {
    'pending': 'info',
    'ongoing': 'warning',
    'completed': 'success',
    'missed': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态标签文本
const getStatusLabel = (status: string): string => {
  const map: Record<string, string> = {
    'pending': '待考试',
    'ongoing': '考试中',
    'completed': '已完成',
    'missed': '缺席'
  }
  return map[status] || status
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
const getScoreColor = (score: number, totalScore: number): string => {
  const percent = (score / totalScore) * 100
  if (percent >= 80) return '#67c23a'
  if (percent >= 60) return '#409eff'
  return '#f56c6c'
}

// 开始考试
const handleStartTest = async (test: Test) => {
  if (!test.testUrl) {
    ElMessage.warning('考试链接不存在')
    return
  }

  try {
    // 标记为考试中
    await writtenTestApi.start(test.id!)
    ElMessage.success('开始考试，祝你好运！')
    handleOpenTestUrl(test.testUrl)
    fetchData()
  } catch (error) {
    console.error('开始考试失败:', error)
    handleOpenTestUrl(test.testUrl)
  }
}

// 继续考试
const handleContinueTest = (test: Test) => {
  if (!test.testUrl) {
    ElMessage.warning('考试链接不存在')
    return
  }
  handleOpenTestUrl(test.testUrl)
}

// 打开考试链接
const handleOpenTestUrl = (url: string) => {
  window.open(url, '_blank')
}

// 查看详情
const handleViewDetail = (test: Test) => {
  currentTest.value = test
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
  handleSearch()
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.my-written-tests {
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

    &.pending {
      background: #e8f4ff;
      color: #409eff;
    }

    &.ongoing {
      background: #fef0e6;
      color: #e6a23c;
    }

    &.completed {
      background: #e1f3d8;
      color: #67c23a;
    }

    &.missed {
      background: #fee;
      color: #f56c6c;
    }

    &.average {
      background: #f4f0ff;
      color: #9093ff;
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

  // 笔试卡片列表
  .test-list {
    min-height: 400px;
  }

  .cards-row {
    margin-bottom: 20px;
  }

  .test-card {
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

    .test-time {
      display: flex;
      align-items: center;
      color: #909399;
      font-size: 13px;
      margin-bottom: 8px;

      .el-icon {
        margin-right: 5px;
      }
    }

    .test-duration {
      display: flex;
      align-items: center;
      color: #909399;
      font-size: 13px;
      margin-bottom: 12px;

      .el-icon {
        margin-right: 5px;
      }
    }

    .test-score {
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
        margin-bottom: 4px;

        &.pass {
          color: #67c23a;
        }

        &.fail {
          color: #f56c6c;
        }
      }

      .score-percent {
        font-size: 13px;
        color: #606266;
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

        .score-percent-text {
          font-size: 18px;
          color: #606266;
          margin-bottom: 8px;
        }

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
