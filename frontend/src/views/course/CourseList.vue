<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">课程管理</h1>
      <div class="page-actions">
        <el-button type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon>
          新建课程
        </el-button>
        <el-button :disabled="selectedCourses.length === 0" @click="batchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button @click="exportData">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称或教师..."
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
          v-model="filterStatus"
          placeholder="课程状态"
          clearable
          @change="handleFilter"
          style="width: 140px;"
        >
          <el-option label="全部" value=""></el-option>
          <el-option label="进行中" value="ongoing"></el-option>
          <el-option label="已结束" value="finished"></el-option>
          <el-option label="未开始" value="not_started"></el-option>
        </el-select>

        <el-button @click="resetFilters">重置</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-card">
      <div class="table-container">
        <el-table
          :data="filteredCourses"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>

          <el-table-column prop="name" label="课程名称" min-width="180">
            <template #default="{ row }">
              <div style="display: flex; align-items: center; gap: 8px;">
                <div style="
                  width: 32px;
                  height: 32px;
                  background: oklch(0.92 0.03 45);
                  border-radius: 8px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                ">
                  <el-icon style="color: oklch(0.50 0.18 45);"><Reading /></el-icon>
                </div>
                <span style="font-weight: 500;">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="teacher" label="授课教师" width="150">
            <template #default="{ row }">
              <div class="user-cell">
                <div class="user-avatar">{{ row.teacher.charAt(0) }}</div>
                <div class="user-info">
                  <span class="user-name">{{ row.teacher }}</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="students" label="学生人数" width="100" align="center">
            <template #default="{ row }">
              <span style="font-weight: 600; color: var(--text-primary);">{{ row.students }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <span
                class="status-tag"
                :class="{
                  'status-tag-success': row.status === 'ongoing',
                  'status-tag-info': row.status === 'not_started',
                  'status-tag-warning': row.status === 'finished'
                }"
              >
                {{ row.statusText }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="startDate" label="开课日期" width="120">
            <template #default="{ row }">
              <span style="color: var(--text-secondary);">{{ row.startDate }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" link size="small" @click="editCourse(row)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="deleteCourse(row)">
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
          共 <strong>{{ totalCourses }}</strong> 条数据,
          当前第 <strong>{{ currentPage }}</strong> / {{ totalPages }} 页
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalCourses"
          layout="sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 新建/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑课程' : '新建课程'"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="courseForm" :rules="formRules" ref="courseFormRef" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input
            v-model="courseForm.name"
            placeholder="请输入课程名称"
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
          ></el-input>
        </el-form-item>

        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="请选择授课教师" style="width: 100%;">
            <el-option
              v-for="teacher in teachers"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="开课日期" prop="startDate">
          <el-date-picker
            v-model="courseForm.startDate"
            type="date"
            placeholder="选择开课日期"
            style="width: 100%;"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="结课日期" prop="endDate">
          <el-date-picker
            v-model="courseForm.endDate"
            type="date"
            placeholder="选择结课日期"
            style="width: 100%;"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number
            v-model="courseForm.maxStudents"
            :min="1"
            :max="200"
            :step="1"
            style="width: 100%;"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="课程状态" prop="status">
          <el-radio-group v-model="courseForm.status">
            <el-radio value="not_started">未开始</el-radio>
            <el-radio value="ongoing">进行中</el-radio>
            <el-radio value="finished">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">
          {{ submitting ? '提交中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 表单引用
const courseFormRef = ref()

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const isEditMode = ref(false)

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const selectedCourses = ref([])

// 教师列表
const teachers = ref([
  { id: 1, name: '张老师' },
  { id: 2, name: '李老师' },
  { id: 3, name: '王老师' }
])

// 课程数据
const courses = ref([])

// 表单数据
const courseForm = reactive({
  id: null,
  name: '',
  description: '',
  teacherId: null,
  startDate: '',
  endDate: '',
  maxStudents: 50,
  status: 'not_started'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '课程名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入课程描述', trigger: 'blur' }
  ],
  teacherId: [
    { required: true, message: '请选择授课教师', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开课日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结课日期', trigger: 'change' }
  ],
  maxStudents: [
    { required: true, message: '请输入最大人数', trigger: 'blur' }
  ]
}

// 计算属性
const filteredCourses = computed(() => {
  let result = [...courses.value]

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter((course: any) =>
      course.name.toLowerCase().includes(keyword) ||
      course.teacher.toLowerCase().includes(keyword)
    )
  }

  if (filterStatus.value) {
    result = result.filter((course: any) => course.status === filterStatus.value)
  }

  return result
})

const totalCourses = computed(() => filteredCourses.value.length)
const totalPages = computed(() => Math.ceil(totalCourses.value / pageSize.value))

// 方法
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  currentPage.value = 1
}

const handleSelectionChange = (selection: any) => {
  selectedCourses.value = selection
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const openCreateDialog = () => {
  isEditMode.value = false
  dialogVisible.value = true
}

const editCourse = (course: any) => {
  isEditMode.value = true
  Object.assign(courseForm, {
    id: course.id,
    name: course.name,
    description: course.description,
    teacherId: course.teacherId,
    startDate: course.startDate,
    endDate: course.endDate,
    maxStudents: course.maxStudents,
    status: course.status
  })
  dialogVisible.value = true
}

const deleteCourse = (course: any) => {
  ElMessageBox.confirm(
    `确定要删除课程"${course.name}"吗?此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = courses.value.findIndex((c: any) => c.id === course.id)
    if (index > -1) {
      courses.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const batchDelete = () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedCourses.value.length} 个课程吗?`,
    '批量删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const selectedIds = selectedCourses.value.map((c: any) => c.id)
    courses.value = courses.value.filter((c: any) => !selectedIds.includes(c.id))
    selectedCourses.value = []
    ElMessage.success(`成功删除 ${selectedIds.length} 个课程`)
  }).catch(() => {})
}

const exportData = () => {
  ElMessage.success('正在导出数据...')
}

const resetForm = () => {
  if (courseFormRef.value) {
    courseFormRef.value.resetFields()
  }
  Object.assign(courseForm, {
    id: null,
    name: '',
    description: '',
    teacherId: null,
    startDate: '',
    endDate: '',
    maxStudents: 50,
    status: 'not_started'
  })
}

const submitForm = async () => {
  if (!courseFormRef.value) return

  try {
    await courseFormRef.value.validate()
    submitting.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    if (isEditMode.value) {
      const index = courses.value.findIndex((c: any) => c.id === courseForm.id)
      if (index > -1) {
        const teacher = teachers.value.find((t: any) => t.id === courseForm.teacherId)
        courses.value[index] = {
          ...courses.value[index],
          ...courseForm,
          teacher: teacher.name,
          statusText: courseForm.status === 'ongoing' ? '进行中' :
                      courseForm.status === 'finished' ? '已结束' : '未开始'
        }
      }
      ElMessage.success('课程更新成功')
    } else {
      const teacher = teachers.value.find((t: any) => t.id === courseForm.teacherId)
      const newCourse = {
        ...courseForm,
        id: Date.now(),
        teacher: teacher.name,
        students: 0,
        statusText: courseForm.status === 'ongoing' ? '进行中' :
                    courseForm.status === 'finished' ? '已结束' : '未开始'
      }
      courses.value.unshift(newCourse)
      ElMessage.success('课程创建成功')
    }

    dialogVisible.value = false
    resetForm()
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 加载课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    courses.value = [
      {
        id: 1,
        name: 'Java 程序设计',
        description: '学习 Java 基础语法和面向对象编程',
        teacherId: 1,
        teacher: '张老师',
        students: 45,
        maxStudents: 50,
        status: 'ongoing',
        statusText: '进行中',
        startDate: '2025-01-15',
        endDate: '2025-06-15'
      },
      {
        id: 2,
        name: 'Vue.js 前端开发',
        description: '掌握 Vue.js 框架开发单页应用',
        teacherId: 2,
        teacher: '李老师',
        students: 38,
        maxStudents: 40,
        status: 'ongoing',
        statusText: '进行中',
        startDate: '2025-02-01',
        endDate: '2025-07-01'
      }
    ]
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCourses()
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

.user-cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;

  .user-avatar {
    width: 32px;
    height: 32px;
    background: var(--gradient-blue);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    font-size: 0.875rem;
  }

  .user-info {
    .user-name {
      font-weight: 500;
    }
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

  &.status-tag-success {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.status-tag-info {
    background: oklch(0.92 0.04 220);
    color: oklch(0.55 0.15 220);
  }

  &.status-tag-warning {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
</style>
