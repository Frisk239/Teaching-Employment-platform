<template>
  <div class="notifications-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>通知管理</h2>
      <p class="description">管理系统中的所有通知，发送系统公告</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <!-- 发送系统公告按钮 -->
          <el-button type="primary" :icon="Bell" @click="handleSendNotice">
            发送系统公告
          </el-button>
        </div>

        <div class="toolbar-right">
          <!-- 筛选器 -->
          <el-select
            v-model="filters.type"
            placeholder="通知类型"
            clearable
            style="width: 150px"
            @change="loadNotifications"
          >
            <el-option label="系统通知" value="system" />
            <el-option label="系统公告" value="notice" />
            <el-option label="作业通知" value="homework" />
            <el-option label="日报通知" value="report" />
            <el-option label="职位通知" value="job" />
            <el-option label="申请通知" value="application" />
            <el-option label="笔试通知" value="test" />
            <el-option label="面试通知" value="interview" />
            <el-option label="Offer通知" value="offer" />
          </el-select>

          <el-select
            v-model="filters.isRead"
            placeholder="已读状态"
            clearable
            style="width: 120px"
            @change="loadNotifications"
          >
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>

          <!-- 刷新 -->
          <el-button :icon="Refresh" @click="loadNotifications">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- 通知列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="notifications"
        border
        stripe
      >
        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <!-- 用户ID列 -->
        <el-table-column prop="userId" label="接收用户" width="120" align="center">
          <template #default="{ row }">
            <div v-if="row.userId" class="user-info">
              <el-tag size="small">{{ row.realName || row.username || `用户${row.userId}` }}</el-tag>
            </div>
            <el-tag v-else type="warning" size="small">所有用户</el-tag>
          </template>
        </el-table-column>

        <!-- 类型列 -->
        <el-table-column prop="type" label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 标题列 -->
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />

        <!-- 内容列 -->
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />

        <!-- 已读状态列 -->
        <el-table-column prop="isRead" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 1 ? 'success' : 'warning'" size="small">
              {{ row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
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

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadNotifications"
          @current-change="loadNotifications"
        />
      </div>
    </el-card>

    <!-- 发送系统公告对话框 -->
    <el-dialog
      v-model="noticeDialogVisible"
      title="发送系统公告"
      width="600px"
      :close-on-click-modal="false"
      @close="handleNoticeDialogClose"
    >
      <el-form
        ref="noticeFormRef"
        :model="noticeForm"
        :rules="noticeRules"
        label-width="80px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input
            v-model="noticeForm.title"
            placeholder="请输入公告标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="noticeForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-alert
          title="提示：系统公告将发送给所有用户"
          type="info"
          :closable="false"
          show-icon
        />
      </el-form>

      <template #footer>
        <el-button @click="noticeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="sendingNotice" @click="handleSubmitNotice">
          发送公告
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Bell,
  Refresh,
  Delete,
} from '@element-plus/icons-vue'
import {
  getNotificationPageApi,
  sendSystemNoticeApi,
  deleteNotificationApi,
} from '@/api/notification'
import type { Notification } from '@/api/notification'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const notifications = ref<Notification[]>([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 筛选条件
const filters = reactive({
  type: '',
  isRead: undefined as number | undefined,
})

// 发送公告对话框
const noticeDialogVisible = ref(false)
const noticeFormRef = ref<FormInstance>()
const sendingNotice = ref(false)

// 公告表单
const noticeForm = reactive({
  title: '',
  content: '',
})

// 表单验证规则
const noticeRules: FormRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '公告标题长度在 2 到 100 个字符', trigger: 'blur' },
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, max: 1000, message: '公告内容长度在 10 到 1000 个字符', trigger: 'blur' },
  ],
}

// 获取类型标签颜色
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    system: 'danger',
    notice: 'warning',
    homework: 'primary',
    report: 'success',
    job: 'info',
    application: 'primary',
    test: 'warning',
    interview: 'success',
    offer: 'danger',
  }
  return typeMap[type] || 'info'
}

// 获取类型标签文本
const getTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    system: '系统通知',
    notice: '系统公告',
    homework: '作业通知',
    report: '日报通知',
    job: '职位通知',
    application: '申请通知',
    test: '笔试通知',
    interview: '面试通知',
    offer: 'Offer通知',
  }
  return labelMap[type] || type
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 加载通知列表
const loadNotifications = async () => {
  loading.value = true
  try {
    const result = await getNotificationPageApi({
      current: pagination.current,
      size: pagination.size,
      type: filters.type || undefined,
      isRead: filters.isRead,
    })

    notifications.value = result.records || []
    pagination.total = result.total || 0
  } catch (error: any) {
    console.error('加载通知列表失败:', error)
    ElMessage.error(error.message || '加载通知列表失败')
  } finally {
    loading.value = false
  }
}

// 发送系统公告
const handleSendNotice = () => {
  noticeDialogVisible.value = true
}

// 提交公告
const handleSubmitNotice = async () => {
  if (!noticeFormRef.value) return

  try {
    await noticeFormRef.value.validate()
    sendingNotice.value = true

    await sendSystemNoticeApi({
      title: noticeForm.title,
      content: noticeForm.content,
    })

    ElMessage.success('系统公告发送成功')
    noticeDialogVisible.value = false
    await loadNotifications()
  } catch (error: any) {
    if (error !== false) {
      console.error('发送公告失败:', error)
      ElMessage.error(error.message || '发送公告失败')
    }
  } finally {
    sendingNotice.value = false
  }
}

// 删除通知
const handleDelete = async (row: Notification) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除通知"${row.title}"吗？删除后将无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await deleteNotificationApi(row.id!, authStore.userId!)
    ElMessage.success('删除成功')
    await loadNotifications()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除通知失败:', error)
      ElMessage.error(error.message || '删除通知失败')
    }
  }
}

// 对话框关闭
const handleNoticeDialogClose = () => {
  noticeFormRef.value?.resetFields()
  Object.assign(noticeForm, {
    title: '',
    content: '',
  })
}

// 组件挂载
onMounted(() => {
  loadNotifications()
})
</script>

<style lang="scss" scoped>
.notifications-page {
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
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
