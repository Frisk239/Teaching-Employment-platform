/**
 * 教室相关常量
 */

export const CLASSROOM_TYPES = [
  { label: '普通教室', value: '普通教室' },
  { label: '多媒体教室', value: '多媒体教室' },
  { label: '阶梯教室', value: '阶梯教室' },
  { label: '实验室', value: '实验室' },
  { label: '计算机教室', value: '计算机教室' },
  { label: '语音教室', value: '语音教室' }
] as const

export type ClassroomType = typeof CLASSROOM_TYPES[number]['value']

export const CLASSROOM_STATUS = [
  { label: '可用', value: 1 },
  { label: '不可用', value: 0 }
] as const

export const EQUIPMENT_OPTIONS = [
  { label: '投影仪', value: 'hasProjector' },
  { label: '电脑', value: 'hasComputer' },
  { label: '多媒体设备', value: 'hasMultimedia' }
] as const
