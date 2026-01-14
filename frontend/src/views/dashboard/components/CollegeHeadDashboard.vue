<template>
  <div class="college-head-dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-banner">
          <h2>🎓 学院负责人工作台</h2>
          <p>欢迎回来,{{ authStore.userName }}</p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本院教师" :value="stats.teachers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本院学生" :value="stats.students" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="开设课程" :value="stats.courses" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="就业率" :value="stats.employmentRate" suffix="%" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>快捷操作</span>
          </template>
          <el-space wrap>
            <el-button type="primary">教师管理</el-button>
            <el-button type="success">课程管理</el-button>
            <el-button type="warning">学生管理</el-button>
            <el-button type="info">查看报表</el-button>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores'

const authStore = useAuthStore()

const stats = ref({
  teachers: 0,
  students: 0,
  courses: 0,
  employmentRate: 0
})

onMounted(() => {
  // TODO: 从API加载数据
  stats.value = {
    teachers: 25,
    students: 380,
    courses: 42,
    employmentRate: 87.5
  }
})
</script>

<style lang="scss" scoped>
.college-head-dashboard {
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

.content-row {
  margin-bottom: 20px;
}
</style>
