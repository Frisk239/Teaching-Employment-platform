<template>
  <div class="permissions-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>权限管理</h2>
      <p class="description">管理系统中的所有权限，支持按模块分类查看</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <!-- 新增权限按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增权限
          </el-button>
        </div>

        <div class="toolbar-right">
          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="搜索权限名称、编码"
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
          <el-button :icon="Refresh" @click="loadPermissions">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- 权限列表（表格视图） -->
    <el-card class="table-card" shadow="never">
      <!-- 视图切换 -->
      <div class="view-switch">
        <el-radio-group v-model="viewMode" @change="handleViewModeChange">
          <el-radio-button value="tree">树形视图</el-radio-button>
          <el-radio-button value="table">表格视图</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 树形表格视图 -->
      <el-table
        v-if="viewMode === 'table'"
        v-loading="loading"
        :data="paginatedPermissions"
        border
        stripe
        row-key="key"
        :tree-props="{ children: 'children' }"
        :default-expand-all="false"
      >
        <!-- 权限名称列 -->
        <el-table-column prop="label" label="权限名称" min-width="200">
          <template #default="{ row }">
            <div class="permission-name">
              <el-icon v-if="row.icon" class="permission-icon">
                <component :is="row.icon" />
              </el-icon>
              <span>{{ row.label }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 权限编码列 -->
        <el-table-column prop="key" label="权限编码" min-width="200">
          <template #default="{ row }">
            <el-tag v-if="row.key" type="info" size="small">
              {{ row.key }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 描述列 -->
        <el-table-column prop="description" label="描述" min-width="250" />

        <!-- 类型列 -->
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.children" type="warning">模块</el-tag>
            <el-tag v-else type="success">权限</el-tag>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.children"
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="!row.children"
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 树形视图 -->
      <div v-else class="tree-view">
        <el-tree
          v-loading="loading"
          :data="permissionTreeData"
          :props="treeProps"
          :default-expand-all="false"
          :expand-on-click-node="false"
          node-key="key"
        >
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <div class="node-content">
                <el-icon v-if="data.icon" class="node-icon">
                  <component :is="data.icon" />
                </el-icon>
                <span class="node-label">{{ data.label }}</span>
                <el-tag v-if="data.key" size="small" type="info" class="node-code">
                  {{ data.key }}
                </el-tag>
                <el-tag v-if="data.children" type="warning" size="small" class="node-type">
                  模块
                </el-tag>
                <el-tag v-else type="success" size="small" class="node-type">
                  权限
                </el-tag>
              </div>
              <div class="node-actions">
                <el-button
                  v-if="!data.children"
                  type="primary"
                  size="small"
                  link
                  :icon="Edit"
                  @click="handleEdit(data)"
                >
                  编辑
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>

    <!-- 权限表单对话框 -->
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
        <el-form-item label="权限名称" prop="label">
          <el-input
            v-model="formData.label"
            placeholder="请输入权限名称"
          />
        </el-form-item>

        <el-form-item label="权限编码" prop="key">
          <el-input
            v-model="formData.key"
            placeholder="请输入权限编码（如：system:user:add）"
            :disabled="isEdit"
          />
          <div class="form-tip">
            权限编码格式：模块:功能:操作，例如 system:user:add
          </div>
        </el-form-item>

        <el-form-item label="所属模块" prop="module">
          <el-select v-model="formData.module" placeholder="请选择所属模块" style="width: 100%">
            <el-option label="系统管理" value="system" />
            <el-option label="教学管理" value="teaching" />
            <el-option label="就业管理" value="employment" />
            <el-option label="个人中心" value="profile" />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入权限描述"
          />
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
  Setting,
  Collection,
  OfficeBuilding,
} from '@element-plus/icons-vue'
import { deletePermissionApi } from '@/api/permission'

// 权限树节点接口
interface PermissionTreeNode {
  key: string
  label: string
  description?: string
  icon?: any
  module?: string
  children?: PermissionTreeNode[]
}

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const viewMode = ref<'tree' | 'table'>('table')
const permissionTreeData = ref<PermissionTreeNode[]>([])
const flatPermissions = ref<PermissionTreeNode[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑权限' : '新增权限'))
const isEdit = computed(() => !!formData.key)
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const formData = reactive<{
  key: string
  label: string
  module?: string
  description: string
}>({
  key: '',
  label: '',
  module: '',
  description: '',
})

// 表单验证规则
const formRules: FormRules = {
  label: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '权限名称长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  key: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { min: 5, max: 100, message: '权限编码长度在 5 到 100 个字符', trigger: 'blur' },
    { pattern: /^[a-z]+:[a-z]+:[a-z]+$/, message: '权限编码格式不正确，应为：模块:功能:操作', trigger: 'blur' },
  ],
  module: [
    { required: true, message: '请选择所属模块', trigger: 'change' },
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

// 计算属性 - 过滤后的权限列表
const filteredPermissions = computed(() => {
  if (!searchKeyword.value) {
    return flatPermissions.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return flatPermissions.value.filter(
    (permission) =>
      permission.label.toLowerCase().includes(keyword) ||
      (permission.key && permission.key.toLowerCase().includes(keyword)) ||
      (permission.description && permission.description.toLowerCase().includes(keyword))
  )
})

// 计算属性 - 分页后的权限列表
const paginatedPermissions = computed(() => {
  // 树形表格不分页，显示所有数据
  return permissionTreeData.value
})

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 加载权限列表
const loadPermissions = async () => {
  loading.value = true
  try {
    // TODO: 调用实际的API
    // const data = await permissionApi.list()

    // 模拟数据 - 从权限常量中获取
    await new Promise((resolve) => setTimeout(resolve, 500))

    // 权限树数据
    permissionTreeData.value = [
      {
        key: 'system',
        label: '系统管理',
        icon: Setting,
        children: [
          { key: 'system:user:view', label: '查看用户', description: '查看用户列表和详情' },
          { key: 'system:user:add', label: '新增用户', description: '创建新用户' },
          { key: 'system:user:edit', label: '编辑用户', description: '修改用户信息' },
          { key: 'system:user:delete', label: '删除用户', description: '删除用户' },
          { key: 'system:role:view', label: '查看角色', description: '查看角色列表和详情' },
          { key: 'system:role:add', label: '新增角色', description: '创建新角色' },
          { key: 'system:role:edit', label: '编辑角色', description: '修改角色信息' },
          { key: 'system:role:delete', label: '删除角色', description: '删除角色' },
          { key: 'system:permission:view', label: '查看权限', description: '查看权限列表' },
          { key: 'system:menu:view', label: '查看菜单', description: '查看菜单列表' },
        ],
      },
      {
        key: 'teaching',
        label: '教学管理',
        icon: Collection,
        children: [
          { key: 'teaching:school:view:all', label: '查看所有学校', description: '查看系统中所有学校' },
          { key: 'teaching:course:view:all', label: '查看所有课程', description: '查看系统中所有课程' },
          { key: 'teaching:student:view:all', label: '查看所有学员', description: '查看系统中所有学员' },
          { key: 'teaching:homework:grade:all', label: '批改所有作业', description: '批改所有学员的作业' },
        ],
      },
      {
        key: 'employment',
        label: '就业管理',
        icon: OfficeBuilding,
        children: [
          { key: 'employment:company:view:all', label: '查看所有企业', description: '查看所有企业信息' },
          { key: 'employment:position:view:all', label: '查看所有岗位', description: '查看所有招聘岗位' },
          { key: 'employment:application:view:all', label: '查看所有申请', description: '查看所有求职申请' },
        ],
      },
    ]

    // 扁平化权限列表
    flatPermissions.value = flattenPermissions(permissionTreeData.value)
  } catch (error: any) {
    ElMessage.error(error.message || '加载权限列表失败')
  } finally {
    loading.value = false
  }
}

// 扁平化权限树
const flattenPermissions = (tree: PermissionTreeNode[]): PermissionTreeNode[] => {
  const result: PermissionTreeNode[] = []

  const flatten = (nodes: PermissionTreeNode[]) => {
    nodes.forEach(node => {
      if (node.children) {
        // 如果有子节点，递归处理
        flatten(node.children)
      } else if (node.key) {
        // 如果是叶子节点且有key，添加到结果中
        result.push(node)
      }
    })
  }

  flatten(tree)
  return result
}

// 搜索
const handleSearch = () => {
  // 实时过滤，不需要额外操作
}

// 视图切换
const handleViewModeChange = () => {
  // 视图切换
}

// 新增权限
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = (row: any) => {
  formData.key = row.key
  formData.label = row.label
  formData.description = row.description || ''

  // 从key中提取模块
  if (row.key && row.key.includes(':')) {
    formData.module = row.key.split(':')[0]
  }

  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // TODO: 调用实际的API
    if (isEdit.value) {
      // await permissionApi.update(formData.key, formData)
      ElMessage.success('更新成功')
    } else {
      // await permissionApi.create(formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    await loadPermissions()
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
    key: '',
    label: '',
    module: '',
    description: '',
  })
}

// 组件挂载
onMounted(() => {
  loadPermissions()
})
</script>

<style lang="scss" scoped>
.permissions-page {
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
    .view-switch {
      margin-bottom: 20px;
      display: flex;
      justify-content: flex-end;
    }

    .permission-name {
      display: flex;
      align-items: center;
      gap: 8px;

      .permission-icon {
        font-size: 16px;
      }
    }

    .tree-view {
      padding: 20px;
      max-height: 600px;
      overflow-y: auto;

      .custom-tree-node {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 0;

        .node-content {
          display: flex;
          align-items: center;
          gap: 8px;
          flex: 1;

          .node-icon {
            font-size: 16px;
          }

          .node-label {
            font-size: 14px;
            font-weight: 500;
          }

          .node-code {
            font-size: 12px;
            font-family: 'Courier New', monospace;
          }

          .node-type {
            margin-left: 8px;
          }
        }

        .node-actions {
          display: flex;
          gap: 8px;
        }
      }
    }

    /* 滚动条样式 */
    .tree-view::-webkit-scrollbar {
      width: 6px;
    }

    .tree-view::-webkit-scrollbar-thumb {
      background-color: #dcdfe6;
      border-radius: 3px;

      &:hover {
        background-color: #c0c4cc;
      }
    }
  }

  .form-tip {
    margin-top: 4px;
    font-size: 12px;
    color: #909399;
    line-height: 1.5;
  }
}
</style>
