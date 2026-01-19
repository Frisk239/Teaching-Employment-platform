<template>
  <div class="written-test-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">笔试管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          安排笔试
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学生姓名"
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
          placeholder="考试状态"
          clearable
          @change="handleFilterChange"
          style="width: 150px;"
        >
          <el-option label="全部状态" value=""></el-option>
          <el-option label="待考试" value="pending"></el-option>
          <el-option label="考试中" value="ongoing"></el-option>
          <el-option label="已完成" value="completed"></el-option>
          <el-option label="缺席" value="missed"></el-option>
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
        :data="filteredTests"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />

        <el-table-column prop="studentName" label="学生姓名" width="120" />

        <el-table-column prop="positionName" label="应聘职位" width="150" />

        <el-table-column label="考试时间" width="180">
          <template #default="{ row }">
            <span v-if="row.startTime">{{ formatDateTime(row.startTime) }}</span>
            <span v-else class="text-muted">未安排</span>
          </template>
        </el-table-column>

        <el-table-column prop="duration" label="时长(分钟)" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.duration">{{ row.duration }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="得分" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.score !== null && row.score !== undefined">
              {{ row.score }}/{{ row.totalScore }}
            </span>
            <span v-else class="text-muted">未出分</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="300" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons-container">
              <!-- 查看按钮 -->
              <button
                class="action-btn action-btn-view"
                @click="handleView(row)"
                title="查看详情"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
                <span>查看</span>
              </button>

              <!-- 编辑按钮 -->
              <button
                class="action-btn action-btn-edit"
                @click="handleEdit(row)"
                title="编辑"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                <span>编辑</span>
              </button>

              <!-- 更多按钮 -->
              <el-dropdown
                v-if="row.status === 'pending' || row.status === 'ongoing'"
                trigger="click"
                @command="(cmd) => handleMoreAction(cmd, row)"
              >
                <button class="action-btn action-btn-more" title="更多操作">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="1"></circle>
                    <circle cx="19" cy="12" r="1"></circle>
                    <circle cx="5" cy="12" r="1"></circle>
                  </svg>
                  <span>更多</span>
                </button>
                <template #dropdown>
                  <el-dropdown-menu class="action-dropdown-menu">
                    <el-dropdown-item command="submitScore" v-if="row.status !== 'completed'" class="dropdown-item-submit">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <path d="M9 15l2 2 4-4"></path>
                      </svg>
                      <span>提交成绩</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="markMissed" v-if="row.status === 'pending'" class="dropdown-item-missed">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                      </svg>
                      <span>标记缺席</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided class="dropdown-item-delete">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      </svg>
                      <span>删除</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>

              <!-- 删除按钮(已完成状态) -->
              <button
                v-else
                class="action-btn action-btn-delete"
                @click="handleDelete(row)"
                title="删除"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
                <span>删除</span>
              </button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handlePageSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 笔试表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="学生" prop="studentId" v-if="dialogMode === 'add'">
          <el-select
            v-model="formData.studentId"
            placeholder="选择学生"
            filterable
            style="width: 100%"
            @change="handleStudentChange"
          >
            <el-option
              v-for="student in screenedApplications"
              :key="student.studentId"
              :label="student.studentName"
              :value="student.studentId"
            >
              <span>{{ student.studentName }}</span>
              <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">
                {{ student.positionName }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="应聘职位" v-if="dialogMode === 'add'">
          <el-input v-model="selectedPositionName" disabled />
        </el-form-item>

        <el-form-item label="考试链接" prop="testUrl" v-if="dialogMode === 'add' || dialogMode === 'edit' || dialogMode === 'view'">
          <el-input
            v-model="formData.testUrl"
            placeholder="请输入考试链接"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>

        <el-form-item label="考试时长(分钟)" prop="duration" v-if="dialogMode === 'add' || dialogMode === 'edit' || dialogMode === 'view'">
          <el-input-number
            v-model="formData.duration"
            :min="15"
            :max="300"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime" v-if="dialogMode === 'add' || dialogMode === 'edit' || dialogMode === 'view'">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime" v-if="dialogMode === 'add' || dialogMode === 'edit' || dialogMode === 'view'">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="得分" v-if="dialogMode === 'view' || dialogMode === 'submitScore'">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-input-number
                v-model="formData.score"
                :min="0"
                :max="formData.totalScore || 100"
                :disabled="dialogMode === 'view'"
                style="width: 100%"
              />
            </el-col>
            <el-col :span="12">
              <el-input-number
                v-model="formData.totalScore"
                :min="0"
                :disabled="dialogMode === 'view'"
                style="width: 100%"
                placeholder="总分"
              />
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="评语" v-if="dialogMode === 'view' || dialogMode === 'submitScore'">
          <el-input
            v-model="formData.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入评语"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>

        <el-form-item label="状态" v-if="dialogMode === 'view'">
          <el-tag :type="getStatusType(formData.status)" size="large">
            {{ getStatusLabel(formData.status) }}
          </el-tag>
        </el-form-item>
      </el-form>

      <template #footer v-if="dialogMode !== 'view'">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, RefreshLeft, ArrowDown } from '@element-plus/icons-vue'
import { writtenTestApi, type WrittenTest } from '@/api/writtenTest'
import { applicationApi } from '@/api/application'

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const tests = ref<WrittenTest[]>([])
const selectedRows = ref<WrittenTest[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view' | 'submitScore'>('add')
const formRef = ref()

// 表单数据
const formData = ref<WrittenTest>({
  applicationId: 0,
  positionId: 0,
  studentId: 0,
  testUrl: '',
  duration: 60,
  startTime: '',
  endTime: '',
  score: undefined,
  totalScore: 100,
  status: 'pending',
  comment: ''
})

// 已筛选的申请(用于安排笔试)
const screenedApplications = ref<any[]>([])
const selectedPositionName = ref('')

// 表单验证规则（动态生成，根据当前对话框模式）
const formRules = computed(() => {
  const rules: any = {}

  if (dialogMode.value === 'add') {
    rules.studentId = [{ required: true, message: '请选择学生', trigger: 'change' }]
    rules.testUrl = [{ required: true, message: '请输入考试链接', trigger: 'blur' }]
    rules.duration = [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
    rules.startTime = [{ required: true, message: '请选择开始时间', trigger: 'change' }]
    rules.endTime = [{ required: true, message: '请选择结束时间', trigger: 'change' }]
  }

  if (dialogMode.value === 'submitScore') {
    rules.score = [{ required: true, message: '请输入得分', trigger: 'blur' }]
    rules.totalScore = [{ required: true, message: '请输入总分', trigger: 'blur' }]
  }

  return rules
})

// 计算属性
const dialogTitle = computed(() => {
  const titles = {
    add: '安排笔试',
    edit: '编辑笔试',
    view: '查看笔试详情',
    submitScore: '提交笔试成绩'
  }
  return titles[dialogMode.value]
})

const filteredTests = computed(() => {
  let result = tests.value

  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter((test) =>
      test.studentName?.toLowerCase().includes(keyword) ||
      test.positionName?.toLowerCase().includes(keyword)
    )
  }

  // 状态过滤
  if (filterStatus.value) {
    result = result.filter((test) => test.status === filterStatus.value)
  }

  return result
})

// 方法
const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待考试',
    'ongoing': '考试中',
    'completed': '已完成',
    'missed': '缺席'
  }
  return map[status] || status
}

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    'pending': 'info',
    'ongoing': 'warning',
    'completed': 'success',
    'missed': 'danger'
  }
  return map[status] || ''
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilterChange = () => {
  currentPage.value = 1
}

const handleResetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  currentPage.value = 1
}

const handleRefresh = () => {
  loadTests()
  loadScreenedApplications()
}

const handleSelectionChange = (selection: WrittenTest[]) => {
  selectedRows.value = selection
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadTests()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadTests()
}

const resetForm = () => {
  formData.value = {
    applicationId: 0,
    positionId: 0,
    studentId: 0,
    testUrl: '',
    duration: 60,
    startTime: '',
    endTime: '',
    score: undefined,
    totalScore: 100,
    status: 'pending',
    comment: ''
  }
  selectedPositionName.value = ''
  formRef.value?.clearValidate()
}

const handleAdd = () => {
  resetForm()
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const handleView = (row: any) => {
  Object.assign(formData.value, row)
  dialogMode.value = 'view'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  Object.assign(formData.value, row)
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

const handleStudentChange = (studentId: number) => {
  const selected = screenedApplications.value.find(
    (app) => app.studentId === studentId
  )
  if (selected) {
    formData.value.applicationId = selected.id
    formData.value.positionId = selected.positionId
    selectedPositionName.value = selected.positionName
  }
}

const handleMoreAction = async (command: string, row: any) => {
  switch (command) {
    case 'submitScore':
      Object.assign(formData.value, row)
      dialogMode.value = 'submitScore'
      dialogVisible.value = true
      break
    case 'markMissed':
      await handleMarkMissed(row)
      break
    case 'delete':
      await handleDelete(row)
      break
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    if (dialogMode.value === 'add') {
      // 使用安排笔试API
      await writtenTestApi.schedule({
        applicationId: formData.value.applicationId,
        testUrl: formData.value.testUrl!,
        duration: formData.value.duration!,
        startTime: formData.value.startTime!,
        endTime: formData.value.endTime!
      })
      ElMessage.success('安排笔试成功')
    } else if (dialogMode.value === 'edit') {
      await writtenTestApi.update(formData.value)
      ElMessage.success('更新成功')
    } else if (dialogMode.value === 'submitScore') {
      await writtenTestApi.submitScore({
        id: formData.value.id!,
        score: formData.value.score!,
        totalScore: formData.value.totalScore!,
        comment: formData.value.comment
      })
      ElMessage.success('提交成绩成功')
    }

    dialogVisible.value = false
    loadTests()
  } catch (error: any) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleMarkMissed = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定标记学生"${row.studentName}"为缺席吗?`,
      '确认标记',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await writtenTestApi.markMissed(row.id)
    ElMessage.success('标记成功')
    loadTests()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
      ElMessage.error(error.message || '标记失败')
    }
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定删除该笔试记录吗?`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await writtenTestApi.delete(row.id)
    ElMessage.success('删除成功')
    loadTests()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleDialogClose = () => {
  resetForm()
}

// 加载笔试列表
const loadTests = async () => {
  loading.value = true
  try {
    const response = await writtenTestApi.getPage({
      current: currentPage.value,
      size: pageSize.value
    }) as any

    if (response && response.records) {
      tests.value = response.records
      total.value = response.total || 0
    } else {
      tests.value = []
      total.value = 0
    }
  } catch (error: any) {
    console.error('加载笔试列表失败:', error)
    ElMessage.error(error.message || '加载失败')
    tests.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载已筛选的申请(用于安排笔试)
const loadScreenedApplications = async () => {
  try {
    const response = await applicationApi.getPage({
      current: 1,
      size: 1000,
      status: 'screened'
    }) as any

    if (response && response.records) {
      screenedApplications.value = response.records
    } else {
      screenedApplications.value = []
    }
  } catch (error: any) {
    console.error('加载已筛选申请失败:', error)
    screenedApplications.value = []
  }
}

onMounted(() => {
  loadTests()
  loadScreenedApplications()
})
</script>

<style scoped>
.written-test-management {
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

.header-actions {
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
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
}

.action-buttons-container {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  align-items: center;
}

/* 操作按钮基础样式 */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 0.875rem;
  border: 1px solid transparent;
  border-radius: 0.5rem;
  font-size: 0.8125rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  background: white;
  color: oklch(0.35 0.02 260);
  box-shadow: 0 1px 2px oklch(0.80 0.02 260 / 0.1);
}

.action-btn svg {
  flex-shrink: 0;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn span {
  white-space: nowrap;
}

/* 查看按钮 */
.action-btn-view {
  color: oklch(0.50 0.15 220);
  background: oklch(0.97 0.01 220);
  border-color: oklch(0.90 0.02 220);
}

.action-btn-view:hover {
  background: oklch(0.92 0.04 220);
  border-color: oklch(0.70 0.15 220);
  color: oklch(0.40 0.18 220);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px oklch(0.80 0.02 260 / 0.15);
}

.action-btn-view:active {
  transform: translateY(0) scale(0.98);
}

/* 编辑按钮 */
.action-btn-edit {
  color: oklch(0.55 0.18 35);
  background: oklch(0.97 0.02 35);
  border-color: oklch(0.90 0.03 35);
}

.action-btn-edit:hover {
  background: oklch(0.92 0.08 35);
  border-color: oklch(0.75 0.18 35);
  color: oklch(0.45 0.22 35);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px oklch(0.80 0.02 260 / 0.15);
}

.action-btn-edit:hover svg {
  transform: rotate(-12deg);
}

.action-btn-edit:active {
  transform: translateY(0) scale(0.98);
}

/* 更多按钮 */
.action-btn-more {
  color: oklch(0.45 0.02 260);
  background: oklch(0.98 0.01 260);
  border-color: oklch(0.92 0.01 260);
}

.action-btn-more:hover {
  background: oklch(0.95 0.02 260);
  border-color: oklch(0.80 0.02 260);
  color: oklch(0.30 0.02 260);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px oklch(0.80 0.02 260 / 0.15);
}

.action-btn-more:hover svg {
  transform: rotate(90deg);
}

.action-btn-more:active {
  transform: translateY(0) scale(0.98);
}

/* 删除按钮 */
.action-btn-delete {
  color: oklch(0.55 0.22 25);
  background: oklch(0.98 0.02 25);
  border-color: oklch(0.92 0.04 25);
}

.action-btn-delete:hover {
  background: oklch(0.94 0.10 25);
  border-color: oklch(0.75 0.22 25);
  color: oklch(0.45 0.25 25);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px oklch(0.80 0.02 260 / 0.15);
}

.action-btn-delete:hover svg {
  transform: scale(1.1);
}

.action-btn-delete:active {
  transform: translateY(0) scale(0.98);
}

/* 下拉菜单样式 */
.action-dropdown-menu {
  border: 1px solid oklch(0.90 0.01 260);
  border-radius: 0.5rem;
  box-shadow: 0 10px 25px oklch(0.80 0.02 260 / 0.2);
  padding: 0.375rem;
}

:deep(.action-dropdown-menu .el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.625rem 0.875rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  color: oklch(0.35 0.02 260);
  transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.action-dropdown-menu .el-dropdown-menu__item svg) {
  flex-shrink: 0;
}

:deep(.dropdown-item-submit) {
  color: oklch(0.50 0.15 150);
}

:deep(.dropdown-item-submit:hover) {
  background: oklch(0.94 0.06 150);
  color: oklch(0.40 0.18 150);
}

:deep(.dropdown-item-missed) {
  color: oklch(0.55 0.18 45);
}

:deep(.dropdown-item-missed:hover) {
  background: oklch(0.94 0.08 45);
  color: oklch(0.45 0.22 45);
}

:deep(.dropdown-item-delete) {
  color: oklch(0.55 0.22 25);
}

:deep(.dropdown-item-delete:hover) {
  background: oklch(0.94 0.10 25);
  color: oklch(0.45 0.25 25);
}

.text-muted {
  color: oklch(0.60 0.02 260);
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
</style>
