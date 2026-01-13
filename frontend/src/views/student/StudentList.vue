<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">学生管理</h1>
      <div class="page-actions">
        <el-button @click="handleExport" :loading="exporting">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增学生
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选工具栏 -->
    <div class="toolbar">
      <div class="toolbar-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学生姓名、手机号或邮箱..."
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="toolbar-filters">
        <el-select
          v-model="filterClass"
          placeholder="筛选班级"
          clearable
          @change="handleFilterChange"
          style="width: 150px;"
        >
          <el-option label="全部班级" value=""></el-option>
          <el-option
            v-for="classItem in classList"
            :key="classItem.id"
            :label="classItem.name"
            :value="classItem.id"
          ></el-option>
        </el-select>

        <el-select
          v-model="filterStatus"
          placeholder="筛选状态"
          clearable
          @change="handleFilterChange"
          style="width: 120px;"
        >
          <el-option label="全部状态" value=""></el-option>
          <el-option label="在读" value="studying"></el-option>
          <el-option label="毕业" value="graduated"></el-option>
          <el-option label="休学" value="suspended"></el-option>
          <el-option label="退学" value="dropped"></el-option>
        </el-select>

        <el-button @click="handleResetFilters">
          <el-icon><RefreshLeft /></el-icon>
          重置筛选
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-card">
      <div class="table-container">
        <el-table
          :data="filteredStudents"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>

          <el-table-column label="学生信息" min-width="200">
            <template #default="{ row }">
              <div class="student-cell">
                <div class="student-avatar">
                  <span>{{ row.name.charAt(0) }}</span>
                </div>
                <div class="student-info">
                  <div class="student-name">{{ row.name }}</div>
                  <div class="student-email">{{ row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="班级" min-width="120">
            <template #default="{ row }">
              <div class="class-cell">
                <div class="class-name">{{ row.className }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="phone" label="手机号" width="130"></el-table-column>

          <el-table-column label="学习状态" width="100" align="center">
            <template #default="{ row }">
              <span class="status-tag" :class="`status-tag-${row.status}`">
                {{ getStatusLabel(row.status) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="就业状态" width="120" align="center">
            <template #default="{ row }">
              <span class="employment-tag" :class="`employment-tag-${row.employmentStatus}`">
                {{ getEmploymentStatusLabel(row.employmentStatus) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" link size="small" @click="handleView(row)">
                  查看
                </el-button>
                <el-button type="primary" link size="small" @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="handleDelete(row)">
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页器 -->
      <div class="pagination-container">
        <div class="pagination-info">
          共 {{ total }} 条记录,当前第 {{ currentPage }} / {{ totalPages }} 页
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 新增/编辑学生对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑学生' : '新增学生'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="studentForm" :rules="formRules" ref="studentFormRef" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" placeholder="请输入学生姓名"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="studentForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="studentForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>

        <el-form-item label="班级" prop="classId">
          <el-select v-model="studentForm.classId" placeholder="请选择班级" style="width: 100%;">
            <el-option
              v-for="classItem in classList"
              :key="classItem.id"
              :label="classItem.name"
              :value="classItem.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="学习状态" prop="status">
          <el-select v-model="studentForm.status" placeholder="请选择学习状态" style="width: 100%;">
            <el-option label="在读" value="studying"></el-option>
            <el-option label="毕业" value="graduated"></el-option>
            <el-option label="休学" value="suspended"></el-option>
            <el-option label="退学" value="dropped"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="就业状态" prop="employmentStatus">
          <el-select v-model="studentForm.employmentStatus" placeholder="请选择就业状态" style="width: 100%;">
            <el-option label="未就业" value="unemployed"></el-option>
            <el-option label="求职中" value="seeking"></el-option>
            <el-option label="已就业" value="employed"></el-option>
            <el-option label="已深造" value="further"></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ submitting ? '提交中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看学生详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="学生详情"
      width="700px"
    >
      <div v-if="currentStudent" style="padding: 1rem;">
        <div style="display: flex; gap: 2rem; margin-bottom: 2rem;">
          <div class="student-avatar" style="width: 100px; height: 100px; font-size: 2rem;">
            <span>{{ currentStudent.name.charAt(0) }}</span>
          </div>
          <div style="flex: 1;">
            <h2 style="margin: 0 0 0.5rem 0; font-size: 1.5rem;">{{ currentStudent.name }}</h2>
            <p style="margin: 0.25rem 0; color: var(--text-secondary);">
              <el-icon><Phone /></el-icon> {{ currentStudent.phone }}
            </p>
            <p style="margin: 0.25rem 0; color: var(--text-secondary);">
              <el-icon><Message /></el-icon> {{ currentStudent.email }}
            </p>
          </div>
        </div>

        <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
          <div style="padding: 1rem; background: oklch(0.99 0.005 240); border-radius: 0.5rem;">
            <div style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 0.25rem;">班级</div>
            <div style="font-weight: 500;">{{ currentStudent.className }}</div>
          </div>
          <div style="padding: 1rem; background: oklch(0.99 0.005 240); border-radius: 0.5rem;">
            <div style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 0.25rem;">学习状态</div>
            <span class="status-tag" :class="`status-tag-${currentStudent.status}`">
              {{ getStatusLabel(currentStudent.status) }}
            </span>
          </div>
          <div style="padding: 1rem; background: oklch(0.99 0.005 240); border-radius: 0.5rem;">
            <div style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 0.25rem;">就业状态</div>
            <span class="employment-tag" :class="`employment-tag-${currentStudent.employmentStatus}`">
              {{ getEmploymentStatusLabel(currentStudent.employmentStatus) }}
            </span>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEditFromDetail">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { studentApi } from '@/api/student'
import type { Student } from '@/api/types'

// 表单引用
const studentFormRef = ref()

// 加载状态
const loading = ref(false)
const submitting = ref(false)
const exporting = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEditMode = ref(false)
const currentStudent = ref<Student | null>(null)

// 搜索和筛选
const searchKeyword = ref('')
const filterClass = ref('')
const filterStatus = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 选中的行
const selectedRows = ref<Student[]>([])

// 班级列表
const classList = ref([
  { id: 1, name: 'Java开发班' },
  { id: 2, name: 'Vue.js前端班' },
  { id: 3, name: 'Python数据分析班' }
])

// 学生列表
const students = ref<Student[]>([])

// 学生表单
const studentForm = reactive({
  id: undefined as number | undefined,
  name: '',
  phone: '',
  email: '',
  classId: undefined as number | undefined,
  status: 'studying' as 'studying' | 'graduated' | 'suspended' | 'dropped',
  employmentStatus: 'unemployed' as 'unemployed' | 'seeking' | 'employed' | 'further'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入学生姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' } as any
  ],
  classId: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择学习状态', trigger: 'change' }
  ],
  employmentStatus: [
    { required: true, message: '请选择就业状态', trigger: 'change' }
  ]
}

// 计算属性
const filteredStudents = computed(() => {
  let result = students.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter((student: any) =>
      student.name.toLowerCase().includes(keyword) ||
      student.phone.includes(keyword) ||
      student.email.toLowerCase().includes(keyword)
    )
  }

  if (filterClass.value) {
    result = result.filter((student: any) => student.classId === filterClass.value)
  }

  if (filterStatus.value) {
    result = result.filter((student: any) => student.status === filterStatus.value)
  }

  total.value = result.length

  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return result.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

// 方法
const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'studying': '在读',
    'graduated': '毕业',
    'suspended': '休学',
    'dropped': '退学'
  }
  return map[status] || status
}

const getEmploymentStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'unemployed': '未就业',
    'seeking': '求职中',
    'employed': '已就业',
    'further': '已深造'
  }
  return map[status] || status
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilterChange = () => {
  currentPage.value = 1
}

const handleResetFilters = () => {
  searchKeyword.value = ''
  filterClass.value = ''
  filterStatus.value = ''
  currentPage.value = 1
}

const handleSelectionChange = (selection: any) => {
  selectedRows.value = selection
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const handleAdd = () => {
  isEditMode.value = false
  Object.assign(studentForm, {
    id: undefined,
    name: '',
    phone: '',
    email: '',
    classId: undefined,
    status: 'studying',
    employmentStatus: 'unemployed'
  })
  dialogVisible.value = true
}

const handleEdit = (row: Student) => {
  isEditMode.value = true
  Object.assign(studentForm, {
    id: row.id,
    name: row.name,
    phone: row.phone,
    email: row.email,
    classId: row.classId,
    status: row.status,
    employmentStatus: row.employmentStatus
  })
  dialogVisible.value = true
}

const handleView = (row: Student) => {
  currentStudent.value = row
  detailDialogVisible.value = true
}

const handleEditFromDetail = () => {
  detailDialogVisible.value = false
  if (currentStudent.value) {
    handleEdit(currentStudent.value)
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学生"${row.name}"吗?此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // TODO: 调用后端 API 删除
    // await studentApi.deleteStudent(row.id)

    const index = students.value.findIndex((s: any) => s.id === row.id)
    if (index > -1) {
      students.value.splice(index, 1)
    }

    ElMessage.success('删除成功!')
  } catch (error) {
    // 用户取消
  }
}

const handleSubmit = async () => {
  if (!studentFormRef.value) return

  try {
    await studentFormRef.value.validate()
    submitting.value = true

    // TODO: 调用后端 API
    // if (isEditMode.value) {
    //   await studentApi.updateStudent(studentForm)
    // } else {
    //   await studentApi.createStudent(studentForm)
    // }

    await new Promise(resolve => setTimeout(resolve, 1000))

    if (isEditMode.value) {
      const index = students.value.findIndex((s: any) => s.id === studentForm.id)
      if (index > -1) {
        students.value[index] = { ...students.value[index], ...studentForm }
      }
      ElMessage.success('更新成功!')
    } else {
      students.value.unshift({ ...studentForm, id: Date.now() })
      ElMessage.success('添加成功!')
    }

    dialogVisible.value = false
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleExport = async () => {
  exporting.value = true
  try {
    // TODO: 调用后端 API 导出
    await new Promise(resolve => setTimeout(resolve, 1500))
    ElMessage.success('导出成功!')
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// 加载学生列表
const loadStudents = async () => {
  loading.value = true
  try {
    const result = await studentApi.getPage({
      page: currentPage.value,
      pageSize: pageSize.value,
      name: searchKeyword.value || undefined,
    })
    students.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStudents()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;

  .page-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0;
    color: var(--text-primary);
  }

  .page-actions {
    display: flex;
    gap: 0.75rem;
  }
}

.toolbar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;

  .toolbar-search {
    flex: 1;
    min-width: 200px;
  }

  .toolbar-filters {
    display: flex;
    gap: 0.75rem;
    flex-wrap: wrap;
  }
}

.data-card {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: 1.5rem;
}

.table-container {
  margin-bottom: 1rem;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid var(--border);

  .pagination-info {
    color: var(--text-secondary);
    font-size: 0.875rem;
  }
}

.student-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;

  .student-avatar {
    width: 40px;
    height: 40px;
    background: var(--gradient-green);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
  }

  .student-info {
    .student-name {
      font-weight: 500;
      color: var(--text-primary);
    }

    .student-email {
      font-size: 0.875rem;
      color: var(--text-secondary);
    }
  }
}

.class-cell {
  .class-name {
    font-weight: 500;
    color: var(--text-primary);
  }
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;

  &.status-tag-studying {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.status-tag-graduated {
    background: oklch(0.92 0.04 220);
    color: oklch(0.55 0.15 220);
  }

  &.status-tag-suspended {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }

  &.status-tag-dropped {
    background: oklch(0.95 0.02 25);
    color: oklch(0.55 0.22 25);
  }
}

.employment-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;

  &.employment-tag-unemployed {
    background: oklch(0.95 0.01 240);
    color: oklch(0.40 0.10 240);
  }

  &.employment-tag-seeking {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }

  &.employment-tag-employed {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.employment-tag-further {
    background: oklch(0.92 0.04 280);
    color: oklch(0.50 0.18 280);
  }
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
</style>
