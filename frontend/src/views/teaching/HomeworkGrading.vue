<template>
  <div class="homework-grading">
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
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

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
        <el-table-column prop="dueDate" label="截止日期" width="160" />
        <el-table-column prop="maxScore" label="满分" width="80" align="center">
          <template #default="{ row }">
            {{ row.maxScore }} 分
          </template>
        </el-table-column>
        <el-table-column label="提交/总数" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.submittedCount || 0 }} / {{ row.totalCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="未批改" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.ungradedCount > 0" type="warning">{{ row.ungradedCount }}</el-tag>
            <el-tag v-else type="success">0</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleGrade(row)"
            >
              批改作业
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

    <!-- 批改作业对话框 -->
    <el-dialog
      v-model="gradingDialogVisible"
      title="批改作业"
      width="900px"
      @close="handleGradingDialogClose"
    >
      <div class="homework-info">
        <h3>{{ currentHomework.title }}</h3>
        <p>课程: {{ currentHomework.courseName }}</p>
        <p>截止日期: {{ currentHomework.dueDate }}</p>
        <p>满分: {{ currentHomework.maxScore }} 分</p>
      </div>

      <el-table
        v-loading="submissionLoading"
        :data="submissionList"
        border
        stripe
        style="width: 100%; margin-top: 20px"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="content" label="作业内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fileUrl" label="附件" width="100">
          <template #default="{ row }">
            <el-button v-if="row.fileUrl" type="primary" size="small" link @click="viewFile(row.fileUrl)">
              查看
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="submitDate" label="提交时间" width="160" />
        <el-table-column prop="score" label="分数" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'GRADED'" type="success">{{ row.score }} 分</el-tag>
            <el-tag v-else type="warning">未批改</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleOpenGradeForm(row)"
            >
              {{ row.status === 'GRADED' ? '修改分数' : '批改' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 打分对话框 -->
    <el-dialog
      v-model="gradeFormDialogVisible"
      :title="gradeFormTitle"
      width="600px"
    >
      <el-form
        ref="gradeFormRef"
        :model="gradeForm"
        :rules="gradeFormRules"
        label-width="100px"
      >
        <el-form-item label="学生">
          <el-input v-model="currentSubmission.studentName" disabled />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input
            v-model="currentSubmission.content"
            type="textarea"
            :rows="3"
            disabled
          />
        </el-form-item>
        <el-form-item label="分数" prop="score">
          <el-input-number
            v-model="gradeForm.score"
            :min="0"
            :max="currentHomework.maxScore"
            :precision="0"
            style="width: 100%"
          />
          <span style="margin-left: 10px">/ {{ currentHomework.maxScore }} 分</span>
        </el-form-item>
        <el-form-item label="评语" prop="comment">
          <el-input
            v-model="gradeForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入评语"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitGrade">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getHomeworkPageApi, getHomeworkSubmissionsApi, gradeHomeworkApi } from '@/api/homework'
import { getCourseListApi } from '@/api/course'
import type { Homework, HomeworkSubmission } from '@/api/homework'
import type { Course } from '@/api/course'

// 搜索表单
const searchForm = reactive({
  title: '',
  courseId: undefined as number | undefined
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

// 批改对话框
const gradingDialogVisible = ref(false)
const submissionLoading = ref(false)
const submissionList = ref<HomeworkSubmission[]>([])
const currentHomework = ref<Homework>({
  title: '',
  courseName: '',
  dueDate: '',
  maxScore: 100
})

// 打分对话框
const gradeFormDialogVisible = ref(false)
const gradeFormTitle = computed(() =>
  currentSubmission.value.status === 'GRADED' ? '修改分数' : '批改作业'
)
const gradeFormRef = ref<FormInstance>()
const gradeForm = reactive({
  score: 0,
  comment: ''
})
const currentSubmission = ref<HomeworkSubmission>({
  studentName: '',
  content: ''
})

const gradeFormRules: FormRules = {
  score: [
    { required: true, message: '请输入分数', trigger: 'blur' }
  ],
  comment: [
    { required: true, message: '请输入评语', trigger: 'blur' }
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
      courseId: searchForm.courseId
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
  pagination.current = 1
  fetchData()
}

// 批改作业
const handleGrade = async (row: Homework) => {
  currentHomework.value = row
  gradingDialogVisible.value = true
  submissionLoading.value = true
  try {
    const response = await getHomeworkSubmissionsApi(row.id!)
    if (response.code === 200) {
      submissionList.value = response.data
    } else {
      ElMessage.error(response.message || '获取作业提交列表失败')
    }
  } catch (error) {
    console.error('获取作业提交列表失败:', error)
    ElMessage.error('获取作业提交列表失败')
  } finally {
    submissionLoading.value = false
  }
}

// 查看文件
const viewFile = (fileUrl: string) => {
  window.open(fileUrl, '_blank')
}

// 打开打分表单
const handleOpenGradeForm = (row: HomeworkSubmission) => {
  currentSubmission.value = row
  gradeForm.score = row.score || 0
  gradeForm.comment = row.comment || ''
  gradeFormDialogVisible.value = true
}

// 提交分数
const handleSubmitGrade = async () => {
  if (!gradeFormRef.value) return
  await gradeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await gradeHomeworkApi(
          currentSubmission.value.id!,
          gradeForm.score,
          gradeForm.comment
        )
        if (response.code === 200) {
          ElMessage.success('批改成功')
          gradeFormDialogVisible.value = false
          // 重新获取作业提交列表
          await handleGrade(currentHomework.value)
          fetchData()
        } else {
          ElMessage.error(response.message || '批改失败')
        }
      } catch (error) {
        console.error('批改失败:', error)
        ElMessage.error('批改失败')
      }
    }
  })
}

// 关闭批改对话框
const handleGradingDialogClose = () => {
  submissionList.value = []
  currentHomework.value = {
    title: '',
    courseName: '',
    dueDate: '',
    maxScore: 100
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchCourseList()
  fetchData()
})
</script>

<style scoped lang="scss">
.homework-grading {
  padding: 20px;

  .search-form {
    margin-bottom: 20px;
  }

  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;
  }

  .homework-info {
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;

    h3 {
      margin: 0 0 10px 0;
      font-size: 18px;
      color: #303133;
    }

    p {
      margin: 5px 0;
      font-size: 14px;
      color: #606266;
    }
  }
}
</style>
