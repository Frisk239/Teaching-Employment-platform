<template>
  <div class="classroom-list">
    <el-card shadow="never" class="search-card">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="教室编号/教室名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="所属学校">
          <el-select
            v-model="searchForm.schoolId"
            placeholder="请选择学校"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="school in schoolList"
              :key="school.id"
              :label="school.schoolName"
              :value="school.id"
            />
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
          <span class="title">教室列表</span>
          <div class="operations">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增教室
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
        <el-table-column prop="classroomNo" label="教室编号" width="120" />
        <el-table-column prop="classroomName" label="教室名称" min-width="150" />
        <el-table-column prop="schoolName" label="所属学校" width="180" />
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="capacity" label="容纳人数" width="100" align="center">
          <template #default="{ row }">
            {{ row.capacity || 0 }} 人
          </template>
        </el-table-column>
        <el-table-column prop="classroomType" label="教室类型" width="120" />
        <el-table-column label="设备" width="200" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.hasProjector === 1" type="success" size="small">投影仪</el-tag>
            <el-tag v-if="row.hasComputer === 1" type="info" size="small">电脑</el-tag>
            <el-tag v-if="row.hasMultimedia === 1" type="warning" size="small">多媒体</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '可用' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
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
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="classroom-form"
      >
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教室编号" prop="classroomNo">
              <el-input v-model="formData.classroomNo" placeholder="如: A101" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教室名称" prop="classroomName">
              <el-input v-model="formData.classroomName" placeholder="请输入教室名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学校" prop="schoolId">
              <el-select v-model="formData.schoolId" placeholder="请选择学校" style="width: 100%">
                <el-option
                  v-for="school in schoolList"
                  :key="school.id"
                  :label="school.schoolName"
                  :value="school.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教室类型" prop="classroomType">
              <el-select v-model="formData.classroomType" placeholder="请选择类型" style="width: 100%">
                <el-option label="普通教室" value="普通教室" />
                <el-option label="多媒体教室" value="多媒体教室" />
                <el-option label="实验室" value="实验室" />
                <el-option label="计算机教室" value="计算机教室" />
                <el-option label="语音教室" value="语音教室" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在楼栋" prop="building">
              <el-input v-model="formData.building" placeholder="如: A栋" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼层" prop="floor">
              <el-input v-model="formData.floor" placeholder="如: 1层" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="容纳人数" prop="capacity">
              <el-input-number
                v-model="formData.capacity"
                :min="1"
                :max="500"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">设备信息</el-divider>
        <el-form-item label="设备配置">
          <el-checkbox v-model="formData.hasProjector" :true-label="1" :false-label="0">
            投影仪
          </el-checkbox>
          <el-checkbox v-model="formData.hasComputer" :true-label="1" :false-label="0" style="margin-left: 20px">
            电脑
          </el-checkbox>
          <el-checkbox v-model="formData.hasMultimedia" :true-label="1" :false-label="0" style="margin-left: 20px">
            多媒体设备
          </el-checkbox>
        </el-form-item>

        <el-form-item label="设备描述" prop="equipment">
          <el-input
            v-model="formData.equipment"
            type="textarea"
            :rows="3"
            placeholder="请详细描述教室设备,如: 投影仪品牌、电脑配置等"
          />
        </el-form-item>

        <el-divider content-position="left">状态</el-divider>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">可用</el-radio>
            <el-radio :label="0">不可用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="教室详情" width="600px">
      <el-descriptions v-if="viewData" :column="2" border>
        <el-descriptions-item label="教室编号">{{ viewData.classroomNo }}</el-descriptions-item>
        <el-descriptions-item label="教室名称">{{ viewData.classroomName }}</el-descriptions-item>
        <el-descriptions-item label="所属学校">{{ viewData.schoolName }}</el-descriptions-item>
        <el-descriptions-item label="教室类型">{{ viewData.classroomType }}</el-descriptions-item>
        <el-descriptions-item label="所在楼栋">{{ viewData.building }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ viewData.floor }}</el-descriptions-item>
        <el-descriptions-item label="容纳人数">{{ viewData.capacity || 0 }} 人</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '可用' : '不可用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投影仪">
          <el-tag :type="viewData.hasProjector === 1 ? 'success' : 'info'" size="small">
            {{ viewData.hasProjector === 1 ? '有' : '无' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="电脑">
          <el-tag :type="viewData.hasComputer === 1 ? 'success' : 'info'" size="small">
            {{ viewData.hasComputer === 1 ? '有' : '无' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="多媒体">
          <el-tag :type="viewData.hasMultimedia === 1 ? 'success' : 'info'" size="small">
            {{ viewData.hasMultimedia === 1 ? '有' : '无' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设备描述" :span="2">{{ viewData.equipment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, View, Edit, Delete } from '@element-plus/icons-vue'
import {
  getClassroomPageApi,
  getClassroomByIdApi,
  createClassroomApi,
  updateClassroomApi,
  deleteClassroomApi
} from '@/api/classroom'
import { getSchoolListApi } from '@/api/school'
import type { Classroom } from '@/api/classroom'
import type { School } from '@/api/school'
import { CLASSROOM_TYPES, CLASSROOM_STATUS } from '@/constants/classroom'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  schoolId: undefined as number | undefined
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<Classroom[]>([])

// 学校列表
const schoolList = ref<School[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑教室' : '新增教室'))
const formRef = ref<FormInstance>()
const formData = reactive<Partial<Classroom>>({
  id: undefined,
  classroomNo: '',
  classroomName: '',
  schoolId: undefined,
  building: '',
  floor: '',
  capacity: 50,
  classroomType: '普通教室',
  hasProjector: 0,
  hasComputer: 0,
  hasMultimedia: 0,
  equipment: '',
  status: 1
})

// 查看详情
const viewDialogVisible = ref(false)
const viewData = ref<Classroom | null>(null)

// 表单验证规则
const formRules: FormRules = {
  classroomNo: [
    { required: true, message: '请输入教室编号', trigger: 'blur' },
    { pattern: /^[A-Z0-9]+$/, message: '教室编号只能包含大写字母和数字', trigger: 'blur' }
  ],
  classroomName: [
    { required: true, message: '请输入教室名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }],
  building: [{ required: true, message: '请输入楼栋', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }],
  classroomType: [{ required: true, message: '请选择教室类型', trigger: 'change' }]
}

// 获取学校列表
const fetchSchoolList = async () => {
  try {
    const data = await getSchoolListApi()
    schoolList.value = data
  } catch (error) {
    console.error('获取学校列表失败:', error)
    ElMessage.error('获取学校列表失败')
  }
}

// 获取教室列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      schoolId: searchForm.schoolId,
      keyword: searchForm.keyword || undefined
    }
    const data = await getClassroomPageApi(params)
    tableData.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('获取教室列表失败:', error)
    ElMessage.error('获取教室列表失败')
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
  searchForm.schoolId = undefined
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    classroomNo: '',
    classroomName: '',
    schoolId: undefined,
    building: '',
    floor: '',
    capacity: 50,
    classroomType: '普通教室',
    hasProjector: 0,
    hasComputer: 0,
    hasMultimedia: 0,
    equipment: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: Classroom) => {
  try {
    const data = await getClassroomByIdApi(row.id!)
    Object.assign(formData, data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取教室信息失败:', error)
    ElMessage.error('获取教室信息失败')
  }
}

// 查看
const handleView = async (row: Classroom) => {
  try {
    const data = await getClassroomByIdApi(row.id!)
    viewData.value = data
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取教室信息失败:', error)
    ElMessage.error('获取教室信息失败')
  }
}

// 删除
const handleDelete = (row: Classroom) => {
  ElMessageBox.confirm('确定要删除该教室吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteClassroomApi(row.id!)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
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
        const api = formData.id ? updateClassroomApi : createClassroomApi
        await api(formData as Classroom)
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

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 页面加载时获取数据
onMounted(() => {
  fetchSchoolList()
  fetchData()
})
</script>

<style scoped lang="scss">
.classroom-list {
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

  .classroom-form {
    :deep(.el-divider) {
      margin: 20px 0;
    }
  }
}
</style>
