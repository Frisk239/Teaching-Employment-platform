<template>
  <div class="course-list">
    <el-card shadow="never" class="search-card">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="课程名称/课程代码"
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
        <el-form-item label="授课教师">
          <el-select
            v-model="searchForm.teacherId"
            placeholder="请选择教师"
            clearable
            filterable
            style="width: 180px"
          >
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.realName || teacher.teacherName"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="未开始" value="pending" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
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
          <span class="title">课程列表</span>
          <div class="operations">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增课程
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
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="courseType" label="课程类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.courseType === '直播课程' ? 'warning' : 'primary'" size="small">
              {{ row.courseType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="schoolName" label="所属学校" width="180" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="classroomName" label="教室" width="150" />
        <el-table-column prop="credit" label="学分" width="80" align="center">
          <template #default="{ row }">
            {{ row.credit || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="totalHours" label="总课时" width="80" align="center">
          <template #default="{ row }">
            {{ row.totalHours || 0 }} 课时
          </template>
        </el-table-column>
        <el-table-column prop="maxStudents" label="人数" width="100" align="center">
          <template #default="{ row }">
            {{ row.currentStudents || 0 }} / {{ row.maxStudents || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开课日期" width="120" />
        <el-table-column prop="endDate" label="结课日期" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" link @click="handleView(row)">
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
      width="800px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="course-form"
      >
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程代码" prop="courseCode">
              <el-input v-model="formData.courseCode" placeholder="如: CS101" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="formData.courseType" placeholder="请选择类型" style="width: 100%">
                <el-option label="普通课程" value="普通课程" />
                <el-option label="直播课程" value="直播课程" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属学校" prop="schoolId">
              <el-select
                v-model="formData.schoolId"
                placeholder="请选择学校"
                style="width: 100%"
                @change="handleSchoolChange"
              >
                <el-option
                  v-for="school in schoolList"
                  :key="school.id"
                  :label="school.schoolName"
                  :value="school.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授课教师" prop="teacherId">
              <el-select
                v-model="formData.teacherId"
                placeholder="请选择教师"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="teacher in filteredTeacherList"
                  :key="teacher.id"
                  :label="teacher.realName || teacher.teacherName"
                  :value="teacher.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教室" prop="classroomId">
              <el-select
                v-model="formData.classroomId"
                placeholder="请选择教室"
                style="width: 100%"
              >
                <el-option
                  v-for="classroom in filteredClassroomList"
                  :key="classroom.id"
                  :label="`${classroom.classroomName} (${classroom.building} ${classroom.floor})`"
                  :value="classroom.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">课程信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学分" prop="credit">
              <el-input-number
                v-model="formData.credit"
                :min="0.5"
                :max="10"
                :step="0.5"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总课时" prop="totalHours">
              <el-input-number
                v-model="formData.totalHours"
                :min="1"
                :max="200"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大学员数" prop="maxStudents">
              <el-input-number
                v-model="formData.maxStudents"
                :min="1"
                :max="500"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开课日期" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="选择开课日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结课日期" prop="endDate">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="选择结课日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">其他信息</el-divider>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="pending">未开始</el-radio>
            <el-radio value="ongoing">进行中</el-radio>
            <el-radio value="completed">已完成</el-radio>
            <el-radio value="cancelled">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="课程详情" width="700px">
      <el-descriptions v-if="viewData" :column="2" border>
        <el-descriptions-item label="课程代码">{{ viewData.courseCode }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ viewData.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ viewData.courseType }}</el-descriptions-item>
        <el-descriptions-item label="所属学校">{{ viewData.schoolName }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ viewData.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="教室">{{ viewData.classroomName }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ viewData.credit || 0 }}</el-descriptions-item>
        <el-descriptions-item label="总课时">{{ viewData.totalHours || 0 }} 课时</el-descriptions-item>
        <el-descriptions-item label="最大学员数">{{ viewData.maxStudents || 0 }}</el-descriptions-item>
        <el-descriptions-item label="当前学员数">{{ viewData.currentStudents || 0 }}</el-descriptions-item>
        <el-descriptions-item label="开课日期">{{ viewData.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结课日期">{{ viewData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="getStatusType(viewData.status)">
            {{ getStatusLabel(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="课程描述" :span="2">{{ viewData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, View, Edit, Delete } from '@element-plus/icons-vue'
import {
  getCoursePageApi,
  getCourseByIdApi,
  createCourseApi,
  updateCourseApi,
  deleteCourseApi
} from '@/api/course'
import { getSchoolListApi } from '@/api/school'
import { getTeacherListApi } from '@/api/teacher'
import { getClassroomListApi } from '@/api/classroom'
import type { Course } from '@/api/course'
import type { School } from '@/api/school'
import type { Teacher } from '@/api/teacher'
import type { Classroom } from '@/api/classroom'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  schoolId: undefined as number | undefined,
  teacherId: undefined as number | undefined,
  status: undefined as string | undefined
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<Course[]>([])

// 学校列表
const schoolList = ref<School[]>([])
// 教师列表
const teacherList = ref<Teacher[]>([])
// 教室列表
const classroomList = ref<Classroom[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑课程' : '新增课程'))
const formRef = ref<FormInstance>()
const formData = reactive<Partial<Course>>({
  id: undefined,
  courseName: '',
  courseCode: '',
  courseType: '普通课程',
  schoolId: undefined,
  teacherId: undefined,
  classroomId: undefined,
  description: '',
  credit: 3,
  totalHours: 48,
  maxStudents: 50,
  startDate: '',
  endDate: '',
  status: 'pending'
})

// 查看详情
const viewDialogVisible = ref(false)
const viewData = ref<Course | null>(null)

// 过滤后的教师列表(根据学校)
const filteredTeacherList = computed(() => {
  if (!formData.schoolId) return teacherList.value
  return teacherList.value.filter(t => t.schoolId === formData.schoolId)
})

// 过滤后的教室列表(根据学校)
const filteredClassroomList = computed(() => {
  if (!formData.schoolId) return classroomList.value
  return classroomList.value.filter(c => c.schoolId === formData.schoolId)
})

// 表单验证规则
const formRules: FormRules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  courseCode: [
    { required: true, message: '请输入课程代码', trigger: 'blur' },
    { pattern: /^[A-Z0-9]+$/, message: '课程代码只能包含大写字母和数字', trigger: 'blur' }
  ],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择授课教师', trigger: 'change' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }],
  totalHours: [{ required: true, message: '请输入总课时', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大学员数', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开课日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结课日期', trigger: 'change' }]
}

// 获取状态类型
const getStatusType = (status: string | undefined) => {
  const typeMap: Record<string, any> = {
    pending: 'info',
    ongoing: 'success',
    completed: '',
    cancelled: 'danger'
  }
  return typeMap[status || ''] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string | undefined) => {
  const labelMap: Record<string, string> = {
    pending: '未开始',
    ongoing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return labelMap[status || ''] || '未知'
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

// 获取教师列表
const fetchTeacherList = async (schoolId?: number) => {
  try {
    const data = await getTeacherListApi(schoolId)
    teacherList.value = data
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  }
}

// 获取教室列表
const fetchClassroomList = async (schoolId?: number) => {
  try {
    const data = await getClassroomListApi(schoolId)
    classroomList.value = data
  } catch (error) {
    console.error('获取教室列表失败:', error)
    ElMessage.error('获取教室列表失败')
  }
}

// 获取课程列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      schoolId: searchForm.schoolId,
      teacherId: searchForm.teacherId,
      status: searchForm.status,
      keyword: searchForm.keyword || undefined
    }
    const data = await getCoursePageApi(params)
    tableData.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 学校变化时清空关联数据
const handleSchoolChange = () => {
  formData.teacherId = undefined
  formData.classroomId = undefined
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
  searchForm.teacherId = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    courseName: '',
    courseCode: '',
    courseType: '普通课程',
    schoolId: undefined,
    teacherId: undefined,
    classroomId: undefined,
    description: '',
    credit: 3,
    totalHours: 48,
    maxStudents: 50,
    startDate: '',
    endDate: '',
    status: 'pending'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: Course) => {
  try {
    const data = await getCourseByIdApi(row.id!)
    Object.assign(formData, data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取课程信息失败:', error)
    ElMessage.error('获取课程信息失败')
  }
}

// 查看
const handleView = async (row: Course) => {
  try {
    const data = await getCourseByIdApi(row.id!)
    viewData.value = data
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取课程信息失败:', error)
    ElMessage.error('获取课程信息失败')
  }
}

// 删除
const handleDelete = (row: Course) => {
  ElMessageBox.confirm('确定要删除该课程吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteCourseApi(row.id!)
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
        const api = formData.id ? updateCourseApi : createCourseApi
        await api(formData as Course)
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

// 监听学校变化，动态加载教师和教室
watch(() => formData.schoolId, (newSchoolId, oldSchoolId) => {
  // 学校改变时，清空已选择的教师和教室
  if (oldSchoolId !== undefined && newSchoolId !== oldSchoolId) {
    formData.teacherId = undefined
    formData.classroomId = undefined
  }
  // 根据选择的学校加载教师和教室
  fetchTeacherList(newSchoolId)
  fetchClassroomList(newSchoolId)
})

// 页面加载时获取数据
onMounted(() => {
  fetchSchoolList()
  fetchTeacherList()
  fetchClassroomList()
  fetchData()
})
</script>

<style scoped lang="scss">
.course-list {
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

  .course-form {
    :deep(.el-divider) {
      margin: 20px 0;
    }
  }
}
</style>
