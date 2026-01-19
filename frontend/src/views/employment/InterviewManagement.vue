<template>
  <div class="interview-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">面试管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          安排面试
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
          placeholder="面试状态"
          clearable
          @change="handleFilterChange"
          style="width: 150px;"
        >
          <el-option label="全部状态" value=""></el-option>
          <el-option label="已安排" value="scheduled"></el-option>
          <el-option label="已完成" value="completed"></el-option>
          <el-option label="已取消" value="cancelled"></el-option>
        </el-select>

        <el-select
          v-model="filterResult"
          placeholder="面试结果"
          clearable
          @change="handleFilterChange"
          style="width: 150px;"
        >
          <el-option label="全部结果" value=""></el-option>
          <el-option label="待面试" value="pending"></el-option>
          <el-option label="通过" value="passed"></el-option>
          <el-option label="未通过" value="failed"></el-option>
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
        :data="filteredInterviews"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" align="center" />

        <el-table-column prop="studentName" label="学生姓名" width="120" />

        <el-table-column prop="positionName" label="应聘职位" width="150" />

        <el-table-column label="面试轮次" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.round === 1" type="info" size="small">初试</el-tag>
            <el-tag v-else-if="row.round === 2" type="warning" size="small">复试</el-tag>
            <el-tag v-else-if="row.round === 3" type="success" size="small">终试</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="面试类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.interviewType === 'online'" type="primary" size="small">线上</el-tag>
            <el-tag v-else-if="row.interviewType === 'offline'" type="success" size="small">现场</el-tag>
            <el-tag v-else-if="row.interviewType === 'phone'" type="warning" size="small">电话</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="面试时间" width="180">
          <template #default="{ row }">
            <span v-if="row.interviewTime">{{ formatDateTime(row.interviewTime) }}</span>
            <span v-else class="text-muted">待安排</span>
          </template>
        </el-table-column>

        <el-table-column prop="interviewer" label="面试官" width="120" />

        <el-table-column label="面试状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="面试结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.result === 'pending'" type="info" size="small">待面试</el-tag>
            <el-tag v-else-if="row.result === 'passed'" type="success" size="small">通过</el-tag>
            <el-tag v-else-if="row.result === 'failed'" type="danger" size="small">未通过</el-tag>
            <el-tag v-else-if="row.result === 'cancelled'" type="warning" size="small">已取消</el-tag>
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
                v-if="row.status === 'scheduled'"
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
                v-if="row.status === 'scheduled'"
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
                    <el-dropdown-item command="submitResult" class="dropdown-item-submit">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <path d="M9 15l2 2 4-4"></path>
                      </svg>
                      <span>提交结果</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="reschedule" class="dropdown-item-reschedule">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
                        <path d="M3 3v5h5"></path>
                        <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74 2.74L21 16"></path>
                        <path d="M16 21h5v-5"></path>
                      </svg>
                      <span>重新安排</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="cancel" class="dropdown-item-cancel">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="15" y1="9" x2="9" y2="15"></line>
                        <line x1="9" y1="9" x2="15" y2="15"></line>
                      </svg>
                      <span>取消面试</span>
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

              <!-- 删除按钮(已完成或已取消) -->
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

    <!-- 面试表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
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
              v-for="app in passedApplications"
              :key="app.studentId"
              :label="app.studentName"
              :value="app.studentId"
            >
              <span>{{ app.studentName }}</span>
              <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">
                {{ app.positionName }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="应聘职位" v-if="dialogMode === 'add'">
          <el-input v-model="selectedPositionName" disabled />
        </el-form-item>

        <el-form-item label="面试轮次" prop="round">
          <el-select
            v-model="formData.round"
            placeholder="选择面试轮次"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
          >
            <el-option label="初试" :value="1"></el-option>
            <el-option label="复试" :value="2"></el-option>
            <el-option label="终试" :value="3"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="面试类型" prop="interviewType">
          <el-select
            v-model="formData.interviewType"
            placeholder="选择面试类型"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
            @change="handleInterviewTypeChange"
          >
            <el-option label="线上面试" value="online"></el-option>
            <el-option label="现场面试" value="offline"></el-option>
            <el-option label="电话面试" value="phone"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="面试时间" prop="interviewTime">
          <el-date-picker
            v-model="formData.interviewTime"
            type="datetime"
            placeholder="选择面试时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item
          label="面试地点"
          prop="location"
          v-if="formData.interviewType === 'offline' || formData.interviewType === 'phone'"
        >
          <el-input
            v-model="formData.location"
            placeholder="请输入面试地点"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>

        <el-form-item
          label="会议链接"
          prop="meetingLink"
          v-if="formData.interviewType === 'online'"
        >
          <el-input
            v-model="formData.meetingLink"
            placeholder="请输入会议链接"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>

        <el-form-item label="面试官" prop="interviewer">
          <el-input
            v-model="formData.interviewer"
            placeholder="请输入面试官姓名"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>

        <el-form-item label="面试官联系方式">
          <el-input
            v-model="formData.interviewerContact"
            placeholder="请输入面试官联系方式"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>

        <el-form-item label="面试结果" v-if="dialogMode === 'view' || dialogMode === 'submitResult'">
          <el-select
            v-model="formData.result"
            placeholder="选择面试结果"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
          >
            <el-option label="待面试" value="pending"></el-option>
            <el-option label="通过" value="passed"></el-option>
            <el-option label="未通过" value="failed"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="面试评分" v-if="dialogMode === 'view' || dialogMode === 'submitResult'">
          <el-input-number
            v-model="formData.score"
            :min="0"
            :max="100"
            :disabled="dialogMode === 'view'"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="面试评价" v-if="dialogMode === 'view' || dialogMode === 'submitResult'">
          <el-input
            v-model="formData.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入面试评价"
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
import { Plus, Refresh, Search, RefreshLeft } from '@element-plus/icons-vue'
import { interviewApi, type Interview } from '@/api/interview'
import { applicationApi } from '@/api/application'

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref('')
const filterResult = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const interviews = ref<Interview[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view' | 'submitResult' | 'reschedule'>('add')
const formRef = ref()

// 表单数据
const formData = ref<Interview>({
  applicationId: 0,
  positionId: 0,
  studentId: 0,
  round: 1,
  interviewType: 'online',
  interviewTime: '',
  location: '',
  meetingLink: '',
  interviewer: '',
  interviewerContact: '',
  result: 'pending',
  score: undefined,
  comment: '',
  status: 'scheduled'
})

// 已通过笔试或已筛选的申请(用于安排面试)
const passedApplications = ref<any[]>([])
const selectedPositionName = ref('')

// 表单验证规则
const formRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  round: [{ required: true, message: '请选择面试轮次', trigger: 'change' }],
  interviewType: [{ required: true, message: '请选择面试类型', trigger: 'change' }],
  interviewTime: [{ required: true, message: '请选择面试时间', trigger: 'change' }],
  interviewer: [{ required: true, message: '请输入面试官姓名', trigger: 'blur' }]
}

// 计算属性
const dialogTitle = computed(() => {
  const titles = {
    add: '安排面试',
    edit: '编辑面试',
    view: '查看面试详情',
    submitResult: '提交面试结果',
    reschedule: '重新安排面试'
  }
  return titles[dialogMode.value]
})

const filteredInterviews = computed(() => {
  let result = interviews.value

  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter((interview) =>
      interview.studentName?.toLowerCase().includes(keyword) ||
      interview.positionName?.toLowerCase().includes(keyword) ||
      interview.interviewer?.toLowerCase().includes(keyword)
    )
  }

  // 状态过滤
  if (filterStatus.value) {
    result = result.filter((interview) => interview.status === filterStatus.value)
  }

  // 结果过滤
  if (filterResult.value) {
    result = result.filter((interview) => interview.result === filterResult.value)
  }

  return result
})

// 方法
const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'scheduled': '已安排',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return map[status] || status
}

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    'scheduled': 'warning',
    'completed': 'success',
    'cancelled': 'info'
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
  filterResult.value = ''
  currentPage.value = 1
}

const handleRefresh = () => {
  loadInterviews()
  loadPassedApplications()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadInterviews()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadInterviews()
}

const resetForm = () => {
  formData.value = {
    applicationId: 0,
    positionId: 0,
    studentId: 0,
    round: 1,
    interviewType: 'online',
    interviewTime: '',
    location: '',
    meetingLink: '',
    interviewer: '',
    interviewerContact: '',
    result: 'pending',
    score: undefined,
    comment: '',
    status: 'scheduled'
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
  const selected = passedApplications.value.find(
    (app) => app.studentId === studentId
  )
  if (selected) {
    formData.value.applicationId = selected.id
    formData.value.positionId = selected.positionId
    selectedPositionName.value = selected.positionName
  }
}

const handleInterviewTypeChange = (type: string) => {
  // 清空不相关的字段
  if (type === 'online') {
    formData.value.location = ''
  } else if (type === 'offline' || type === 'phone') {
    formData.value.meetingLink = ''
  }
}

const handleMoreAction = async (command: string, row: any) => {
  switch (command) {
    case 'submitResult':
      Object.assign(formData.value, row)
      dialogMode.value = 'submitResult'
      dialogVisible.value = true
      break
    case 'reschedule':
      Object.assign(formData.value, row)
      dialogMode.value = 'reschedule'
      dialogVisible.value = true
      break
    case 'cancel':
      await handleCancel(row)
      break
    case 'delete':
      await handleDelete(row)
      break
  }
}

// 格式化日期时间为后端期望的格式 (yyyy-MM-dd HH:mm)
const formatDateTimeForBackend = (dateTime: string): string => {
  if (!dateTime) return ''
  // 移除秒部分，从 "yyyy-MM-dd HH:mm:ss" 转换为 "yyyy-MM-dd HH:mm"
  return dateTime.replace(/:\d{2}$/, '')
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    if (dialogMode.value === 'add') {
      await interviewApi.schedule({
        applicationId: formData.value.applicationId,
        round: formData.value.round!,
        interviewType: formData.value.interviewType!,
        interviewTime: formatDateTimeForBackend(formData.value.interviewTime!),
        location: formData.value.location,
        meetingLink: formData.value.meetingLink,
        interviewer: formData.value.interviewer!,
        interviewerContact: formData.value.interviewerContact
      })
      ElMessage.success('安排面试成功')
    } else if (dialogMode.value === 'edit') {
      await interviewApi.update(formData.value)
      ElMessage.success('更新成功')
    } else if (dialogMode.value === 'submitResult') {
      await interviewApi.submitResult({
        id: formData.value.id!,
        result: formData.value.result,
        score: formData.value.score,
        comment: formData.value.comment
      })
      ElMessage.success('提交结果成功')
    } else if (dialogMode.value === 'reschedule') {
      await interviewApi.reschedule({
        id: formData.value.id!,
        interviewTime: formatDateTimeForBackend(formData.value.interviewTime!),
        location: formData.value.location,
        meetingLink: formData.value.meetingLink
      })
      ElMessage.success('重新安排成功')
    }

    dialogVisible.value = false
    loadInterviews()
  } catch (error: any) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleCancel = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定取消该面试吗?`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await interviewApi.cancel(row.id)
    ElMessage.success('取消成功')
    loadInterviews()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
      ElMessage.error(error.message || '取消失败')
    }
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定删除该面试记录吗?`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await interviewApi.delete(row.id)
    ElMessage.success('删除成功')
    loadInterviews()
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

// 加载面试列表
const loadInterviews = async () => {
  loading.value = true
  try {
    const response = await interviewApi.getPage({
      current: currentPage.value,
      size: pageSize.value
    }) as any

    if (response && response.records) {
      interviews.value = response.records
      total.value = response.total || 0
    } else {
      interviews.value = []
      total.value = 0
    }
  } catch (error: any) {
    console.error('加载面试列表失败:', error)
    ElMessage.error(error.message || '加载失败')
    interviews.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载已通过笔试或已筛选的申请(用于安排面试)
const loadPassedApplications = async () => {
  try {
    const response = await applicationApi.getPage({
      current: 1,
      size: 1000
    }) as any

    if (response && response.records) {
      // 筛选笔试通过或已筛选的申请
      passedApplications.value = response.records.filter((app: any) =>
        app.status === 'test_passed' || app.status === 'screened'
      )
    } else {
      passedApplications.value = []
    }
  } catch (error: any) {
    console.error('加载申请失败:', error)
    passedApplications.value = []
  }
}

onMounted(() => {
  loadInterviews()
  loadPassedApplications()
})
</script>

<style scoped>
.interview-management {
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

:deep(.dropdown-item-reschedule) {
  color: oklch(0.50 0.15 220);
}

:deep(.dropdown-item-reschedule:hover) {
  background: oklch(0.94 0.04 220);
  color: oklch(0.40 0.18 220);
}

:deep(.dropdown-item-cancel) {
  color: oklch(0.55 0.18 45);
}

:deep(.dropdown-item-cancel:hover) {
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
