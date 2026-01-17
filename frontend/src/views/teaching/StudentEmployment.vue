<template>
  <div class="student-employment">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">学生就业情况</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-card shadow="never" class="stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon total">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalStudents }}</div>
            <div class="stat-label">总学生数</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon applied">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.appliedStudents }}</div>
            <div class="stat-label">已投递简历</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon test">
            <el-icon><Edit /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.testInProgress }}</div>
            <div class="stat-label">笔试中</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon interview">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.interviewInProgress }}</div>
            <div class="stat-label">面试中</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon offer">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.offerReceived }}</div>
            <div class="stat-label">已获Offer</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon hired">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.hiredStudents }}</div>
            <div class="stat-label">已入职</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon rate">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.employmentRate }}%</div>
            <div class="stat-label">就业率</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 筛选条件 -->
    <el-card shadow="never" class="filter-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索学生姓名"
            clearable
            style="width: 200px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="班级">
          <el-input
            v-model="searchForm.className"
            placeholder="输入班级"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="专业">
          <el-input
            v-model="searchForm.major"
            placeholder="输入专业"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="就业状态">
          <el-select
            v-model="searchForm.employmentStatus"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="已入职" value="hired" />
            <el-option label="已获Offer" value="offered" />
            <el-option label="面试中" value="interviewing" />
            <el-option label="求职中" value="applied" />
            <el-option label="未开始" value="not_started" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 学生就业列表 -->
    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column label="就业状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getEmploymentStatusType(row.employmentStatus)">
              {{ row.employmentStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请数" width="100" align="center">
          <template #default="{ row }">
            <el-badge :value="row.applicationCount" :max="99" type="primary">
              <template #default>
                <el-icon><Document /></el-icon>
              </template>
            </el-badge>
          </template>
        </el-table-column>
        <el-table-column label="面试数" width="100" align="center">
          <template #default="{ row }">
            <el-badge :value="row.interviewCount" :max="99" type="warning">
              <template #default>
                <el-icon><ChatDotRound /></el-icon>
              </template>
            </el-badge>
          </template>
        </el-table-column>
        <el-table-column label="Offer数" width="100" align="center">
          <template #default="{ row }">
            <el-badge :value="row.offerCount" :max="99" type="success">
              <template #default>
                <el-icon><Trophy /></el-icon>
              </template>
            </el-badge>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${currentStudent?.realName} - 就业详情`"
      width="900px"
      :close-on-click-modal="false"
    >
      <div v-if="studentDetail" class="detail-content">
        <!-- 基本信息卡片 -->
        <el-card shadow="never" class="detail-section">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </div>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="姓名">{{ studentDetail.student?.realName }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ studentDetail.student?.className || '-' }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ studentDetail.student?.major || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年级">{{ studentDetail.student?.grade || '-' }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ studentDetail.student?.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ studentDetail.student?.email || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 统计数据 -->
        <el-card shadow="never" class="detail-section">
          <template #header>
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span>统计数据</span>
            </div>
          </template>
          <div class="detail-stats">
            <div class="detail-stat-item">
              <div class="stat-value">{{ studentDetail.stats?.totalApplications || 0 }}</div>
              <div class="stat-label">投递申请</div>
            </div>
            <div class="detail-stat-item">
              <div class="stat-value">{{ studentDetail.stats?.totalInterviews || 0 }}</div>
              <div class="stat-label">参加面试</div>
            </div>
            <div class="detail-stat-item">
              <div class="stat-value">{{ studentDetail.stats?.totalOffers || 0 }}</div>
              <div class="stat-label">获得Offer</div>
            </div>
            <div class="detail-stat-item">
              <div class="stat-value">{{ studentDetail.stats?.passedTests || 0 }}</div>
              <div class="stat-label">笔试通过</div>
            </div>
            <div class="detail-stat-item">
              <div class="stat-value">{{ studentDetail.stats?.passedInterviews || 0 }}</div>
              <div class="stat-label">面试通过</div>
            </div>
          </div>
        </el-card>

        <!-- 申请记录 -->
        <el-card shadow="never" class="detail-section">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>申请记录</span>
            </div>
          </template>
          <el-table
            :data="studentDetail.applications"
            size="small"
            max-height="250"
          >
            <el-table-column prop="positionName" label="职位名称" />
            <el-table-column prop="companyName" label="公司名称" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="getApplicationStatusType(row.status)">
                  {{ getApplicationStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applicationTime" label="申请时间" width="120" />
          </el-table>
          <el-empty v-if="!studentDetail.applications || studentDetail.applications.length === 0" description="暂无申请记录" />
        </el-card>

        <!-- Offer记录 -->
        <el-card shadow="never" class="detail-section">
          <template #header>
            <div class="card-header">
              <el-icon><Trophy /></el-icon>
              <span>Offer记录</span>
            </div>
          </template>
          <el-table
            :data="studentDetail.offers"
            size="small"
            max-height="250"
          >
            <el-table-column prop="positionName" label="职位名称" />
            <el-table-column prop="companyName" label="公司名称" />
            <el-table-column label="薪资" width="150">
              <template #default="{ row }">
                {{ row.salaryMin }}-{{ row.salaryMax }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="getOfferStatusType(row.status)">
                  {{ getOfferStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="获得时间" width="120" />
          </el-table>
          <el-empty v-if="!studentDetail.offers || studentDetail.offers.length === 0" description="暂无Offer记录" />
        </el-card>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Search,
  User,
  Document,
  Edit,
  ChatDotRound,
  Trophy,
  CircleCheck,
  TrendCharts,
  DataAnalysis
} from '@element-plus/icons-vue'
import {
  studentEmploymentApi,
  type StudentEmployment,
  type EmploymentStats,
  type StudentEmploymentDetail,
  getEmploymentStatusType
} from '@/api/studentEmployment'

const loading = ref(false)
const tableData = ref<StudentEmployment[]>([])
const detailDialogVisible = ref(false)
const currentStudent = ref<StudentEmployment | null>(null)
const studentDetail = ref<StudentEmploymentDetail | null>(null)

// 统计数据
const stats = ref<EmploymentStats>({
  totalStudents: 0,
  appliedStudents: 0,
  testInProgress: 0,
  interviewInProgress: 0,
  offerReceived: 0,
  hiredStudents: 0,
  employmentRate: 0
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  className: '',
  major: '',
  employmentStatus: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await studentEmploymentApi.getStats() as any
    stats.value = response || {}
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取学生就业列表
const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size
    }

    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    if (searchForm.className) {
      params.className = searchForm.className
    }
    if (searchForm.major) {
      params.major = searchForm.major
    }
    if (searchForm.employmentStatus) {
      params.employmentStatus = searchForm.employmentStatus
    }

    const response = await studentEmploymentApi.getPage(params) as any
    tableData.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取学生就业列表失败:', error)
    ElMessage.error('获取学生就业列表失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const handleViewDetail = async (student: StudentEmployment) => {
  currentStudent.value = student
  try {
    const response = await studentEmploymentApi.getDetail(student.id) as any
    studentDetail.value = response
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取学生详情失败:', error)
    ElMessage.error('获取学生详情失败')
  }
}

// 获取申请状态类型
const getApplicationStatusType = (status: string): string => {
  const typeMap: Record<string, string> = {
    'hired': 'success',
    'offered': 'warning',
    'interview_passed': 'primary',
    'test_passed': 'info',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取申请状态文本
const getApplicationStatusText = (status: string): string => {
  const textMap: Record<string, string> = {
    'pending': '待处理',
    'screened': '已筛选',
    'test_passed': '笔试通过',
    'test_failed': '笔试失败',
    'interview_passed': '面试通过',
    'interview_failed': '面试失败',
    'rejected': '已拒绝',
    'offered': '已发Offer',
    'hired': '已入职',
    'declined': '已拒绝Offer'
  }
  return textMap[status] || status
}

// 获取Offer状态类型
const getOfferStatusType = (status: string): string => {
  const typeMap: Record<string, string> = {
    'accepted': 'success',
    'pending': 'warning',
    'rejected': 'danger',
    'hired': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取Offer状态文本
const getOfferStatusText = (status: string): string => {
  const textMap: Record<string, string> = {
    'pending': '待处理',
    'accepted': '已接受',
    'rejected': '已拒绝',
    'hired': '已入职'
  }
  return textMap[status] || status
}

// 刷新
const handleRefresh = () => {
  fetchStats()
  fetchData()
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.className = ''
  searchForm.major = ''
  searchForm.employmentStatus = ''
  handleSearch()
}

// 初始化
onMounted(() => {
  fetchStats()
  fetchData()
})
</script>

<style scoped lang="scss">
.student-employment {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }

  // 统计卡片
  .stats-card {
    margin-bottom: 20px;
    padding: 20px;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;

    @media (max-width: 1400px) {
      grid-template-columns: repeat(3, 1fr);
    }

    @media (max-width: 1024px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  .stat-item {
    display: flex;
    align-items: center;
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    transition: all 0.3s;
    background: #fff;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
    }
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    margin-right: 16px;
    flex-shrink: 0;

    .el-icon {
      font-size: 24px;
      color: #fff;
    }

    &.total {
      background: #409eff;
    }

    &.applied {
      background: #67c23a;
    }

    &.test {
      background: #e6a23c;
    }

    &.interview {
      background: #909399;
    }

    &.offer {
      background: #f56c6c;
    }

    &.hired {
      background: #67c23a;
    }

    &.rate {
      background: #909399;
    }
  }

  .stat-info {
    flex: 1;
    min-width: 0;
  }

  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1.2;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }

  // 筛选卡片
  .filter-card {
    margin-bottom: 20px;
  }

  .search-form {
    margin-bottom: 0;
  }

  // 表格卡片
  .table-card {
    .pagination {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
    }
  }

  // 详情对话框
  .detail-content {
    .detail-section {
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }

      .card-header {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
      }

      .detail-stats {
        display: flex;
        gap: 24px;
      }

      .detail-stat-item {
        text-align: center;

        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #409eff;
          line-height: 1.2;
        }

        .stat-label {
          font-size: 13px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
}
</style>
