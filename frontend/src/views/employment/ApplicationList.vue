

<template>
  <div class="application-list">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchForm.studentName"
            placeholder="请输入学生姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="企业">
          <el-select
            v-model="searchForm.companyId"
            placeholder="请选择企业"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="company in companyList"
              :key="company.id"
              :label="company.companyName"
              :value="company.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-select
            v-model="searchForm.positionId"
            placeholder="请选择职位"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="position in positionList"
              :key="position.id"
              :label="position.positionName"
              :value="position.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in APPLICATION_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-actions">
        <el-button
          type="success"
          :icon="CirclePlus"
          v-permission="'employment:application:add'"
          @click="handleAdd"
        >
          新增申请
        </el-button>
        <el-button
          type="danger"
          :icon="Delete"
          v-permission="'employment:application:delete'"
          @click="handleBatchDelete"
          :disabled="selectedIds.length === 0"
        >
          批量删除
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        @selection-change="handleSelectionChange"
        class="data-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="positionName" label="职位名称" width="180" />
        <el-table-column prop="companyName" label="企业名称" width="200" />
        <el-table-column label="申请状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="当前阶段" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStageType(row.currentStage)">
            {{ getStageLabel(row.currentStage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicationTime" label="申请日期" width="160" />
        <el-table-column prop="hrRemark" label="备注" width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="320" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="View"
              v-permission="'employment:application:view'"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 'pending' && row.currentStage === 'resume'"
              type="success"
              size="small"
              :icon="Select"
              v-permission="'employment:application:screen'"
              @click="handleScreen(row, true)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending' && row.currentStage === 'resume'"
              type="warning"
              size="small"
              :icon="Close"
              v-permission="'employment:application:screen'"
              @click="handleScreen(row, false)"
            >
              不通过
            </el-button>
            <el-dropdown v-permission="'employment:application:edit'">
              <el-button type="info" size="small" :icon="More">
                更多
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :icon="Edit" @click="handleEdit(row)">编辑</el-dropdown-item>
                  <el-dropdown-item :icon="Document" @click="handleUpdateStatus(row)">更新状态</el-dropdown-item>
                  <el-dropdown-item :icon="Delete" @click="handleDelete(row)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="formData.studentId"
            placeholder="请选择学生"
            filterable
            style="width: 100%"
            :disabled="dialogMode === 'view'"
          >
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="`${student.name} (${student.studentId})`"
              :value="student.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职位" prop="positionId">
          <el-select
            v-model="formData.positionId"
            placeholder="请选择职位"
            filterable
            style="width: 100%"
            @change="handlePositionChange"
            :disabled="dialogMode === 'view'"
          >
            <el-option
              v-for="position in positionList"
              :key="position.id"
              :label="`${position.positionName} - ${position.companyName}`"
              :value="position.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-input v-model="formData.companyName" disabled />
        </el-form-item>
        <el-form-item label="简历" prop="resumeId">
          <el-select
            v-model="formData.resumeId"
            placeholder="请选择简历"
            style="width: 100%"
            :disabled="dialogMode === 'view'"
          >
            <el-option
              v-for="resume in resumeList"
              :key="resume.id"
              :label="resume.title"
              :value="resume.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="formData.applicationTime"
            type="datetime"
            placeholder="申请时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>
        <el-form-item label="申请状态">
          <el-select
            v-model="formData.status"
            placeholder="请选择状态"
            clearable
            style="width: 100%"
            :disabled="dialogMode === 'view'"
          >
            <el-option
              v-for="item in APPLICATION_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="当前阶段">
          <el-select
            v-model="formData.currentStage"
            placeholder="请选择阶段"
            clearable
            style="width: 100%"
            :disabled="dialogMode === 'view'"
          >
            <el-option
              v-for="item in APPLICATION_STAGE_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.hrRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            :disabled="dialogMode === 'view'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="dialogMode !== 'view'">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态更新对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="更新申请状态"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="statusForm" :rules="statusRules" ref="statusFormRef" label-width="100px">
        <el-form-item label="申请状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option
              v-for="item in APPLICATION_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="当前阶段" prop="currentStage">
          <el-select v-model="statusForm.currentStage" placeholder="请选择阶段" style="width: 100%">
            <el-option
              v-for="item in APPLICATION_STAGE_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="statusForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 简历筛选对话框 -->
    <el-dialog
      v-model="screenDialogVisible"
      :title="screenForm.passed ? '通过简历筛选' : '不通过简历筛选'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="screenForm" ref="screenFormRef" label-width="100px">
        <el-form-item label="学生姓名">
          <el-input v-model="screenForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="职位名称">
          <el-input v-model="screenForm.positionName" disabled />
        </el-form-item>
        <el-form-item label="企业名称">
          <el-input v-model="screenForm.companyName" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="screenForm.remark"
            type="textarea"
            :rows="4"
            :placeholder="screenForm.passed ? '请输入通过理由（可选）' : '请输入不通过理由'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="screenDialogVisible = false">取消</el-button>
        <el-button
          :type="screenForm.passed ? 'success' : 'warning'"
          @click="handleScreenSubmit"
        >
          {{ screenForm.passed ? '通过' : '不通过' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  CirclePlus,
  Delete,
  Edit,
  View,
  More,
  Select,
  Close,
  Document
} from '@element-plus/icons-vue'
import {
  getJobApplicationPageApi,
  getJobApplicationByIdApi,
  createJobApplicationApi,
  updateJobApplicationApi,
  deleteJobApplicationApi,
  batchDeleteJobApplicationApi,
  screenResumeApi,
  updateApplicationStatusApi
} from '@/api/jobApplication'
import { getCompanyPageApi } from '@/api/company'
import { getPositionPageApi } from '@/api/position'
import { getStudentPageApi } from '@/api/student'
import {
  APPLICATION_STATUS_OPTIONS,
  APPLICATION_STAGE_OPTIONS,
  getStatusType,
  getStatusLabel,
  getStageType,
  getStageLabel
} from '@/constants/application'

// 响应式数据
const loading = ref(false)
const tableData = ref<any[]>([])
const selectedIds = ref<number[]>([])

// 搜索表单
const searchForm = reactive({
  studentName: '',
  companyId: undefined as number | undefined,
  positionId: undefined as number | undefined,
  status: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 下拉列表数据
const companyList = ref<any[]>([])
const positionList = ref<any[]>([])
const studentList = ref<any[]>([])
const resumeList = ref<any[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view'>('add')
const dialogTitle = computed(() => {
  const modeMap = {
    add: '新增申请',
    edit: '编辑申请',
    view: '查看申请'
  }
  return modeMap[dialogMode.value]
})

// 表单数据
const formData = reactive({
  id: undefined as number | undefined,
  studentId: undefined as number | undefined,
  positionId: undefined as number | undefined,
  companyId: undefined as number | undefined,
  companyName: '',
  resumeId: undefined as number | undefined,
  applicationTime: '',
  hrRemark: '',
  status: '',
  currentStage: ''
})

const formRef = ref<FormInstance>()
const formRules: FormRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  positionId: [{ required: true, message: '请选择职位', trigger: 'change' }],
  resumeId: [{ required: true, message: '请选择简历', trigger: 'change' }]
}

// 状态更新对话框
const statusDialogVisible = ref(false)
const statusForm = reactive({
  id: undefined as number | undefined,
  status: '',
  currentStage: '',
  remark: ''
})
const statusFormRef = ref<FormInstance>()
const statusRules: FormRules = {
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  currentStage: [{ required: true, message: '请选择阶段', trigger: 'change' }]
}

// 简历筛选对话框
const screenDialogVisible = ref(false)
const screenForm = reactive({
  id: undefined as number | undefined,
  studentName: '',
  positionName: '',
  companyName: '',
  passed: false,
  remark: ''
})
const screenFormRef = ref<FormInstance>()

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      studentName: searchForm.studentName || undefined,
      companyId: searchForm.companyId,
      positionId: searchForm.positionId,
      status: searchForm.status || undefined
    }
    const response = await getJobApplicationPageApi(params)
    // 响应拦截器返回的是 data 部分,直接使用
    tableData.value = response.records
    pagination.total = response.total
  } catch (error) {
    console.error('获取求职申请列表失败:', error)
    ElMessage.error('获取求职申请列表失败')
  } finally {
    loading.value = false
  }
}

// 获取下拉列表数据
const fetchDropdownData = async () => {
  try {
    // 获取企业列表
    const companyRes = await getCompanyPageApi({ current: 1, size: 1000 })
    companyList.value = companyRes.records || []

    // 获取职位列表
    const positionRes = await getPositionPageApi({ current: 1, size: 1000 })
    positionList.value = positionRes.records || []

    // 获取学生列表
    const studentRes = await getStudentPageApi({ current: 1, size: 1000 })
    studentList.value = studentRes.records || []
  } catch (error) {
    console.error('获取下拉列表数据失败:', error)
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
  searchForm.companyId = undefined
  searchForm.positionId = undefined
  searchForm.status = ''
  searchForm.currentStage = ''
  handleSearch()
}

// 表格选择
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增
const handleAdd = () => {
  dialogMode.value = 'add'
  dialogVisible.value = true
  resetForm()
}

// 查看
const handleView = async (row: any) => {
  dialogMode.value = 'view'
  dialogVisible.value = true
  try {
    const response = await getJobApplicationByIdApi(row.id)
    if (response.code === 200) {
      Object.assign(formData, response.data)
    }
  } catch (error) {
    console.error('获取申请详情失败:', error)
  }
}

// 编辑
const handleEdit = async (row: any) => {
  dialogMode.value = 'edit'
  dialogVisible.value = true
  try {
    const response = await getJobApplicationByIdApi(row.id)
    if (response.code === 200) {
      Object.assign(formData, response.data)
    }
  } catch (error) {
    console.error('获取申请详情失败:', error)
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该申请吗?', '提示', {
      type: 'warning'
    })
    const response = await deleteJobApplicationApi(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条申请吗?`, '提示', {
      type: 'warning'
    })
    const response = await batchDeleteJobApplicationApi(selectedIds.value)
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      fetchData()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = { ...formData }
        const response = dialogMode.value === 'add'
          ? await createJobApplicationApi(data)
          : await updateJobApplicationApi(data)

        if (response.code === 200) {
          ElMessage.success(dialogMode.value === 'add' ? '新增成功' : '更新成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 更新状态
const handleUpdateStatus = (row: any) => {
  statusForm.id = row.id
  statusForm.status = row.status || ''
  statusForm.currentStage = row.currentStage || ''
  statusForm.remark = ''
  statusDialogVisible.value = true
}

// 提交状态更新
const handleStatusSubmit = async () => {
  if (!statusFormRef.value) return
  await statusFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await updateApplicationStatusApi(
          statusForm.id!,
          statusForm.status,
          statusForm.currentStage
        )
        if (response.code === 200) {
          ElMessage.success('状态更新成功')
          statusDialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || '状态更新失败')
        }
      } catch (error) {
        console.error('状态更新失败:', error)
        ElMessage.error('状态更新失败')
      }
    }
  })
}

// 简历筛选
const handleScreen = (row: any, passed: boolean) => {
  screenForm.id = row.id
  screenForm.studentName = row.studentName
  screenForm.positionName = row.positionName
  screenForm.companyName = row.companyName
  screenForm.passed = passed
  screenForm.remark = ''
  screenDialogVisible.value = true
}

// 提交简历筛选
const handleScreenSubmit = async () => {
  try {
    const response = await screenResumeApi(
      screenForm.id!,
      screenForm.passed,
      screenForm.remark
    )
    if (response.code === 200) {
      ElMessage.success(screenForm.passed ? '已通过简历筛选' : '已标记为不通过')
      screenDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 职位改变
const handlePositionChange = () => {
  const position = positionList.value.find(p => p.id === formData.positionId)
  if (position) {
    formData.companyId = position.companyId
    formData.companyName = position.companyName
  }
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.studentId = undefined
  formData.positionId = undefined
  formData.companyId = undefined
  formData.companyName = ''
  formData.resumeId = undefined
  formData.applicationTime = ''
  formData.hrRemark = ''
  formData.status = ''
  formData.currentStage = ''
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  fetchData()
  fetchDropdownData()
})
</script>

<style scoped lang="scss">
.application-list {
  padding: 20px;

  .search-form {
    margin-bottom: 20px;
  }

  .table-actions {
    margin-bottom: 15px;
  }

  .data-table {
    margin-bottom: 20px;
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
  }
}
</style>
