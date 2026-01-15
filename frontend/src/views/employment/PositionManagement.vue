<template>
  <div class="position-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>岗位管理</h2>
      <p class="description">管理所有招聘职位信息</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <!-- 新增岗位按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增岗位
          </el-button>

          <!-- 批量操作 -->
          <el-button
            type="success"
            :icon="VideoPlay"
            :disabled="selectedIds.length === 0"
            @click="handleBatchPublish"
          >
            批量发布
          </el-button>
          <el-button
            type="warning"
            :icon="VideoPause"
            :disabled="selectedIds.length === 0"
            @click="handleBatchPause"
          >
            批量暂停
          </el-button>
          <el-button
            type="info"
            :icon="CircleClose"
            :disabled="selectedIds.length === 0"
            @click="handleBatchClose"
          >
            批量关闭
          </el-button>
        </div>

        <div class="toolbar-right">
          <!-- 搜索框 -->
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索职位名称"
            clearable
            style="width: 300px"
            :prefix-icon="Search"
            @clear="loadPositions"
            @keyup.enter="loadPositions"
          />

          <!-- 刷新 -->
          <el-button :icon="Refresh" @click="loadPositions">刷新</el-button>
        </div>
      </div>

      <!-- 高级筛选 -->
      <div class="filter-section">
        <el-form :inline="true" class="filter-form">
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

          <el-form-item label="学历要求">
            <el-select
              v-model="queryParams.education"
              placeholder="选择学历"
              clearable
              style="width: 150px"
              @change="loadPositions"
            >
              <el-option label="大专" value="junior_college" />
              <el-option label="本科" value="bachelor" />
              <el-option label="硕士" value="master" />
              <el-option label="博士" value="doctor" />
              <el-option label="不限" value="unlimited" />
            </el-select>
          </el-form-item>

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
              <el-option label="暂停" value="paused" />
              <el-option label="已关闭" value="closed" />
            </el-select>
          </el-form-item>

          <el-form-item label="薪资范围">
            <el-input-number
              v-model="queryParams.salaryMin"
              placeholder="最低"
              :min="0"
              :precision="0"
              style="width: 120px"
              controls-position="right"
            />
            <span style="margin: 0 8px">-</span>
            <el-input-number
              v-model="queryParams.salaryMax"
              placeholder="最高"
              :min="0"
              :precision="0"
              style="width: 120px"
              controls-position="right"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="loadPositions">查询</el-button>
            <el-button @click="handleResetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 岗位列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="positionList"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <!-- 选择列 -->
        <el-table-column type="selection" width="55" align="center" />

        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <!-- 职位信息列 -->
        <el-table-column label="职位信息" min-width="200">
          <template #default="{ row }">
            <div class="position-info">
              <div class="position-name">{{ row.positionName }}</div>
              <div class="company-name">{{ row.companyName || '未分配企业' }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 职位类型列 -->
        <el-table-column label="职位类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getPositionTypeTagType(row.positionType)">
              {{ getPositionTypeLabel(row.positionType) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 工作城市列 -->
        <el-table-column prop="city" label="工作城市" width="120" align="center" />

        <!-- 薪资范围列 -->
        <el-table-column label="薪资范围" width="150" align="center">
          <template #default="{ row }">
            <span class="salary-text">
              {{ formatSalary(row.salaryMin, row.salaryMax, row.salaryUnit) }}
            </span>
          </template>
        </el-table-column>

        <!-- 学历要求列 -->
        <el-table-column label="学历要求" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">
              {{ getEducationLabel(row.education) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 招聘进度列 -->
        <el-table-column label="招聘进度" width="150" align="center">
          <template #default="{ row }">
            <div class="recruit-progress">
              <el-progress
                :percentage="getRecruitPercentage(row.recruitCount, row.hiredCount)"
                :stroke-width="8"
                :color="getProgressColor(row.recruitCount, row.hiredCount)"
              />
              <div class="progress-text">
                {{ row.hiredCount || 0 }} / {{ row.recruitCount || 0 }}
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 投递数列 -->
        <el-table-column label="投递数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="warning" effect="plain">
              {{ row.applicationCount || 0 }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 状态列 -->
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 发布时间列 -->
        <el-table-column prop="publishTime" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.publishTime) }}
          </template>
        </el-table-column>

        <!-- 截止日期列 -->
        <el-table-column prop="deadline" label="截止日期" width="120">
          <template #default="{ row }">
            {{ row.deadline || '-' }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="350" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              :icon="View"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>

            <!-- 状态操作按钮 -->
            <el-button
              v-if="row.status === 'draft' || row.status === 'paused'"
              type="success"
              size="small"
              link
              :icon="VideoPlay"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.status === 'active'"
              type="warning"
              size="small"
              link
              :icon="VideoPause"
              @click="handlePause(row)"
            >
              暂停
            </el-button>
            <el-button
              v-if="row.status === 'active' || row.status === 'paused'"
              type="info"
              size="small"
              link
              :icon="CircleClose"
              @click="handleClose(row)"
            >
              关闭
            </el-button>

            <el-button
              type="danger"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 岗位表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位名称" prop="positionName">
              <el-input
                v-model="formData.positionName"
                placeholder="请输入职位名称"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="职位类型" prop="positionType">
              <el-select
                v-model="formData.positionType"
                placeholder="选择职位类型"
                style="width: 100%"
              >
                <el-option label="全职" value="fulltime" />
                <el-option label="兼职" value="parttime" />
                <el-option label="实习" value="internship" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="工作城市" prop="city">
              <el-input
                v-model="formData.city"
                placeholder="请输入工作城市"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="学历要求" prop="education">
              <el-select
                v-model="formData.education"
                placeholder="选择学历要求"
                style="width: 100%"
              >
                <el-option label="大专" value="junior_college" />
                <el-option label="本科" value="bachelor" />
                <el-option label="硕士" value="master" />
                <el-option label="博士" value="doctor" />
                <el-option label="不限" value="unlimited" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="最低薪资" prop="salaryMin">
              <el-input-number
                v-model="formData.salaryMin"
                :min="0"
                :precision="0"
                style="width: 100%"
                controls-position="right"
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="最高薪资" prop="salaryMax">
              <el-input-number
                v-model="formData.salaryMax"
                :min="0"
                :precision="0"
                style="width: 100%"
                controls-position="right"
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="薪资单位" prop="salaryUnit">
              <el-select
                v-model="formData.salaryUnit"
                placeholder="选择单位"
                style="width: 100%"
              >
                <el-option label="按月" value="month" />
                <el-option label="按年" value="year" />
                <el-option label="按天" value="day" />
                <el-option label="按时" value="hour" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="工作年限要求" prop="experience">
              <el-input
                v-model="formData.experience"
                placeholder="例如:1-3年、3-5年"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="招聘人数" prop="recruitCount">
              <el-input-number
                v-model="formData.recruitCount"
                :min="1"
                :max="999"
                style="width: 100%"
                controls-position="right"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="截止日期" prop="deadline">
              <el-date-picker
                v-model="formData.deadline"
                type="date"
                placeholder="选择截止日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="职位状态" prop="status">
              <el-select
                v-model="formData.status"
                placeholder="选择状态"
                style="width: 100%"
              >
                <el-option label="草稿" value="draft" />
                <el-option label="招聘中" value="active" />
                <el-option label="暂停" value="paused" />
                <el-option label="已关闭" value="closed" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="职位描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入职位描述"
          />
        </el-form-item>

        <el-form-item label="职位要求" prop="requirements">
          <el-input
            v-model="formData.requirements"
            type="textarea"
            :rows="4"
            placeholder="请输入职位要求"
          />
        </el-form-item>

        <el-form-item label="技术栈" prop="techStack">
          <el-input
            v-model="formData.techStack"
            type="textarea"
            :rows="2"
            placeholder="请输入技术栈,多个用逗号分隔,例如:Java,Spring,MySQL"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="岗位详情"
      width="900px"
    >
      <el-descriptions :column="2" border v-if="currentPosition">
        <el-descriptions-item label="职位名称">
          {{ currentPosition.positionName }}
        </el-descriptions-item>
        <el-descriptions-item label="职位类型">
          <el-tag :type="getPositionTypeTagType(currentPosition.positionType)">
            {{ getPositionTypeLabel(currentPosition.positionType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属企业">
          {{ currentPosition.companyName || '未分配' }}
        </el-descriptions-item>
        <el-descriptions-item label="工作城市">
          {{ currentPosition.city }}
        </el-descriptions-item>
        <el-descriptions-item label="薪资范围">
          {{ formatSalary(currentPosition.salaryMin, currentPosition.salaryMax, currentPosition.salaryUnit) }}
        </el-descriptions-item>
        <el-descriptions-item label="学历要求">
          {{ getEducationLabel(currentPosition.education) }}
        </el-descriptions-item>
        <el-descriptions-item label="工作年限">
          {{ currentPosition.experience || '不限' }}
        </el-descriptions-item>
        <el-descriptions-item label="招聘人数">
          {{ currentPosition.recruitCount }} 人
        </el-descriptions-item>
        <el-descriptions-item label="已招人数">
          {{ currentPosition.hiredCount || 0 }} 人
        </el-descriptions-item>
        <el-descriptions-item label="投递数量">
          {{ currentPosition.applicationCount || 0 }} 份
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentPosition.status)">
            {{ getStatusLabel(currentPosition.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">
          {{ formatDate(currentPosition.publishTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="截止日期">
          {{ currentPosition.deadline || '不限' }}
        </el-descriptions-item>
        <el-descriptions-item label="技术栈" :span="2">
          <el-tag
            v-for="(tech, index) in parseTechStack(currentPosition.techStack)"
            :key="index"
            style="margin-right: 8px; margin-bottom: 8px"
            size="small"
          >
            {{ tech }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="职位描述" :span="2">
          <div class="description-content">{{ currentPosition.description || '暂无描述' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="职位要求" :span="2">
          <div class="description-content">{{ currentPosition.requirements || '暂无要求' }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Delete,
  Edit,
  Search,
  Refresh,
  View,
  VideoPlay,
  VideoPause,
  CircleClose,
} from '@element-plus/icons-vue'
import {
  getPositionPageApi,
  createPositionApi,
  updatePositionApi,
  deletePositionApi,
  publishPositionApi,
  pausePositionApi,
  closePositionApi,
} from '@/api/recruitment'
import type { Position } from '@/api/types'

// 响应式数据
const loading = ref(false)
const positionList = ref<Position[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 查询参数
const queryParams = reactive<{
  current: number
  size: number
  keyword?: string
  positionType?: string
  city?: string
  education?: string
  status?: string
  salaryMin?: number
  salaryMax?: number
}>({
  current: 1,
  size: 10,
})

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑岗位' : '新增岗位'))
const isEdit = computed(() => !!formData.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 查看详情对话框
const viewDialogVisible = ref(false)
const currentPosition = ref<Position | null>(null)

// 表单数据
const formData = reactive<Partial<Position>>({
  id: undefined,
  positionName: '',
  positionType: 'fulltime',
  city: '',
  salaryMin: undefined,
  salaryMax: undefined,
  salaryUnit: 'month',
  education: 'unlimited',
  experience: '',
  description: '',
  requirements: '',
  techStack: '',
  recruitCount: 1,
  status: 'draft',
  deadline: undefined,
})

// 表单验证规则
const formRules: FormRules = {
  positionName: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '职位名称长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  positionType: [
    { required: true, message: '请选择职位类型', trigger: 'change' },
  ],
  city: [
    { required: true, message: '请输入工作城市', trigger: 'blur' },
  ],
  education: [
    { required: true, message: '请选择学历要求', trigger: 'change' },
  ],
  recruitCount: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' },
    { type: 'number', min: 1, message: '招聘人数至少为1', trigger: 'blur' },
  ],
}

// 加载岗位列表
const loadPositions = async () => {
  loading.value = true
  try {
    const response = await getPositionPageApi(queryParams)
    // 响应拦截器已经返回了 data，所以 response 就是 IPage<Position>
    positionList.value = response.records || []
    total.value = response.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载岗位列表失败')
  } finally {
    loading.value = false
  }
}

// 选择变化
const handleSelectionChange = (selection: Position[]) => {
  selectedIds.value = selection.map((item) => item.id!).filter(id => id !== undefined)
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  queryParams.size = val
  queryParams.current = 1
  loadPositions()
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  queryParams.current = val
  loadPositions()
}

// 重置筛选
const handleResetFilters = () => {
  queryParams.keyword = undefined
  queryParams.positionType = undefined
  queryParams.city = undefined
  queryParams.education = undefined
  queryParams.status = undefined
  queryParams.salaryMin = undefined
  queryParams.salaryMax = undefined
  queryParams.current = 1
  loadPositions()
}

// 新增岗位
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑岗位
const handleEdit = (row: Position) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = (row: Position) => {
  currentPosition.value = row
  viewDialogVisible.value = true
}

// 删除岗位
const handleDelete = async (row: Position) => {
  try {
    await ElMessageBox.confirm(`确定要删除岗位 "${row.positionName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await deletePositionApi(row.id!)
    ElMessage.success('删除成功')
    loadPositions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 发布岗位
const handlePublish = async (row: Position) => {
  try {
    await publishPositionApi(row.id!)
    ElMessage.success('发布成功')
    loadPositions()
  } catch (error: any) {
    ElMessage.error(error.message || '发布失败')
  }
}

// 暂停岗位
const handlePause = async (row: Position) => {
  try {
    await pausePositionApi(row.id!)
    ElMessage.success('暂停成功')
    loadPositions()
  } catch (error: any) {
    ElMessage.error(error.message || '暂停失败')
  }
}

// 关闭岗位
const handleClose = async (row: Position) => {
  try {
    await closePositionApi(row.id!)
    ElMessage.success('关闭成功')
    loadPositions()
  } catch (error: any) {
    ElMessage.error(error.message || '关闭失败')
  }
}

// 批量发布
const handleBatchPublish = async () => {
  try {
    await ElMessageBox.confirm(`确定要发布选中的 ${selectedIds.value.length} 个岗位吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const promises = selectedIds.value.map(id => publishPositionApi(id))
    await Promise.all(promises)
    ElMessage.success('批量发布成功')
    selectedIds.value = []
    loadPositions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量发布失败')
    }
  }
}

// 批量暂停
const handleBatchPause = async () => {
  try {
    await ElMessageBox.confirm(`确定要暂停选中的 ${selectedIds.value.length} 个岗位吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const promises = selectedIds.value.map(id => pausePositionApi(id))
    await Promise.all(promises)
    ElMessage.success('批量暂停成功')
    selectedIds.value = []
    loadPositions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量暂停失败')
    }
  }
}

// 批量关闭
const handleBatchClose = async () => {
  try {
    await ElMessageBox.confirm(`确定要关闭选中的 ${selectedIds.value.length} 个岗位吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const promises = selectedIds.value.map(id => closePositionApi(id))
    await Promise.all(promises)
    ElMessage.success('批量关闭成功')
    selectedIds.value = []
    loadPositions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量关闭失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await updatePositionApi(formData as Position)
    } else {
      await createPositionApi(formData as Position)
    }

    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadPositions()
  } catch (error: any) {
    if (error !== false) {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: undefined,
    positionName: '',
    positionType: 'fulltime',
    city: '',
    salaryMin: undefined,
    salaryMax: undefined,
    salaryUnit: 'month',
    education: 'unlimited',
    experience: '',
    description: '',
    requirements: '',
    techStack: '',
    recruitCount: 1,
    status: 'draft',
    deadline: undefined,
  })
}

// 获取职位类型标签类型
const getPositionTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    fulltime: 'primary',
    parttime: 'success',
    internship: 'warning',
  }
  return typeMap[type] || 'info'
}

// 获取职位类型标签
const getPositionTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    fulltime: '全职',
    parttime: '兼职',
    internship: '实习',
  }
  return labelMap[type] || type
}

// 获取学历标签
const getEducationLabel = (education: string) => {
  const labelMap: Record<string, string> = {
    junior_college: '大专',
    bachelor: '本科',
    master: '硕士',
    doctor: '博士',
    unlimited: '不限',
  }
  return labelMap[education] || education
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    draft: 'info',
    active: 'success',
    paused: 'warning',
    closed: 'danger',
  }
  return typeMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const labelMap: Record<string, string> = {
    draft: '草稿',
    active: '招聘中',
    paused: '暂停',
    closed: '已关闭',
  }
  return labelMap[status] || status
}

// 格式化薪资
const formatSalary = (min?: number, max?: number, unit?: string) => {
  if (!min && !max) return '面议'

  const unitLabelMap: Record<string, string> = {
    month: '元/月',
    year: '元/年',
    day: '元/天',
    hour: '元/小时',
  }

  const unitLabel = unitLabelMap[unit || 'month'] || '元/月'

  if (min && max) {
    return `${min} - ${max} ${unitLabel}`
  } else if (min) {
    return `${min}+ ${unitLabel}`
  } else if (max) {
    return `最高 ${max} ${unitLabel}`
  }

  return '面议'
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 获取招聘进度百分比
const getRecruitPercentage = (total?: number, current?: number) => {
  if (!total || total === 0) return 0
  const percentage = ((current || 0) / total) * 100
  return Math.min(percentage, 100)
}

// 获取进度条颜色
const getProgressColor = (total?: number, current?: number) => {
  const percentage = getRecruitPercentage(total, current)
  if (percentage >= 100) return '#67c23a'
  if (percentage >= 80) return '#e6a23c'
  if (percentage >= 50) return '#409eff'
  return '#f56c6c'
}

// 解析技术栈
const parseTechStack = (techStack?: string) => {
  if (!techStack) return []
  try {
    // 尝试解析JSON数组
    const parsed = JSON.parse(techStack)
    if (Array.isArray(parsed)) {
      return parsed
    }
  } catch {
    // 如果解析失败,按逗号分隔
    return techStack.split(/,|，/).filter(t => t.trim())
  }
  return []
}

// 组件挂载
onMounted(() => {
  loadPositions()
})
</script>

<style lang="scss" scoped>
.position-management {
  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0 0 8px 0;
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    .description {
      margin: 0;
      font-size: 14px;
      color: #909399;
    }
  }

  .toolbar-card {
    margin-bottom: 20px;

    .toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .toolbar-left,
      .toolbar-right {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }

    .filter-section {
      border-top: 1px solid #ebeef5;
      padding-top: 20px;

      .filter-form {
        .el-form-item {
          margin-bottom: 12px;
        }
      }
    }
  }

  .table-card {
    .position-info {
      .position-name {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }

      .company-name {
        font-size: 12px;
        color: #909399;
      }
    }

    .salary-text {
      color: #f56c6c;
      font-weight: 600;
    }

    .recruit-progress {
      .progress-text {
        font-size: 12px;
        color: #909399;
        text-align: center;
        margin-top: 4px;
      }
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .description-content {
    white-space: pre-wrap;
    word-break: break-word;
    line-height: 1.6;
  }
}
</style>
