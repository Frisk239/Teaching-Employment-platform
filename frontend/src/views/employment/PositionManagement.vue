<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">职位管理</h1>
      <div class="page-actions">
        <el-button @click="handleExport" :loading="exporting">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-button type="primary" @click="goToPublish">
          <el-icon><Plus /></el-icon>
          发布职位
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选工具栏 -->
    <div class="toolbar">
      <div class="toolbar-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索职位名称或企业..."
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="toolbar-filters">
        <el-select
          v-model="filterStatus"
          placeholder="职位状态"
          clearable
          @change="handleFilterChange"
          style="width: 120px;"
        >
          <el-option label="全部状态" value=""></el-option>
          <el-option label="招聘中" value="active"></el-option>
          <el-option label="已暂停" value="paused"></el-option>
          <el-option label="已结束" value="closed"></el-option>
        </el-select>

        <el-select
          v-model="filterJobType"
          placeholder="职位性质"
          clearable
          @change="handleFilterChange"
          style="width: 120px;"
        >
          <el-option label="全部性质" value=""></el-option>
          <el-option label="全职" value="fulltime"></el-option>
          <el-option label="兼职" value="parttime"></el-option>
          <el-option label="实习" value="intern"></el-option>
        </el-select>

        <el-button @click="handleResetFilters">
          <el-icon><RefreshLeft /></el-icon>
          重置筛选
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-card">
      <div class="table-container">
        <el-table
          :data="filteredPositions"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>

          <el-table-column label="职位信息" min-width="200">
            <template #default="{ row }">
              <div class="position-cell">
                <div class="position-icon">
                  <el-icon><Briefcase /></el-icon>
                </div>
                <div class="position-info">
                  <div class="position-name">{{ row.name }}</div>
                  <div class="position-company">{{ row.companyName }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="location" label="工作地点" width="150"></el-table-column>

          <el-table-column prop="salaryRange" label="薪资范围" width="120">
            <template #default="{ row }">
              <span style="color: var(--primary); font-weight: 600;">{{ row.salaryRange }}</span>
            </template>
          </el-table-column>

          <el-table-column label="职位性质" width="100" align="center">
            <template #default="{ row }">
              <el-tag size="small">{{ getJobTypeLabel(row.jobType) }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column label="招聘人数" width="100" align="center">
            <template #default="{ row }">
              <span style="font-weight: 600;">{{ row.recruitCount }}人</span>
            </template>
          </el-table-column>

          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <span class="status-tag" :class="`status-tag-${row.status}`">
                {{ getStatusLabel(row.status) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="发布时间" width="120">
            <template #default="{ row }">
              <span style="color: var(--text-secondary); font-size: 0.875rem;">{{ row.publishTime }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" link size="small" @click="handleView(row)">
                  查看
                </el-button>
                <el-button type="primary" link size="small" @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="handleDelete(row)">
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页器 -->
      <div class="pagination-container">
        <div class="pagination-info">
          共 {{ total }} 条记录,当前第 {{ currentPage }} / {{ totalPages }} 页
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { recruitmentApi } from '@/api'

const router = useRouter()

// 加载状态
const loading = ref(false)
const exporting = ref(false)

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref('')
const filterJobType = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 选中的行
const selectedRows = ref([])

// 职位列表
const positions = ref([])

// 计算属性
const filteredPositions = computed(() => {
  let result = positions.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter((position: any) =>
      position.name.toLowerCase().includes(keyword) ||
      position.companyName.toLowerCase().includes(keyword)
    )
  }

  if (filterStatus.value) {
    result = result.filter((position: any) => position.status === filterStatus.value)
  }

  if (filterJobType.value) {
    result = result.filter((position: any) => position.jobType === filterJobType.value)
  }

  total.value = result.length

  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return result.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

// 方法
const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'active': '招聘中',
    'paused': '已暂停',
    'closed': '已结束'
  }
  return map[status] || status
}

const getJobTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    'fulltime': '全职',
    'parttime': '兼职',
    'intern': '实习'
  }
  return map[type] || type
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilterChange = () => {
  currentPage.value = 1
}

const handleResetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  filterJobType.value = ''
  currentPage.value = 1
}

const handleSelectionChange = (selection: any) => {
  selectedRows.value = selection
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const goToPublish = () => {
  router.push('/employment/publish')
}

const handleView = (row: any) => {
  ElMessage.info('查看职位详情')
  // TODO: 打开详情对话框或跳转到详情页
}

const handleEdit = (row: any) => {
  ElMessage.info('编辑职位')
  // TODO: 跳转到编辑页面
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除职位"${row.name}"吗?此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // TODO: 调用后端 API 删除
    // await recruitmentApi.deletePosition(row.id)

    const index = positions.value.findIndex((p: any) => p.id === row.id)
    if (index > -1) {
      positions.value.splice(index, 1)
    }

    ElMessage.success('删除成功!')
  } catch (error) {
    // 用户取消
  }
}

const handleExport = async () => {
  exporting.value = true
  try {
    // TODO: 调用后端 API 导出
    await new Promise(resolve => setTimeout(resolve, 1500))
    ElMessage.success('导出成功!')
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// 加载职位列表
const loadPositions = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    // const data = await recruitmentApi.getPositionList()
    // positions.value = data

    // 模拟数据
    positions.value = [
      {
        id: 1,
        name: 'Java开发工程师',
        companyName: '阿里巴巴',
        location: '北京市朝阳区',
        salaryRange: '15K-25K',
        jobType: 'fulltime',
        recruitCount: 5,
        status: 'active',
        publishTime: '2025-01-10'
      },
      {
        id: 2,
        name: 'Vue.js前端开发',
        companyName: '腾讯科技',
        location: '深圳市南山区',
        salaryRange: '12K-20K',
        jobType: 'fulltime',
        recruitCount: 3,
        status: 'active',
        publishTime: '2025-01-08'
      },
      {
        id: 3,
        name: 'Python数据分析',
        companyName: '字节跳动',
        location: '北京市海淀区',
        salaryRange: '18K-30K',
        jobType: 'fulltime',
        recruitCount: 2,
        status: 'paused',
        publishTime: '2025-01-05'
      }
    ]
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPositions()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;

  .page-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0;
    color: var(--text-primary);
  }

  .page-actions {
    display: flex;
    gap: 0.75rem;
  }
}

.toolbar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;

  .toolbar-search {
    flex: 1;
    min-width: 200px;
  }

  .toolbar-filters {
    display: flex;
    gap: 0.75rem;
    flex-wrap: wrap;
  }
}

.data-card {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: 1.5rem;
}

.table-container {
  margin-bottom: 1rem;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid var(--border);

  .pagination-info {
    color: var(--text-secondary);
    font-size: 0.875rem;
  }
}

.position-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;

  .position-icon {
    width: 40px;
    height: 40px;
    background: var(--gradient-blue);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 1.25rem;
  }

  .position-info {
    .position-name {
      font-weight: 500;
      color: var(--text-primary);
    }

    .position-company {
      font-size: 0.875rem;
      color: var(--text-secondary);
    }
  }
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;

  &.status-tag-active {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.status-tag-paused {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }

  &.status-tag-closed {
    background: oklch(0.95 0.01 240);
    color: oklch(0.40 0.10 240);
  }
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
</style>
