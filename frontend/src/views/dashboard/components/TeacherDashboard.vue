<template>
  <div class="teacher-dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-banner">
          <h2>👨‍🏫 教师工作台</h2>
          <p>欢迎回来,{{ authStore.userName }}</p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.myCourses }}</div>
              <div class="stat-label">我的课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.myStudents }}</div>
              <div class="stat-label">我的学生</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingHomework }}</div>
              <div class="stat-label">待批改作业</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon><DocumentChecked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.gradedHomework }}</div>
              <div class="stat-label">已批改</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>📚 我的课程</span>
              <el-button type="primary" text @click="$router.push('/teacher/courses')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="myCourses" style="width: 100%" v-loading="loading">
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="courseCode" label="课程代码" width="120" />
            <el-table-column prop="courseType" label="课程类型" width="120" />
            <el-table-column prop="studentCount" label="学生数" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores'
import { teacherDashboardApi } from '@/api/teacher'
import { Reading, User, Document, DocumentChecked } from '@element-plus/icons-vue'

const authStore = useAuthStore()

const stats = ref({
  myCourses: 0,
  myStudents: 0,
  pendingHomework: 0,
  gradedHomework: 0
})

const myCourses = ref<any[]>([])
const loading = ref(false)

const teacherId = ref(authStore.user?.teacherId || authStore.user?.id || 1)

onMounted(async () => {
  try {
    const data = await teacherDashboardApi.getStats(teacherId.value)
    stats.value = data

    const courses = await teacherDashboardApi.getCourses(teacherId.value)
    myCourses.value = courses.slice(0, 5)
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
</script>

<style lang="scss" scoped>
.teacher-dashboard {
  padding: 20px;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;

  h2 {
    margin: 0 0 10px 0;
    font-size: 24px;
  }

  p {
    margin: 0;
    opacity: 0.9;
  }
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    gap: 15px;

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 5px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.content-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
</style>
