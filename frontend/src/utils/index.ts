/**
 * 高校教学就业平台 - 工具函数
 */

import dayjs from 'dayjs'

/**
 * 格式化日期时间
 */
export const formatDateTime = (date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string => {
  return dayjs(date).format(format)
}

/**
 * 格式化日期
 */
export const formatDate = (date: string | Date): string => {
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * 格式化时间
 */
export const formatTime = (date: string | Date): string => {
  return dayjs(date).format('HH:mm:ss')
}

/**
 * 相对时间
 */
export const fromNow = (date: string | Date): string => {
  return dayjs(date).fromNow()
}

/**
 * 防抖函数
 */
export const debounce = <T extends (...args: any[]) => any>(
  fn: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let timer: ReturnType<typeof setTimeout> | null = null

  return (...args: Parameters<T>) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数
 */
export const throttle = <T extends (...args: any[]) => any>(
  fn: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let timer: ReturnType<typeof setTimeout> | null = null
  let lastTime = 0

  return (...args: Parameters<T>) => {
    const now = Date.now()
    if (now - lastTime >= delay) {
      fn.apply(this, args)
      lastTime = now
    } else {
      if (timer) clearTimeout(timer)
      timer = setTimeout(() => {
        fn.apply(this, args)
        lastTime = Date.now()
      }, delay - (now - lastTime))
    }
  }
}

/**
 * 深拷贝
 */
export const deepClone = <T>(obj: T): T => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime()) as any
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as any
  if (obj instanceof Object) {
    const clonedObj: any = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
  return obj
}

/**
 * 生成 UUID
 */
export const uuid = (): string => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

/**
 * 数字格式化
 */
export const formatNumber = (num: number, decimals = 2): string => {
  return num.toFixed(decimals)
}

/**
 * 千分位格式化
 */
export const formatThousands = (num: number): string => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/**
 * 金额格式化
 */
export const formatMoney = (num: number): string => {
  return '¥' + formatThousands(num)
}

/**
 * 百分比格式化
 */
export const formatPercent = (num: number, decimals = 2): string => {
  return (num * 100).toFixed(decimals) + '%'
}

/**
 * 文件大小格式化
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

/**
 * 获取文件扩展名
 */
export const getFileExtension = (filename: string): string => {
  return filename.slice(((filename.lastIndexOf('.') - 1) >>> 0) + 2)
}

/**
 * 检查是否为图片文件
 */
export const isImageFile = (filename: string): boolean => {
  const ext = getFileExtension(filename).toLowerCase()
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'].includes(ext)
}

/**
 * 检查是否为视频文件
 */
export const isVideoFile = (filename: string): boolean => {
  const ext = getFileExtension(filename).toLowerCase()
  return ['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv', 'webm'].includes(ext)
}

/**
 * 检查是否为文档文件
 */
export const isDocumentFile = (filename: string): boolean => {
  const ext = getFileExtension(filename).toLowerCase()
  return ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt'].includes(ext)
}

/**
 * 下载文件
 */
export const downloadFile = (blob: Blob, filename: string): void => {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/**
 * 复制到剪贴板
 */
export const copyToClipboard = async (text: string): Promise<boolean> => {
  try {
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(text)
      return true
    } else {
      // 兼容性处理
      const textarea = document.createElement('textarea')
      textarea.value = text
      textarea.style.position = 'fixed'
      textarea.style.opacity = '0'
      document.body.appendChild(textarea)
      textarea.select()
      const result = document.execCommand('copy')
      document.body.removeChild(textarea)
      return result
    }
  } catch (error) {
    console.error('复制失败:', error)
    return false
  }
}

/**
 * 验证邮箱
 */
export const isValidEmail = (email: string): boolean => {
  const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return reg.test(email)
}

/**
 * 验证手机号
 */
export const isValidPhone = (phone: string): boolean => {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(phone)
}

/**
 * 验证身份证号
 */
export const isValidIdCard = (idCard: string): boolean => {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return reg.test(idCard)
}

/**
 * 获取浏览器信息
 */
export const getBrowserInfo = (): string => {
  const ua = navigator.userAgent
  let browser = 'Unknown'

  if (ua.includes('Chrome')) {
    browser = 'Chrome'
  } else if (ua.includes('Firefox')) {
    browser = 'Firefox'
  } else if (ua.includes('Safari')) {
    browser = 'Safari'
  } else if (ua.includes('Edge')) {
    browser = 'Edge'
  } else if (ua.includes('MSIE') || ua.includes('Trident')) {
    browser = 'IE'
  }

  return browser
}

/**
 * 获取操作系统
 */
export const getOS = (): string => {
  const ua = navigator.userAgent
  let os = 'Unknown'

  if (ua.includes('Windows')) {
    os = 'Windows'
  } else if (ua.includes('Mac OS')) {
    os = 'macOS'
  } else if (ua.includes('Linux')) {
    os = 'Linux'
  } else if (ua.includes('Android')) {
    os = 'Android'
  } else if (ua.includes('iOS')) {
    os = 'iOS'
  }

  return os
}

/**
 * LocalStorage 操作
 */
export const storage = {
  set(key: string, value: any): void {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('LocalStorage set error:', error)
    }
  },

  get<T = any>(key: string): T | null {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (error) {
      console.error('LocalStorage get error:', error)
      return null
    }
  },

  remove(key: string): void {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('LocalStorage remove error:', error)
    }
  },

  clear(): void {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('LocalStorage clear error:', error)
    }
  },
}

/**
 * SessionStorage 操作
 */
export const sessionStorage = {
  set(key: string, value: any): void {
    try {
      window.sessionStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('SessionStorage set error:', error)
    }
  },

  get<T = any>(key: string): T | null {
    try {
      const item = window.sessionStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (error) {
      console.error('SessionStorage get error:', error)
      return null
    }
  },

  remove(key: string): void {
    try {
      window.sessionStorage.removeItem(key)
    } catch (error) {
      console.error('SessionStorage remove error:', error)
    }
  },

  clear(): void {
    try {
      window.sessionStorage.clear()
    } catch (error) {
      console.error('SessionStorage clear error:', error)
    }
  },
}

/**
 * 获取 URL 参数
 */
export const getUrlParams = (url?: string): Record<string, string> => {
  const params: Record<string, string> = {}
  const search = url ? url.split('?')[1] : window.location.search.slice(1)

  if (search) {
    search.split('&').forEach(param => {
      const [key, value] = param.split('=')
      params[decodeURIComponent(key)] = decodeURIComponent(value || '')
    })
  }

  return params
}

/**
 * 构建 URL 参数
 */
export const buildUrlParams = (params: Record<string, any>): string => {
  return Object.keys(params)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
    .join('&')
}

/**
 * 对象转查询字符串
 */
export const objectToQueryString = (obj: Record<string, any>): string => {
  return Object.keys(obj)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(obj[key])}`)
    .join('&')
}

/**
 * 查询字符串转对象
 */
export const queryStringToObject = (query: string): Record<string, string> => {
  const params: Record<string, string> = {}
  const pairs = query.slice(1).split('&')

  pairs.forEach(pair => {
    const [key, value] = pair.split('=')
    if (key) {
      params[decodeURIComponent(key)] = decodeURIComponent(value || '')
    }
  })

  return params
}
