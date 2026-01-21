<template>
  <el-dialog
    v-model="dialogVisible"
    title="从题库选择题目"
    width="1200px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="question-bank-selector">
      <!-- 左侧筛选区 -->
      <div class="filter-panel">
        <div class="filter-title">
          <el-icon><Filter /></el-icon>
          <span>筛选条件</span>
        </div>

        <el-form :model="filterForm" label-position="top" class="filter-form">
          <!-- 知识点 -->
          <el-form-item label="知识点">
            <el-select
              v-model="filterForm.knowledgePoint"
              placeholder="请选择知识点"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="point in knowledgePoints"
                :key="point"
                :label="point"
                :value="point"
              />
            </el-select>
          </el-form-item>

          <!-- 题型 -->
          <el-form-item label="题型">
            <el-checkbox-group v-model="filterForm.questionTypes">
              <el-checkbox value="single_choice">单选题</el-checkbox>
              <el-checkbox value="multiple_choice">多选题</el-checkbox>
              <el-checkbox value="true_false">判断题</el-checkbox>
              <el-checkbox value="fill_blank">填空题</el-checkbox>
              <el-checkbox value="short_answer">简答题</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <!-- 难度 -->
          <el-form-item label="难度">
            <el-select
              v-model="filterForm.difficulty"
              placeholder="请选择难度"
              clearable
              style="width: 100%"
            >
              <el-option label="简单" value="easy" />
              <el-option label="中等" value="medium" />
              <el-option label="困难" value="hard" />
            </el-select>
          </el-form-item>

          <!-- 关键词 -->
          <el-form-item label="关键词">
            <el-input
              v-model="filterForm.keyword"
              placeholder="搜索题目内容"
              clearable
            />
          </el-form-item>

          <!-- 操作按钮 -->
          <el-form-item>
            <el-button type="primary" @click="handleQuery" style="width: 100%">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleReset" style="width: 100%; margin-top: 8px">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 右侧题目列表区 -->
      <div class="question-panel">
        <!-- 已选择计数 -->
        <div class="selected-count">
          <el-tag type="primary" size="large">
            已选择 {{ selectedQuestionIds.length }} 道题
          </el-tag>
        </div>

        <!-- 题目表格 -->
        <el-table
          ref="tableRef"
          v-loading="loading"
          :data="questionList"
          border
          stripe
          style="width: 100%"
          :height="tableHeight"
          @selection-change="handleSelectionChange"
          :row-key="getRowKey"
        >
          <el-table-column
            type="selection"
            width="55"
            :selectable="checkSelectable"
          />
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column
            prop="questionText"
            label="题干"
            min-width="300"
            show-overflow-tooltip
          />
          <el-table-column label="题型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getQuestionTypeTag(row.questionType)" size="small">
                {{ getQuestionTypeLabel(row.questionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="knowledgePoint"
            label="知识点"
            width="120"
            show-overflow-tooltip
          />
          <el-table-column label="难度" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="getDifficultyTag(row.difficulty)" size="small">
                {{ getDifficultyLabel(row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag v-if="isQuestionAdded(row.id)" type="info" size="small">
                已添加
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleQuery"
            @current-change="handleQuery"
          />
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">
        确定（已选择 {{ selectedQuestionIds.length }} 道题）
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { ElMessage, type TableInstance } from 'element-plus'
import { Filter, Search, Refresh } from '@element-plus/icons-vue'
import { getKnowledgePointsApi, getQuestionPageApi, type QuestionBank, type QuestionQueryParams } from '@/api/question'
import { getExamQuestionIdsApi } from '@/api/exam-question'

// Props 定义
interface Props {
  visible: boolean
  examId?: number
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  examId: undefined
})

// Events 定义
const emit = defineEmits<{
  confirm: [questionIds: number[]]
  close: []
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => {
    if (!val) emit('close')
  }
})

const loading = ref(false)
const tableRef = ref<TableInstance>()
const tableHeight = ref(500)

// 知识点列表
const knowledgePoints = ref<string[]>([])

// 试卷已有题目ID集合
const existingQuestionIds = ref<Set<number>>(new Set())

// 题目列表
const questionList = ref<QuestionBank[]>([])

// 选中的题目ID
const selectedQuestionIds = ref<number[]>([])

// 筛选表单
const filterForm = reactive({
  knowledgePoint: undefined as string | undefined,
  questionTypes: [] as string[],
  difficulty: '' as string, // 与后端API保持一致，使用单数形式
  keyword: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 获取行唯一标识
const getRowKey = (row: QuestionBank) => row.id!

// 检查题目是否已添加
const isQuestionAdded = (questionId: number) => {
  return existingQuestionIds.value.has(questionId)
}

// 检查行是否可选择
const checkSelectable = (row: QuestionBank) => {
  return !isQuestionAdded(row.id!)
}

// 题型标签类型
const getQuestionTypeTag = (type: string) => {
  const tagMap: Record<string, any> = {
    single_choice: 'primary',
    multiple_choice: 'success',
    true_false: 'warning',
    fill_blank: 'info',
    short_answer: 'danger'
  }
  return tagMap[type] || ''
}

// 题型文本
const getQuestionTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    single_choice: '单选',
    multiple_choice: '多选',
    true_false: '判断',
    fill_blank: '填空',
    short_answer: '简答'
  }
  return labelMap[type] || type
}

// 难度标签类型
const getDifficultyTag = (difficulty: string) => {
  const tagMap: Record<string, any> = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger'
  }
  return tagMap[difficulty] || ''
}

// 难度文本
const getDifficultyLabel = (difficulty: string) => {
  const labelMap: Record<string, string> = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return labelMap[difficulty] || difficulty
}

// 加载知识点列表
const loadKnowledgePoints = async () => {
  try {
    const data = await getKnowledgePointsApi()
    knowledgePoints.value = data || []
  } catch (error) {
    console.error('加载知识点失败:', error)
    ElMessage.error('加载知识点失败')
  }
}

// 加载试卷已有题目ID
const loadExistingQuestionIds = async () => {
  if (!props.examId) return

  try {
    const data = await getExamQuestionIdsApi(props.examId)
    existingQuestionIds.value = new Set(data || [])
  } catch (error) {
    console.error('加载已有题目失败:', error)
  }
}

// 加载题目列表
const loadQuestions = async () => {
  loading.value = true
  try {
    const params: QuestionQueryParams = {
      current: pagination.current,
      size: pagination.size
    }

    // 添加筛选条件
    if (filterForm.knowledgePoint) {
      params.knowledgePoint = filterForm.knowledgePoint
    }

    // 题型多选
    if (filterForm.questionTypes.length > 0) {
      params.questionType = filterForm.questionTypes.join(',')
    }

    // 难度筛选
    if (filterForm.difficulty) {
      params.difficulty = filterForm.difficulty
    }

    if (filterForm.keyword) {
      params.keyword = filterForm.keyword
    }

    const data = await getQuestionPageApi(params)
    questionList.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载题目失败:', error)
    ElMessage.error('加载题目失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选表单
const resetForm = () => {
  filterForm.knowledgePoint = undefined
  filterForm.questionTypes = []
  filterForm.difficulty = ''
  filterForm.keyword = ''
}

// 重置所有状态
const resetState = () => {
  selectedQuestionIds.value = []
  questionList.value = []
  existingQuestionIds.value = new Set()
  resetForm()
  pagination.current = 1
}

// 查询按钮
const handleQuery = () => {
  pagination.current = 1
  loadQuestions()
}

// 重置按钮
const handleReset = () => {
  resetForm()
  pagination.current = 1
  loadQuestions()
}

// 表格选择变化
const handleSelectionChange = (selection: QuestionBank[]) => {
  selectedQuestionIds.value = selection.map(item => item.id!)
}

// 关闭对话框
const handleClose = () => {
  emit('close')
}

// 确定按钮
const handleConfirm = () => {
  if (selectedQuestionIds.value.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }

  emit('confirm', selectedQuestionIds.value)
  emit('close')
}

// 初始化
const init = async () => {
  await loadKnowledgePoints()
  await loadExistingQuestionIds()
  await loadQuestions()

  // 计算表格高度
  // 对话框内容区高度减去预留空间：已选择计数(60px) + 分页器(60px) + 其他间距(60px) = 180px
  // 最小高度300px确保在小屏幕上也能正常显示
  await nextTick()
  const dialogContent = document.querySelector('.el-dialog__body')
  if (dialogContent) {
    const height = dialogContent.clientHeight - 180
    tableHeight.value = Math.max(height, 300)
  }
}

// 监听对话框显示
watch(() => props.visible, (newVal) => {
  if (newVal) {
    init()
  } else {
    // 对话框关闭时重置状态
    resetState()
  }
})
</script>

<style scoped lang="scss">
.question-bank-selector {
  display: flex;
  gap: 20px;
  height: 600px;

  // 左侧筛选区
  .filter-panel {
    width: 280px;
    flex-shrink: 0;
    background: var(--muted);
    border-radius: 8px;
    padding: 16px;
    overflow-y: auto;

    .filter-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: var(--foreground);
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 2px solid var(--border);
    }

    .filter-form {
      :deep(.el-form-item) {
        margin-bottom: 16px;
      }

      :deep(.el-form-item__label) {
        font-weight: 500;
        color: var(--foreground);
      }

      :deep(.el-checkbox-group) {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }
    }
  }

  // 右侧题目列表区
  .question-panel {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;
    overflow: hidden;

    .selected-count {
      flex-shrink: 0;
    }

    .pagination-container {
      flex-shrink: 0;
      display: flex;
      justify-content: flex-end;
      padding: 8px 0;
      border-top: 1px solid var(--border);
    }
  }
}

// 对话框样式优化
:deep(.el-dialog__body) {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

// 响应式设计
@media (max-width: 1200px) {
  .question-bank-selector {
    flex-direction: column;
    height: auto;

    .filter-panel {
      width: 100%;
    }

    .question-panel {
      .pagination-container {
        justify-content: center;
      }
    }
  }
}
</style>
