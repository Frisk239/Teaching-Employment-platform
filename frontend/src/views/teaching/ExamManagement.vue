<template>
  <div class="exam-management">
    <el-card shadow="never" class="search-card">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="试卷名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="试卷类型">
          <el-select
            v-model="searchForm.examType"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
          >
            <el-option label="课程考试" value="course" />
            <el-option label="企业笔试" value="company" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="草稿" value="draft" />
            <el-option label="已发布" value="published" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格卡片 -->
    <el-card shadow="never" class="table-card">
      <!-- 操作按钮 -->
      <template #header>
        <div class="card-header">
          <span class="title">试卷列表</span>
          <div class="operations">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增试卷
            </el-button>
          </div>
        </div>
      </template>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="examName" label="试卷名称" min-width="180" />
        <el-table-column label="试卷类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.examType === 'course' ? 'primary' : 'success'" size="small">
              {{ getExamTypeLabel(row.examType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refName" label="关联名称" width="180" />
        <el-table-column prop="duration" label="考试时长" width="100" align="center">
          <template #default="{ row }">
            {{ row.duration || 0 }} 分钟
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="及格/总分" width="120" align="center">
          <template #default="{ row }">
            {{ row.passScore || 0 }} / {{ row.totalScore || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'draft'"
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-if="row.status === 'draft'"
              type="success"
              size="small"
              link
              @click="handlePublish(row)"
            >
              <el-icon><Promotion /></el-icon>
              发布
            </el-button>
            <el-button
              v-if="row.status === 'published'"
              type="warning"
              size="small"
              link
              @click="handleEnd(row)"
            >
              <el-icon><CircleClose /></el-icon>
              结束
            </el-button>
            <el-button
              v-if="row.status === 'draft'"
              type="danger"
              size="small"
              link
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
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
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-tabs v-model="activeTab" class="exam-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            label-width="120px"
            class="exam-form"
          >
            <el-divider content-position="left">基本信息</el-divider>
            <el-form-item label="试卷名称" prop="examName">
              <el-input v-model="formData.examName" placeholder="请输入试卷名称" />
            </el-form-item>

            <el-form-item label="试卷类型" prop="examType">
              <el-radio-group v-model="formData.examType">
                <el-radio value="course">课程考试</el-radio>
                <el-radio value="company">企业笔试</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="关联" prop="refId">
              <el-select
                v-model="formData.refId"
                :placeholder="formData.examType === 'course' ? '请选择课程' : '请选择职位'"
                style="width: 100%"
                filterable
              >
                <el-option
                  v-for="item in refOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>

            <el-divider content-position="left">考试设置</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试时长" prop="duration">
              <el-input-number
                v-model="formData.duration"
                :min="1"
                :max="600"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #909399">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number
                v-model="formData.totalScore"
                :min="1"
                :max="1000"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #909399">分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="及格分数" prop="passScore">
              <el-input-number
                v-model="formData.passScore"
                :min="0"
                :max="formData.totalScore || 100"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #909399">分</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="题目乱序">
              <el-switch v-model="formData.shuffleQuestions" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选项乱序">
              <el-switch v-model="formData.shuffleOptions" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">时间设置</el-divider>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      </el-tab-pane>

      <!-- 题目管理 -->
      <el-tab-pane label="题目管理" name="questions">
        <div v-if="!formData.id" class="empty-state">
          <el-empty description="请先保存试卷基本信息" />
        </div>
        <div v-else class="questions-management">
          <!-- 操作按钮 -->
          <div class="question-actions">
            <el-button type="primary" @click="handleShowQuestionBank">
              <el-icon><Plus /></el-icon>
              从题库添加
            </el-button>
            <el-button type="danger" :disabled="selectedQuestions.length === 0" @click="handleBatchDeleteQuestions">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
          </div>

          <!-- 题目列表 -->
          <el-table
            :data="examQuestions"
            border
            stripe
            style="width: 100%; margin-top: 16px"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="questionText" label="题目内容" min-width="300" show-overflow-tooltip />
            <el-table-column prop="questionType" label="题型" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getQuestionTypeTag(row.questionType)" size="small">
                  {{ getQuestionTypeLabel(row.questionType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="questionScore" label="分值" width="100" align="center">
              <template #default="{ row }">
                <el-input-number
                  v-model="row.questionScore"
                  :min="0"
                  :max="100"
                  size="small"
                  @change="handleUpdateQuestionScore(row)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="100" align="center">
              <template #default="{ row }">
                <el-input-number
                  v-model="row.sortOrder"
                  :min="1"
                  size="small"
                  @change="handleUpdateQuestionSort(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="handleDeleteQuestion(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 题库选择器 -->
    <QuestionBankSelector
      :visible="selectorVisible"
      :exam-id="currentExamId"
      @confirm="handleQuestionSelectorConfirm"
      @close="selectorVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Promotion, CircleClose } from '@element-plus/icons-vue'
import {
  getExamPageApi,
  getExamByIdApi,
  createExamApi,
  updateExamApi,
  deleteExamApi,
  publishExamApi,
  endExamApi
} from '@/api/exam'
import type { Exam } from '@/api/exam'
import { getCoursesByTeacherApi, type Course } from '@/api/course'
import { getPositionListApi, type Position } from '@/api/position'
import { useAuthStore } from '@/stores'
import QuestionBankSelector from '@/components/QuestionBankSelector.vue'
import { batchAddQuestionsApi } from '@/api/exam-question'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  examType: undefined as string | undefined,
  status: undefined as string | undefined
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<Exam[]>([])

// 课程和职位列表
const courseList = ref<Course[]>([])
const positionList = ref<Position[]>([])

// 关联选项
const refOptions = computed(() => {
  if (formData.examType === 'course') {
    return courseList.value.map(course => ({
      label: course.courseName || '未命名课程',
      value: course.id
    }))
  } else {
    return positionList.value.map(position => ({
      label: position.positionName || '未命名职位',
      value: position.id
    }))
  }
})

// 当前激活的标签页
const activeTab = ref('basic')

// 题目管理相关数据
const examQuestions = ref<any[]>([])
const selectedQuestions = ref<any[]>([])

// 题库选择器相关
const selectorVisible = ref(false)
const currentExamId = ref<number>()

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑试卷' : '新增试卷'))
const formRef = ref<FormInstance>()
const formData = reactive<Partial<Exam>>({
  id: undefined,
  examName: '',
  examType: 'course',
  refId: undefined,
  duration: 60,
  startTime: '',
  endTime: '',
  passScore: 60,
  totalScore: 100,
  shuffleQuestions: 0,
  shuffleOptions: 0,
  status: 'draft'
})

// 表单验证规则
const formRules: FormRules = {
  examName: [
    { required: true, message: '请输入试卷名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  examType: [{ required: true, message: '请选择试卷类型', trigger: 'change' }],
  refId: [{ required: true, message: '请输入关联ID', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }],
  passScore: [{ required: true, message: '请输入及格分数', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

// 获取试卷类型标签
const getExamTypeLabel = (examType: string | undefined) => {
  const labelMap: Record<string, string> = {
    course: '课程考试',
    company: '企业笔试'
  }
  return labelMap[examType || ''] || '未知'
}

// 获取状态类型
const getStatusType = (status: string | undefined) => {
  const typeMap: Record<string, any> = {
    draft: 'info',
    published: 'success',
    ended: 'danger'
  }
  return typeMap[status || ''] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string | undefined) => {
  const labelMap: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
    ended: '已结束'
  }
  return labelMap[status || ''] || '未知'
}

// 获取试卷列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      examType: searchForm.examType,
      status: searchForm.status,
      keyword: searchForm.keyword || undefined
    }
    const data = await getExamPageApi(params)
    tableData.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('获取试卷列表失败:', error)
    ElMessage.error('获取试卷列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.examType = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchData()
}

// 加载课程和职位列表
const loadRefData = async () => {
  const authStore = useAuthStore()

  try {
    // 加载当前教师的课程列表
    if (authStore.teacherId) {
      const courses = await getCoursesByTeacherApi(authStore.teacherId)
      courseList.value = courses || []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }

  try {
    // 加载职位列表
    const positions = await getPositionListApi()
    positionList.value = positions || []
  } catch (error) {
    console.error('加载职位列表失败:', error)
  }
}

// 新增
const handleAdd = async () => {
  // 先加载课程和职位列表
  await loadRefData()

  // 清空题目列表
  examQuestions.value = []
  selectedQuestions.value = []
  activeTab.value = 'basic'

  Object.assign(formData, {
    id: undefined,
    examName: '',
    examType: 'course',
    refId: undefined,
    duration: 60,
    startTime: '',
    endTime: '',
    passScore: 60,
    totalScore: 100,
    shuffleQuestions: 0,
    shuffleOptions: 0,
    status: 'draft'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: Exam) => {
  try {
    // 先加载课程和职位列表
    await loadRefData()

    const data = await getExamByIdApi(row.id!)
    Object.assign(formData, data)

    // 重置状态
    selectedQuestions.value = []
    activeTab.value = 'basic'

    dialogVisible.value = true

    // 加载题目列表
    await loadExamQuestions(row.id!)
  } catch (error) {
    console.error('获取试卷信息失败:', error)
    ElMessage.error('获取试卷信息失败')
  }
}

// 删除
const handleDelete = (row: Exam) => {
  ElMessageBox.confirm('确定要删除该试卷吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteExamApi(row.id!)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 发布
const handlePublish = (row: Exam) => {
  ElMessageBox.confirm('确定要发布该试卷吗？发布后学生可以看到并参加考试。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await publishExamApi(row.id!)
        ElMessage.success('发布成功')
        fetchData()
      } catch (error) {
        console.error('发布失败:', error)
        ElMessage.error('发布失败')
      }
    })
    .catch(() => {})
}

// 结束考试
const handleEnd = (row: Exam) => {
  ElMessageBox.confirm('确定要结束该考试吗？结束后学生将无法继续答题。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await endExamApi(row.id!)
        ElMessage.success('考试已结束')
        fetchData()
      } catch (error) {
        console.error('结束考试失败:', error)
        ElMessage.error('结束考试失败')
      }
    })
    .catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 验证结束时间必须晚于开始时间
        if (formData.startTime && formData.endTime) {
          const start = new Date(formData.startTime).getTime()
          const end = new Date(formData.endTime).getTime()
          if (end <= start) {
            ElMessage.error('结束时间必须晚于开始时间')
            return
          }
        }

        // 验证及格分数不能大于总分
        if (formData.passScore && formData.totalScore && formData.passScore > formData.totalScore) {
          ElMessage.error('及格分数不能大于总分')
          return
        }

        const api = formData.id ? updateExamApi : createExamApi
        await api(formData as Exam)
        ElMessage.success(formData.id ? '更新成功' : '新增成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 加载试卷题目
const loadExamQuestions = async (examId: number) => {
  try {
    const token = sessionStorage.getItem('token')
    const response = await fetch(`http://localhost:8080/api/exam-question/exam/${examId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const result = await response.json()
    if (result.code === 200) {
      examQuestions.value = result.data || []
    }
  } catch (error) {
    console.error('加载题目列表失败:', error)
    ElMessage.error('加载题目列表失败')
  }
}

// 切换到题目管理标签页时加载题目
watch(activeTab, async (newTab) => {
  if (newTab === 'questions' && formData.id && examQuestions.value.length === 0) {
    await loadExamQuestions(formData.id)
  }
})

// 表格选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedQuestions.value = selection
}

// 显示题库选择对话框
const handleShowQuestionBank = () => {
  if (!formData.id) {
    ElMessage.warning('请先保存试卷基本信息')
    return
  }
  currentExamId.value = formData.id
  selectorVisible.value = true
}

// 确认从题库选择器添加题目
const handleQuestionSelectorConfirm = async (questionIds: number[]) => {
  if (!formData.id) {
    ElMessage.warning('试卷信息异常')
    return
  }

  try {
    // 构建试卷题目列表
    const questionsToAdd = questionIds.map((questionId, index) => ({
      examId: formData.id,
      questionId: questionId,
      questionScore: 5, // 默认5分
      sortOrder: examQuestions.value.length + index + 1,
      generateType: 'manual'
    }))

    // 调用批量添加API
    await batchAddQuestionsApi(questionsToAdd)

    ElMessage.success(`成功添加 ${questionIds.length} 道题目`)

    // 刷新题目列表
    await loadExamQuestions(formData.id)
  } catch (error) {
    console.error('添加题目失败:', error)
    ElMessage.error('添加题目失败')
  }
}

// 更新题目分值
const handleUpdateQuestionScore = async (row: any) => {
  try {
    const token = sessionStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/exam-question', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(row)
    })
    const result = await response.json()
    if (result.code !== 200) {
      throw new Error(result.message || '更新失败')
    }
    ElMessage.success('更新成功')
  } catch (error: any) {
    console.error('更新失败:', error)
    ElMessage.error(error.message || '更新失败')
    await loadExamQuestions(formData.id!) // 重新加载以恢复原值
  }
}

// 更新题目排序
const handleUpdateQuestionSort = async (row: any) => {
  try {
    const token = sessionStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/exam-question', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(row)
    })
    const result = await response.json()
    if (result.code !== 200) {
      throw new Error(result.message || '更新失败')
    }
    ElMessage.success('更新成功')
  } catch (error: any) {
    console.error('更新失败:', error)
    ElMessage.error(error.message || '更新失败')
    await loadExamQuestions(formData.id!)
  }
}

// 删除题目
const handleDeleteQuestion = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这道题目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const token = sessionStorage.getItem('token')
    const response = await fetch(`http://localhost:8080/api/exam-question/${row.id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadExamQuestions(formData.id!)
    } else {
      throw new Error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除题目
const handleBatchDeleteQuestions = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedQuestions.value.length} 道题目吗？`, '批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const token = sessionStorage.getItem('token')
    const deletePromises = selectedQuestions.value.map(q =>
      fetch(`http://localhost:8080/api/exam-question/${q.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
    )

    await Promise.all(deletePromises)
    ElMessage.success('批量删除成功')
    selectedQuestions.value = []
    await loadExamQuestions(formData.id!)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 题目类型标签映射
const getQuestionTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    true_false: '判断题',
    short_answer: '简答题',
    fill_blank: '填空题'
  }
  return typeMap[type] || '未知'
}

const getQuestionTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    single_choice: 'primary',
    multiple_choice: 'success',
    true_false: 'warning',
    short_answer: 'danger',
    fill_blank: 'info'
  }
  return tagMap[type] || ''
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.exam-management {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;

    .search-form {
      margin-bottom: 0;
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .operations {
        display: flex;
        gap: 10px;
      }
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .exam-form {
    :deep(.el-divider) {
      margin: 20px 0;
    }
  }
}
</style>
