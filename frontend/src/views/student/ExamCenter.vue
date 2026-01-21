<template>
  <div class="exam-center">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">考试中心</h1>
        <p class="page-subtitle">参加课程考试和企业笔试</p>
      </div>
    </div>

    <!-- 顶部标签页 -->
    <el-tabs v-model="activeTab" class="exam-tabs">
      <!-- 可用考试标签页 -->
      <el-tab-pane label="可用考试" name="available">
        <!-- 筛选栏 -->
        <el-card shadow="never" class="filter-card">
          <el-form :model="searchForm" inline class="filter-form">
            <el-form-item label="考试类型">
              <el-select
                v-model="searchForm.examType"
                placeholder="全部类型"
                clearable
                style="width: 150px"
                @change="loadOngoingExams"
              >
                <el-option label="课程考试" value="course" />
                <el-option label="企业笔试" value="company" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 考试卡片列表 -->
        <div v-loading="loading" class="exam-list">
          <el-row v-if="ongoingExams.length > 0" :gutter="16">
            <el-col
              v-for="exam in ongoingExams"
              :key="exam.id"
              :span="8"
              style="margin-bottom: 16px"
            >
              <el-card shadow="hover" class="exam-card">
                <div class="exam-header">
                  <h4>{{ exam.examName }}</h4>
                  <div class="exam-tags">
                    <el-tag
                      :type="exam.examType === 'course' ? 'primary' : 'success'"
                      size="small"
                    >
                      {{ getExamTypeText(exam.examType) }}
                    </el-tag>
                  </div>
                </div>

                <div class="exam-info">
                  <p class="info-item">
                    <el-icon><Reading /></el-icon>
                    <span>关联：{{ exam.refName || '-' }}</span>
                  </p>
                  <p class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>时长：{{ exam.duration }} 分钟</span>
                  </p>
                  <p class="info-item">
                    <el-icon><Calendar /></el-icon>
                    <span>开始：{{ formatDateTime(exam.startTime) }}</span>
                  </p>
                  <p class="info-item">
                    <el-icon><Calendar /></el-icon>
                    <span>截止：{{ formatDateTime(exam.endTime) }}</span>
                  </p>
                  <p class="info-item">
                    <el-icon><TrendCharts /></el-icon>
                    <span>满分：{{ exam.totalScore }} 分</span>
                  </p>
                  <p class="info-item">
                    <el-icon><Checked /></el-icon>
                    <span>及格分：{{ exam.passScore }} 分</span>
                  </p>
                </div>

                <div class="exam-actions">
                  <el-button
                    type="primary"
                    :disabled="!canStartExam(exam) || hasTakenExam(exam.id!)"
                    @click="handleStartExam(exam)"
                  >
                    {{ getExamButtonText(exam) }}
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无可用考试" />
        </div>
      </el-tab-pane>

      <!-- 历史成绩标签页 -->
      <el-tab-pane label="历史成绩" name="history">
        <!-- 成绩统计 -->
        <el-row :gutter="16" class="stats-row">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon total">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ examStats.total }}</div>
                  <div class="stat-label">总考试数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon passed">
                  <el-icon><CircleCheck /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ examStats.passed }}</div>
                  <div class="stat-label">已及格</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon failed">
                  <el-icon><CircleClose /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ examStats.failed }}</div>
                  <div class="stat-label">未及格</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon pending">
                  <el-icon><Clock /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ examStats.pending }}</div>
                  <div class="stat-label">待评阅</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 考试记录表格 -->
        <el-card shadow="never" class="table-card">
          <el-table
            v-loading="loadingRecords"
            :data="examRecords"
            stripe
            style="width: 100%"
          >
            <el-table-column prop="examName" label="考试名称" min-width="150" />
            <el-table-column prop="studentName" label="学员姓名" width="120" />
            <el-table-column label="得分" width="120" align="center">
              <template #default="{ row }">
                <span v-if="row.gradingStatus === 'pending'" style="color: #909399">
                  待评阅
                </span>
                <span v-else :style="{ color: getScoreColor(row.totalScore!, row.passScore!) }">
                  <strong>{{ row.totalScore }}</strong> / {{ row.examPaper?.totalScore || 100 }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="及格状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.gradingStatus === 'pending'" type="info" size="small">
                  待评阅
                </el-tag>
                <el-tag v-else :type="row.passed ? 'success' : 'danger'" size="small">
                  {{ row.passed ? '及格' : '不及格' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="评阅状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.gradingStatus === 'graded' ? 'success' : 'warning'" size="small">
                  {{ row.gradingStatus === 'graded' ? '已评阅' : '待评阅' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submitTime" label="提交时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.submitTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="viewExamDetail(row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadExamRecords"
              @current-change="loadExamRecords"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 考试结果详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="考试详情"
      width="800px"
    >
      <div v-if="currentRecord" class="exam-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="考试名称">
            {{ currentRecord.examName }}
          </el-descriptions-item>
          <el-descriptions-item label="学员姓名">
            {{ currentRecord.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            {{ formatDateTime(currentRecord.startTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ formatDateTime(currentRecord.submitTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="客观题得分">
            {{ currentRecord.objectiveScore || 0 }} 分
          </el-descriptions-item>
          <el-descriptions-item label="主观题得分">
            {{ currentRecord.subjectiveScore || 0 }} 分
          </el-descriptions-item>
          <el-descriptions-item label="总分">
            <span :style="{ color: getScoreColor(currentRecord.totalScore!, currentRecord.passScore!) }">
              <strong>{{ currentRecord.totalScore || 0 }}</strong> 分
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="及格状态">
            <el-tag v-if="currentRecord.gradingStatus === 'pending'" type="info" size="small">
              待评阅
            </el-tag>
            <el-tag v-else :type="currentRecord.passed ? 'success' : 'danger'" size="small">
              {{ currentRecord.passed ? '及格' : '不及格' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评阅状态">
            <el-tag :type="currentRecord.gradingStatus === 'graded' ? 'success' : 'warning'" size="small">
              {{ currentRecord.gradingStatus === 'graded' ? '已评阅' : '待评阅' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评阅时间">
            {{ formatDateTime(currentRecord.gradingTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <el-alert
          v-if="currentRecord.gradingStatus === 'pending'"
          title="提示"
          type="info"
          :closable="false"
          style="margin-top: 20px"
        >
          本次考试包含主观题，需要等待教师评阅后才能看到最终成绩。客观题成绩已自动评阅完成。
        </el-alert>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Clock,
  CircleCheck,
  CircleClose,
  Reading,
  Timer,
  Calendar,
  TrendCharts,
  Checked,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  getOngoingExamsApi,
  type Exam,
} from '@/api/exam'
import {
  getExamRecordPageApi,
  getExamRecordByIdApi,
  startExamApi,
  type ExamRecord,
} from '@/api/exam-record'

const router = useRouter()
const authStore = useAuthStore()
const studentId = computed(() => authStore.studentId)

// 当前激活的标签页
const activeTab = ref('available')

// 加载状态
const loading = ref(false)
const loadingRecords = ref(false)

// 搜索表单
const searchForm = reactive({
  examType: '',
})

// 进行中的考试列表
const ongoingExams = ref<Exam[]>([])

// 考试记录列表
const examRecords = ref<ExamRecord[]>([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 成绩统计
const examStats = reactive({
  total: 0,
  passed: 0,
  failed: 0,
  pending: 0,
})

// 已参加的考试ID集合
const takenExamIds = ref<Set<number>>(new Set())

// 当前查看的考试记录
const currentRecord = ref<ExamRecord | null>(null)
const detailDialogVisible = ref(false)

// 初始化
onMounted(async () => {
  await loadOngoingExams()
  await loadExamRecords()
})

// 加载进行中的考试列表
async function loadOngoingExams() {
  loading.value = true
  try {
    const res = await getOngoingExamsApi()
    // 响应拦截器已经提取了 res.data，所以直接使用 res
    ongoingExams.value = res || []

    // 根据类型筛选
    if (searchForm.examType) {
      ongoingExams.value = ongoingExams.value.filter(
        exam => exam.examType === searchForm.examType
      )
    }
  } catch (error) {
    ElMessage.error('加载考试列表失败')
  } finally {
    loading.value = false
  }
}

// 加载考试记录列表
async function loadExamRecords() {
  if (!studentId.value) {
    ElMessage.warning('未获取到学员信息')
    return
  }

  loadingRecords.value = true
  try {
    const res = await getExamRecordPageApi({
      current: pagination.current,
      size: pagination.size,
      studentId: studentId.value,
    })

    examRecords.value = res.data.records || []
    pagination.total = res.data.total || 0

    // 收集已参加的考试ID
    takenExamIds.value = new Set(
      examRecords.value.map(record => record.examId!).filter(id => id !== undefined)
    )

    // 计算统计数据
    calculateStats()
  } catch (error) {
    ElMessage.error('加载考试记录失败')
  } finally {
    loadingRecords.value = false
  }
}

// 计算统计数据
function calculateStats() {
  examStats.total = examRecords.value.length
  examStats.passed = examRecords.value.filter(
    r => r.gradingStatus === 'graded' && r.passed
  ).length
  examStats.failed = examRecords.value.filter(
    r => r.gradingStatus === 'graded' && !r.passed
  ).length
  examStats.pending = examRecords.value.filter(
    r => r.gradingStatus === 'pending'
  ).length
}

// 判断是否可以开始考试
function canStartExam(exam: Exam) {
  if (!exam.startTime || !exam.endTime) return false
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  return now >= startTime && now <= endTime
}

// 判断是否已参加考试
function hasTakenExam(examId: number) {
  return takenExamIds.value.has(examId)
}

// 获取考试按钮文本
function getExamButtonText(exam: Exam) {
  if (hasTakenExam(exam.id!)) {
    return '已参加'
  }
  if (!canStartExam(exam)) {
    const now = new Date()
    const startTime = new Date(exam.startTime!)
    if (now < startTime) {
      return '未到开始时间'
    }
    const endTime = new Date(exam.endTime!)
    if (now > endTime) {
      return '已结束'
    }
  }
  return '开始考试'
}

// 处理开始考试
async function handleStartExam(exam: Exam) {
  if (!studentId.value) {
    ElMessage.warning('未获取到学员信息')
    return
  }

  if (!canStartExam(exam)) {
    ElMessage.warning('当前不在考试时间内')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要开始参加"${exam.examName}"考试吗？考试时长为 ${exam.duration} 分钟，开始后计时将启动。`,
      '开始考试',
      {
        type: 'warning',
        confirmButtonText: '开始考试',
        cancelButtonText: '取消',
      }
    )

    // 调用API开始考试
    const res = await startExamApi(exam.id!, studentId.value)

    ElMessage.success('考试已开始，请认真作答')

    // 跳转到考试作答页面（这里可以根据实际需求调整）
    // 暂时使用考试记录ID作为参数
    if (res?.id) {
      router.push({
        name: 'ExamTaking',
        params: { recordId: res.id },
      })
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '开始考试失败')
    }
  }
}

// 查看考试详情
async function viewExamDetail(record: ExamRecord) {
  if (!record.id) return

  try {
    const res = await getExamRecordByIdApi(record.id)
    currentRecord.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载考试详情失败')
  }
}

// 格式化日期时间
function formatDateTime(dateTime: string | undefined) {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取考试类型文本
function getExamTypeText(type: string) {
  const types: Record<string, string> = {
    course: '课程考试',
    company: '企业笔试',
  }
  return types[type] || '未知类型'
}

// 获取分数颜色
function getScoreColor(score: number, passScore: number) {
  return score >= passScore ? '#67c23a' : '#f56c6c'
}
</script>

<style scoped lang="scss">
.exam-center {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .page-title {
      margin: 0;
      font-size: 24px;
      font-weight: 500;
    }

    .page-subtitle {
      margin: 5px 0 0 0;
      color: var(--el-text-color-secondary);
      font-size: 14px;
    }
  }

  .exam-tabs {
    .filter-card {
      margin-bottom: 20px;

      .filter-form {
        margin-bottom: 0;
      }
    }

    .stats-row {
      margin-bottom: 20px;

      .stat-card {
        .stat-content {
          display: flex;
          align-items: center;
          gap: 15px;

          .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: white;

            &.total {
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            }

            &.passed {
              background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            }

            &.failed {
              background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            }

            &.pending {
              background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            }
          }

          .stat-info {
            .stat-value {
              font-size: 28px;
              font-weight: bold;
              line-height: 1;
            }

            .stat-label {
              margin-top: 5px;
              color: var(--el-text-color-secondary);
              font-size: 14px;
            }
          }
        }
      }
    }

    .exam-list {
      min-height: 200px;

      .exam-card {
        .exam-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 15px;

          h4 {
            margin: 0;
            font-size: 16px;
            flex: 1;
          }

          .exam-tags {
            display: flex;
            gap: 5px;
          }
        }

        .exam-info {
          .info-item {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 8px 0;
            color: var(--el-text-color-secondary);
            font-size: 14px;

            .el-icon {
              font-size: 16px;
            }
          }
        }

        .exam-actions {
          margin-top: 15px;
          text-align: center;

          .el-button {
            width: 100%;
          }
        }
      }
    }

    .table-card {
      .pagination {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
      }
    }
  }

  .exam-detail {
    :deep(.el-descriptions) {
      .el-descriptions__body {
        .el-descriptions__table {
          .el-descriptions__cell {
            &.is-bordered-label {
              background-color: var(--el-fill-color-light);
            }
          }
        }
      }
    }
  }
}
</style>
