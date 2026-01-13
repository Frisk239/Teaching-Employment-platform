<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">企业管理</h1>
      <div class="page-actions">
        <el-button @click="handleExport" :loading="exporting">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增企业
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选工具栏 -->
    <div class="toolbar">
      <div class="toolbar-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索企业名称或联系人..."
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
          v-model="filterIndustry"
          placeholder="筛选行业"
          clearable
          @change="handleFilterChange"
          style="width: 150px;"
        >
          <el-option label="全部行业" value=""></el-option>
          <el-option
            v-for="industry in industries"
            :key="industry"
            :label="industry"
            :value="industry"
          ></el-option>
        </el-select>

        <el-select
          v-model="filterStatus"
          placeholder="筛选状态"
          clearable
          @change="handleFilterChange"
          style="width: 120px;"
        >
          <el-option label="全部状态" value=""></el-option>
          <el-option label="合作中" value="active"></el-option>
          <el-option label="暂停合作" value="inactive"></el-option>
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
          :data="filteredCompanies"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>

          <el-table-column label="企业信息" min-width="200">
            <template #default="{ row }">
              <div class="company-cell">
                <div class="company-logo">
                  <span>{{ row.name.charAt(0) }}</span>
                </div>
                <div class="company-info">
                  <div class="company-name">{{ row.name }}</div>
                  <div class="company-industry">{{ row.industry }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="contact" label="联系人" width="120"></el-table-column>

          <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>

          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <span class="status-tag" :class="`status-tag-${row.status === 1 ? 'active' : 'inactive'}`">
                {{ getStatusLabel(row.status || 1) }}
              </span>
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

    <!-- 新增/编辑企业对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑企业' : '新增企业'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="companyForm" :rules="formRules" ref="companyFormRef" label-width="100px">
        <el-form-item label="企业名称" prop="name">
          <el-input v-model="companyForm.name" placeholder="请输入企业名称"></el-input>
        </el-form-item>

        <el-form-item label="联系人" prop="contact">
          <el-input v-model="companyForm.contact" placeholder="请输入联系人姓名"></el-input>
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="companyForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>

        <el-form-item label="所属行业" prop="industry">
          <el-select v-model="companyForm.industry" placeholder="请选择所属行业" style="width: 100%;">
            <el-option
              v-for="industry in industries"
              :key="industry"
              :label="industry"
              :value="industry"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="企业规模" prop="scale">
          <el-select v-model="companyForm.scale" placeholder="请选择企业规模" style="width: 100%;">
            <el-option label="1-50人" value="1-50"></el-option>
            <el-option label="50-150人" value="50-150"></el-option>
            <el-option label="150-500人" value="150-500"></el-option>
            <el-option label="500-1000人" value="500-1000"></el-option>
            <el-option label="1000人以上" value="1000+"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="companyForm.status">
            <el-radio value="active">合作中</el-radio>
            <el-radio value="inactive">暂停合作</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ submitting ? '提交中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看企业详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="企业详情"
      width="700px"
    >
      <div v-if="currentCompany" style="padding: 1rem;">
        <div style="display: flex; gap: 2rem; margin-bottom: 2rem;">
          <div class="company-logo" style="width: 100px; height: 100px; font-size: 2rem;">
            <span>{{ currentCompany.name.charAt(0) }}</span>
          </div>
          <div style="flex: 1;">
            <h2 style="margin: 0 0 0.5rem 0; font-size: 1.5rem;">{{ currentCompany.name }}</h2>
            <p style="margin: 0.25rem 0; color: var(--text-secondary);">
              <el-icon><User /></el-icon> {{ currentCompany.contact }}
            </p>
            <p style="margin: 0.25rem 0; color: var(--text-secondary);">
              <el-icon><Phone /></el-icon> {{ currentCompany.phone }}
            </p>
          </div>
        </div>

        <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
          <div style="padding: 1rem; background: oklch(0.99 0.005 240); border-radius: 0.5rem;">
            <div style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 0.25rem;">所属行业</div>
            <div style="font-weight: 500;">{{ currentCompany.industry }}</div>
          </div>
          <div style="padding: 1rem; background: oklch(0.99 0.005 240); border-radius: 0.5rem;">
            <div style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 0.25rem;">企业规模</div>
            <div style="font-weight: 500;">{{ currentCompany.scale }}</div>
          </div>
          <div style="padding: 1rem; background: oklch(0.99 0.005 240); border-radius: 0.5rem;">
            <div style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 0.25rem;">合作状态</div>
            <span class="status-tag" :class="`status-tag-${currentCompany.status === 1 ? 'active' : 'inactive'}`">
              {{ getStatusLabel(currentCompany.status || 1) }}
            </span>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEditFromDetail">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { companyApi } from '@/api/company'
import type { Company } from '@/api/types'

// 表单引用
const companyFormRef = ref()

// 加载状态
const loading = ref(false)
const submitting = ref(false)
const exporting = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEditMode = ref(false)
const currentCompany = ref<Company | null>(null)

// 搜索和筛选
const searchKeyword = ref('')
const filterIndustry = ref('')
const filterStatus = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 选中的行
const selectedRows = ref<Company[]>([])

// 企业列表
const industries = ref(['互联网', '电子商务', '金融科技', '通信技术', '教育培训', '其他'])
const companies = ref<Company[]>([])

// 企业表单
const companyForm = reactive({
  id: undefined as number | undefined,
  name: '',
  contact: '',
  phone: '',
  industry: '',
  scale: '',
  status: 1 as number
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入企业名称', trigger: 'blur' },
    { min: 2, max: 50, message: '企业名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  industry: [
    { required: true, message: '请选择所属行业', trigger: 'change' }
  ],
  scale: [
    { required: true, message: '请选择企业规模', trigger: 'change' }
  ]
}

// 计算属性
const filteredCompanies = computed(() => {
  let result = companies.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter((company: any) =>
      company.name.toLowerCase().includes(keyword) ||
      company.contact.toLowerCase().includes(keyword)
    )
  }

  if (filterIndustry.value) {
    result = result.filter((company: any) => company.industry === filterIndustry.value)
  }

  if (filterStatus.value) {
    result = result.filter((company: any) => company.status === filterStatus.value)
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
const getStatusLabel = (status: number) => {
  const map: Record<number, string> = {
    1: '合作中',
    0: '暂停合作'
  }
  return map[status] || '未知'
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilterChange = () => {
  currentPage.value = 1
}

const handleResetFilters = () => {
  searchKeyword.value = ''
  filterIndustry.value = ''
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

const handleAdd = () => {
  isEditMode.value = false
  Object.assign(companyForm, {
    id: undefined,
    name: '',
    contact: '',
    phone: '',
    industry: '',
    scale: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: Company) => {
  isEditMode.value = true
  Object.assign(companyForm, {
    id: row.id,
    name: row.name,
    contact: row.contact,
    phone: row.phone,
    industry: row.industry || '',
    scale: row.scale || '',
    status: row.status || 1
  })
  dialogVisible.value = true
}

const handleView = (row: Company) => {
  currentCompany.value = row
  detailDialogVisible.value = true
}

const handleEditFromDetail = () => {
  detailDialogVisible.value = false
  if (currentCompany.value) {
    handleEdit(currentCompany.value)
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除企业"${row.name}"吗?此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // TODO: 调用后端 API 删除
    // await companyApi.deleteCompany(row.id)

    const index = companies.value.findIndex((c: any) => c.id === row.id)
    if (index > -1) {
      companies.value.splice(index, 1)
    }

    ElMessage.success('删除成功!')
  } catch (error) {
    // 用户取消
  }
}

const handleSubmit = async () => {
  if (!companyFormRef.value) return

  try {
    await companyFormRef.value.validate()
    submitting.value = true

    // TODO: 调用后端 API
    // if (isEditMode.value) {
    //   await companyApi.updateCompany(companyForm)
    // } else {
    //   await companyApi.createCompany(companyForm)
    // }

    await new Promise(resolve => setTimeout(resolve, 1000))

    if (isEditMode.value) {
      const index = companies.value.findIndex((c: any) => c.id === companyForm.id)
      if (index > -1) {
        companies.value[index] = { ...companies.value[index], ...companyForm }
      }
      ElMessage.success('更新成功!')
    } else {
      companies.value.unshift({ ...companyForm, id: Date.now() })
      ElMessage.success('添加成功!')
    }

    dialogVisible.value = false
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
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

// 加载企业列表
const loadCompanies = async () => {
  loading.value = true
  try {
    const result = await companyApi.getPage({
      page: currentPage.value,
      pageSize: pageSize.value,
      name: searchKeyword.value || undefined,
    })
    companies.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载企业列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCompanies()
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

.company-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;

  .company-logo {
    width: 40px;
    height: 40px;
    background: var(--gradient-orange);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    font-size: 1rem;
  }

  .company-info {
    .company-name {
      font-weight: 500;
      color: var(--text-primary);
    }

    .company-industry {
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

  &.status-tag-inactive {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
</style>
