<template>
  <div class="assignments-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">我的作业</span>
        </div>
      </template>
      <el-empty description="作业管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: 实现作业管理功能
</script>

<style scoped lang="scss">
.assignments-page {
  .card-header {
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}
</style>
