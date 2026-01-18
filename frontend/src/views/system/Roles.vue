<template>
  <div class="roles-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>角色管理</h2>
      <p class="description">管理系统中的所有角色，并分配相应的权限</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
        </div>

        <div class="toolbar-right">
          <!-- 刷新 -->
          <el-button :icon="Refresh" @click="loadRoles">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- 角色列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="paginatedRoles"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <!-- 选择列 -->
        <el-table-column type="selection" width="55" align="center" />

        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <!-- 角色名列 -->
        <el-table-column prop="roleName" label="角色名称" min-width="150">
          <template #default="{ row }">
            <div class="role-name">
              <el-tag :type="getRoleTagType(row.roleCode)" size="large">
                {{ row.roleName }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <!-- 角色编码列 -->
        <el-table-column prop="roleCode" label="角色编码" min-width="150">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.roleCode }}</el-tag>
          </template>
        </el-table-column>

        <!-- 描述列 -->
        <el-table-column prop="description" label="描述" min-width="200" />

        <!-- 状态列 -->
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:role:edit'"
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="info"
              size="small"
              link
              :icon="User"
              @click="handleViewUsers(row)"
            >
              查看用户
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="roles.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 角色表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑角色"
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input
            v-model="formData.roleName"
            placeholder="请输入角色名称"
          />
        </el-form-item>

        <el-form-item label="角色编码" prop="roleCode">
          <el-input
            v-model="formData.roleCode"
            placeholder="请输入角色编码（英文）"
            disabled
          />
          <div class="form-tip">
            角色编码用于系统识别，不支持修改
          </div>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 角色用户对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="`拥有「${currentRole?.roleName}」角色的用户`"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="role-users">
        <div class="user-header">
          <el-alert
            :title="`共找到 ${roleUsers.length} 个用户拥有此角色`"
            type="success"
            :closable="false"
            show-icon
          />
        </div>

        <el-table
          v-loading="loadingUsers"
          :data="paginatedUsers"
          border
          stripe
          max-height="400"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="realName" label="姓名" min-width="100" />
          <el-table-column prop="phone" label="手机号" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="150" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="userCurrentPage"
            v-model:page-size="userPageSize"
            :page-sizes="[10, 20, 50]"
            :total="roleUsers.length"
            layout="total, sizes, prev, pager, next"
            @size-change="handleUserSizeChange"
            @current-change="handleUserCurrentChange"
          />
        </div>
      </div>

      <template #footer>
        <el-button @click="userDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  Edit,
  Refresh,
  User,
} from '@element-plus/icons-vue'
import {
  getRolePageApi,
  updateRoleApi,
} from '@/api/role'
import type { Role } from '@/api/types'
import type { User as UserType } from '@/api/types'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const roles = ref<Role[]>([])
const selectedIds = ref<number[]>([])
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框相关
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 用户列表对话框
const userDialogVisible = ref(false)
const currentRole = ref<Role | null>(null)
const roleUsers = ref<UserType[]>([])
const loadingUsers = ref(false)
const userCurrentPage = ref(1)
const userPageSize = ref(10)

// 表单数据
const formData = reactive<Partial<Role>>({
  id: undefined,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1,
})

// 表单验证规则
const formRules: FormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 50, message: '角色编码长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-z_][a-z0-9_]*$/, message: '角色编码只能包含小写字母、数字和下划线，且必须以字母或下划线开头', trigger: 'blur' },
  ],
  description: [
    { max: 200, message: '描述长度不能超过 200 个字符', trigger: 'blur' },
  ],
}

// 角色标签类型
const getRoleTagType = (code: string) => {
  const typeMap: Record<string, string> = {
    admin: 'danger',
    enterprise_contact: 'primary',
  }
  return typeMap[code] || 'info'
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 计算属性 - 分页后的角色列表
const paginatedRoles = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return roles.value.slice(startIndex, endIndex)
})

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

// 选择变化
const handleSelectionChange = (selection: Role[]) => {
  selectedIds.value = selection.map(item => item.id!)
}

// 状态切换
const handleStatusChange = async (row: Role) => {
  try {
    await updateRoleApi(row)
    ElMessage.success('状态更新成功')
  } catch (error: any) {
    row.status = row.status === 1 ? 0 : 1 // 失败时回滚状态
    console.error('更新状态失败:', error)
    ElMessage.error(error.message || '更新状态失败')
  }
}

// 编辑
const handleEdit = (row: Role) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 加载角色列表
const loadRoles = async () => {
  loading.value = true
  try {
    const data = await getRolePageApi({
      current: currentPage.value,
      size: 100, // 先获取所有数据，前端进行分页和搜索
    })
    roles.value = data.records || []
  } catch (error: any) {
    console.error('加载角色列表失败:', error)
    ElMessage.error(error.message || '加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // 创建表单数据的副本
    const formDataCopy = { ...formData }

    // 更新角色
    await updateRoleApi(formDataCopy as Role)
    ElMessage.success('角色更新成功')

    dialogVisible.value = false
    await loadRoles()
  } catch (error: any) {
    if (error !== false) {
      console.error('保存角色失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 查看角色用户
const handleViewUsers = async (row: Role) => {
  currentRole.value = row
  loadingUsers.value = true
  userDialogVisible.value = true

  try {
    // 调用后端API获取拥有该角色的所有用户
    const response = await fetch(`http://localhost:8080/api/role/${row.id}/users`, {
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    const result = await response.json()
    if (result.code === 200) {
      roleUsers.value = result.data || []
    } else {
      ElMessage.error(result.message || '获取用户列表失败')
      roleUsers.value = []
    }
  } catch (error: any) {
    console.error('获取角色用户失败:', error)
    ElMessage.error('获取用户列表失败')
    roleUsers.value = []
  } finally {
    loadingUsers.value = false
  }
}

// 用户列表分页计算属性
const paginatedUsers = computed(() => {
  const startIndex = (userCurrentPage.value - 1) * userPageSize.value
  const endIndex = startIndex + userPageSize.value
  return roleUsers.value.slice(startIndex, endIndex)
})

// 用户列表分页大小变化
const handleUserSizeChange = (val: number) => {
  userPageSize.value = val
  userCurrentPage.value = 1
}

// 用户列表当前页变化
const handleUserCurrentChange = (val: number) => {
  userCurrentPage.value = val
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: undefined,
    roleName: '',
    roleCode: '',
    description: '',
    status: 1,
  })
}

// 组件挂载
onMounted(() => {
  loadRoles()
})
</script>

<style lang="scss" scoped>
.roles-page {
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

  .toolbar-card {
    margin-bottom: 20px;

    .toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .toolbar-left,
      .toolbar-right {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }
  }

  .table-card {
    .role-name {
      display: flex;
      align-items: center;
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .form-tip {
    margin-top: 4px;
    font-size: 12px;
    color: #909399;
    line-height: 1.5;
  }

  .permission-assignment {
    .permission-header {
      margin-bottom: 20px;
    }

    .permission-tree {
      max-height: 500px;
      overflow-y: auto;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      padding: 10px;

      .tree-node {
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;

        .node-label {
          flex: 1;
          font-size: 14px;
        }

        .node-code {
          font-size: 12px;
          font-family: 'Courier New', monospace;
        }
      }
    }

    /* 滚动条样式 */
    .permission-tree::-webkit-scrollbar {
      width: 6px;
    }

    .permission-tree::-webkit-scrollbar-thumb {
      background-color: #dcdfe6;
      border-radius: 3px;

      &:hover {
        background-color: #c0c4cc;
      }
    }
  }
}
</style>
