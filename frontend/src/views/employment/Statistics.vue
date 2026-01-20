<template>
  <div class="employment-statistics">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStudents || 0 }}</div>
              <div class="stat-label">学员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><Select /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.employedCount || 0 }}</div>
              <div class="stat-label">已就业</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.seekingCount || 0 }}</div>
              <div class="stat-label">求职中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="28"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.employmentRate }}%</div>
              <div class="stat-label">就业率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 求职进度统计 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <!-- 求职漏斗 -->
        <el-card shadow="hover" class="funnel-card">
          <template #header>
            <span>求职进度分析</span>
          </template>
          <div class="funnel-stats">
            <div class="funnel-item">
              <div class="funnel-header">
                <span class="funnel-label">投递简历</span>
                <span class="funnel-count">{{ funnel.applications }}</span>
              </div>
              <el-progress :percentage="100" :stroke-width="20" />
            </div>
            <div class="funnel-item">
              <div class="funnel-header">
                <span class="funnel-label">笔试通过</span>
                <span class="funnel-count">{{ funnel.testPassed }}</span>
              </div>
              <el-progress
                :percentage="getFunnelPercentage(funnel.testPassed, funnel.applications)"
                :stroke-width="20"
                color="#67c23a"
              />
            </div>
            <div class="funnel-item">
              <div class="funnel-header">
                <span class="funnel-label">面试通过</span>
                <span class="funnel-count">{{ funnel.interviewPassed }}</span>
              </div>
              <el-progress
                :percentage="getFunnelPercentage(funnel.interviewPassed, funnel.applications)"
                :stroke-width="20"
                color="#e6a23c"
              />
            </div>
            <div class="funnel-item">
              <div class="funnel-header">
                <span class="funnel-label">收到Offer</span>
                <span class="funnel-count">{{ funnel.offers }}</span>
              </div>
              <el-progress
                :percentage="getFunnelPercentage(funnel.offers, funnel.applications)"
                :stroke-width="20"
                color="#f56c6c"
              />
            </div>
            <div class="funnel-item">
              <div class="funnel-header">
                <span class="funnel-label">接受Offer</span>
                <span class="funnel-count">{{ funnel.acceptedOffers }}</span>
              </div>
              <el-progress
                :percentage="getFunnelPercentage(funnel.acceptedOffers, funnel.applications)"
                :stroke-width="20"
                color="#409eff"
              />
            </div>
          </div>
        </el-card>

        <!-- 学员求职状态列表 -->
        <el-card shadow="hover" class="students-card">
          <template #header>
            <div class="card-header">
              <span>学员求职状态</span>
              <div class="header-actions">
                <el-select v-model="filterStatus" placeholder="全部状态" style="width: 120px; margin-right: 12px" @change="loadStudentStats">
                  <el-option label="全部" value="" />
                  <el-option label="已录取" value="admitted" />
                  <el-option label="求职中" value="seeking" />
                </el-select>
                <el-button :icon="Refresh" @click="loadStudentStats">刷新</el-button>
              </div>
            </div>
          </template>
          <el-table :data="studentList" style="width: 100%" v-loading="loading" stripe>
            <el-table-column label="学员" width="140">
              <template #default="{ row }">
                <div style="display: flex; align-items: center; gap: 12px">
                  <el-avatar :size="40">{{ row.studentName?.charAt(0) }}</el-avatar>
                  <div>
                    <div style="font-weight: 500">{{ row.studentName }}</div>
                    <div style="font-size: 12px; color: #909399">{{ row.major }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="employmentStatus" label="就业状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getEmploymentStatusType(row.employmentStatus)" size="small">
                  {{ getEmploymentStatusText(row.employmentStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applicationCount" label="投递数" width="80" align="center" />
            <el-table-column prop="testCount" label="笔试数" width="80" align="center" />
            <el-table-column prop="interviewCount" label="面试数" width="80" align="center" />
            <el-table-column prop="offerCount" label="Offer数" width="80" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.offerCount > 0" type="success" size="small">{{ row.offerCount }}</el-tag>
                <span v-else style="color: #909399">0</span>
              </template>
            </el-table-column>
            <el-table-column label="最近动态" min-width="200">
              <template #default="{ row }">
                <div style="font-size: 13px; color: #606266">
                  {{ row.latestActivity || '暂无动态' }}
                </div>
                <div style="font-size: 11px; color: #909399">
                  {{ row.latestActivityTime || '-' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="viewStudent(row)">
                  查看详情
                </el-button>
                <el-button link type="success" size="small" @click="guideStudent(row)">
                  就业指导
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="total > 0"
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="loadStudentStats"
            @current-change="loadStudentStats"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-card>
      </el-col>

      <!-- 右侧：Offer统计 & 就业分布 -->
      <el-col :span="8">
        <!-- Offer统计 -->
        <el-card shadow="hover" class="offer-card">
          <template #header>
            <span>Offer统计</span>
          </template>
          <div class="offer-stats">
            <div class="offer-item">
              <div class="offer-label">已发送Offer</div>
              <div class="offer-value">{{ offerStats.totalOffers || 0 }}</div>
            </div>
            <div class="offer-item">
              <div class="offer-label">已接受Offer</div>
              <div class="offer-value offer-accepted">{{ offerStats.acceptedOffers || 0 }}</div>
            </div>
            <div class="offer-item">
              <div class="offer-label">已拒绝Offer</div>
              <div class="offer-value offer-rejected">{{ offerStats.rejectedOffers || 0 }}</div>
            </div>
            <div class="offer-item">
              <div class="offer-label">待处理</div>
              <div class="offer-value offer-pending">{{ offerStats.pendingOffers || 0 }}</div>
            </div>
            <el-divider />
            <div class="offer-rate">
              <div class="rate-label">Offer接受率</div>
              <div class="rate-value">{{ offerStats.acceptRate }}%</div>
            </div>
          </div>
        </el-card>

        <!-- 热门企业 -->
        <el-card shadow="hover" class="company-card">
          <template #header>
            <span>热门就业企业</span>
          </template>
          <div class="company-list">
            <div v-for="(company, index) in topCompanies" :key="company.id" class="company-item">
              <div class="company-rank">{{ index + 1 }}</div>
              <div class="company-info">
                <div class="company-name">{{ company.companyName }}</div>
                <div class="company-count">{{ company.count }} 人入职</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学员详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="学员求职详情"
      width="800px"
      @open="loadStudentDetail"
    >
      <div v-loading="detailLoading">
        <el-descriptions v-if="currentStudent" :column="2" border>
          <el-descriptions-item label="姓名">
            {{ currentStudent.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="专业">
            {{ currentStudent.major }}
          </el-descriptions-item>
          <el-descriptions-item label="就业状态">
            <el-tag :type="getEmploymentStatusType(currentStudent.employmentStatus)">
              {{ getEmploymentStatusText(currentStudent.employmentStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系方式">
            {{ currentStudent.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="投递简历" :span="2">
            {{ currentStudent.applicationCount || 0 }} 份
          </el-descriptions-item>
          <el-descriptions-item label="参加笔试" :span="2">
            {{ currentStudent.testCount || 0 }} 次
          </el-descriptions-item>
          <el-descriptions-item label="参加面试" :span="2">
            {{ currentStudent.interviewCount || 0 }} 次
          </el-descriptions-item>
          <el-descriptions-item label="收到Offer" :span="2">
            <el-tag v-if="currentStudent.offerCount > 0" type="success" size="large">
              {{ currentStudent.offerCount }} 个
            </el-tag>
            <span v-else style="color: #909399">暂无Offer</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- Offer列表 -->
        <div v-if="currentStudent?.offerCount > 0" style="margin-top: 20px">
          <h4 style="margin-bottom: 12px">收到的Offer</h4>
          <el-table :data="currentStudent.offers" size="small">
            <el-table-column prop="companyName" label="企业" />
            <el-table-column prop="positionName" label="职位" />
            <el-table-column label="薪资">
              <template #default="{ row }">
                ¥{{ row.salary }} / {{ row.salaryUnit === 'month' ? '月' : '年' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getOfferStatusType(row.status)" size="small">
                  {{ getOfferStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="guideStudent">就业指导</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  Select,
  Document,
  TrendCharts,
  Refresh
} from '@element-plus/icons-vue'
import { statisticsApi } from '@/api/statistics'
import { studentEmploymentApi } from '@/api/studentEmployment'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const detailDialogVisible = ref(false)

// 筛选状态
const filterStatus = ref('')

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})
const total = ref(0)

// 统计数据
const stats = ref({
  totalStudents: 0,
  employedCount: 0,
  seekingCount: 0,
  employmentRate: 0
})

// 求职漏斗数据
const funnel = ref({
  applications: 0,
  testPassed: 0,
  interviewPassed: 0,
  offers: 0,
  acceptedOffers: 0
})

// Offer统计
const offerStats = ref({
  totalOffers: 0,
  acceptedOffers: 0,
  rejectedOffers: 0,
  pendingOffers: 0,
  acceptRate: 0
})

// 学员列表
const studentList = ref([])
const currentStudent = ref<any>(null)

// 热门企业
const topCompanies = ref<any[]>([])

// 加载统计数据
const loadStats = async () => {
  try {
    loading.value = true

    // 并行加载所有统计数据
    const [employmentStatusRes, funnelRes, companyRankingRes, offerStatsRes] = await Promise.allSettled([
      statisticsApi.getEmploymentStatusDistribution(),
      statisticsApi.getFunnelData(),
      statisticsApi.getCompanyRanking(5),
      statisticsApi.getOfferStats()
    ])

    // 处理就业状态分布数据
    if (employmentStatusRes.status === 'fulfilled') {
      const statusData = employmentStatusRes.value || {}
      stats.value = {
        totalStudents: statusData.total || 0,
        employedCount: statusData.已就业 || 0,
        seekingCount: statusData.求职中 || 0,
        employmentRate: statusData.total > 0
          ? Math.round((statusData.已就业 / statusData.total) * 1000) / 10
          : 0
      }
    }

    // 处理求职漏斗数据
    if (funnelRes.status === 'fulfilled') {
      const funnelData = funnelRes.value || []
      if (funnelData.length > 0) {
        funnel.value = {
          applications: funnelData.find((item: any) => item.name === '简历投递')?.value || 0,
          testPassed: funnelData.find((item: any) => item.name === '筛选通过')?.value || 0,
          interviewPassed: funnelData.find((item: any) => item.name === '进入面试')?.value || 0,
          offers: funnelData.find((item: any) => item.name === '收到Offer')?.value || 0,
          acceptedOffers: funnelData.find((item: any) => item.name === '收到Offer')?.value || 0
        }
      }
    }

    // 处理热门企业数据 - 字段映射: hireCount -> count
    if (companyRankingRes.status === 'fulfilled') {
      const companies = companyRankingRes.value || []
      topCompanies.value = companies.map((company: any) => ({
        id: company.companyId,
        companyName: company.companyName,
        count: company.hireCount // 字段映射
      }))
    }

    // 处理Offer统计数据
    if (offerStatsRes.status === 'fulfilled') {
      offerStats.value = offerStatsRes.value || offerStats.value
    }

  } catch (error) {
    console.error('加载统计数据失败', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 加载学员求职状态列表
const loadStudentStats = async () => {
  try {
    loading.value = true

    // 对于"求职中"这种包含多个状态的选项，需要获取所有数据然后前端过滤
    const shouldGetAllData = filterStatus.value === 'seeking'

    const res = await studentEmploymentApi.getPage({
      current: pagination.current,
      size: pagination.size,
      employmentStatus: shouldGetAllData ? undefined : mapFilterStatusToBackend(filterStatus.value)
    })

    if (res) {
      let records = res.records || []

      // 如果选择了"求职中"，需要在前端过滤（因为后端只支持单个状态）
      if (filterStatus.value === 'seeking') {
        records = records.filter((item: any) =>
          item.employmentStatus === 'applied' ||
          item.employmentStatus === 'interviewing' ||
          item.employmentStatus === 'not_started'
        )
      }

      // 转换数据格式以适配前端
      studentList.value = records.map((item: any) => ({
        id: item.id,
        studentName: item.realName,
        major: item.major,
        employmentStatus: mapEmploymentStatus(item.employmentStatus),
        applicationCount: item.applicationCount || 0,
        testCount: 0, // TODO: 后续可以从后端获取笔试数统计
        interviewCount: item.interviewCount || 0,
        offerCount: item.offerCount || 0,
        latestActivity: getLatestActivity(item.employmentStatus),
        latestActivityTime: '-' // TODO: 后续可以从后端获取最新活动时间
      }))

      total.value = filterStatus.value === 'seeking' ? records.length : (res.total || 0)
    } else {
      studentList.value = []
      total.value = 0
    }

  } catch (error) {
    console.error('加载学员列表失败', error)
    ElMessage.error('加载学员列表失败')
    studentList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 映射后端就业状态到前端
const mapEmploymentStatus = (backendStatus: string): string => {
  const statusMap: Record<string, string> = {
    'hired': 'employed',         // 已入职 -> 已就业
    'offered': 'admitted',       // 已获Offer -> 已录取
    'interviewing': 'seeking',   // 面试中 -> 求职中
    'applied': 'seeking',        // 求职中 -> 求职中
    'not_started': 'seeking'     // 未开始 -> 求职中
  }
  return statusMap[backendStatus] || 'seeking'
}

// 映射前端筛选值到后端
const mapFilterStatusToBackend = (frontendStatus: string): string | undefined => {
  const statusMap: Record<string, string> = {
    'employed': 'hired',         // 已就业 -> 已入职
    'admitted': 'offered',       // 已录取 -> 已获Offer
    'seeking': 'applied',        // 求职中 -> 求职中（使用applied，因为求职中包括applied和interviewing）
    'further': undefined         // 继续深造 -> 暂不支持
  }
  return statusMap[frontendStatus]
}

// 获取最新活动描述
const getLatestActivity = (status: string): string => {
  const activityMap: Record<string, string> = {
    'hired': '已入职',
    'offered': '已收到Offer',
    'interviewing': '面试进行中',
    'applied': '求职中',
    'not_started': '未开始求职'
  }
  return activityMap[status] || '暂无动态'
}

// 计算漏斗百分比
const getFunnelPercentage = (current: number, total: number) => {
  if (total === 0) return 0
  return Math.round((current / total) * 100)
}

// 就业状态类型
const getEmploymentStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    employed: 'success',
    seeking: 'warning',
    admitted: 'info',
    further: 'info'
  }
  return typeMap[status] || 'info'
}

const getEmploymentStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    employed: '已就业',
    seeking: '求职中',
    admitted: '已录取',
    further: '继续深造',
    unemployed: '未就业'
  }
  return textMap[status] || status
}

// Offer状态类型
const getOfferStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    accepted: 'success',
    rejected: 'danger',
    pending: 'warning'
  }
  return typeMap[status] || 'info'
}

const getOfferStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    accepted: '已接受',
    rejected: '已拒绝',
    pending: '待接受'
  }
  return textMap[status] || status
}

// 查看学员详情
const viewStudent = async (row: any) => {
  currentStudent.value = {
    ...row,
    offers: [] // 将在loadStudentDetail中加载
  }
  detailDialogVisible.value = true
}

const loadStudentDetail = async () => {
  if (!currentStudent.value?.id) return

  try {
    detailLoading.value = true
    const res = await studentEmploymentApi.getDetail(currentStudent.value.id)

    if (res) {
      const data = res

      // 更新学员基本信息
      if (data.student) {
        currentStudent.value.phone = data.student.phone
      }

      // 更新Offer列表
      if (data.offers && Array.isArray(data.offers)) {
        currentStudent.value.offers = data.offers.map((offer: any) => ({
          companyName: offer.companyName,
          positionName: offer.positionName,
          salary: offer.salary?.toString() || '0',
          salaryUnit: offer.salaryUnit || 'month',
          status: offer.status
        }))
      }
    }
  } catch (error) {
    console.error('加载学员详情失败', error)
    ElMessage.error('加载学员详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 就业指导
const guideStudent = (row: any) => {
  router.push(`/teaching/students?id=${row.id}&tab=employment`)
}

onMounted(() => {
  loadStats()
  loadStudentStats()
})
</script>

<style lang="scss" scoped>
.employment-statistics {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
          line-height: 1;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
}

.content-row {
  .funnel-card,
  .students-card,
  .offer-card,
  .company-card {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      align-items: center;
    }
  }
}

.funnel-card {
  .funnel-stats {
    .funnel-item {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      .funnel-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;

        .funnel-label {
          font-size: 14px;
          color: #606266;
          font-weight: 500;
        }

        .funnel-count {
          font-size: 14px;
          color: #303133;
          font-weight: 600;
        }
      }
    }
  }
}

.offer-card {
  .offer-stats {
    .offer-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-of-type {
        border-bottom: none;
      }

      .offer-label {
        font-size: 14px;
        color: #606266;
      }

      .offer-value {
        font-size: 18px;
        font-weight: 600;
        color: #303133;

        &.offer-accepted {
          color: #67c23a;
        }

        &.offer-rejected {
          color: #f56c6c;
        }

        &.offer-pending {
          color: #e6a23c;
        }
      }
    }

    .offer-rate {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 12px;

      .rate-label {
        font-size: 14px;
        color: #606266;
        font-weight: 500;
      }

      .rate-value {
        font-size: 24px;
        font-weight: 600;
        color: #67c23a;
      }
    }
  }
}

.company-card {
  .company-list {
    .company-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .company-rank {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: #ecf5ff;
        color: #409eff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        font-size: 14px;
        flex-shrink: 0;
      }

      .company-info {
        flex: 1;

        .company-name {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
        }

        .company-count {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}
</style>
