<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

// 恢复登录状态并验证token
const authStore = useAuthStore()

onMounted(async () => {
  await authStore.restoreAuth()
})
</script>

<style lang="scss">
// 全局样式已在 global.scss 中定义
</style>
