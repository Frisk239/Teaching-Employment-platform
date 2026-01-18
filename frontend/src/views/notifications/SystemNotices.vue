<template>
  <div class="system-notices-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>系统公告</h2>
      <p class="description">查看平台最新公告和通知</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-statistic title="未读公告" :value="unreadCount">
            <template #suffix>
              <span style="color: var(--el-color-danger)">条</span>
            </template>
          </el-statistic>
        </div>

        <div class="toolbar-right">
          <!-- 全部标记已读 -->
          <el-button
            :icon="Check"
            @click="handleMarkAllRead"
            :disabled="unreadCount === 0"
          >
            全部标记已读
          </el-button>

          <!-- 刷新 -->
          <el-button :icon="Refresh" @click="loadNotices">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- 公告列表 -->
    <div class="notices-list">
      <el-card
        v-for="notice in notices"
        :key="notice.id"
        class="notice-card"
        shadow="hover"
        :class="{ unread: notice.isRead === 0 }"
      >
        <div class="notice-header">
          <div class="notice-title">
            <el-icon v-if="notice.isRead === 0" class="unread-icon" :size="20">
              <Bell />
            </el-icon>
            <h3>{{ notice.title }}</h3>
            <el-tag v-if="notice.isRead === 0" type="danger" size="small">未读</el-tag>
            <el-tag v-else type="success" size="small">已读</el-tag>
          </div>
          <div class="notice-actions">
            <el-button
              v-if="notice.isRead === 0"
              type="primary"
              size="small"
              link
              :icon="Check"
              @click="handleMarkRead(notice)"
            >
              标记已读
            </el-button>
            <el-button
              type="info"
              size="small"
              link
              :icon="View"
              @click="handleViewDetail(notice)"
            >
              查看详情
            </el-button>
          </div>
        </div>

        <div class="notice-content">
          <p>{{ truncateContent(notice.content) }}</p>
        </div>

        <div class="notice-footer">
          <span class="notice-time">
            <el-icon><Clock /></el-icon>
            {{ formatDate(notice.createTime) }}
          </span>
        </div>
      </el-card>

      <!-- 空状态 -->
      <el-empty
        v-if="notices.length === 0 && !loading"
        description="暂无系统公告"
        :image-size="200"
      />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="notices.length > 0">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadNotices"
        @current-change="loadNotices"
      />
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentNotice?.title"
      width="600px"
      @open="handleDetailOpen"
    >
      <div class="notice-detail">
        <div class="detail-meta">
          <el-tag type="warning">系统公告</el-tag>
          <span class="detail-time">
            <el-icon><Clock /></el-icon>
            {{ formatDate(currentNotice?.createTime) }}
          </span>
        </div>

        <div class="detail-content">
          {{ currentNotice?.content }}
        </div>
      </div>

      <template #footer>
        <el-button type="primary" @click="detailDialogVisible = false">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Bell,
  Check,
  Refresh,
  View,
  Clock,
} from '@element-plus/icons-vue'
import {
  getSystemNoticesApi,
  getUnreadCountApi,
  markAsReadApi,
  markAllAsReadApi,
} from '@/api/notification'
import type { Notification } from '@/api/notification'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const notices = ref<Notification[]>([])
const unreadCount = ref(0)
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentNotice = ref<Notification | null>(null)

// 截断内容显示
const truncateContent = (content: string, maxLength = 150) => {
  if (!content) return ''
  if (content.length <= maxLength) return content
  return content.substring(0, maxLength) + '...'
}

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 加载公告列表
const loadNotices = async () => {
  loading.value = true
  try {
    const result = await getSystemNoticesApi(pagination.current, pagination.size, authStore.userId)
    notices.value = result.records || []
    pagination.total = result.total || 0
  } catch (error: any) {
    console.error('加载公告列表失败:', error)
    ElMessage.error(error.message || '加载公告列表失败')
  } finally {
    loading.value = false
  }
}

// 加载未读数量
const loadUnreadCount = async () => {
  try {
    const count = await getUnreadCountApi(authStore.userId!)
    unreadCount.value = count || 0
  } catch (error: any) {
    console.error('加载未读数量失败:', error)
  }
}

// 标记单个已读
const handleMarkRead = async (notice: Notification) => {
  try {
    await markAsReadApi(notice.id!, authStore.userId!)
    notice.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
    ElMessage.success('标记成功')
  } catch (error: any) {
    console.error('标记失败:', error)
    ElMessage.error(error.message || '标记失败')
  }
}

// 全部标记已读
const handleMarkAllRead = async () => {
  try {
    await markAllAsReadApi(authStore.userId!)
    // 更新所有未读公告的状态
    notices.value.forEach(notice => {
      if (notice.isRead === 0) {
        notice.isRead = 1
      }
    })
    unreadCount.value = 0
    ElMessage.success('全部标记成功')
  } catch (error: any) {
    console.error('标记失败:', error)
    ElMessage.error(error.message || '标记失败')
  }
}

// 查看详情
const handleViewDetail = (notice: Notification) => {
  currentNotice.value = notice
  detailDialogVisible.value = true
}

// 详情对话框打开时自动标记已读
const handleDetailOpen = async () => {
  if (currentNotice.value && currentNotice.value.isRead === 0) {
    try {
      await markAsReadApi(currentNotice.value.id!, authStore.userId!)
      currentNotice.value.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('自动标记失败:', error)
    }
  }
}

// 组件挂载
onMounted(() => {
  loadNotices()
  loadUnreadCount()
})
</script>

<style lang="scss" scoped>
.system-notices-page {
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

  .notices-list {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .notice-card {
      transition: all 0.3s;

      &.unread {
        border-left: 4px solid var(--el-color-danger);
      }

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      .notice-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;

        .notice-title {
          display: flex;
          align-items: center;
          gap: 8px;
          flex: 1;

          .unread-icon {
            color: var(--el-color-danger);
            flex-shrink: 0;
          }

          h3 {
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            color: #303133;
            flex: 1;
          }
        }

        .notice-actions {
          display: flex;
          gap: 8px;
          flex-shrink: 0;
        }
      }

      .notice-content {
        margin-bottom: 12px;
        padding: 12px;
        background-color: #f5f7fa;
        border-radius: 4px;

        p {
          margin: 0;
          color: #606266;
          line-height: 1.6;
          white-space: pre-wrap;
        }
      }

      .notice-footer {
        display: flex;
        justify-content: flex-end;
        align-items: center;

        .notice-time {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 13px;
          color: #909399;

          .el-icon {
            font-size: 14px;
          }
        }
      }
    }
  }

  .pagination {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }

  .notice-detail {
    .detail-meta {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
      padding-bottom: 16px;
      border-bottom: 1px solid #ebeef5;

      .detail-time {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 14px;
        color: #909399;
      }
    }

    .detail-content {
      font-size: 15px;
      line-height: 1.8;
      color: #303133;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }
}
</style>
