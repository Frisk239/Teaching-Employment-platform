<template>
  <div class="my-daily-reports">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">我的日报</h1>
        <p class="page-subtitle">记录每日学习进度与心得</p>
      </div>
      <el-button type="primary" :icon="DocumentAdd" @click="handleWriteReport">
        写日报
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">总日报数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon draft">
              <el-icon><Edit /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.draft }}</div>
              <div class="stat-label">草稿</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon submitted">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.submitted }}</div>
              <div class="stat-label">待批阅</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon reviewed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.reviewed }}</div>
              <div class="stat-label">已批阅</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <el-card shadow="never" class="filter-card">
      <el-form :model="searchForm" inline class="filter-form">
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="草稿" value="draft" />
            <el-option label="已提交" value="submitted" />
            <el-option label="已批阅" value="reviewed" />
          </el-select>
        </el-form-item>

        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px"
            @change="handleDateRangeChange"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日报列表 -->
    <el-card shadow="never" class="list-card">
      <div v-loading="loading" class="report-list">
        <el-empty v-if="!loading && isEmpty" description="暂无日报记录">
          <el-button type="primary" @click="handleWriteReport">开始写日报</el-button>
        </el-empty>

        <div v-for="report in tableData" :key="report.id" class="report-item">
          <!-- 报告头部 -->
          <div class="report-header">
            <div class="report-date">
              <el-icon><Calendar /></el-icon>
              <span>{{ report.reportDate }}</span>
            </div>
            <div class="report-status">
              <el-tag v-if="report.status === 'draft'" type="info">草稿</el-tag>
              <el-tag v-else-if="report.status === 'submitted'" type="warning">已提交</el-tag>
              <el-tag v-else-if="report.status === 'reviewed'" type="success">已批阅</el-tag>
            </div>
          </div>

          <!-- 报告内容 -->
          <div class="report-body">
            <div class="report-content">
              <h4 class="content-title">今日学习内容</h4>
              <p class="content-text">{{ report.todayContent || '暂无内容' }}</p>
            </div>

            <div class="report-meta">
              <div class="meta-item">
                <el-icon><Timer /></el-icon>
                <span>{{ report.studyHours ? `${report.studyHours}小时` : '-' }}</span>
              </div>
              <div v-if="report.rating" class="meta-item rating">
                <el-rate
                  v-model="report.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  :score-template="'{value}分'"
                />
              </div>
            </div>
          </div>

          <!-- 报告操作 -->
          <div class="report-actions">
            <el-button
              v-if="report.status === 'draft'"
              type="primary"
              size="small"
              :icon="Edit"
              @click="handleEdit(report)"
            >
              编辑
            </el-button>
            <el-button
              v-if="report.status === 'draft'"
              type="success"
              size="small"
              :icon="Promotion"
              @click="handleSubmit(report.id)"
            >
              提交
            </el-button>
            <el-button
              v-if="report.status === 'submitted'"
              type="info"
              size="small"
              :icon="View"
              @click="handleView(report)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="report.status === 'reviewed'"
              type="success"
              size="small"
              :icon="ChatDotRound"
              @click="handleView(report)"
            >
              查看批阅
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 写/编辑日报对话框 -->
    <el-dialog
      v-model="writeDialogVisible"
      :title="isEditMode ? '编辑日报' : '写日报'"
      width="700px"
      @close="handleWriteDialogClose"
    >
      <el-form
        ref="writeFormRef"
        :model="writeForm"
        :rules="writeRules"
        label-width="120px"
      >
        <el-form-item label="日报日期" prop="reportDate">
          <el-date-picker
            v-model="writeForm.reportDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled-date="(date) => date > new Date()"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="今日学习内容" prop="todayContent">
          <el-input
            v-model="writeForm.todayContent"
            type="textarea"
            :rows="4"
            placeholder="请详细描述今天的学习内容..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="完成进度">
          <el-input
            v-model="writeForm.todayProgress"
            type="textarea"
            :rows="3"
            placeholder="描述今天完成的进度和成果..."
          />
        </el-form-item>

        <el-form-item label="遇到的问题">
          <el-input
            v-model="writeForm.problems"
            type="textarea"
            :rows="3"
            placeholder="学习中遇到的难点和问题..."
          />
        </el-form-item>

        <el-form-item label="明日计划">
          <el-input
            v-model="writeForm.tomorrowPlan"
            type="textarea"
            :rows="3"
            placeholder="明天的学习计划和目标..."
          />
        </el-form-item>

        <el-form-item label="学习时长" prop="studyHours">
          <el-input-number
            v-model="writeForm.studyHours"
            :min="0"
            :max="24"
            :precision="1"
            style="width: 200px"
          />
          <span style="margin-left: 10px">小时</span>
        </el-form-item>

      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="writeDialogVisible = false">取消</el-button>
          <el-button @click="handleSaveDraft">保存草稿</el-button>
          <el-button type="primary" @click="handleSubmitDirectly">提交日报</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看日报对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="viewDialogTitle"
      width="700px"
    >
      <div v-if="currentReport" class="report-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="日报日期" :span="2">
            {{ currentReport.reportDate }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="currentReport.status === 'draft'" type="info">草稿</el-tag>
            <el-tag v-else-if="currentReport.status === 'submitted'" type="warning">已提交</el-tag>
            <el-tag v-else-if="currentReport.status === 'reviewed'" type="success">已批阅</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学习时长">
            {{ currentReport.studyHours ? `${currentReport.studyHours} 小时` : '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">学习内容</el-divider>

        <el-descriptions :column="1" border>
          <el-descriptions-item label="今日学习内容">
            {{ currentReport.todayContent || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="完成进度">
            {{ currentReport.todayProgress || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="遇到的问题">
            {{ currentReport.problems || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="明日计划">
            {{ currentReport.tomorrowPlan || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 批阅信息(已批阅时显示) -->
        <template v-if="currentReport.status === 'reviewed'">
          <el-divider content-position="left">教师评语</el-divider>
          <el-card shadow="never" class="teacher-comment">
            <div class="comment-rating">
              <span class="rating-label">评分：</span>
              <el-rate
                v-model="currentReport.rating"
                disabled
                show-score
                text-color="#ff9900"
              />
            </div>
            <div class="comment-content">
              <span class="comment-label">教师评语：</span>
              <p>{{ currentReport.teacherComment || '-' }}</p>
            </div>
            <div class="comment-time">
              <span class="time-label">批阅时间：</span>
              <span>{{ currentReport.reviewTime || '-' }}</span>
            </div>
          </el-card>
        </template>
      </div>

      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  DocumentAdd,
  Document,
  Edit,
  Clock,
  CircleCheck,
  Search,
  Refresh,
  View,
  Promotion,
  Calendar,
  Timer,
  Notebook,
  ChatDotRound,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import {
  getDailyReportsByStudentApi,
  createDailyReportApi,
  updateDailyReportApi,
  deleteDailyReportApi,
  submitDailyReportApi,
  type DailyReport,
} from '@/api/dailyReport'

const authStore = useAuthStore()
const userId = computed(() => authStore.user?.studentId)

// 统计数据
const stats = reactive({
  total: 0,
  draft: 0,
  submitted: 0,
  reviewed: 0,
})

// 搜索表单
const searchForm = reactive({
  status: '',
  startDate: '',
  endDate: '',
})

const dateRange = ref<[string, string] | null>(null)

// 表格数据
const loading = ref(false)
const tableData = ref<DailyReport[]>([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 计算是否为空
const isEmpty = computed(() => tableData.value.length === 0)

// 写/编辑日报对话框
const writeDialogVisible = ref(false)
const isEditMode = ref(false)
const writeFormRef = ref<FormInstance>()
const writeForm = reactive({
  id: undefined as number | undefined,
  reportDate: '',
  todayContent: '',
  todayProgress: '',
  problems: '',
  tomorrowPlan: '',
  studyHours: undefined as number | undefined,
})

const writeRules: FormRules = {
  reportDate: [{ required: true, message: '请选择日报日期', trigger: 'change' }],
  todayContent: [{ required: true, message: '请输入今日学习内容', trigger: 'blur' }],
  studyHours: [{ required: true, message: '请输入学习时长', trigger: 'blur' }],
}

// 查看日报对话框
const viewDialogVisible = ref(false)
const viewDialogTitle = computed(() => {
  return currentReport.value?.status === 'reviewed' ? '批阅结果' : '日报详情'
})
const currentReport = ref<DailyReport | null>(null)

// 加载日报列表
const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      status: searchForm.status || undefined,
      startDate: searchForm.startDate || undefined,
      endDate: searchForm.endDate || undefined,
    }

    const res = await getDailyReportsByStudentApi(userId.value!, params)
    tableData.value = res.records || []
    pagination.total = res.total || 0

    // 更新统计数据
    updateStats()
  } catch (error) {
    console.error('加载日报列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.total = pagination.total
  stats.draft = tableData.value.filter((item) => item.status === 'draft').length
  stats.submitted = tableData.value.filter((item) => item.status === 'submitted').length
  stats.reviewed = tableData.value.filter((item) => item.status === 'reviewed').length
}

// 日期范围变化
const handleDateRangeChange = (dates: [string, string] | null) => {
  if (dates && dates.length === 2) {
    searchForm.startDate = dates[0]
    searchForm.endDate = dates[1]
  } else {
    searchForm.startDate = ''
    searchForm.endDate = ''
  }
  handleSearch()
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  dateRange.value = null
  pagination.current = 1
  fetchData()
}

// 写日报
const handleWriteReport = () => {
  isEditMode.value = false
  // 默认日期为今天
  writeForm.reportDate = new Date().toISOString().split('T')[0]
  writeDialogVisible.value = true
}

// 编辑日报
const handleEdit = (row: DailyReport) => {
  isEditMode.value = true
  Object.assign(writeForm, {
    id: row.id,
    reportDate: row.reportDate,
    todayContent: row.todayContent,
    todayProgress: row.todayProgress,
    problems: row.problems,
    tomorrowPlan: row.tomorrowPlan,
    studyHours: row.studyHours,
  })
  writeDialogVisible.value = true
}

// 关闭写日报对话框
const handleWriteDialogClose = () => {
  writeFormRef.value?.resetFields()
  Object.assign(writeForm, {
    id: undefined,
    reportDate: '',
    todayContent: '',
    todayProgress: '',
    problems: '',
    tomorrowPlan: '',
    studyHours: undefined,
  })
}

// 保存草稿
const handleSaveDraft = async () => {
  try {
    const data = { ...writeForm, status: 'draft' }

    if (isEditMode.value && writeForm.id) {
      await updateDailyReportApi(data)
      ElMessage.success('保存草稿成功')
    } else {
      data.studentId = userId.value
      await createDailyReportApi(data)
      ElMessage.success('创建草稿成功')
    }

    writeDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存失败')
  }
}

// 直接提交
const handleSubmitDirectly = async () => {
  const valid = await writeFormRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = { ...writeForm, status: 'submitted' }

    if (isEditMode.value && writeForm.id) {
      await updateDailyReportApi(data)
      ElMessage.success('提交成功')
    } else {
      data.studentId = userId.value
      await createDailyReportApi(data)
      ElMessage.success('提交成功')
    }

    writeDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 提交日报
const handleSubmit = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认提交此日报吗?提交后将无法修改', '提示', {
      type: 'warning',
    })

    await submitDailyReportApi(id)
    ElMessage.success('提交成功')
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
      ElMessage.error('提交失败')
    }
  }
}

// 查看日报
const handleView = (row: DailyReport) => {
  currentReport.value = row
  viewDialogVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.my-daily-reports {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.stats-row {
  margin-bottom: 1.5rem;

  .stat-card {
    border-radius: 12px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        margin-right: 16px;

        &.total {
          background: oklch(0.94 0.05 220);
          color: oklch(0.55 0.15 220);
        }

        &.draft {
          background: oklch(0.95 0.08 80);
          color: oklch(0.65 0.2 80);
        }

        &.submitted {
          background: oklch(0.95 0.08 40);
          color: oklch(0.65 0.2 40);
        }

        &.reviewed {
          background: oklch(0.93 0.08 150);
          color: oklch(0.55 0.15 150);
        }
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
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

.filter-card {
  margin-bottom: 1.5rem;
  border-radius: 12px;
}

.list-card {
  border-radius: 12px;

  .report-list {
    min-height: 400px;

    .report-item {
      border: 1px solid var(--el-border-color-lighter);
      border-radius: 12px;
      padding: 1.5rem;
      margin-bottom: 1rem;
      transition: all 0.3s;
      background: white;

      &:hover {
        border-color: var(--el-color-primary);
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      }

      &:last-child {
        margin-bottom: 0;
      }

      .report-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;

        .report-date {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          font-size: 1.1rem;
          font-weight: 600;
          color: var(--el-text-color-primary);

          .el-icon {
            font-size: 20px;
            color: var(--el-color-primary);
          }
        }

        .report-status {
          flex-shrink: 0;
        }
      }

      .report-body {
        margin-bottom: 1rem;

        .report-content {
          margin-bottom: 1rem;

          .content-title {
            font-size: 0.9rem;
            font-weight: 600;
            color: var(--el-text-color-secondary);
            margin: 0 0 0.5rem 0;
          }

          .content-text {
            margin: 0;
            color: var(--el-text-color-regular);
            line-height: 1.6;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }

        .report-meta {
          display: flex;
          gap: 1.5rem;
          flex-wrap: wrap;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 0.25rem;
            font-size: 0.875rem;
            color: var(--el-text-color-secondary);

            .el-icon {
              font-size: 16px;
            }

            &.rating {
              margin-left: auto;
            }
          }
        }
      }

      .report-actions {
        display: flex;
        gap: 0.5rem;
        justify-content: flex-end;
      }
    }
  }

  .pagination {
    margin-top: 2rem;
    display: flex;
    justify-content: flex-end;
  }
}

.report-detail {
  :deep(.el-descriptions) {
    margin-bottom: 1rem;
  }

  .el-divider {
    margin: 1.5rem 0;
  }

  .teacher-comment {
    background: oklch(0.97 0.01 150);
    border-radius: 8px;

    .comment-rating,
    .comment-content,
    .comment-time {
      margin-bottom: 1rem;

      &:last-child {
        margin-bottom: 0;
      }

      .rating-label,
      .comment-label,
      .time-label {
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin-right: 0.5rem;
      }

      p {
        margin: 0.5rem 0 0 0;
        color: var(--el-text-color-regular);
        line-height: 1.6;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .my-daily-reports {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    gap: 1rem;

    .page-title {
      font-size: 1.5rem;
    }
  }

  .stats-row {
    :deep(.el-col) {
      margin-bottom: 1rem;
    }
  }

  .report-item {
    .report-meta {
      flex-direction: column;
      gap: 0.5rem !important;

      .meta-item.rating {
        margin-left: 0;
      }
    }

    .report-actions {
      flex-direction: column;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
