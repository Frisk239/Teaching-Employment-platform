<template>
  <div class="teacher-dashboard">
    <!-- 欢迎横幅 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card" shadow="never">
          <div class="welcome-content">
            <div class="welcome-info">
              <div class="welcome-icon">
                <el-icon :size="40"><User /></el-icon>
              </div>
              <div class="welcome-text">
                <h2>教师工作台</h2>
                <p class="welcome-greeting">欢迎回来，{{ authStore.userName || '教师' }}</p>
                <p class="date-info">{{ currentDate }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click="navigateTo('/teaching/courses')">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.myCourses || 0 }}</div>
              <div class="stat-label">我的课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" @click="navigateTo('/teaching/students')">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.myStudents || 0 }}</div>
              <div class="stat-label">我的学生</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card pending-card" @click="navigateTo('/teaching/homework-publish')">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingHomework || 0 }}</div>
              <div class="stat-label">待批改作业</div>
            </div>
          </div>
          <el-badge :value="stats.pendingHomework || 0" class="stat-badge" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><DocumentChecked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.gradedHomework || 0 }}</div>
              <div class="stat-label">已批改</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷功能 -->
    <el-row :gutter="20" class="quick-actions-row">
      <el-col :span="24">
        <el-card shadow="hover" class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="18" style="margin-right: 8px"><Lightning /></el-icon>
              <span>快捷功能</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="navigateTo('/teaching/homework-publish')">
              <div class="action-icon" style="background: #ecf5ff;">
                <el-icon :size="24"><DocumentAdd /></el-icon>
              </div>
              <span class="action-text">发布作业</span>
            </div>
            <div class="action-item" @click="navigateTo('/teaching/students')">
              <div class="action-icon" style="background: #f0f9ff;">
                <el-icon :size="24"><User /></el-icon>
              </div>
              <span class="action-text">学生管理</span>
            </div>
            <div class="action-item" @click="navigateTo('/teaching/daily-reports')">
              <div class="action-icon" style="background: #fef0f0;">
                <el-icon :size="24"><DocumentCopy /></el-icon>
              </div>
              <span class="action-text">日报管理</span>
            </div>
            <div class="action-item" @click="navigateTo('/teaching/courses')">
              <div class="action-icon" style="background: #fdf6ec;">
                <el-icon :size="24"><Reading /></el-icon>
              </div>
              <span class="action-text">课程管理</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="content-row">
      <!-- 待批改作业 -->
      <el-col :span="12">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="18" style="margin-right: 8px"><Document /></el-icon>
              <span>待批改作业</span>
              <el-button type="primary" text @click="navigateTo('/teaching/homework-publish')">查看全部</el-button>
            </div>
          </template>
          <div v-loading="loading" class="homework-list">
            <el-empty v-if="!pendingHomeworkList || pendingHomeworkList.length === 0" description="暂无待批改作业" :image-size="100" />
            <div v-else>
              <div v-for="item in pendingHomeworkList.slice(0, 5)" :key="item.id" class="homework-item">
                <div class="homework-info">
                  <div class="homework-title">{{ item.title }}</div>
                  <div class="homework-detail">
                    <el-tag size="small" type="info">{{ item.courseName }}</el-tag>
                    <span class="student-name">{{ item.studentName }}</span>
                    <span class="submit-time">{{ formatTime(item.submitTime) }}</span>
                  </div>
                </div>
                <el-button type="primary" size="small" @click="gradeHomework(item)">批改</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 我的课程 -->
      <el-col :span="12">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="18" style="margin-right: 8px"><Reading /></el-icon>
              <span>我的课程</span>
              <el-button type="primary" text @click="navigateTo('/teaching/courses')">查看全部</el-button>
            </div>
          </template>
          <div v-loading="loading" class="course-list">
            <el-empty v-if="!myCourses || myCourses.length === 0" description="暂无课程" :image-size="100" />
            <div v-else>
              <div v-for="course in myCourses.slice(0, 5)" :key="course.id" class="course-item" @click="navigateTo('/teaching/courses')">
                <div class="course-info">
                  <div class="course-name">{{ course.name || course.courseName }}</div>
                  <div class="course-detail">
                    <el-tag size="small">{{ course.type || course.courseType }}</el-tag>
                    <span class="student-count">{{ course.students || course.studentCount || 0 }} 学生</span>
                    <span class="homework-count">{{ course.homework || 0 }} 作业</span>
                  </div>
                </div>
                <el-icon :size="20" color="#909399"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 批改作业对话框 -->
    <el-dialog v-model="gradeDialogVisible" title="批改作业" width="600px">
      <el-form :model="gradeForm" label-width="100px">
        <el-form-item label="作业标题">
          <el-input v-model="gradeForm.title" disabled />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="gradeForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="分数" required>
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="gradeForm.comment" type="textarea" :rows="4" placeholder="请输入评语..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGrade" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores'
import { ElMessage } from 'element-plus'
import {
  Reading,
  User,
  Document,
  DocumentChecked,
  DocumentAdd,
  DocumentCopy,
  ArrowRight,
  Lightning
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const authStore = useAuthStore()
const router = useRouter()

const stats = ref({
  myCourses: 0,
  myStudents: 0,
  pendingHomework: 0,
  gradedHomework: 0
})

const myCourses = ref<any[]>([])
const pendingHomeworkList = ref<any[]>([])
const loading = ref(false)
const currentDate = ref('')

const gradeDialogVisible = ref(false)
const submitting = ref(false)
const gradeForm = ref({
  submissionId: null as number | null,
  title: '',
  studentName: '',
  score: 0,
  comment: ''
})

const teacherId = ref(authStore.user?.teacherId || authStore.user?.id || 1)
let timeInterval: any = null

// 更新当前时间
const updateDateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const day = now.getDate()
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekDay = weekDays[now.getDay()]
  const hour = now.getHours()
  let greeting = ''
  if (hour < 6) greeting = '凌晨好'
  else if (hour < 12) greeting = '早上好'
  else if (hour < 18) greeting = '下午好'
  else greeting = '晚上好'

  currentDate.value = `${year}年${month}月${day}日 ${weekDay}，${greeting}`
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return time.split('T')[0]
}

// 导航到指定页面
const navigateTo = (path: string) => {
  router.push(path)
}

// 批改作业
const gradeHomework = (item: any) => {
  gradeForm.value = {
    submissionId: item.id,
    title: item.title,
    studentName: item.studentName,
    score: 0,
    comment: ''
  }
  gradeDialogVisible.value = true
}

// 提交批改
const submitGrade = async () => {
  if (gradeForm.value.score === null || gradeForm.value.score === undefined) {
    ElMessage.warning('请输入分数')
    return
  }

  submitting.value = true
  try {
    await request.post('/teacher-dashboard/grade-homework', {
      submissionId: gradeForm.value.submissionId,
      score: gradeForm.value.score,
      comment: gradeForm.value.comment
    })

    ElMessage.success('批改成功')
    gradeDialogVisible.value = false

    // 重新加载数据
    await loadPendingHomework()
    await loadStats()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '批改失败')
  } finally {
    submitting.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    console.log('正在加载统计数据，teacherId:', teacherId.value)
    const response = await request.get(`/teacher-dashboard/stats/${teacherId.value}`)
    console.log('统计数据响应:', response)
    stats.value = response || {
      myCourses: 0,
      myStudents: 0,
      pendingHomework: 0,
      gradedHomework: 0
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
    console.error('错误详情:', error.response?.data)
  }
}

// 加载课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    console.log('正在加载课程列表，teacherId:', teacherId.value)
    const response = await request.get(`/teacher-dashboard/courses/${teacherId.value}`)
    console.log('课程列表响应:', response)
    myCourses.value = response || []
  } catch (error: any) {
    console.error('加载课程列表失败:', error)
    console.error('错误详情:', error.response?.data)
  } finally {
    loading.value = false
  }
}

// 加载待批改作业
const loadPendingHomework = async () => {
  try {
    console.log('正在加载待批改作业，teacherId:', teacherId.value)
    const response = await request.get(`/teacher-dashboard/pending-homework/${teacherId.value}`, {
      params: { current: 1, size: 10 }
    })
    console.log('待批改作业响应:', response)
    // response是分页对象，包含records, total等字段
    pendingHomeworkList.value = response?.records || []
  } catch (error: any) {
    console.error('加载待批改作业失败:', error)
    console.error('错误详情:', error.response?.data)
  }
}

onMounted(async () => {
  console.log('教师Dashboard已挂载，teacherId:', teacherId.value)
  console.log('当前用户信息:', authStore.user)

  updateDateTime()
  timeInterval = setInterval(updateDateTime, 60000)

  await Promise.all([
    loadStats(),
    loadCourses(),
    loadPendingHomework()
  ])
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style lang="scss" scoped>
.teacher-dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.welcome-card {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;

  :deep(.el-card__body) {
    padding: 24px;
  }

  .welcome-content {
    .welcome-info {
      display: flex;
      align-items: center;
      gap: 20px;

      .welcome-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        background-color: #ecf5ff;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #409eff;
      }

      .welcome-text {
        flex: 1;

        h2 {
          margin: 0 0 8px 0;
          font-size: 20px;
          font-weight: 600;
          color: #303133;
        }

        .welcome-greeting {
          margin: 0 0 4px 0;
          font-size: 14px;
          color: #606266;
        }

        .date-info {
          margin: 0;
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    border-color: #409eff;
  }

  :deep(.el-card__body) {
    padding: 20px;
  }

  .stat-content {
    display: flex;
    align-items: center;
    gap: 15px;

    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
        line-height: 1;
      }

      .stat-label {
        font-size: 13px;
        color: #909399;
      }
    }
  }

  &.pending-card {
    position: relative;

    .stat-badge {
      position: absolute;
      top: 10px;
      right: 10px;
    }
  }
}

.quick-actions-row {
  margin-bottom: 20px;
}

.quick-actions-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;

  :deep(.el-card__body) {
    padding: 20px;
  }

  :deep(.el-card__header) {
    border-bottom: 1px solid #e4e7ed;
    padding: 16px 20px;
  }
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  gap: 20px;

  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 16px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    flex: 1;

    &:hover {
      background-color: #f5f7fa;
      transform: translateY(-2px);
    }

    .action-icon {
      width: 50px;
      height: 50px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .action-text {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
  }
}

.content-row {
  margin-bottom: 20px;
}

.content-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;

  :deep(.el-card__body) {
    padding: 20px;
  }

  :deep(.el-card__header) {
    border-bottom: 1px solid #e4e7ed;
    padding: 16px 20px;
  }
}

.homework-list,
.course-list {
  min-height: 200px;
}

.homework-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 12px;
  background-color: #f5f7fa;
  transition: all 0.3s ease;

  &:hover {
    background-color: #e9ecef;
  }

  &:last-child {
    margin-bottom: 0;
  }

  .homework-info {
    flex: 1;

    .homework-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }

    .homework-detail {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 13px;
      color: #606266;

      .student-name {
        color: #409eff;
        font-weight: 500;
      }

      .submit-time {
        color: #909399;
      }
    }
  }
}

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 12px;
  background-color: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: #e9ecef;
    transform: translateX(4px);
  }

  &:last-child {
    margin-bottom: 0;
  }

  .course-info {
    flex: 1;

    .course-name {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }

    .course-detail {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 13px;
      color: #606266;

      .student-count,
      .homework-count {
        color: #909399;
      }
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}
</style>
