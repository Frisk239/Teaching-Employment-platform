/**
 * 高校教学就业平台 - 文件上传相关 API
 */
import request, { http } from '@/utils/request'
import type { UploadFile, UploadResponse } from './types'

export const fileApi = {
  /**
   * 上传单个文件
   */
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload<UploadResponse>('/file/upload', formData)
  },

  /**
   * 批量上传文件
   */
  batchUpload: (files: File[]) => {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    return http.upload<UploadResponse[]>('/file/batch-upload', formData)
  },

  /**
   * 上传图片
   */
  uploadImage: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload<UploadResponse>('/file/upload-image', formData)
  },

  /**
   * 上传简历
   */
  uploadResume: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload<UploadResponse>('/file/upload-resume', formData)
  },

  /**
   * 删除文件
   */
  delete: (url: string) => {
    return http.post<boolean>('/file/delete', { url })
  },
}
