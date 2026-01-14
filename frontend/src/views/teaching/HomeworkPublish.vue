<template>
  <div class="homework-publish">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="作业标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入作业标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="课程">
          <el-select
            v-model="searchForm.courseId"
            placeholder="请选择课程"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.name"
              :value="course.id"
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
          type="primary"
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          发布作业
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
        <el-table-column prop="title" label="作业标题" min-width="200" />
        <el-table-column prop="courseName" label="课程" width="180" />
        <el-table-column prop="teacherName" label="发布教师" width="120" />
        <el-table-column prop="description" label="作业描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="dueDate" label="截止日期" width="160" />
        <el-table-column prop="maxScore" label="满分" width="80" align="center">
          <template #default="{ row }">
            {{ row.maxScore }} 分
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
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
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="选择课程" prop="courseId">
          <el-select
            v-model="formData.courseId"
            placeholder="请选择课程"
            style="width: 100%"
          >
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作业描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入作业描述、要求等"
          />
        </el-form-item>
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
            v-model="formData.dueDate"
            type="datetime"
            placeholder="选择截止日期和时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="满分" prop="maxScore">
          <el-input-number
            v-model="formData.maxScore"
            :min="1"
            :max="1000"
            style="width: 100%"
          />
          <span style="margin-left: 10px">分</span>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getHomeworkPageApi, getHomeworkByIdApi, createHomeworkApi, updateHomeworkApi, deleteHomeworkApi } from '@/api/homework'
import { getCourseListApi } from '@/api/course'
import type { Homework } from '@/api/homework'
import type { Course } from '@/api/course'

// 搜索表单
const searchForm = reactive({
  title: '',
  courseId: undefined as number | undefined,
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
const tableData = ref<Homework[]>([])

// 课程列表
const courseList = ref<Course[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑作业' : '发布作业'))
const formRef = ref<FormInstance>()
const formData = reactive<Homework>({
  title: '',
  courseId: undefined,
  description: '',
  dueDate: '',
  maxScore: 100,
  status: 1
})

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入作业标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入作业描述', trigger: 'blur' }
  ],
  dueDate: [
    { required: true, message: '请选择截止日期', trigger: 'change' }
  ],
  maxScore: [
    { required: true, message: '请输入满分', trigger: 'blur' }
  ]
}

// 获取课程列表
const fetchCourseList = async () => {
  try {
    const response = await getCourseListApi()
    if (response.code === 200) {
      courseList.value = response.data
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 获取作业列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      title: searchForm.title || undefined,
      courseId: searchForm.courseId,
      status: searchForm.status
    }
    const response = await getHomeworkPageApi(params)
    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取作业列表失败')
    }
  } catch (error) {
    console.error('获取作业列表失败:', error)
    ElMessage.error('获取作业列表失败')
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
  searchForm.title = ''
  searchForm.courseId = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    title: '',
    courseId: undefined,
    description: '',
    dueDate: '',
    maxScore: 100,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: Homework) => {
  try {
    const response = await getHomeworkByIdApi(row.id!)
    if (response.code === 200) {
      Object.assign(formData, response.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取作业信息失败')
    }
  } catch (error) {
    console.error('获取作业信息失败:', error)
    ElMessage.error('获取作业信息失败')
  }
}

// 删除
const handleDelete = (row: Homework) => {
  ElMessageBox.confirm('确定要删除该作业吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await deleteHomeworkApi(row.id!)
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

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = formData.id ? updateHomeworkApi : createHomeworkApi
        const response = await api(formData)
        if (response.code === 200) {
          ElMessage.success(formData.id ? '更新成功' : '发布成功')
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

// 页面加载时获取数据
onMounted(() => {
  fetchCourseList()
  fetchData()
})
</script>

<style scoped lang="scss">
.homework-publish {
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
