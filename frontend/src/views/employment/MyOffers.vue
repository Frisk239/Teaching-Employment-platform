<template>
  <div class="my-offers">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOffers || 0 }}</div>
              <div class="stat-label">全部Offer</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOffers || 0 }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><Select /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.acceptedOffers || 0 }}</div>
              <div class="stat-label">已接受</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="28"><Close /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.rejectedOffers || 0 }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-bar">
        <el-radio-group v-model="filterStatus" @change="loadOffers">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="pending">待接受</el-radio-button>
          <el-radio-button label="accepted">已接受</el-radio-button>
          <el-radio-button label="rejected">已拒绝</el-radio-button>
          <el-radio-button label="expired">已过期</el-radio-button>
        </el-radio-group>

        <div style="margin-left: auto">
          <el-button :icon="Refresh" @click="loadOffers">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- Offer列表 -->
    <div v-loading="loading" class="offer-list">
      <!-- 空状态 -->
      <el-empty
        v-if="offerList.length === 0 && !loading"
        description="暂无Offer记录"
        :image-size="200"
      >
        <template #description>
          <div style="text-align: center">
            <p style="color: #909399; margin-bottom: 16px">
              还没有收到Offer，继续加油投递简历吧！
            </p>
            <el-button type="primary" @click="$router.push('/employment/positions')">
              查看职位列表
            </el-button>
          </div>
        </template>
      </el-empty>

      <!-- Offer卡片列表 -->
      <div v-else class="offer-cards">
        <el-card
          v-for="offer in offerList"
          :key="offer.id"
          shadow="hover"
          class="offer-card"
          :class="getOfferCardClass(offer.status)"
        >
          <!-- 卡片头部 -->
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <div class="company-info">
                  <el-image
                    v-if="offer.companyLogo"
                    :src="offer.companyLogo"
                    fit="contain"
                    class="company-logo"
                  />
                  <el-icon v-else class="company-logo-placeholder">
                    <OfficeBuilding />
                  </el-icon>
                  <div>
                    <div class="company-name">{{ offer.companyName }}</div>
                    <div class="offer-no">{{ offer.offerNo }}</div>
                  </div>
                </div>
              </div>
              <div class="header-right">
                <el-tag :type="getStatusType(offer.status)" size="large">
                  {{ getStatusText(offer.status) }}
                </el-tag>
              </div>
            </div>
          </template>

          <!-- 卡片内容 -->
          <div class="card-content">
            <div class="position-info">
              <h3 class="position-name">{{ offer.positionName }}</h3>
              <div class="position-meta">
                <span class="meta-item">
                  <el-icon><Location /></el-icon>
                  {{ offer.city }}
                </span>
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  入职日期：{{ offer.onboardDate }}
                </span>
              </div>
            </div>

            <div class="salary-info">
              <div class="salary-label">薪资待遇</div>
              <div class="salary-value">
                ¥{{ offer.salary }}
                <span class="salary-unit">/{{ offer.salaryUnit === 'month' ? '月' : '年' }}</span>
              </div>
            </div>

            <div class="deadline-info" v-if="offer.status === 'pending'">
              <el-alert
                :title="`接受截止：${offer.acceptDeadline}`"
                :type="getDeadlineType(offer.acceptDeadline)"
                :closable="false"
                show-icon
              >
                <template #default>
                  <div class="deadline-content">
                    <span>距离截止还有：</span>
                    <count-down
                      :deadline="offer.acceptDeadline"
                      @expired="handleExpired(offer)"
                    />
                  </div>
                </template>
              </el-alert>
            </div>

            <div class="remark-info" v-if="offer.remark">
              <div class="remark-label">备注：</div>
              <div class="remark-text">{{ offer.remark }}</div>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-button size="large" @click="handleViewDetail(offer)">
                查看详情
              </el-button>
              <el-button
                v-if="offer.attachmentUrl"
                size="large"
                @click="handleViewAttachment(offer)"
              >
                <el-icon><Download /></el-icon>
                下载附件
              </el-button>
              <template v-if="offer.status === 'pending'">
                <el-button
                  type="success"
                  size="large"
                  @click="handleAccept(offer)"
                >
                  接受Offer
                </el-button>
                <el-button
                  type="danger"
                  size="large"
                  @click="handleReject(offer)"
                >
                  拒绝Offer
                </el-button>
              </template>
            </div>

            <!-- 时间信息 -->
            <div class="time-info">
              <span>发送时间：{{ offer.createTime }}</span>
              <span v-if="offer.acceptTime" style="margin-left: 20px">
                接受时间：{{ offer.acceptTime }}
              </span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadOffers"
        @current-change="loadOffers"
        class="pagination"
      />
    </div>

    <!-- Offer详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="Offer详情"
      width="700px"
      @open="loadOfferDetail"
    >
      <div v-loading="detailLoading">
        <el-descriptions v-if="currentOffer" :column="2" border>
          <el-descriptions-item label="Offer编号" :span="2">
            {{ currentOffer.offerNo }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentOffer.status)">
              {{ getStatusText(currentOffer.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="邮件状态">
            <el-tag :type="getEmailStatusType(currentOffer.emailStatus)" size="small">
              {{ getEmailStatusText(currentOffer.emailStatus) }}
            </el-tag>
          </el-descriptions-item>

          <el-descriptions-item label="企业名称" :span="2">
            {{ currentOffer.companyName }}
          </el-descriptions-item>

          <el-descriptions-item label="职位名称" :span="2">
            {{ currentOffer.positionName }}
          </el-descriptions-item>

          <el-descriptions-item label="入职城市">
            {{ currentOffer.city }}
          </el-descriptions-item>
          <el-descriptions-item label="薪资">
            <span style="color: #67c23a; font-weight: 600">
              ¥{{ currentOffer.salary }} / {{ currentOffer.salaryUnit === 'month' ? '月' : '年' }}
            </span>
          </el-descriptions-item>

          <el-descriptions-item label="入职日期">
            {{ currentOffer.onboardDate }}
          </el-descriptions-item>
          <el-descriptions-item label="接受截止">
            {{ currentOffer.acceptDeadline }}
          </el-descriptions-item>

          <el-descriptions-item label="接受时间" :span="2" v-if="currentOffer.acceptTime">
            {{ currentOffer.acceptTime }}
          </el-descriptions-item>

          <el-descriptions-item label="备注" :span="2" v-if="currentOffer.remark">
            {{ currentOffer.remark }}
          </el-descriptions-item>

          <el-descriptions-item label="发送时间" :span="2">
            {{ currentOffer.createTime }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 附件下载 -->
        <div v-if="currentOffer?.attachmentUrl" class="attachment-section">
          <el-divider />
          <div class="attachment-title">Offer附件：</div>
          <el-button type="primary" :icon="Download" @click="handleViewAttachment(currentOffer)">
            下载Offer文件
          </el-button>
        </div>
      </div>

      <template #footer v-if="currentOffer?.status === 'pending'">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="danger" @click="handleRejectFromDialog">拒绝Offer</el-button>
        <el-button type="success" @click="handleAcceptFromDialog">接受Offer</el-button>
      </template>
      <template #footer v-else>
        <el-button type="primary" @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝Offer"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因（可选）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejecting">
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, defineComponent, h } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document, Clock, Select, Close, Refresh, OfficeBuilding, Location,
  Calendar, Download
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores'

// 倒计时组件
const CountDown = defineComponent({
  props: {
    deadline: String
  },
  emits: ['expired'],
  setup(props: any, { emit }) {
    const remaining = ref('')

    const calculateRemaining = () => {
      if (!props.deadline) return

      const deadline = new Date(props.deadline).getTime()
      const now = new Date().getTime()
      const diff = deadline - now

      if (diff <= 0) {
        remaining.value = '已过期'
        emit('expired')
        return
      }

      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))

      if (days > 0) {
        remaining.value = `${days}天${hours}小时`
      } else if (hours > 0) {
        remaining.value = `${hours}小时${minutes}分钟`
      } else {
        remaining.value = `${minutes}分钟`
      }
    }

    const timer = setInterval(calculateRemaining, 60000)
    calculateRemaining()

    return { remaining }
  },
  render() {
    return h('span', { class: 'countdown' }, this.remaining)
  }
})

const authStore = useAuthStore()

// 统计数据
const stats = ref({
  totalOffers: 0,
  pendingOffers: 0,
  acceptedOffers: 0,
  rejectedOffers: 0
})

// 筛选状态
const filterStatus = ref('')

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// Offer列表
const offerList = ref([])
const total = ref(0)
const loading = ref(false)

// 详情对话框
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const currentOffer = ref<any>(null)

// 拒绝对话框
const rejectDialogVisible = ref(false)
const rejecting = ref(false)
const rejectForm = reactive({
  reason: ''
})
const rejectOfferId = ref<number | null>(null)

// 加载Offer列表
const loadOffers = async () => {
  loading.value = true
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) {
      ElMessage.warning('未找到学员信息')
      return
    }

    const response: any = await request.get('/offer/page', {
      params: {
        current: pagination.current,
        size: pagination.size,
        studentId,
        status: filterStatus.value || undefined
      }
    })

    // 为每个offer添加公司信息（这里简化处理，实际应该从后端返回）
    const records = (response.records || []).map((offer: any) => ({
      ...offer,
      companyName: offer.companyName || '企业名称',
      companyLogo: offer.companyLogo || null
    }))

    offerList.value = records
    total.value = response.total || 0

    // 加载统计数据
    await loadStats()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载Offer列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) return

    // 获取所有offer进行统计
    const allResponse: any = await request.get('/offer/page', {
      params: { studentId, current: 1, size: 1000 }
    })

    const allOffers = allResponse.records || []
    stats.value = {
      totalOffers: allOffers.length,
      pendingOffers: allOffers.filter((o: any) => o.status === 'pending').length,
      acceptedOffers: allOffers.filter((o: any) => o.status === 'accepted').length,
      rejectedOffers: allOffers.filter((o: any) => o.status === 'rejected').length
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    pending: 'warning',
    accepted: 'success',
    rejected: 'danger',
    cancelled: 'info',
    expired: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待接受',
    accepted: '已接受',
    rejected: '已拒绝',
    cancelled: '已取消',
    expired: '已过期'
  }
  return textMap[status] || status
}

const getEmailStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    pending: 'info',
    sent: 'success',
    failed: 'danger'
  }
  return typeMap[status] || 'info'
}

const getEmailStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待发送',
    sent: '已发送',
    failed: '发送失败'
  }
  return textMap[status] || status
}

// 获取截止日期类型
const getDeadlineType = (deadline: string) => {
  const now = new Date().getTime()
  const deadlineTime = new Date(deadline).getTime()
  const diff = deadlineTime - now

  if (diff <= 0) return 'error'
  if (diff < 24 * 60 * 60 * 1000) return 'warning' // 24小时内
  return 'success'
}

// 获取卡片样式类
const getOfferCardClass = (status: string) => {
  return {
    'offer-card-pending': status === 'pending',
    'offer-card-accepted': status === 'accepted',
    'offer-card-rejected': status === 'rejected',
    'offer-card-expired': status === 'expired'
  }
}

// 查看详情
const handleViewDetail = async (offer: any) => {
  currentOffer.value = offer
  detailDialogVisible.value = true
}

const loadOfferDetail = async () => {
  if (!currentOffer.value) return

  detailLoading.value = true
  try {
    const response: any = await request.get(`/offer/${currentOffer.value.id}`)
    currentOffer.value = response
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 查看附件
const handleViewAttachment = (offer: any) => {
  if (offer.attachmentUrl) {
    window.open(offer.attachmentUrl, '_blank')
  } else {
    ElMessage.warning('暂无附件')
  }
}

// 接受Offer
const handleAccept = async (offer: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要接受 ${offer.companyName} 的Offer吗？`,
      '接受Offer',
      {
        confirmButtonText: '确定接受',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    await request.post(`/offer/${offer.id}/accept`)
    ElMessage.success('已接受Offer！恭喜您！')
    detailDialogVisible.value = false
    loadOffers()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleAcceptFromDialog = () => {
  if (currentOffer.value) {
    handleAccept(currentOffer.value)
  }
}

// 拒绝Offer
const handleReject = (offer: any) => {
  rejectOfferId.value = offer.id
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectOfferId.value) return

  rejecting.value = true
  try {
    await request.post(`/offer/${rejectOfferId.value}/reject`)
    ElMessage.success('已拒绝Offer')
    rejectDialogVisible.value = false
    detailDialogVisible.value = false
    loadOffers()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    rejecting.value = false
  }
}

const handleRejectFromDialog = () => {
  detailDialogVisible.value = false
  if (currentOffer.value) {
    handleReject(currentOffer.value)
  }
}

// Offer过期处理
const handleExpired = (offer: any) => {
  offer.status = 'expired'
}

onMounted(() => {
  loadOffers()
})
</script>

<style scoped lang="scss">
.my-offers {
  padding: 20px;

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
            line-height: 1;
            margin-bottom: 8px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .filter-card {
    margin-bottom: 20px;

    .filter-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .offer-list {
    min-height: 400px;

    .offer-cards {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
      gap: 20px;

      .offer-card {
        border-left-width: 4px;
        border-left-style: solid;

        &.offer-card-pending {
          border-left-color: #e6a23c;
        }

        &.offer-card-accepted {
          border-left-color: #67c23a;
        }

        &.offer-card-rejected {
          border-left-color: #f56c6c;
          opacity: 0.7;
        }

        &.offer-card-expired {
          border-left-color: #909399;
          opacity: 0.6;
        }

        :deep(.el-card__header) {
          padding: 16px 20px;
          background: #fafafa;
        }

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .company-info {
            display: flex;
            align-items: center;
            gap: 12px;

            .company-logo {
              width: 48px;
              height: 48px;
              border-radius: 8px;
            }

            .company-logo-placeholder {
              width: 48px;
              height: 48px;
              font-size: 24px;
              color: #c0c4cc;
            }

            .company-name {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
              margin-bottom: 4px;
            }

            .offer-no {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }

      .card-content {
        .position-info {
          margin-bottom: 20px;

          .position-name {
            font-size: 20px;
            font-weight: 600;
            color: #303133;
            margin: 0 0 12px 0;
          }

          .position-meta {
            display: flex;
            gap: 20px;
            color: #606266;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }

        .salary-info {
          display: flex;
          align-items: center;
          gap: 16px;
          padding: 16px;
          background: #f0f9ff;
          border-radius: 8px;
          margin-bottom: 16px;

          .salary-label {
            font-size: 14px;
            color: #606266;
          }

          .salary-value {
            font-size: 24px;
            font-weight: 600;
            color: #67c23a;

            .salary-unit {
              font-size: 14px;
              margin-left: 4px;
            }
          }
        }

        .deadline-info {
          margin-bottom: 16px;

          .deadline-content {
            display: flex;
            align-items: center;
            gap: 8px;
          }

          .countdown {
            font-weight: 600;
          }
        }

        .remark-info {
          display: flex;
          gap: 8px;
          margin-bottom: 16px;
          padding: 12px;
          background: #fafafa;
          border-radius: 8px;

          .remark-label {
            font-weight: 500;
            color: #606266;
            flex-shrink: 0;
          }

          .remark-text {
            color: #606266;
          }
        }

        .card-actions {
          display: flex;
          gap: 12px;
          margin-bottom: 16px;
          flex-wrap: wrap;
        }

        .time-info {
          font-size: 12px;
          color: #909399;
          border-top: 1px solid #ebeef5;
          padding-top: 12px;
        }
      }
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .attachment-section {
    .attachment-title {
      font-weight: 500;
      margin-bottom: 12px;
    }
  }
}

// 响应式布局
@media (max-width: 768px) {
  .my-offers {
    padding: 12px;

    .stats-row {
      .el-col {
        margin-bottom: 12px;
      }
    }

    .offer-list {
      .offer-cards {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
