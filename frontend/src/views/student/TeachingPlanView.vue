<template>
  <div class="teaching-plan-view">
    <div class="page-header">
      <el-button @click="handleBack">
        <el-icon><ArrowLeft /></el-icon>
        返回课程列表
      </el-button>
      <h1 class="page-title">{{ courseName }} - 教学计划</h1>
    </div>

    <!-- 课程信息 -->
    <el-card shadow="never" class="course-info" v-if="courseInfo">
      <div class="info-row">
        <span><strong>授课教师：</strong>{{ courseInfo.teacherName }}</span>
        <span><strong>教室：</strong>{{ courseInfo.classroomName }}</span>
        <span><strong>总课时：</strong>{{ courseInfo.totalHours }}</span>
      </div>
    </el-card>

    <!-- 教学计划时间线 -->
    <el-card shadow="never" v-loading="loading">
      <el-timeline v-if="plans.length > 0">
        <el-timeline-item
          v-for="plan in plans"
          :key="plan.id"
          :timestamp="formatTime(plan.timetable)"
          placement="top"
        >
          <el-card>
            <h3>第{{ plan.lessonNumber }}次课 - {{ plan.title }}</h3>
            <p v-if="plan.content" class="content">{{ plan.content }}</p>
            <div class="meta-info">
              <el-tag size="small">第{{ plan.weekNumber }}周</el-tag>
              <el-tag v-if="plan.timetable" size="small" type="info">
                {{ formatWeekTime(plan.timetable) }}
              </el-tag>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无教学计划" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { teachingPlanApi, type TeachingPlanVO } from '@/api/teachingPlan'
import { getCourseByIdApi } from '@/api/course'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const courseId = ref<number>(Number(route.params.courseId))
const courseName = ref('')
const courseInfo = ref<any>(null)
const plans = ref<TeachingPlanVO[]>([])

// 获取课程信息
const fetchCourseInfo = async () => {
  try {
    const response = await getCourseByIdApi(courseId.value) as any
    courseName.value = response.courseName
    courseInfo.value = response
  } catch (error) {
    console.error('获取课程信息失败:', error)
  }
}

// 获取教学计划
const fetchPlans = async () => {
  loading.value = true
  try {
    const response = await teachingPlanApi.getDetail(courseId.value) as any
    plans.value = response || []
  } catch (error) {
    console.error('获取教学计划失败:', error)
    ElMessage.error('获取教学计划失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间显示
const formatTime = (timetable: any) => {
  if (!timetable) return '时间待定'
  return `第${timetable.weekNumber}周`
}

const formatWeekTime = (timetable: any) => {
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${weekDays[timetable.dayOfWeek]} 第${timetable.startPeriod}-${timetable.endPeriod}节`
}

// 返回
const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchCourseInfo()
  fetchPlans()
})
</script>

<style scoped lang="scss">
.teaching-plan-view {
  padding: 20px;

  .page-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 20px;
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .course-info {
    margin-bottom: 20px;

    .info-row {
      display: flex;
      gap: 30px;

      span {
        font-size: 14px;
        color: #606266;
      }
    }
  }

  h3 {
    margin: 0 0 12px 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .content {
    margin: 12px 0;
    color: #606266;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  .meta-info {
    display: flex;
    gap: 8px;
  }
}
</style>
