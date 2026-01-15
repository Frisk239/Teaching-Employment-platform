<template>
  <div class="my-offers-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">我的Offer</span>
        </div>
      </template>
      <el-empty description="Offer管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: 实现Offer管理功能
</script>

<style scoped lang="scss">
.my-offers-page {
  .card-header {
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}
</style>
