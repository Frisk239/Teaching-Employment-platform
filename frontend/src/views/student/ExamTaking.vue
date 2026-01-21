<template>
  <div class="exam-taking">
    <el-card shadow="never" class="exam-header-card">
      <div class="exam-header">
        <div class="exam-title">
          <h2>{{ examInfo.examName }}</h2>
          <el-tag :type="examInfo.examType === 'course' ? 'primary' : 'success'">
            {{ getExamTypeText(examInfo.examType) }}
          </el-tag>
        </div>
        <div class="exam-timer" :class="{ 'warning': remainingSeconds <= 300, 'danger': remainingSeconds <= 60 }">
          <el-statistic
            :value="remainingSeconds"
            format="HH:mm:ss"
            title="剩余时间"
          />
        </div>
      </div>
      <el-alert
        title="考试说明"
        type="info"
        :closable="false"
        style="margin-top: 15px"
      >
        <p>考试时长：{{ examInfo.duration }} 分钟</p>
        <p>满分：{{ examInfo.totalScore }} 分，及格分：{{ examInfo.passScore }} 分</p>
        <p>请认真作答，考试结束前请确保提交答案</p>
      </el-alert>
    </el-card>

    <!-- 主内容区：题目显示区 + 答题卡 -->
    <div class="exam-content" v-loading="loading">
      <!-- 左侧：题目显示区 -->
      <div class="question-area" v-if="questions.length > 0">
        <el-card shadow="never">
          <div v-if="currentQuestion">
            <!-- 题目标题区 -->
            <div class="question-header">
              <h3>{{ currentQuestionIndex + 1 }}/{{ questions.length }}. {{ currentQuestion.questionText }}</h3>
              <div class="question-meta">
                <el-tag :type="getQuestionTypeTag(currentQuestion.questionType)">
                  {{ getQuestionTypeText(currentQuestion.questionType) }}
                </el-tag>
                <span class="question-score">（{{ currentQuestion.score }} 分）</span>
              </div>
            </div>

            <!-- 题目选项/输入区 -->
            <div class="question-body">
              <!-- 单选题 -->
              <div v-if="currentQuestion.questionType === 'single_choice'" class="question-options">
                <el-radio-group v-model="answers[currentQuestion.id!]" @change="handleAnswerChange" :disabled="isTimeExpired">
                  <el-radio
                    v-for="option in parseOptions(currentQuestion.options!)"
                    :key="option.key"
                    :label="option.key"
                    :disabled="isTimeExpired"
                    style="display: block; margin-bottom: 12px"
                  >
                    {{ option.key }}. {{ option.value }}
                  </el-radio>
                </el-radio-group>
              </div>

              <!-- 多选题 -->
              <div v-else-if="currentQuestion.questionType === 'multiple_choice'" class="question-options">
                <el-checkbox-group v-model="answers[currentQuestion.id!]" @change="handleAnswerChange" :disabled="isTimeExpired">
                  <el-checkbox
                    v-for="option in parseOptions(currentQuestion.options!)"
                    :key="option.key"
                    :label="option.key"
                    :disabled="isTimeExpired"
                    style="display: block; margin-bottom: 12px"
                  >
                    {{ option.key }}. {{ option.value }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>

              <!-- 判断题 -->
              <div v-else-if="currentQuestion.questionType === 'true_false'" class="question-options">
                <el-radio-group v-model="answers[currentQuestion.id!]" @change="handleAnswerChange" :disabled="isTimeExpired">
                  <el-radio label="true" :disabled="isTimeExpired" style="display: block; margin-bottom: 12px">
                    正确
                  </el-radio>
                  <el-radio label="false" :disabled="isTimeExpired" style="display: block; margin-bottom: 12px">
                    错误
                  </el-radio>
                </el-radio-group>
              </div>

              <!-- 填空题 -->
              <div v-else-if="currentQuestion.questionType === 'fill_blank'" class="question-input">
                <el-input
                  v-model="answers[currentQuestion.id!]"
                  @input="handleAnswerChange"
                  placeholder="请输入答案"
                  type="textarea"
                  :rows="2"
                  :disabled="isTimeExpired"
                />
              </div>

              <!-- 简答题 -->
              <div v-else-if="currentQuestion.questionType === 'short_answer'" class="question-input">
                <el-input
                  v-model="answers[currentQuestion.id!]"
                  @input="handleAnswerChange"
                  placeholder="请输入答案"
                  type="textarea"
                  :rows="6"
                  :disabled="isTimeExpired"
                />
              </div>
            </div>

            <!-- 导航按钮 -->
            <div class="question-navigation">
              <el-button @click="goToPrevQuestion" :disabled="currentQuestionIndex === 0 || isTimeExpired" size="large">
                上一题
              </el-button>
              <el-button
                v-if="currentQuestionIndex < questions.length - 1"
                type="primary"
                @click="goToNextQuestion"
                :disabled="isTimeExpired"
                size="large"
              >
                下一题
              </el-button>
              <el-button v-else type="success" @click="handleSubmit" :loading="submitting" :disabled="isTimeExpired" size="large">
                提交试卷
              </el-button>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：答题卡 -->
      <div class="answer-card" v-if="questions.length > 0">
        <el-card shadow="never" class="answer-card-content">
          <div class="answer-progress">
            已答 {{ answeredCount }}/{{ questions.length }} 题
          </div>
          <el-divider />
          <div class="question-grid">
            <div
              v-for="(question, index) in questions"
              :key="question.id"
              :class="[
                'question-number',
                { answered: isQuestionAnswered(question.id!) },
                { current: index === currentQuestionIndex },
                { disabled: isTimeExpired }
              ]"
              @click="!isTimeExpired && goToQuestion(index)"
            >
              {{ index + 1 }}
            </div>
          </div>

          <!-- 答题卡图例 -->
          <div class="answer-legend">
            <div class="legend-item">
              <span class="legend-box answered"></span>
              <span>已答</span>
            </div>
            <div class="legend-item">
              <span class="legend-box current"></span>
              <span>当前</span>
            </div>
            <div class="legend-item">
              <span class="legend-box"></span>
              <span>未答</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 无题目提示 -->
      <el-empty v-if="questions.length === 0" description="暂无题目" />
    </div>

    <!-- 考试结果对话框 -->
    <el-dialog
      v-model="showResultDialog"
      title="考试结果"
      width="600px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div v-if="examResult" class="exam-result">
        <!-- graded状态：已评阅 -->
        <div v-if="examResult.gradingStatus === 'graded'">
          <el-result
            :icon="examResult.passed ? 'success' : 'error'"
            :title="examResult.passed ? '恭喜！考试通过' : '很遗憾，考试未通过'"
            :sub-title="`总分：${examResult.totalScore} 分`"
          >
            <template #extra>
              <div class="result-details">
                <p><strong>客观题得分：</strong>{{ examResult.objectiveScore || 0 }} 分</p>
                <p><strong>主观题得分：</strong>{{ examResult.subjectiveScore || 0 }} 分</p>
                <p><strong>总分：</strong>
                  <span :style="{
                    color: examResult.passed ? '#67c23a' : '#f56c6c',
                    fontSize: '24px',
                    fontWeight: 'bold'
                  }">
                    {{ examResult.totalScore || 0 }}
                  </span> 分
                </p>
                <p><strong>及格分数：</strong>{{ examInfo.passScore }} 分</p>
                <el-tag :type="examResult.passed ? 'success' : 'danger'" size="large">
                  {{ examResult.passed ? '及格' : '不及格' }}
                </el-tag>
              </div>
            </template>
          </el-result>
        </div>

        <!-- pending状态：待评阅 -->
        <div v-else>
          <el-result
            icon="info"
            title="试卷已提交"
            sub-title="主观题正在等待教师评阅"
          >
            <template #extra>
              <div class="result-details">
                <p><strong>客观题得分：</strong>{{ examResult.objectiveScore || 0 }} 分（已自动评阅）</p>
                <p><strong>主观题得分：</strong>待评阅</p>
                <p><strong>总分：</strong>待评阅</p>
                <el-alert
                  title="提示"
                  type="info"
                  :closable="false"
                  style="margin-top: 20px"
                >
                  教师评阅完成后，您可以在考试中心查看最终成绩
                </el-alert>
              </div>
            </template>
          </el-result>
        </div>
      </div>

      <template #footer>
        <el-button type="primary" size="large" @click="goToExamCenter">
          返回考试中心
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { http } from '@/utils/request'
import { getExamByIdApi, type Exam } from '@/api/exam'
import {
  getExamRecordByIdApi,
  submitExamApi,
  autoGradeObjectiveQuestionsApi,
  type ExamRecord,
} from '@/api/exam-record'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const recordId = computed(() => Number(route.params.recordId))

const loading = ref(false)
const submitting = ref(false)

// 考试结果对话框
const showResultDialog = ref(false)
const examResult = ref<ExamRecord | null>(null)

const examInfo = reactive<Exam>({
  examName: '',
  examType: 'course',
  duration: 0,
  totalScore: 0,
  passScore: 0,
})

const questions = ref<any[]>([])
const answers = reactive<Record<number, any>>({})
const currentQuestionIndex = ref(0) // 当前题目索引

const remainingSeconds = ref(0)
let timerInterval: any = null
const isTimeExpired = ref(false) // 标记时间是否已到
const hasShownWarning = ref(false) // 标记是否已显示警告

// 自动保存相关
const AUTO_SAVE_INTERVAL = 60 * 1000 // 60秒
let autoSaveTimer: number | null = null

onMounted(async () => {
  await loadExamRecord()

  // 先尝试从localStorage恢复答案
  loadFromLocalStorage()

  // 如果localStorage为空，初始化答案
  if (Object.keys(answers).length === 0) {
    initAnswers()
  }

  startTimer()
  startAutoSave() // 启动定时保存
})

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
  stopAutoSave() // 停止自动保存定时器
})

async function loadExamRecord() {
  if (!recordId.value) {
    ElMessage.error('考试记录ID不存在')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await getExamRecordByIdApi(recordId.value)
    const record = res

    if (record.status !== 'taking') {
      ElMessage.warning('该考试已提交')
      router.back()
      return
    }

    // 加载试卷信息
    const examRes = await getExamByIdApi(record.examId!)
    Object.assign(examInfo, examRes)

    // 解析题目快照
    if (record.questionsSnapshot) {
      try {
        const snapshot = JSON.parse(record.questionsSnapshot)
        questions.value = snapshot.questions || []
      } catch (error) {
        ElMessage.error('解析题目失败')
      }
    }

    // 计算剩余时间
    const startTime = new Date(record.startTime!)
    const endTime = new Date(startTime.getTime() + examInfo.duration * 60 * 1000)
    const now = new Date()
    remainingSeconds.value = Math.max(0, Math.floor((endTime.getTime() - now.getTime()) / 1000))
  } catch (error) {
    ElMessage.error('加载考试信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

function startTimer() {
  if (timerInterval) {
    clearInterval(timerInterval)
  }

  timerInterval = setInterval(() => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value--

      // 最后1分钟显示警告（仅显示一次）
      if (remainingSeconds.value === 60 && !hasShownWarning.value) {
        hasShownWarning.value = true
        showTimeWarning()
      }
    } else {
      // 时间到，自动提交
      isTimeExpired.value = true
      handleAutoSubmit()
    }
  }, 1000)
}

// 显示时间警告
function showTimeWarning() {
  ElMessageBox.alert(
    '考试时间仅剩1分钟，请尽快完成答题并提交试卷！',
    '时间警告',
    {
      type: 'warning',
      confirmButtonText: '我知道了',
      autofocus: false,
      closeOnClickModal: false,
      closeOnPressEscape: false,
    }
  )
}

// 自动提交（时间到）
async function handleAutoSubmit() {
  // 停止计时器
  if (timerInterval) {
    clearInterval(timerInterval)
  }

  // 先保存所有答案到后端
  await saveAnswersToBackend()

  ElMessageBox.alert(
    '考试时间已到，试卷已自动提交！',
    '自动提交',
    {
      type: 'warning',
      confirmButtonText: '确定',
      showClose: false,
      closeOnClickModal: false,
      closeOnPressEscape: false,
      beforeClose: async (action, instance, done) => {
        if (action === 'confirm') {
          instance.confirmButtonLoading = true
          try {
            await submitExamPaper()
            done()
          } catch (error) {
            instance.confirmButtonLoading = false
          }
        } else {
          done()
        }
      },
    }
  )
}

async function handleSubmit() {
  // 先保存所有答案到后端
  await saveAnswersToBackend()

  try {
    await ElMessageBox.confirm(
      remainingSeconds.value <= 0
        ? '考试时间已到，系统将自动提交试卷'
        : '确定要提交试卷吗？提交后无法修改。',
      '提交试卷',
      {
        type: 'warning',
        confirmButtonText: '确定提交',
        cancelButtonText: '取消',
        beforeClose: async (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            try {
              await submitExamPaper()
              done()
            } catch (error) {
              instance.confirmButtonLoading = false
            }
          } else {
            done()
          }
        },
      }
    )
  } catch (error) {
    // 用户取消
  }
}

async function submitExamPaper() {
  submitting.value = true
  try {
    // 提交试卷
    await submitExamApi(recordId.value)

    // 自动评阅客观题
    await autoGradeObjectiveQuestionsApi(recordId.value)

    ElMessage.success('试卷提交成功！')

    // 获取考试结果
    const result = await getExamRecordByIdApi(recordId.value)
    examResult.value = result.data || result
    showResultDialog.value = true

    // 清除localStorage
    clearLocalStorage()

    // 停止自动保存定时器
    stopAutoSave()

    // 停止计时器
    if (timerInterval) {
      clearInterval(timerInterval)
      timerInterval = null
    }
  } catch (error: any) {
    ElMessage.error(error.message || '提交试卷失败')
    throw error
  } finally {
    submitting.value = false
  }
}

function parseOptions(optionsStr: string) {
  try {
    const options = JSON.parse(optionsStr)
    return Object.entries(options).map(([key, value]) => ({ key, value }))
  } catch (error) {
    return []
  }
}

function getExamTypeText(type: string) {
  const types: Record<string, string> = {
    course: '课程考试',
    company: '企业笔试',
  }
  return types[type] || '未知类型'
}

function getQuestionTypeText(type: string) {
  const types: Record<string, string> = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    true_false: '判断题',
    fill_blank: '填空题',
    short_answer: '简答题',
  }
  return types[type] || '未知类型'
}

function getQuestionTypeTag(type: string) {
  const tags: Record<string, string> = {
    single_choice: '',
    multiple_choice: 'success',
    true_false: 'warning',
    fill_blank: 'info',
    short_answer: 'primary',
  }
  return tags[type] || ''
}

// 当前题目
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value]
})

// 已答题数量
const answeredCount = computed(() => {
  let count = 0
  questions.value.forEach(question => {
    if (isQuestionAnswered(question.id!)) {
      count++
    }
  })
  return count
})

// 判断题目是否已答
function isQuestionAnswered(questionId: number): boolean {
  const answer = answers[questionId]
  if (answer === null || answer === undefined) {
    return false
  }
  if (typeof answer === 'string') {
    return answer.trim() !== ''
  }
  if (Array.isArray(answer)) {
    return answer.length > 0
  }
  return true
}

// 初始化答案
function initAnswers() {
  questions.value.forEach(question => {
    if (question.questionType === 'multiple_choice') {
      answers[question.id!] = []
    } else {
      answers[question.id!] = ''
    }
  })
}

// 保存到localStorage
function saveToLocalStorage() {
  const key = `exam_answers_${recordId.value}`
  try {
    localStorage.setItem(key, JSON.stringify(answers))
  } catch (error) {
    console.error('保存到localStorage失败', error)
  }
}

// 从localStorage恢复答案
function loadFromLocalStorage() {
  const key = `exam_answers_${recordId.value}`
  const saved = localStorage.getItem(key)
  if (saved) {
    try {
      const parsed = JSON.parse(saved)
      Object.assign(answers, parsed)
      console.log('从localStorage恢复答案成功', parsed)
    } catch (error) {
      console.error('恢复答案失败', error)
    }
  }
}

// 清除localStorage
function clearLocalStorage() {
  const key = `exam_answers_${recordId.value}`
  try {
    localStorage.removeItem(key)
    console.log('清除localStorage成功')
  } catch (error) {
    console.error('清除localStorage失败', error)
  }
}

// 处理答案变化
function handleAnswerChange() {
  // 立即保存到localStorage
  saveToLocalStorage()
}

// 启动自动保存
function startAutoSave() {
  autoSaveTimer = window.setInterval(async () => {
    await saveAnswersToBackend()
  }, AUTO_SAVE_INTERVAL)
  console.log('自动保存定时器已启动，间隔：', AUTO_SAVE_INTERVAL / 1000, '秒')
}

// 停止自动保存
function stopAutoSave() {
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer)
    autoSaveTimer = null
    console.log('自动保存定时器已停止')
  }
}

// 保存答案到后端
async function saveAnswersToBackend() {
  if (!recordId.value) return

  // 收集有答案的题目
  const answersList = Object.entries(answers)
    .filter(([_, answer]) => {
      // 过滤掉空答案
      if (answer === null || answer === undefined) return false
      if (typeof answer === 'string') return answer.trim() !== ''
      if (Array.isArray(answer)) return answer.length > 0
      return true
    })
    .map(([questionId, answer]) => {
      const question = questions.value.find(q => q.id === Number(questionId))
      return {
        questionId: Number(questionId),
        questionType: question?.questionType,
        studentAnswer: Array.isArray(answer) ? answer.join(',') : answer
      }
    })

  if (answersList.length === 0) {
    console.log('没有答案需要保存')
    return // 没有答案需要保存
  }

  try {
    const res = await http.post(`/exam-record/${recordId.value}/answers`, {
      answers: answersList
    })

    // 静默保存成功
    console.log('答案自动保存成功:', res)
  } catch (error) {
    console.error('自动保存答案失败:', error)
    ElMessage.warning('答案保存失败，请检查网络连接')
  }
}

// 跳转到指定题目
function goToQuestion(index: number) {
  if (index >= 0 && index < questions.value.length) {
    currentQuestionIndex.value = index
  }
}

// 上一题
function goToPrevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 下一题
function goToNextQuestion() {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

// 跳转到考试中心
function goToExamCenter() {
  router.push({ name: 'ExamCenter' })
}
</script>

<style scoped lang="scss">
.exam-taking {
  padding: 20px;
  min-height: calc(100vh - 100px);

  .exam-header-card {
    margin-bottom: 20px;

    .exam-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .exam-title {
        display: flex;
        align-items: center;
        gap: 15px;

        h2 {
          margin: 0;
          font-size: 24px;
          font-weight: 500;
        }
      }

      .exam-timer {
        text-align: center;
        padding: 15px 25px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 10px;
        color: white;
        transition: all 0.5s ease;

        // 最后5分钟：橙黄色警告
        &.warning {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          animation: pulse 2s infinite;
        }

        // 最后1分钟：红色危险
        &.danger {
          background: linear-gradient(135deg, #ff416c 0%, #ff4b2b 100%);
          animation: pulse 1s infinite;
        }

        @keyframes pulse {
          0%, 100% {
            transform: scale(1);
            box-shadow: 0 0 0 0 rgba(255, 65, 108, 0.7);
          }
          50% {
            transform: scale(1.02);
            box-shadow: 0 0 20px 0 rgba(255, 65, 108, 0.4);
          }
        }

        :deep(.el-statistic) {
          .el-statistic__head {
            color: rgba(255, 255, 255, 0.9);
            font-size: 14px;
            margin-bottom: 5px;
          }

          .el-statistic__content {
            font-size: 32px;
            font-weight: bold;
          }
        }
      }
    }
  }

  .exam-content {
    display: flex;
    gap: 20px;
    align-items: flex-start;

    // 左侧：题目显示区
    .question-area {
      flex: 7;
      min-width: 0;

      .el-card {
        :deep(.el-card__body) {
          padding: 30px;
        }
      }

      .question-header {
        margin-bottom: 25px;
        padding-bottom: 15px;
        border-bottom: 2px solid var(--el-border-color-lighter);

        h3 {
          margin: 0 0 10px 0;
          font-size: 18px;
          font-weight: 500;
          line-height: 1.6;
        }

        .question-meta {
          display: flex;
          align-items: center;
          gap: 10px;

          .question-score {
            color: var(--el-text-color-secondary);
            font-size: 14px;
          }
        }
      }

      .question-body {
        margin: 25px 0;
        min-height: 200px;

        .question-options,
        .question-input {
          padding: 0;
        }

        :deep(.el-radio-group),
        :deep(.el-checkbox-group) {
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        :deep(.el-radio),
        :deep(.el-checkbox) {
          margin-right: 0;
          padding: 10px 15px;
          border-radius: 6px;
          transition: all 0.3s;

          &:hover {
            background-color: var(--el-fill-color-light);
          }
        }

        :deep(.el-textarea__inner) {
          font-size: 15px;
          line-height: 1.6;
        }
      }

      .question-navigation {
        display: flex;
        justify-content: space-between;
        margin-top: 30px;
        padding-top: 20px;
        border-top: 1px solid var(--el-border-color-lighter);

        .el-button {
          min-width: 120px;
        }
      }
    }

    // 右侧：答题卡
    .answer-card {
      flex: 3;
      position: sticky;
      top: 20px;
      max-height: calc(100vh - 140px);
      overflow-y: auto;

      .answer-card-content {
        :deep(.el-card__body) {
          padding: 20px;
        }
      }

      .answer-progress {
        text-align: center;
        font-size: 16px;
        font-weight: bold;
        color: var(--el-color-primary);
        margin-bottom: 10px;
      }

      .question-grid {
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        gap: 10px;
        margin-bottom: 20px;

        .question-number {
          width: 100%;
          aspect-ratio: 1;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 2px solid var(--el-border-color);
          border-radius: 8px;
          cursor: pointer;
          font-weight: bold;
          font-size: 14px;
          transition: all 0.3s;
          background-color: white;
          user-select: none;

          &:hover {
            border-color: var(--el-color-primary);
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
          }

          &.answered {
            background-color: var(--el-color-success);
            color: white;
            border-color: var(--el-color-success);

            &:hover {
              background-color: var(--el-color-success-dark-2);
              border-color: var(--el-color-success-dark-2);
            }
          }

          &.current {
            background-color: var(--el-color-primary);
            color: white;
            border-color: var(--el-color-primary);
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.4);

            &:hover {
              background-color: var(--el-color-primary-dark-2);
              border-color: var(--el-color-primary-dark-2);
            }
          }

          &.answered.current {
            background-color: var(--el-color-warning);
            border-color: var(--el-color-warning);

            &:hover {
              background-color: var(--el-color-warning-dark-2);
              border-color: var(--el-color-warning-dark-2);
            }
          }

          // 禁用状态（时间到期）
          &.disabled {
            cursor: not-allowed;
            opacity: 0.5;

            &:hover {
              border-color: var(--el-border-color);
              transform: none;
              box-shadow: none;
            }
          }
        }
      }

      .answer-legend {
        display: flex;
        justify-content: center;
        gap: 20px;
        flex-wrap: wrap;
        padding-top: 10px;
        border-top: 1px solid var(--el-border-color-lighter);

        .legend-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: var(--el-text-color-regular);

          .legend-box {
            width: 20px;
            height: 20px;
            border: 2px solid var(--el-border-color);
            border-radius: 4px;
            display: inline-block;

            &.answered {
              background-color: var(--el-color-success);
              border-color: var(--el-color-success);
            }

            &.current {
              background-color: var(--el-color-primary);
              border-color: var(--el-color-primary);
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .exam-taking {
    .exam-content {
      flex-direction: column;

      .question-area {
        flex: none;
        width: 100%;
      }

      .answer-card {
        flex: none;
        width: 100%;
        position: static;
        max-height: none;
      }
    }
  }
}

// 考试结果对话框样式
.exam-result {
  .result-details {
    text-align: center;
    padding: 20px;

    p {
      font-size: 16px;
      margin: 15px 0;

      strong {
        font-weight: 600;
      }
    }
  }
}
</style>
