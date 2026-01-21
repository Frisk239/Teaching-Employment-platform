<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">企业笔试管理</h1>
      <div class="page-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          创建笔试
        </el-button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索试卷名称"
          clearable
          @change="handleSearch"
          style="width: 200px;"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filterStatus"
          placeholder="试卷状态"
          clearable
          @change="handleFilterChange"
          style="width: 150px;"
        >
          <el-option label="全部状态" value=""></el-option>
          <el-option label="草稿" value="draft"></el-option>
          <el-option label="已发布" value="published"></el-option>
          <el-option label="已结束" value="ended"></el-option>
        </el-select>

        <el-button @click="handleResetFilters">
          <el-icon><RefreshLeft /></el-icon>
          重置筛选
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="examName" label="试卷名称" min-width="180" />

        <el-table-column label="关联职位" width="180">
          <template #default="{ row }">
            <span v-if="row.refName">{{ row.refName }}</span>
            <span v-else class="text-muted">未关联</span>
          </template>
        </el-table-column>

        <el-table-column prop="duration" label="考试时长" width="100" align="center">
          <template #default="{ row }">
            {{ row.duration || 0 }} 分钟
          </template>
        </el-table-column>

        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            <span v-if="row.startTime">{{ formatDateTime(row.startTime) }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            <span v-if="row.endTime">{{ formatDateTime(row.endTime) }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>

        <el-table-column label="及格/总分" width="120" align="center">
          <template #default="{ row }">
            {{ row.passScore || 0 }} / {{ row.totalScore || 0 }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 'draft'"
                type="primary"
                link
                size="small"
                @click="handleEdit(row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                v-if="row.status === 'draft'"
                type="success"
                link
                size="small"
                @click="handlePublish(row)"
              >
                <el-icon><Promotion /></el-icon>
                发布
              </el-button>
              <el-button
                v-if="row.status === 'published'"
                type="warning"
                link
                size="small"
                @click="handleEnd(row)"
              >
                <el-icon><CircleClose /></el-icon>
                结束
              </el-button>
              <el-button
                v-if="row.status === 'draft'"
                type="danger"
                link
                size="small"
                @click="handleDelete(row)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
              <el-button
                type="info"
                link
                size="small"
                @click="handleView(row)"
              >
                <el-icon><View /></el-icon>
                查看
              </el-button>
            </div>
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
    </div>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="试卷名称" prop="examName">
          <el-input
            v-model="formData.examName"
            placeholder="请输入试卷名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="关联职位" prop="refId">
          <el-select
            v-model="formData.refId"
            placeholder="请选择关联职位"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="position in positions"
              :key="position.id"
              :label="position.positionName"
              :value="position.id"
            >
              <span>{{ position.positionName }}</span>
              <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">
                {{ position.city }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-divider content-position="left">考试设置</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试时长" prop="duration">
              <el-input-number
                v-model="formData.duration"
                :min="15"
                :max="600"
                :step="5"
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
              <span style="margin-left: 8px; color: #909399; font-size: 12px">随机排列题目顺序</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选项乱序">
              <el-switch v-model="formData.shuffleOptions" :active-value="1" :inactive-value="0" />
              <span style="margin-left: 8px; color: #909399; font-size: 12px">随机排列选项顺序</span>
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
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-alert
          title="提示"
          type="info"
          :closable="false"
          style="margin-top: 1rem;"
        >
          <ul style="margin: 0.5rem 0; padding-left: 1.25rem;">
            <li>创建试卷后，可以在"管理题目"中添加试题</li>
            <li>试卷发布后，关联职位的求职者可以参加笔试</li>
            <li>只能从公共题库或企业私有题库中选择题目</li>
          </ul>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="试卷详情"
      width="700px"
    >
      <div v-if="viewData" class="view-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="试卷名称" :span="2">
            {{ viewData.examName }}
          </el-descriptions-item>
          <el-descriptions-item label="试卷类型" :span="2">
            <el-tag type="success">企业笔试</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="关联职位" :span="2">
            {{ viewData.refName || '未关联' }}
          </el-descriptions-item>
          <el-descriptions-item label="考试时长">
            {{ viewData.duration }} 分钟
          </el-descriptions-item>
          <el-descriptions-item label="及格/总分">
            {{ viewData.passScore }} / {{ viewData.totalScore }}
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            {{ viewData.startTime ? formatDateTime(viewData.startTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="结束时间">
            {{ viewData.endTime ? formatDateTime(viewData.endTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="题目乱序">
            <el-tag :type="viewData.shuffleQuestions ? 'success' : 'info'" size="small">
              {{ viewData.shuffleQuestions ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="选项乱序">
            <el-tag :type="viewData.shuffleOptions ? 'success' : 'info'" size="small">
              {{ viewData.shuffleOptions ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态" :span="2">
            <el-tag :type="getStatusType(viewData.status)" size="large">
              {{ getStatusLabel(viewData.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ viewData.createTime ? formatDateTime(viewData.createTime) : '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button type="primary" @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Search,
  RefreshLeft,
  Plus,
  Edit,
  Delete,
  Promotion,
  CircleClose,
  View
} from '@element-plus/icons-vue'
import {
  getExamPageApi,
  getExamByIdApi,
  createExamApi,
  updateExamApi,
  deleteExamApi,
  publishExamApi,
  endExamApi
} from '@/api/exam'
import { getPositionPageApi, type Position } from '@/api/position'
import { useAuthStore } from '@/stores/auth'
import type { Exam } from '@/api/exam'

const authStore = useAuthStore()

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref('')

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const submitting = ref(false)
const tableData = ref<Exam[]>([])

// 职位列表
const positions = ref<Position[]>([])

// 对话框
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑试卷' : '创建试卷'))
const formRef = ref<FormInstance>()
const viewData = ref<Exam | null>(null)

// 表单数据
const formData = reactive<Partial<Exam>>({
  id: undefined,
  examName: '',
  examType: 'company',
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
  refId: [{ required: true, message: '请选择关联职位', trigger: 'change' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }],
  passScore: [{ required: true, message: '请输入及格分数', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
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

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 获取试卷列表
const fetchData = async () => {
  loading.value = true
  try {
    // 从 authStore 获取当前企业的 companyId
    const companyId = authStore.companyId
    if (!companyId) {
      ElMessage.error('无法获取企业信息')
      return
    }

    const params = {
      current: pagination.current,
      size: pagination.size,
      examType: 'company',
      refId: companyId, // 使用 companyId 作为 refId
      status: filterStatus.value || undefined,
      keyword: searchKeyword.value || undefined
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

// 加载企业职位列表
const loadPositions = async () => {
  try {
    const companyId = authStore.companyId
    if (!companyId) {
      ElMessage.error('无法获取企业信息')
      return
    }

    const data = await getPositionPageApi({
      current: 1,
      size: 1000,
      companyId: companyId
    })
    positions.value = data.records || []
  } catch (error) {
    console.error('加载职位列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 筛选改变
const handleFilterChange = () => {
  pagination.current = 1
  fetchData()
}

// 重置筛选
const handleResetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    examName: '',
    examType: 'company',
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
    const data = await getExamByIdApi(row.id!)
    Object.assign(formData, data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取试卷信息失败:', error)
    ElMessage.error('获取试卷信息失败')
  }
}

// 查看
const handleView = async (row: Exam) => {
  try {
    const data = await getExamByIdApi(row.id!)
    viewData.value = data
    viewDialogVisible.value = true
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
  ElMessageBox.confirm(
    '确定要发布该试卷吗？发布后关联职位的求职者可以看到并参加考试。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
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
  ElMessageBox.confirm(
    '确定要结束该考试吗？结束后求职者将无法继续答题。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
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
        submitting.value = true

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

        // 固定 examType 为 company
        formData.examType = 'company'

        const api = formData.id ? updateExamApi : createExamApi
        await api(formData as Exam)
        ElMessage.success(formData.id ? '更新成功' : '创建成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
  loadPositions()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 1.5rem;
  background: oklch(0.98 0.01 260);
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: oklch(0.20 0.02 260);
  margin: 0;
}

.page-actions {
  display: flex;
  gap: 0.75rem;
}

.filter-section {
  background: white;
  padding: 1.25rem;
  border-radius: 0.75rem;
  box-shadow: 0 1px 3px oklch(0.80 0.02 260 / 0.12);
  margin-bottom: 1.5rem;
}

.filter-row {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.table-container {
  background: white;
  border-radius: 0.75rem;
  box-shadow: 0 1px 3px oklch(0.80 0.02 260 / 0.12);
  overflow: hidden;
  padding: 1.5rem;
}

.pagination-container {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.text-muted {
  color: oklch(0.60 0.02 260);
}

.view-content {
  :deep(.el-descriptions__label) {
    font-weight: 500;
  }
}

:deep(.el-button--primary) {
  background: oklch(0.55 0.20 260);
  border-color: oklch(0.55 0.20 260);
}

:deep(.el-button--primary:hover) {
  background: oklch(0.50 0.20 260);
  border-color: oklch(0.50 0.20 260);
}

:deep(.el-table) {
  font-size: 0.875rem;
}

:deep(.el-table th) {
  background: oklch(0.95 0.02 260);
  color: oklch(0.30 0.02 260);
  font-weight: 600;
}

:deep(.el-table tr:hover > td) {
  background: oklch(0.97 0.02 260) !important;
}

:deep(.el-dialog__header) {
  background: oklch(0.98 0.01 260);
  border-bottom: 1px solid oklch(0.92 0.01 260);
}

:deep(.el-dialog__title) {
  font-size: 1.125rem;
  font-weight: 600;
  color: oklch(0.20 0.02 260);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: oklch(0.35 0.02 260);
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: oklch(0.30 0.02 260);
}
</style>
