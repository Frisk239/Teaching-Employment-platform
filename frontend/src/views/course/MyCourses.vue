<template>
  <div class="my-courses-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">我的课程</h1>
        <p class="page-subtitle">查看和管理你的课程学习进度</p>
      </div>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称或代码..."
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
      <div class="stat-card stat-card-primary">
        <div class="stat-icon">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">在学课程</div>
          <div class="stat-value">{{ stats.inProgressCourses || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-success">
        <div class="stat-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已完成课程</div>
          <div class="stat-value">{{ stats.completedCourses || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-warning">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待完成作业</div>
          <div class="stat-value">{{ stats.pendingHomework || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-info">
        <div class="stat-icon">
          <el-icon><Trophy /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">获得学分</div>
          <div class="stat-value">{{ stats.totalCredits || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- 课程列表 -->
    <div v-loading="loading" class="courses-container">
      <el-empty v-if="!loading && filteredCourses.length === 0" description="暂无课程数据">
        <el-button type="primary" @click="$router.push('/course/list')">浏览可选课程</el-button>
      </el-empty>

      <div v-else class="courses-grid">
        <div
          v-for="course in filteredCourses"
          :key="course.id"
          class="course-card"
        >
          <!-- 课程封面 -->
          <div class="course-cover">
            <div class="cover-placeholder">
              <el-icon class="cover-icon"><Reading /></el-icon>
            </div>
            <div class="course-type-badge">{{ course.type || '必修' }}</div>
          </div>

          <!-- 课程信息 -->
          <div class="course-info">
            <div class="course-header">
              <h3 class="course-name">{{ course.name }}</h3>
              <el-tag :type="getProgressType(course.progress)" size="small">
                {{ Math.round(course.progress || 0) }}% 完成
              </el-tag>
            </div>

            <div class="course-meta">
              <span class="meta-item">
                <el-icon><DocumentCopy /></el-icon>
                {{ course.code }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ course.totalHours || 0 }} 课时
              </span>
              <span class="meta-item">
                <el-icon><Star /></el-icon>
                {{ course.credit || 0 }} 学分
              </span>
            </div>

            <p class="course-description">{{ course.description || '暂无描述' }}</p>

            <!-- 进度条 -->
            <div class="progress-section">
              <div class="progress-header">
                <span class="progress-label">学习进度</span>
                <span class="progress-value">{{ Math.round(course.progress || 0) }}%</span>
              </div>
              <el-progress
                :percentage="Math.round(course.progress || 0)"
                :stroke-width="8"
                :show-text="false"
                :color="getProgressColor(course.progress)"
              />
            </div>

            <!-- 课程统计 -->
            <div class="course-stats">
              <div class="stat-item">
                <el-icon class="stat-icon"><Document /></el-icon>
                <span>{{ course.homeworkCount || 0 }} 个作业</span>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><User /></el-icon>
                <span>{{ course.teacherName || '教师' }}</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="course-actions">
              <el-button type="primary" size="small" @click="viewCourseDetail(course)">
                查看详情
              </el-button>
              <el-button size="small" @click="viewCourseHomework(course)">
                查看作业
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedCourse?.courseName || '课程详情'"
      width="800px"
      @close="closeDetailDialog"
    >
      <div v-if="selectedCourse" v-loading="loadingDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">
            {{ selectedCourse.courseName }}
          </el-descriptions-item>
          <el-descriptions-item label="课程代码">
            {{ selectedCourse.courseCode }}
          </el-descriptions-item>
          <el-descriptions-item label="授课教师">
            {{ selectedCourse.teacherName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="所属学校">
            {{ selectedCourse.schoolName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="教室">
            {{ selectedCourse.classroomName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="学分">
            {{ selectedCourse.credit || 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="总课时">
            {{ selectedCourse.totalHours || 0 }} 课时
          </el-descriptions-item>
          <el-descriptions-item label="开课日期">
            {{ selectedCourse.startDate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="结课日期">
            {{ selectedCourse.endDate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="课程状态" :span="2">
            <el-tag :type="getStatusType(selectedCourse.status)">
              {{ getStatusText(selectedCourse.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">
            {{ selectedCourse.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 学习进度 -->
        <div class="detail-progress" v-if="courseProgress !== null">
          <h4>学习进度</h4>
          <el-progress
            :percentage="Math.round(courseProgress || 0)"
            :stroke-width="20"
            :color="getProgressColor(courseProgress)"
          />
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="viewCourseHomeworkFromDetail">
          查看作业
        </el-button>
      </template>
    </el-dialog>

    <!-- 课程作业对话框 -->
    <el-dialog
      v-model="homeworkDialogVisible"
      :title="`作业列表 - ${selectedCourse?.name || selectedCourse?.courseName || ''}`"
      width="900px"
      @close="closeHomeworkDialog"
    >
      <div v-loading="loadingHomework">
        <el-table :data="homeworkList" stripe>
          <el-table-column prop="title" label="作业标题" min-width="200" />
          <el-table-column prop="description" label="作业描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="deadline" label="截止时间" width="180">
            <template #default="{ row }">
              <span :class="{ 'text-danger': row.urgent }">
                {{ formatDateTime(row.deadline) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="maxScore" label="满分" width="80" align="center" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.submitted ? 'success' : 'warning'" size="small">
                {{ row.submitted ? '已提交' : '未提交' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="!row.submitted"
                type="primary"
                size="small"
                link
                @click="submitHomework(row)"
              >
                提交作业
              </el-button>
              <el-button
                v-else
                type="info"
                size="small"
                link
                @click="viewHomeworkDetail(row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!loadingHomework && homeworkList.length === 0" description="暂无作业" />
      </div>

      <template #footer>
        <el-button @click="homeworkDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 提交作业对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交作业"
      width="600px"
      @close="closeSubmitDialog"
    >
      <el-form :model="submitForm" label-width="100px">
        <el-form-item label="作业标题">
          <span>{{ currentHomework?.title }}</span>
        </el-form-item>
        <el-form-item label="作业描述">
          <div class="homework-description">{{ currentHomework?.description }}</div>
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input
            v-model="submitForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入作业内容或答案..."
          />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            @change="handleFileChange"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持doc/docx/pdf/txt/zip等格式，文件大小不超过10MB
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Reading,
  Search,
  CircleCheck,
  Document,
  Trophy,
  DocumentCopy,
  Clock,
  Star,
  User,
  UploadFilled
} from '@element-plus/icons-vue'
import axios from '@/utils/request'
import { useAuthStore } from '@/stores'
import dayjs from 'dayjs'

const authStore = useAuthStore()

// 状态
const loading = ref(false)
const loadingDetail = ref(false)
const loadingHomework = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const courses = ref<any[]>([])
const stats = ref({
  inProgressCourses: 0,
  completedCourses: 0,
  pendingHomework: 0,
  totalCredits: 0
})

// 对话框状态
const detailDialogVisible = ref(false)
const homeworkDialogVisible = ref(false)
const submitDialogVisible = ref(false)

// 选中的课程和作业
const selectedCourse = ref<any>(null)
const courseProgress = ref<number | null>(null)
const homeworkList = ref<any[]>([])
const currentHomework = ref<any>(null)

// 提交作业表单
const submitForm = ref({
  content: '',
  fileUrl: ''
})

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

// 过滤后的课程列表
const filteredCourses = computed(() => {
  if (!searchKeyword.value) {
    return courses.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return courses.value.filter(course =>
    course.name?.toLowerCase().includes(keyword) ||
    course.code?.toLowerCase().includes(keyword) ||
    course.courseName?.toLowerCase().includes(keyword) ||
    course.courseCode?.toLowerCase().includes(keyword)
  )
})

// 进度颜色
const getProgressColor = (progress: number) => {
  if (progress >= 80) return '#67c23a'
  if (progress >= 50) return '#409eff'
  if (progress >= 20) return '#e6a23c'
  return '#f56c6c'
}

// 进度类型
const getProgressType = (progress: number) => {
  if (progress >= 80) return 'success'
  if (progress >= 50) return 'primary'
  if (progress >= 20) return 'warning'
  return 'danger'
}

// 状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, any> = {
    active: 'success',
    ongoing: 'success',
    completed: 'info',
    pending: 'warning',
    cancelled: 'danger'
  }
  return statusMap[status] || 'info'
}

// 状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    active: '进行中',
    ongoing: '进行中',
    completed: '已完成',
    pending: '未开始',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm')
}

// 搜索处理
const handleSearch = () => {
  // 搜索逻辑由 computed 自动处理
}

// 查看课程详情
const viewCourseDetail = async (course: any) => {
  selectedCourse.value = course
  detailDialogVisible.value = true

  // 加载课程详细信息
  await loadCourseDetail(course.id)
}

// 加载课程详细信息
const loadCourseDetail = async (courseId: number) => {
  loadingDetail.value = true
  try {
    const studentId = getStudentId()
    // 使用学员专用的课程详情接口,返回学员的学习进度
    const response = await axios.get(`/student-dashboard/course/${courseId}/student/${studentId}`)
    if (response) {
      selectedCourse.value = response
      // 从CourseStudent表获取的实际进度
      courseProgress.value = (response as any).progress || 0
    }
  } catch (error: any) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('加载课程详情失败')
  } finally {
    loadingDetail.value = false
  }
}

// 关闭详情对话框
const closeDetailDialog = () => {
  detailDialogVisible.value = false
  selectedCourse.value = null
  courseProgress.value = null
}

// 查看课程作业
const viewCourseHomework = async (course: any) => {
  selectedCourse.value = course
  homeworkDialogVisible.value = true
  await loadCourseHomework(course.id)
}

// 从详情对话框查看作业
const viewCourseHomeworkFromDetail = () => {
  detailDialogVisible.value = false
  homeworkDialogVisible.value = true
  loadCourseHomework(selectedCourse.value.id)
}

// 加载课程作业列表
const loadCourseHomework = async (courseId: number) => {
  loadingHomework.value = true
  try {
    const studentId = getStudentId()
    // 获取该课程的所有作业（分页数据）
    const response = await axios.get(`/homework/course/${courseId}`, {
      params: { current: 1, size: 100 }
    })

    // 处理分页响应
    const homeworkListData = (response as any).records || []

    // 为每个作业检查提交状态
    const homeworksWithStatus = await Promise.all(
      homeworkListData.map(async (homework: any) => {
        // 使用StudentDashboard的pending-homework API来检查提交状态
        try {
          const pendingResponse = await axios.get(`/student-dashboard/pending-homework/${studentId}`, {
            params: { current: 1, size: 1000 }
          })

          if (pendingResponse && (pendingResponse as any).records) {
            const submittedHw = (pendingResponse as any).records.find((ph: any) => ph.id === homework.id)
            return {
              ...homework,
              submitted: submittedHw ? submittedHw.submitted : false,
              score: submittedHw ? submittedHw.score : undefined,
              urgent: homework.deadline && dayjs(homework.deadline).isBefore(dayjs().add(3, 'day'))
            }
          }
        } catch (e) {
          console.error('检查提交状态失败:', e)
        }

        return {
          ...homework,
          submitted: false,
          urgent: homework.deadline && dayjs(homework.deadline).isBefore(dayjs().add(3, 'day'))
        }
      })
    )

    homeworkList.value = homeworksWithStatus
  } catch (error: any) {
    console.error('加载作业列表失败:', error)
    // 降级：使用待办作业API过滤
    loadPendingHomeworkForCourse(courseId)
  } finally {
    loadingHomework.value = false
  }
}

// 使用待办作业API加载课程作业
const loadPendingHomeworkForCourse = async (courseId: number) => {
  try {
    const studentId = getStudentId()
    const response = await axios.get(`/student-dashboard/pending-homework/${studentId}`, {
      params: { current: 1, size: 100 }
    })

    if (response && (response as any).records) {
      homeworkList.value = (response as any).records.filter((hw: any) => hw.courseId === courseId)
    } else {
      homeworkList.value = []
    }
  } catch (error: any) {
    console.error('加载作业列表失败:', error)
    homeworkList.value = []
  }
}

// 关闭作业对话框
const closeHomeworkDialog = () => {
  homeworkDialogVisible.value = false
  homeworkList.value = []
}

// 提交作业
const submitHomework = (homework: any) => {
  currentHomework.value = homework
  submitForm.value = {
    content: '',
    fileUrl: ''
  }
  submitDialogVisible.value = true
}

// 查看作业详情
const viewHomeworkDetail = (homework: any) => {
  ElMessageBox.alert(`
    <div style="line-height: 1.8;">
      <p><strong>作业标题:</strong> ${homework.title}</p>
      <p><strong>作业描述:</strong> ${homework.description || '无'}</p>
      <p><strong>满分:</strong> ${homework.maxScore}</p>
      <p><strong>截止时间:</strong> ${formatDateTime(homework.deadline)}</p>
      ${homework.submitted && homework.score !== undefined ? `<p><strong>得分:</strong> ${homework.score}</p>` : ''}
    </div>
  `, '作业详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭'
  })
}

// 处理文件选择
const handleFileChange = (file: any) => {
  console.log('选择的文件:', file)
  // 这里可以添加文件上传逻辑
}

// 确认提交作业
const confirmSubmitHomework = async () => {
  if (!submitForm.value.content && !submitForm.value.fileUrl) {
    ElMessage.warning('请输入作业内容或上传附件')
    return
  }

  submitting.value = true
  try {
    const studentId = getStudentId()
    await axios.post('/student-dashboard/submit-homework', {
      homeworkId: currentHomework.value.id,
      studentId: studentId,
      content: submitForm.value.content,
      fileUrl: submitForm.value.fileUrl
    })

    ElMessage.success('作业提交成功')
    submitDialogVisible.value = false

    // 刷新作业列表
    if (selectedCourse.value) {
      await loadCourseHomework(selectedCourse.value.id)
    }
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
}

// 加载统计数据
const loadStats = async () => {
  try {
    const studentId = getStudentId()
    const response = await axios.get(`/student-dashboard/stats/${studentId}`)
    if (response) {
      stats.value = response as any
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const studentId = getStudentId()
    const response = await axios.get(`/student-dashboard/courses/${studentId}`)

    if (response && Array.isArray(response)) {
      courses.value = response
    }
  } catch (error: any) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
  loadCourses()
})
</script>

<style scoped lang="scss">
.my-courses-page {
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

  &.stat-card-primary .stat-icon {
    background: oklch(0.95 0.02 220);
    color: oklch(0.55 0.15 220);
  }

  &.stat-card-success .stat-icon {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.stat-card-warning .stat-icon {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }

  &.stat-card-info .stat-icon {
    background: oklch(0.92 0.04 280);
    color: oklch(0.55 0.15 280);
  }
}

// 课程列表
.courses-container {
  min-height: 400px;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.course-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .course-cover {
    position: relative;
    height: 160px;
    background: linear-gradient(135deg, oklch(0.95 0.02 220) 0%, oklch(0.98 0.01 220) 100%);

    .cover-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;

      .cover-icon {
        font-size: 64px;
        color: oklch(0.70 0.10 220);
      }
    }

    .course-type-badge {
      position: absolute;
      top: 1rem;
      right: 1rem;
      padding: 0.375rem 0.75rem;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 20px;
      font-size: 0.75rem;
      font-weight: 600;
      color: var(--el-color-primary);
      backdrop-filter: blur(4px);
    }
  }

  .course-info {
    padding: 1.5rem;
  }

  .course-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0.75rem;
    gap: 0.5rem;

    .course-name {
      font-size: 1.125rem;
      font-weight: 600;
      margin: 0;
      color: var(--el-text-color-primary);
      line-height: 1.4;
      flex: 1;
    }
  }

  .course-meta {
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

      .el-icon {
        font-size: 16px;
      }
    }
  }

  .course-description {
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

  .progress-section {
    margin-bottom: 1rem;

    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 0.5rem;

      .progress-label {
        font-size: 0.875rem;
        color: var(--el-text-color-secondary);
      }

      .progress-value {
        font-size: 0.875rem;
        font-weight: 600;
        color: var(--el-color-primary);
      }
    }
  }

  .course-stats {
    display: flex;
    gap: 1rem;
    margin-bottom: 1rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid var(--el-border-color-lighter);

    .stat-item {
      display: flex;
      align-items: center;
      gap: 0.375rem;
      font-size: 0.875rem;
      color: var(--el-text-color-secondary);

      .stat-icon {
        font-size: 16px;
      }
    }
  }

  .course-actions {
    display: flex;
    gap: 0.5rem;
  }
}

// 详情对话框
.detail-progress {
  margin-top: 1.5rem;

  h4 {
    font-size: 1rem;
    font-weight: 600;
    margin-bottom: 1rem;
    color: var(--el-text-color-primary);
  }
}

.homework-description {
  color: var(--el-text-color-secondary);
  line-height: 1.6;
  white-space: pre-wrap;
}

.text-danger {
  color: var(--el-color-danger);
}

// 响应式设计
@media (max-width: 768px) {
  .my-courses-page {
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

  .courses-grid {
    grid-template-columns: 1fr;
  }
}
</style>
