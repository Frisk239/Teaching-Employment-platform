<template>
  <div class="school-list">
    <el-card shadow="never" class="search-card">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="学校名称">
          <el-input
            v-model="searchForm.schoolName"
            placeholder="请输入学校名称"
            clearable
            style="width: 200px"
          />
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
          <span class="title">学校列表</span>
          <div class="operations">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增学校
            </el-button>
            <el-button @click="handleExport">
              <el-icon><Download /></el-icon>
              导出
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
        <el-table-column prop="schoolName" label="学校名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="schoolCode" label="学校代码" width="120" />
        <el-table-column prop="address" label="详细地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
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
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="school-form"
      >
        <el-form-item label="学校名称" prop="schoolName">
          <el-input v-model="formData.schoolName" placeholder="请输入学校名称" />
        </el-form-item>

        <el-form-item label="学校代码" prop="schoolCode">
          <el-input v-model="formData.schoolCode" placeholder="请输入学校代码" />
        </el-form-item>

        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
        </el-form-item>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input
            v-model="formData.contactPhone"
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="学校简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入学校简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="学校详情" width="600px">
      <el-descriptions v-if="viewData" :column="2" border>
        <el-descriptions-item label="学校名称">{{ viewData.schoolName }}</el-descriptions-item>
        <el-descriptions-item label="学校代码">{{ viewData.schoolCode }}</el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">{{ viewData.address }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ viewData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ viewData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="学校简介" :span="2">{{ viewData.description || '-' }}</el-descriptions-item>
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
import {
  Search,
  Refresh,
  Plus,
  Download,
  View,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import {
  getSchoolPageApi,
  getSchoolByIdApi,
  createSchoolApi,
  updateSchoolApi,
  deleteSchoolApi,
  exportSchoolsApi
} from '@/api/school'
import type { School } from '@/api/school'

// 搜索表单
const searchForm = reactive({
  schoolName: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<School[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑学校' : '新增学校'))
const formRef = ref<FormInstance>()
const formData = reactive<Partial<School>>({
  id: undefined,
  schoolName: '',
  schoolCode: '',
  address: '',
  contactPerson: '',
  contactPhone: '',
  description: '',
  status: 1
})

// 查看详情
const viewDialogVisible = ref(false)
const viewData = ref<School | null>(null)

// 表单验证规则
const formRules: FormRules = {
  schoolName: [
    { required: true, message: '请输入学校名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  schoolCode: [
    { required: true, message: '请输入学校代码', trigger: 'blur' },
    { pattern: /^[A-Z0-9]+$/, message: '学校代码只能包含大写字母和数字', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 获取学校列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      schoolName: searchForm.schoolName || undefined
    }
    const data = await getSchoolPageApi(params)
    tableData.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('获取学校列表失败:', error)
    ElMessage.error('获取学校列表失败')
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
  searchForm.schoolName = ''
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    schoolName: '',
    schoolCode: '',
    address: '',
    contactPerson: '',
    contactPhone: '',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: School) => {
  try {
    const data = await getSchoolByIdApi(row.id!)
    Object.assign(formData, data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取学校信息失败:', error)
    ElMessage.error('获取学校信息失败')
  }
}

// 查看
const handleView = async (row: School) => {
  try {
    const data = await getSchoolByIdApi(row.id!)
    viewData.value = data
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取学校信息失败:', error)
    ElMessage.error('获取学校信息失败')
  }
}

// 删除
const handleDelete = (row: School) => {
  ElMessageBox.confirm('确定要删除该学校吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteSchoolApi(row.id!)
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
        const api = formData.id ? updateSchoolApi : createSchoolApi
        await api(formData as School)
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

// 导出
const handleExport = async () => {
  try {
    const response = await exportSchoolsApi() as any
    // response.data 才是实际的 Blob 数据
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `学校列表_${new Date().getTime()}.xlsx`

    // 触发下载
    document.body.appendChild(link)
    link.click()

    // 清理
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.school-list {
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
}
</style>
