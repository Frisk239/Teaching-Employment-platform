<template>
  <div class="position-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>职位列表</h2>
      <p class="description">浏览和搜索所有招聘职位</p>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索职位名称"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="职位类型">
          <el-select v-model="filters.positionType" placeholder="全部" clearable style="width: 150px">
            <el-option label="全职" value="fulltime" />
            <el-option label="兼职" value="parttime" />
            <el-option label="实习" value="internship" />
          </el-select>
        </el-form-item>

        <el-form-item label="工作城市">
          <el-input v-model="filters.city" placeholder="输入城市" clearable style="width: 150px" />
        </el-form-item>

        <el-form-item label="学历要求">
          <el-select v-model="filters.education" placeholder="全部" clearable style="width: 150px">
            <el-option label="大专" value="junior_college" />
            <el-option label="本科" value="bachelor" />
            <el-option label="硕士" value="master" />
            <el-option label="博士" value="doctor" />
            <el-option label="不限" value="unlimited" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 职位列表 -->
    <div v-loading="loading" class="position-list-content">
      <el-empty v-if="positionList.length === 0 && !loading" description="暂无职位信息" />

      <div v-else class="position-cards">
        <el-card
          v-for="position in positionList"
          :key="position.id"
          class="position-card"
          shadow="hover"
          @click="handleViewDetail(position)"
        >
          <div class="position-header">
            <h3 class="position-title">{{ position.positionName }}</h3>
            <el-tag :type="getPositionTypeTagType(position.positionType)">
              {{ getPositionTypeLabel(position.positionType) }}
            </el-tag>
          </div>

          <div class="company-info">
            <el-icon><OfficeBuilding /></el-icon>
            <span>{{ position.companyName || '未分配企业' }}</span>
          </div>

          <div class="position-info">
            <div class="info-item">
              <el-icon><Location /></el-icon>
              <span>{{ position.city }}</span>
            </div>
            <div class="info-item">
              <el-icon><Money /></el-icon>
              <span class="salary">{{ formatSalary(position.salaryMin, position.salaryMax, position.salaryUnit) }}</span>
            </div>
            <div class="info-item">
              <el-icon><Reading /></el-icon>
              <span>{{ getEducationLabel(position.education) }}</span>
            </div>
          </div>

          <div class="position-footer">
            <div class="recruit-info">
              <span>招聘: {{ position.recruitCount }}人</span>
              <span>已招: {{ position.hiredCount || 0 }}人</span>
              <span>投递: {{ position.applicationCount || 0 }}</span>
            </div>
            <el-tag :type="getStatusTagType(position.status)">
              {{ getStatusLabel(position.status) }}
            </el-tag>
          </div>

          <div v-if="position.techStack" class="tech-stack">
            <el-tag
              v-for="(tech, index) in parseTechStack(position.techStack)"
              :key="index"
              size="small"
              style="margin-right: 8px"
            >
              {{ tech }}
            </el-tag>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <div v-if="positionList.length > 0" class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadPositions"
          @current-change="loadPositions"
        />
      </div>
    </div>

    <!-- 职位详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="职位详情"
      width="900px"
    >
      <el-descriptions :column="2" border v-if="currentPosition">
        <el-descriptions-item label="职位名称">
          {{ currentPosition.positionName }}
        </el-descriptions-item>
        <el-descriptions-item label="职位类型">
          <el-tag :type="getPositionTypeTagType(currentPosition.positionType)">
            {{ getPositionTypeLabel(currentPosition.positionType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属企业">
          {{ currentPosition.companyName || '未分配' }}
        </el-descriptions-item>
        <el-descriptions-item label="工作城市">
          {{ currentPosition.city }}
        </el-descriptions-item>
        <el-descriptions-item label="薪资范围">
          {{ formatSalary(currentPosition.salaryMin, currentPosition.salaryMax, currentPosition.salaryUnit) }}
        </el-descriptions-item>
        <el-descriptions-item label="学历要求">
          {{ getEducationLabel(currentPosition.education) }}
        </el-descriptions-item>
        <el-descriptions-item label="工作年限">
          {{ currentPosition.experience || '不限' }}
        </el-descriptions-item>
        <el-descriptions-item label="招聘人数">
          {{ currentPosition.recruitCount }} 人
        </el-descriptions-item>
        <el-descriptions-item label="已招人数">
          {{ currentPosition.hiredCount || 0 }} 人
        </el-descriptions-item>
        <el-descriptions-item label="投递数量">
          {{ currentPosition.applicationCount || 0 }} 份
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentPosition.status)">
            {{ getStatusLabel(currentPosition.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">
          {{ formatDate(currentPosition.publishTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="截止日期">
          {{ currentPosition.deadline || '不限' }}
        </el-descriptions-item>
        <el-descriptions-item label="技术栈" :span="2">
          <el-tag
            v-for="(tech, index) in parseTechStack(currentPosition.techStack)"
            :key="index"
            style="margin-right: 8px; margin-bottom: 8px"
            size="small"
          >
            {{ tech }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="职位描述" :span="2">
          <div class="description-content">{{ currentPosition.description || '暂无描述' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="职位要求" :span="2">
          <div class="description-content">{{ currentPosition.requirements || '暂无要求' }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="handleApply" type="primary">投递简历</el-button>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Location,
  Money,
  Reading,
  OfficeBuilding,
} from '@element-plus/icons-vue'
import { getPositionPageApi } from '@/api/recruitment'
import type { Position } from '@/api/types'

// 响应式数据
const loading = ref(false)
const positionList = ref<Position[]>([])
const searchKeyword = ref('')
const detailDialogVisible = ref(false)
const currentPosition = ref<Position | null>(null)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 筛选条件
const filters = reactive({
  positionType: undefined as string | undefined,
  city: '',
  education: undefined as string | undefined,
})

// 加载职位列表
const loadPositions = async () => {
  loading.value = true
  try {
    const { data } = await getPositionPageApi({
      current: pagination.current,
      size: pagination.size,
      keyword: searchKeyword.value || undefined,
      positionType: filters.positionType,
      city: filters.city || undefined,
      education: filters.education,
      status: 'active', // 只显示招聘中的职位
    })

    if (data.code === 200) {
      positionList.value = data.data.records
      pagination.total = data.data.total
    } else {
      ElMessage.error(data.message || '加载职位列表失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载职位列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadPositions()
}

// 重置
const handleReset = () => {
  searchKeyword.value = ''
  filters.positionType = undefined
  filters.city = ''
  filters.education = undefined
  pagination.current = 1
  loadPositions()
}

// 查看详情
const handleViewDetail = (position: Position) => {
  currentPosition.value = position
  detailDialogVisible.value = true
}

// 投递简历
const handleApply = () => {
  ElMessage.info('投递功能开发中...')
  // TODO: 实现投递简历功能
}

// 获取职位类型标签类型
const getPositionTypeTagType = (type?: string) => {
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    fulltime: 'primary',
    parttime: 'success',
    internship: 'warning',
  }
  return typeMap[type || ''] || 'info'
}

// 获取职位类型标签
const getPositionTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    fulltime: '全职',
    parttime: '兼职',
    internship: '实习',
  }
  return labelMap[type] || type
}

// 获取学历标签
const getEducationLabel = (education?: string) => {
  const labelMap: Record<string, string> = {
    junior_college: '大专',
    bachelor: '本科',
    master: '硕士',
    doctor: '博士',
    unlimited: '不限',
  }
  return labelMap[education || ''] || education || '不限'
}

// 获取状态标签类型
const getStatusTagType = (status?: string) => {
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    draft: 'info',
    active: 'success',
    paused: 'warning',
    closed: 'danger',
  }
  return typeMap[status || ''] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const labelMap: Record<string, string> = {
    draft: '草稿',
    active: '招聘中',
    paused: '暂停',
    closed: '已关闭',
  }
  return labelMap[status] || status
}

// 格式化薪资
const formatSalary = (min?: number, max?: number, unit?: string) => {
  if (!min && !max) return '面议'

  const unitLabelMap: Record<string, string> = {
    month: '元/月',
    year: '元/年',
    day: '元/天',
    hour: '元/小时',
  }

  const unitLabel = unitLabelMap[unit || 'month'] || '元/月'

  if (min && max) {
    return `${min} - ${max} ${unitLabel}`
  } else if (min) {
    return `${min}+ ${unitLabel}`
  } else if (max) {
    return `最高 ${max} ${unitLabel}`
  }

  return '面议'
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 解析技术栈
const parseTechStack = (techStack?: string) => {
  if (!techStack) return []
  try {
    const parsed = JSON.parse(techStack)
    if (Array.isArray(parsed)) {
      return parsed
    }
  } catch {
    return techStack.split(/,|，/).filter(t => t.trim())
  }
  return []
}

// 组件挂载
onMounted(() => {
  loadPositions()
})
</script>

<style lang="scss" scoped>
.position-list {
  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0 0 8px 0;
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    .description {
      margin: 0;
      font-size: 14px;
      color: #909399;
    }
  }

  .search-card {
    margin-bottom: 20px;
  }

  .position-list-content {
    min-height: 400px;

    .position-cards {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 20px;
      margin-bottom: 20px;

      .position-card {
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }

        .position-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          .position-title {
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            color: #303133;
          }
        }

        .company-info {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 12px;
          font-size: 14px;
          color: #606266;

          .el-icon {
            color: #909399;
          }
        }

        .position-info {
          display: flex;
          flex-wrap: wrap;
          gap: 16px;
          margin-bottom: 12px;

          .info-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 14px;
            color: #606266;

            .el-icon {
              color: #909399;
            }

            .salary {
              color: #f56c6c;
              font-weight: 600;
            }
          }
        }

        .position-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 12px;
          border-top: 1px solid #ebeef5;

          .recruit-info {
            display: flex;
            gap: 16px;
            font-size: 13px;
            color: #909399;
          }
        }

        .tech-stack {
          margin-top: 12px;
          padding-top: 12px;
          border-top: 1px solid #ebeef5;
        }
      }
    }

    .pagination {
      display: flex;
      justify-content: center;
      padding: 20px 0;
    }
  }

  .description-content {
    white-space: pre-wrap;
    word-break: break-word;
    line-height: 1.6;
  }
}
</style>
