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
          placeholder="搜索课程名称或课程代码..."
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
          <el-option label="已完成" value="completed"></el-option>
          <el-option label="未开始" value="pending"></el-option>
          <el-option label="已取消" value="cancelled"></el-option>
        </el-select>

        <el-button @click="resetFilters">重置</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-card">
      <div class="table-container">
        <el-table
          :data="courses"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>

          <el-table-column prop="courseName" label="课程名称" min-width="180">
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
                <span style="font-weight: 500;">{{ row.courseName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="teacherName" label="授课教师" width="150">
            <template #default="{ row }">
              <div class="user-cell">
                <div class="user-avatar">{{ row.teacherName?.charAt(0) || '?' }}</div>
                <div class="user-info">
                  <span class="user-name">{{ row.teacherName || '-' }}</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="schoolName" label="所属学校" width="150">
            <template #default="{ row }">
              <span style="color: var(--text-secondary);">{{ row.schoolName || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="classroomName" label="教室" width="120">
            <template #default="{ row }">
              <span style="color: var(--text-secondary);">{{ row.classroomName || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="currentStudents" label="学生人数" width="100" align="center">
            <template #default="{ row }">
              <span style="font-weight: 600; color: var(--text-primary);">{{ row.currentStudents || 0 }}/{{ row.maxStudents || 0 }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <span
                class="status-tag"
                :class="{
                  'status-tag-success': row.status === 'ongoing',
                  'status-tag-info': row.status === 'pending',
                  'status-tag-warning': row.status === 'completed',
                  'status-tag-danger': row.status === 'cancelled'
                }"
              >
                {{ getStatusText(row.status) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="startDate" label="开课日期" width="120">
            <template #default="{ row }">
              <span style="color: var(--text-secondary);">{{ row.startDate || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="endDate" label="结课日期" width="120">
            <template #default="{ row }">
              <span style="color: var(--text-secondary);">{{ row.endDate || '-' }}</span>
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
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="courseForm.courseName"
            placeholder="请输入课程名称"
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="课程代码" prop="courseCode">
          <el-input
            v-model="courseForm.courseCode"
            placeholder="请输入课程代码,如: CS101"
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="课程类型" prop="courseType">
          <el-radio-group v-model="courseForm.courseType">
            <el-radio value="普通课程">普通课程</el-radio>
            <el-radio value="直播课程">直播课程</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="所属学校" prop="schoolId">
          <el-select v-model="courseForm.schoolId" placeholder="请选择所属学校" style="width: 100%;">
            <el-option
              v-for="school in schools"
              :key="school.id"
              :label="school.schoolName"
              :value="school.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="请选择授课教师" style="width: 100%;">
            <el-option
              v-for="teacher in teachers"
              :key="teacher.id"
              :label="teacher.realName || teacher.teacherName"
              :value="teacher.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="教室" prop="classroomId">
          <el-select v-model="courseForm.classroomId" placeholder="请选择教室" clearable style="width: 100%;">
            <el-option
              v-for="classroom in classrooms"
              :key="classroom.id"
              :label="classroom.classroomName"
              :value="classroom.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
          ></el-input>
        </el-form-item>

        <el-form-item label="学分" prop="credit">
          <el-input-number
            v-model="courseForm.credit"
            :min="0"
            :max="10"
            :step="0.5"
            :precision="1"
            style="width: 100%;"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="总课时" prop="totalHours">
          <el-input-number
            v-model="courseForm.totalHours"
            :min="0"
            :max="200"
            :step="1"
            style="width: 100%;"
          ></el-input-number>
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
            <el-radio value="pending">未开始</el-radio>
            <el-radio value="ongoing">进行中</el-radio>
            <el-radio value="completed">已完成</el-radio>
            <el-radio value="cancelled">已取消</el-radio>
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
import { getCoursePageApi, createCourseApi, updateCourseApi, deleteCourseApi } from '@/api/course'
import { getSchoolListApi } from '@/api/school'
import { getTeacherListApi } from '@/api/teacher'
import { getClassroomListApi } from '@/api/classroom'

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
const teachers = ref<any[]>([])

// 学校列表
const schools = ref<any[]>([])

// 教室列表
const classrooms = ref<any[]>([])

// 课程数据
const courses = ref<any[]>([])

// 表单数据
const courseForm = reactive({
  id: null,
  courseName: '',
  courseCode: '',
  courseType: '普通课程',
  schoolId: null,
  teacherId: null,
  classroomId: null,
  description: '',
  credit: null,
  totalHours: null,
  startDate: '',
  endDate: '',
  maxStudents: 50,
  status: 'pending'
})

// 表单验证规则
const formRules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '课程名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  courseCode: [
    { required: true, message: '请输入课程代码', trigger: 'blur' }
  ],
  schoolId: [
    { required: true, message: '请选择所属学校', trigger: 'change' }
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

// 计算属性 - 直接使用courses,因为筛选已经在后端完成
const totalCourses = ref(0)
const totalPages = computed(() => Math.ceil(totalCourses.value / pageSize.value))

// 方法
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '未开始',
    ongoing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const handleSearch = () => {
  currentPage.value = 1
  loadCourses()
}

const handleFilter = () => {
  currentPage.value = 1
  loadCourses()
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  currentPage.value = 1
  loadCourses()
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
    courseName: course.courseName,
    courseCode: course.courseCode,
    courseType: course.courseType || '普通课程',
    schoolId: course.schoolId,
    teacherId: course.teacherId,
    classroomId: course.classroomId,
    description: course.description,
    credit: course.credit,
    totalHours: course.totalHours,
    startDate: course.startDate,
    endDate: course.endDate,
    maxStudents: course.maxStudents,
    status: course.status || 'pending'
  })
  dialogVisible.value = true
}

const deleteCourse = async (course: any) => {
  ElMessageBox.confirm(
    `确定要删除课程"${course.courseName}"吗?此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCourseApi(course.id)
      ElMessage.success('删除成功')
      loadCourses()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const batchDelete = async () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedCourses.value.length} 个课程吗?`,
    '批量删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const deletePromises = selectedCourses.value.map((c: any) => deleteCourseApi(c.id))
      await Promise.all(deletePromises)
      selectedCourses.value = []
      ElMessage.success(`成功删除 ${deletePromises.length} 个课程`)
      loadCourses()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
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
    courseName: '',
    courseCode: '',
    courseType: '普通课程',
    schoolId: null,
    teacherId: null,
    classroomId: null,
    description: '',
    credit: null,
    totalHours: null,
    startDate: '',
    endDate: '',
    maxStudents: 50,
    status: 'pending'
  })
}

const submitForm = async () => {
  if (!courseFormRef.value) return

  try {
    await courseFormRef.value.validate()
    submitting.value = true

    // 构建提交数据,移除id字段(新建时)或保留id字段(编辑时)
    const submitData: any = { ...courseForm }
    if (!isEditMode.value) {
      delete submitData.id
    }

    if (isEditMode.value) {
      await updateCourseApi(submitData)
      ElMessage.success('课程更新成功')
    } else {
      await createCourseApi(submitData)
      ElMessage.success('课程创建成功')
    }

    dialogVisible.value = false
    resetForm()
    loadCourses()
  } catch (error: any) {
    console.error('表单验证失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}

// 加载课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const res = await getCoursePageApi({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      status: filterStatus.value || undefined
    })
    courses.value = res.records || []
    totalCourses.value = res.total || 0
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载学校列表
const loadSchools = async () => {
  try {
    const res = await getSchoolListApi()
    console.log('学校列表数据:', res)
    schools.value = res || []
  } catch (error) {
    console.error('加载学校列表失败:', error)
  }
}

// 加载教师列表
const loadTeachers = async () => {
  try {
    const res = await getTeacherListApi()
    console.log('教师列表数据:', res)
    teachers.value = res || []
  } catch (error) {
    console.error('加载教师列表失败:', error)
  }
}

// 加载教室列表
const loadClassrooms = async () => {
  try {
    const res = await getClassroomListApi()
    console.log('教室列表数据:', res)
    classrooms.value = res || []
  } catch (error) {
    console.error('加载教室列表失败:', error)
  }
}

onMounted(() => {
  loadCourses()
  loadSchools()
  loadTeachers()
  loadClassrooms()
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

  &.status-tag-danger {
    background: oklch(0.95 0.02 25);
    color: oklch(0.55 0.20 25);
  }
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
</style>
