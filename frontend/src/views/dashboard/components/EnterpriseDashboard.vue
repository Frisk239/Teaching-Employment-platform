<template>
  <div class="enterprise-dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-banner">
          <h2>🏢 企业工作台</h2>
          <p>欢迎回来,{{ authStore.userName }}</p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="在招职位" :value="stats.activePositions" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="收到申请" :value="stats.totalApplications" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待处理" :value="stats.pending" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="已发Offer" :value="stats.offers" />
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
            <el-button type="primary" @click="$router.push('/positions/publish')">发布职位</el-button>
            <el-button type="success" @click="$router.push('/applications')">查看申请</el-button>
            <el-button type="warning" @click="$router.push('/interviews')">面试安排</el-button>
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
  activePositions: 0,
  totalApplications: 0,
  pending: 0,
  offers: 0
})

onMounted(() => {
  // TODO: 从API加载数据
  stats.value = {
    activePositions: 12,
    totalApplications: 156,
    pending: 23,
    offers: 8
  }
})
</script>

<style lang="scss" scoped>
.enterprise-dashboard {
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
