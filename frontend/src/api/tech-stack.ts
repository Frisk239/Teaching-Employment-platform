import request from '@/utils/request'
import type { Result } from './types'

/**
 * 技术栈维度
 */
export interface TechStackDimension {
  name: string
  max: number
  value?: number
}

/**
 * 技术栈模板
 */
export interface TechStackTemplate {
  id?: number
  templateName: string
  dimensions: string // JSON 字符串
  positionType?: string
  isDefault?: number
  createTime?: string
}

/**
 * 能力雷达图数据
 */
export interface CapabilityRadarData {
  dimensions: TechStackDimension[]
}

/**
 * 获取所有技术栈模板
 */
export function getTechStackTemplateListApi(): Promise<TechStackTemplate[]> {
  return request({
    url: '/tech-stack-template/list',
    method: 'get'
  }) as Promise<TechStackTemplate[]>
}

/**
 * 根据岗位类型获取技术栈模板
 */
export function getTechStackTemplatesByPositionTypeApi(positionType: string): Promise<TechStackTemplate[]> {
  return request({
    url: `/tech-stack-template/position-type/${positionType}`,
    method: 'get'
  }) as Promise<TechStackTemplate[]>
}

/**
 * 根据ID获取技术栈模板
 */
export function getTechStackTemplateByIdApi(id: number): Promise<TechStackTemplate> {
  return request({
    url: `/tech-stack-template/${id}`,
    method: 'get'
  }) as Promise<TechStackTemplate>
}

/**
 * 创建技术栈模板
 */
export function createTechStackTemplateApi(data: TechStackTemplate) {
  return request<Result<void>>({
    url: '/tech-stack-template',
    method: 'post',
    data
  })
}

/**
 * 更新技术栈模板
 */
export function updateTechStackTemplateApi(data: TechStackTemplate) {
  return request<Result<void>>({
    url: '/tech-stack-template',
    method: 'put',
    data
  })
}

/**
 * 删除技术栈模板
 */
export function deleteTechStackTemplateApi(id: number) {
  return request<Result<void>>({
    url: `/tech-stack-template/${id}`,
    method: 'delete'
  })
}

/**
 * 解析技术栈模板的 JSON dimensions
 */
export function parseTemplateDimensions(dimensionsJson: string): TechStackDimension[] {
  try {
    const parsed = JSON.parse(dimensionsJson)
    return parsed.map((d: any) => ({
      name: d.name,
      max: d.max || 100,
      value: d.value || 50 // 使用模板中的值或默认值
    }))
  } catch (error) {
    console.error('解析技术栈模板失败:', error)
    return []
  }
}

/**
 * 将能力雷达图数据转换为 JSON 字符串
 */
export function stringifyCapabilityRadar(data: CapabilityRadarData): string {
  return JSON.stringify(data)
}

/**
 * 解析能力雷达图 JSON 字符串
 */
export function parseCapabilityRadar(jsonString: string): CapabilityRadarData {
  try {
    return JSON.parse(jsonString)
  } catch (error) {
    console.error('解析能力雷达图数据失败:', error)
    return { dimensions: [] }
  }
}
