import { http } from '@/utils/request'
import type { Result } from './types'

export interface Notification {
  id?: number
  userId?: number
  type?: string
  title?: string
  content?: string
  relatedId?: number
  isRead?: number
  readTime?: string
  isDeleted?: number
  createTime?: string
  updateTime?: string
}

export interface NotificationPageQuery {
  current?: number
  size?: number
  userId?: number
  type?: string
  isRead?: number
}

/**
 * 分页查询通知列表
 */
export function getNotificationPageApi(params: NotificationPageQuery) {
  return http.get<any>('/notification/page', { params })
}

/**
 * 根据用户ID查询通知列表
 */
export function getNotificationsByUserIdApi(userId: number, current = 1, size = 10) {
  return http.get<any>(`/notification/user/${userId}`, { params: { current, size } })
}

/**
 * 获取用户未读通知数量
 */
export function getUnreadCountApi(userId: number) {
  return http.get<number>(`/notification/user/${userId}/unread-count`)
}

/**
 * 获取用户未读通知列表
 */
export function getUnreadNotificationsApi(userId: number) {
  return http.get<Notification[]>(`/notification/user/${userId}/unread`)
}

/**
 * 根据ID查询通知
 */
export function getNotificationByIdApi(id: number) {
  return http.get<Notification>(`/notification/${id}`)
}

/**
 * 新增通知
 */
export function createNotificationApi(data: Notification) {
  return http.post<void>('/notification', data)
}

/**
 * 更新通知
 */
export function updateNotificationApi(data: Notification) {
  return http.put<void>('/notification', data)
}

/**
 * 删除通知
 */
export function deleteNotificationApi(id: number, userId: number) {
  return http.delete<void>(`/notification/${id}`, { params: { userId } })
}

/**
 * 发送通知
 */
export function sendNotificationApi(data: {
  userId: number
  type: string
  title: string
  content: string
  relatedId?: number
}) {
  return http.post<void>('/notification/send', null, { params: data })
}

/**
 * 批量发送通知
 */
export function batchSendNotificationApi(data: {
  userIds: number[]
  type: string
  title: string
  content: string
  relatedId?: number
}) {
  return http.post<void>('/notification/batch-send', null, { params: data })
}

/**
 * 发送系统公告
 */
export function sendSystemNoticeApi(data: { title: string; content: string }) {
  return http.post<void>('/notification/send-notice', null, { params: data })
}

/**
 * 标记通知为已读
 */
export function markAsReadApi(id: number, userId: number) {
  return http.post<void>(`/notification/${id}/mark-read`, null, { params: { userId } })
}

/**
 * 批量标记通知为已读
 */
export function batchMarkAsReadApi(notificationIds: number[], userId: number) {
  return http.post<void>('/notification/batch-mark-read', null, {
    params: { notificationIds, userId }
  })
}

/**
 * 标记所有通知为已读
 */
export function markAllAsReadApi(userId: number) {
  return http.post<void>(`/notification/user/${userId}/mark-all-read`)
}

/**
 * 清空所有已读通知
 */
export function clearReadNotificationsApi(userId: number) {
  return http.post<void>(`/notification/user/${userId}/clear-read`)
}

/**
 * 获取系统公告列表（type=notice）
 */
export function getSystemNoticesApi(current = 1, size = 10, userId?: number) {
  return http.get<any>('/notification/page', {
    params: { current, size, type: 'notice', userId }
  })
}
