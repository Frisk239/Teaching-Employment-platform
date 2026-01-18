<template>
  <div class="my-exams">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">我的考试</h1>
        <p class="page-subtitle">查看和参加课程相关考试</p>
      </div>
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
              <div class="stat-label">总考试数</div>
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
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待考试</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon in-progress">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.inProgress }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <el-card shadow="never" class="filter-card">
      <el-form :model="searchForm" inline class="filter-form">
        <el-form-item label="考试状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="未开始" value="not_started" />
            <el-option label="进行中" value="in_progress" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>

        <el-form-item label="课程">
          <el-select
            v-model="searchForm.courseId"
            placeholder="全部课程"
            clearable
            style="width: 200px"
            @change="handleSearch"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 考试列表 -->
    <el-card shadow="never" class="exam-list">
      <div v-if="examGroups.length > 0">
        <el-collapse v-model="activeGroups">
          <el-collapse-item
            v-for="group in examGroups"
            :key="group.courseId"
            :name="group.courseId"
          >
            <template #title>
              <div class="course-title">
                <el-icon><Reading /></el-icon>
                <span>{{ group.courseName }}</span>
                <el-tag size="small" type="info" style="margin-left: 10px">
                  {{ group.exams.length }} 门考试
                </el-tag>
              </div>
            </template>

            <el-row :gutter="16">
              <el-col
                v-for="exam in group.exams"
                :key="exam.id"
                :span="8"
                style="margin-bottom: 16px"
              >
                <el-card shadow="hover" class="exam-card">
                  <div class="exam-header">
                    <h4>{{ exam.title }}</h4>
                    <el-tag
                      :type="getStatusType(exam.status)"
                      size="small"
                    >
                      {{ getStatusText(exam.status) }}
                    </el-tag>
                  </div>

                  <div class="exam-info">
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
                    <p v-if="exam.status === 'completed'" class="info-item">
                      <el-icon><TrendCharts /></el-icon>
                      <span>得分：<strong style="font-size: 18px; color: #409eff">{{ exam.score }}</strong> / {{ exam.fullScore }}</span>
                    </p>
                  </div>

                  <div class="exam-actions">
                    <el-button
                      v-if="exam.status === 'not_started'"
                      type="primary"
                      :disabled="!canStartExam(exam)"
                      @click="startExam(exam)"
                    >
                      {{ canStartExam(exam) ? '开始考试' : '未到考试时间' }}
                    </el-button>
                    <el-button
                      v-else-if="exam.status === 'in_progress'"
                      type="warning"
                      @click="continueExam(exam)"
                    >
                      继续考试
                    </el-button>
                    <el-button
                      v-else-if="exam.status === 'completed'"
                      type="success"
                      @click="viewResult(exam)"
                    >
                      查看成绩
                    </el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
      </div>

      <el-empty v-else description="暂无考试" />
    </el-card>

    <!-- 考试对话框 -->
    <el-dialog
      v-model="examDialogVisible"
      :title="currentExam?.title"
      width="900px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div v-if="currentExam" class="exam-content">
        <!-- 考试信息 -->
        <div class="exam-info-header">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="考试时长">{{ currentExam.duration }} 分钟</el-descriptions-item>
            <el-descriptions-item label="满分">{{ currentExam.fullScore }} 分</el-descriptions-item>
            <el-descriptions-item label="及格分">{{ currentExam.passingScore }} 分</el-descriptions-item>
          </el-descriptions>

          <div class="timer-display">
            <el-statistic
              :value="remainingSeconds"
              format="HH:mm:ss"
              title="剩余时间"
            />
          </div>
        </div>

        <el-divider />

        <!-- 考试题目（示例） -->
        <div class="exam-questions">
          <h3>考试题目</h3>
          <el-alert
            title="提示"
            type="info"
            :closable="false"
            style="margin-bottom: 20px"
          >
            此为考试界面示例，实际题目需要关联题目库系统
          </el-alert>

          <div class="question-item">
            <h4>1. 以下哪个是Vue.js的核心特性？</h4>
            <el-radio-group v-model="answers.q1">
              <el-radio label="A">组件化开发</el-radio>
              <el-radio label="B">响应式数据绑定</el-radio>
              <el-radio label="C">虚拟DOM</el-radio>
              <el-radio label="D">以上都是</el-radio>
            </el-radio-group>
          </div>

          <div class="question-item" style="margin-top: 20px">
            <h4>2. Spring Boot的启动注解是？</h4>
            <el-input
              v-model="answers.q2"
              placeholder="请输入答案"
            />
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="examDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitExam">提交试卷</el-button>
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
  Loading,
  CircleCheck,
  Reading,
  Timer,
  Calendar,
  TrendCharts,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import type { StudentExamRecord, Course } from '@/api/types'

// 状态管理
const authStore = useAuthStore()
const studentId = computed(() => authStore.studentId)

// 统计数据
const stats = reactive({
  total: 0,
  pending: 0,
  inProgress: 0,
  completed: 0,
})

// 搜索表单
const searchForm = reactive({
  status: '',
  courseId: null as number | null,
})

// 课程列表
const courses = ref<Course[]>([])

// 考试分组（按课程）
const examGroups = ref<any[]>([])
const activeGroups = ref<string[]>([])

// 当前考试
const currentExam = ref<any>(null)
const examDialogVisible = ref(false)
const remainingSeconds = ref(0)
let timerInterval: any = null

// 答案（示例）
const answers = reactive({
  q1: '',
  q2: '',
})

// 加载数据
onMounted(async () => {
  await loadExamData()
})

// 加载考试数据
async function loadExamData() {
  // TODO: 调用实际API获取考试数据
  // 模拟数据
  courses.value = [
    { id: 1, courseName: '数据结构与算法' },
    { id: 2, courseName: '计算机网络' },
    { id: 3, courseName: '操作系统' },
  ]

  examGroups.value = [
    {
      courseId: 1,
      courseName: '数据结构与算法',
      exams: [
        {
          id: 1,
          title: '期中考试',
          duration: 90,
          startTime: '2025-01-20 10:00:00',
          endTime: '2025-01-20 12:00:00',
          fullScore: 100,
          passingScore: 60,
          status: 'not_started',
        },
        {
          id: 2,
          title: '单元测验 - 树与图',
          duration: 60,
          startTime: '2025-01-15 14:00:00',
          endTime: '2025-01-15 16:00:00',
          fullScore: 50,
          passingScore: 30,
          status: 'completed',
          score: 85,
        },
      ],
    },
    {
      courseId: 2,
      courseName: '计算机网络',
      exams: [
        {
          id: 3,
          title: 'TCP/IP协议测试',
          duration: 45,
          startTime: '2025-01-25 09:00:00',
          endTime: '2025-01-25 10:00:00',
          fullScore: 100,
          passingScore: 60,
          status: 'in_progress',
        },
      ],
    },
  ]

  // 计算统计数据
  const allExams = examGroups.value.flatMap(g => g.exams)
  stats.total = allExams.length
  stats.pending = allExams.filter(e => e.status === 'not_started').length
  stats.inProgress = allExams.filter(e => e.status === 'in_progress').length
  stats.completed = allExams.filter(e => e.status === 'completed').length
}

// 判断是否可以开始考试
function canStartExam(exam: any) {
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  return now >= startTime && now <= endTime && exam.status === 'not_started'
}

// 开始考试
function startExam(exam: any) {
  currentExam.value = exam
  examDialogVisible.value = true

  // 计算剩余时间（秒）
  const endTime = new Date(exam.endTime)
  const now = new Date()
  remainingSeconds.value = Math.floor((endTime.getTime() - now.getTime()) / 1000)

  // 启动倒计时
  startTimer()
}

// 继续考试
function continueExam(exam: any) {
  startExam(exam)
}

// 启动倒计时
function startTimer() {
  if (timerInterval) {
    clearInterval(timerInterval)
  }

  timerInterval = setInterval(() => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value--
    } else {
      // 时间到，自动提交
      submitExam()
    }
  }, 1000)
}

// 提交试卷
async function submitExam() {
  try {
    await ElMessageBox.confirm('确定要提交试卷吗？提交后无法修改。', '提示', {
      type: 'warning',
    })

    // TODO: 调用实际API提交答案
    ElMessage.success('试卷提交成功！')
    examDialogVisible.value = false

    if (timerInterval) {
      clearInterval(timerInterval)
    }

    // 重新加载数据
    await loadExamData()
  } catch (error) {
    // 用户取消
  }
}

// 查看成绩
function viewResult(exam: any) {
  ElMessage.info('成绩详情功能开发中...')
}

// 搜索
function handleSearch() {
  // TODO: 实现搜索逻辑
  loadExamData()
}

// 格式化日期时间
function formatDateTime(dateTime: string) {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取状态类型
function getStatusType(status: string) {
  const types: Record<string, any> = {
    not_started: 'info',
    in_progress: 'warning',
    completed: 'success',
  }
  return types[status] || 'info'
}

// 获取状态文本
function getStatusText(status: string) {
  const texts: Record<string, string> = {
    not_started: '未开始',
    in_progress: '进行中',
    completed: '已完成',
  }
  return texts[status] || '未知'
}
</script>

<style scoped lang="scss">
.my-exams {
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

          &.pending {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }

          &.in-progress {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }

          &.completed {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

  .filter-card {
    margin-bottom: 20px;

    .filter-form {
      margin-bottom: 0;
    }
  }

  .exam-list {
    .course-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 500;
    }

    .exam-card {
      .exam-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;

        h4 {
          margin: 0;
          font-size: 16px;
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

  .exam-content {
    .exam-info-header {
      margin-bottom: 20px;

      .timer-display {
        margin-top: 20px;
        text-align: center;
        padding: 15px;
        background-color: var(--el-fill-color-light);
        border-radius: 8px;
      }
    }

    .exam-questions {
      .question-item {
        h4 {
          margin: 0 0 10px 0;
          font-size: 16px;
        }

        .el-radio-group {
          display: flex;
          flex-direction: column;
          gap: 10px;
        }
      }
    }
  }
}
</style>
