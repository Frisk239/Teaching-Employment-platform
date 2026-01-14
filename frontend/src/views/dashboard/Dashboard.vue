<template>
  <div class="dashboard-page">
    <!-- 根据用户角色显示不同的Dashboard -->
    <component :is="dashboardComponent" />
  </div>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted } from 'vue'
import { useAuthStore } from '@/stores'

const authStore = useAuthStore()

// 根据用户角色动态加载对应的Dashboard组件
const dashboardComponent = computed(() => {
  const role = authStore.userRole

  console.log('=== Dashboard.vue ===')
  console.log('用户角色:', role)
  console.log('用户信息:', authStore.user)
  console.log('是否登录:', authStore.isLoggedIn)

  switch (role) {
    case 'admin':
      return defineAsyncComponent(() => import('./components/AdminDashboard.vue'))
    case 'college_head':
      return defineAsyncComponent(() => import('./components/CollegeHeadDashboard.vue'))
    case 'teacher':
      return defineAsyncComponent(() => import('./components/TeacherDashboard.vue'))
    case 'user':
      return defineAsyncComponent(() => import('./components/StudentDashboard.vue'))
    case 'enterprise_contact':
      return defineAsyncComponent(() => import('./components/EnterpriseDashboard.vue'))
    default:
      // 默认显示学员Dashboard
      console.log('使用默认学员Dashboard')
      return defineAsyncComponent(() => import('./components/StudentDashboard.vue'))
  }
})

onMounted(() => {
  console.log('=== Dashboard 组件已挂载 ===')
  console.log('userRole:', authStore.userRole)
  console.log('user:', authStore.user)
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  width: 100%;
  // 样式由各个子组件自己管理
}
</style>
