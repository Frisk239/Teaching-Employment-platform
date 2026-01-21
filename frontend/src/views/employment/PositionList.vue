<template>
  <div class="position-list-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">岗位列表</h1>
        <p class="page-subtitle">探索适合你的职业机会</p>
      </div>
    </div>

    <!-- 推荐岗位 -->
    <el-card shadow="never" class="recommended-card" v-if="recommendedPositions.length > 0">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon><Star /></el-icon>
            <span>推荐岗位</span>
          </div>
          <el-button text @click="viewAllRecommended">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div class="recommended-positions">
        <div
          v-for="position in recommendedPositions"
          :key="position.id"
          class="recommended-item"
          @click="handleViewDetail(position)"
        >
          <div class="position-header">
            <h3 class="position-name">{{ position.positionName }}</h3>
            <el-tag type="danger" size="small" effect="dark">推荐</el-tag>
          </div>
          <div class="company-info">
            <el-icon><OfficeBuilding /></el-icon>
            <span>{{ position.companyName }}</span>
          </div>
          <div class="position-meta">
            <div class="meta-item">
              <el-icon><Location /></el-icon>
              <span>{{ position.city }}</span>
            </div>
            <div class="meta-item salary">
              <el-icon><Coin /></el-icon>
              <span class="salary-text">{{ formatSalary(position) }}</span>
            </div>
          </div>
          <div class="position-tags">
            <el-tag size="small" type="info">{{ position.positionType }}</el-tag>
            <el-tag size="small">{{ getEducationText(position.educationRequire) }}</el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 搜索和筛选 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="岗位名称">
          <el-input
            v-model="searchForm.positionName"
            placeholder="搜索岗位名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="工作城市">
          <el-select
            v-model="searchForm.city"
            placeholder="全部城市"
            clearable
            filterable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="深圳" value="深圳" />
            <el-option label="杭州" value="杭州" />
            <el-option label="广州" value="广州" />
          </el-select>
        </el-form-item>

        <el-form-item label="岗位类型">
          <el-select
            v-model="searchForm.positionType"
            placeholder="全部类型"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="全职" value="fulltime" />
            <el-option label="兼职" value="parttime" />
            <el-option label="实习" value="internship" />
          </el-select>
        </el-form-item>

        <el-form-item label="学历要求">
          <el-select
            v-model="searchForm.educationRequire"
            placeholder="不限"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="大专" value="junior_college" />
            <el-option label="本科" value="bachelor" />
            <el-option label="硕士" value="master" />
            <el-option label="博士" value="doctor" />
          </el-select>
        </el-form-item>

        <el-form-item label="薪资范围">
          <el-select
            v-model="searchForm.salaryRange"
            placeholder="不限"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="5k以下" value="0-5000" />
            <el-option label="5k-10k" value="5000-10000" />
            <el-option label="10k-15k" value="10000-15000" />
            <el-option label="15k-25k" value="15000-25000" />
            <el-option label="25k以上" value="25000-999999" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 岗位列表 -->
    <el-card shadow="never" class="list-card">
      <div v-loading="loading" class="position-list">
        <el-empty v-if="!loading && isEmpty" description="暂无岗位信息">
          <el-button type="primary" @click="handleReset">清除筛选条件</el-button>
        </el-empty>

        <div
          v-for="position in tableData"
          :key="position.id"
          class="position-card"
          @click="handleViewDetail(position)"
        >
          <div class="card-header">
            <div class="header-left">
              <h3 class="position-name">{{ position.positionName }}</h3>
              <el-tag v-if="position.isRecommended" type="danger" size="small" effect="plain">
                <el-icon><Star /></el-icon>
                推荐
              </el-tag>
              <el-tag :type="getStatusType(position.status)" size="small">
                {{ getStatusText(position.status) }}
              </el-tag>
            </div>
            <div class="salary">
              <span class="salary-amount">{{ formatSalary(position) }}</span>
              <span class="salary-unit">/{{ position.salaryUnit || '月' }}</span>
            </div>
          </div>

          <div class="company-section">
            <div class="company-info">
              <el-icon><OfficeBuilding /></el-icon>
              <span class="company-name">{{ position.companyName }}</span>
            </div>
            <div class="publish-time">
              <el-icon><Clock /></el-icon>
              <span>{{ formatPublishTime(position.publishTime) }}</span>
            </div>
          </div>

          <div class="position-details">
            <div class="detail-tags">
              <div class="tag-item">
                <el-icon><Location /></el-icon>
                <span>{{ position.city }}</span>
              </div>
              <div class="tag-item">
                <el-icon><Reading /></el-icon>
                <span>{{ getEducationText(position.educationRequire) }}</span>
              </div>
              <div class="tag-item">
                <el-icon><Briefcase /></el-icon>
                <span>{{ getTypeText(position.positionType) }}</span>
              </div>
              <div class="tag-item">
                <el-icon><User /></el-icon>
                <span>招{{ position.recruitNum || '若干' }}人</span>
              </div>
            </div>
            <div class="tech-stack" v-if="position.techStack">
              <el-tag
                v-for="(tech, index) in getTechStackList(position.techStack)"
                :key="index"
                size="small"
                class="tech-tag"
              >
                {{ tech }}
              </el-tag>
            </div>
          </div>

          <div class="card-footer">
            <div class="stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ position.viewCount || 0 }} 浏览
              </span>
              <span class="stat-item">
                <el-icon><Document /></el-icon>
                {{ position.applicationCount || 0 }} 已投递
              </span>
            </div>
            <div class="actions">
              <el-button type="primary" size="small" @click.stop="handleApply(position)">
                立即投递
              </el-button>
              <el-button size="small" @click.stop="handleViewDetail(position)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 岗位详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentPosition?.positionName"
      width="1200px"
      @open="handleViewDetailOpen"
    >
      <div v-if="currentPosition" class="position-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border class="detail-section">
          <el-descriptions-item label="公司名称" :span="2">
            {{ currentPosition.companyName }}
          </el-descriptions-item>
          <el-descriptions-item label="薪资待遇">
            <span class="salary-highlight">{{ formatSalary(currentPosition) }}</span>
            <span v-if="currentPosition.salaryUnit">/{{ currentPosition.salaryUnit }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="工作城市">
            {{ currentPosition.city }}
          </el-descriptions-item>
          <el-descriptions-item label="岗位类型">
            {{ getTypeText(currentPosition.positionType) }}
          </el-descriptions-item>
          <el-descriptions-item label="学历要求">
            {{ getEducationText(currentPosition.educationRequire) }}
          </el-descriptions-item>
          <el-descriptions-item label="工作经验">
            {{ getExperienceText(currentPosition.experienceRequire) }}
          </el-descriptions-item>
          <el-descriptions-item label="招聘人数">
            {{ currentPosition.recruitNum || '若干' }}人
          </el-descriptions-item>
          <el-descriptions-item label="发布时间" :span="2">
            {{ formatPublishTime(currentPosition.publishTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 岗位描述 -->
        <el-divider content-position="left">岗位描述</el-divider>
        <div class="description-content">
          {{ currentPosition.description || '暂无描述' }}
        </div>

        <!-- 任职要求 -->
        <el-divider content-position="left">任职要求</el-divider>
        <div class="requirements-content">
          {{ currentPosition.requirements || '暂无要求' }}
        </div>

        <!-- 能力雷达图 -->
        <el-divider content-position="left" v-if="radarData.length > 0">能力要求</el-divider>
        <div class="radar-chart-container" v-if="radarData.length > 0">
          <TechStackRadar
            ref="radarChartRef"
            :data="radarData"
            :series="radarSeries"
            width="100%"
            height="600px"
          />
        </div>

        <!-- 福利待遇 -->
        <el-divider content-position="left" v-if="currentPosition.benefits">福利待遇</el-divider>
        <div class="benefits-content" v-if="currentPosition.benefits">
          {{ currentPosition.benefits }}
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleApply(currentPosition!)">
          立即投递
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Star,
  ArrowRight,
  Search,
  Refresh,
  Location,
  Coin,
  OfficeBuilding,
  Clock,
  Reading,
  Briefcase,
  User,
  View,
  Document,
} from '@element-plus/icons-vue'
import { getPositionPageApi, getPositionByIdApi, type Position } from '@/api/position'
import { applicationApi } from '@/api/application'
import { getStudentByIdApi } from '@/api/student'
import {
  parseCapabilityRadar,
  type TechStackDimension,
  type CapabilityRadarData
} from '@/api/tech-stack'
import TechStackRadar from '@/components/TechStackRadar.vue'
import type { TechStackData, TechStackSeries } from '@/components/TechStackRadar.vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores'

const router = useRouter()
const authStore = useAuthStore()

// 雷达图组件引用
const radarChartRef = ref<InstanceType<typeof TechStackRadar> | null>(null)

// 当前学生信息（包含简历URL）
const currentStudent = ref<any>(null)

// 搜索表单
const searchForm = reactive({
  positionName: '',
  city: '',
  positionType: '',
  educationRequire: '',
  salaryRange: '',
})

// 推荐岗位
const recommendedPositions = ref<Position[]>([])

// 表格数据
const loading = ref(false)
const tableData = ref<Position[]>([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 计算是否为空
const isEmpty = computed(() => tableData.value.length === 0)

// 岗位详情
const detailDialogVisible = ref(false)
const currentPosition = ref<Position | null>(null)

// 计算雷达图数据
const radarData = computed<TechStackData[]>(() => {
  if (!currentPosition.value?.capabilityRadar) return []

  try {
    const radarInfo = parseCapabilityRadar(currentPosition.value.capabilityRadar)
    return (radarInfo.dimensions || []).map(dim => ({
      name: dim.name,
      max: dim.max || 100,
      value: dim.value || 0
    }))
  } catch (error) {
    console.error('解析雷达图数据失败:', error)
    return []
  }
})

// 计算雷达图系列数据
const radarSeries = computed<TechStackSeries[]>(() => {
  if (radarData.value.length === 0) return []

  return [{
    name: '能力要求',
    data: radarData.value.map(dim => dim.value)
  }]
})

// 格式化薪资
const formatSalary = (position: Position): string => {
  if (!position.salaryMin && !position.salaryMax) return '面议'
  const min = position.salaryMin ? Math.floor(position.salaryMin / 1000) + 'k' : ''
  const max = position.salaryMax ? Math.floor(position.salaryMax / 1000) + 'k' : ''
  if (min && max) return `${min}-${max}`
  return min || max || '面议'
}

// 获取学历文本
const getEducationText = (education?: string): string => {
  const map: Record<string, string> = {
    junior_college: '大专',
    bachelor: '本科',
    master: '硕士',
    doctor: '博士',
  }
  return map[education || ''] || '不限'
}

// 获取岗位类型文本
const getTypeText = (type?: string): string => {
  const map: Record<string, string> = {
    fulltime: '全职',
    parttime: '兼职',
    internship: '实习',
  }
  return map[type || ''] || '全职'
}

// 获取经验要求文本
const getExperienceText = (experience?: string): string => {
  const map: Record<string, string> = {
    fresh: '应届生',
    '1-3': '1-3年',
    '3-5': '3-5年',
    '5-10': '5-10年',
    '10+': '10年以上',
  }
  return map[experience || ''] || '不限'
}

// 获取状态类型
const getStatusType = (status?: string) => {
  const map: Record<string, string> = {
    active: 'success',
    draft: 'info',
    closed: 'danger',
  }
  return map[status || ''] || 'info'
}

// 获取状态文本
const getStatusText = (status?: string): string => {
  const map: Record<string, string> = {
    active: '招聘中',
    draft: '草稿',
    closed: '已关闭',
  }
  return map[status || ''] || '未知'
}

// 格式化发布时间
const formatPublishTime = (time?: string): string => {
  if (!time) return '-'
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  return date.toLocaleDateString('zh-CN')
}

// 解析技术栈
const getTechStackList = (techStack?: any): string[] => {
  if (!techStack) return []
  if (typeof techStack === 'string') {
    try {
      techStack = JSON.parse(techStack)
    } catch (e) {
      return []
    }
  }
  if (Array.isArray(techStack)) return techStack
  return []
}

// 加载推荐岗位
const fetchRecommendedPositions = async () => {
  try {
    const res = await getPositionPageApi({
      current: 1,
      size: 6,
      status: 'active',
    })
    recommendedPositions.value = (res.records || [])
      .filter((p: Position) => p.isRecommended)
      .slice(0, 5)
  } catch (error) {
    console.error('加载推荐岗位失败:', error)
  }
}

// 加载岗位列表
const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      positionName: searchForm.positionName || undefined,
      city: searchForm.city || undefined,
      positionType: searchForm.positionType || undefined,
      educationRequire: searchForm.educationRequire || undefined,
      status: 'active',
    }

    // 处理薪资范围
    if (searchForm.salaryRange) {
      const [min, max] = searchForm.salaryRange.split('-').map(Number)
      params.salaryMin = min
      params.salaryMax = max
    }

    const res = await getPositionPageApi(params)
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载岗位列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.positionName = ''
  searchForm.city = ''
  searchForm.positionType = ''
  searchForm.educationRequire = ''
  searchForm.salaryRange = ''
  pagination.current = 1
  fetchData()
}

// 查看全部推荐
const viewAllRecommended = () => {
  searchForm.positionName = ''
  searchForm.city = ''
  searchForm.positionType = ''
  searchForm.educationRequire = ''
  searchForm.salaryRange = ''
  // 这里可以添加特殊标记来只显示推荐岗位
  handleSearch()
}

// 查看详情
const handleViewDetail = async (position: Position) => {
  currentPosition.value = position
  detailDialogVisible.value = true
}

// 详情对话框打开时
const handleViewDetailOpen = async () => {
  if (currentPosition.value?.id) {
    try {
      // 增加浏览量
      // await increaseViewCountApi(currentPosition.value.id)
      const detail = await getPositionByIdApi(currentPosition.value.id)
      currentPosition.value = detail

      // 延迟触发雷达图 resize，确保 Dialog 容器尺寸已确定
      await nextTick()
      setTimeout(() => {
        radarChartRef.value?.resize()
      }, 300) // 等待 Dialog 动画完成（Element Plus Dialog 默认动画时长约 300ms）
    } catch (error) {
      console.error('加载岗位详情失败:', error)
    }
  }
}

// 投递简历
const handleApply = async (position: Position) => {
  try {
    // 检查学生ID是否存在
    const studentId = authStore.studentId
    if (!studentId) {
      ElMessage.error('无法获取学生信息，请重新登录')
      return
    }

    // 获取学生的完整信息（包括简历URL）
    const studentInfo: any = await getStudentByIdApi(studentId)
    const studentResumeUrl = studentInfo?.resumeUrl

    if (!studentResumeUrl) {
      await ElMessageBox.confirm(
        '您还没有上传简历，请先上传简历后再投递岗位。',
        '提示',
        {
          confirmButtonText: '去上传',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
      router.push('/student/student-profile')
      return
    }

    // 确认投递
    await ElMessageBox.confirm(
      `确定要投递【${position.companyName}】的【${position.positionName}】岗位吗？`,
      '确认投递',
      {
        confirmButtonText: '确定投递',
        cancelButtonText: '取消',
        type: 'info',
      }
    )

    // 调用投递API
    await applicationApi.submit({
      studentId: studentId,
      positionId: position.id!,
      companyId: position.companyId,
      // 保留临时字段以便后端填充关联数据
      studentName: authStore.userName || '',
      companyName: position.companyName || '',
      positionName: position.positionName,
    })

    ElMessage.success('投递成功！企业将在3个工作日内处理您的简历')
    // 刷新列表（如果需要显示投递状态）
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '投递失败，请稍后重试')
    }
  }
}

onMounted(() => {
  fetchRecommendedPositions()
  fetchData()
})
</script>

<style scoped lang="scss">
.position-list-page {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 2rem;

  .page-title {
    font-size: 2rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: var(--el-text-color-primary);
  }

  .page-subtitle {
    font-size: 1rem;
    color: var(--el-text-color-secondary);
    margin: 0;
  }
}

.recommended-card {
  margin-bottom: 1.5rem;
  border-radius: 12px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-title {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 1.1rem;
      font-weight: 600;
      color: var(--el-text-color-primary);

      .el-icon {
        color: #f56c6c;
      }
    }
  }

  .recommended-positions {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1rem;
  }

  .recommended-item {
    padding: 1.25rem;
    border: 2px solid #f56c6c;
    border-radius: 12px;
    background: linear-gradient(135deg, #fff5f5 0%, #ffffff 100%);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 16px rgba(245, 108, 108, 0.2);
    }

    .position-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 0.75rem;

      .position-name {
        font-size: 1.1rem;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0;
        line-height: 1.4;
      }
    }

    .company-info {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin-bottom: 0.75rem;
      color: var(--el-text-color-secondary);
      font-size: 0.9rem;
    }

    .position-meta {
      display: flex;
      gap: 1rem;
      margin-bottom: 0.75rem;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        font-size: 0.875rem;
        color: var(--el-text-color-secondary);

        &.salary {
          .salary-text {
            color: #f56c6c;
            font-weight: 600;
          }
        }
      }
    }

    .position-tags {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
  }
}

.search-card {
  margin-bottom: 1.5rem;
  border-radius: 12px;

  .search-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
}

.list-card {
  border-radius: 12px;

  .position-list {
    min-height: 400px;

    .position-card {
      border: 1px solid var(--el-border-color-lighter);
      border-radius: 12px;
      padding: 1.5rem;
      margin-bottom: 1rem;
      cursor: pointer;
      transition: all 0.3s;
      background: white;

      &:hover {
        border-color: var(--el-color-primary);
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      }

      &:last-child {
        margin-bottom: 0;
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1rem;

        .header-left {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          flex: 1;

          .position-name {
            font-size: 1.25rem;
            font-weight: 600;
            color: var(--el-text-color-primary);
            margin: 0;
          }
        }

        .salary {
          text-align: right;

          .salary-amount {
            font-size: 1.5rem;
            font-weight: 700;
            color: #f56c6c;
          }

          .salary-unit {
            font-size: 0.875rem;
            color: var(--el-text-color-secondary);
          }
        }
      }

      .company-section {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid var(--el-border-color-lighter);

        .company-info {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          color: var(--el-text-color-regular);

          .company-name {
            font-weight: 500;
          }
        }

        .publish-time {
          display: flex;
          align-items: center;
          gap: 0.25rem;
          font-size: 0.875rem;
          color: var(--el-text-color-secondary);
        }
      }

      .position-details {
        margin-bottom: 1rem;

        .detail-tags {
          display: flex;
          gap: 1.5rem;
          margin-bottom: 1rem;
          flex-wrap: wrap;

          .tag-item {
            display: flex;
            align-items: center;
            gap: 0.25rem;
            font-size: 0.875rem;
            color: var(--el-text-color-secondary);
          }
        }

        .tech-stack {
          display: flex;
          gap: 0.5rem;
          flex-wrap: wrap;

          .tech-tag {
            background: oklch(0.95 0.01 220);
            border: none;
          }
        }
      }

      .card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 1rem;
        border-top: 1px solid var(--el-border-color-lighter);

        .stats {
          display: flex;
          gap: 1.5rem;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 0.25rem;
            font-size: 0.875rem;
            color: var(--el-text-color-secondary);
          }
        }

        .actions {
          display: flex;
          gap: 0.5rem;
        }
      }
    }
  }

  .pagination {
    margin-top: 2rem;
    display: flex;
    justify-content: flex-end;
  }
}

.position-detail {
  .detail-section {
    margin-bottom: 1.5rem;

    .salary-highlight {
      font-size: 1.25rem;
      font-weight: 600;
      color: #f56c6c;
    }
  }

  .el-divider {
    margin: 1.5rem 0;
  }

  .description-content,
  .requirements-content,
  .benefits-content {
    line-height: 1.8;
    color: var(--el-text-color-regular);
    white-space: pre-wrap;
  }

  .tech-stack-content {
    display: flex;
    gap: 0.75rem;
    flex-wrap: wrap;

    .tech-tag-large {
      padding: 0.5rem 1rem;
      font-size: 0.9rem;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .position-list-page {
    padding: 1rem;
  }

  .recommended-positions {
    grid-template-columns: 1fr;
  }

  .search-form {
    :deep(.el-form-item) {
      display: block;
      margin-right: 0;
      margin-bottom: 1rem;
    }
  }

  .position-card {
    .card-header {
      flex-direction: column;
      gap: 1rem;

      .salary {
        text-align: left;
      }
    }

    .company-section {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }

    .card-footer {
      flex-direction: column;
      gap: 1rem;

      .actions {
        width: 100%;

        .el-button {
          flex: 1;
        }
      }
    }
  }

  .radar-chart-container {
    margin: 2rem 0;
    padding: 0;
    background: oklch(0.99 0.005 240);
    border-radius: 12px;
    border: 1px solid var(--el-border-color-lighter);
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 600px;
  }
}



</style>
