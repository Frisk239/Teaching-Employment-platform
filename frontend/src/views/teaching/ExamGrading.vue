<template>
  <div class="exam-grading">
    <el-card shadow="never">
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待评阅" name="pending">
          <span class="tab-label">
            待评阅
            <el-badge v-if="pendingCount > 0" :value="pendingCount" class="tab-badge" />
          </span>
        </el-tab-pane>
        <el-tab-pane label="已评阅" name="graded">
          <span class="tab-label">
            已评阅
            <el-badge v-if="gradedCount > 0" :value="gradedCount" class="tab-badge" />
          </span>
        </el-tab-pane>
      </el-tabs>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="试卷名称">
          <el-input
            v-model="searchForm.examName"
            placeholder="请输入试卷名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchForm.studentName"
            placeholder="请输入学生姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="examName" label="试卷名称" min-width="180" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column prop="objectiveScore" label="客观题得分" width="110" align="center">
          <template #default="{ row }">
            <el-tag type="success">{{ row.objectiveScore || 0 }} 分</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subjectiveScore" label="主观题得分" width="110" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.gradingStatus === 'graded'" type="warning">{{ row.subjectiveScore || 0 }} 分</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.gradingStatus === 'graded'" type="primary">{{ row.totalScore || 0 }} 分</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="passed" label="及格状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.gradingStatus === 'graded'" :type="row.passed ? 'success' : 'danger'">
              {{ row.passed ? '及格' : '不及格' }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="graderName" label="评阅人" width="120" />
        <el-table-column prop="gradingTime" label="评阅时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleGrade(row)"
            >
              {{ row.gradingStatus === 'graded' ? '查看详情' : '开始评阅' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 评阅对话框 -->
    <el-dialog
      v-model="gradingDialogVisible"
      :title="gradingDialogTitle"
      width="1200px"
      @close="handleGradingDialogClose"
    >
      <div v-loading="detailLoading" class="grading-container">
        <!-- 考试信息 -->
        <div class="exam-info">
          <h3>{{ currentExamRecord.examName }}</h3>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="学生姓名">{{ currentExamRecord.studentName }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ currentExamRecord.submitTime }}</el-descriptions-item>
            <el-descriptions-item label="客观题得分">
              <el-tag type="success">{{ currentExamRecord.objectiveScore || 0 }} 分</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 题目列表 -->
        <div class="questions-container">
          <h4>试题评阅</h4>

          <!-- 客观题列表 -->
          <div v-if="objectiveQuestions.length > 0" class="question-section">
            <h5>客观题（已自动评阅）</h5>
            <div v-for="(question, index) in objectiveQuestions" :key="question.id" class="question-item">
              <div class="question-header">
                <span class="question-index">客观题 {{ index + 1 }}</span>
                <el-tag :type="question.isCorrect ? 'success' : 'danger'" size="small">
                  {{ question.isCorrect ? '正确' : '错误' }}
                </el-tag>
                <el-tag type="info" size="small">{{ question.score }} / {{ question.fullScore }} 分</el-tag>
              </div>
              <div class="question-content">{{ question.questionText }}</div>
              <div v-if="question.options" class="question-options">
                <div v-for="(option, optIndex) in parseOptions(question.options)" :key="optIndex" class="option-item">
                  {{ String.fromCharCode(65 + optIndex) }}. {{ option }}
                </div>
              </div>
              <div class="answer-section">
                <div class="answer-item">
                  <span class="label">学生答案：</span>
                  <span class="value" :class="{ correct: question.isCorrect, incorrect: !question.isCorrect }">
                    {{ formatAnswer(question.studentAnswer, question.questionType) }}
                  </span>
                </div>
                <div class="answer-item">
                  <span class="label">正确答案：</span>
                  <span class="value correct-answer">{{ formatAnswer(question.correctAnswer, question.questionType) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 主观题列表 -->
          <div v-if="subjectiveQuestions.length > 0" class="question-section">
            <h5>主观题（需要评阅）</h5>
            <div v-for="(question, index) in subjectiveQuestions" :key="question.id" class="question-item subjective">
              <div class="question-header">
                <span class="question-index">主观题 {{ index + 1 }}</span>
                <el-tag type="warning" size="small">{{ question.fullScore }} 分</el-tag>
              </div>
              <div class="question-content">{{ question.questionText }}</div>
              <div class="student-answer">
                <div class="answer-label">学生答案：</div>
                <div class="answer-text">{{ question.studentAnswer || '未作答' }}</div>
              </div>
              <div class="grading-section">
                <div class="score-input">
                  <span class="label">评分：</span>
                  <el-input-number
                    v-model="question.score"
                    :min="0"
                    :max="question.fullScore"
                    :precision="0"
                    :disabled="currentExamRecord.gradingStatus === 'graded'"
                    controls-position="right"
                    style="width: 150px"
                  />
                  <span class="score-max"> / {{ question.fullScore }} 分</span>
                </div>
                <div class="comment-input">
                  <span class="label">批语：</span>
                  <el-input
                    v-model="question.comment"
                    type="textarea"
                    :rows="3"
                    :disabled="currentExamRecord.gradingStatus === 'graded'"
                    placeholder="请输入批语"
                    style="flex: 1"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 评分统计 -->
          <div class="score-summary">
            <el-descriptions :column="4" border>
              <el-descriptions-item label="客观题得分">{{ currentExamRecord.objectiveScore || 0 }} 分</el-descriptions-item>
              <el-descriptions-item label="主观题得分">
                <span class="highlight">{{ subjectiveTotalScore }}</span> 分
              </el-descriptions-item>
              <el-descriptions-item label="总分">
                <span class="highlight">{{ totalScore }}</span> 分
              </el-descriptions-item>
              <el-descriptions-item label="及格状态">
                <el-tag :type="isPassed ? 'success' : 'danger'">{{ isPassed ? '及格' : '不及格' }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="gradingDialogVisible = false">取消</el-button>
        <el-button
          v-if="currentExamRecord.gradingStatus !== 'graded'"
          type="primary"
          :disabled="subjectiveQuestions.length === 0"
          @click="handleSubmitGrading"
        >
          提交评阅
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  getExamRecordPageApi,
  getExamRecordByIdApi,
  gradeSubjectiveQuestionsApi,
  type ExamRecord
} from '@/api/exam-record'
import {
  getStudentAnswersByExamRecordApi,
  batchUpdateStudentAnswersApi,
  type StudentAnswer
} from '@/api/student-answer'

const authStore = useAuthStore()

// 标签页
const activeTab = ref<'pending' | 'graded'>('pending')
const pendingCount = ref(0)
const gradedCount = ref(0)

// 搜索表单
const searchForm = reactive({
  examName: '',
  studentName: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<ExamRecord[]>([])

// 评阅对话框
const gradingDialogVisible = ref(false)
const detailLoading = ref(false)
const gradingDialogTitle = computed(() =>
  currentExamRecord.value.gradingStatus === 'graded' ? '查看评阅详情' : '评阅试卷'
)

const currentExamRecord = ref<Partial<ExamRecord>>({})
const studentAnswers = ref<StudentAnswer[]>([])

// 客观题和主观题分类
const objectiveQuestions = computed(() => {
  return studentAnswers.value.filter(
    (answer) =>
      answer.questionType === 'single_choice' ||
      answer.questionType === 'multiple_choice' ||
      answer.questionType === 'true_false'
  )
})

const subjectiveQuestions = computed(() => {
  return studentAnswers.value.filter(
    (answer) => answer.questionType === 'short_answer' || answer.questionType === 'fill_blank'
  )
})

// 主观题总分
const subjectiveTotalScore = computed(() => {
  return subjectiveQuestions.value.reduce((sum, q) => sum + (q.score || 0), 0)
})

// 总分
const totalScore = computed(() => {
  return (currentExamRecord.value.objectiveScore || 0) + subjectiveTotalScore.value
})

// 及格状态（需要从试卷信息中获取及格分数，这里暂时使用60分作为默认值）
const isPassed = computed(() => {
  // TODO: 从Exam信息中获取及格分数
  const passScore = 60
  return totalScore.value >= passScore
})

// 切换标签页
const handleTabChange = () => {
  pagination.current = 1
  fetchData()
}

// 获取考试记录列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      gradingStatus: activeTab.value,
      status: 'submitted' // 只显示已提交的试卷
    }
    const response = await getExamRecordPageApi(params)
    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total

      // 更新计数
      if (activeTab.value === 'pending') {
        pendingCount.value = response.data.total
      } else {
        gradedCount.value = response.data.total
      }
    } else {
      ElMessage.error(response.message || '获取考试记录列表失败')
    }
  } catch (error) {
    console.error('获取考试记录列表失败:', error)
    ElMessage.error('获取考试记录列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  // TODO: 实现搜索功能
  ElMessage.info('搜索功能待实现')
}

// 重置
const handleReset = () => {
  searchForm.examName = ''
  searchForm.studentName = ''
  pagination.current = 1
  fetchData()
}

// 评阅试卷
const handleGrade = async (row: ExamRecord) => {
  currentExamRecord.value = row
  gradingDialogVisible.value = true
  detailLoading.value = true

  try {
    // 获取考试记录详情（包含题目快照）
    const recordResponse = await getExamRecordByIdApi(row.id!)
    if (recordResponse.code === 200) {
      currentExamRecord.value = recordResponse.data
    }

    // TODO: 后端需要实现此API - 根据考试记录ID获取学生答案列表
    // 目前先从题目快照中解析题目和答案信息
    if (currentExamRecord.value.questionsSnapshot) {
      try {
        const questionsData = JSON.parse(currentExamRecord.value.questionsSnapshot)
        // 将题目快照转换为StudentAnswer格式
        studentAnswers.value = questionsData.map((q: any) => ({
          questionId: q.id,
          questionType: q.questionType,
          questionText: q.questionText,
          options: q.options,
          studentAnswer: q.studentAnswer || '',
          correctAnswer: q.correctAnswer,
          score: q.score || (q.isCorrect ? q.fullScore : 0),
          fullScore: q.fullScore,
          comment: q.comment || '',
          isCorrect: q.isCorrect,
          graded: q.graded || false
        }))
      } catch (error) {
        console.error('解析题目快照失败:', error)
        ElMessage.error('解析试卷信息失败')
      }
    }

    // 如果后端实现了学生答案API，使用以下代码替代上面的解析逻辑：
    /*
    const answersResponse = await getStudentAnswersByExamRecordApi(row.id!)
    if (answersResponse.code === 200) {
      studentAnswers.value = answersResponse.data
    }
    */
  } catch (error) {
    console.error('获取试卷详情失败:', error)
    ElMessage.error('获取试卷详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 解析选项
const parseOptions = (options: string) => {
  if (!options) return []
  try {
    return JSON.parse(options)
  } catch {
    return []
  }
}

// 格式化答案显示
const formatAnswer = (answer: string | undefined, questionType: string | undefined) => {
  if (!answer) return '-'

  if (questionType === 'single_choice' || questionType === 'true_false') {
    // 单选题和判断题，答案是单个字母
    return answer
  } else if (questionType === 'multiple_choice') {
    // 多选题，答案是字母数组，如 ['A', 'B', 'C']
    try {
      const parsed = JSON.parse(answer)
      if (Array.isArray(parsed)) {
        return parsed.join(', ')
      }
      return answer
    } catch {
      return answer
    }
  }

  return answer
}

// 提交评阅
const handleSubmitGrading = async () => {
  // 验证所有主观题是否都已评分
  const ungradedQuestions = subjectiveQuestions.value.filter((q) => q.score === undefined || q.score === null)
  if (ungradedQuestions.length > 0) {
    ElMessage.warning('请先为所有主观题评分')
    return
  }

  // 验证分数是否超过满分
  const invalidScore = subjectiveQuestions.value.find((q) => (q.score || 0) > (q.fullScore || 0))
  if (invalidScore) {
    ElMessage.warning(`某题评分不能超过 ${invalidScore.fullScore} 分`)
    return
  }

  ElMessageBox.confirm('确定要提交评阅结果吗？提交后将无法修改。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        // TODO: 后端需要实现批量更新学生答案的API
        // await batchUpdateStudentAnswersApi(subjectiveQuestions.value)

        // 提交主观题评阅
        const teacherId = authStore.teacherId
        if (!teacherId) {
          ElMessage.error('获取教师信息失败')
          return
        }

        const response = await gradeSubjectiveQuestionsApi(currentExamRecord.value.id!, teacherId)
        if (response.code === 200) {
          ElMessage.success('评阅提交成功')
          gradingDialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || '提交评阅失败')
        }
      } catch (error) {
        console.error('提交评阅失败:', error)
        ElMessage.error('提交评阅失败')
      }
    })
    .catch(() => {})
}

// 关闭评阅对话框
const handleGradingDialogClose = () => {
  currentExamRecord.value = {}
  studentAnswers.value = []
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.exam-grading {
  padding: 20px;

  .tab-label {
    position: relative;
    display: inline-flex;
    align-items: center;
    gap: 8px;

    .tab-badge {
      margin-left: 4px;
    }
  }

  .search-form {
    margin-top: 20px;
    margin-bottom: 20px;
  }

  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;
  }

  .grading-container {
    .exam-info {
      padding: 15px;
      background-color: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 20px;

      h3 {
        margin: 0 0 15px 0;
        font-size: 18px;
        color: #303133;
      }
    }

    .questions-container {
      h4 {
        margin: 0 0 15px 0;
        font-size: 16px;
        color: #303133;
        border-left: 4px solid #409eff;
        padding-left: 10px;
      }

      .question-section {
        margin-bottom: 30px;

        h5 {
          margin: 0 0 15px 0;
          font-size: 14px;
          color: #606266;
          font-weight: 600;
        }

        .question-item {
          border: 1px solid #dcdfe6;
          border-radius: 4px;
          padding: 15px;
          margin-bottom: 15px;
          background-color: #fff;

          &.subjective {
            border-color: #e6a23c;
            background-color: #fdf6ec;
          }

          .question-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 10px;

            .question-index {
              font-weight: 600;
              color: #303133;
            }
          }

          .question-content {
            margin-bottom: 10px;
            line-height: 1.6;
            color: #303133;
          }

          .question-options {
            margin: 10px 0;
            padding-left: 20px;

            .option-item {
              padding: 5px 0;
              color: #606266;
            }
          }

          .answer-section {
            margin-top: 10px;
            padding-top: 10px;
            border-top: 1px dashed #dcdfe6;

            .answer-item {
              display: flex;
              align-items: center;
              margin: 8px 0;

              .label {
                min-width: 80px;
                font-weight: 500;
                color: #606266;
              }

              .value {
                flex: 1;
                color: #303133;

                &.correct {
                  color: #67c23a;
                  font-weight: 600;
                }

                &.incorrect {
                  color: #f56c6c;
                }
              }

              .correct-answer {
                color: #67c23a;
                font-weight: 600;
              }
            }
          }

          .student-answer {
            margin: 15px 0;
            padding: 12px;
            background-color: #fff;
            border-radius: 4px;

            .answer-label {
              font-weight: 500;
              color: #606266;
              margin-bottom: 8px;
            }

            .answer-text {
              line-height: 1.6;
              color: #303133;
              white-space: pre-wrap;
              word-break: break-all;
            }
          }

          .grading-section {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px dashed #e6a23c;

            .score-input {
              display: flex;
              align-items: center;
              margin-bottom: 15px;

              .label {
                min-width: 60px;
                font-weight: 500;
                color: #606266;
              }

              .score-max {
                margin-left: 10px;
                color: #909399;
              }
            }

            .comment-input {
              display: flex;

              .label {
                min-width: 60px;
                font-weight: 500;
                color: #606266;
                padding-top: 2px;
              }
            }
          }
        }
      }

      .score-summary {
        margin-top: 20px;
        padding: 15px;
        background-color: #f0f9ff;
        border-radius: 4px;
        border: 1px solid #b3d8ff;

        .highlight {
          font-size: 18px;
          font-weight: 600;
          color: #409eff;
        }
      }
    }
  }
}
</style>
