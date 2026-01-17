<template>
  <div class="enterprise-profile-management">
    <el-row :gutter="20">
      <!-- 左侧：企业基本信息 -->
      <el-col :span="16">
        <!-- 企业信息卡片 -->
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">企业基本信息</span>
              <el-tag :type="getVerifyStatusType(companyInfo.verifyStatus)" size="small">
                {{ getVerifyStatusText(companyInfo.verifyStatus) }}
              </el-tag>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            label-width="120px"
            :disabled="!isEditing"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="企业名称" prop="companyName">
                  <el-input v-model="formData.companyName" placeholder="请输入企业名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="企业简称" prop="shortName">
                  <el-input v-model="formData.shortName" placeholder="请输入企业简称" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="所属行业" prop="industry">
                  <el-select v-model="formData.industry" placeholder="请选择行业" style="width: 100%">
                    <el-option label="互联网/IT" value="互联网" />
                    <el-option label="金融" value="金融" />
                    <el-option label="教育" value="教育" />
                    <el-option label="医疗" value="医疗" />
                    <el-option label="制造业" value="制造业" />
                    <el-option label="零售" value="零售" />
                    <el-option label="房地产" value="房地产" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="企业规模" prop="scale">
                  <el-select v-model="formData.scale" placeholder="请选择规模" style="width: 100%">
                    <el-option label="1-10人" value="1-10人" />
                    <el-option label="11-50人" value="11-50人" />
                    <el-option label="51-200人" value="51-200人" />
                    <el-option label="201-500人" value="201-500人" />
                    <el-option label="501-1000人" value="501-1000人" />
                    <el-option label="1000人以上" value="1000人以上" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="发展阶段" prop="stage">
                  <el-select v-model="formData.stage" placeholder="请选择发展阶段" style="width: 100%">
                    <el-option label="初创期" value="startup" />
                    <el-option label="成长期" value="growth" />
                    <el-option label="成熟期" value="maturity" />
                    <el-option label="上市" value="listing" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="企业官网" prop="website">
                  <el-input v-model="formData.website" placeholder="请输入企业官网" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="所在地区" prop="address">
              <el-cascader
                v-model="addressArray"
                :options="regionOptions"
                placeholder="请选择省市区"
                style="width: 100%"
                @change="handleAddressChange"
              />
            </el-form-item>

            <el-form-item label="详细地址" prop="address">
              <el-input
                v-model="formData.address"
                type="textarea"
                :rows="2"
                placeholder="请输入详细地址"
              />
            </el-form-item>

            <el-form-item label="企业简介" prop="description">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="4"
                placeholder="请输入企业简介"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="企业福利" prop="benefits">
              <el-input
                v-model="formData.benefits"
                type="textarea"
                :rows="3"
                placeholder="请输入企业福利，如：五险一金、年终奖、带薪年假等"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="Logo" prop="logo">
              <el-upload
                class="logo-uploader"
                :action="uploadAction"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
                :before-upload="beforeLogoUpload"
              >
                <img v-if="formData.logo" :src="formData.logo" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">建议尺寸：200x200px，支持JPG、PNG格式</div>
            </el-form-item>
          </el-form>

          <div class="form-actions" v-if="!isEditing">
            <el-button type="primary" :icon="Edit" @click="handleEdit">
              编辑信息
            </el-button>
          </div>
          <div class="form-actions" v-else>
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">
              保存
            </el-button>
          </div>
        </el-card>

        <!-- 企业相册 -->
        <el-card class="album-card" shadow="never" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span class="card-title">企业相册</span>
              <el-button type="primary" size="small" :icon="Plus" @click="handleAddPhoto">
                添加照片
              </el-button>
            </div>
          </template>

          <div class="album-grid">
            <div
              v-for="(photo, index) in albumList"
              :key="index"
              class="album-item"
            >
              <el-image
                :src="photo.url"
                fit="cover"
                :preview-src-list="albumList.map(p => p.url)"
                :initial-index="index"
              />
              <div class="album-actions">
                <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  circle
                  @click="handleDeletePhoto(index)"
                />
              </div>
              <div class="album-desc">{{ photo.description }}</div>
            </div>

            <div
              v-if="albumList.length === 0"
              class="album-empty"
            >
              <el-empty description="暂无相册照片" />
            </div>
          </div>
        </el-card>

        <!-- 招聘宣传片 -->
        <el-card class="video-card" shadow="never" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span class="card-title">招聘宣传片</span>
              <el-button
                v-if="!companyInfo.recruitmentVideo"
                type="primary"
                size="small"
                :icon="VideoPlay"
                @click="handleUploadVideo"
              >
                上传视频
              </el-button>
              <template v-else>
                <el-button size="small" @click="handlePlayVideo">
                  播放
                </el-button>
                <el-button size="small" :icon="Delete" @click="handleDeleteVideo">
                  删除
                </el-button>
              </template>
            </div>
          </template>

          <div v-if="companyInfo.recruitmentVideo" class="video-preview">
            <video
              ref="videoRef"
              :src="companyInfo.recruitmentVideo"
              :poster="companyInfo.videoCover"
              controls
              style="width: 100%; max-height: 400px"
            />
          </div>
          <el-empty v-else description="暂未上传招聘宣传片" />
        </el-card>
      </el-col>

      <!-- 右侧：认证信息 -->
      <el-col :span="8">
        <!-- 认证状态 -->
        <el-card class="verify-card" shadow="never">
          <template #header>
            <span class="card-title">企业认证</span>
          </template>

          <div class="verify-status">
            <el-result
              :icon="getVerifyStatusIcon(companyInfo.verifyStatus)"
              :title="getVerifyStatusText(companyInfo.verifyStatus)"
            >
              <template #sub-title>
                <div v-if="companyInfo.verifyStatus === 'rejected'" class="reject-reason">
                  拒绝原因：{{ companyInfo.rejectReason || '未填写' }}
                </div>
                <div v-else class="verify-tips">
                  {{ getVerifyStatusTips(companyInfo.verifyStatus) }}
                </div>
              </template>
              <template #extra>
                <el-button
                  v-if="companyInfo.verifyStatus === 'pending'"
                  type="primary"
                  @click="handleVerify"
                >
                  查看认证详情
                </el-button>
                <el-button
                  v-if="companyInfo.verifyStatus === 'rejected'"
                  type="primary"
                  @click="handleReVerify"
                >
                  重新认证
                </el-button>
              </template>
            </el-result>
          </div>
        </el-card>

        <!-- 联系信息 -->
        <el-card class="contact-card" shadow="never" style="margin-top: 20px">
          <template #header>
            <span class="card-title">联系信息</span>
          </template>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="联系人">
              {{ companyInfo.contactName }}
            </el-descriptions-item>
            <el-descriptions-item label="联系职位">
              {{ companyInfo.contactPosition }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ companyInfo.contactPhone }}
            </el-descriptions-item>
            <el-descriptions-item label="联系邮箱">
              {{ companyInfo.contactEmail }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 快速统计 -->
        <el-card class="stats-card" shadow="never" style="margin-top: 20px">
          <template #header>
            <span class="card-title">招聘数据</span>
          </template>

          <div class="quick-stats">
            <div class="stat-item">
              <div class="stat-value">{{ quickStats.positionCount }}</div>
              <div class="stat-label">在招职位</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ quickStats.applicationCount }}</div>
              <div class="stat-label">收到简历</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ quickStats.offerCount }}</div>
              <div class="stat-label">已发Offer</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ quickStats.hiredCount }}</div>
              <div class="stat-label">已入职</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 上传视频对话框 -->
    <el-dialog
      v-model="videoDialogVisible"
      title="上传招聘宣传片"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="视频文件">
          <el-upload
            class="video-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :accept="'video/*'"
            :limit="1"
            :on-success="handleVideoSuccess"
            :on-exceed="handleExceed"
          >
            <el-button type="primary" :icon="Upload">
              选择视频
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持MP4、AVI等格式，大小不超过100MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="视频封面">
          <el-upload
            class="cover-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="videoForm.cover" :src="videoForm.cover" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="videoDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveVideo">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加照片对话框 -->
    <el-dialog
      v-model="photoDialogVisible"
      title="添加照片"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="照片">
          <el-upload
            :action="uploadAction"
            :headers="uploadHeaders"
            list-type="picture-card"
            :limit="9"
            :on-success="handlePhotoSuccess"
            :on-remove="handleRemovePhoto"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="photoForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入照片描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="photoDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePhoto">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import {
  Plus, Edit, Delete, VideoPlay, Upload
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// 企业信息
const companyInfo = ref<any>({})

// 表单
const formRef = ref<FormInstance>()
const isEditing = ref(false)
const submitting = ref(false)

const formData = reactive({
  companyName: '',
  shortName: '',
  industry: '',
  scale: '',
  stage: '',
  website: '',
  province: '',
  city: '',
  address: '',
  description: '',
  benefits: '',
  logo: ''
})

const addressArray = ref<string[]>([])

const formRules: FormRules = {
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业', trigger: 'change' }],
  scale: [{ required: true, message: '请选择规模', trigger: 'change' }]
}

// 相册
const albumList = ref<any[]>([])
const photoDialogVisible = ref(false)
const photoForm = reactive({
  url: '',
  description: ''
})

// 视频
const videoDialogVisible = ref(false)
const videoForm = reactive({
  url: '',
  cover: ''
})
const videoRef = ref<HTMLVideoElement>()

// 上传配置
const uploadAction = '/api/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

// 地区选项
const regionOptions = [
  {
    value: '北京市',
    label: '北京市',
    children: [
      { value: '朝阳区', label: '朝阳区' },
      { value: '海淀区', label: '海淀区' }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      { value: '浦东新区', label: '浦东新区' },
      { value: '徐汇区', label: '徐汇区' }
    ]
  }
]

// 快速统计
const quickStats = ref({
  positionCount: 0,
  applicationCount: 0,
  offerCount: 0,
  hiredCount: 0
})

// 认证状态
const getVerifyStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getVerifyStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待认证',
    approved: '已认证',
    rejected: '认证未通过'
  }
  return textMap[status] || status
}

const getVerifyStatusIcon = (status: string) => {
  const iconMap: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'error'
  }
  return iconMap[status] || 'info'
}

const getVerifyStatusTips = (status: string) => {
  const tipsMap: Record<string, string> = {
    pending: '您的企业认证正在审核中，请耐心等待',
    approved: '恭喜！您的企业已通过认证',
    rejected: '很抱歉，您的企业认证未通过'
  }
  return tipsMap[status] || ''
}

// 加载企业信息
const loadCompanyInfo = async () => {
  try {
    const response: any = await request.get('/company/1')
    companyInfo.value = response

    // 填充表单
    Object.assign(formData, {
      companyName: response.companyName,
      shortName: response.shortName,
      industry: response.industry,
      scale: response.scale,
      stage: response.stage,
      website: response.website,
      province: response.province,
      city: response.city,
      address: response.address,
      description: response.description,
      benefits: response.benefits,
      logo: response.logo
    })

    addressArray.value = [response.province, response.city]

    // 相册
    if (response.companyAlbums) {
      albumList.value = JSON.parse(response.companyAlbums)
    }

    // 快速统计
    loadQuickStats()
  } catch (error) {
    console.error('加载企业信息失败', error)
  }
}

// 加载快速统计
const loadQuickStats = async () => {
  try {
    // 这里应该调用实际的统计接口
    quickStats.value = {
      positionCount: 12,
      applicationCount: 45,
      offerCount: 8,
      hiredCount: 5
    }
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

// 地址变化
const handleAddressChange = (value: string[]) => {
  formData.province = value[0]
  formData.city = value[1]
}

// 编辑
const handleEdit = () => {
  isEditing.value = true
}

// 取消
const handleCancel = () => {
  isEditing.value = false
  Object.assign(formData, companyInfo.value)
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await request.put(`/company/${companyInfo.value.id}`, formData)
      ElMessage.success('保存成功')
      isEditing.value = false
      loadCompanyInfo()
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '保存失败')
    } finally {
      submitting.value = false
    }
  })
}

// Logo上传
const handleLogoSuccess: UploadProps['onSuccess'] = (response) => {
  formData.logo = response.url
  ElMessage.success('上传成功')
}

const beforeLogoUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (!rawFile.type.includes('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

// 添加照片
const handleAddPhoto = () => {
  photoDialogVisible.value = true
}

const handlePhotoSuccess: UploadProps['onSuccess'] = (response) => {
  photoForm.url = response.url
}

const handleRemovePhoto: UploadProps['onRemove'] = () => {
  photoForm.url = ''
}

const handleSavePhoto = () => {
  if (!photoForm.url) {
    ElMessage.warning('请先上传照片')
    return
  }
  albumList.value.push({
    url: photoForm.url,
    description: photoForm.description
  })
  photoDialogVisible.value = false
  photoForm.url = ''
  photoForm.description = ''
  ElMessage.success('添加成功')
  // 保存到服务器
  saveAlbum()
}

const handleDeletePhoto = (index: number) => {
  albumList.value.splice(index, 1)
  saveAlbum()
}

const saveAlbum = async () => {
  try {
    await request.put(`/company/${companyInfo.value.id}`, {
      companyAlbums: JSON.stringify(albumList.value)
    })
  } catch (error) {
    console.error('保存相册失败', error)
  }
}

// 视频上传
const handleUploadVideo = () => {
  videoDialogVisible.value = true
}

const handleVideoSuccess: UploadProps['onSuccess'] = (response) => {
  videoForm.url = response.url
  ElMessage.success('上传成功')
}

const handleCoverSuccess: UploadProps['onSuccess'] = (response) => {
  videoForm.cover = response.url
}

const beforeCoverUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (!rawFile.type.includes('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  return true
}

const handleExceed = () => {
  ElMessage.warning('只能上传一个视频')
}

const handleSaveVideo = async () => {
  try {
    await request.put(`/company/${companyInfo.value.id}`, {
      recruitmentVideo: videoForm.url,
      videoCover: videoForm.cover
    })
    ElMessage.success('保存成功')
    videoDialogVisible.value = false
    loadCompanyInfo()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  }
}

const handlePlayVideo = () => {
  videoRef.value?.play()
}

const handleDeleteVideo = async () => {
  try {
    await request.put(`/company/${companyInfo.value.id}`, {
      recruitmentVideo: null,
      videoCover: null
    })
    ElMessage.success('删除成功')
    loadCompanyInfo()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

// 认证
const handleVerify = () => {
  ElMessage.info('查看认证详情功能开发中')
}

const handleReVerify = () => {
  ElMessage.info('重新认证功能开发中')
}

onMounted(() => {
  loadCompanyInfo()
})
</script>

<style scoped lang="scss">
.enterprise-profile-management {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .form-actions {
    margin-top: 24px;
    text-align: right;
    border-top: 1px solid #ebeef5;
    padding-top: 16px;
  }

  .logo-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }

    .logo {
      width: 178px;
      height: 178px;
      display: block;
    }

    .logo-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      line-height: 178px;
      text-align: center;
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
  }

  .album-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;

    .album-item {
      position: relative;
      border-radius: 8px;
      overflow: hidden;
      aspect-ratio: 16/10;

      :deep(.el-image) {
        width: 100%;
        height: 100%;
      }

      .album-actions {
        position: absolute;
        top: 8px;
        right: 8px;
        opacity: 0;
        transition: opacity 0.3s;
      }

      &:hover .album-actions {
        opacity: 1;
      }

      .album-desc {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: rgba(0, 0, 0, 0.6);
        color: white;
        padding: 8px;
        font-size: 12px;
      }
    }

    .album-empty {
      grid-column: 1 / -1;
    }
  }

  .video-preview {
    border-radius: 8px;
    overflow: hidden;
    background: #000;
  }

  .verify-status {
    .reject-reason {
      color: #f56c6c;
      margin-top: 8px;
      padding: 8px;
      background: #fef0f0;
      border-radius: 4px;
    }

    .verify-tips {
      color: #909399;
      font-size: 14px;
      margin-top: 8px;
    }
  }

  .quick-stats {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .stat-item {
      text-align: center;
      padding: 16px;
      background: #f5f7fa;
      border-radius: 8px;

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #409eff;
        line-height: 1;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }

    .cover-image {
      width: 200px;
      height: 112px;
      display: block;
      object-fit: cover;
    }

    .cover-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 200px;
      height: 112px;
      line-height: 112px;
      text-align: center;
    }
  }
}
</style>
