<template>
  <div class="timetable-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">我的课程表</h1>
        <p class="page-subtitle">查看每周课程安排</p>
      </div>
      <div class="header-actions">
        <el-select
          v-model="currentSemester"
          placeholder="选择学期"
          @change="loadTimetable"
          style="width: 200px; margin-right: 1rem;"
        >
          <el-option label="全部学期" :value="null" />
          <el-option
            v-for="semester in semesters"
            :key="semester.value"
            :label="semester.label"
            :value="semester.value"
          />
        </el-select>
        <el-button :icon="Refresh" @click="loadTimetable">刷新</el-button>
      </div>
    </div>

    <!-- 课程表 -->
    <div v-loading="loading" class="timetable-container">
      <el-empty v-if="!loading && isEmpty" description="暂无课程安排">
        <el-button type="primary" @click="$router.push('/teaching/my-courses')">查看我的课程</el-button>
      </el-empty>

      <div v-else class="timetable-wrapper">
        <table class="timetable-table">
          <thead>
            <tr>
              <th class="period-column">节次</th>
              <th v-for="day in weekDays" :key="day.value" :class="{ 'weekend': day.isWeekend }">
                {{ day.label }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="period in periods" :key="period.value">
              <td class="period-column">
                <div class="period-info">
                  <div class="period-number">第{{ period.value }}节</div>
                  <div class="period-time">{{ period.time }}</div>
                </div>
              </td>
              <td
                v-for="day in weekDays"
                :key="`${day.value}-${period.value}`"
                :class="{ 'weekend': day.isWeekend, 'has-course': hasCourse(day.value, period.value) }"
              >
                <div
                  v-if="hasCourse(day.value, period.value)"
                  class="course-cell"
                  @click="showCourseDetail(getCourse(day.value, period.value))"
                >
                  <div class="course-name">
                    {{ getCourse(day.value, period.value)?.course?.courseName }}
                  </div>
                  <div class="course-info">
                    <el-icon><Location /></el-icon>
                    {{ getCourse(day.value, period.value)?.classroom?.classroomName }}
                  </div>
                  <div class="course-info">
                    <el-icon><User /></el-icon>
                    {{ getCourse(day.value, period.value)?.teacher?.realName || getCourse(day.value, period.value)?.teacher?.name }}
                  </div>
                  <div class="course-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatTime(getCourse(day.value, period.value)) }}
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentCourse?.course?.courseName"
      width="600px"
    >
      <div v-if="currentCourse" v-loading="loadingDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">
            {{ currentCourse.course?.courseName }}
          </el-descriptions-item>
          <el-descriptions-item label="课程代码">
            {{ currentCourse.course?.courseCode }}
          </el-descriptions-item>
          <el-descriptions-item label="授课教师">
            {{ currentCourse.teacher?.realName || currentCourse.teacher?.name }}
          </el-descriptions-item>
          <el-descriptions-item label="教室">
            {{ currentCourse.classroom?.classroomName }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            {{ getWeekDayText(currentCourse.dayOfWeek) }} 第{{ currentCourse.period }}节
            ({{ formatTime(currentCourse) }})
          </el-descriptions-item>
          <el-descriptions-item label="学分">
            {{ currentCourse.course?.credit }}分
          </el-descriptions-item>
          <el-descriptions-item label="学时">
            {{ currentCourse.course?.totalHours }}学时
          </el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="1">
            <div class="course-description">
              {{ currentCourse.course?.description || '暂无描述' }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Location, User, Clock } from '@element-plus/icons-vue'
import axios from '@/utils/request'
import { useAuthStore } from '@/stores'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

// 状态
const loading = ref(false)
const loadingDetail = ref(false)
const currentSemester = ref<string>('2024-2025-1')
const timetableGrid = ref<Map<number, Map<number, any>>>(new Map())

// 对话框
const detailDialogVisible = ref(false)
const currentCourse = ref<any>(null)

// 配置
const weekDays = [
  { label: '周一', value: 1, isWeekend: false },
  { label: '周二', value: 2, isWeekend: false },
  { label: '周三', value: 3, isWeekend: false },
  { label: '周四', value: 4, isWeekend: false },
  { label: '周五', value: 5, isWeekend: false },
  { label: '周六', value: 6, isWeekend: true },
  { label: '周日', value: 7, isWeekend: true }
]

const periods = [
  { value: 1, time: '08:00-08:45' },
  { value: 2, time: '08:55-09:40' },
  { value: 3, time: '10:00-10:45' },
  { value: 4, time: '10:55-11:40' },
  { value: 5, time: '14:00-14:45' },
  { value: 6, time: '14:55-15:40' },
  { value: 7, time: '15:55-16:40' },
  { value: 8, time: '16:50-17:35' },
  { value: 9, time: '19:00-19:45' },
  { value: 10, time: '19:55-20:40' },
  { value: 11, time: '20:50-21:35' },
  { value: 12, time: '21:45-22:30' }
]

const semesters = [
  { label: '2024-2025学年 第一学期', value: '2024-2025-1' },
  { label: '2024-2025学年 第二学期', value: '2024-2025-2' },
  { label: '2023-2024学年 第一学期', value: '2023-2024-1' },
  { label: '2023-2024学年 第二学期', value: '2023-2024-2' }
]

// 计算属性
const isEmpty = computed(() => {
  if (!timetableGrid.value) return true
  for (const day in timetableGrid.value) {
    const dayMap = timetableGrid.value[day]
    if (dayMap && Object.keys(dayMap).length > 0) return false
  }
  return true
})

// 获取学生ID
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
  return 0
}

// 加载课程表
const loadTimetable = async () => {
  loading.value = true
  try {
    const studentId = getStudentId()
    const response = await axios.get(`/timetable/student/${studentId}/grid`, {
      params: {
        semester: currentSemester.value,
        academicYear: currentSemester.value?.split('-')[0] + '-' + currentSemester.value?.split('-')[1]
      }
    })

    if (response) {
      timetableGrid.value = response
      console.log('课程表数据:', response)
    }
  } catch (error: any) {
    console.error('加载课程表失败:', error)
    ElMessage.error(error.message || '加载课程表失败')
  } finally {
    loading.value = false
  }
}

// 判断某天某节是否有课
const hasCourse = (day: number, period: number): boolean => {
  const dayMap = timetableGrid.value[day]
  if (!dayMap) return false
  return dayMap[period] !== undefined
}

// 获取某天某节的信息
const getCourse = (day: number, period: number) => {
  const dayMap = timetableGrid.value[day]
  if (!dayMap) return null
  return dayMap[period]
}

// 显示课程详情
const showCourseDetail = async (course: any) => {
  currentCourse.value = course
  detailDialogVisible.value = true
}

// 格式化时间
const formatTime = (course: any): string => {
  if (!course) return '-'
  const start = course.startTime || ''
  const end = course.endTime || ''
  return `${start}-${end}`
}

// 获取星期文本
const getWeekDayText = (day: number): string => {
  const dayMap: Record<number, string> = {
    1: '周一',
    2: '周二',
    3: '周三',
    4: '周四',
    5: '周五',
    6: '周六',
    7: '周日'
  }
  return dayMap[day] || ''
}

// 进入课程
const goToCourse = () => {
  if (currentCourse.value?.course?.id) {
    detailDialogVisible.value = false
    router.push(`/teaching/my-courses`)
  }
}

onMounted(() => {
  loadTimetable()
})
</script>

<style scoped lang="scss">
.timetable-page {
  padding: 2rem;
  max-width: 1600px;
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

.timetable-container {
  min-height: 600px;
}

.timetable-wrapper {
  overflow-x: auto;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.timetable-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  min-width: 1200px;

  thead {
    background: oklch(0.95 0.01 220);

    th {
      padding: 1rem;
      text-align: center;
      font-weight: 600;
      color: var(--el-text-color-primary);
      border: 1px solid var(--el-border-color-lighter);
      min-width: 150px;

      &.period-column {
        min-width: 120px;
        background: oklch(0.90 0.02 220);
      }

      &.weekend {
        background: oklch(0.98 0.01 100);
        color: var(--el-text-color-secondary);
      }
    }
  }

  tbody {
    tr {
      &:hover {
        background: oklch(0.98 0.01 220);
      }

      td {
        padding: 0.75rem;
        text-align: center;
        border: 1px solid var(--el-border-color-lighter);
        min-height: 100px;
        vertical-align: middle;

        &.period-column {
          background: oklch(0.97 0.01 220);
          font-weight: 500;
        }

        &.weekend {
          background: oklch(0.99 0.005 100);
        }

        &.has-course {
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            background: oklch(0.95 0.02 220);
          }
        }
      }
    }
  }

  .period-info {
    .period-number {
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin-bottom: 0.25rem;
    }

    .period-time {
      font-size: 0.75rem;
      color: var(--el-text-color-secondary);
    }
  }

  .course-cell {
    padding: 0.5rem;
    background: oklch(0.92 0.04 150);
    border-radius: 8px;
    border-left: 3px solid oklch(0.65 0.15 150);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .course-name {
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin-bottom: 0.5rem;
      font-size: 0.9rem;
      line-height: 1.3;
    }

    .course-info {
      display: flex;
      align-items: center;
      gap: 0.25rem;
      font-size: 0.75rem;
      color: var(--el-text-color-secondary);
      margin-bottom: 0.25rem;

      .el-icon {
        font-size: 14px;
      }
    }

    .course-time {
      display: flex;
      align-items: center;
      gap: 0.25rem;
      font-size: 0.75rem;
      color: var(--el-color-primary);
      margin-top: 0.5rem;

      .el-icon {
        font-size: 14px;
      }
    }
  }
}

.course-description {
  white-space: pre-wrap;
  line-height: 1.8;
  color: var(--el-text-color-regular);
}

// 响应式设计
@media (max-width: 768px) {
  .timetable-page {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    gap: 1rem;

    .header-actions {
      width: 100%;

      .el-select {
        width: 100% !important;
      }
    }
  }

  .timetable-table {
    font-size: 0.875rem;

    thead th,
    tbody td {
      padding: 0.5rem;
      min-width: 100px;
    }

    .course-cell {
      .course-name {
        font-size: 0.8rem;
      }

      .course-info,
      .course-time {
        font-size: 0.7rem;
      }
    }
  }
}
</style>
