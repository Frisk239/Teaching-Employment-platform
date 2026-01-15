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
          <!-- 新增角色按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增角色
          </el-button>
        </div>

        <div class="toolbar-right">
          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="搜索角色名称、编码"
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
        <el-table-column prop="name" label="角色名称" min-width="150">
          <template #default="{ row }">
            <div class="role-name">
              <el-tag :type="getRoleTagType(row.code)" size="large">
                {{ row.name }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <!-- 角色编码列 -->
        <el-table-column prop="code" label="角色编码" min-width="150">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.code }}</el-tag>
          </template>
        </el-table-column>

        <!-- 描述列 -->
        <el-table-column prop="description" label="描述" min-width="200" />

        <!-- 权限数量列 -->
        <el-table-column label="权限数量" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="success" effect="plain">
              {{ row.permissions?.length || 0 }} 项
            </el-tag>
          </template>
        </el-table-column>

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
        <el-table-column label="操作" width="250" align="center" fixed="right">
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
              v-permission="'system:role:assign:permission'"
              type="success"
              size="small"
              link
              :icon="Key"
              @click="handleAssignPermissions(row)"
            >
              分配权限
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
          :total="filteredRoles.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 角色表单对话框 -->
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
        <el-form-item label="角色名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入角色名称"
          />
        </el-form-item>

        <el-form-item label="角色编码" prop="code">
          <el-input
            v-model="formData.code"
            placeholder="请输入角色编码（英文）"
            :disabled="isEdit"
          />
          <div class="form-tip">
            角色编码用于系统识别，创建后不可修改，建议使用小写英文和下划线
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

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="`分配权限 - ${currentRole?.name}`"
      width="800px"
      :close-on-click-modal="false"
      @close="handlePermissionDialogClose"
    >
      <div class="permission-assignment">
        <div class="permission-header">
          <el-alert
            title="选择该角色可以访问的权限"
            type="info"
            :closable="false"
            show-icon
          >
            <template #default>
              <div>已选择 <strong>{{ selectedPermissionCount }}</strong> 项权限</div>
            </template>
          </el-alert>
        </div>

        <div class="permission-tree">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTreeData"
            :props="treeProps"
            show-checkbox
            node-key="key"
            :default-checked-keys="checkedPermissions"
            :default-expanded-keys="expandedKeys"
            @check="handlePermissionCheck"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <el-icon v-if="data.icon">
                  <component :is="data.icon" />
                </el-icon>
                <span class="node-label">{{ data.label }}</span>
                <el-tag v-if="data.key" size="small" type="info" class="node-code">
                  {{ data.key }}
                </el-tag>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSavePermissions">
          保存权限
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Delete,
  Edit,
  Search,
  Refresh,
  Key,
  Setting,
  User,
  Lock,
  Menu,
  School,
  House,
  Collection,
  Avatar,
  Checked,
  DocumentAdd,
  OfficeBuilding,
  Management,
  Briefcase,
  Edit as EditIcon,
  DataAnalysis,
  ChatDotRound,
} from '@element-plus/icons-vue'

// 角色接口定义
interface Role {
  id: number
  name: string
  code: string
  description: string
  status: number
  permissions?: string[]
  createTime?: string
}

// 权限树节点接口
interface PermissionTreeNode {
  key: string
  label: string
  icon?: any
  children?: PermissionTreeNode[]
}

// 响应式数据
const loading = ref(false)
const roles = ref<Role[]>([])
const searchKeyword = ref('')
const selectedIds = ref<number[]>([])
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑角色' : '新增角色'))
const isEdit = computed(() => !!formData.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 权限分配对话框
const permissionDialogVisible = ref(false)
const currentRole = ref<Role | null>(null)
const permissionTreeRef = ref()
const checkedPermissions = ref<string[]>([])
const selectedPermissionCount = ref(0)

// 表单数据
const formData = reactive<Partial<Role>>({
  id: undefined,
  name: '',
  code: '',
  description: '',
  status: 1,
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 50, message: '角色编码长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-z_][a-z0-9_]*$/, message: '角色编码只能包含小写字母、数字和下划线，且必须以字母或下划线开头', trigger: 'blur' },
  ],
  description: [
    { max: 200, message: '描述长度不能超过 200 个字符', trigger: 'blur' },
  ],
}

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'label',
}

// 默认展开的节点
const expandedKeys = ref<string[]>(['system', 'teaching', 'employment', 'profile'])

// 权限树数据
const permissionTreeData = ref<PermissionTreeNode[]>([
  {
    key: 'system',
    label: '系统管理',
    icon: Setting,
    children: [
      { key: 'system:user:view', label: '查看用户' },
      { key: 'system:user:add', label: '新增用户' },
      { key: 'system:user:edit', label: '编辑用户' },
      { key: 'system:user:delete', label: '删除用户' },
      { key: 'system:user:export', label: '导出用户' },
      { key: 'system:role:view', label: '查看角色' },
      { key: 'system:role:add', label: '新增角色' },
      { key: 'system:role:edit', label: '编辑角色' },
      { key: 'system:role:delete', label: '删除角色' },
      { key: 'system:role:assign:permission', label: '分配权限' },
      { key: 'system:permission:view', label: '查看权限' },
      { key: 'system:permission:add', label: '新增权限' },
      { key: 'system:permission:edit', label: '编辑权限' },
      { key: 'system:permission:delete', label: '删除权限' },
      { key: 'system:menu:view', label: '查看菜单' },
      { key: 'system:menu:add', label: '新增菜单' },
      { key: 'system:menu:edit', label: '编辑菜单' },
      { key: 'system:menu:delete', label: '删除菜单' },
    ],
  },
  {
    key: 'teaching',
    label: '教学管理',
    icon: Collection,
    children: [
      {
        key: 'teaching:school',
        label: '学校管理',
        children: [
          { key: 'teaching:school:view:all', label: '查看所有学校' },
          { key: 'teaching:school:view:own', label: '查看本校' },
          { key: 'teaching:school:edit', label: '编辑学校' },
          { key: 'teaching:school:add', label: '新增学校' },
          { key: 'teaching:school:delete', label: '删除学校' },
        ],
      },
      {
        key: 'teaching:classroom',
        label: '教室管理',
        children: [
          { key: 'teaching:classroom:view:all', label: '查看所有教室' },
          { key: 'teaching:classroom:view:school', label: '查看本校教室' },
          { key: 'teaching:classroom:book', label: '预约教室' },
          { key: 'teaching:classroom:edit', label: '编辑教室' },
        ],
      },
      {
        key: 'teaching:course',
        label: '课程管理',
        children: [
          { key: 'teaching:course:view:all', label: '查看所有课程' },
          { key: 'teaching:course:view:school', label: '查看本校课程' },
          { key: 'teaching:course:view:own', label: '查看自己的课程' },
          { key: 'teaching:course:view:selected', label: '查看已选课程' },
          { key: 'teaching:course:edit', label: '编辑课程' },
          { key: 'teaching:course:add', label: '新增课程' },
          { key: 'teaching:course:delete', label: '删除课程' },
          { key: 'teaching:course:publish', label: '发布课程' },
          { key: 'teaching:course:select', label: '选择课程' },
        ],
      },
      {
        key: 'teaching:student',
        label: '学员管理',
        children: [
          { key: 'teaching:student:view:all', label: '查看所有学员' },
          { key: 'teaching:student:view:school', label: '查看本校学员' },
          { key: 'teaching:student:view:class', label: '查看班级学员' },
          { key: 'teaching:student:view:own', label: '查看自己' },
          { key: 'teaching:student:edit', label: '编辑学员' },
        ],
      },
      {
        key: 'teaching:teacher',
        label: '教师管理',
        children: [
          { key: 'teaching:teacher:view:all', label: '查看所有教师' },
          { key: 'teaching:teacher:view:school', label: '查看本校教师' },
          { key: 'teaching:teacher:edit', label: '编辑教师' },
        ],
      },
      {
        key: 'teaching:homework',
        label: '作业管理',
        children: [
          { key: 'teaching:homework:view:all', label: '查看所有作业' },
          { key: 'teaching:homework:view:school', label: '查看本校作业' },
          { key: 'teaching:homework:view:own', label: '查看自己的作业' },
          { key: 'teaching:homework:grade:all', label: '批改所有作业' },
          { key: 'teaching:homework:grade:own', label: '批改自己的作业' },
          { key: 'teaching:homework:submit', label: '提交作业' },
          { key: 'teaching:homework:publish:all', label: '发布所有作业' },
          { key: 'teaching:homework:publish:own', label: '发布自己的作业' },
        ],
      },
      {
        key: 'teaching:dailyreport',
        label: '日报管理',
        children: [
          { key: 'teaching:dailyreport:view:all', label: '查看所有日报' },
          { key: 'teaching:dailyreport:view:school', label: '查看本校日报' },
          { key: 'teaching:dailyreport:submit', label: '提交日报' },
          { key: 'teaching:dailyreport:review', label: '审阅日报' },
        ],
      },
    ],
  },
  {
    key: 'employment',
    label: '就业管理',
    icon: OfficeBuilding,
    children: [
      {
        key: 'employment:company',
        label: '企业管理',
        children: [
          { key: 'employment:company:view:all', label: '查看所有企业' },
          { key: 'employment:company:view:own', label: '查看自己的企业' },
          { key: 'employment:company:edit', label: '编辑企业' },
          { key: 'employment:company:add', label: '新增企业' },
        ],
      },
      {
        key: 'employment:position',
        label: '岗位管理',
        children: [
          { key: 'employment:position:view:all', label: '查看所有岗位' },
          { key: 'employment:position:view:own', label: '查看自己的岗位' },
          { key: 'employment:position:view:available', label: '查看可投递岗位' },
          { key: 'employment:position:edit', label: '编辑岗位' },
          { key: 'employment:position:add', label: '新增岗位' },
          { key: 'employment:position:apply', label: '投递岗位' },
        ],
      },
      {
        key: 'employment:application',
        label: '求职管理',
        children: [
          { key: 'employment:application:view:all', label: '查看所有申请' },
          { key: 'employment:application:view:school', label: '查看本校申请' },
          { key: 'employment:application:view:own', label: '查看自己的申请' },
          { key: 'employment:application:view:company', label: '查看本企业申请' },
          { key: 'employment:application:process', label: '处理申请' },
        ],
      },
      {
        key: 'employment:writtentest',
        label: '笔试管理',
        children: [
          { key: 'employment:writtentest:view:all', label: '查看所有笔试' },
          { key: 'employment:writtentest:view:own', label: '查看自己的笔试' },
          { key: 'employment:writtentest:manage', label: '管理笔试' },
          { key: 'employment:writtentest:take', label: '参加笔试' },
        ],
      },
      {
        key: 'employment:interview',
        label: '面试管理',
        children: [
          { key: 'employment:interview:view:all', label: '查看所有面试' },
          { key: 'employment:interview:view:school', label: '查看本校面试' },
          { key: 'employment:interview:view:own', label: '查看自己的面试' },
          { key: 'employment:interview:view:company', label: '查看本企业面试' },
          { key: 'employment:interview:arrange', label: '安排面试' },
          { key: 'employment:interview:attend', label: '参加面试' },
        ],
      },
      {
        key: 'employment:statistics',
        label: '统计分析',
        children: [
          { key: 'employment:statistics:view:global', label: '查看全局统计' },
          { key: 'employment:statistics:view:school', label: '查看本校统计' },
          { key: 'employment:statistics:view:company', label: '查看企业统计' },
        ],
      },
    ],
  },
  {
    key: 'profile',
    label: '个人中心',
    icon: User,
    children: [
      { key: 'profile:view', label: '查看个人中心' },
      { key: 'profile:edit', label: '编辑个人信息' },
      { key: 'profile:change:password', label: '修改密码' },
      { key: 'profile:upload:avatar', label: '上传头像' },
      { key: 'profile:view:notifications', label: '查看通知' },
    ],
  },
])

// 计算属性 - 过滤后的角色列表
const filteredRoles = computed(() => {
  if (!searchKeyword.value) {
    return roles.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return roles.value.filter(
    (role) =>
      role.name.toLowerCase().includes(keyword) ||
      role.code.toLowerCase().includes(keyword) ||
      (role.description && role.description.toLowerCase().includes(keyword))
  )
})

// 计算属性 - 分页后的角色列表
const paginatedRoles = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return filteredRoles.value.slice(startIndex, endIndex)
})

// 角色标签类型
const getRoleTagType = (code: string) => {
  const typeMap: Record<string, string> = {
    admin: 'danger',
    college_head: 'warning',
    teacher: 'success',
    user: 'info',
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

// 加载角色列表
const loadRoles = async () => {
  loading.value = true
  try {
    // TODO: 调用实际的API
    // const data = await roleApi.list()

    // 模拟数据
    await new Promise((resolve) => setTimeout(resolve, 500))
    roles.value = [
      {
        id: 1,
        name: '管理员',
        code: 'admin',
        description: '系统管理员，拥有最高权限',
        status: 1,
        permissions: ['*'],
        createTime: '2024-01-01 10:00:00',
      },
      {
        id: 2,
        name: '学院负责人',
        code: 'college_head',
        description: '学院负责人，管理学校相关事务',
        status: 1,
        permissions: [
          'teaching:school:view:own',
          'teaching:course:view:school',
          'teaching:student:view:school',
        ],
        createTime: '2024-01-02 10:00:00',
      },
      {
        id: 3,
        name: '教师',
        code: 'teacher',
        description: '教师，管理课程和学员',
        status: 1,
        permissions: [
          'teaching:course:view:own',
          'teaching:student:view:class',
          'teaching:homework:grade:own',
        ],
        createTime: '2024-01-03 10:00:00',
      },
      {
        id: 4,
        name: '学员',
        code: 'user',
        description: '学员，查看课程和提交作业',
        status: 1,
        permissions: [
          'teaching:course:view:selected',
          'teaching:homework:view:own',
          'employment:position:apply',
        ],
        createTime: '2024-01-04 10:00:00',
      },
      {
        id: 5,
        name: '企业对接人',
        code: 'enterprise_contact',
        description: '企业对接人，管理企业招聘事务',
        status: 1,
        permissions: [
          'employment:company:view:own',
          'employment:position:manage',
          'employment:application:process',
        ],
        createTime: '2024-01-05 10:00:00',
      },
    ]
  } catch (error: any) {
    ElMessage.error(error.message || '加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
}

// 选择变化
const handleSelectionChange = (selection: Role[]) => {
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

// 新增角色
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row: Role) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 状态变化
const handleStatusChange = async (row: Role) => {
  try {
    // TODO: 调用实际的API
    // await roleApi.updateStatus(row.id, row.status)

    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
  } catch (error: any) {
    ElMessage.error(error.message || '状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 分配权限
const handleAssignPermissions = (row: Role) => {
  currentRole.value = row
  checkedPermissions.value = row.permissions?.filter(p => p !== '*') || []
  selectedPermissionCount.value = checkedPermissions.value.length
  permissionDialogVisible.value = true
}

// 权限选择变化
const handlePermissionCheck = () => {
  const checkedNodes = permissionTreeRef.value?.getCheckedKeys() || []
  const halfCheckedNodes = permissionTreeRef.value?.getHalfCheckedKeys() || []
  selectedPermissionCount.value = checkedNodes.length + halfCheckedNodes.length
}

// 保存权限
const handleSavePermissions = async () => {
  if (!currentRole.value) return

  try {
    submitting.value = true

    const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
    const allPermissions = [...checkedKeys, ...halfCheckedKeys]

    // TODO: 调用实际的API
    // await roleApi.assignPermissions(currentRole.value.id, allPermissions)

    // 更新本地数据
    const role = roles.value.find(r => r.id === currentRole.value!.id)
    if (role) {
      role.permissions = allPermissions
    }

    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '保存权限失败')
  } finally {
    submitting.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // TODO: 调用实际的API
    if (isEdit.value) {
      // await roleApi.update(formData.id, formData)
      const index = roles.value.findIndex((r) => r.id === formData.id)
      if (index !== -1) {
        roles.value[index] = { ...roles.value[index], ...formData } as Role
      }
      ElMessage.success('更新成功')
    } else {
      // const data = await roleApi.create(formData)
      // roles.value.unshift(data)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    await loadRoles()
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

// 权限对话框关闭
const handlePermissionDialogClose = () => {
  currentRole.value = null
  checkedPermissions.value = []
  selectedPermissionCount.value = 0
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
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
