<template>
  <div class="admin-dashboard">
    <!-- 欢迎横幅 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-banner">
          <h2><el-icon><Lock /></el-icon> 管理员工作台</h2>
          <p>欢迎回来, {{ authStore.userName }} | 今天是 {{ currentDate }}</p>
        </div>
      </el-col>
    </el-row>

    <!-- 核心统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-blue">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-green">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStudents }}</div>
              <div class="stat-label">学生数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-purple">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><Notebook /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCourses }}</div>
              <div class="stat-label">总课程数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-orange">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.employmentRate }}%</div>
              <div class="stat-label">就业率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 月度就业趋势图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon><TrendCharts /></el-icon> 月度就业趋势</span>
              <el-tag type="success" size="small">最近6个月</el-tag>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 就业状态分布饼图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon><PieChart /></el-icon> 就业状态分布</span>
              <el-tag type="info" size="small">实时统计</el-tag>
            </div>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 薪资分布柱状图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon><Coin /></el-icon> 薪资分布统计</span>
              <el-tag type="warning" size="small">已就业学生</el-tag>
            </div>
          </template>
          <div ref="salaryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 热门课程排行 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon><Trophy /></el-icon> 热门课程排行</span>
              <el-tag type="danger" size="small">TOP 10</el-tag>
            </div>
          </template>
          <div ref="courseChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 企业录用排行榜 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon><OfficeBuilding /></el-icon> 企业录用排行</span>
              <el-tag type="primary" size="small">TOP 10</el-tag>
            </div>
          </template>
          <div ref="companyChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 快捷操作和公告 -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title"><el-icon><Lightning /></el-icon> 快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-space wrap :size="12">
              <el-button type="primary" :icon="User" @click="$router.push('/system/users')">
                用户管理
              </el-button>
              <el-button type="success" :icon="Notebook" @click="$router.push('/teaching/courses')">
                课程管理
              </el-button>
              <el-button type="warning" :icon="Reading" @click="$router.push('/teaching/students')">
                学员管理
              </el-button>
              <el-button type="info" :icon="UserFilled" @click="$router.push('/teaching/teachers')">
                教师管理
              </el-button>
              <el-button type="danger" :icon="OfficeBuilding" @click="$router.push('/employment/companies')">
                企业管理
              </el-button>
              <el-button type="primary" :icon="DataLine" @click="$router.push('/system/roles')">
                角色权限
              </el-button>
            </el-space>
          </div>

          <!-- 系统公告 -->
          <el-divider></el-divider>
          <div class="announcements">
            <h4><el-icon><Bell /></el-icon> 系统公告</h4>
            <el-timeline v-if="announcements.length > 0">
              <el-timeline-item
                v-for="item in announcements"
                :key="item.id"
                :timestamp="item.publishTime"
                placement="top"
              >
                <el-tag
                  :type="item.type === 'info' ? 'primary' : item.type === 'warning' ? 'warning' : item.type === 'success' ? 'success' : 'danger'"
                  size="small"
                >
                  {{ item.title }}
                </el-tag>
                <p class="announcement-content">{{ item.content }}</p>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无公告" :image-size="80"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Reading,
  Notebook,
  TrendCharts,
  UserFilled,
  OfficeBuilding,
  DataLine,
  Lock,
  PieChart,
  Coin,
  Trophy,
  Grid,
  Lightning,
  Bell
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import { statisticsApi } from '@/api/statistics'
import { getUserPageApi } from '@/api/user'
import { getStudentPageApi } from '@/api/student'
import { getCoursePageApi } from '@/api/course'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const authStore = useAuthStore()

// 当前日期
const currentDate = dayjs().format('YYYY年MM月DD日 dddd')

// 核心统计数据
const stats = ref({
  totalUsers: 0,        // 总用户数（t_user表）
  totalStudents: 0,     // 学生数（t_student表）
  totalCourses: 0,      // 总课程数
  employmentRate: 0     // 就业率
})

// 图表引用
const trendChartRef = ref<HTMLElement>()
const statusChartRef = ref<HTMLElement>()
const salaryChartRef = ref<HTMLElement>()
const courseChartRef = ref<HTMLElement>()
const companyChartRef = ref<HTMLElement>()

// 图表实例
let trendChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null
let salaryChart: echarts.ECharts | null = null
let courseChart: echarts.ECharts | null = null
let companyChart: echarts.ECharts | null = null

// 公告数据
const announcements = ref<any[]>([])

// 加载统计数据
const fetchStats = async () => {
  try {
    // 并行请求多个API
    const [overviewRes, usersRes, studentsRes, coursesRes] = await Promise.allSettled([
      statisticsApi.getOverview(),
      getUserPageApi({ current: 1, size: 1 }),
      getStudentPageApi({ current: 1, size: 1 }),
      getCoursePageApi({ current: 1, size: 1 })
    ])

    // 处理就业概况数据（获取就业率）
    if (overviewRes.status === 'fulfilled') {
      const overviewData = overviewRes.value as any
      if (overviewData.employmentRate !== undefined) {
        stats.value.employmentRate = Number(overviewData.employmentRate || 0)
      }
    }

    // 处理总用户数
    if (usersRes.status === 'fulfilled') {
      const usersData = usersRes.value as any
      if (usersData.total !== undefined) {
        stats.value.totalUsers = usersData.total
      }
    }

    // 处理学生数
    if (studentsRes.status === 'fulfilled') {
      const studentsData = studentsRes.value as any
      if (studentsData.total !== undefined) {
        stats.value.totalStudents = studentsData.total
      }
    }

    // 处理课程总数数据
    if (coursesRes.status === 'fulfilled') {
      const coursesData = coursesRes.value as any
      if (coursesData.total !== undefined) {
        stats.value.totalCourses = coursesData.total
      }
    }

  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 初始化月度就业趋势图
const initTrendChart = async () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  try {
    const data = await statisticsApi.getMonthlyTrend(6) as any

    const months = data.months || []
    const employed = data.employed || []
    const seeking = data.seeking || []

    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross'
        }
      },
      legend: {
        data: ['已就业', '求职中']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: months
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '已就业',
          type: 'line',
          data: employed,
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          },
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '求职中',
          type: 'line',
          data: seeking,
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
            ])
          },
          itemStyle: { color: '#67C23A' }
        }
      ]
    }

    trendChart.setOption(option)
  } catch (error) {
    console.error('加载趋势图失败:', error)
  }
}

// 初始化就业状态分布饼图
const initStatusChart = async () => {
  if (!statusChartRef.value) return

  statusChart = echarts.init(statusChartRef.value)

  try {
    const data = await statisticsApi.getEmploymentStatusDistribution() as any

    // 后端返回格式: { "已就业": 8, "求职中": 5, "继续深造": 0, "未就业": 0 }
    // 转换为 echarts 需要的格式: [{ name: "已就业", value: 8 }, ...]
    const statusData = Object.entries(data)
      .filter(([key]) => key !== 'total') // 排除 total 字段
      .map(([name, value]) => ({ name, value: value as number }))

    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '就业状态',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c}人\n({d}%)'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          data: statusData
        }
      ]
    }

    statusChart.setOption(option)
  } catch (error) {
    console.error('加载状态分布图失败:', error)
  }
}

// 初始化薪资分布柱状图
const initSalaryChart = async () => {
  if (!salaryChartRef.value) return

  salaryChart = echarts.init(salaryChartRef.value)

  try {
    const data = await statisticsApi.getSalaryDistribution() as any

    const ranges = ['0-5k', '5k-10k', '10k-15k', '15k-20k', '20k+']
    const values = ranges.map(range => data[range] || 0)

    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ranges
      },
      yAxis: {
        type: 'value',
        name: '人数'
      },
      series: [
        {
          name: '人数',
          type: 'bar',
          data: values,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#E6A23C' },
              { offset: 1, color: '#F56C6C' }
            ]),
            borderRadius: [8, 8, 0, 0]
          },
          label: {
            show: true,
            position: 'top'
          }
        }
      ]
    }

    salaryChart.setOption(option)
  } catch (error) {
    console.error('加载薪资分布图失败:', error)
  }
}

// 初始化热门课程排行图
const initCourseChart = async () => {
  if (!courseChartRef.value) return

  courseChart = echarts.init(courseChartRef.value)

  try {
    const data = await statisticsApi.getPopularCourses(10) as any
    const courses = data || []

    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: courses.map((c: any) => c.name || '').reverse()
      },
      series: [
        {
          name: '学生数',
          type: 'bar',
          data: courses.map((c: any) => c.students || 0).reverse(),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#67C23A' },
              { offset: 1, color: '#409EFF' }
            ]),
            borderRadius: [0, 8, 8, 0]
          },
          label: {
            show: true,
            position: 'right'
          }
        }
      ]
    }

    courseChart.setOption(option)
  } catch (error) {
    console.error('加载热门课程图失败:', error)
  }
}

// 初始化企业录用排行榜
const initCompanyChart = async () => {
  if (!companyChartRef.value) return

  companyChart = echarts.init(companyChartRef.value)

  try {
    const data = await statisticsApi.getCompanyRanking(10) as any
    // 后端返回格式: [{ companyId: 1, companyName: "xxx", hireCount: 5 }, ...]
    const companies = data || []

    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: companies.map((c: any) => c.companyName || '').reverse()
      },
      series: [
        {
          name: '录用人数',
          type: 'bar',
          data: companies.map((c: any) => c.hireCount || 0).reverse(),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#909399' },
              { offset: 1, color: '#409EFF' }
            ]),
            borderRadius: [0, 8, 8, 0]
          },
          label: {
            show: true,
            position: 'right'
          }
        }
      ]
    }

    companyChart.setOption(option)
  } catch (error) {
    console.error('加载企业排行榜失败:', error)
  }
}

// 加载公告
const loadAnnouncements = async () => {
  try {
    const data = await statisticsApi.getAnnouncements(5) as any
    announcements.value = data || []
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

// 窗口大小改变时重绘图表
const handleResize = () => {
  trendChart?.resize()
  statusChart?.resize()
  salaryChart?.resize()
  courseChart?.resize()
  companyChart?.resize()
}

onMounted(async () => {
  await fetchStats()
  await Promise.all([
    initTrendChart(),
    initStatusChart(),
    initSalaryChart(),
    initCourseChart(),
    initCompanyChart(),
    loadAnnouncements()
  ])

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  statusChart?.dispose()
  salaryChart?.dispose()
  courseChart?.dispose()
  companyChart?.dispose()
})
</script>

<style scoped lang="scss">
.admin-dashboard {
  padding: 20px;

  .welcome-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 24px 32px;
    border-radius: 12px;
    margin-bottom: 20px;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    h2 {
      margin: 0 0 8px 0;
      font-size: 24px;
      font-weight: 600;
    }

    p {
      margin: 0;
      font-size: 14px;
      opacity: 0.95;
    }
  }

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      border-radius: 12px;
      transition: all 0.3s ease;
      border: none;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
      }

      .stat-content {
        display: flex;
        align-items: center;
        padding: 12px 0;

        .stat-icon {
          width: 64px;
          height: 64px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 32px;
            font-weight: 700;
            line-height: 1;
            margin-bottom: 8px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }

      &.stat-card-blue .stat-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
      }
      &.stat-card-blue .stat-value {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }

      &.stat-card-green .stat-icon {
        background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
        color: white;
      }
      &.stat-card-green .stat-value {
        background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }

      &.stat-card-purple .stat-icon {
        background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
        color: white;
      }
      &.stat-card-purple .stat-value {
        background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }

      &.stat-card-orange .stat-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: white;
      }
      &.stat-card-orange .stat-value {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }
  }

  .charts-row {
    margin-bottom: 20px;

    .chart-card {
      border-radius: 12px;
      border: none;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .card-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }

      .chart-container {
        height: 300px;
      }

      .quick-actions {
        margin-bottom: 16px;

        .el-button {
          margin-bottom: 8px;
        }
      }

      .announcements {
        h4 {
          margin-bottom: 16px;
          font-size: 15px;
          color: #303133;
        }

        .announcement-content {
          margin: 8px 0 0 0;
          font-size: 13px;
          color: #606266;
          line-height: 1.6;
        }

        .el-timeline {
          padding-left: 0;
        }
      }
    }
  }
}
</style>
