<template>
  <div
    ref="chartRef"
    class="tech-stack-radar"
    :style="{ width: width, height: height }"
  ></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

// 技术栈维度数据
export interface TechStackData {
  name: string // 技术维度名称（如"Java"、"Vue"等）
  max: number // 该维度的最大值（通常为100）
  value: number // 当前值
}

// 技术栈系列数据
export interface TechStackSeries {
  name: string // 系列名称（如"要求"、"学员能力"等）
  data: number[] // 各维度的值
  color?: string // 线条颜色（可选）
}

// Props 定义
interface Props {
  data: TechStackData[] // 技术栈维度数据
  series: TechStackSeries[] // 要显示的系列数据
  title?: string // 图表标题
  width?: string | number // 图表宽度
  height?: string | number // 图表高度
  showValue?: boolean // 是否在顶点显示数值
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  width: '100%',
  height: '400px',
  showValue: true
})

// 图表引用
const chartRef = ref<HTMLElement>()

// 图表实例
let chart: echarts.ECharts | null = null

// 默认颜色方案（使用项目的 OKLCH 颜色系统）
const defaultColors = [
  'oklch(0.65 0.18 45)', // --primary
  'oklch(0.55 0.15 220)', // --chart-2
  'oklch(0.60 0.15 150)', // --chart-3
  'oklch(0.70 0.15 85)', // --chart-4
  'oklch(0.55 0.22 25)' // --chart-5
]

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  // 如果已有实例，先销毁
  if (chart) {
    chart.dispose()
  }

  // 创建新实例
  chart = echarts.init(chartRef.value)

  // 更新图表配置
  updateChartOption()
}

// 更新图表配置
const updateChartOption = () => {
  if (!chart) return

  // 准备雷达图配置
  const indicator = props.data.map(item => ({
    name: item.name,
    max: item.max
  }))

  // 准备系列数据
  const series = props.series.map((s, index) => ({
    name: s.name,
    type: 'radar',
    data: [
      {
        value: s.data,
        name: s.name
      }
    ],
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: {
      width: 2,
      color: s.color || defaultColors[index % defaultColors.length]
    },
    areaStyle: {
      color: s.color || defaultColors[index % defaultColors.length],
      opacity: 0.2
    },
    itemStyle: {
      color: s.color || defaultColors[index % defaultColors.length]
    },
    emphasis: {
      lineStyle: {
        width: 4
      },
      areaStyle: {
        opacity: 0.3
      }
    }
  }))

  // 准备图例数据
  const legendData = props.series.map(s => s.name)

  const option: EChartsOption = {
    title: props.title
      ? {
          text: props.title,
          left: 'center',
          top: 10,
          textStyle: {
            fontSize: 16,
            fontWeight: 600,
            color: 'oklch(0.20 0.02 240)' // --text-primary
          }
        }
      : undefined,
    tooltip: {
      trigger: 'item',
      backgroundColor: 'oklch(1.00 0 0)',
      borderColor: 'oklch(0.90 0.01 240)', // --border
      borderWidth: 1,
      textStyle: {
        color: 'oklch(0.20 0.02 240)' // --text-primary
      },
      formatter: (params: any) => {
        if (params && params.name) {
          let result = `<strong>${params.name}</strong><br/>`
          if (params.value) {
            props.data.forEach((item, index) => {
              result += `${item.name}: ${params.value[index]}<br/>`
            })
          }
          return result
        }
        return ''
      }
    },
    legend: {
      data: legendData,
      bottom: 10,
      left: 'center',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        fontSize: 12,
        color: 'oklch(0.45 0.015 240)' // --text-secondary
      }
    },
    radar: {
      indicator: indicator,
      shape: 'polygon',
      splitNumber: 4,
      axisName: {
        color: 'oklch(0.20 0.02 240)', // --text-primary
        fontSize: 12,
        fontWeight: 500
      },
      splitLine: {
        lineStyle: {
          color: 'oklch(0.90 0.01 240)', // --border
          width: 1
        }
      },
      splitArea: {
        show: true,
        areaStyle: {
          color: ['oklch(0.98 0.005 240)', 'oklch(1.00 0 0)'] // 交替的背景色
        }
      },
      axisLine: {
        lineStyle: {
          color: 'oklch(0.90 0.01 240)', // --border
          width: 1
        }
      }
    },
    series: series
  }

  // 设置配置
  chart.setOption(option)
}

// 窗口大小变化时调整图表尺寸
const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}

// 监听数据变化
watch(
  () => [props.data, props.series, props.title],
  () => {
    nextTick(() => {
      updateChartOption()
    })
  },
  { deep: true }
)

// 组件挂载后初始化图表
onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

// 组件卸载前清理
onUnmounted(() => {
  if (chart) {
    chart.dispose()
    chart = null
  }
  window.removeEventListener('resize', handleResize)
})

// 暴露 resize 方法给父组件
defineExpose({
  resize: handleResize,
  chart: () => chart
})

// 导出 ECharts 类型
export type { EChartsOption }
</script>

<style scoped lang="scss">
.tech-stack-radar {
  min-height: 300px;
  width: 100%;
}

// 响应式设计
@media (max-width: 768px) {
  .tech-stack-radar {
    min-height: 250px;
  }
}
</style>
