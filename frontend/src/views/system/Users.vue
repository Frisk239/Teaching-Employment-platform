<template>
  <div class="users-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>用户管理</h2>
      <p class="description">管理系统中的所有用户，包括管理员、教师、学员等</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <!-- 新增用户按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增用户
          </el-button>

          <!-- 批量删除 -->
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>

          <!-- 导出用户 -->
          <el-button :icon="Download" @click="handleExport">
            导出用户
          </el-button>
        </div>

        <div class="toolbar-right">
          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名、姓名、手机号"
            clearable
            style="width: 300px"
            :prefix-icon="Search"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>

          <!-- 刷新 -->
          <el-button :icon="Refresh" @click="loadUsers">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="paginatedUsers"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <!-- 选择列 -->
        <el-table-column type="selection" width="55" align="center" />

        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <!-- 用户名列 -->
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" :src="row.avatar">
                {{ row.realName ? row.realName.charAt(0) : row.username.charAt(0) }}
              </el-avatar>
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 姓名列 -->
        <el-table-column prop="realName" label="姓名" min-width="100" />

        <!-- 手机号列 -->
        <el-table-column prop="phone" label="手机号" min-width="120" />

        <!-- 邮箱列 -->
        <el-table-column prop="email" label="邮箱" min-width="150" />

        <!-- 角色列 -->
        <el-table-column prop="roleName" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.roleCode)">
              {{ getRoleText(row.roleCode) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 学校列 -->
        <el-table-column prop="schoolName" label="所属学校" min-width="150" />

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
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:user:edit'"
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-permission="'system:user:delete'"
              type="danger"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-dropdown @command="(cmd) => handleDropdownCommand(cmd, row)">
              <el-button type="info" size="small" link :icon="MoreFilled">
                更多
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="resetPassword" :icon="RefreshRight">
                    重置密码
                  </el-dropdown-item>
                  <el-dropdown-item command="viewDetail" :icon="View">
                    查看详情
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredUsers.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
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
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="角色" prop="roleId">
          <el-select v-model="formData.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" :value="1" />
            <el-option label="学院负责人" :value="2" />
            <el-option label="教师" :value="3" />
            <el-option label="企业对接人" :value="4" />
            <el-option label="学员" :value="5" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属学校" prop="schoolId">
          <el-select v-model="formData.schoolId" placeholder="请选择学校" style="width: 100%">
            <el-option
              v-for="school in schools"
              :key="school.id"
              :label="school.name"
              :value="school.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Delete,
  Edit,
  Search,
  Refresh,
  Download,
  MoreFilled,
  RefreshRight,
  View,
} from '@element-plus/icons-vue'
import { deleteUserApi } from '@/api/user'

// 用户接口定义
interface User {
  id: number
  username: string
  realName?: string
  password?: string
  phone?: string
  email?: string
  roleId?: number
  roleCode?: string
  roleName?: string
  schoolId?: number
  schoolName?: string
  avatar?: string
  status: number
  createTime?: string
}

// 学校接口定义
interface School {
  id: number
  name: string
}

// 响应式数据
const loading = ref(false)
const users = ref<User[]>([])
const schools = ref<School[]>([])
const searchKeyword = ref('')
const selectedIds = ref<number[]>([])
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑用户' : '新增用户'))
const isEdit = computed(() => !!formData.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const formData = reactive<Partial<User>>({
  id: undefined,
  username: '',
  realName: '',
  password: '',
  phone: '',
  email: '',
  roleId: undefined,
  schoolId: undefined,
  avatar: '',
  status: 1,
})

// 表单验证规则
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' },
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  ],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

// 上传地址
const uploadUrl = computed(() => {
  return `${import.meta.env.VITE_API_BASE_URL}/file/upload`
})

// 计算属性 - 过滤后的用户列表
const filteredUsers = computed(() => {
  if (!searchKeyword.value) {
    return users.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return users.value.filter(
    (user) =>
      user.username.toLowerCase().includes(keyword) ||
      (user.realName && user.realName.toLowerCase().includes(keyword)) ||
      (user.phone && user.phone.includes(keyword)) ||
      (user.email && user.email.toLowerCase().includes(keyword))
  )
})

// 计算属性 - 分页后的用户列表
const paginatedUsers = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return filteredUsers.value.slice(startIndex, endIndex)
})

// 角色标签类型
const getRoleTagType = (roleCode?: string) => {
  const typeMap: Record<string, string> = {
    admin: 'danger',
    college_head: 'warning',
    teacher: 'success',
    user: 'info',
    enterprise_contact: 'primary',
  }
  return typeMap[roleCode || ''] || 'info'
}

// 角色文本
const getRoleText = (roleCode?: string) => {
  const textMap: Record<string, string> = {
    admin: '管理员',
    college_head: '学院负责人',
    teacher: '教师',
    user: '学员',
    enterprise_contact: '企业对接人',
  }
  return textMap[roleCode || ''] || '未知角色'
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    // TODO: 调用实际的API
    // const data = await userApi.list({ current: currentPage.value, size: pageSize.value })

    // 模拟数据
    await new Promise((resolve) => setTimeout(resolve, 500))
    users.value = [
      {
        id: 1,
        username: 'admin',
        realName: '系统管理员',
        phone: '13800138000',
        email: 'admin@example.com',
        roleId: 1,
        roleCode: 'admin',
        roleName: '管理员',
        schoolId: 1,
        schoolName: '福建师范大学',
        avatar: '',
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
      {
        id: 2,
        username: 'teacher01',
        realName: '张老师',
        phone: '13800138001',
        email: 'teacher01@example.com',
        roleId: 3,
        roleCode: 'teacher',
        roleName: '教师',
        schoolId: 1,
        schoolName: '福建师范大学',
        avatar: '',
        status: 1,
        createTime: '2024-01-02 10:00:00',
      },
      {
        id: 3,
        username: 'student01',
        realName: '李同学',
        phone: '13800138002',
        email: 'student01@example.com',
        roleId: 5,
        roleCode: 'user',
        roleName: '学员',
        schoolId: 1,
        schoolName: '福建师范大学',
        avatar: '',
        status: 1,
        createTime: '2024-01-03 10:00:00',
      },
    ]
  } catch (error: any) {
    ElMessage.error(error.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载学校列表
const loadSchools = async () => {
  try {
    // TODO: 调用实际的API
    schools.value = [
      { id: 1, name: '福建师范大学' },
      { id: 2, name: '福建农林大学' },
      { id: 3, name: '福建理工大学' },
      { id: 4, name: '福州大学' },
      { id: 5, name: '厦门大学' },
    ]
  } catch (error: any) {
    console.error('加载学校列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
}

// 选择变化
const handleSelectionChange = (selection: User[]) => {
  selectedIds.value = selection.map((item) => item.id)
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

// 新增用户
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row: User) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除用户
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await deleteUserApi(row.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // TODO: 调用实际的API
    // await userApi.batchDelete(selectedIds.value)

    users.value = users.value.filter((u) => !selectedIds.value.includes(u.id))
    selectedIds.value = []
    ElMessage.success('批量删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 导出用户
const handleExport = () => {
  // TODO: 实现导出功能
  ElMessage.info('导出功能开发中...')
}

// 状态变化
const handleStatusChange = async (row: User) => {
  try {
    // TODO: 调用实际的API
    // await userApi.updateStatus(row.id, row.status)

    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
  } catch (error: any) {
    ElMessage.error(error.message || '状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 下拉菜单命令
const handleDropdownCommand = async (command: string, row: User) => {
  switch (command) {
    case 'resetPassword':
      await handleResetPassword(row)
      break
    case 'viewDetail':
      await handleViewDetail(row)
      break
  }
}

// 重置密码
const handleResetPassword = async (row: User) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${row.username}" 的密码吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // TODO: 调用实际的API
    // await userApi.resetPassword(row.id)

    ElMessage.success('密码已重置为：123456')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '重置密码失败')
    }
  }
}

// 查看详情
const handleViewDetail = (row: User) => {
  // TODO: 实现详情查看功能
  ElMessage.info('详情查看功能开发中...')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // TODO: 调用实际的API
    if (isEdit.value) {
      // await userApi.update(formData.id, formData)
      const index = users.value.findIndex((u) => u.id === formData.id)
      if (index !== -1) {
        users.value[index] = { ...users.value[index], ...formData } as User
      }
      ElMessage.success('更新成功')
    } else {
      // const data = await userApi.create(formData)
      // users.value.unshift(data)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    await loadUsers()
  } catch (error: any) {
    if (error !== false) {
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

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: undefined,
    username: '',
    realName: '',
    password: '',
    phone: '',
    email: '',
    roleId: undefined,
    schoolId: undefined,
    avatar: '',
    status: 1,
  })
}

// 头像上传成功
const handleAvatarSuccess = (response: any) => {
  formData.avatar = response.url
  ElMessage.success('头像上传成功')
}

// 头像上传前验证
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 组件挂载
onMounted(() => {
  loadUsers()
  loadSchools()
})
</script>

<style lang="scss" scoped>
.users-page {
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
    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .username {
        font-weight: 500;
      }
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .avatar-uploader {
    .avatar {
      width: 100px;
      height: 100px;
      display: block;
      border-radius: 4px;
    }

    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }

    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 100px;
      height: 100px;
      text-align: center;
      line-height: 100px;
    }
  }
}
</style>
