<template>
  <div class="daily-reports">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchForm.studentName"
            placeholder="请输入学生姓名"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px"
          />
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
          写日报
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
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="reportDate" label="日期" width="120" />
        <el-table-column prop="content" label="日报内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="studyHours" label="学习时长" width="100" align="center">
          <template #default="{ row }">
            {{ row.studyHours }} 小时
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="isOwner(row)"
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
        <el-form-item label="日期" prop="reportDate">
          <el-date-picker
            v-model="formData.reportDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="学习内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="6"
            placeholder="请详细描述今天学习的内容、遇到的问题、解决方案等"
          />
        </el-form-item>
        <el-form-item label="学习时长" prop="studyHours">
          <el-input-number
            v-model="formData.studyHours"
            :min="0.5"
            :max="24"
            :step="0.5"
            :precision="1"
            style="width: 100%"
          />
          <span style="margin-left: 10px">小时</span>
        </el-form-item>
        <el-form-item label="明日计划" prop="tomorrowPlan">
          <el-input
            v-model="formData.tomorrowPlan"
            type="textarea"
            :rows="3"
            placeholder="请输入明天的学习计划"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="日报详情"
      width="700px"
    >
      <div class="report-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学生姓名">
            {{ currentReport.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="课程">
            {{ currentReport.courseName }}
          </el-descriptions-item>
          <el-descriptions-item label="日期" :span="2">
            {{ currentReport.reportDate }}
          </el-descriptions-item>
          <el-descriptions-item label="学习时长">
            {{ currentReport.studyHours }} 小时
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ currentReport.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="学习内容" :span="2">
            <div class="content-text">{{ currentReport.content }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="明日计划" :span="2">
            <div class="content-text">{{ currentReport.tomorrowPlan }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCourseListApi } from '@/api/course'
import type { Course } from '@/api/course'
import { useAuthStore } from '@/stores/auth'

// 模拟日报数据（实际应该从API获取）
interface DailyReport {
  id?: number
  studentId?: number
  studentName?: string
  courseId?: number
  courseName?: string
  reportDate?: string
  content?: string
  studyHours?: number
  tomorrowPlan?: string
  createTime?: string
}

const authStore = useAuthStore()

// 搜索表单
const searchForm = reactive({
  studentName: '',
  startDate: '',
  endDate: ''
})

// 日期范围
const dateRange = ref<string[]>([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<DailyReport[]>([])

// 课程列表
const courseList = ref<Course[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑日报' : '写日报'))
const formRef = ref<FormInstance>()
const formData = reactive<DailyReport>({
  reportDate: new Date().toISOString().split('T')[0],
  content: '',
  studyHours: 4,
  tomorrowPlan: ''
})

// 查看对话框
const viewDialogVisible = ref(false)
const currentReport = ref<DailyReport>({})

// 表单验证规则
const formRules: FormRules = {
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  reportDate: [
    { required: true, message: '请选择日期', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入学习内容', trigger: 'blur' },
    { min: 10, message: '学习内容至少10个字符', trigger: 'blur' }
  ],
  studyHours: [
    { required: true, message: '请输入学习时长', trigger: 'blur' }
  ]
}

// 判断是否是日报的所有者
const isOwner = (row: DailyReport) => {
  // 简化判断，实际应该比较当前用户ID
  return authStore.userRole === 'user' || authStore.userRole === 'admin'
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

// 获取日报列表（模拟数据）
const fetchData = async () => {
  loading.value = true
  try {
    // TODO: 实际应该调用API
    // const response = await getDailyReportPageApi(params)

    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = [
      {
        id: 1,
        studentName: '张三',
        courseName: 'Java程序设计',
        reportDate: '2024-01-13',
        content: '今天学习了Java面向对象编程，包括类、对象、继承等概念。完成了三个练习题，遇到的问题是如何理解多态，通过查阅资料和观看视频教程解决了。',
        studyHours: 6,
        tomorrowPlan: '明天继续学习Java接口和抽象类，完成课后作业',
        createTime: '2024-01-13 18:30:00'
      },
      {
        id: 2,
        studentName: '李四',
        courseName: '数据结构',
        reportDate: '2024-01-13',
        content: '学习了二叉树的基本操作，包括前中后序遍历。实现了递归和非递归两种方式，对递归的理解更深刻了。',
        studyHours: 5,
        tomorrowPlan: '学习图的存储结构和遍历算法',
        createTime: '2024-01-13 19:00:00'
      }
    ]
    pagination.total = 2

    // ElMessage.error(response.message || '获取日报列表失败')
  } catch (error) {
    console.error('获取日报列表失败:', error)
    ElMessage.error('获取日报列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startDate = dateRange.value[0]
    searchForm.endDate = dateRange.value[1]
  }
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.studentName = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  dateRange.value = []
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    reportDate: new Date().toISOString().split('T')[0],
    courseId: undefined,
    content: '',
    studyHours: 4,
    tomorrowPlan: ''
  })
  dialogVisible.value = true
}

// 查看
const handleView = (row: DailyReport) => {
  currentReport.value = row
  viewDialogVisible.value = true
}

// 删除
const handleDelete = (row: DailyReport) => {
  ElMessageBox.confirm('确定要删除该日报吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        // TODO: 实际应该调用API
        // const response = await deleteDailyReportApi(row.id!)

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
        // TODO: 实际应该调用API
        // const api = formData.id ? updateDailyReportApi : createDailyReportApi
        // const response = await api(formData)

        await new Promise(resolve => setTimeout(resolve, 500))
        ElMessage.success(formData.id ? '更新成功' : '提交成功')
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
  fetchCourseList()
  fetchData()
})
</script>

<style scoped lang="scss">
.daily-reports {
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

  .report-detail {
    .content-text {
      white-space: pre-wrap;
      word-break: break-word;
      line-height: 1.6;
    }
  }
}
</style>
