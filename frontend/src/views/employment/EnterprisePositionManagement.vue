<template>
  <div class="enterprise-position-management">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><Briefcase /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalPositions || 0 }}</div>
              <div class="stat-label">全部职位</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><VideoPlay /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activePositions || 0 }}</div>
              <div class="stat-label">招聘中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalApplications || 0 }}</div>
              <div class="stat-label">收到简历</div>
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
              <div class="stat-value">{{ stats.totalHired || 0 }}</div>
              <div class="stat-label">已录用</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <!-- 发布新职位按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            发布新职位
          </el-button>

          <!-- 批量操作 -->
          <el-dropdown split-button type="default" :disabled="selectedIds.length === 0" @click="handleBatchPublish">
            批量操作
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="VideoPlay" @click="handleBatchPublish">批量发布</el-dropdown-item>
                <el-dropdown-item :icon="VideoPause" @click="handleBatchPause">批量暂停</el-dropdown-item>
                <el-dropdown-item :icon="CircleClose" @click="handleBatchClose">批量关闭</el-dropdown-item>
                <el-dropdown-item :icon="Delete" divided @click="handleBatchDelete">批量删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="toolbar-right">
          <!-- 搜索框 -->
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索职位名称"
            clearable
            style="width: 260px"
            :prefix-icon="Search"
            @clear="loadPositions"
            @keyup.enter="loadPositions"
          />

          <!-- 刷新 -->
          <el-button :icon="Refresh" @click="loadPositions" />
        </div>
      </div>

      <!-- 高级筛选 -->
      <el-collapse-transition>
        <div v-show="showFilter" class="filter-section">
          <el-form :inline="true" class="filter-form">
            <el-form-item label="职位状态">
              <el-select
                v-model="queryParams.status"
                placeholder="选择状态"
                clearable
                style="width: 150px"
                @change="loadPositions"
              >
                <el-option label="草稿" value="draft" />
                <el-option label="招聘中" value="active" />
                <el-option label="已暂停" value="paused" />
                <el-option label="已关闭" value="closed" />
              </el-select>
            </el-form-item>

            <el-form-item label="职位类型">
              <el-select
                v-model="queryParams.positionType"
                placeholder="选择类型"
                clearable
                style="width: 150px"
                @change="loadPositions"
              >
                <el-option label="全职" value="fulltime" />
                <el-option label="兼职" value="parttime" />
                <el-option label="实习" value="internship" />
              </el-select>
            </el-form-item>

            <el-form-item label="工作城市">
              <el-input
                v-model="queryParams.city"
                placeholder="输入城市"
                clearable
                style="width: 150px"
                @keyup.enter="loadPositions"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :icon="Search" @click="loadPositions">查询</el-button>
              <el-button :icon="RefreshLeft" @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>

      <!-- 展开/收起筛选 -->
      <div class="filter-toggle">
        <el-button type="text" @click="showFilter = !showFilter">
          {{ showFilter ? '收起筛选' : '展开筛选' }}
          <el-icon class="el-icon--right">
            <component :is="showFilter ? ArrowUp : ArrowDown" />
          </el-icon>
        </el-button>
      </div>
    </el-card>

    <!-- 职位列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="positionList"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="positionName" label="职位名称" min-width="180">
          <template #default="{ row }">
            <div class="position-name-cell">
              <div class="position-name">{{ row.positionName }}</div>
              <el-tag v-if="row.status === 'draft'" type="info" size="small">草稿</el-tag>
              <el-tag v-else-if="row.status === 'active'" type="success" size="small">招聘中</el-tag>
              <el-tag v-else-if="row.status === 'paused'" type="warning" size="small">已暂停</el-tag>
              <el-tag v-else-if="row.status === 'closed'" type="danger" size="small">已关闭</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="职位类型" width="100">
          <template #default="{ row }">
            <span v-if="row.positionType === 'fulltime'">全职</span>
            <span v-else-if="row.positionType === 'parttime'">兼职</span>
            <span v-else-if="row.positionType === 'internship'">实习</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="city" label="工作地点" width="120" />

        <el-table-column label="薪资范围" width="150">
          <template #default="{ row }">
            <span v-if="row.salaryMin && row.salaryMax">
              {{ formatSalary(row.salaryMin, row.salaryMax, row.salaryUnit) }}
            </span>
            <span v-else class="text-muted">面议</span>
          </template>
        </el-table-column>

        <el-table-column label="招聘进度" width="150">
          <template #default="{ row }">
            <div class="progress-cell">
              <el-progress
                :percentage="getHirePercentage(row)"
                :color="getProgressColor(row)"
                :stroke-width="6"
              />
              <span class="progress-text">{{ row.hiredCount || 0 }}/{{ row.recruitCount || 0 }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="投递/已招" width="100" align="center">
          <template #default="{ row }">
            <span>{{ row.applicationCount || 0 }}/{{ row.hiredCount || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="发布时间" width="110">
          <template #default="{ row }">
            {{ row.publishTime ? formatDate(row.publishTime) : '-' }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button link type="primary" :icon="View" @click="handleView(row)">
                查看
              </el-button>
              <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-dropdown @command="(cmd) => handleAction(cmd, row)">
                <el-button link type="primary" :icon="More">
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-if="row.status === 'draft' || row.status === 'paused'"
                      command="publish"
                      :icon="VideoPlay"
                    >
                      发布
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="row.status === 'active'"
                      command="pause"
                      :icon="VideoPause"
                    >
                      暂停
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="row.status === 'active' || row.status === 'paused'"
                      command="close"
                      :icon="CircleClose"
                    >
                      关闭
                    </el-dropdown-item>
                    <el-dropdown-item command="stats" :icon="DataAnalysis" divided>
                      查看统计
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" :icon="Delete" style="color: #f56c6c">
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadPositions"
          @current-change="loadPositions"
        />
      </div>
    </el-card>

    <!-- 职位编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-tabs v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="职位名称" prop="positionName">
                  <el-input
                    v-model="formData.positionName"
                    placeholder="请输入职位名称"
                    maxlength="50"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职位类型" prop="positionType">
                  <el-select v-model="formData.positionType" placeholder="请选择">
                    <el-option label="全职" value="fulltime" />
                    <el-option label="兼职" value="parttime" />
                    <el-option label="实习" value="internship" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="工作城市" prop="city">
                  <el-input v-model="formData.city" placeholder="请输入工作城市" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="招聘人数" prop="recruitCount">
                  <el-input-number
                    v-model="formData.recruitCount"
                    :min="1"
                    :max="999"
                    placeholder="招聘人数"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="薪资范围">
              <el-row :gutter="10">
                <el-col :span="8">
                  <el-input-number
                    v-model="formData.salaryMin"
                    :min="0"
                    :precision="0"
                    placeholder="最低薪资"
                    style="width: 100%"
                  />
                </el-col>
                <el-col :span="2" style="text-align: center">至</el-col>
                <el-col :span="8">
                  <el-input-number
                    v-model="formData.salaryMax"
                    :min="0"
                    :precision="0"
                    placeholder="最高薪资"
                    style="width: 100%"
                  />
                </el-col>
                <el-col :span="6">
                  <el-select v-model="formData.salaryUnit" placeholder="单位">
                    <el-option label="元/月" value="month" />
                    <el-option label="元/年" value="year" />
                    <el-option label="元/天" value="day" />
                    <el-option label="元/时" value="hour" />
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="学历要求">
                  <el-select v-model="formData.education" placeholder="请选择">
                    <el-option label="不限" value="unlimited" />
                    <el-option label="大专" value="junior_college" />
                    <el-option label="本科" value="bachelor" />
                    <el-option label="硕士" value="master" />
                    <el-option label="博士" value="doctor" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="工作年限">
                  <el-select v-model="formData.experience" placeholder="请选择">
                    <el-option label="不限" value="unlimited" />
                    <el-option label="1年以下" value="under_1" />
                    <el-option label="1-3年" value="1_3" />
                    <el-option label="3-5年" value="3_5" />
                    <el-option label="5-10年" value="5_10" />
                    <el-option label="10年以上" value="over_10" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="截止日期">
              <el-date-picker
                v-model="formData.deadline"
                type="date"
                placeholder="选择截止日期"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
              />
            </el-form-item>
          </el-tab-pane>

          <!-- 职位详情 -->
          <el-tab-pane label="职位详情" name="detail">
            <el-form-item label="职位描述" prop="description">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="6"
                placeholder="请输入职位描述，包括岗位职责、工作内容等"
                maxlength="2000"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="任职要求" prop="requirements">
              <el-input
                v-model="formData.requirements"
                type="textarea"
                :rows="6"
                placeholder="请输入任职要求，包括学历、技能、经验等"
                maxlength="2000"
                show-word-limit
              />
            </el-form-item>
          </el-tab-pane>

          <!-- 其他信息 -->
          <el-tab-pane label="其他信息" name="other">
            <el-form-item label="技术栈">
              <el-input
                v-model="formData.techStack"
                type="textarea"
                :rows="3"
                placeholder="请输入技术栈要求，如：Java, Spring Boot, MySQL等，用逗号分隔"
              />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ formData.status === 'draft' ? '保存草稿' : '保存' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 职位详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="职位详情"
      width="700px"
    >
      <div v-if="currentPosition" class="position-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="职位名称" :span="2">
            {{ currentPosition.positionName }}
          </el-descriptions-item>
          <el-descriptions-item label="职位类型">
            <span v-if="currentPosition.positionType === 'fulltime'">全职</span>
            <span v-else-if="currentPosition.positionType === 'parttime'">兼职</span>
            <span v-else-if="currentPosition.positionType === 'internship'">实习</span>
          </el-descriptions-item>
          <el-descriptions-item label="工作地点">
            {{ currentPosition.city }}
          </el-descriptions-item>
          <el-descriptions-item label="薪资范围">
            {{ formatSalary(currentPosition.salaryMin, currentPosition.salaryMax, currentPosition.salaryUnit) }}
          </el-descriptions-item>
          <el-descriptions-item label="招聘人数">
            {{ currentPosition.recruitCount }}人
          </el-descriptions-item>
          <el-descriptions-item label="学历要求">
            {{ formatEducation(currentPosition.education) }}
          </el-descriptions-item>
          <el-descriptions-item label="工作年限">
            {{ formatExperience(currentPosition.experience) }}
          </el-descriptions-item>
          <el-descriptions-item label="招聘进度" :span="2">
            <el-progress
              :percentage="getHirePercentage(currentPosition)"
              :color="getProgressColor(currentPosition)"
            />
          </el-descriptions-item>
          <el-descriptions-item label="职位描述" :span="2">
            <div class="description-content">{{ currentPosition.description }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="任职要求" :span="2">
            <div class="description-content">{{ currentPosition.requirements }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 统计详情对话框 -->
    <el-dialog
      v-model="statsDialogVisible"
      title="职位统计"
      width="600px"
    >
      <div v-if="currentStats" class="position-stats">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-statistic title="投递简历数" :value="currentStats.applicationCount || 0">
              <template #prefix>
                <el-icon><Document /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="12">
            <el-statistic title="已录用人数" :value="currentStats.hiredCount || 0">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
        <el-divider />
        <el-row :gutter="20">
          <el-col :span="12">
            <el-statistic title="录用率" :value="getHireRate(currentStats)" suffix="%">
              <template #prefix>
                <el-icon><TrendCharts /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="12">
            <el-statistic title="招聘进度" :value="getHirePercentage(currentPosition)" suffix="%">
              <template #prefix>
                <el-icon><DataLine /></el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  RefreshLeft,
  VideoPlay,
  VideoPause,
  CircleClose,
  Edit,
  Delete,
  View,
  More,
  ArrowDown,
  ArrowUp,
  Briefcase,
  Document,
  User,
  DataAnalysis,
  TrendCharts,
  DataLine
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores'

const authStore = useAuthStore()
const companyId = computed(() => authStore.companyId)

interface Position {
  id?: number
  companyId?: number
  positionName: string
  positionType: string
  city: string
  salaryMin?: number
  salaryMax?: number
  salaryUnit?: string
  education?: string
  experience?: string
  description?: string
  requirements?: string
  techStack?: string
  recruitCount?: number
  hiredCount?: number
  applicationCount?: number
  status?: string
  publishTime?: string
  deadline?: string
}

// 状态管理
const loading = ref(false)
const submitting = ref(false)
const showFilter = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const statsDialogVisible = ref(false)
const activeTab = ref('basic')

// 数据列表
const positionList = ref<Position[]>([])
const selectedIds = ref<number[]>([])
const total = ref(0)
const currentPosition = ref<Position | null>(null)
const currentStats = ref<any>(null)

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: '',
  status: '',
  positionType: '',
  city: ''
})

// 统计数据
const stats = ref({
  totalPositions: 0,
  activePositions: 0,
  totalApplications: 0,
  totalHired: 0
})

// 表单数据
const formData = reactive<Position>({
  positionName: '',
  positionType: 'fulltime',
  city: '',
  salaryMin: undefined,
  salaryMax: undefined,
  salaryUnit: 'month',
  education: 'unlimited',
  experience: 'unlimited',
  description: '',
  requirements: '',
  techStack: '',
  recruitCount: 1,
  status: 'draft'
})

const formRef = ref<FormInstance>()
const dialogTitle = ref('新增职位')

// 表单验证规则
const formRules: FormRules = {
  positionName: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  positionType: [
    { required: true, message: '请选择职位类型', trigger: 'change' }
  ],
  city: [
    { required: true, message: '请输入工作城市', trigger: 'blur' }
  ],
  recruitCount: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入职位描述', trigger: 'blur' },
    { min: 10, max: 2000, message: '长度在 10 到 2000 个字符', trigger: 'blur' }
  ],
  requirements: [
    { required: true, message: '请输入任职要求', trigger: 'blur' },
    { min: 10, max: 2000, message: '长度在 10 到 2000 个字符', trigger: 'blur' }
  ]
}

// 加载职位列表
const loadPositions = async () => {
  loading.value = true
  try {
    // 添加 companyId 过滤 - 企业对接人只能看到自己企业的职位
    const params = {
      ...queryParams,
      companyId: companyId.value
    }
    const response: any = await request.get('/position/page', { params })
    positionList.value = response.records || []
    total.value = response.total || 0
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载职位列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 获取所有职位数据用于统计（不限制分页大小）
    const response: any = await request.get('/position/page', {
      params: {
        current: 1,
        size: 1000,  // 获取所有职位
        companyId: companyId.value  // 只统计本企业的职位
      }
    })

    const allPositions = response.records || []

    // 计算统计数据
    stats.value = {
      totalPositions: response.total || 0,  // 使用分页的总数
      activePositions: allPositions.filter(p => p.status === 'active').length,
      totalApplications: allPositions.reduce((sum: number, p: Position) => sum + (p.applicationCount || 0), 0),
      totalHired: allPositions.reduce((sum: number, p: Position) => sum + (p.hiredCount || 0), 0)
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
  }
}

// 重置筛选
const resetFilter = () => {
  queryParams.keyword = ''
  queryParams.status = ''
  queryParams.positionType = ''
  queryParams.city = ''
  queryParams.current = 1
  loadPositions()
}

// 处理选择变化
const handleSelectionChange = (selection: Position[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(id => id)
}

// 新增职位
const handleAdd = () => {
  dialogTitle.value = '新增职位'
  Object.assign(formData, {
    positionName: '',
    positionType: 'fulltime',
    city: '',
    salaryMin: undefined,
    salaryMax: undefined,
    salaryUnit: 'month',
    education: 'unlimited',
    experience: 'unlimited',
    description: '',
    requirements: '',
    techStack: '',
    recruitCount: 1,
    status: 'draft',
    companyId: companyId.value  // 添加当前登录企业的ID
  })
  dialogVisible.value = true
}

// 编辑职位
const handleEdit = (row: Position) => {
  dialogTitle.value = '编辑职位'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 查看职位详情
const handleView = (row: Position) => {
  currentPosition.value = row
  viewDialogVisible.value = true
}

// 处理操作
const handleAction = async (command: string, row: Position) => {
  switch (command) {
    case 'publish':
      await handlePublish(row.id!)
      break
    case 'pause':
      await handlePause(row.id!)
      break
    case 'close':
      await handleClose(row.id!)
      break
    case 'stats':
      await handleViewStats(row)
      break
    case 'delete':
      await handleDelete(row.id!)
      break
  }
}

// 发布职位
const handlePublish = async (id: number) => {
  try {
    await request.post(`/position/${id}/publish`)
    ElMessage.success('发布成功')
    await loadPositions()
    await loadStats()  // 更新统计
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '发布失败')
  }
}

// 暂停职位
const handlePause = async (id: number) => {
  try {
    await request.post(`/position/${id}/pause`)
    ElMessage.success('暂停成功')
    await loadPositions()
    await loadStats()  // 更新统计
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '暂停失败')
  }
}

// 关闭职位
const handleClose = async (id: number) => {
  try {
    await request.post(`/position/${id}/close`)
    ElMessage.success('关闭成功')
    await loadPositions()
    await loadStats()  // 更新统计
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '关闭失败')
  }
}

// 删除职位
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个职位吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/position/${id}`)
    ElMessage.success('删除成功')
    await loadPositions()
    await loadStats()  // 更新统计
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 批量发布
const handleBatchPublish = async () => {
  try {
    await ElMessageBox.confirm(`确定要发布选中的 ${selectedIds.value.length} 个职位吗？`, '批量发布', {
      type: 'warning'
    })
    // TODO: 调用批量发布API
    ElMessage.success('批量发布成功')
    await loadPositions()
    await loadStats()
  } catch (error) {
    // 用户取消
  }
}

// 批量暂停
const handleBatchPause = async () => {
  try {
    await ElMessageBox.confirm(`确定要暂停选中的 ${selectedIds.value.length} 个职位吗？`, '批量暂停', {
      type: 'warning'
    })
    // TODO: 调用批量暂停API
    ElMessage.success('批量暂停成功')
    await loadPositions()
    await loadStats()
  } catch (error) {
    // 用户取消
  }
}

// 批量关闭
const handleBatchClose = async () => {
  try {
    await ElMessageBox.confirm(`确定要关闭选中的 ${selectedIds.value.length} 个职位吗？`, '批量关闭', {
      type: 'warning'
    })
    // TODO: 调用批量关闭API
    ElMessage.success('批量关闭成功')
    await loadPositions()
    await loadStats()
  } catch (error) {
    // 用户取消
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个职位吗？此操作不可恢复！`, '批量删除', {
      type: 'error'
    })
    // TODO: 调用批量删除API
    ElMessage.success('批量删除成功')
    await loadPositions()
    await loadStats()
  } catch (error) {
    // 用户取消
  }
}

// 查看统计
const handleViewStats = async (row: Position) => {
  currentPosition.value = row
  try {
    const response: any = await request.get(`/position/${row.id}/stats`)
    currentStats.value = response
    statsDialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '获取统计数据失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // 确保 companyId 存在（新增时需要）
    const submitData = {
      ...formData,
      companyId: formData.companyId || companyId.value
    }

    if (formData.id) {
      // 更新
      await request.put('/position', submitData)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await request.post('/position', submitData)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    await loadPositions()
    await loadStats()  // 更新统计
  } catch (error: any) {
    if (error !== false) {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  activeTab.value = 'basic'
}

// 格式化薪资
const formatSalary = (min?: number, max?: number, unit?: string) => {
  if (!min && !max) return '面议'
  const unitText = unit === 'month' ? '元/月' : unit === 'year' ? '元/年' : unit === 'day' ? '元/天' : '元/时'
  if (min && max) return `${min}-${max} ${unitText}`
  if (min) return `${min}+ ${unitText}`
  if (max) return `${max} ${unitText}`
  return '面议'
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  return date.toLocaleDateString('zh-CN')
}

// 格式化学历
const formatEducation = (education?: string) => {
  const map: Record<string, string> = {
    unlimited: '不限',
    junior_college: '大专',
    bachelor: '本科',
    master: '硕士',
    doctor: '博士'
  }
  return map[education || ''] || '-'
}

// 格式化经验
const formatExperience = (experience?: string) => {
  const map: Record<string, string> = {
    unlimited: '不限',
    under_1: '1年以下',
    '1_3': '1-3年',
    '3_5': '3-5年',
    '5_10': '5-10年',
    over_10: '10年以上'
  }
  return map[experience || ''] || '-'
}

// 获取招聘进度百分比
const getHirePercentage = (position: Position) => {
  if (!position.recruitCount || position.recruitCount === 0) return 0
  const percentage = Math.round(((position.hiredCount || 0) / position.recruitCount) * 100)
  return Math.min(percentage, 100)
}

// 获取进度条颜色
const getProgressColor = (position: Position) => {
  const percentage = getHirePercentage(position)
  if (percentage >= 100) return '#67c23a'
  if (percentage >= 80) return '#409eff'
  if (percentage >= 50) return '#e6a23c'
  return '#f56c6c'
}

// 获取录用率
const getHireRate = (stats: any) => {
  if (!stats || !stats.applicationCount || stats.applicationCount === 0) return 0
  return ((stats.hiredCount || 0) / stats.applicationCount * 100).toFixed(1)
}

// 禁用日期
const disabledDate = (date: Date) => {
  return date.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 初始化
onMounted(async () => {
  await loadPositions()  // 等待职位列表加载完成
  await loadStats()      // 然后再加载统计数据
})
</script>

<style lang="scss" scoped>
.enterprise-position-management {
  padding: 20px;

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 15px;

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          flex: 1;

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
      margin-bottom: 16px;

      .toolbar-left,
      .toolbar-right {
        display: flex;
        gap: 10px;
        align-items: center;
      }
    }

    .filter-section {
      padding-top: 16px;
      border-top: 1px solid #ebeef5;

      .filter-form {
        margin-bottom: 0;

        :deep(.el-form-item) {
          margin-bottom: 12px;
        }
      }
    }

    .filter-toggle {
      text-align: center;
      padding-top: 8px;
      border-top: 1px solid #ebeef5;
    }
  }

  .table-card {
    .position-name-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .position-name {
        font-weight: 500;
        color: #303133;
      }
    }

    .progress-cell {
      .progress-text {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
        display: block;
      }
    }

    .text-muted {
      color: #909399;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .position-detail {
    .description-content {
      white-space: pre-wrap;
      line-height: 1.6;
      color: #606266;
    }
  }

  .position-stats {
    padding: 20px 0;
  }
}
</style>
