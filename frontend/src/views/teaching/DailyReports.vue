<template>
  <div class="daily-reports">
    <!-- 页面标题和操作 -->
    <el-card shadow="never" class="header-card">
      <div class="page-header">
        <div class="title-section">
          <h2>日报管理</h2>
          <p class="subtitle">学员学习日报记录与批阅</p>
        </div>
        <div class="action-section">
          <!-- 学员显示写日报按钮 -->
          <el-button
            v-if="userRole === 'user'"
            type="primary"
            :icon="DocumentAdd"
            @click="handleWriteReport"
          >
            写日报
          </el-button>
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
    </el-card>

    <!-- 搜索筛选 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <!-- 学员只能看自己的,管理员和教师可以筛选 -->
        <el-form-item v-if="userRole !== 'user'" label="学员">
          <el-select
            v-model="searchForm.studentId"
            placeholder="选择学员"
            clearable
            filterable
            style="width: 200px"
            @change="handleSearch"
          >
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="`${student.realName} (${student.studentNo})`"
              :value="student.userId"
            />
          </el-select>
        </el-form-item>

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
    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="studentNo" label="学号" width="120">
          <template #default="{ row }">
            {{ row.studentNo || '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="studentName" label="学员姓名" width="120">
          <template #default="{ row }">
            {{ row.studentName || '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="reportDate" label="日报日期" width="120">
          <template #default="{ row }">
            {{ row.reportDate }}
          </template>
        </el-table-column>

        <el-table-column prop="todayContent" label="今日学习内容" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.todayContent || '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="studyHours" label="学习时长" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.studyHours">
              {{ row.studyHours }} 小时
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="codeLines" label="代码行数" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.codeLines">{{ row.codeLines }} 行</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'draft'" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 'submitted'" type="warning">已提交</el-tag>
            <el-tag v-else-if="row.status === 'reviewed'" type="success">已批阅</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="rating" label="评分" width="100" align="center">
          <template #default="{ row }">
            <el-rate
              v-if="row.rating"
              v-model="row.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="submitTime" label="提交时间" width="160">
          <template #default="{ row }">
            {{ row.submitTime || '-' }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <!-- 学员操作 -->
            <template v-if="userRole === 'user'">
              <el-button
                v-if="row.status === 'draft'"
                type="primary"
                size="small"
                :icon="Edit"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="row.status === 'draft'"
                type="success"
                size="small"
                :icon="Promotion"
                @click="handleSubmit(row.id)"
              >
                提交
              </el-button>
              <el-button
                v-if="row.status === 'submitted'"
                type="info"
                size="small"
                :icon="View"
                @click="handleView(row)"
              >
                查看
              </el-button>
              <el-button
                v-if="row.status === 'reviewed'"
                type="success"
                size="small"
                :icon="View"
                @click="handleView(row)"
              >
                批阅结果
              </el-button>
            </template>

            <!-- 教师/管理员操作 -->
            <template v-else>
              <el-button
                type="primary"
                size="small"
                :icon="View"
                @click="handleView(row)"
              >
                查看
              </el-button>
              <el-button
                v-if="row.status === 'submitted'"
                type="warning"
                size="small"
                :icon="EditPen"
                @click="handleReview(row)"
              >
                批阅
              </el-button>
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
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
      width="800px"
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
          />
        </el-form-item>

        <el-form-item label="今日完成进度">
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

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学习时长" prop="studyHours">
              <el-input-number
                v-model="writeForm.studyHours"
                :min="0"
                :max="24"
                :precision="1"
                style="width: 100%"
              />
              <span style="margin-left: 10px">小时</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代码行数">
              <el-input-number
                v-model="writeForm.codeLines"
                :min="0"
                style="width: 100%"
              />
              <span style="margin-left: 10px">行</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="附件URL">
          <el-input
            v-model="writeForm.attachmentUrl"
            placeholder="截图、代码等附件链接(可选)"
          />
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
      title="日报详情"
      width="800px"
    >
      <div v-if="currentReport" class="report-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学员姓名">
            {{ currentReport.studentName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="学号">
            {{ currentReport.studentNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="日报日期">
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
          <el-descriptions-item label="代码行数">
            {{ currentReport.codeLines ? `${currentReport.codeLines} 行` : '-' }}
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
          <el-descriptions-item v-if="currentReport.attachmentUrl" label="附件">
            <el-link :href="currentReport.attachmentUrl" target="_blank" type="primary">
              查看附件
            </el-link>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 批阅信息(已批阅时显示) -->
        <template v-if="currentReport.status === 'reviewed'">
          <el-divider content-position="left">教师评语</el-divider>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="评分" :span="2">
              <el-rate
                v-model="currentReport.rating"
                disabled
                show-score
                text-color="#ff9900"
              />
            </el-descriptions-item>
            <el-descriptions-item label="教师评语" :span="2">
              {{ currentReport.teacherComment || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="批阅时间" :span="2">
              {{ currentReport.reviewTime || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </template>
      </div>

      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 批阅日报对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="批阅日报"
      width="700px"
    >
      <div v-if="currentReport" class="review-content">
        <!-- 日报摘要 -->
        <el-card shadow="never" class="report-summary">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学员姓名">
              {{ currentReport.studentName }}
            </el-descriptions-item>
            <el-descriptions-item label="日报日期">
              {{ currentReport.reportDate }}
            </el-descriptions-item>
            <el-descriptions-item label="学习时长" :span="2">
              {{ currentReport.studyHours ? `${currentReport.studyHours} 小时` : '-' }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider style="margin: 15px 0" />

          <div class="summary-item">
            <strong>今日学习内容：</strong>
            <p>{{ currentReport.todayContent }}</p>
          </div>
          <div class="summary-item">
            <strong>遇到的问题：</strong>
            <p>{{ currentReport.problems || '无' }}</p>
          </div>
        </el-card>

        <!-- 批阅表单 -->
        <el-form
          ref="reviewFormRef"
          :model="reviewForm"
          label-width="100px"
          style="margin-top: 20px"
        >
          <el-form-item label="评分">
            <el-rate
              v-model="reviewForm.rating"
              :texts="['需改进', '一般', '良好', '优秀', '出色']"
              show-text
              :max="5"
            />
          </el-form-item>

          <el-form-item label="教师评语">
            <el-input
              v-model="reviewForm.teacherComment"
              type="textarea"
              :rows="6"
              placeholder="请填写评语，鼓励学员、指出问题、给出建议..."
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmReview">确认批阅</el-button>
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
  Delete,
  Promotion,
  EditPen,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import {
  getDailyReportPageApi,
  createDailyReportApi,
  updateDailyReportApi,
  deleteDailyReportApi,
  submitDailyReportApi,
  reviewDailyReportApi,
  type DailyReport,
} from '@/api/dailyReport'
import { getStudentListApi } from '@/api/student'

const authStore = useAuthStore()
const userRole = computed(() => authStore.userRole)
const userId = computed(() => authStore.user?.id)

// 统计数据
const stats = reactive({
  total: 0,
  draft: 0,
  submitted: 0,
  reviewed: 0,
})

// 搜索表单
const searchForm = reactive({
  studentId: undefined as number | undefined,
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

// 学员列表(管理员和教师筛选用)
const studentList = ref<any[]>([])

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
  codeLines: undefined as number | undefined,
  attachmentUrl: '',
})

const writeRules: FormRules = {
  reportDate: [{ required: true, message: '请选择日报日期', trigger: 'change' }],
  todayContent: [{ required: true, message: '请输入今日学习内容', trigger: 'blur' }],
  studyHours: [{ required: true, message: '请输入学习时长', trigger: 'blur' }],
}

// 查看日报对话框
const viewDialogVisible = ref(false)
const currentReport = ref<DailyReport | null>(null)

// 批阅日报对话框
const reviewDialogVisible = ref(false)
const reviewFormRef = ref<FormInstance>()
const reviewForm = reactive({
  teacherComment: '',
  rating: 3,
})

// 加载学员列表
const fetchStudentList = async () => {
  try {
    const res = await getStudentListApi()
    studentList.value = res.records || []
  } catch (error) {
    console.error('加载学员列表失败:', error)
  }
}

// 加载日报列表
const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      studentId: searchForm.studentId,
      status: searchForm.status,
      startDate: searchForm.startDate,
      endDate: searchForm.endDate,
    }

    // 学员只能查看自己的日报
    if (userRole.value === 'user') {
      params.studentId = userId.value
    }

    const res = await getDailyReportPageApi(params)
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
  stats.total = tableData.value.length
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
  searchForm.studentId = undefined
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
    codeLines: row.codeLines,
    attachmentUrl: row.attachmentUrl,
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
    codeLines: undefined,
    attachmentUrl: '',
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
      await submitDailyReportApi(writeForm.id!)
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

// 批阅日报
const handleReview = (row: DailyReport) => {
  currentReport.value = row
  reviewForm.teacherComment = row.teacherComment || ''
  reviewForm.rating = row.rating || 3
  reviewDialogVisible.value = true
}

// 确认批阅
const handleConfirmReview = async () => {
  if (!reviewForm.teacherComment) {
    ElMessage.warning('请填写教师评语')
    return
  }

  try {
    await reviewDailyReportApi(currentReport.value!.id!, {
      teacherComment: reviewForm.teacherComment,
      rating: reviewForm.rating,
    })

    ElMessage.success('批阅成功')
    reviewDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('批阅失败:', error)
    ElMessage.error('批阅失败')
  }
}

// 删除日报
const handleDelete = async (row: DailyReport) => {
  try {
    await ElMessageBox.confirm('确认删除此日报吗?', '提示', {
      type: 'warning',
    })

    await deleteDailyReportApi(row.id!)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  // 管理员和教师加载学员列表
  if (userRole.value !== 'user') {
    fetchStudentList()
  }
  fetchData()
})
</script>

<style scoped lang="scss">
.daily-reports {
  padding: 20px;

  .header-card {
    margin-bottom: 20px;

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .title-section {
        h2 {
          margin: 0 0 5px 0;
          font-size: 24px;
          font-weight: 600;
        }

        .subtitle {
          margin: 0;
          color: var(--el-text-color-secondary);
          font-size: 14px;
        }
      }
    }

    .stats-row {
      .stat-card {
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
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: white;
            }

            &.draft {
              background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
              color: white;
            }

            &.submitted {
              background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
              color: white;
            }

            &.reviewed {
              background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
              color: white;
            }
          }

          .stat-info {
            .stat-value {
              font-size: 28px;
              font-weight: 600;
              line-height: 1;
              margin-bottom: 4px;
            }

            .stat-label {
              font-size: 14px;
              color: var(--el-text-color-secondary);
            }
          }
        }
      }
    }
  }

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .report-detail {
    :deep(.el-descriptions) {
      margin-bottom: 20px;
    }

    .el-divider {
      margin: 20px 0;
    }
  }

  .review-content {
    .report-summary {
      margin-bottom: 20px;

      .summary-item {
        margin: 15px 0;

        strong {
          display: block;
          margin-bottom: 8px;
          color: var(--el-text-color-primary);
        }

        p {
          margin: 0;
          color: var(--el-text-color-regular);
          line-height: 1.6;
        }
      }
    }
  }
}
</style>
