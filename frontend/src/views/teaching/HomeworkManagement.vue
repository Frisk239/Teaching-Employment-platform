<template>
  <div class="homework-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">作业管理</h1>
        <p class="page-subtitle">发布和管理作业,批改学生提交</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="createHomework">
          发布新作业
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card stat-card-primary">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">作业总数</div>
          <div class="stat-value">{{ stats.totalHomeworks || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-success">
        <div class="stat-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已发布</div>
          <div class="stat-value">{{ stats.publishedCount || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-warning">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待批改</div>
          <div class="stat-value">{{ stats.pendingGrading || 0 }}</div>
        </div>
      </div>
      <div class="stat-card stat-card-info">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总提交数</div>
          <div class="stat-value">{{ stats.totalSubmissions || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all">
          <template #label>
            <span>全部 ({{ stats.totalHomeworks || 0 }})</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已发布" name="published">
          <template #label>
            <span>已发布 ({{ stats.publishedCount || 0 }})</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="草稿箱" name="draft">
          <template #label>
            <span>草稿箱 ({{ stats.draftCount || 0 }})</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已截止" name="closed">
          <template #label>
            <span>已截止 ({{ stats.closedCount || 0 }})</span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <div class="filter-controls">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索作业标题..."
          clearable
          style="width: 300px;"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="selectedCourse"
          placeholder="筛选课程"
          clearable
          style="width: 200px;"
          @change="handleCourseFilter"
        >
          <el-option label="全部课程" :value="null" />
          <el-option
            v-for="course in courses"
            :key="course.id"
            :label="course.courseName || course.name"
            :value="course.id"
          />
        </el-select>
      </div>
    </div>

    <!-- 作业列表 -->
    <div v-loading="loading" class="homework-list">
      <el-empty v-if="!loading && filteredHomework.length === 0" description="暂无作业">
        <el-button type="primary" @click="createHomework">发布新作业</el-button>
      </el-empty>

      <div v-else class="homework-cards">
        <div
          v-for="homework in filteredHomework"
          :key="homework.id"
          class="homework-card"
        >
          <!-- 作业头部 -->
          <div class="homework-header">
            <div class="homework-title-section">
              <h3 class="homework-title">{{ homework.title }}</h3>
              <div class="homework-meta">
                <el-tag size="small" :type="getStatusType(homework.status)">
                  {{ getStatusText(homework.status) }}
                </el-tag>
                <span class="meta-item">
                  <el-icon><Reading /></el-icon>
                  {{ homework.courseName || homework.course?.courseName }}
                </span>
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDateTime(homework.deadline) }}
                </span>
                <span class="meta-item">
                  <el-icon><Star /></el-icon>
                  {{ homework.maxScore }}分
                </span>
              </div>
            </div>
            <div class="homework-actions">
              <el-dropdown @command="(cmd) => handleAction(cmd, homework)">
                <el-button :icon="MoreFilled" circle />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="view" :icon="View">查看详情</el-dropdown-item>
                    <el-dropdown-item command="submissions" :icon="User">
                      查看提交 ({{ homework.submissionCount || 0 }})
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="homework.status === 'draft'"
                      command="publish"
                      :icon="Promotion"
                    >
                      发布作业
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="homework.status === 'published'"
                      command="close"
                      :icon="CircleClose"
                    >
                      关闭作业
                    </el-dropdown-item>
                    <el-dropdown-item command="edit" :icon="Edit">编辑</el-dropdown-item>
                    <el-dropdown-item command="delete" :icon="Delete" divided>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <!-- 作业描述 -->
          <p class="homework-description">
            {{ homework.description || '暂无描述' }}
          </p>

          <!-- 提交统计 -->
          <div class="homework-stats">
            <div class="stat-item">
              <div class="stat-label">提交进度</div>
              <el-progress
                :percentage="getSubmissionRate(homework)"
                :stroke-width="8"
                :show-text="false"
                :color="getProgressColor(homework)"
              />
              <div class="stat-value">
                {{ homework.submissionCount || 0 }} / {{ homework.pendingCount + (homework.submissionCount || 0) }}
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">批改进度</div>
              <el-progress
                :percentage="getGradingRate(homework)"
                :stroke-width="8"
                :show-text="false"
                color="#67c23a"
              />
              <div class="stat-value">
                {{ homework.gradedCount || 0 }} / {{ homework.submissionCount || 0 }}
              </div>
            </div>
          </div>

          <!-- 底部操作栏 -->
          <div class="homework-footer">
            <el-button
              type="primary"
              size="small"
              @click="viewSubmissions(homework)"
            >
              <el-icon><User /></el-icon>
              查看提交 ({{ homework.submissionCount || 0 }})
            </el-button>
            <el-tag
              v-if="homework.pendingGradingCount > 0"
              type="warning"
              size="small"
            >
              {{ homework.pendingGradingCount }} 人待批改
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 作业详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentHomework?.title"
      width="700px"
    >
      <div v-if="currentHomework" v-loading="loadingDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">
            {{ currentHomework.courseName || currentHomework.course?.courseName }}
          </el-descriptions-item>
          <el-descriptions-item label="作业类型">
            {{ getHomeworkTypeText(currentHomework.homeworkType) }}
          </el-descriptions-item>
          <el-descriptions-item label="满分">
            {{ currentHomework.maxScore }}分
          </el-descriptions-item>
          <el-descriptions-item label="截止时间">
            {{ formatDateTime(currentHomework.deadline) }}
          </el-descriptions-item>
          <el-descriptions-item label="作业状态">
            <el-tag :type="getStatusType(currentHomework.status)">
              {{ getStatusText(currentHomework.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="作业描述" :span="1">
            <div class="homework-detail-description">
              {{ currentHomework.description || '暂无描述' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentHomework.attachmentUrl" label="附件">
            <el-button type="primary" link size="small" @click="downloadFile(currentHomework.attachmentUrl, '作业附件')">
              <el-icon><Download /></el-icon>
              下载附件
            </el-button>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 提交统计 -->
        <div v-if="currentHomeworkStats" class="detail-stats">
          <h4>提交统计</h4>
          <el-row :gutter="16">
            <el-col :span="8">
              <div class="stat-box">
                <div class="stat-value">{{ currentHomeworkStats.submissionCount || 0 }}</div>
                <div class="stat-label">已提交</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-box">
                <div class="stat-value">{{ currentHomeworkStats.pendingCount || 0 }}</div>
                <div class="stat-label">未提交</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-box">
                <div class="stat-value">{{ currentHomeworkStats.gradedCount || 0 }}</div>
                <div class="stat-label">已批改</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="viewSubmissionsFromDetail">
          查看提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 创建/编辑作业对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="isEditMode ? '编辑作业' : '发布新作业'"
      width="800px"
      @close="closeFormDialog"
    >
      <el-form :model="homeworkForm" label-width="100px" :rules="formRules" ref="formRef">
        <el-form-item label="课程" prop="courseId">
          <el-select
            v-model="homeworkForm.courseId"
            placeholder="请选择课程"
            style="width: 100%;"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.courseName || course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作业标题" prop="title">
          <el-input
            v-model="homeworkForm.title"
            placeholder="请输入作业标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="作业类型" prop="homeworkType">
          <el-radio-group v-model="homeworkForm.homeworkType">
            <el-radio value="assignment">作业</el-radio>
            <el-radio value="project">项目</el-radio>
            <el-radio value="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="作业描述" prop="description">
          <el-input
            v-model="homeworkForm.description"
            type="textarea"
            :rows="6"
            placeholder="请输入作业描述、要求等详细信息..."
          />
        </el-form-item>
        <el-form-item label="满分" prop="maxScore">
          <el-input-number
            v-model="homeworkForm.maxScore"
            :min="1"
            :max="1000"
            :step="10"
          />
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="homeworkForm.deadline"
            type="datetime"
            placeholder="选择截止时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disableDate"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="附件上传">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                上传作业要求的文件、参考资料等
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="发布选项">
          <el-radio-group v-model="publishOption">
            <el-radio value="draft">保存为草稿</el-radio>
            <el-radio value="publish">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveHomework">
          {{ publishOption === 'publish' ? '发布作业' : '保存草稿' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 提交列表对话框 -->
    <el-dialog
      v-model="submissionsDialogVisible"
      :title="`提交列表 - ${currentHomework?.title}`"
      width="1200px"
      @close="closeSubmissionsDialog"
    >
      <div v-loading="loadingSubmissions">
        <!-- 筛选标签 -->
        <el-tabs v-model="submissionTab" @tab-change="loadSubmissions">
          <el-tab-pane label="全部" name="all">
            <template #label>
              <span>全部 ({{ submissionStats.total || 0 }})</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="待批改" name="submitted">
            <template #label>
              <span>待批改 ({{ submissionStats.pending || 0 }})</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="已批改" name="graded">
            <template #label>
              <span>已批改 ({{ submissionStats.graded || 0 }})</span>
            </template>
          </el-tab-pane>
        </el-tabs>

        <el-table :data="submissions" stripe>
          <el-table-column prop="studentName" label="学生姓名" width="120" />
          <el-table-column prop="studentNo" label="学号" width="120" />
          <el-table-column label="提交状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 'graded' ? 'success' : 'warning'" size="small">
                {{ row.status === 'graded' ? '已批改' : '待批改' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.submitTime || row.submitDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="100" align="center">
            <template #default="{ row }">
              <span v-if="row.score !== null" :class="getScoreClass(row.score)">
                {{ row.score }}分
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="提交内容" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                link
                @click="gradeSubmission(row)"
              >
                {{ row.status === 'graded' ? '查看/修改' : '批改' }}
              </el-button>
              <el-button
                type="info"
                size="small"
                link
                @click="viewSubmissionDetail(row)"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!loadingSubmissions && submissions.length === 0" description="暂无提交记录" />
      </div>

      <template #footer>
        <el-button @click="submissionsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 批改对话框 -->
    <el-dialog
      v-model="gradingDialogVisible"
      title="批改作业"
      width="800px"
      @close="closeGradingDialog"
    >
      <div v-if="currentSubmission" v-loading="loadingGrading">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学生姓名">
            {{ currentSubmission.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="学号">
            {{ currentSubmission.studentNo }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ formatDateTime(currentSubmission.submitTime || currentSubmission.submitDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="提交内容">
            <div class="submission-content-view">
              {{ currentSubmission.content || '无内容' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentSubmission.attachmentUrl" label="附件">
            <el-button type="primary" link size="small" @click="downloadFile(currentSubmission.attachmentUrl, '学生提交附件')">
              <el-icon><Download /></el-icon>
              下载附件
            </el-button>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-form :model="gradingForm" label-width="100px">
          <el-form-item label="得分" required>
            <el-input-number
              v-model="gradingForm.score"
              :min="0"
              :max="currentHomework?.maxScore || 100"
              :precision="1"
              :step="1"
            />
            <span style="margin-left: 1rem; color: var(--el-text-color-secondary);">
              满分: {{ currentHomework?.maxScore || 100 }}分
            </span>
          </el-form-item>
          <el-form-item label="评语">
            <el-input
              v-model="gradingForm.comment"
              type="textarea"
              :rows="6"
              placeholder="请输入批改意见、评语等..."
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="gradingDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingGrade" @click="confirmGrade">
          确认批改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Search,
  Document,
  CircleCheck,
  Clock,
  User,
  Reading,
  Calendar,
  Star,
  MoreFilled,
  View,
  Edit,
  Delete,
  Promotion,
  CircleClose,
  Download,
  UploadFilled
} from '@element-plus/icons-vue'
import axios from '@/utils/request'
import { useAuthStore } from '@/stores'
import dayjs from 'dayjs'

const authStore = useAuthStore()

// 状态
const loading = ref(false)
const loadingDetail = ref(false)
const loadingSubmissions = ref(false)
const loadingGrading = ref(false)
const saving = ref(false)
const savingGrade = ref(false)
const searchKeyword = ref('')
const activeTab = ref('all')
const selectedCourse = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 数据
const homeworkList = ref<any[]>([])
const courses = ref<any[]>([])
const stats = ref({
  totalHomeworks: 0,
  publishedCount: 0,
  draftCount: 0,
  closedCount: 0,
  pendingGrading: 0,
  totalSubmissions: 0
})

// 对话框状态
const detailDialogVisible = ref(false)
const formDialogVisible = ref(false)
const submissionsDialogVisible = ref(false)
const gradingDialogVisible = ref(false)

// 当前选中的作业和提交
const currentHomework = ref<any>(null)
const currentHomeworkStats = ref<any>(null)
const currentSubmission = ref<any>(null)
const submissions = ref<any[]>([])
const submissionTab = ref('all')
const submissionStats = ref({
  total: 0,
  pending: 0,
  graded: 0
})

// 表单
const formRef = ref<FormInstance>()
const isEditMode = ref(false)
const publishOption = ref('publish')
const homeworkForm = ref({
  courseId: null as number | null,
  title: '',
  description: '',
  homeworkType: 'assignment',
  maxScore: 100,
  deadline: ''
})
const gradingForm = ref({
  score: 0,
  comment: ''
})
const fileList = ref<any[]>([])

// 表单验证规则
const formRules: FormRules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
  homeworkType: [{ required: true, message: '请选择作业类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入作业描述', trigger: 'blur' }],
  maxScore: [{ required: true, message: '请输入满分', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

// 获取教师ID
const getTeacherId = () => {
  const userInfo = authStore.user
  if (userInfo && userInfo.teacherId) {
    return userInfo.teacherId
  }

  const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      if (user.teacherId) {
        return user.teacherId
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }

  console.warn('无法获取教师ID')
  return 0
}

// 过滤后的作业列表
const filteredHomework = computed(() => {
  let result = homeworkList.value

  // 标签筛选
  if (activeTab.value !== 'all') {
    result = result.filter(h => h.status === activeTab.value)
  }

  // 课程筛选
  if (selectedCourse.value) {
    result = result.filter(h => h.courseId === selectedCourse.value)
  }

  // 搜索筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(h =>
      h.title?.toLowerCase().includes(keyword) ||
      h.description?.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, any> = {
    draft: 'info',
    published: 'success',
    closed: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
    closed: '已截止'
  }
  return statusMap[status] || status
}

// 获取作业类型文本
const getHomeworkTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    assignment: '作业',
    project: '项目',
    other: '其他'
  }
  return typeMap[type] || '作业'
}

// 获取分数样式
const getScoreClass = (score: number) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

// 获取提交率
const getSubmissionRate = (homework: any) => {
  const total = (homework.submissionCount || 0) + (homework.pendingCount || 0)
  if (total === 0) return 0
  return Math.round((homework.submissionCount || 0) / total * 100)
}

// 获取批改率
const getGradingRate = (homework: any) => {
  const submitted = homework.submissionCount || 0
  if (submitted === 0) return 0
  return Math.round((homework.gradedCount || 0) / submitted * 100)
}

// 获取进度条颜色
const getProgressColor = (homework: any) => {
  const rate = getSubmissionRate(homework)
  if (rate >= 80) return '#67c23a'
  if (rate >= 50) return '#409eff'
  if (rate >= 20) return '#e6a23c'
  return '#f56c6c'
}

// 禁用日期
const disableDate = (date: Date) => {
  return date.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm')
}

// 加载作业列表
const loadHomeworkList = async () => {
  loading.value = true
  try {
    const teacherId = getTeacherId()
    const response = await axios.get('/homework/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        teacherId: teacherId
      }
    })

    if (response && (response as any).records) {
      const homeworks = (response as any).records || []

      // 填充课程名称和统计信息
      const homeworksWithDetails = await Promise.all(
        homeworks.map(async (homework: any) => {
          // 获取课程信息
          let courseName = '未知课程'
          try {
            const courseResponse = await axios.get(`/course/${homework.courseId}`)
            courseName = (courseResponse as any).courseName || (courseResponse as any).name || '未知课程'
          } catch (e) {
            console.error('获取课程信息失败:', e)
          }

          // 获取统计信息
          let stats = { submissionCount: 0, pendingCount: 0, gradedCount: 0 }
          try {
            const statsResponse = await axios.get(`/homework/${homework.id}/stats`)
            stats = statsResponse || {}
          } catch (e) {
            console.error('获取统计信息失败:', e)
          }

          return {
            ...homework,
            courseName,
            submissionCount: stats.submissionCount || 0,
            pendingCount: stats.pendingCount || 0,
            gradedCount: stats.gradedCount || 0,
            pendingGradingCount: (stats.submissionCount || 0) - (stats.gradedCount || 0)
          }
        })
      )

      homeworkList.value = homeworksWithDetails
      total.value = (response as any).total || 0

      // 提取课程列表
      const courseMap = new Map()
      homeworksWithDetails.forEach((h: any) => {
        if (h.courseId && h.courseName && !courseMap.has(h.courseId)) {
          courseMap.set(h.courseId, { id: h.courseId, courseName: h.courseName })
        }
      })
      courses.value = Array.from(courseMap.values())

      // 计算统计数据
      calculateStats()
    }
  } catch (error: any) {
    console.error('加载作业列表失败:', error)
    ElMessage.error('加载作业列表失败')
  } finally {
    loading.value = false
  }
}

// 计算统计数据
const calculateStats = () => {
  const total = homeworkList.value.length
  const published = homeworkList.value.filter(h => h.status === 'published').length
  const draft = homeworkList.value.filter(h => h.status === 'draft').length
  const closed = homeworkList.value.filter(h => h.status === 'closed').length
  const pendingGrading = homeworkList.value.reduce((sum, h) => sum + (h.pendingGradingCount || 0), 0)
  const totalSubmissions = homeworkList.value.reduce((sum, h) => sum + (h.submissionCount || 0), 0)

  stats.value = {
    totalHomeworks: total,
    publishedCount: published,
    draftCount: draft,
    closedCount: closed,
    pendingGrading: pendingGrading,
    totalSubmissions: totalSubmissions
  }
}

// 搜索处理
const handleSearch = () => {
  // 搜索逻辑由 computed 自动处理
}

// 标签切换
const handleTabChange = () => {
  // 标签筛选逻辑由 computed 自动处理
}

// 课程筛选
const handleCourseFilter = () => {
  // 课程筛选逻辑由 computed 自动处理
}

// 分页
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadHomeworkList()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadHomeworkList()
}

// 操作处理
const handleAction = async (command: string, homework: any) => {
  switch (command) {
    case 'view':
      await viewHomeworkDetail(homework)
      break
    case 'submissions':
      viewSubmissions(homework)
      break
    case 'publish':
      await publishHomework(homework)
      break
    case 'close':
      await closeHomework(homework)
      break
    case 'edit':
      editHomework(homework)
      break
    case 'delete':
      await deleteHomework(homework)
      break
  }
}

// 查看作业详情
const viewHomeworkDetail = async (homework: any) => {
  currentHomework.value = homework
  detailDialogVisible.value = true

  // 加载统计信息
  loadingDetail.value = true
  try {
    const stats = await axios.get(`/homework/${homework.id}/stats`)
    currentHomeworkStats.value = stats
  } catch (error) {
    console.error('加载统计信息失败:', error)
  } finally {
    loadingDetail.value = false
  }
}

// 从详情对话框查看提交
const viewSubmissionsFromDetail = () => {
  detailDialogVisible.value = false
  viewSubmissions(currentHomework.value)
}

// 创建作业
const createHomework = () => {
  isEditMode.value = false
  publishOption.value = 'publish'
  homeworkForm.value = {
    courseId: null,
    title: '',
    description: '',
    homeworkType: 'assignment',
    maxScore: 100,
    deadline: ''
  }
  fileList.value = []
  formDialogVisible.value = true
}

// 编辑作业
const editHomework = (homework: any) => {
  isEditMode.value = true
  homeworkForm.value = {
    courseId: homework.courseId,
    title: homework.title,
    description: homework.description,
    homeworkType: homework.homeworkType,
    maxScore: homework.maxScore,
    deadline: homework.deadline
  }
  fileList.value = []
  currentHomework.value = homework
  formDialogVisible.value = true
}

// 保存作业
const saveHomework = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      const teacherId = getTeacherId()
      const data = {
        ...homeworkForm.value,
        teacherId: teacherId,
        status: publishOption.value === 'publish' ? 'published' : 'draft'
      }

      if (isEditMode.value) {
        await axios.put('/homework', {
          ...data,
          id: currentHomework.value.id
        })
        ElMessage.success('作业更新成功')
      } else {
        await axios.post('/homework', data)
        ElMessage.success(publishOption.value === 'publish' ? '作业发布成功' : '草稿保存成功')
      }

      formDialogVisible.value = false
      await loadHomeworkList()
    } catch (error: any) {
      console.error('保存作业失败:', error)
      ElMessage.error(error.message || '保存作业失败')
    } finally {
      saving.value = false
    }
  })
}

// 关闭表单对话框
const closeFormDialog = () => {
  formDialogVisible.value = false
  currentHomework.value = null
  homeworkForm.value = {
    courseId: null,
    title: '',
    description: '',
    homeworkType: 'assignment',
    maxScore: 100,
    deadline: ''
  }
  fileList.value = []
  formRef.value?.resetFields()
}

// 文件选择
const handleFileChange = (file: any) => {
  console.log('选择的文件:', file)
  // TODO: 实现文件上传逻辑
}

// 发布作业
const publishHomework = async (homework: any) => {
  try {
    await axios.post(`/homework/${homework.id}/publish`)
    ElMessage.success('作业发布成功')
    await loadHomeworkList()
  } catch (error: any) {
    console.error('发布作业失败:', error)
    ElMessage.error(error.message || '发布作业失败')
  }
}

// 关闭作业
const closeHomework = async (homework: any) => {
  try {
    await ElMessageBox.confirm('确定要关闭此作业吗?关闭后学生将无法提交。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await axios.post(`/homework/${homework.id}/close`)
    ElMessage.success('作业已关闭')
    await loadHomeworkList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('关闭作业失败:', error)
      ElMessage.error(error.message || '关闭作业失败')
    }
  }
}

// 删除作业
const deleteHomework = async (homework: any) => {
  try {
    await ElMessageBox.confirm('确定要删除此作业吗?此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await axios.delete(`/homework/${homework.id}`)
    ElMessage.success('作业删除成功')
    await loadHomeworkList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除作业失败:', error)
      ElMessage.error(error.message || '删除作业失败')
    }
  }
}

// 查看提交列表
const viewSubmissions = async (homework: any) => {
  currentHomework.value = homework
  submissionsDialogVisible.value = true
  submissionTab.value = 'all'
  await loadSubmissions()
}

// 加载提交列表
const loadSubmissions = async () => {
  loadingSubmissions.value = true
  try {
    const status = submissionTab.value === 'all' ? undefined : submissionTab.value
    const response = await axios.get('/homework-submission/page', {
      params: {
        current: 1,
        size: 100,
        homeworkId: currentHomework.value.id,
        status: status
      }
    })

    if (response && (response as any).records) {
      submissions.value = (response as any).records || []

      // 计算统计
      const all = await axios.get('/homework-submission/page', {
        params: { current: 1, size: 100, homeworkId: currentHomework.value.id }
      })
      const pending = await axios.get('/homework-submission/page', {
        params: { current: 1, size: 100, homeworkId: currentHomework.value.id, status: 'submitted' }
      })
      const graded = await axios.get('/homework-submission/page', {
        params: { current: 1, size: 100, homeworkId: currentHomework.value.id, status: 'graded' }
      })

      submissionStats.value = {
        total: (all as any).total || 0,
        pending: (pending as any).total || 0,
        graded: (graded as any).total || 0
      }
    }
  } catch (error: any) {
    console.error('加载提交列表失败:', error)
    ElMessage.error('加载提交列表失败')
  } finally {
    loadingSubmissions.value = false
  }
}

// 关闭提交列表对话框
const closeSubmissionsDialog = () => {
  submissionsDialogVisible.value = false
  currentHomework.value = null
  submissions.value = []
}

// 批改作业
const gradeSubmission = (submission: any) => {
  currentSubmission.value = submission
  gradingForm.value = {
    score: submission.score || 0,
    comment: submission.comment || ''
  }
  gradingDialogVisible.value = true
}

// 确认批改
const confirmGrade = async () => {
  if (gradingForm.value.score === null || gradingForm.value.score === undefined) {
    ElMessage.warning('请输入分数')
    return
  }

  savingGrade.value = true
  try {
    await axios.post(`/homework-submission/${currentSubmission.value.id}/grade`, gradingForm.value)
    ElMessage.success('批改成功')
    gradingDialogVisible.value = false
    await loadSubmissions()
    await loadHomeworkList()
  } catch (error: any) {
    console.error('批改失败:', error)
    ElMessage.error(error.message || '批改失败')
  } finally {
    savingGrade.value = false
  }
}

// 关闭批改对话框
const closeGradingDialog = () => {
  gradingDialogVisible.value = false
  currentSubmission.value = null
  gradingForm.value = {
    score: 0,
    comment: ''
  }
}

// 查看提交详情
const viewSubmissionDetail = (submission: any) => {
  // TODO: 实现查看提交详情功能
  ElMessageBox.alert(`
    <div style="line-height: 1.8;">
      <p><strong>学生姓名:</strong> ${submission.studentName}</p>
      <p><strong>学号:</strong> ${submission.studentNo}</p>
      <p><strong>提交时间:</strong> ${formatDateTime(submission.submitTime || submission.submitDate)}</p>
      <p><strong>提交状态:</strong> ${submission.status === 'graded' ? '已批改' : '待批改'}</p>
      ${submission.score ? `<p><strong>得分:</strong> ${submission.score}分</p>` : ''}
      <p><strong>提交内容:</strong></p>
      <p>${submission.content || '无内容'}</p>
      ${submission.comment ? `<p><strong>教师评语:</strong> ${submission.comment}</p>` : ''}
    </div>
  `, '提交详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭'
  })
}

// 下载文件
const downloadFile = (fileUrl: string, defaultName: string) => {
  if (!fileUrl) {
    ElMessage.warning('文件地址不存在')
    return
  }

  try {
    // 从URL中提取文件名
    const urlParts = fileUrl.split('/')
    const fileName = urlParts[urlParts.length - 1] || defaultName

    // 创建下载链接
    const link = document.createElement('a')
    link.href = fileUrl
    link.download = fileName
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success('开始下载')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  }
}

onMounted(() => {
  loadHomeworkList()
})
</script>

<style scoped lang="scss">
.homework-management-page {
  padding: 2rem;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;

  .page-title {
    font-size: 2rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: var(--el-text-color-primary);
  }

  .page-subtitle {
    font-size: 1rem;
    color: var(--el-text-color-secondary);
    margin: 0;
  }

  .header-actions {
    display: flex;
    gap: 1rem;
  }
}

// 统计卡片
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    flex-shrink: 0;
  }

  .stat-content {
    flex: 1;
  }

  .stat-label {
    font-size: 0.875rem;
    color: var(--el-text-color-secondary);
    margin-bottom: 0.25rem;
  }

  .stat-value {
    font-size: 1.75rem;
    font-weight: 700;
    color: var(--el-text-color-primary);
  }

  &.stat-card-primary .stat-icon {
    background: oklch(0.95 0.02 220);
    color: oklch(0.55 0.15 220);
  }

  &.stat-card-success .stat-icon {
    background: oklch(0.92 0.04 150);
    color: oklch(0.55 0.15 150);
  }

  &.stat-card-warning .stat-icon {
    background: oklch(0.95 0.02 45);
    color: oklch(0.60 0.18 45);
  }

  &.stat-card-info .stat-icon {
    background: oklch(0.92 0.04 280);
    color: oklch(0.55 0.15 280);
  }
}

// 筛选区域
.filter-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .filter-controls {
    display: flex;
    gap: 1rem;
    margin-top: 1rem;
    flex-wrap: wrap;
  }
}

// 作业列表
.homework-list {
  min-height: 400px;
}

.homework-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 1.5rem;
}

.homework-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .homework-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
    gap: 1rem;

    .homework-title-section {
      flex: 1;

      .homework-title {
        font-size: 1.125rem;
        font-weight: 600;
        margin: 0 0 0.5rem 0;
        color: var(--el-text-color-primary);
        line-height: 1.4;
      }

      .homework-meta {
        display: flex;
        flex-wrap: wrap;
        gap: 0.75rem;
        align-items: center;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 0.25rem;
          font-size: 0.875rem;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }

  .homework-description {
    font-size: 0.875rem;
    color: var(--el-text-color-secondary);
    line-height: 1.6;
    margin: 0 0 1rem 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    min-height: 2.8em;
  }

  .homework-stats {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
    margin-bottom: 1rem;
    padding: 1rem;
    background: oklch(0.98 0.01 220);
    border-radius: 8px;

    .stat-item {
      .stat-label {
        font-size: 0.75rem;
        color: var(--el-text-color-secondary);
        margin-bottom: 0.25rem;
      }

      .stat-value {
        font-size: 0.875rem;
        font-weight: 600;
        color: var(--el-text-color-primary);
        text-align: right;
      }
    }
  }

  .homework-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 1rem;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

// 详情对话框样式
.homework-detail-description {
  white-space: pre-wrap;
  line-height: 1.8;
  color: var(--el-text-color-regular);
}

.detail-stats {
  margin-top: 1.5rem;

  h4 {
    font-size: 1rem;
    font-weight: 600;
    margin-bottom: 1rem;
    color: var(--el-text-color-primary);
  }

  .stat-box {
    text-align: center;
    padding: 1rem;
    background: oklch(0.98 0.01 220);
    border-radius: 8px;

    .stat-value {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--el-color-primary);
      margin-bottom: 0.25rem;
    }

    .stat-label {
      font-size: 0.875rem;
      color: var(--el-text-color-secondary);
    }
  }
}

// 批改相关样式
.submission-content-view {
  white-space: pre-wrap;
  line-height: 1.8;
  max-height: 300px;
  overflow-y: auto;
  padding: 0.75rem;
  background: oklch(0.98 0.01 220);
  border-radius: 4px;
}

.score-excellent { color: oklch(0.55 0.15 150); font-weight: 600; }
.score-good { color: oklch(0.55 0.15 220); font-weight: 600; }
.score-pass { color: oklch(0.60 0.18 45); font-weight: 600; }
.score-fail { color: oklch(0.60 0.18 25); font-weight: 600; }

// 分页
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

// 响应式设计
@media (max-width: 768px) {
  .homework-management-page {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    gap: 1rem;

    .header-actions {
      width: 100%;
    }
  }

  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }

  .filter-controls {
    flex-direction: column;

    .el-input,
    .el-select {
      width: 100% !important;
    }
  }

  .homework-cards {
    grid-template-columns: 1fr;
  }
}
</style>
