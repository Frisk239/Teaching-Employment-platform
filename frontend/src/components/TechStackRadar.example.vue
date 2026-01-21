<template>
  <div class="tech-stack-radar-example">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">技术栈雷达图使用示例</span>
        </div>
      </template>

      <!-- 基础示例 -->
      <div class="example-section">
        <h3>基础示例 - 单系列</h3>
        <TechStackRadar
          :data="basicData"
          :series="[
            {
              name: '学员能力',
              data: [85, 90, 75, 80, 70],
              color: 'oklch(0.65 0.18 45)'
            }
          ]"
          title="学员技术栈评估"
          height="400px"
        />
      </div>

      <el-divider />

      <!-- 多系列对比 -->
      <div class="example-section">
        <h3>多系列对比 - 职位要求 vs 学员能力</h3>
        <TechStackRadar
          :data="techData"
          :series="[
            {
              name: '职位要求',
              data: [85, 90, 75, 80, 70, 88],
              color: 'oklch(0.65 0.18 45)'
            },
            {
              name: '学员能力',
              data: [75, 80, 70, 85, 65, 82],
              color: 'oklch(0.60 0.15 150)'
            }
          ]"
          title="技术栈匹配度分析"
          height="450px"
        />
      </div>

      <el-divider />

      <!-- 响应式示例 -->
      <div class="example-section">
        <h3>响应式设计 - 自适应宽度</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <TechStackRadar
              :data="frontendData"
              :series="[
                {
                  name: '前端能力',
                  data: [90, 85, 80, 88, 75],
                  color: 'oklch(0.55 0.15 220)'
                }
              ]"
              title="前端技术栈"
              height="350px"
            />
          </el-col>
          <el-col :xs="24" :sm="12">
            <TechStackRadar
              :data="backendData"
              :series="[
                {
                  name: '后端能力',
                  data: [88, 92, 78, 85, 80],
                  color: 'oklch(0.70 0.15 85)'
                }
              ]"
              title="后端技术栈"
              height="350px"
            />
          </el-col>
        </el-row>
      </div>

      <el-divider />

      <!-- 代码示例 -->
      <div class="code-example">
        <h3>代码示例</h3>
        <el-collapse>
          <el-collapse-item title="基础用法" name="1">
            <pre><code>&lt;TechStackRadar
  :data="[
    { name: 'Java', max: 100, value: 85 },
    { name: 'Spring', max: 100, value: 90 },
    { name: 'MySQL', max: 100, value: 75 },
    { name: 'Vue', max: 100, value: 80 },
    { name: 'Git', max: 100, value: 70 }
  ]"
  :series="[
    {
      name: '学员能力',
      data: [85, 90, 75, 80, 70],
      color: 'oklch(0.65 0.18 45)'
    }
  ]"
  title="技术栈评估"
  height="400px"
/&gt;</code></pre>
          </el-collapse-item>

          <el-collapse-item title="多系列对比" name="2">
            <pre><code>&lt;TechStackRadar
  :data="techData"
  :series="[
    {
      name: '职位要求',
      data: [85, 90, 75, 80, 70],
      color: 'oklch(0.65 0.18 45)'
    },
    {
      name: '学员能力',
      data: [75, 80, 70, 85, 65],
      color: 'oklch(0.60 0.15 150)'
    }
  ]"
  title="技术栈匹配度分析"
  height="450px"
/&gt;

// TypeScript 数据定义
const techData = [
  { name: 'Java', max: 100, value: 85 },
  { name: 'Spring', max: 100, value: 90 },
  { name: 'MySQL', max: 100, value: 75 },
  { name: 'Vue', max: 100, value: 80 },
  { name: 'Git', max: 100, value: 70 }
]</code></pre>
          </el-collapse-item>

          <el-collapse-item title="TypeScript 类型定义" name="3">
            <pre><code>// 技术栈维度数据
interface TechStackData {
  name: string      // 技术维度名称
  max: number       // 该维度的最大值
  value: number     // 当前值
}

// 技术栈系列数据
interface TechStackSeries {
  name: string      // 系列名称
  data: number[]    // 各维度的值
  color?: string    // 线条颜色（可选）
}</code></pre>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { TechStackData } from './TechStackRadar.vue'

// 基础数据
const basicData = ref<TechStackData[]>([
  { name: 'Java', max: 100, value: 85 },
  { name: 'Spring', max: 100, value: 90 },
  { name: 'MySQL', max: 100, value: 75 },
  { name: 'Vue', max: 100, value: 80 },
  { name: 'Git', max: 100, value: 70 }
])

// 技术栈数据（多系列）
const techData = ref<TechStackData[]>([
  { name: 'Java', max: 100, value: 85 },
  { name: 'Spring Boot', max: 100, value: 90 },
  { name: 'MySQL', max: 100, value: 75 },
  { name: 'Vue.js', max: 100, value: 80 },
  { name: 'Git', max: 100, value: 70 },
  { name: 'Docker', max: 100, value: 88 }
])

// 前端技术栈
const frontendData = ref<TechStackData[]>([
  { name: 'Vue.js', max: 100, value: 90 },
  { name: 'TypeScript', max: 100, value: 85 },
  { name: 'CSS/SCSS', max: 100, value: 80 },
  { name: 'Vite', max: 100, value: 88 },
  { name: 'Element Plus', max: 100, value: 75 }
])

// 后端技术栈
const backendData = ref<TechStackData[]>([
  { name: 'Java', max: 100, value: 88 },
  { name: 'Spring Boot', max: 100, value: 92 },
  { name: 'MySQL', max: 100, value: 78 },
  { name: 'Redis', max: 100, value: 85 },
  { name: 'MyBatis', max: 100, value: 80 }
])
</script>

<style scoped lang="scss">
.tech-stack-radar-example {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      font-size: 18px;
      font-weight: 600;
      color: oklch(0.20 0.02 240); // --text-primary
    }
  }

  .example-section {
    margin-bottom: 40px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: oklch(0.20 0.02 240); // --text-primary
      margin-bottom: 20px;
    }
  }

  .code-example {
    h3 {
      font-size: 16px;
      font-weight: 600;
      color: oklch(0.20 0.02 240); // --text-primary
      margin-bottom: 20px;
    }

    pre {
      background: oklch(0.96 0.01 240); // --muted
      padding: 16px;
      border-radius: 8px;
      overflow-x: auto;
      margin: 0;

      code {
        font-family: 'JetBrains Mono', 'Consolas', monospace;
        font-size: 13px;
        line-height: 1.6;
        color: oklch(0.20 0.02 240); // --text-primary
      }
    }
  }

  :deep(.el-divider) {
    margin: 40px 0;
  }
}
</style>
