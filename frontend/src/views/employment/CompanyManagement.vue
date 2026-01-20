<template>
  <div class="company-management">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-primary">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">企业总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-warning">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待认证</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-success">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><Select /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.approved }}</div>
              <div class="stat-label">已认证</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-danger">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.rejected }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="企业名称/联系人"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="认证状态">
          <el-select v-model="queryParams.verifyStatus" placeholder="全部" clearable @change="handleSearch" @clear="handleSearch" style="width: 150px">
            <el-option label="待认证" value="pending" />
            <el-option label="已认证" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable @change="handleSearch" @clear="handleSearch" style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">企业列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增企业
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="companyName" label="企业名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="industry" label="所属行业" width="120" />
        <el-table-column prop="scale" label="企业规模" width="120">
          <template #default="{ row }">
            <el-tag>{{ getScaleLabel(row.scale) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="city" label="所在城市" width="120" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="verifyStatus" label="认证状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getVerifyStatusType(row.verifyStatus)">
              {{ getVerifyStatusLabel(row.verifyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="primary" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-if="row.verifyStatus === 'pending'"
              link
              type="warning"
              @click="handleVerify(row)"
            >
              <el-icon><Checked /></el-icon>
              审核
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
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
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-divider content-position="left">企业基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业名称" prop="companyName">
              <el-input v-model="formData.companyName" placeholder="请输入企业全称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业简称" prop="shortName">
              <el-input v-model="formData.shortName" placeholder="请输入企业简称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="formData.creditCode" placeholder="18位统一社会信用代码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-select v-model="formData.industry" placeholder="请选择行业" style="width: 100%">
                <el-option label="互联网/IT" value="互联网/IT" />
                <el-option label="金融" value="金融" />
                <el-option label="教育" value="教育" />
                <el-option label="制造业" value="制造业" />
                <el-option label="零售/消费" value="零售/消费" />
                <el-option label="医疗健康" value="医疗健康" />
                <el-option label="房地产" value="房地产" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">企业详细信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业规模" prop="scale">
              <el-select v-model="formData.scale" placeholder="请选择规模" style="width: 100%">
                <el-option label="1-10人" value="1-10人" />
                <el-option label="11-50人" value="11-50人" />
                <el-option label="51-100人" value="51-100人" />
                <el-option label="101-500人" value="101-500人" />
                <el-option label="500人以上" value="500人以上" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发展阶段" prop="stage">
              <el-select v-model="formData.stage" placeholder="请选择阶段" style="width: 100%">
                <el-option label="初创期" value="startup" />
                <el-option label="成长期" value="growth" />
                <el-option label="成熟期" value="maturity" />
                <el-option label="上市" value="listing" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所在城市" prop="city">
              <el-input v-model="formData.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="官网地址" prop="website">
              <el-input v-model="formData.website" placeholder="https://" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入详细地址" />
        </el-form-item>

        <el-divider content-position="left">联系信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="formData.contactPerson" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="contactPosition">
              <el-input v-model="formData.contactPosition" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="contactEmail">
              <el-input v-model="formData.contactEmail" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">其他信息</el-divider>
        <el-form-item label="企业Logo" prop="logo">
          <el-input v-model="formData.logo" placeholder="请输入Logo URL" />
        </el-form-item>
        <el-form-item label="企业简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请输入企业简介(最多500字)"
          />
        </el-form-item>
        <el-form-item label="福利待遇" prop="benefits">
          <el-input
            v-model="formData.benefits"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="请输入福利待遇(最多500字)"
          />
        </el-form-item>

        <el-divider content-position="left">状态设置</el-divider>
        <el-form-item label="启用状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="企业详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业名称" :span="2">
          {{ currentCompany.companyName }}
        </el-descriptions-item>
        <el-descriptions-item label="企业简称">
          {{ currentCompany.shortName }}
        </el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码">
          {{ currentCompany.creditCode }}
        </el-descriptions-item>
        <el-descriptions-item label="所属行业">
          {{ currentCompany.industry }}
        </el-descriptions-item>
        <el-descriptions-item label="企业规模">
          <el-tag>{{ getScaleLabel(currentCompany.scale) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发展阶段">
          <el-tag :type="getStageType(currentCompany.stage)">
            {{ getStageLabel(currentCompany.stage) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所在城市">
          {{ currentCompany.city }}
        </el-descriptions-item>
        <el-descriptions-item label="官网地址" :span="2">
          <a v-if="currentCompany.website" :href="currentCompany.website" target="_blank">
            {{ currentCompany.website }}
          </a>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">
          {{ currentCompany.address || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系人">
          {{ currentCompany.contactPerson }}
        </el-descriptions-item>
        <el-descriptions-item label="职位">
          {{ currentCompany.contactPosition }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentCompany.contactPhone }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ currentCompany.contactEmail }}
        </el-descriptions-item>
        <el-descriptions-item label="认证状态">
          <el-tag :type="getVerifyStatusType(currentCompany.verifyStatus)">
            {{ getVerifyStatusLabel(currentCompany.verifyStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="认证时间">
          {{ currentCompany.verifyTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="启用状态">
          <el-tag :type="currentCompany.status === 1 ? 'success' : 'danger'">
            {{ currentCompany.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="企业简介" :span="2">
          <div class="description-text">{{ currentCompany.description || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="福利待遇" :span="2">
          <div class="description-text">{{ currentCompany.benefits || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ currentCompany.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ currentCompany.updateTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 认证审核对话框 -->
    <el-dialog
      v-model="verifyDialogVisible"
      title="企业认证审核"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="verifyFormRef" :model="verifyForm" :rules="verifyRules" label-width="100px">
        <el-form-item label="企业名称">
          <el-input :value="currentCompany.companyName" disabled />
        </el-form-item>
        <el-form-item label="审核结果" prop="verifyStatus">
          <el-radio-group v-model="verifyForm.verifyStatus">
            <el-radio value="approved">
              <el-icon color="#67c23a"><CircleCheck /></el-icon>
              通过认证
            </el-radio>
            <el-radio value="rejected">
              <el-icon color="#f56c6c"><CircleClose /></el-icon>
              拒绝认证
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="拒绝原因"
          prop="rejectReason"
          v-if="verifyForm.verifyStatus === 'rejected'"
        >
          <el-input
            v-model="verifyForm.rejectReason"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
            placeholder="请输入拒绝原因(必填)"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="verifyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleVerifySubmit" :loading="verifyLoading">
          提交审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  OfficeBuilding,
  Clock,
  Select,
  CircleClose,
  Search,
  RefreshLeft,
  Plus,
  View,
  Edit,
  Delete,
  Checked,
  CircleCheck
} from '@element-plus/icons-vue'
import {
  getCompanyPageApi,
  createCompanyApi,
  updateCompanyApi,
  deleteCompanyApi,
  verifyCompanyApi,
  type Company,
  type CompanyQueryParams
} from '@/api/company'

// 查询参数
const queryParams = reactive<CompanyQueryParams>({
  current: 1,
  size: 10,
  keyword: '',
  verifyStatus: '',
  status: undefined
})

// 统计数据
const stats = reactive({
  total: 0,
  pending: 0,
  approved: 0,
  rejected: 0
})

// 表格数据
const tableData = ref<Company[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑企业' : '新增企业'))
const viewDialogVisible = ref(false)
const verifyDialogVisible = ref(false)
const currentCompany = ref<Company>({})

// 表单数据
const formData = reactive<Company>({
  status: 1
})
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 审核表单
const verifyForm = reactive({
  verifyStatus: 'approved',
  rejectReason: ''
})
const verifyFormRef = ref<FormInstance>()
const verifyLoading = ref(false)

// 表单验证规则
const formRules: FormRules = {
  companyName: [
    { required: true, message: '请输入企业名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  creditCode: [
    { required: true, message: '请输入统一社会信用代码', trigger: 'blur' },
    {
      pattern: /^[0-9A-HJ-NPQRTUWXY]{2}\d{6}[0-9A-HJ-NPQRTUWXY]{10}$/,
      message: '请输入正确的统一社会信用代码',
      trigger: 'blur'
    }
  ],
  industry: [{ required: true, message: '请选择所属行业', trigger: 'change' }],
  scale: [{ required: true, message: '请选择企业规模', trigger: 'change' }],
  stage: [{ required: true, message: '请选择发展阶段', trigger: 'change' }],
  city: [{ required: true, message: '请输入所在城市', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 审核表单验证规则
const verifyRules: FormRules = {
  verifyStatus: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  rejectReason: [
    {
      required: false,
      validator: (_rule, value, callback) => {
        if (verifyForm.verifyStatus === 'rejected' && !value) {
          callback(new Error('拒绝认证时必须填写拒绝原因'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取企业列表
const fetchData = async () => {
  loading.value = true
  try {
    const response = await getCompanyPageApi(queryParams)
    // 响应拦截器已经返回了 data，所以 response 就是 IPage<Company>
    tableData.value = response.records || []
    total.value = response.total || 0

    // 更新统计数据
    await fetchStats()
  } catch (error) {
    ElMessage.error('获取企业列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    // 获取全部统计数据
    const [totalRes, pendingRes, approvedRes, rejectedRes] = await Promise.all([
      getCompanyPageApi({ current: 1, size: 1 }),
      getCompanyPageApi({ current: 1, size: 1, verifyStatus: 'pending' }),
      getCompanyPageApi({ current: 1, size: 1, verifyStatus: 'approved' }),
      getCompanyPageApi({ current: 1, size: 1, verifyStatus: 'rejected' })
    ])

    stats.total = totalRes.total || 0
    stats.pending = pendingRes.total || 0
    stats.approved = approvedRes.total || 0
    stats.rejected = rejectedRes.total || 0
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

// 搜索
const handleSearch = () => {
  queryParams.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  queryParams.keyword = ''
  queryParams.verifyStatus = ''
  queryParams.status = undefined
  queryParams.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    companyName: '',
    creditCode: '',
    shortName: '',
    logo: '',
    industry: '',
    scale: '',
    stage: '',
    city: '',
    address: '',
    description: '',
    benefits: '',
    website: '',
    contactPerson: '',
    contactPosition: '',
    contactPhone: '',
    contactEmail: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Company) => {
  // 深拷贝数据,避免响应式问题
  Object.assign(formData, {
    id: row.id,
    companyName: row.companyName || '',
    creditCode: row.creditCode || '',
    shortName: row.shortName || '',
    logo: row.logo || '',
    industry: row.industry || '',
    scale: row.scale || '',
    stage: row.stage || '',
    city: row.city || '',
    address: row.address || '',
    description: row.description || '',
    benefits: row.benefits || '',
    website: row.website || '',
    contactPerson: row.contactPerson || '',
    contactPosition: row.contactPosition || '',
    contactPhone: row.contactPhone || '',
    contactEmail: row.contactEmail || '',
    verifyStatus: row.verifyStatus,
    verifyTime: row.verifyTime,
    rejectReason: row.rejectReason,
    status: row.status ?? 1
  })
  dialogVisible.value = true
}

// 查看
const handleView = (row: Company) => {
  currentCompany.value = row
  viewDialogVisible.value = true
}

// 删除
const handleDelete = (row: Company) => {
  ElMessageBox.confirm(`确定要删除企业"${row.companyName}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCompanyApi(row.id!)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 审核
const handleVerify = (row: Company) => {
  currentCompany.value = row
  verifyForm.verifyStatus = 'approved'
  verifyForm.rejectReason = ''
  verifyDialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formData.id) {
          await updateCompanyApi(formData)
          ElMessage.success('更新成功')
        } else {
          await createCompanyApi(formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(formData.id ? '更新失败' : '新增失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 提交审核
const handleVerifySubmit = async () => {
  if (!verifyFormRef.value) return

  await verifyFormRef.value.validate(async (valid) => {
    if (valid) {
      verifyLoading.value = true
      try {
        await verifyCompanyApi(currentCompany.value.id!, verifyForm)
        ElMessage.success('审核成功')
        verifyDialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('审核失败')
      } finally {
        verifyLoading.value = false
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 辅助函数
const getVerifyStatusLabel = (status?: string) => {
  const map = {
    pending: '待认证',
    approved: '已认证',
    rejected: '已拒绝'
  }
  return map[status as keyof typeof map] || '-'
}

const getVerifyStatusType = (status?: string): 'success' | 'warning' | 'danger' | 'info' | 'primary' => {
  const map: Record<string, 'success' | 'warning' | 'danger' | 'info' | 'primary'> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status || ''] || 'info'
}

const getScaleLabel = (scale?: string) => {
  return scale || '-'
}

const getStageLabel = (stage?: string) => {
  const map = {
    startup: '初创期',
    growth: '成长期',
    maturity: '成熟期',
    listing: '上市'
  }
  return map[stage as keyof typeof map] || '-'
}

const getStageType = (stage?: string): 'success' | 'warning' | 'danger' | 'info' | 'primary' => {
  const map: Record<string, 'success' | 'warning' | 'danger' | 'info' | 'primary'> = {
    startup: 'info',
    growth: 'warning',
    maturity: 'success',
    listing: 'danger'
  }
  return map[stage || ''] || 'info'
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.company-management {
  padding: 20px;

  .stats-row {
    margin-bottom: 20px;
  }

  .stat-card {
    border-radius: 8px;
    border: none;

    :deep(.el-card__body) {
      padding: 20px;
    }
  }

  .stat-content {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }

  .stat-info {
    flex: 1;
  }

  .stat-value {
    font-size: 32px;
    font-weight: bold;
    line-height: 1.2;
    color: #303133;
  }

  .stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
  }

  .stat-card-primary {
    background: #f1f5f9;

    .stat-value {
      color: #0f766e;
    }

    .stat-label {
      color: #64748b;
    }

    .stat-icon {
      background: #ccfbf1;
      color: #0f766e;
    }
  }

  .stat-card-warning {
    background: #fffbeb;

    .stat-value {
      color: #b45309;
    }

    .stat-label {
      color: #92400e;
    }

    .stat-icon {
      background: #fef3c7;
      color: #b45309;
    }
  }

  .stat-card-success {
    background: #f0fdf4;

    .stat-value {
      color: #15803d;
    }

    .stat-label {
      color: #166534;
    }

    .stat-icon {
      background: #dcfce7;
      color: #15803d;
    }
  }

  .stat-card-danger {
    background: #fef2f2;

    .stat-value {
      color: #b91c1c;
    }

    .stat-label {
      color: #991b1b;
    }

    .stat-icon {
      background: #fee2e2;
      color: #b91c1c;
    }
  }

  .search-card {
    margin-bottom: 20px;

    .search-form {
      :deep(.el-form-item) {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
      }
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .description-text {
    white-space: pre-wrap;
    word-break: break-word;
    line-height: 1.6;
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }
}
</style>
