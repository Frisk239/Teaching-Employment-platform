<template>
  <div class="my-homework-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">我的作业</h1>
        <p class="page-subtitle">查看和管理你的作业提交情况</p>
      </div>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索作业标题..."
          clearable
          style="width: 300px;"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card stat-card-danger">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待提交</div>
          <div class="stat-value">{{ stats.pendingCount || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-warning">
        <div class="stat-icon">
          <el-icon><Upload /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已提交(待批改)</div>
          <div class="stat-value">{{ stats.submittedCount || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-success">
        <div class="stat-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已完成</div>
          <div class="stat-value">{{ stats.completedCount || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-primary">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">平均分</div>
          <div class="stat-value">{{ stats.averageScore || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <el-radio-group v-model="activeTab" @change="handleTabChange">
        <el-radio-button value="all">全部 ({{ homeworkList.length }})</el-radio-button>
        <el-radio-button value="pending">待提交 ({{ stats.pendingCount || 0 }})</el-radio-button>
        <el-radio-button value="submitted">已提交 ({{ stats.submittedCount || 0 }})</el-radio-button>
        <el-radio-button value="graded">已批改 ({{ stats.completedCount || 0 }})</el-radio-button>
      </el-radio-group>

      <el-select
        v-model="selectedCourse"
        placeholder="筛选课程"
        clearable
        style="width: 200px;"
        @change="handleCourseFilter"
      >
        <el-option label="全部课程" :value="null" />
        <el-option
          v-for="course in courses"
          :key="course.id"
          :label="course.courseName || course.name"
          :value="course.id"
        />
      </el-select>
    </div>

    <!-- 作业列表 -->
    <div v-loading="loading" class="homework-container">
      <el-empty v-if="!loading && filteredHomework.length === 0" description="暂无作业">
        <el-button type="primary" @click="$router.push('/teaching/my-courses')">查看我的课程</el-button>
      </el-empty>

      <div v-else class="homework-grid">
        <div
          v-for="homework in filteredHomework"
          :key="homework.id"
          class="homework-card"
          :class="getHomeworkCardClass(homework)"
        >
          <!-- 作业状态标签 -->
          <div class="homework-status-badge">
            <el-tag :type="getStatusType(homework)" size="small">
              {{ getStatusText(homework) }}
            </el-tag>
          </div>

          <!-- 作业信息 -->
          <div class="homework-info">
            <div class="homework-header">
              <h3 class="homework-title">{{ homework.title }}</h3>
              <el-tag v-if="homework.maxScore" type="info" size="small">
                {{ homework.maxScore }}分
              </el-tag>
            </div>

            <div class="homework-meta">
              <span class="meta-item">
                <el-icon><Reading /></el-icon>
                {{ homework.courseName || homework.course?.courseName }}
              </span>
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                {{ formatDateTime(homework.deadline) }}
              </span>
            </div>

            <p class="homework-description">{{ homework.description || '暂无描述' }}</p>

            <!-- 作业类型标签 -->
            <div class="homework-type-tags">
              <el-tag size="small" effect="plain">
                {{ getHomeworkTypeText(homework.homeworkType) }}
              </el-tag>
              <el-tag
                v-if="isOverdue(homework)"
                size="small"
                type="danger"
                effect="plain"
              >
                已逾期
              </el-tag>
              <el-tag
                v-else-if="isUrgent(homework)"
                size="small"
                type="warning"
                effect="plain"
              >
                即将截止
              </el-tag>
            </div>

            <!-- 提交状态信息 -->
            <div v-if="hasSubmitted(homework)" class="submission-info">
              <div v-if="homework.submission" class="submission-detail">
                <div class="submission-item">
                  <span class="label">提交时间:</span>
                  <span class="value">{{ formatDateTime(homework.submission.submitTime || homework.submission.submitDate) }}</span>
                </div>
                <div v-if="homework.submission.status === 'graded'" class="submission-item">
                  <span class="label">得分:</span>
                  <span class="value score" :class="getScoreClass(homework.submission.score)">
                    {{ homework.submission.score }}分
                  </span>
                </div>
                <div v-if="homework.submission.comment" class="submission-item full-width">
                  <span class="label">教师评语:</span>
                  <p class="comment">{{ homework.submission.comment }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="homework-actions">
            <el-button
              v-if="!hasSubmitted(homework)"
              type="primary"
              size="small"
              @click="submitHomework(homework)"
              :disabled="homework.status === 'closed'"
            >
              {{ homework.status === 'closed' ? '已截止' : '提交作业' }}
            </el-button>
            <el-button
              v-else-if="homework.submission?.status === 'submitted'"
              type="warning"
              size="small"
              @click="viewSubmission(homework)"
            >
              查看提交
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="viewSubmission(homework)"
            >
              查看详情
            </el-button>
            <el-button
              size="small"
              @click="viewHomeworkDetail(homework)"
            >
              作业要求
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 作业详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentHomework?.title"
      width="700px"
    >
      <div v-if="currentHomework" v-loading="loadingDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">
            {{ currentHomework.courseName || currentHomework.course?.courseName }}
          </el-descriptions-item>
          <el-descriptions-item label="作业类型">
            {{ getHomeworkTypeText(currentHomework.homeworkType) }}
          </el-descriptions-item>
          <el-descriptions-item label="满分">
            {{ currentHomework.maxScore }}分
          </el-descriptions-item>
          <el-descriptions-item label="截止时间">
            <span :class="{ 'text-danger': isOverdue(currentHomework) }">
              {{ formatDateTime(currentHomework.deadline) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="作业描述" :span="1">
            <div class="homework-detail-description">
              {{ currentHomework.description || '暂无描述' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentHomework.attachmentUrl" label="附件">
            <el-button type="primary" link size="small" @click="downloadFile(currentHomework.attachmentUrl, '作业附件')">
              <el-icon><Download /></el-icon>
              下载附件
            </el-button>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="!hasSubmitted(currentHomework) && currentHomework?.status !== 'closed'"
          type="primary"
          @click="submitFromDetail"
        >
          提交作业
        </el-button>
      </template>
    </el-dialog>

    <!-- 提交作业对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交作业"
      width="700px"
      @close="closeSubmitDialog"
    >
      <el-form :model="submitForm" label-width="100px">
        <el-form-item label="作业标题">
          <span>{{ currentHomework?.title }}</span>
        </el-form-item>
        <el-form-item label="作业描述">
          <div class="homework-description-text">
            {{ currentHomework?.description || '无' }}
          </div>
        </el-form-item>
        <el-form-item label="满分">
          <el-tag type="info">{{ currentHomework?.maxScore }}分</el-tag>
        </el-form-item>
        <el-form-item label="截止时间">
          <span :class="{ 'text-danger': isOverdue(currentHomework) }">
            {{ formatDateTime(currentHomework?.deadline) }}
          </span>
        </el-form-item>
        <el-form-item label="作业内容" required>
          <el-input
            v-model="submitForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入作业内容、答案或说明..."
          />
        </el-form-item>
        <el-form-item label="附件上传">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持doc/docx/pdf/txt/zip/rar等格式，文件大小不超过50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmSubmitHomework">
          确认提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看提交对话框 -->
    <el-dialog
      v-model="submissionDialogVisible"
      title="提交详情"
      width="700px"
    >
      <div v-if="currentHomework?.submission" v-loading="loadingSubmission">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="作业标题">
            {{ currentHomework.title }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ formatDateTime(currentHomework.submission.submitTime || currentHomework.submission.submitDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="提交状态">
            <el-tag :type="currentHomework.submission.status === 'graded' ? 'success' : 'warning'">
              {{ currentHomework.submission.status === 'graded' ? '已批改' : '待批改' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentHomework.submission.score" label="得分">
            <span class="score-large" :class="getScoreClass(currentHomework.submission.score)">
              {{ currentHomework.submission.score }}分
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="作业内容">
            <div class="submission-content">
              {{ currentHomework.submission.content || '无内容' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentHomework.submission.comment" label="教师评语">
            <div class="teacher-comment">
              {{ currentHomework.submission.comment }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentHomework.submission.attachmentUrl" label="提交附件">
            <el-button type="primary" link size="small" @click="downloadFile(currentHomework.submission.attachmentUrl, '我的提交附件')">
              <el-icon><Download /></el-icon>
              下载附件
            </el-button>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <el-button @click="submissionDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Clock,
  Upload,
  CircleCheck,
  TrendCharts,
  Reading,
  Calendar,
  Download,
  UploadFilled
} from '@element-plus/icons-vue'
import axios from '@/utils/request'
import { useAuthStore } from '@/stores'
import dayjs from 'dayjs'

const authStore = useAuthStore()

// 状态
const loading = ref(false)
const loadingDetail = ref(false)
const loadingSubmission = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const activeTab = ref('all')
const selectedCourse = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 数据
const homeworkList = ref<any[]>([])
const courses = ref<any[]>([])
const stats = ref({
  pendingCount: 0,
  submittedCount: 0,
  completedCount: 0,
  averageScore: 0
})

// 对话框
const detailDialogVisible = ref(false)
const submitDialogVisible = ref(false)
const submissionDialogVisible = ref(false)
const currentHomework = ref<any>(null)

// 提交表单
const submitForm = ref({
  content: '',
  fileUrl: ''
})
const fileList = ref<any[]>([])

// 获取学员ID
const getStudentId = () => {
  const userInfo = authStore.user
  if (userInfo && userInfo.studentId) {
    return userInfo.studentId
  }

  const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      if (user.studentId) {
        return user.studentId
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }

  console.warn('无法获取学员ID')
  return 0
}

// 过滤后的作业列表
const filteredHomework = computed(() => {
  let result = homeworkList.value

  // 标签筛选
  if (activeTab.value === 'pending') {
    result = result.filter(h => !h.submission)
  } else if (activeTab.value === 'submitted') {
    result = result.filter(h => h.submission && h.submission.status === 'submitted')
  } else if (activeTab.value === 'graded') {
    result = result.filter(h => h.submission && h.submission.status === 'graded')
  }

  // 课程筛选
  if (selectedCourse.value) {
    result = result.filter(h => h.courseId === selectedCourse.value)
  }

  // 搜索筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(h =>
      h.title?.toLowerCase().includes(keyword) ||
      h.description?.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 获取作业卡片样式
const getHomeworkCardClass = (homework: any) => {
  if (!homework.submission) return 'card-pending'
  if (homework.submission.status === 'graded') {
    if (homework.submission.score >= 90) return 'card-excellent'
    if (homework.submission.score >= 80) return 'card-good'
    if (homework.submission.score >= 60) return 'card-pass'
    return 'card-fail'
  }
  return 'card-submitted'
}

// 获取状态类型
const getStatusType = (homework: any) => {
  if (!homework.submission) {
    if (isOverdue(homework)) return 'danger'
    if (homework.status === 'closed') return 'info'
    return 'warning'
  }
  if (homework.submission.status === 'graded') {
    if (homework.submission.score >= 90) return 'success'
    if (homework.submission.score >= 60) return 'primary'
    return 'danger'
  }
  return 'warning'
}

// 获取状态文本
const getStatusText = (homework: any) => {
  // 检查是否有提交记录
  const submission = homework.submission
  if (!submission) {
    if (homework.status === 'closed') return '已截止'
    if (isOverdue(homework)) return '已逾期'
    return '待提交'
  }

  // 已提交，检查是否已批改
  if (submission.status === 'graded') {
    return `已批改 (${submission.score}分)`
  }
  return '已提交'
}

// 获取作业类型文本
const getHomeworkTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    assignment: '作业',
    project: '项目',
    other: '其他'
  }
  return typeMap[type] || '作业'
}

// 获取分数样式
const getScoreClass = (score: number) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

// 是否已提交
const hasSubmitted = (homework: any) => {
  return !!homework.submission
}

// 是否逾期
const isOverdue = (homework: any) => {
  if (!homework.deadline) return false
  return dayjs(homework.deadline).isBefore(dayjs())
}

// 是否紧急
const isUrgent = (homework: any) => {
  if (!homework.deadline) return false
  return dayjs(homework.deadline).isBefore(dayjs().add(3, 'day'))
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm')
}

// 加载作业列表
const loadHomeworkList = async () => {
  loading.value = true
  try {
    const studentId = getStudentId()
    const response = await axios.get(`/homework/student/${studentId}`, {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })

    if (response && (response as any).records) {
      const homeworks = (response as any).records || []

      // 为每个作业加载提交状态
      const homeworksWithSubmission = await Promise.all(
        homeworks.map(async (homework: any) => {
          try {
            const submissionResponse = await axios.get(
              `/homework-submission/homework/${homework.id}/student/${studentId}`
            )
            // 检查是否是有效的提交对象（必须有id字段）
            const isValidSubmission = submissionResponse && typeof submissionResponse === 'object' && 'id' in submissionResponse
            return {
              ...homework,
              submission: isValidSubmission ? submissionResponse : null,
              courseName: homework.course?.courseName || '未知课程'
            }
          } catch (e) {
            return {
              ...homework,
              submission: null,
              courseName: homework.course?.courseName || '未知课程'
            }
          }
        })
      )

      homeworkList.value = homeworksWithSubmission
      total.value = (response as any).total || 0

      // 提取课程列表
      const courseMap = new Map()
      homeworksWithSubmission.forEach((h: any) => {
        if (h.courseId && h.courseName && !courseMap.has(h.courseId)) {
          courseMap.set(h.courseId, { id: h.courseId, courseName: h.courseName })
        }
      })
      courses.value = Array.from(courseMap.values())

      // 计算统计数据
      calculateStats()
    }
  } catch (error: any) {
    console.error('加载作业列表失败:', error)
    ElMessage.error('加载作业列表失败')
  } finally {
    loading.value = false
  }
}

// 计算统计数据
const calculateStats = () => {
  const pending = homeworkList.value.filter(h => !h.submission).length
  const submitted = homeworkList.value.filter(h =>
    h.submission && h.submission.status === 'submitted'
  ).length
  const graded = homeworkList.value.filter(h =>
    h.submission && h.submission.status === 'graded'
  )

  const completedCount = graded.length
  const averageScore = graded.length > 0
    ? (graded.reduce((sum, h) => sum + (h.submission?.score || 0), 0) / graded.length).toFixed(1)
    : 0

  stats.value = {
    pendingCount: pending,
    submittedCount: submitted,
    completedCount: completedCount,
    averageScore: Number(averageScore)
  }
}

// 搜索处理
const handleSearch = () => {
  // 搜索逻辑由 computed 自动处理
}

// 标签切换
const handleTabChange = () => {
  // 标签筛选逻辑由 computed 自动处理
}

// 课程筛选
const handleCourseFilter = () => {
  // 课程筛选逻辑由 computed 自动处理
}

// 分页
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadHomeworkList()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadHomeworkList()
}

// 查看作业详情
const viewHomeworkDetail = (homework: any) => {
  currentHomework.value = homework
  detailDialogVisible.value = true
}

// 从详情对话框提交
const submitFromDetail = () => {
  detailDialogVisible.value = false
  submitHomework(currentHomework.value)
}

// 提交作业
const submitHomework = (homework: any) => {
  currentHomework.value = homework
  submitForm.value = {
    content: '',
    fileUrl: ''
  }
  fileList.value = []
  submitDialogVisible.value = true
}

// 文件选择
const handleFileChange = async (file: any) => {
  console.log('选择的文件:', file)

  // 上传文件到服务器
  const formData = new FormData()
  formData.append('file', file.raw)
  formData.append('type', 'homework') // 作业类型目录

  try {
    const response = await axios.post('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response && (response as any).url) {
      submitForm.value.fileUrl = (response as any).url
      ElMessage.success('文件上传成功')
    }
  } catch (error: any) {
    console.error('文件上传失败:', error)
    ElMessage.error(error.message || '文件上传失败')
  }
}

// 确认提交作业
const confirmSubmitHomework = async () => {
  if (!submitForm.value.content && fileList.value.length === 0) {
    ElMessage.warning('请输入作业内容或上传附件')
    return
  }

  submitting.value = true
  try {
    const studentId = getStudentId()
    await axios.post('/homework-submission', {
      homeworkId: currentHomework.value.id,
      studentId: studentId,
      content: submitForm.value.content,
      attachmentUrl: submitForm.value.fileUrl || null
    })

    ElMessage.success('作业提交成功')
    submitDialogVisible.value = false
    await loadHomeworkList()
  } catch (error: any) {
    console.error('提交作业失败:', error)
    ElMessage.error(error.message || '提交作业失败')
  } finally {
    submitting.value = false
  }
}

// 关闭提交对话框
const closeSubmitDialog = () => {
  submitDialogVisible.value = false
  currentHomework.value = null
  submitForm.value = {
    content: '',
    fileUrl: ''
  }
  fileList.value = []
}

// 查看提交详情
const viewSubmission = (homework: any) => {
  currentHomework.value = homework
  submissionDialogVisible.value = true
}

// 下载文件
const downloadFile = (fileUrl: string, defaultName: string) => {
  if (!fileUrl) {
    ElMessage.warning('文件地址不存在')
    return
  }

  try {
    // 从URL中提取文件名
    const urlParts = fileUrl.split('/')
    const fileName = urlParts[urlParts.length - 1] || defaultName

    // 创建下载链接
    const link = document.createElement('a')
    link.href = fileUrl
    link.download = fileName
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success('开始下载')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  }
}

onMounted(() => {
  loadHomeworkList()
})
</script>

<style scoped lang="scss">
.my-homework-page {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;

  .page-title {
    font-size: 2rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: var(--el-text-color-primary);
  }

  .page-subtitle {
    font-size: 1rem;
    color: var(--el-text-color-secondary);
    margin: 0;
  }

  .header-actions {
    display: flex;
    gap: 1rem;
  }
}

// 统计卡片
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    flex-shrink: 0;
  }

  .stat-content {
    flex: 1;
  }

  .stat-label {
    font-size: 0.875rem;
    color: var(--el-text-color-secondary);
    margin-bottom: 0.25rem;
  }

  .stat-value {
    font-size: 1.75rem;
    font-weight: 700;
    color: var(--el-text-color-primary);
  }

  &.stat-card-danger .stat-icon {
    background: oklch(0.95 0.02 25);
    color: oklch(0.60 0.18 25);
  }

  &.stat-card-warning .stat-icon {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }

  &.stat-card-success .stat-icon {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.stat-card-primary .stat-icon {
    background: oklch(0.95 0.02 220);
    color: oklch(0.55 0.15 220);
  }
}

// 筛选标签
.filter-tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

// 作业列表
.homework-container {
  min-height: 400px;
}

.homework-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 1.5rem;
}

.homework-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  position: relative;
  border-left: 4px solid transparent;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  &.card-pending {
    border-left-color: oklch(0.60 0.18 45);
  }

  &.card-submitted {
    border-left-color: oklch(0.60 0.18 45);
  }

  &.card-excellent {
    border-left-color: oklch(0.55 0.15 150);
    background: linear-gradient(to right, oklch(0.98 0.01 150), white);
  }

  &.card-good {
    border-left-color: oklch(0.55 0.15 220);
  }

  &.card-pass {
    border-left-color: oklch(0.60 0.18 45);
  }

  &.card-fail {
    border-left-color: oklch(0.60 0.18 25);
  }

  .homework-status-badge {
    position: absolute;
    top: 1rem;
    right: 1rem;
  }
}

.homework-info {
  .homework-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0.75rem;
    gap: 0.5rem;

    .homework-title {
      font-size: 1.125rem;
      font-weight: 600;
      margin: 0;
      color: var(--el-text-color-primary);
      line-height: 1.4;
      flex: 1;
      padding-right: 60px;
    }
  }

  .homework-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;
    margin-bottom: 0.75rem;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 0.375rem;
      font-size: 0.875rem;
      color: var(--el-text-color-secondary);
    }
  }

  .homework-description {
    font-size: 0.875rem;
    color: var(--el-text-color-secondary);
    line-height: 1.6;
    margin: 0 0 1rem 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    min-height: 2.8em;
  }

  .homework-type-tags {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1rem;
    flex-wrap: wrap;
  }

  .submission-info {
    background: oklch(0.98 0.01 220);
    border-radius: 8px;
    padding: 0.75rem;
    margin-bottom: 1rem;

    .submission-detail {
      .submission-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.5rem;
        font-size: 0.875rem;

        &.full-width {
          flex-direction: column;
          align-items: flex-start;
          gap: 0.5rem;
        }

        &:last-child {
          margin-bottom: 0;
        }

        .label {
          color: var(--el-text-color-secondary);
          font-weight: 500;
        }

        .value {
          color: var(--el-text-color-primary);
          font-weight: 600;

          &.score {
            font-size: 1.125rem;

            &.score-excellent { color: oklch(0.55 0.15 150); }
            &.score-good { color: oklch(0.55 0.15 220); }
            &.score-pass { color: oklch(0.60 0.18 45); }
            &.score-fail { color: oklch(0.60 0.18 25); }
          }
        }

        .comment {
          margin: 0;
          padding: 0.5rem;
          background: white;
          border-radius: 4px;
          font-size: 0.875rem;
          line-height: 1.6;
          color: var(--el-text-color-primary);
        }
      }
    }
  }
}

.homework-actions {
  display: flex;
  gap: 0.5rem;
}

// 对话框样式
.homework-detail-description {
  white-space: pre-wrap;
  line-height: 1.8;
  color: var(--el-text-color-regular);
}

.homework-description-text {
  color: var(--el-text-color-secondary);
  line-height: 1.6;
  white-space: pre-wrap;
}

.submission-content {
  white-space: pre-wrap;
  line-height: 1.8;
  max-height: 300px;
  overflow-y: auto;
}

.teacher-comment {
  background: oklch(0.98 0.01 150);
  border-left: 3px solid oklch(0.55 0.15 150);
  padding: 1rem;
  border-radius: 4px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.score-large {
  font-size: 1.5rem;
  font-weight: 700;

  &.score-excellent { color: oklch(0.55 0.15 150); }
  &.score-good { color: oklch(0.55 0.15 220); }
  &.score-pass { color: oklch(0.60 0.18 45); }
  &.score-fail { color: oklch(0.60 0.18 25); }
}

.text-danger {
  color: var(--el-color-danger);
}

// 分页
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

// 响应式设计
@media (max-width: 768px) {
  .my-homework-page {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    gap: 1rem;

    .header-actions {
      width: 100%;

      .el-input {
        width: 100% !important;
      }
    }
  }

  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }

  .filter-tabs {
    flex-direction: column;
    align-items: stretch;

    .el-select {
      width: 100% !important;
    }
  }

  .homework-grid {
    grid-template-columns: 1fr;
  }
}
</style>
