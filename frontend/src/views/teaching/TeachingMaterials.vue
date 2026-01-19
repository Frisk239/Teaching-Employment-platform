<template>
  <div class="teaching-materials">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">教学资料管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleUpload">
          <el-icon><Upload /></el-icon>
          上传资料
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-card shadow="never" class="stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon total">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总资料</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon ppt">
            <el-icon><Notebook /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.ppt }}</div>
            <div class="stat-label">PPT课件</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon video">
            <el-icon><VideoCamera /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.video }}</div>
            <div class="stat-label">视频资料</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon document">
            <el-icon><Files /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.document }}</div>
            <div class="stat-label">文档资料</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon download">
            <el-icon><Download /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalDownloads }}</div>
            <div class="stat-label">总下载</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon public">
            <el-icon><Share /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.public }}</div>
            <div class="stat-label">已公开</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 搜索和筛选 -->
    <el-card shadow="never" class="filter-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="资料类型">
          <el-select
            v-model="searchForm.materialType"
            placeholder="全部类型"
            clearable
            style="width: 150px"
          >
            <el-option label="PPT课件" value="ppt" />
            <el-option label="视频资料" value="video" />
            <el-option label="文档资料" value="document" />
            <el-option label="图片资料" value="image" />
            <el-option label="其他资料" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-input
            v-model="searchForm.category"
            placeholder="输入分类"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索资料名称/描述/标签"
            clearable
            style="width: 200px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 资料列表 -->
    <div v-loading="loading" class="material-list">
      <el-empty v-if="tableData.length === 0" description="暂无教学资料" />

      <el-row v-else :gutter="16" class="cards-row">
        <el-col
          v-for="material in tableData"
          :key="material.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card shadow="hover" class="material-card">
            <!-- 资料图标和类型 -->
            <div class="material-icon">
              <div class="icon-wrapper" :class="material.materialType">
                <el-icon class="icon"><component :is="getMaterialTypeIcon(material.materialType)" /></el-icon>
              </div>
              <div class="type-tag">
                <el-tag size="small" :type="getMaterialTypeColor(material.materialType)">
                  {{ getMaterialTypeLabel(material.materialType) }}
                </el-tag>
              </div>
            </div>

            <!-- 资料名称 -->
            <div class="material-name" :title="material.title">
              {{ material.title }}
            </div>

            <!-- 资料信息 -->
            <div class="material-info">
              <div class="info-item" v-if="material.courseName">
                <el-icon><Reading /></el-icon>
                <span>{{ material.courseName }}</span>
              </div>
              <div class="info-item" v-if="material.fileSize">
                <el-icon><DocumentCopy /></el-icon>
                <span>{{ formatFileSize(material.fileSize!) }}</span>
              </div>
              <div class="info-item" v-if="material.category">
                <el-icon><FolderOpened /></el-icon>
                <span>{{ material.category }}</span>
              </div>
            </div>

            <!-- 统计信息 -->
            <div class="material-stats">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ material.viewCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Download /></el-icon>
                <span>{{ material.downloadCount || 0 }}</span>
              </div>
              <div class="stat-item" v-if="material.isPublic === 1">
                <el-icon><Share /></el-icon>
                <span>已公开</span>
              </div>
            </div>

            <!-- 标签 -->
            <div class="material-tags" v-if="material.tags">
              <el-tag
                v-for="tag in material.tags.split(',').slice(0, 3)"
                :key="tag"
                size="small"
                type="info"
              >
                {{ tag }}
              </el-tag>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-button
                type="primary"
                size="small"
                :icon="Download"
                @click="handleDownload(material)"
              >
                下载
              </el-button>
              <el-button
                type="info"
                size="small"
                :icon="View"
                @click="handleViewDetail(material)"
              >
                详情
              </el-button>
              <el-dropdown @command="(cmd) => handleMoreAction(cmd, material)">
                <el-button size="small">
                  更多
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit" divided>
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="delete">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 -->
      <el-pagination
        v-if="tableData.length > 0"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </div>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传教学资料"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        :rules="uploadRules"
        label-width="100px"
      >
        <el-form-item label="资料文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
            accept=".ppt,.pptx,.doc,.docx,.pdf,.mp4,.avi,.mov,.jpg,.jpeg,.png,.gif"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 PPT、Word、PDF、视频、图片等格式，单个文件不超过100MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="资料名称" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入资料名称" />
        </el-form-item>

        <el-form-item label="资料类型" prop="materialType">
          <el-select v-model="uploadForm.materialType" placeholder="请选择资料类型" style="width: 100%">
            <el-option label="PPT课件" value="ppt" />
            <el-option label="视频资料" value="video" />
            <el-option label="文档资料" value="document" />
            <el-option label="图片资料" value="image" />
            <el-option label="其他资料" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="关联课程" prop="courseId">
          <el-select v-model="uploadForm.courseId" placeholder="请选择课程" clearable filterable style="width: 100%">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="分类标签" prop="category">
          <el-input v-model="uploadForm.category" placeholder="例如：基础理论、实践案例等" />
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-input v-model="uploadForm.tags" placeholder="多个标签用逗号分隔，如：重点,考试必考" />
        </el-form-item>

        <el-form-item label="资料描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资料描述"
          />
        </el-form-item>

        <el-form-item label="是否公开" prop="isPublic">
          <el-switch v-model="uploadForm.isPublic" :active-value="1" :inactive-value="0" />
          <span style="margin-left: 10px; color: #909399;">公开后学员可以查看和下载</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUploadSubmit" :loading="uploading">
          上传
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="资料详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentMaterial" class="detail-content">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="资料名称">
              {{ currentMaterial.title }}
            </el-descriptions-item>
            <el-descriptions-item label="资料类型">
              <el-tag :type="getMaterialTypeColor(currentMaterial.materialType)" size="small">
                {{ getMaterialTypeLabel(currentMaterial.materialType) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="文件大小">
              {{ formatFileSize(currentMaterial.fileSize || 0) }}
            </el-descriptions-item>
            <el-descriptions-item label="课程">
              {{ currentMaterial.courseName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="分类">
              {{ currentMaterial.category || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="currentMaterial.isPublic === 1 ? 'success' : 'info'" size="small">
                {{ currentMaterial.isPublic === 1 ? '已公开' : '私有' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="查看次数" :span="2">
              {{ currentMaterial.viewCount || 0 }} 次
            </el-descriptions-item>
            <el-descriptions-item label="下载次数" :span="2">
              {{ currentMaterial.downloadCount || 0 }} 次
            </el-descriptions-item>
            <el-descriptions-item label="上传时间" :span="2">
              {{ currentMaterial.createTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 标签 -->
        <div class="detail-section" v-if="currentMaterial.tags">
          <h4 class="section-title">标签</h4>
          <div class="tags-list">
            <el-tag
              v-for="tag in currentMaterial.tags.split(',')"
              :key="tag"
              type="info"
              style="margin-right: 8px; margin-bottom: 8px;"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <!-- 描述 -->
        <div class="detail-section" v-if="currentMaterial.description">
          <h4 class="section-title">资料描述</h4>
          <p class="description-text">{{ currentMaterial.description }}</p>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleDownload(currentMaterial!)">
          <el-icon><Download /></el-icon>
          下载资料
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑资料"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="资料名称" prop="title">
          <el-input v-model="editForm.title" />
        </el-form-item>

        <el-form-item label="分类标签" prop="category">
          <el-input v-model="editForm.category" />
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-input v-model="editForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>

        <el-form-item label="资料描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
          />
        </el-form-item>

        <el-form-item label="是否公开">
          <el-switch v-model="editForm.isPublic" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editing">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  Refresh,
  Document,
  Notebook,
  VideoCamera,
  Files,
  Download,
  Share,
  Search,
  Reading,
  DocumentCopy,
  FolderOpened,
  View,
  ArrowDown,
  Edit,
  Delete,
  Picture,
  Folder
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import {
  teachingMaterialApi,
  type TeachingMaterial,
  formatFileSize,
  getMaterialTypeLabel
} from '@/api/teachingMaterial'
import { getCoursesByTeacherApi } from '@/api/course'
import type { UploadFile, UploadRawFile } from 'element-plus'
import type { FormInstance, FormRules, UploadInstance } from 'element-plus'

interface Stats {
  total: number
  ppt: number
  video: number
  document: number
  totalDownloads: number
  public: number
}

const authStore = useAuthStore()
const loading = ref(false)
const uploading = ref(false)
const editing = ref(false)
const tableData = ref<TeachingMaterial[]>([])
const courses = ref<any[]>([])
const detailDialogVisible = ref(false)
const uploadDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentMaterial = ref<TeachingMaterial | null>(null)

// 表单引用
const uploadFormRef = ref<FormInstance>()
const editFormRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()

// 教师ID
const teacherId = computed(() => {
  // 从authStore获取教师ID
  return authStore.user?.teacherId || authStore.user?.id
})

// 统计数据
const stats = ref<Stats>({
  total: 0,
  ppt: 0,
  video: 0,
  document: 0,
  totalDownloads: 0,
  public: 0
})

// 搜索表单
const searchForm = reactive({
  materialType: '',
  category: '',
  keyword: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 12,
  total: 0
})

// 上传表单
const uploadForm = reactive({
  file: null as File | null,
  title: '',
  materialType: '',
  courseId: undefined as number | undefined,
  category: '',
  tags: '',
  description: '',
  isPublic: 0
})

// 编辑表单
const editForm = reactive({
  id: 0,
  title: '',
  category: '',
  tags: '',
  description: '',
  isPublic: 0
})

// 表单验证规则
const uploadRules: FormRules = {
  file: [{ required: true, message: '请选择文件', trigger: 'change' }],
  title: [{ required: true, message: '请输入资料名称', trigger: 'blur' }],
  materialType: [{ required: true, message: '请选择资料类型', trigger: 'change' }]
}

const editRules: FormRules = {
  title: [{ required: true, message: '请输入资料名称', trigger: 'blur' }]
}

// 获取资料列表
const fetchData = async () => {
  if (!teacherId.value) {
    ElMessage.warning('请先登录')
    return
  }

  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size,
      teacherId: teacherId.value
    }

    if (searchForm.materialType) {
      params.materialType = searchForm.materialType
    }
    if (searchForm.category) {
      params.category = searchForm.category
    }
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }

    const response = await teachingMaterialApi.getPage(params) as any
    tableData.value = response.records || []
    pagination.total = response.total || 0
    updateStats()
  } catch (error) {
    console.error('获取教学资料列表失败:', error)
    ElMessage.error('获取教学资料列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.value = {
    total: tableData.value.length,
    ppt: tableData.value.filter((m) => m.materialType === 'ppt').length,
    video: tableData.value.filter((m) => m.materialType === 'video').length,
    document: tableData.value.filter((m) => m.materialType === 'document').length,
    totalDownloads: tableData.value.reduce((sum, m) => sum + (m.downloadCount || 0), 0),
    public: tableData.value.filter((m) => m.isPublic === 1).length
  }
}

// 获取课程列表
const fetchCourses = async () => {
  if (!teacherId.value) {
    return
  }

  try {
    const response = await getCoursesByTeacherApi(teacherId.value) as any
    courses.value = response || []
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 获取资料类型颜色
const getMaterialTypeColor = (type: string): string => {
  const colorMap: Record<string, string> = {
    'ppt': 'danger',
    'video': 'warning',
    'document': 'primary',
    'image': 'success',
    'other': 'info'
  }
  return colorMap[type] || 'info'
}

// 获取资料类型图标
const getMaterialTypeIcon = (type: string): any => {
  const iconMap: Record<string, any> = {
    'ppt': Document,
    'video': VideoCamera,
    'document': Files,
    'image': Picture,
    'other': Folder
  }
  return iconMap[type] || Folder
}

// 打开上传对话框
const handleUpload = () => {
  Object.assign(uploadForm, {
    file: null,
    title: '',
    materialType: '',
    courseId: undefined,
    category: '',
    tags: '',
    description: '',
    isPublic: 0
  })
  uploadFormRef.value?.clearValidate()
  uploadDialogVisible.value = true
}

// 文件选择
const handleFileChange = (file: UploadFile) => {
  uploadForm.file = file.raw
  // 自动填充资料名称（如果未填写）
  if (!uploadForm.title) {
    uploadForm.title = file.name.replace(/\.[^/.]+$/, '')
  }
}

// 超出限制
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

// 提交上传
const handleUploadSubmit = async () => {
  try {
    await uploadFormRef.value?.validate()

    if (!uploadForm.file) {
      ElMessage.warning('请选择文件')
      return
    }

    uploading.value = true

    const formData = new FormData()
    formData.append('file', uploadForm.file)
    formData.append('title', uploadForm.title)
    formData.append('materialType', uploadForm.materialType)
    formData.append('teacherId', teacherId.value!.toString())
    if (uploadForm.courseId) {
      formData.append('courseId', uploadForm.courseId.toString())
    }
    formData.append('category', uploadForm.category || '')
    formData.append('tags', uploadForm.tags || '')
    formData.append('description', uploadForm.description || '')
    formData.append('isPublic', uploadForm.isPublic.toString())

    await teachingMaterialApi.upload(formData)
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('上传失败:', error)
    ElMessage.error(error.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

// 查看详情
const handleViewDetail = (material: TeachingMaterial) => {
  currentMaterial.value = material
  detailDialogVisible.value = true
  // 记录查看
  teachingMaterialApi.recordView(material.id!)
}

// 下载资料
const handleDownload = (material: TeachingMaterial) => {
  // 记录下载
  teachingMaterialApi.recordDownload(material.id!)

  // 构建完整的下载URL
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  const downloadUrl = baseUrl + material.fileUrl

  // 打开下载链接
  window.open(downloadUrl, '_blank')
}

// 编辑资料
const handleMoreAction = (command: string, material: TeachingMaterial) => {
  if (command === 'edit') {
    Object.assign(editForm, {
      id: material.id!,
      title: material.title,
      category: material.category || '',
      tags: material.tags || '',
      description: material.description || '',
      isPublic: material.isPublic || 0
    })
    editDialogVisible.value = true
  } else if (command === 'delete') {
    handleDelete(material)
  }
}

// 提交编辑
const handleEditSubmit = async () => {
  try {
    await editFormRef.value?.validate()
    editing.value = true

    await teachingMaterialApi.update(editForm)
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('更新失败:', error)
    ElMessage.error(error.message || '更新失败')
  } finally {
    editing.value = false
  }
}

// 删除资料
const handleDelete = async (material: TeachingMaterial) => {
  try {
    await ElMessageBox.confirm(`确定删除资料"${material.title}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await teachingMaterialApi.delete(material.id!)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 刷新
const handleRefresh = () => {
  fetchData()
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.materialType = ''
  searchForm.category = ''
  searchForm.keyword = ''
  handleSearch()
}

// 初始化
onMounted(() => {
  fetchData()
  fetchCourses()
})
</script>

<style scoped lang="scss">
.teaching-materials {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }

  // 统计卡片
  .stats-card {
    margin-bottom: 20px;
    padding: 20px;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  .stat-item {
    display: flex;
    align-items: center;
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    transition: all 0.3s;
    background: #fff;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
    }
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    margin-right: 16px;
    flex-shrink: 0;

    .el-icon {
      font-size: 24px;
    }

    &.total {
      background: #f0f2f5;
      color: #606266;
    }

    &.ppt {
      background: #fee;
      color: #f56c6c;
    }

    &.video {
      background: #fef0e6;
      color: #e6a23c;
    }

    &.document {
      background: #e8f4ff;
      color: #409eff;
    }

    &.download {
      background: #e1f3d8;
      color: #67c23a;
    }

    &.public {
      background: #fdf2ec;
      color: #f56c6c;
    }
  }

  .stat-info {
    flex: 1;
    min-width: 0;
  }

  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1.2;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }

  // 筛选卡片
  .filter-card {
    margin-bottom: 20px;
  }

  .search-form {
    margin-bottom: 0;
  }

  // 资料卡片列表
  .material-list {
    min-height: 400px;
  }

  .cards-row {
    margin-bottom: 20px;
  }

  .material-card {
    border-radius: 8px;
    margin-bottom: 16px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .material-icon {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .icon-wrapper {
        width: 56px;
        height: 56px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8px;

        .icon {
          font-size: 28px;
          color: #fff;
        }

        &.ppt {
          background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
        }

        &.video {
          background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
        }

        &.document {
          background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
        }

        &.image {
          background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
        }

        &.other {
          background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
        }
      }

      .type-tag {
        flex: 1;
        text-align: right;
      }
    }

    .material-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .material-info {
      display: flex;
      flex-direction: column;
      gap: 6px;
      margin-bottom: 12px;

      .info-item {
        display: flex;
        align-items: center;
        font-size: 13px;
        color: #606266;

        .el-icon {
          margin-right: 5px;
          font-size: 14px;
        }
      }
    }

    .material-stats {
      display: flex;
      gap: 16px;
      padding: 8px 0;
      margin-bottom: 12px;
      border-top: 1px solid #ebeef5;

      .stat-item {
        display: flex;
        align-items: center;
        font-size: 13px;
        color: #909399;

        .el-icon {
          margin-right: 4px;
        }
      }
    }

    .material-tags {
      margin-bottom: 12px;
    }

    .card-actions {
      display: flex;
      gap: 8px;
      padding-top: 12px;
      border-top: 1px solid #ebeef5;

      .el-button {
        flex: 1;
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  // 详情对话框
  .detail-content {
    .detail-section {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 12px;
        padding-bottom: 8px;
        border-bottom: 2px solid #409eff;
      }

      .tags-list {
        display: flex;
        flex-wrap: wrap;
      }

      .description-text {
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
        margin: 0;
      }
    }
  }
}
</style>
