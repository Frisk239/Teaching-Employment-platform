<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">求职管理</h1>
      <div class="page-actions">
        <el-button @click="handleExport" :loading="exporting">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选工具栏 -->
    <div class="toolbar">
      <div class="toolbar-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学生姓名或职位名称..."
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
          placeholder="申请状态"
          clearable
          @change="handleFilterChange"
          style="width: 120px;"
        >
          <el-option label="全部状态" value=""></el-option>
          <el-option label="待审核" value="pending"></el-option>
          <el-option label="面试中" value="interview"></el-option>
          <el-option label="已通过" value="accepted"></el-option>
          <el-option label="已拒绝" value="rejected"></el-option>
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
          :data="filteredApplications"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>

          <el-table-column label="学生信息" min-width="150">
            <template #default="{ row }">
              <div class="student-cell">
                <div class="student-avatar">{{ row.studentName.charAt(0) }}</div>
                <div class="student-info">
                  <div class="student-name">{{ row.studentName }}</div>
                  <div class="student-contact">{{ row.studentPhone }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="职位信息" min-width="200">
            <template #default="{ row }">
              <div class="position-cell">
                <div class="position-name">{{ row.positionName }}</div>
                <div class="position-company">{{ row.companyName }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="申请状态" width="100" align="center">
            <template #default="{ row }">
              <span class="status-tag" :class="`status-tag-${row.status}`">
                {{ getStatusLabel(row.status) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="申请时间" width="120">
            <template #default="{ row }">
              <span style="color: var(--text-secondary); font-size: 0.875rem;">{{ row.applyTime }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" link size="small" @click="handleView(row)">
                  查看
                </el-button>
                <el-button type="success" link size="small" @click="handleApprove(row)" v-if="row.status === 'pending'">
                  通过
                </el-button>
                <el-button type="danger" link size="small" @click="handleReject(row)" v-if="row.status === 'pending'">
                  拒绝
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { applicationApi } from '@/api'

// 加载状态
const loading = ref(false)
const exporting = ref(false)

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 选中的行
const selectedRows = ref([])

// 申请列表
const applications = ref([])

// 计算属性
const filteredApplications = computed(() => {
  let result = applications.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter((app: any) =>
      app.studentName.toLowerCase().includes(keyword) ||
      app.positionName.toLowerCase().includes(keyword)
    )
  }

  if (filterStatus.value) {
    result = result.filter((app: any) => app.status === filterStatus.value)
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
    'pending': '待审核',
    'interview': '面试中',
    'accepted': '已通过',
    'rejected': '已拒绝'
  }
  return map[status] || status
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

const handleView = (row: any) => {
  ElMessage.info('查看申请详情')
  // TODO: 打开详情对话框
}

const handleApprove = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定通过学生"${row.studentName}"的求职申请吗?`,
      '确认通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    // TODO: 调用后端 API
    // await applicationApi.approveApplication(row.id)

    const index = applications.value.findIndex((a: any) => a.id === row.id)
    if (index > -1) {
      applications.value[index].status = 'accepted'
    }

    ElMessage.success('已通过申请!')
  } catch (error) {
    // 用户取消
  }
}

const handleReject = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定拒绝学生"${row.studentName}"的求职申请吗?`,
      '确认拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // TODO: 调用后端 API
    // await applicationApi.rejectApplication(row.id)

    const index = applications.value.findIndex((a: any) => a.id === row.id)
    if (index > -1) {
      applications.value[index].status = 'rejected'
    }

    ElMessage.success('已拒绝申请!')
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

// 加载申请列表
const loadApplications = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    // const data = await applicationApi.getApplicationList()
    // applications.value = data

    // 模拟数据
    applications.value = [
      {
        id: 1,
        studentName: '张三',
        studentPhone: '13800138000',
        positionName: 'Java开发工程师',
        companyName: '阿里巴巴',
        status: 'pending',
        applyTime: '2025-01-10'
      },
      {
        id: 2,
        studentName: '李四',
        studentPhone: '13900139000',
        positionName: 'Vue.js前端开发',
        companyName: '腾讯科技',
        status: 'interview',
        applyTime: '2025-01-08'
      },
      {
        id: 3,
        studentName: '王五',
        studentPhone: '13700137000',
        positionName: 'Python数据分析',
        companyName: '字节跳动',
        status: 'accepted',
        applyTime: '2025-01-05'
      }
    ]
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadApplications()
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

.student-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;

  .student-avatar {
    width: 40px;
    height: 40px;
    background: var(--gradient-green);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
  }

  .student-info {
    .student-name {
      font-weight: 500;
      color: var(--text-primary);
    }

    .student-contact {
      font-size: 0.875rem;
      color: var(--text-secondary);
    }
  }
}

.position-cell {
  .position-name {
    font-weight: 500;
    color: var(--text-primary);
  }

  .position-company {
    font-size: 0.875rem;
    color: var(--text-secondary);
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

  &.status-tag-pending {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }

  &.status-tag-interview {
    background: oklch(0.92 0.04 220);
    color: oklch(0.55 0.15 220);
  }

  &.status-tag-accepted {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.status-tag-rejected {
    background: oklch(0.95 0.02 25);
    color: oklch(0.55 0.22 25);
  }
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
</style>
