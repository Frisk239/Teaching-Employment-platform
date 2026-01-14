/**
 * 求职申请相关常量配置
 */

// 申请状态配置
export const APPLICATION_STATUS_OPTIONS = [
  { label: '待处理', value: 'pending', type: 'warning' },
  { label: '已筛选', value: 'screened', type: 'primary' },
  { label: '笔试通过', value: 'test_passed', type: 'success' },
  { label: '笔试失败', value: 'test_failed', type: 'danger' },
  { label: '面试通过', value: 'interview_passed', type: 'success' },
  { label: '面试失败', value: 'interview_failed', type: 'danger' },
  { label: '已拒绝', value: 'rejected', type: 'danger' },
  { label: '已发Offer', value: 'offered', type: 'primary' },
  { label: '已入职', value: 'hired', type: 'success' },
  { label: '已拒绝Offer', value: 'declined', type: 'info' }
]

// 当前阶段配置
export const APPLICATION_STAGE_OPTIONS = [
  { label: '简历筛选', value: 'resume', type: 'primary' },
  { label: '笔试', value: 'test', type: 'success' },
  { label: '面试', value: 'interview', type: 'warning' },
  { label: 'Offer', value: 'offer', type: 'success' },
  { label: '已入职', value: 'hired', type: 'success' }
]

// 获取状态标签类型
export function getStatusType(status: string): string {
  const option = APPLICATION_STATUS_OPTIONS.find(item => item.value === status)
  return option?.type || 'info'
}

// 获取状态标签文本
export function getStatusLabel(status: string): string {
  const option = APPLICATION_STATUS_OPTIONS.find(item => item.value === status)
  return option?.label || status
}

// 获取阶段标签类型
export function getStageType(stage: string): string {
  const option = APPLICATION_STAGE_OPTIONS.find(item => item.value === stage)
  return option?.type || 'info'
}

// 获取阶段标签文本
export function getStageLabel(stage: string): string {
  const option = APPLICATION_STAGE_OPTIONS.find(item => item.value === stage)
  return option?.label || stage || '-'
}
