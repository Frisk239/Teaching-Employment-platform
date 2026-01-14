<template>
  <div class="teacher-management">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">教师管理</h2>
        <p class="page-description">管理所有教师信息、查看教师详情和课程安排</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增教师
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索工号、姓名、手机号..."
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="searchForm.schoolId"
            placeholder="选择学校"
            clearable
            style="width: 100%"
            @change="handleSearch"
          >
            <el-option
              v-for="school in schoolList"
              :key="school.id"
              :label="school.schoolName"
              :value="school.id"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input
            v-model="searchForm.department"
            placeholder="搜索部门..."
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :span="6">
          <el-radio-group v-model="viewMode" size="large">
            <el-radio-button value="table">
              <el-icon><List /></el-icon>
              列表视图
            </el-radio-button>
            <el-radio-button value="card">
              <el-icon><Grid /></el-icon>
              卡片视图
            </el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :span="4" style="text-align: right">
          <el-button @click="handleReset">重置筛选</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff">
              <el-icon :size="24" color="#409eff"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">教师总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff">
              <el-icon :size="24" color="#67c23a"><School /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.schools }}</div>
              <div class="stat-label">覆盖学校</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0">
              <el-icon :size="24" color="#f56c6c"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.departments }}</div>
              <div class="stat-label">涉及部门</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec">
              <el-icon :size="24" color="#e6a23c"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.courses }}</div>
              <div class="stat-label">开设课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据展示区域 -->
    <el-card class="data-card" shadow="never" v-loading="loading">
      <!-- 表格视图 -->
      <el-table
        v-if="viewMode === 'table'"
        :data="tableData"
        stripe
        style="width: 100%"
        @row-click="handleRowClick"
      >
        <el-table-column type="index" label="#" width="60" />
        <el-table-column label="教师信息" min-width="200">
          <template #default="{ row }">
            <div class="teacher-info">
              <el-avatar :size="50" :src="row.avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <div class="info-text">
                <div class="teacher-name">{{ row.realName }}</div>
                <div class="teacher-no">工号: {{ row.teacherNo }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="schoolName" label="所属学校" width="160" />
        <el-table-column prop="department" label="部门" width="140" />
        <el-table-column label="职称" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.title" size="small">{{ row.title }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="specialty" label="专业领域" min-width="150" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column label="入职日期" width="110" align="center">
          <template #default="{ row }">
            <span v-if="row.entryDate">{{ formatDate(row.entryDate) }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click.stop="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click.stop="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 卡片视图 -->
      <div v-else class="card-view">
        <el-row :gutter="16">
          <el-col :span="8" v-for="teacher in tableData" :key="teacher.id">
            <el-card class="teacher-card" shadow="hover" @click="handleRowClick(teacher)">
              <div class="card-header">
                <el-avatar :size="60" :src="teacher.avatar">
                  <el-icon><UserFilled /></el-icon>
                </el-avatar>
                <div class="header-info">
                  <div class="teacher-name">{{ teacher.realName }}</div>
                  <div class="teacher-no">{{ teacher.teacherNo }}</div>
                </div>
                <el-tag v-if="teacher.title" size="small" type="success">{{ teacher.title }}</el-tag>
              </div>
              <el-divider style="margin: 12px 0" />
              <div class="card-body">
                <div class="info-item">
                  <el-icon><School /></el-icon>
                  <span>{{ teacher.schoolName || '-' }}</span>
                </div>
                <div class="info-item">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>{{ teacher.department || '-' }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Reading /></el-icon>
                  <span>{{ teacher.specialty || '-' }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Phone /></el-icon>
                  <span>{{ teacher.phone || '-' }}</span>
                </div>
              </div>
              <el-divider style="margin: 12px 0" />
              <div class="card-footer">
                <el-button type="primary" link @click.stop="handleEdit(teacher)">
                  <el-icon><Edit /></el-icon> 编辑
                </el-button>
                <el-button type="danger" link @click.stop="handleDelete(teacher)">
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        label-position="left"
      >
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="teacherNo">
              <el-input v-model="formData.teacherNo" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="formData.birthDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">任职信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学校" prop="schoolId">
              <el-select
                v-model="formData.schoolId"
                placeholder="请选择学校"
                style="width: 100%"
              >
                <el-option
                  v-for="school in schoolList"
                  :key="school.id"
                  :label="school.schoolName"
                  :value="school.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="formData.department" placeholder="如: 计算机学院" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-select v-model="formData.title" placeholder="请选择职称" style="width: 100%">
                <el-option label="教授" value="教授" />
                <el-option label="副教授" value="副教授" />
                <el-option label="讲师" value="讲师" />
                <el-option label="助教" value="助教" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历" prop="education">
              <el-select v-model="formData.education" placeholder="请选择学历" style="width: 100%">
                <el-option label="博士" value="博士" />
                <el-option label="硕士" value="硕士" />
                <el-option label="本科" value="本科" />
                <el-option label="专科" value="专科" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业领域" prop="specialty">
              <el-input v-model="formData.specialty" placeholder="如: 人工智能" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="formData.entryDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">联系方式</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" maxlength="18" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="家庭地址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入家庭地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="个人简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入个人简介..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            <el-icon><Check /></el-icon>
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="教师详情" width="700px">
      <el-descriptions v-if="currentTeacher" :column="2" border>
        <el-descriptions-item label="工号">{{ currentTeacher.teacherNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentTeacher.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentTeacher.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ currentTeacher.birthDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属学校">{{ currentTeacher.schoolName }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentTeacher.department }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentTeacher.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ currentTeacher.education || '-' }}</el-descriptions-item>
        <el-descriptions-item label="专业领域">{{ currentTeacher.specialty || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ currentTeacher.entryDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentTeacher.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentTeacher.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号" :span="2">{{ currentTeacher.idCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家庭地址" :span="2">{{ currentTeacher.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">
          {{ currentTeacher.description || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEditFromDetail">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Download,
  Search,
  List,
  Grid,
  User,
  School,
  OfficeBuilding,
  Reading,
  UserFilled,
  Edit,
  Delete,
  Check,
  Phone
} from '@element-plus/icons-vue'
import {
  getTeacherPageApi,
  getTeacherByIdApi,
  createTeacherApi,
  updateTeacherApi,
  deleteTeacherApi
} from '@/api/teacher'
import { getSchoolListApi } from '@/api/school'

// 视图模式
const viewMode = ref<'table' | 'card'>('table')

// 搜索表单
const searchForm = reactive({
  keyword: '',
  schoolId: undefined as number | undefined,
  department: ''
})

// 统计数据
const stats = reactive({
  total: 0,
  schools: 0,
  departments: 0,
  courses: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([])
const schoolList = ref<any[]>([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑教师' : '新增教师'))
const formRef = ref<FormInstance>()

// 详情对话框
const detailVisible = ref(false)
const currentTeacher = ref<any>(null)

// 表单数据
const formData = reactive({
  id: undefined as number | undefined,
  teacherNo: '',
  realName: '',
  schoolId: undefined as number | undefined,
  department: '',
  title: '',
  education: '',
  specialty: '',
  entryDate: '',
  idCard: '',
  gender: 1,
  birthDate: '',
  phone: '',
  email: '',
  address: '',
  description: ''
})

// 表单验证规则
const formRules: FormRules = {
  teacherNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

// 格式化日期
const formatDate = (date: string) => {
  return date?.split(' ')[0] || '-'
}

// 获取学校列表
const fetchSchoolList = async () => {
  try {
    const response = await getSchoolListApi()
    schoolList.value = response || []
  } catch (error) {
    console.error('获取学校列表失败:', error)
  }
}

// 获取教师列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      schoolId: searchForm.schoolId,
      department: searchForm.department || undefined
    }
    const response = await getTeacherPageApi(params)
    tableData.value = response.records || []
    pagination.total = response.total || 0

    // 更新统计
    stats.total = pagination.total
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
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
  searchForm.keyword = ''
  searchForm.schoolId = undefined
  searchForm.department = ''
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    teacherNo: '',
    realName: '',
    schoolId: undefined,
    department: '',
    title: '',
    education: '',
    specialty: '',
    entryDate: '',
    idCard: '',
    gender: 1,
    birthDate: '',
    phone: '',
    email: '',
    address: '',
    description: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: any) => {
  try {
    const response = await getTeacherByIdApi(row.id)
    Object.assign(formData, response)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取教师信息失败:', error)
    ElMessage.error('获取教师信息失败')
  }
}

// 从详情页编辑
const handleEditFromDetail = () => {
  detailVisible.value = false
  dialogVisible.value = true
}

// 行点击查看详情
const handleRowClick = (row: any) => {
  currentTeacher.value = row
  detailVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除教师"${row.realName}"吗？删除后不可恢复！`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteTeacherApi(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateTeacherApi(formData)
          ElMessage.success('更新成功')
        } else {
          await createTeacherApi(formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

// 初始化
onMounted(() => {
  fetchSchoolList()
  fetchData()
})
</script>

<style scoped lang="scss">
.teacher-management {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-left {
      .page-title {
        font-size: 24px;
        font-weight: 600;
        margin: 0 0 8px 0;
        color: #303133;
      }

      .page-description {
        font-size: 14px;
        color: #909399;
        margin: 0;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .search-card {
    margin-bottom: 20px;
  }

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
            line-height: 1;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .data-card {
    .teacher-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .info-text {
        .teacher-name {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
        }

        .teacher-no {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .text-muted {
      color: #909399;
    }

    .card-view {
      .teacher-card {
        margin-bottom: 16px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }

        .card-header {
          display: flex;
          align-items: center;
          gap: 12px;

          .header-info {
            flex: 1;

            .teacher-name {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
              margin-bottom: 4px;
            }

            .teacher-no {
              font-size: 12px;
              color: #909399;
            }
          }
        }

        .card-body {
          .info-item {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;
            font-size: 13px;
            color: #606266;

            .el-icon {
              color: #909399;
            }
          }
        }

        .card-footer {
          display: flex;
          justify-content: space-between;
          padding-top: 8px;
        }
      }
    }
  }

  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}
</style>
