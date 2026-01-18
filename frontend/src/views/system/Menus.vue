<template>
  <div class="menus-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>菜单管理</h2>
      <p class="description">管理系统侧边栏菜单，支持拖拽排序和图标选择</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <!-- 新增菜单按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新增菜单
          </el-button>

          <!-- 展开全部 -->
          <el-button :icon="Open" @click="expandAll">
            展开全部
          </el-button>

          <!-- 折叠全部 -->
          <el-button :icon="Fold" @click="collapseAll">
            折叠全部
          </el-button>
        </div>

        <div class="toolbar-right">
          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="搜索菜单名称"
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
          <el-button :icon="Refresh" @click="loadMenus">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- 菜单列表（树形表格） -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="filteredMenus"
        border
        stripe
        row-key="id"
        :tree-props="{ children: 'children' }"
        :default-expand-all="false"
        :expand-row-keys="expandedKeys"
        ref="menuTableRef"
      >
        <!-- 菜单名称列 -->
        <el-table-column prop="menuName" label="菜单名称" min-width="200">
          <template #default="{ row }">
            <div class="menu-name">
              <el-icon v-if="row.icon" class="menu-icon">
                <component :is="getIconComponent(row.icon)" />
              </el-icon>
              <span>{{ row.menuName }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 图标列 -->
        <el-table-column label="图标" width="100" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon" class="icon-preview">
              <component :is="getIconComponent(row.icon)" />
            </el-icon>
            <span v-else class="no-icon">-</span>
          </template>
        </el-table-column>

        <!-- 路径列 -->
        <el-table-column prop="path" label="路由路径" min-width="200" />

        <!-- 组件路径列 -->
        <el-table-column prop="component" label="组件路径" min-width="200">
          <template #default="{ row }">
            <span v-if="row.component">{{ row.component }}</span>
            <el-tag v-else type="info" size="small">目录</el-tag>
          </template>
        </el-table-column>

        <!-- 排序列 -->
        <el-table-column prop="sortOrder" label="排序" width="100" align="center" />

        <!-- 状态列 -->
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="显示"
              inactive-text="隐藏"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="300" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              :icon="Plus"
              @click="handleAddChild(row)"
              v-if="!row.component || row.menuType === 'menu'"
            >
              新增子菜单
            </el-button>
            <el-button
              type="success"
              size="small"
              link
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 菜单表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="menuTreeOptions"
            :props="{ label: 'menuName', value: 'id' }"
            placeholder="请选择上级菜单（不选则为顶级菜单）"
            clearable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>

        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="formData.menuType">
            <el-radio value="menu">菜单目录</el-radio>
            <el-radio value="button">按钮权限</el-radio>
          </el-radio-group>
          <div class="form-tip">
            菜单目录：包含子菜单的分组，不可直接访问
            按钮权限：页面内的操作按钮权限
          </div>
        </el-form-item>

        <el-form-item label="菜单名称" prop="menuName">
          <el-input
            v-model="formData.menuName"
            placeholder="请输入菜单名称"
          />
        </el-form-item>

        <el-form-item label="菜单图标" prop="icon">
          <div class="icon-selector">
            <el-input
              v-model="formData.icon"
              placeholder="请输入图标名称"
              clearable
            >
              <template #prepend>
                <el-icon v-if="formData.icon">
                  <component :is="getIconComponent(formData.icon)" />
                </el-icon>
                <el-icon v-else><Picture /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" link @click="showIconPicker = true">
              选择图标
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="路由路径" prop="path">
          <el-input
            v-model="formData.path"
            placeholder="请输入路由路径，如：/system/users"
          />
        </el-form-item>

        <el-form-item label="组件路径" prop="component" v-if="formData.menuType === 'menu'">
          <el-input
            v-model="formData.component"
            placeholder="请输入组件路径，如：@/views/system/Users.vue"
          />
          <div class="form-tip">
            相对于 src/views 目录的路径，以 @/views 开头
          </div>
        </el-form-item>

        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="9999"
            controls-position="right"
          />
          <div class="form-tip">
            数值越小越靠前
          </div>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
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

    <!-- 图标选择器对话框 -->
    <el-dialog
      v-model="showIconPicker"
      title="选择图标"
      width="800px"
    >
      <div class="icon-picker">
        <el-input
          v-model="iconSearchKeyword"
          placeholder="搜索图标名称"
          clearable
          :prefix-icon="Search"
          style="margin-bottom: 20px"
        />
        <div class="icon-grid">
          <div
            v-for="icon in filteredIcons"
            :key="icon"
            class="icon-item"
            :class="{ selected: formData.icon === icon }"
            @click="selectIcon(icon)"
          >
            <el-icon :size="24">
              <component :is="icon" />
            </el-icon>
            <div class="icon-name">{{ icon }}</div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showIconPicker = false">取消</el-button>
        <el-button type="primary" @click="showIconPicker = false">
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
  Open,
  Fold,
  Picture,
} from '@element-plus/icons-vue'
import {
  getMenuTreeApi,
  createMenuApi,
  updateMenuApi,
  deleteMenuApi
} from '@/api/menu'

// 导入所有 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 菜单接口定义
interface MenuItem {
  id: number
  menuName: string
  path: string
  component?: string
  icon?: string
  parentId: number
  sortOrder: number
  menuType: string // 'menu' 或 'button'
  status: number // 0: 隐藏, 1: 显示
  permission?: string
  children?: MenuItem[]
}

// 响应式数据
const loading = ref(false)
const menus = ref<MenuItem[]>([])
const searchKeyword = ref('')
const menuTableRef = ref()
const showIconPicker = ref(false)
const iconSearchKeyword = ref('')
const expandedKeys = ref<number[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑菜单' : '新增菜单'))
const isEdit = computed(() => !!formData.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const formData = reactive<Partial<MenuItem>>({
  id: undefined,
  parentId: 0,
  menuType: 'menu',
  menuName: '',
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  status: 1,
})

// 表单验证规则
const formRules: FormRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { min: 2, max: 20, message: '菜单名称长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  path: [
    { required: true, message: '请输入路由路径', trigger: 'blur' },
    { pattern: /^\/[a-zA-Z0-9/_-]*$/, message: '路由路径格式不正确', trigger: 'blur' },
  ],
  component: [
    { pattern: /^@\/views\/[a-zA-Z0-9/_\-/]+\.vue$/, message: '组件路径格式不正确', trigger: 'blur' },
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' },
  ],
}

// 所有图标列表
const allIcons = Object.keys(ElementPlusIconsVue)

// 过滤后的图标列表
const filteredIcons = computed(() => {
  if (!iconSearchKeyword.value) {
    return allIcons
  }
  return allIcons.filter(icon =>
    icon.toLowerCase().includes(iconSearchKeyword.value.toLowerCase())
  )
})

// 菜单树选项（用于上级菜单选择）
const menuTreeOptions = computed(() => {
  return [
    { id: 0, menuName: '顶级菜单', children: menus.value }
  ]
})

// 计算属性 - 过滤后的菜单列表
const filteredMenus = computed(() => {
  if (!searchKeyword.value) {
    return menus.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  const filterMenu = (menuList: MenuItem[]): MenuItem[] => {
    return menuList.reduce((acc: MenuItem[], menu) => {
      const matchName = menu.menuName.toLowerCase().includes(keyword)
      const matchPath = menu.path.toLowerCase().includes(keyword)

      if (matchName || matchPath) {
        acc.push(menu)
      } else if (menu.children) {
        const filteredChildren = filterMenu(menu.children)
        if (filteredChildren.length > 0) {
          acc.push({ ...menu, children: filteredChildren })
        }
      }

      return acc
    }, [])
  }

  return filterMenu(menus.value)
})

// 获取图标组件
const getIconComponent = (iconName?: string) => {
  if (!iconName) return Picture
  const icon = (ElementPlusIconsVue as any)[iconName]
  return icon || Picture
}

// 加载菜单列表
const loadMenus = async () => {
  loading.value = true
  try {
    const data = await getMenuTreeApi()
    menus.value = data || []
    ElMessage.success('加载菜单列表成功')
  } catch (error: any) {
    ElMessage.error(error.message || '加载菜单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  // 实时过滤，不需要额外操作
}

// 展开全部
const expandAll = () => {
  const expandNode = (nodes: MenuItem[]) => {
    nodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        expandedKeys.value.push(node.id)
        expandNode(node.children)
      }
    })
  }
  expandedKeys.value = []
  expandNode(menus.value)
}

// 折叠全部
const collapseAll = () => {
  expandedKeys.value = []
}

// 选择图标
const selectIcon = (icon: string) => {
  formData.icon = icon
}

// 新增菜单
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 新增子菜单
const handleAddChild = (row: MenuItem) => {
  resetForm()
  formData.parentId = row.id
  formData.menuType = 'menu' // 子菜单必须是菜单类型
  dialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row: MenuItem) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除菜单
const handleDelete = async (row: MenuItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜单"${row.menuName}"吗？删除后将无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await deleteMenuApi(row.id!)
    ElMessage.success('删除成功')
    await loadMenus()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 状态变化
const handleStatusChange = async (row: MenuItem) => {
  try {
    const previousStatus = row.status
    await updateMenuApi({ id: row.id, status: row.status })
    ElMessage.success(row.status === 1 ? '已显示' : '已隐藏')
  } catch (error: any) {
    ElMessage.error(error.message || '状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await updateMenuApi(formData)
      ElMessage.success('更新成功')
    } else {
      await createMenuApi(formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    await loadMenus()
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
    parentId: 0,
    menuType: 'menu',
    menuName: '',
    path: '',
    component: '',
    icon: '',
    sortOrder: 0,
    status: 1,
  })
}

// 组件挂载
onMounted(() => {
  loadMenus()
})
</script>

<style lang="scss" scoped>
.menus-page {
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
    .menu-name {
      display: flex;
      align-items: center;
      gap: 8px;

      .menu-icon {
        font-size: 16px;
      }
    }

    .icon-preview {
      font-size: 20px;
      color: #606266;
    }

    .no-icon {
      color: #c0c4cc;
    }
  }

  .icon-selector {
    display: flex;
    gap: 12px;
    align-items: center;
    width: 100%;
  }

  .form-tip {
    margin-top: 4px;
    font-size: 12px;
    color: #909399;
    line-height: 1.5;
  }

  .icon-picker {
    .icon-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
      gap: 10px;
      max-height: 400px;
      overflow-y: auto;
      padding: 10px;
      border: 1px solid #dcdfe6;
      border-radius: 4px;

      .icon-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        padding: 12px;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          border-color: var(--el-color-primary);
          background-color: var(--el-color-primary-light-9);
        }

        &.selected {
          border-color: var(--el-color-primary);
          background-color: var(--el-color-primary-light-9);
          box-shadow: 0 0 0 2px var(--el-color-primary-light-7);
        }

        .icon-name {
          font-size: 10px;
          color: #909399;
          text-align: center;
          word-break: break-all;
          line-height: 1.2;
        }
      }
    }

    /* 滚动条样式 */
    .icon-grid::-webkit-scrollbar {
      width: 6px;
    }

    .icon-grid::-webkit-scrollbar-thumb {
      background-color: #dcdfe6;
      border-radius: 3px;

      &:hover {
        background-color: #c0c4cc;
      }
    }
  }
}
</style>
