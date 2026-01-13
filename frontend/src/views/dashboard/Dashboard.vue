<template>
  <div class="dashboard-page">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎回来，{{ authStore.userName }}！</h1>
      <p class="welcome-date">{{ currentDate }}</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div
        v-for="(stat, index) in stats"
        :key="index"
        class="stat-card"
        :class="`stat-card-${stat.color}`"
      >
        <div class="stat-header">
          <div
            class="stat-icon"
            :class="`stat-icon-${stat.color}`"
          >
            <el-icon :size="24">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div
            class="stat-trend"
            :class="stat.trend > 0 ? 'stat-trend-up' : 'stat-trend-down'"
          >
            <el-icon :size="14">
              <component :is="stat.trend > 0 ? 'Top' : 'Bottom'" />
            </el-icon>
            {{ Math.abs(stat.trend) }}%
          </div>
        </div>
        <div class="stat-value">{{ stat.value }}</div>
        <p class="stat-label">{{ stat.label }}</p>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <h3 class="chart-title">
              <el-icon><TrendCharts /></el-icon>
              就业趋势
            </h3>
            <div class="chart-actions">
              <el-button size="small" text>近6个月</el-button>
              <el-button size="small" text>近12个月</el-button>
            </div>
          </div>
        </template>
        <div ref="trendChartRef" class="chart-container"></div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <h3 class="chart-title">
              <el-icon><PieChart /></el-icon>
              就业状态分布
            </h3>
          </div>
        </template>
        <div ref="pieChartRef" class="chart-container"></div>
      </el-card>
    </div>

    <!-- 双栏信息区 -->
    <div class="info-grid">
      <!-- 热门课程 -->
      <el-card class="popular-courses-card">
        <template #header>
          <h3 class="section-title">
            <el-icon><Star /></el-icon>
            热门课程
          </h3>
        </template>
        <div class="popular-course-list">
          <div
            v-for="(course, index) in popularCourses"
            :key="course.id"
            class="popular-course-item"
          >
            <div
              class="course-rank"
              :class="`course-rank-${index + 1}`"
            >
              {{ index + 1 }}
            </div>
            <div class="course-info">
              <div class="course-name">{{ course.name }}</div>
              <div class="course-stats">
                <span>{{ course.students }} 学生</span>
                <div class="course-progress">
                  <div class="progress-bar">
                    <div
                      class="progress-fill"
                      :style="{ width: course.progress + '%' }"
                    ></div>
                  </div>
                </div>
                <span class="course-employment-rate">
                  就业率 {{ course.employmentRate }}%
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 最新公告 -->
      <el-card class="info-card">
        <template #header>
          <h3 class="section-title">
            <el-icon><Bell /></el-icon>
            最新公告
          </h3>
        </template>
        <div class="info-list">
          <div
            v-for="announcement in announcements"
            :key="announcement.id"
            class="info-item"
          >
            <div class="info-item-title">{{ announcement.title }}</div>
            <div class="info-item-meta">
              <el-tag
                :type="getTypeText(announcement.type) as any"
                size="small"
              >
                {{ getTypeText(announcement.type) }}
              </el-tag>
              <span>{{ announcement.publishTime }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 合作企业 -->
    <el-card class="partner-companies-card">
      <template #header>
        <h3 class="section-title">
          <el-icon><OfficeBuilding /></el-icon>
          合作企业
        </h3>
      </template>
      <div class="partner-companies-grid">
        <div
          v-for="company in partnerCompanies"
          :key="company.id"
          class="partner-company-item"
        >
          <div class="company-logo">
            {{ company.name.substring(0, 2) }}
          </div>
          <div class="company-name">{{ company.name }}</div>
          <div class="company-jobs">
            <span class="company-jobs-count">{{ company.jobsCount }}</span>
            个在招职位
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores'
import { statisticsApi } from '@/api/statistics'
import type { PopularCourse, Announcement } from '@/api/types'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const authStore = useAuthStore()

// 当前日期
const currentDate = dayjs().format('YYYY年MM月DD日 dddd')

// 加载状态
const loading = ref(false)

// 图表引用
const trendChartRef = ref<HTMLDivElement>()
const pieChartRef = ref<HTMLDivElement>()

let trendChart: ECharts | null = null
let pieChart: ECharts | null = null

// 统计数据
const stats = ref([
  {
    label: '总学生数',
    value: '0',
    icon: 'User',
    color: 'orange',
    trend: 0,
  },
  {
    label: '合作企业',
    value: '0',
    icon: 'OfficeBuilding',
    color: 'blue',
    trend: 0,
  },
  {
    label: '在招职位',
    value: '0',
    icon: 'Briefcase',
    color: 'green',
    trend: 0,
  },
  {
    label: '求职申请',
    value: '0',
    icon: 'Document',
    color: 'purple',
    trend: 0,
  },
])

// 热门课程
const popularCourses = ref<PopularCourse[]>([])

// 公告
const announcements = ref<Announcement[]>([])

// 合作企业
const partnerCompanies = ref<any[]>([])

// 获取公告类型文本
const getTypeText = (type: string) => {
  const map: Record<string, string> = {
    'info': '通知',
    'warning': '培训',
    'success': '活动',
    'error': '紧急',
  }
  return map[type] || '通知'
}

// 加载概览数据
const loadOverview = async () => {
  try {
    const data = await statisticsApi.getOverview()
    stats.value = [
      {
        label: '总学生数',
        value: data.totalStudents.toLocaleString(),
        icon: 'User',
        color: 'orange',
        trend: 12.5,
      },
      {
        label: '合作企业',
        value: data.totalCompanies.toLocaleString(),
        icon: 'OfficeBuilding',
        color: 'blue',
        trend: 8.3,
      },
      {
        label: '在招职位',
        value: data.totalPositions.toLocaleString(),
        icon: 'Briefcase',
        color: 'green',
        trend: 15.2,
      },
      {
        label: '求职申请',
        value: data.totalApplications.toLocaleString(),
        icon: 'Document',
        color: 'purple',
        trend: -3.5,
      },
    ]
  } catch (error) {
    console.error('加载概览数据失败:', error)
  }
}

// 加载热门课程
const loadPopularCourses = async () => {
  try {
    const data = await statisticsApi.getPopularCourses(5)
    popularCourses.value = data
  } catch (error) {
    console.error('加载热门课程失败:', error)
  }
}

// 加载公告
const loadAnnouncements = async () => {
  try {
    const data = await statisticsApi.getAnnouncements(4)
    announcements.value = data.map((item: any) => ({
      ...item,
      publishTime: dayjs(item.publishTime).fromNow(),
    }))
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

// 加载企业统计
const loadCompanyStats = async () => {
  try {
    const data = await statisticsApi.getCompanyStats()
    partnerCompanies.value = data.slice(0, 6).map((company: any) => ({
      id: company.id,
      name: company.name,
      jobsCount: company.positionCount || 0,
    }))
  } catch (error) {
    console.error('加载企业统计失败:', error)
  }
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadOverview(),
      loadPopularCourses(),
      loadAnnouncements(),
      loadCompanyStats(),
    ])
  } finally {
    loading.value = false
  }
}

// 加载就业趋势数据
const loadTrendData = async () => {
  try {
    const data = await statisticsApi.getMonthlyTrend(6)

    // 更新趋势图
    if (trendChart) {
      const option = {
        tooltip: {
          trigger: 'axis',
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          data: data.labels || [],
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: '已就业',
            type: 'line',
            data: data.employed || [],
            smooth: true,
            itemStyle: { color: '#67C23A' },
          },
          {
            name: '求职中',
            type: 'line',
            data: data.seeking || [],
            smooth: true,
            itemStyle: { color: '#E6A23C' },
          },
        ],
      }
      trendChart.setOption(option)
    }
  } catch (error) {
    console.error('加载就业趋势数据失败:', error)
    // 使用默认空数据
    if (trendChart) {
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: [] },
        yAxis: { type: 'value' },
        series: [
          { name: '已就业', type: 'line', data: [], smooth: true, itemStyle: { color: '#67C23A' } },
          { name: '求职中', type: 'line', data: [], smooth: true, itemStyle: { color: '#E6A23C' } },
        ],
      }
      trendChart.setOption(option)
    }
  }
}

// 加载就业状态分布数据
const loadStatusDistribution = async () => {
  try {
    const data = await statisticsApi.getEmploymentStatusDistribution()

    // 更新饼图
    if (pieChart) {
      const chartData = [
        { value: data['已就业'] || 0, name: '已就业' },
        { value: data['求职中'] || 0, name: '求职中' },
        { value: data['继续深造'] || 0, name: '继续深造' },
        { value: data['未就业'] || 0, name: '未就业' },
      ]

      const option = {
        tooltip: {
          trigger: 'item',
        },
        legend: {
          orient: 'vertical',
          left: 'left',
        },
        series: [
          {
            name: '就业状态',
            type: 'pie',
            radius: '60%',
            data: chartData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      }
      pieChart.setOption(option)
    }
  } catch (error) {
    console.error('加载就业状态分布数据失败:', error)
    // 使用默认空数据
    if (pieChart) {
      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          name: '就业状态',
          type: 'pie',
          radius: '60%',
          data: [
            { value: 0, name: '已就业' },
            { value: 0, name: '求职中' },
            { value: 0, name: '继续深造' },
            { value: 0, name: '未就业' },
          ],
        }],
      }
      pieChart.setOption(option)
    }
  }
}

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis',
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: [],
    },
    yAxis: {
      type: 'value',
    },
    series: [
      {
        name: '已就业',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: { color: '#67C23A' },
      },
      {
        name: '求职中',
        type: 'line',
        data: [],
        smooth: true,
        itemStyle: { color: '#E6A23C' },
      },
    ],
  }

  trendChart.setOption(option)
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return

  pieChart = echarts.init(pieChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
    },
    legend: {
      orient: 'vertical',
      left: 'left',
    },
    series: [
      {
        name: '就业状态',
        type: 'pie',
        radius: '60%',
        data: [
          { value: 0, name: '已就业' },
          { value: 0, name: '求职中' },
          { value: 0, name: '继续深造' },
          { value: 0, name: '未就业' },
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  }

  pieChart.setOption(option)
}

// 响应式图表
const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  loadAllData()
  initTrendChart()
  initPieChart()
  window.addEventListener('resize', handleResize)

  // 加载图表数据
  await loadTrendData()
  await loadStatusDistribution()
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

// 欢迎区域
.welcome-section {
  padding: 1.5rem;
  background: linear-gradient(135deg, oklch(0.98 0.005 240) 0%, oklch(0.95 0.01 240) 100%);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-light);
}

.welcome-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 0.5rem 0;
}

.welcome-date {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  margin: 0;
}

// 统计卡片网格
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.25rem;
}

// 统计卡片
.stat-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-lg);
  padding: 1.5rem;
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: var(--stat-color, var(--primary));
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &:hover {
    box-shadow: var(--card-shadow-hover);
    transform: translateY(-2px);

    &::before {
      opacity: 1;
    }
  }
}

.stat-card-orange {
  --stat-color: oklch(0.65 0.18 45);
}

.stat-card-blue {
  --stat-color: oklch(0.55 0.15 220);
}

.stat-card-green {
  --stat-color: oklch(0.60 0.15 150);
}

.stat-card-purple {
  --stat-color: oklch(0.65 0.18 280);
}

.stat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon-orange {
  background: var(--gradient-orange);
}

.stat-icon-blue {
  background: var(--gradient-blue);
}

.stat-icon-green {
  background: var(--gradient-green);
}

.stat-icon-purple {
  background: var(--gradient-purple);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.875rem;
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: 9999px;
}

.stat-trend-up {
  background: var(--success-light);
  color: var(--success);
}

.stat-trend-down {
  background: var(--danger-light);
  color: var(--danger);
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
  line-height: 1.2;
}

.stat-label {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  margin: 0;
}

// 图表区域
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}

.chart-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: var(--card-shadow-hover);
  }

  :deep(.el-card__header) {
    padding: 1.5rem;
    border-bottom: 1px solid var(--border);
  }

  :deep(.el-card__body) {
    padding: 0;
  }
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.chart-actions {
  display: flex;
  gap: 0.5rem;
}

.chart-container {
  width: 100%;
  height: 300px;
  padding: 1.5rem;
}

// 双栏信息区
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}

.popular-courses-card,
.info-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--card-shadow);

  :deep(.el-card__header) {
    padding: 1.5rem;
    border-bottom: 1px solid var(--border);
  }

  :deep(.el-card__body) {
    padding: 1.5rem;
  }
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

// 热门课程列表
.popular-course-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.popular-course-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: oklch(0.99 0.005 240);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  transition: all 0.2s ease;

  &:hover {
    background: var(--primary-light);
    border-color: var(--primary);
    transform: translateX(4px);
  }
}

.course-rank {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1rem;
  flex-shrink: 0;
  background: var(--gradient-orange);
}

.course-rank-1 {
  background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
}

.course-rank-2 {
  background: linear-gradient(135deg, #c0c0c0 0%, #a0a0a0 100%);
}

.course-rank-3 {
  background: linear-gradient(135deg, #cd7f32 0%, #b8860b 100%);
}

.course-info {
  flex: 1;
  min-width: 0;
}

.course-name {
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
  font-size: 0.9375rem;
}

.course-stats {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.course-progress {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  min-width: 120px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: var(--border-light);
  border-radius: 9999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--gradient-orange);
  border-radius: 9999px;
  transition: width 0.6s ease;
}

.course-employment-rate {
  font-weight: 600;
  color: var(--success);
  padding: 0.25rem 0.5rem;
  background: var(--success-light);
  border-radius: 9999px;
  font-size: 0.8125rem;
}

// 信息列表
.info-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: 400px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: var(--border);
    border-radius: 3px;
  }
}

.info-item {
  padding: 0.75rem;
  background: oklch(0.99 0.005 240);
  border-radius: var(--radius-md);
  border-left: 3px solid var(--primary);
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    background: var(--primary-light);
    border-left-color: var(--primary-hover);
    transform: translateX(2px);
  }
}

.info-item-title {
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 0.375rem;
  font-size: 0.9375rem;
}

.info-item-meta {
  font-size: 0.875rem;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

// 合作企业卡片
.partner-companies-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--card-shadow);

  :deep(.el-card__header) {
    padding: 1.5rem;
    border-bottom: 1px solid var(--border);
  }

  :deep(.el-card__body) {
    padding: 1.5rem;
  }
}

.partner-companies-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.25rem;
}

.partner-company-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  padding: 1.25rem;
  background: oklch(0.99 0.005 240);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    background: var(--card-bg);
    border-color: var(--primary);
    box-shadow: var(--shadow-md);
    transform: translateY(-2px);
  }
}

.company-logo {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-md);
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--text-secondary);
  border: 1px solid var(--border-light);
}

.company-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 0.9375rem;
  text-align: center;
}

.company-jobs {
  font-size: 0.875rem;
  color: var(--text-muted);
  text-align: center;
}

.company-jobs-count {
  color: var(--primary);
  font-weight: 600;
}

// 响应式设计
@media (max-width: 1023px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid,
  .info-grid {
    grid-template-columns: 1fr;
  }

  .partner-companies-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 767px) {
  .welcome-section {
    padding: 1rem;
  }

  .welcome-title {
    font-size: 1.25rem;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .stat-card {
    padding: 1.25rem;
  }

  .stat-value {
    font-size: 1.75rem;
  }

  .chart-card,
  .popular-courses-card,
  .info-card,
  .partner-companies-card {
    padding: 1rem;
  }

  .chart-container {
    height: 250px;
  }

  .popular-course-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .course-stats {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .course-progress {
    width: 100%;
  }

  .info-card {
    max-height: 350px;
  }

  .partner-companies-grid {
    grid-template-columns: 1fr;
  }
}
</style>
