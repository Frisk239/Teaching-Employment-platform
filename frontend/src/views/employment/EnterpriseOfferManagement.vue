<template>
  <div class="enterprise-offer-management">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOffers || 0 }}</div>
              <div class="stat-label">全部Offer</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOffers || 0 }}</div>
              <div class="stat-label">待确认</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><Select /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.acceptedOffers || 0 }}</div>
              <div class="stat-label">已接受</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.hiredCount || 0 }}</div>
              <div class="stat-label">已入职</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" :icon="Plus" @click="handleCreate">
            发送Offer
          </el-button>

          <el-button type="default" :icon="Download" :disabled="selectedIds.length === 0" @click="handleExport">
            导出
          </el-button>
        </div>

        <div class="toolbar-right">
          <el-select
            v-model="queryParams.status"
            placeholder="Offer状态"
            clearable
            style="width: 150px; margin-right: 10px"
            @change="loadOffers"
          >
            <el-option label="待接受" value="pending" />
            <el-option label="已接受" value="accepted" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="已过期" value="expired" />
          </el-select>

          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索学员姓名"
            clearable
            style="width: 260px"
            :prefix-icon="Search"
            @clear="loadOffers"
            @keyup.enter="loadOffers"
          />

          <el-button :icon="Refresh" @click="loadOffers" />
        </div>
      </div>
    </el-card>

    <!-- Offer列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="offerList"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="offerNo" label="Offer编号" width="150" />

        <el-table-column prop="studentName" label="学员姓名" width="120">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 8px">
              <el-avatar :size="32" :src="row.studentAvatar">
                {{ row.studentName?.charAt(0) }}
              </el-avatar>
              <span>{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="positionName" label="职位名称" width="150" />

        <el-table-column prop="city" label="入职城市" width="120" />

        <el-table-column label="薪资" width="150">
          <template #default="{ row }">
            <span style="font-weight: 500; color: #67c23a">
              ¥{{ row.salary }} / {{ row.salaryUnit === 'month' ? '月' : '年' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="onboardDate" label="入职日期" width="120">
          <template #default="{ row }">
            {{ row.onboardDate || '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="acceptDeadline" label="接受截止" width="120">
          <template #default="{ row }">
            {{ row.acceptDeadline || '-' }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="发送时间" width="160" />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              link
              type="warning"
              size="small"
              @click="handleCancel(row)"
            >
              撤销
            </el-button>
            <el-button link type="primary" size="small" @click="handleReminder(row)">
              提醒
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadOffers"
        @current-change="loadOffers"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 发送Offer对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="申请记录" prop="applicationId">
          <el-select
            v-model="formData.applicationId"
            placeholder="选择通过的面试申请"
            filterable
            style="width: 100%"
            @change="handleApplicationChange"
          >
            <el-option
              v-for="app in applicationOptions"
              :key="app.id"
              :label="`${app.studentName} - ${app.positionName}`"
              :value="app.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="职位名称" prop="positionName">
          <el-input v-model="formData.positionName" placeholder="职位名称" disabled />
        </el-form-item>

        <el-form-item label="入职城市" prop="city">
          <el-input v-model="formData.city" placeholder="如：北京" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input-number
                v-model="formData.salary"
                :min="0"
                :step="1000"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资单位" prop="salaryUnit">
              <el-select v-model="formData.salaryUnit" placeholder="选择单位" style="width: 100%">
                <el-option label="元/月" value="month" />
                <el-option label="元/年" value="year" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="入职日期" prop="onboardDate">
          <el-date-picker
            v-model="formData.onboardDate"
            type="date"
            placeholder="选择入职日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="接受截止" prop="acceptDeadline">
          <el-date-picker
            v-model="formData.acceptDeadline"
            type="date"
            placeholder="选择接受截止日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          发送Offer
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看Offer详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="Offer详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentOffer">
        <el-descriptions-item label="Offer编号">
          {{ currentOffer.offerNo }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOffer.status)">
            {{ getStatusText(currentOffer.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="学员姓名">
          {{ currentOffer.studentName }}
        </el-descriptions-item>
        <el-descriptions-item label="职位名称">
          {{ currentOffer.positionName }}
        </el-descriptions-item>
        <el-descriptions-item label="入职城市">
          {{ currentOffer.city }}
        </el-descriptions-item>
        <el-descriptions-item label="薪资">
          ¥{{ currentOffer.salary }} / {{ currentOffer.salaryUnit === 'month' ? '月' : '年' }}
        </el-descriptions-item>
        <el-descriptions-item label="入职日期">
          {{ currentOffer.onboardDate }}
        </el-descriptions-item>
        <el-descriptions-item label="接受截止">
          {{ currentOffer.acceptDeadline }}
        </el-descriptions-item>
        <el-descriptions-item label="接受时间" :span="2">
          {{ currentOffer.acceptTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentOffer.remark || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="发送时间" :span="2">
          {{ currentOffer.createTime }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button type="primary" @click="detailDialogVisible = false">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus, Refresh, Search, Download, Document, Clock, Select, User
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// 统计数据
const stats = ref({
  totalOffers: 0,
  pendingOffers: 0,
  acceptedOffers: 0,
  hiredCount: 0
})

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  status: '',
  keyword: ''
})

// Offer列表
const offerList = ref([])
const total = ref(0)
const loading = ref(false)

// 选中的ID
const selectedIds = ref<number[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('发送Offer')
const detailDialogVisible = ref(false)
const currentOffer = ref<any>(null)
const submitting = ref(false)

// 表单相关
const formRef = ref<FormInstance>()
const formData = reactive({
  applicationId: null,
  positionName: '',
  city: '',
  salary: null,
  salaryUnit: 'month',
  onboardDate: '',
  acceptDeadline: '',
  remark: ''
})

const formRules: FormRules = {
  applicationId: [{ required: true, message: '请选择申请记录', trigger: 'change' }],
  positionName: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入入职城市', trigger: 'blur' }],
  salary: [{ required: true, message: '请输入薪资', trigger: 'blur' }],
  salaryUnit: [{ required: true, message: '请选择薪资单位', trigger: 'change' }],
  onboardDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  acceptDeadline: [{ required: true, message: '请选择接受截止日期', trigger: 'change' }]
}

// 申请选项
const applicationOptions = ref([])

// 加载Offer列表
const loadOffers = async () => {
  loading.value = true
  try {
    const response: any = await request.get('/offer/page', {
      params: queryParams
    })
    offerList.value = response.records || []
    total.value = response.total || 0
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载Offer列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 这里使用企业的ID，实际应该从用户信息中获取
    const response: any = await request.get('/offer/stats/company/1')
    stats.value = response
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 加载申请选项（面试通过的）
const loadApplicationOptions = async () => {
  try {
    const response: any = await request.get('/application/page', {
      params: {
        current: 1,
        size: 1000,
        status: 'interview_passed'
      }
    })
    applicationOptions.value = response.records || []
  } catch (error) {
    console.error('加载申请选项失败', error)
  }
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    pending: 'warning',
    accepted: 'success',
    rejected: 'danger',
    cancelled: 'info',
    expired: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待接受',
    accepted: '已接受',
    rejected: '已拒绝',
    cancelled: '已取消',
    expired: '已过期'
  }
  return textMap[status] || status
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 发送Offer
const handleCreate = () => {
  dialogTitle.value = '发送Offer'
  dialogVisible.value = true
  loadApplicationOptions()
}

// 申请变化
const handleApplicationChange = (applicationId: number) => {
  const application = applicationOptions.value.find((app: any) => app.id === applicationId)
  if (application) {
    formData.positionName = application.positionName
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await request.post('/offer/create-and-send', null, {
        params: formData
      })
      ElMessage.success('Offer发送成功')
      dialogVisible.value = false
      resetForm()
      loadOffers()
      loadStats()
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '发送失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.applicationId = null
  formData.positionName = ''
  formData.city = ''
  formData.salary = null
  formData.salaryUnit = 'month'
  formData.onboardDate = ''
  formData.acceptDeadline = ''
  formData.remark = ''
  formRef.value?.resetFields()
}

// 查看详情
const handleView = (row: any) => {
  currentOffer.value = row
  detailDialogVisible.value = true
}

// 撤销Offer
const handleCancel = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要撤销这个Offer吗？', '提示', {
      type: 'warning'
    })
    await request.post(`/offer/${row.id}/cancel`)
    ElMessage.success('撤销成功')
    loadOffers()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '撤销失败')
    }
  }
}

// 发送提醒
const handleReminder = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发送提醒邮件吗？', '提示', {
      type: 'info'
    })
    // 这里应该调用发送提醒的接口
    ElMessage.success('提醒发送成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发送失败')
    }
  }
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

onMounted(() => {
  loadOffers()
  loadStats()
})
</script>

<style scoped lang="scss">
.enterprise-offer-management {
  padding: 20px;

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
            line-height: 1;
            margin-bottom: 8px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .toolbar-card {
    margin-bottom: 20px;

    .toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .toolbar-left,
      .toolbar-right {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }
  }

  .table-card {
    :deep(.el-table) {
      .el-avatar {
        background-color: #ecf5ff;
        color: #409eff;
      }
    }
  }
}
</style>
