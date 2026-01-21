# TechStackRadar 技术栈雷达图组件

## 功能特性

- ✅ 基于 ECharts 5.x 的雷达图组件
- ✅ 支持单系列和多系列数据对比
- ✅ 完全响应式设计，自适应容器大小
- ✅ 使用项目 OKLCH 颜色系统
- ✅ TypeScript 类型定义
- ✅ 支持自定义配置（颜色、尺寸等）
- ✅ 自动销毁和内存管理
- ✅ 组件自动注册（unplugin-vue-components）

## 安装依赖

项目已安装 ECharts，无需额外安装：

```bash
npm install echarts@5.4.3
```

## 基础用法

### 单系列雷达图

```vue
<template>
  <TechStackRadar
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
  />
</template>

<script setup lang="ts">
import type { TechStackData } from '@/components/TechStackRadar.vue'

const techData: TechStackData[] = [
  { name: 'Java', max: 100, value: 85 },
  { name: 'Spring', max: 100, value: 90 },
  { name: 'MySQL', max: 100, value: 75 },
  { name: 'Vue', max: 100, value: 80 },
  { name: 'Git', max: 100, value: 70 }
]
</script>
```

### 多系列对比

```vue
<template>
  <TechStackRadar
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
  />
</template>
```

## Props 参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `data` | `TechStackData[]` | 是 | - | 技术栈维度数据 |
| `series` | `TechStackSeries[]` | 是 | - | 要显示的系列数据 |
| `title` | `string` | 否 | `''` | 图表标题 |
| `width` | `string \| number` | 否 | `'100%'` | 图表宽度 |
| `height` | `string \| number` | 否 | `'400px'` | 图表高度 |
| `showValue` | `boolean` | 否 | `true` | 是否在顶点显示数值 |

## TypeScript 类型定义

### TechStackData

```typescript
interface TechStackData {
  name: string      // 技术维度名称（如"Java"、"Vue"等）
  max: number       // 该维度的最大值（通常为100）
  value: number     // 当前值
}
```

### TechStackSeries

```typescript
interface TechStackSeries {
  name: string      // 系列名称（如"要求"、"学员能力"等）
  data: number[]    // 各维度的值
  color?: string    // 线条颜色（可选）
}
```

## 颜色系统

组件使用项目的 OKLCH 颜色系统，默认颜色方案：

```css
--chart-1: oklch(0.65 0.18 45)     /* 主色调 - 橙色 */
--chart-2: oklch(0.55 0.15 220)    /* 蓝色 */
--chart-3: oklch(0.60 0.15 150)    /* 绿色 */
--chart-4: oklch(0.70 0.15 85)     /* 黄色 */
--chart-5: oklch(0.55 0.22 25)     /* 红色 */
```

可以自定义颜色：

```vue
<TechStackRadar
  :series="[
    {
      name: '学员能力',
      data: [85, 90, 75, 80, 70],
      color: '#5470c6' // 使用自定义颜色
    }
  ]"
/>
```

## 实际应用场景

### 1. 学员技术栈评估

在学员档案页面展示学员的技术能力评估：

```vue
<el-card shadow="never">
  <template #header>
    <span>技术栈评估</span>
  </template>
  <TechStackRadar
    :data="studentTechData"
    :series="[
      {
        name: '学员能力',
        data: techScores
      }
    ]"
    height="400px"
  />
</el-card>
```

### 2. 职位要求与学员能力对比

在求职申请页面对比职位要求和学员能力：

```vue
<TechStackRadar
  :data="positionTechData"
  :series="[
    {
      name: '职位要求',
      data: positionRequirements,
      color: 'oklch(0.65 0.18 45)'
    },
    {
      name: '学员能力',
      data: studentAbilities,
      color: 'oklch(0.60 0.15 150)'
    }
  ]"
  title="技术栈匹配度分析"
  height="450px"
/>
```

### 3. 多个学员能力对比

在教师仪表盘对比多个学员：

```vue
<TechStackRadar
  :data="techData"
  :series="[
    {
      name: '张三',
      data: [85, 90, 75, 80, 70]
    },
    {
      name: '李四',
      data: [78, 85, 82, 88, 72]
    },
    {
      name: '王五',
      data: [92, 88, 80, 85, 78]
    }
  ]"
  title="学员能力对比"
  height="500px"
/>
```

### 4. 响应式布局

在不同屏幕尺寸下自适应：

```vue
<el-row :gutter="20">
  <el-col :xs="24" :sm="12" :md="8">
    <TechStackRadar
      :data="frontendData"
      :series="[{ name: '前端', data: frontendScores }]"
      title="前端技术栈"
    />
  </el-col>
  <el-col :xs="24" :sm="12" :md="8">
    <TechStackRadar
      :data="backendData"
      :series="[{ name: '后端', data: backendScores }]"
      title="后端技术栈"
    />
  </el-col>
  <el-col :xs="24" :sm="12" :md="8">
    <TechStackRadar
      :data="devopsData"
      :series="[{ name: '运维', data: devopsScores }]"
      title="运维技术栈"
    />
  </el-col>
</el-row>
```

## 注意事项

1. **数据一致性**：`data` 中的维度数量必须与 `series[i].data` 的数组长度一致
2. **取值范围**：建议所有维度的 `max` 值保持一致（通常为 100）
3. **性能优化**：组件会自动监听窗口大小变化并调整图表尺寸
4. **内存管理**：组件卸载时会自动销毁 ECharts 实例
5. **响应式**：建议设置 `width="100%"` 以支持响应式布局

## 组件特性

### 自动响应式

组件会自动监听窗口大小变化并调整图表尺寸：

```typescript
// 内部实现
window.addEventListener('resize', handleResize)
```

### 数据监听

组件会自动监听 props 变化并实时更新图表：

```typescript
watch(
  () => [props.data, props.series, props.title],
  () => {
    nextTick(() => {
      updateChartOption()
    })
  },
  { deep: true }
)
```

### 内存管理

组件卸载时自动清理资源：

```typescript
onUnmounted(() => {
  if (chart) {
    chart.dispose()
    chart = null
  }
  window.removeEventListener('resize', handleResize)
})
```

## 示例文件

查看完整示例：
- 示例组件：`frontend/src/components/TechStackRadar.example.vue`
- 组件源码：`frontend/src/components/TechStackRadar.vue`

## 样式定制

组件使用 scoped 样式，可以通过全局样式覆盖：

```scss
// 自定义雷达图样式
.tech-stack-radar {
  min-height: 400px;
}
```

## 浏览器兼容性

支持所有现代浏览器：
- Chrome/Edge >= 90
- Firefox >= 88
- Safari >= 14

## 依赖版本

```json
{
  "echarts": "^5.4.3",
  "vue": "^3.3.11"
}
```

## 更新日志

### v1.0.0 (2026-01-20)

- ✨ 初始版本发布
- ✅ 支持单系列和多系列雷达图
- ✅ 完整的 TypeScript 类型定义
- ✅ 响应式设计
- ✅ 使用项目 OKLCH 颜色系统

## 许可证

MIT
