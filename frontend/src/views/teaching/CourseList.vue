<template>
  <div class="course-list">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="课程代码">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入课程代码"
            clearable
            style="width: 150px"
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
              :label="school.name"
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
              :label="teacher.realName"
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
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button
          v-permission="'teaching:course:add'"
          type="primary"
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增课程
        </el-button>
        <el-button
          v-permission="'teaching:course:export'"
          @click="handleExport"
        >
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="code" label="课程代码" width="120" />
        <el-table-column prop="schoolName" label="所属学校" width="180" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="classroomName" label="教室" width="120" />
        <el-table-column prop="credits" label="学分" width="80" align="center" />
        <el-table-column prop="hours" label="课时" width="80" align="center">
          <template #default="{ row }">
            {{ row.hours }} 课时
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量" width="80" align="center">
          <template #default="{ row }">
            {{ row.enrolledCount || 0 }} / {{ row.capacity }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'teaching:course:view'"
              type="info"
              size="small"
              link
              @click="handleViewStudents(row)"
            >
              学生
            </el-button>
            <el-button
              v-permission="'teaching:course:edit'"
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-permission="'teaching:course:delete'"
              type="danger"
              size="small"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程代码" prop="code">
              <el-input v-model="formData.code" placeholder="如: CS101" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
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
                  :label="school.name"
                  :value="school.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
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
                  :label="teacher.realName"
                  :value="teacher.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
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
                  :label="`${classroom.name} (${classroom.building}${classroom.roomNumber})`"
                  :value="classroom.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学分" prop="credits">
              <el-input-number v-model="formData.credits" :min="0.5" :max="10" :step="0.5" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课时" prop="hours">
              <el-input-number v-model="formData.hours" :min="1" :max="200" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容量" prop="capacity">
              <el-input-number v-model="formData.capacity" :min="1" :max="500" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="课程简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程简介"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 学生列表对话框 -->
    <el-dialog
      v-model="studentDialogVisible"
      title="课程学生列表"
      width="900px"
    >
      <el-table
        v-loading="studentLoading"
        :data="studentList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="username" label="学号" width="120" />
        <el-table-column prop="schoolName" label="学校" width="180" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="enrollDate" label="选课日期" width="120" />
      </el-table>
      <template #footer>
        <el-button @click="studentDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Download } from '@element-plus/icons-vue'
import { getCoursePageApi, getCourseByIdApi, createCourseApi, updateCourseApi, deleteCourseApi, getCourseStudentsApi } from '@/api/course'
import { getSchoolListApi } from '@/api/school'
import { getTeacherListApi } from '@/api/teacher'
import { getClassroomListApi } from '@/api/classroom'
import type { School } from '@/api/school'
import type { Teacher } from '@/api/teacher'
import type { Classroom } from '@/api/classroom'

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  schoolId: undefined as number | undefined,
  teacherId: undefined as number | undefined,
  status: undefined as number | undefined
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref([])

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
const formData = reactive({
  id: undefined,
  name: '',
  code: '',
  schoolId: undefined,
  teacherId: undefined,
  classroomId: undefined,
  description: '',
  credits: 3,
  hours: 48,
  capacity: 50,
  startDate: '',
  endDate: '',
  status: 1
})

// 学生列表对话框
const studentDialogVisible = ref(false)
const studentLoading = ref(false)
const studentList = ref([])

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
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入课程代码', trigger: 'blur' },
    { pattern: /^[A-Z0-9]+$/, message: '课程代码只能包含大写字母和数字', trigger: 'blur' }
  ],
  schoolId: [
    { required: true, message: '请选择学校', trigger: 'change' }
  ],
  teacherId: [
    { required: true, message: '请选择授课教师', trigger: 'change' }
  ],
  classroomId: [
    { required: true, message: '请选择教室', trigger: 'change' }
  ],
  credits: [
    { required: true, message: '请输入学分', trigger: 'blur' }
  ],
  hours: [
    { required: true, message: '请输入课时', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入容量', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ]
}

// 获取学校列表
const fetchSchoolList = async () => {
  try {
    const response = await getSchoolListApi()
    if (response.code === 200) {
      schoolList.value = response.data
    }
  } catch (error) {
    console.error('获取学校列表失败:', error)
  }
}

// 获取教师列表
const fetchTeacherList = async () => {
  try {
    const response = await getTeacherListApi()
    if (response.code === 200) {
      teacherList.value = response.data
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
  }
}

// 获取教室列表
const fetchClassroomList = async () => {
  try {
    const response = await getClassroomListApi()
    if (response.code === 200) {
      classroomList.value = response.data
    }
  } catch (error) {
    console.error('获取教室列表失败:', error)
  }
}

// 获取课程列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      name: searchForm.name || undefined,
      code: searchForm.code || undefined,
      schoolId: searchForm.schoolId,
      teacherId: searchForm.teacherId,
      status: searchForm.status
    }
    const response = await getCoursePageApi(params)
    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取课程列表失败')
    }
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
  searchForm.name = ''
  searchForm.code = ''
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
    name: '',
    code: '',
    schoolId: undefined,
    teacherId: undefined,
    classroomId: undefined,
    description: '',
    credits: 3,
    hours: 48,
    capacity: 50,
    startDate: '',
    endDate: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: any) => {
  try {
    const response = await getCourseByIdApi(row.id)
    if (response.code === 200) {
      Object.assign(formData, response.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取课程信息失败')
    }
  } catch (error) {
    console.error('获取课程信息失败:', error)
    ElMessage.error('获取课程信息失败')
  }
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该课程吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await deleteCourseApi(row.id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          fetchData()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 查看学生
const handleViewStudents = async (row: any) => {
  studentDialogVisible.value = true
  studentLoading.value = true
  try {
    const response = await getCourseStudentsApi(row.id)
    if (response.code === 200) {
      studentList.value = response.data
    } else {
      ElMessage.error(response.message || '获取学生列表失败')
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败')
  } finally {
    studentLoading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = formData.id ? updateCourseApi : createCourseApi
        const response = await api(formData)
        if (response.code === 200) {
          ElMessage.success(formData.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
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
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

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

  .search-form {
    margin-bottom: 20px;
  }

  .table-operations {
    margin-bottom: 20px;
  }

  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
