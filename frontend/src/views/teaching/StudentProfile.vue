<template>
  <div class="student-profile">
    <!-- 顶部搜索和筛选 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学员姓名、专业"
            clearable
            style="width: 280px"
            :prefix-icon="Search"
            @clear="loadStudentList"
            @keyup.enter="loadStudentList"
          />
          <el-select
            v-model="filterMajor"
            placeholder="专业筛选"
            clearable
            style="width: 150px"
            @change="loadStudentList"
          >
            <el-option label="全部专业" value="" />
            <el-option
              v-for="major in majorList"
              :key="major"
              :label="major"
              :value="major"
            />
          </el-select>
          <el-select
            v-model="filterEmployment"
            placeholder="就业状态"
            clearable
            style="width: 130px"
            @change="loadStudentList"
          >
            <el-option label="全部状态" value="" />
            <el-option label="已就业" value="employed" />
            <el-option label="求职中" value="seeking" />
            <el-option label="已录取" value="admitted" />
            <el-option label="继续深造" value="further" />
          </el-select>
        </div>
        <div class="filter-right">
          <el-button :icon="Refresh" @click="loadStudentList">刷新</el-button>
        </div>
      </div>
    </el-card>

    <!-- 学员档案列表 -->
    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="studentList"
        style="width: 100%"
        stripe
        @row-click="viewStudentProfile"
      >
        <el-table-column label="学员" width="180">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 12px">
              <el-avatar :size="48">{{ row.realName?.charAt(0) }}</el-avatar>
              <div>
                <div style="font-weight: 500; font-size: 14px">{{ row.realName }}</div>
                <div style="font-size: 12px; color: #909399">
                  {{ row.studentNo }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="major" label="专业" width="120" />

        <el-table-column prop="grade" label="年级" width="100" />

        <el-table-column label="就业状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getEmploymentStatusType(row.employmentStatus)" size="small">
              {{ getEmploymentStatusText(row.employmentStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="求职进度" width="280">
          <template #default="{ row }">
            <div class="progress-info">
              <div class="progress-item">
                <span class="progress-label">投递:</span>
                <span class="progress-value">{{ row.applicationCount || 0 }}</span>
              </div>
              <div class="progress-item">
                <span class="progress-label">笔试:</span>
                <span class="progress-value">{{ row.testCount || 0 }}</span>
              </div>
              <div class="progress-item">
                <span class="progress-label">面试:</span>
                <span class="progress-value">{{ row.interviewCount || 0 }}</span>
              </div>
              <div class="progress-item">
                <span class="progress-label">Offer:</span>
                <span class="progress-value offer-count">{{ row.offerCount || 0 }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="联系方式" width="140">
          <template #default="{ row }">
            <div style="font-size: 13px">
              <div style="margin-bottom: 4px;">
                <el-icon style="color: #409eff"><Phone /></el-icon>
                {{ row.phone || '-' }}
              </div>
              <div style="color: #909399; font-size: 12px;">
                <el-icon style="color: #67c23a"><Message /></el-icon>
                {{ row.email || '-' }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click.stop="viewStudentProfile(row)">
              查看档案
            </el-button>
            <el-button link type="success" size="small" @click.stop="viewResume(row)">
              查看简历
            </el-button>
            <el-button link type="warning" size="small" @click.stop="recommendPosition(row)">
              推荐职位
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadStudentList"
        @current-change="loadStudentList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 学员档案详情对话框 -->
    <el-dialog
      v-model="profileDialogVisible"
      :title="`${currentStudent?.realName}的档案`"
      width="900px"
      @open="loadStudentDetail"
    >
      <div v-loading="detailLoading">
        <el-tabs v-if="currentStudent" v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="姓名">
                {{ currentStudent.realName }}
              </el-descriptions-item>
              <el-descriptions-item label="学号">
                {{ currentStudent.studentNo }}
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                {{ currentStudent.gender === 1 ? '男' : '女' }}
              </el-descriptions-item>
              <el-descriptions-item label="出生日期">
                {{ currentStudent.birthDate || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="专业">
                {{ currentStudent.major }}
              </el-descriptions-item>
              <el-descriptions-item label="年级">
                {{ currentStudent.grade }}
              </el-descriptions-item>
              <el-descriptions-item label="班级">
                {{ currentStudent.className || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="入学日期">
                {{ currentStudent.enrollmentDate || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="手机号" :span="2">
                {{ currentStudent.phone || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="邮箱" :span="2">
                {{ currentStudent.email || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="身份证号" :span="2">
                {{ currentStudent.idCard ? maskIdCard(currentStudent.idCard) : '-' }}
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>

          <!-- 求职状态 -->
          <el-tab-pane label="求职状态" name="employment">
            <div class="employment-detail">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="就业状态">
                  <el-tag :type="getEmploymentStatusType(currentStudent.employmentStatus)" size="large">
                    {{ getEmploymentStatusText(currentStudent.employmentStatus) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="目标职位">
                  {{ currentStudent.targetPosition || '未填写' }}
                </el-descriptions-item>
                <el-descriptions-item label="期望城市">
                  {{ currentStudent.expectedCity || '未填写' }}
                </el-descriptions-item>
                <el-descriptions-item label="期望薪资">
                  <span v-if="currentStudent.expectedSalaryMin || currentStudent.expectedSalaryMax">
                    {{ currentStudent.expectedSalaryMin }}-{{ currentStudent.expectedSalaryMax }} / 月
                  </span>
                  <span v-else style="color: #909399">未填写</span>
                </el-descriptions-item>
              </el-descriptions>

              <el-divider />

              <h4 style="margin-bottom: 16px">求职统计</h4>
              <el-row :gutter="20">
                <el-col :span="6">
                  <div class="stat-box">
                    <div class="stat-value">{{ currentStudent.applicationCount || 0 }}</div>
                    <div class="stat-label">投递简历</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-box">
                    <div class="stat-value">{{ currentStudent.testCount || 0 }}</div>
                    <div class="stat-label">参加笔试</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-box">
                    <div class="stat-value">{{ currentStudent.interviewCount || 0 }}</div>
                    <div class="stat-label">参加面试</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-box">
                    <div class="stat-value offer-stat">{{ currentStudent.offerCount || 0 }}</div>
                    <div class="stat-label">收到Offer</div>
                  </div>
                </el-col>
              </el-row>

              <el-divider />

              <h4 style="margin-bottom: 16px">Offer记录</h4>
              <el-table v-if="currentStudent.offers && currentStudent.offers.length > 0" :data="currentStudent.offers" size="small">
                <el-table-column prop="companyName" label="企业" width="150" />
                <el-table-column prop="positionName" label="职位" width="150" />
                <el-table-column label="薪资">
                  <template #default="{ row }">
                    ¥{{ row.salary }} / {{ row.salaryUnit === 'month' ? '月' : '年' }}
                  </template>
                </el-table-column>
                <el-table-column prop="city" label="城市" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getOfferStatusType(row.status)" size="small">
                      {{ getOfferStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无Offer记录" :image-size="60" />
            </div>
          </el-tab-pane>

          <!-- 技能标签 -->
          <el-tab-pane label="技能标签" name="skills">
            <div class="skills-detail">
              <h4 style="margin-bottom: 16px">专业技能</h4>
              <div class="skills-container">
                <el-tag
                  v-for="skill in (currentStudent.skills || [])"
                  :key="skill"
                  size="large"
                  style="margin: 0 8px 8px 0"
                >
                  {{ skill }}
                </el-tag>
                <el-tag v-if="!currentStudent.skills || currentStudent.skills.length === 0" type="info">
                  未填写技能标签
                </el-tag>
              </div>

              <h4 style="margin: 24px 0 16px 0">项目经验</h4>
              <el-timeline>
                <el-timeline-item
                  v-for="(project, index) in (currentStudent.projects || [])"
                  :key="index"
                  :timestamp="project.period"
                  placement="top"
                >
                  <el-card>
                    <h4>{{ project.name }}</h4>
                    <p style="color: #606266; margin: 8px 0;">{{ project.description }}</p>
                    <div style="margin-bottom: 8px;">
                      <el-tag v-for="tech in project.technologies" :key="tech" size="small" style="margin-right: 4px;">
                        {{ tech }}
                      </el-tag>
                    </div>
                    <div style="font-size: 12px; color: #909399">
                      角色: {{ project.role }}
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-if="!currentStudent.projects || currentStudent.projects.length === 0" description="暂无项目经验" :image-size="60" />
            </div>
          </el-tab-pane>

          <!-- 教育经历 -->
          <el-tab-pane label="教育经历" name="education">
            <div class="education-detail">
              <el-descriptions v-if="currentStudent.education && currentStudent.education.length > 0" :column="1" border>
                <el-descriptions-item
                  v-for="(edu, index) in currentStudent.education"
                  :key="index"
                  :label="edu.school || '教育经历'"
                >
                  <div>
                    <div style="font-weight: 500; margin-bottom: 4px;">
                      {{ edu.major }} · {{ edu.degree }}
                    </div>
                    <div style="color: #909399; font-size: 13px;">
                      {{ edu.startDate }} - {{ edu.endDate }}
                    </div>
                    <div v-if="edu.description" style="margin-top: 8px; color: #606266; font-size: 13px;">
                      {{ edu.description }}
                    </div>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
              <el-empty v-else description="暂无教育经历" :image-size="60" />
            </div>
          </el-tab-pane>

          <!-- 课程成绩 -->
          <el-tab-pane label="课程成绩" name="courses">
            <div class="courses-detail">
              <el-table v-if="currentStudent.courses && currentStudent.courses.length > 0" :data="currentStudent.courses" size="small" stripe>
                <el-table-column prop="courseName" label="课程名称" width="200" />
                <el-table-column prop="credit" label="学分" width="80" align="center" />
                <el-table-column prop="score" label="成绩" width="80" align="center">
                  <template #default="{ row }">
                    <el-tag :type="getScoreType(row.score)" size="small">
                      {{ row.score }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="semester" label="学期" width="120" />
                <el-table-column prop="teacherName" label="授课教师" />
              </el-table>
              <el-empty v-else description="暂无课程成绩" :image-size="60" />
            </div>
          </el-tab-pane>

          <!-- 就业指导记录 -->
          <el-tab-pane label="指导记录" name="guidance">
            <div class="guidance-detail">
              <div class="guidance-actions" style="margin-bottom: 20px;">
                <el-button type="primary" size="small" @click="addGuidance">
                  <el-icon><Plus /></el-icon>
                  添加指导记录
                </el-button>
              </div>

              <el-timeline>
                <el-timeline-item
                  v-for="(record, index) in (currentStudent.guidanceRecords || [])"
                  :key="index"
                  :timestamp="record.date"
                  placement="top"
                >
                  <el-card>
                    <div class="guidance-header">
                      <span class="guidance-type">{{ record.type }}</span>
                      <span class="guidance-author">{{ record.author }}</span>
                    </div>
                    <p style="color: #606266; margin: 8px 0;">{{ record.content }}</p>
                    <div v-if="record.nextAction" style="padding: 8px; background: #f5f7fa; border-radius: 4px; font-size: 13px;">
                      <strong>后续跟进：</strong>{{ record.nextAction }}
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-if="!currentStudent.guidanceRecords || currentStudent.guidanceRecords.length === 0" description="暂无指导记录" :image-size="60" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <template #footer>
        <el-button @click="profileDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="recommendPosition">推荐职位</el-button>
        <el-button type="success" @click="addGuidance">添加指导</el-button>
      </template>
    </el-dialog>

    <!-- 推荐职位对话框 -->
    <el-dialog
      v-model="recommendDialogVisible"
      title="推荐职位"
      width="700px"
    >
      <el-form label-width="80px">
        <el-form-item label="选择职位">
          <el-select
            v-model="recommendForm.positionId"
            placeholder="请选择职位"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="position in availablePositions"
              :key="position.id"
              :label="`${position.companyName} - ${position.positionName}`"
              :value="position.id"
            >
              <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
                <span>{{ position.companyName }} - {{ position.positionName }}</span>
                <span style="color: #909399; font-size: 12px;">{{ position.city }} · ¥{{ position.salaryMin }}-{{ position.salaryMax }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="推荐理由">
          <el-input
            v-model="recommendForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请填写推荐理由，如：技能匹配、项目经验相关等"
          />
        </el-form-item>
        <el-form-item label="推荐备注">
          <el-input
            v-model="recommendForm.remark"
            type="textarea"
            :rows="3"
            placeholder="其他备注信息（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="recommendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecommend" :loading="recommending">
          确认推荐
        </el-button>
      </template>
    </el-dialog>

    <!-- 简历预览对话框 -->
    <el-dialog
      v-model="resumePreviewDialogVisible"
      title="简历预览"
      width="80%"
      top="5vh"
      @close="closeResumePreview"
    >
      <div v-loading="pdfLoading" element-loading-text="加载中..." style="min-height: 600px">
        <div v-if="pdfError" style="text-align: center; padding: 50px">
          <el-empty description="PDF加载失败"></el-empty>
          <el-button type="primary" @click="resumePreviewDialogVisible = false">关闭</el-button>
        </div>
        <VuePdfEmbed
          v-if="resumePreviewUrl && !pdfError"
          :source="resumePreviewUrl"
          @loaded="handlePdfLoaded"
          @error="handlePdfError"
          style="width: 100%; height: 700px"
        />
      </div>
      <template #footer>
        <el-button @click="resumePreviewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="downloadResume" v-if="resumePreviewUrl">
          下载简历
        </el-button>
      </template>
    </el-dialog>

    <!-- 添加指导记录对话框 -->
    <el-dialog
      v-model="guidanceDialogVisible"
      title="添加指导记录"
      width="600px"
    >
      <el-form label-width="100px">
        <el-form-item label="指导类型" required>
          <el-select v-model="guidanceForm.guidanceType" placeholder="请选择指导类型" style="width: 100%">
            <el-option
              v-for="option in guidanceTypeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="指导时间" required>
          <el-date-picker
            v-model="guidanceForm.guidanceDate"
            type="datetime"
            placeholder="选择指导时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="指导地点">
          <el-input v-model="guidanceForm.location" placeholder="如：线上、办公室A201等" />
        </el-form-item>
        <el-form-item label="指导内容" required>
          <el-input
            v-model="guidanceForm.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述指导内容..."
          />
        </el-form-item>
        <el-form-item label="后续跟进">
          <el-input
            v-model="guidanceForm.nextAction"
            type="textarea"
            :rows="3"
            placeholder="后续需要跟进的事项（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="guidanceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGuidance" :loading="guidanceSubmitting">
          确认添加
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh,
  Phone,
  Message,
  Plus
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import {
  getMajorListApi,
  getStudentByIdApi,
  getStudentSkillsApi,
  getStudentProjectsApi,
  getStudentPreferenceApi,
  getStudentCoursesApi,
  getStudentEducationApi,
  getStudentPageApi
} from '@/api/student'
import { getPositionListApi } from '@/api/position'
import {
  createRecommendationApi,
  createGuidanceApi,
  getGuidanceListByStudentApi
} from '@/api/guidance'
import VuePdfEmbed from 'vue-pdf-embed'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const profileDialogVisible = ref(false)
const recommendDialogVisible = ref(false)
const resumePreviewDialogVisible = ref(false)
const resumePreviewUrl = ref('')
const pdfLoading = ref(true)
const pdfError = ref(false)
const recommending = ref(false)

// 专业列表
const majorList = ref<string[]>([])

// 搜索和筛选
const searchKeyword = ref('')
const filterMajor = ref('')
const filterEmployment = ref('')

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})
const total = ref(0)

// 学员列表
const studentList = ref([])
const currentStudent = ref<any>(null)
const activeTab = ref('basic')

// 推荐表单
const recommendForm = reactive({
  positionId: null,
  reason: '',
  remark: ''
})

// 可推荐的职位
const availablePositions = ref([])

// 指导记录表单
const guidanceForm = reactive({
  guidanceType: '',
  content: '',
  nextAction: '',
  guidanceDate: '',
  location: ''
})

// 指导记录对话框
const guidanceDialogVisible = ref(false)
const guidanceSubmitting = ref(false)

// 指导类型选项
const guidanceTypeOptions = [
  { label: '职业规划', value: 'career_planning' },
  { label: '简历指导', value: 'resume_guidance' },
  { label: '面试指导', value: 'interview_guidance' },
  { label: '技能提升', value: 'skill_improvement' },
  { label: '心理辅导', value: 'psychological_counseling' },
  { label: '其他', value: 'other' }
]

// 加载学员列表
const loadStudentList = async () => {
  try {
    loading.value = true

    // 调用后端API获取学员数据
    const data: any = await getStudentPageApi({
      current: pagination.current,
      size: pagination.size,
      schoolId: undefined,
      keyword: undefined
    })

    let filteredData = data.records || []

    // 按专业筛选
    if (filterMajor.value) {
      filteredData = filteredData.filter((student: any) => student.major === filterMajor.value)
    }

    // 按就业状态筛选
    if (filterEmployment.value) {
      filteredData = filteredData.filter((student: any) => student.employmentStatus === filterEmployment.value)
    }

    // 按关键词搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredData = filteredData.filter((student: any) =>
        student.realName.toLowerCase().includes(keyword) ||
        student.major.toLowerCase().includes(keyword) ||
        student.studentNo.toLowerCase().includes(keyword)
      )
    }

    studentList.value = filteredData
    total.value = data.total || 0

  } catch (error) {
    console.error('加载学员列表失败', error)
  } finally {
    loading.value = false
  }
}

// 加载学员详细信息
const loadStudentDetail = async () => {
  try {
    detailLoading.value = true

    if (!currentStudent.value?.id) {
      return
    }

    // 并行加载学员的各种详细信息
    const [skills, projects, preference, courses, education] = await Promise.all([
      getStudentSkillsApi(currentStudent.value.id).catch(() => []),
      getStudentProjectsApi(currentStudent.value.id).catch(() => []),
      getStudentPreferenceApi(currentStudent.value.id).catch(() => ({})),
      getStudentCoursesApi(currentStudent.value.id).catch(() => []),
      getStudentEducationApi(currentStudent.value.id).catch(() => [])
    ])

    // 更新当前学员对象的详细信息
    currentStudent.value.skills = skills || []
    currentStudent.value.projects = projects || []
    currentStudent.value.preference = preference || {}
    currentStudent.value.courses = courses || []
    currentStudent.value.education = education || []

    // 求职偏好映射到显示字段
    if (preference) {
      currentStudent.value.targetPosition = preference.targetPosition || ''
      currentStudent.value.expectedCity = preference.expectedCity || ''
      currentStudent.value.expectedSalaryMin = preference.expectedSalaryMin || ''
      currentStudent.value.expectedSalaryMax = preference.expectedSalaryMax || ''
    }

  } catch (error) {
    console.error('加载学员详情失败', error)
    ElMessage.error('加载学员详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 查看学员档案
const viewStudentProfile = (row: any) => {
  currentStudent.value = row
  activeTab.value = 'basic'
  profileDialogVisible.value = true
}

// 查看简历
const viewResume = (row: any) => {
  if (!row.resumeUrl) {
    ElMessage.warning('该学员尚未上传简历')
    return
  }
  // 设置当前预览的简历URL和显示对话框
  resumePreviewUrl.value = row.resumeUrl
  resumePreviewDialogVisible.value = true
  pdfLoading.value = true
  pdfError.value = false
}

// PDF加载完成
const handlePdfLoaded = () => {
  pdfLoading.value = false
}

// PDF加载失败
const handlePdfError = () => {
  pdfLoading.value = false
  pdfError.value = true
  ElMessage.error('PDF加载失败')
}

// 关闭简历预览
const closeResumePreview = () => {
  resumePreviewDialogVisible.value = false
  resumePreviewUrl.value = ''
  pdfLoading.value = true
  pdfError.value = false
}

// 下载简历
const downloadResume = () => {
  if (resumePreviewUrl.value) {
    window.open(resumePreviewUrl.value, '_blank')
  }
}

// 推荐职位
const recommendPosition = async (row: any) => {
  currentStudent.value = row

  try {
    // 从API加载所有可推荐的职位
    const positions: any = await getPositionListApi()
    availablePositions.value = positions || []
    recommendDialogVisible.value = true
  } catch (error) {
    console.error('加载职位列表失败', error)
    ElMessage.error('加载职位列表失败')
  }
}

// 提交推荐
const submitRecommend = async () => {
  if (!recommendForm.positionId) {
    ElMessage.warning('请选择推荐职位')
    return
  }

  if (!currentStudent.value) {
    ElMessage.warning('请先选择学员')
    return
  }

  try {
    recommending.value = true
    // 从authStore获取teacherId
    const authStore = useAuthStore()
    const teacherId = authStore.teacherId

    await createRecommendationApi({
      studentId: currentStudent.value.id,
      positionId: recommendForm.positionId,
      teacherId: teacherId || 0,
      reason: recommendForm.reason,
      remark: recommendForm.remark
    })

    ElMessage.success('推荐成功')
    recommendDialogVisible.value = false

    // 重置表单
    recommendForm.positionId = null
    recommendForm.reason = ''
    recommendForm.remark = ''
  } catch (error) {
    ElMessage.error('推荐失败')
  } finally {
    recommending.value = false
  }
}

// 添加指导记录
const addGuidance = () => {
  // 设置默认指导时间为当前时间
  const now = new Date()
  guidanceForm.guidanceDate = now.toISOString().slice(0, 16) // 格式化为 yyyy-MM-dd HH:mm
  guidanceDialogVisible.value = true
}

// 提交指导记录
const submitGuidance = async () => {
  if (!guidanceForm.guidanceType) {
    ElMessage.warning('请选择指导类型')
    return
  }

  if (!guidanceForm.content) {
    ElMessage.warning('请填写指导内容')
    return
  }

  if (!currentStudent.value) {
    ElMessage.warning('请先选择学员')
    return
  }

  try {
    guidanceSubmitting.value = true
    // 从authStore获取teacherId
    const authStore = useAuthStore()
    const teacherId = authStore.teacherId

    await createGuidanceApi({
      studentId: currentStudent.value.id,
      teacherId: teacherId || 0,
      guidanceType: guidanceForm.guidanceType,
      content: guidanceForm.content,
      nextAction: guidanceForm.nextAction,
      guidanceDate: guidanceForm.guidanceDate + ':00',
      location: guidanceForm.location
    })

    ElMessage.success('添加成功')
    guidanceDialogVisible.value = false

    // 重置表单
    guidanceForm.guidanceType = ''
    guidanceForm.content = ''
    guidanceForm.nextAction = ''
    guidanceForm.guidanceDate = ''
    guidanceForm.location = ''

    // 重新加载学员详情以显示新增的指导记录
    if (currentStudent.value.id) {
      const guidances: any = await getGuidanceListByStudentApi(currentStudent.value.id)
      currentStudent.value.guidanceRecords = guidances || []
    }
  } catch (error) {
    ElMessage.error('添加失败')
  } finally {
    guidanceSubmitting.value = false
  }
}

// 就业状态类型
const getEmploymentStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    employed: 'success',
    seeking: 'warning',
    admitted: 'info',
    further: 'info'
  }
  return typeMap[status] || 'info'
}

const getEmploymentStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    employed: '已就业',
    seeking: '求职中',
    admitted: '已录取',
    further: '继续深造',
    unemployed: '未就业'
  }
  return textMap[status] || status
}

// Offer状态类型
const getOfferStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    accepted: 'success',
    rejected: 'danger',
    pending: 'warning'
  }
  return typeMap[status] || 'info'
}

const getOfferStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    accepted: '已接受',
    rejected: '已拒绝',
    pending: '待接受'
  }
  return textMap[status] || status
}

// 成绩类型
const getScoreType = (score: number) => {
  if (score >= 90) return 'success' // 优秀 - 绿色
  if (score >= 80) return '' // 良好 - 默认
  if (score >= 70) return 'warning' // 中等 - 橙色
  if (score >= 60) return 'info' // 及格 - 灰色
  return 'danger' // 不及格 - 红色
}

// 身份证脱敏
const maskIdCard = (idCard: string) => {
  if (!idCard || idCard.length < 8) return idCard
  return idCard.substring(0, 6) + '********' + idCard.substring(14)
}

onMounted(() => {
  loadMajorList()
  loadStudentList()
})

// 加载专业列表
const loadMajorList = async () => {
  try {
    const data: any = await getMajorListApi()
    majorList.value = data || []
  } catch (error) {
    console.error('加载专业列表失败', error)
  }
}
</script>

<style lang="scss" scoped>
.student-profile {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;

  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .filter-left {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }
}

.table-card {
  :deep(.el-table__row) {
    cursor: pointer;

    &:hover {
      background-color: #f5f7fa;
    }
  }
}

.progress-info {
  display: flex;
  gap: 16px;

  .progress-item {
    .progress-label {
      font-size: 12px;
      color: #909399;
      margin-right: 4px;
    }

    .progress-value {
      font-size: 14px;
      color: #303133;
      font-weight: 500;

      &.offer-count {
        color: #67c23a;
        font-weight: 600;
      }
    }
  }
}

.employment-detail {
  .stat-box {
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
    text-align: center;

    .stat-value {
      font-size: 32px;
      font-weight: 600;
      color: #303133;
      line-height: 1;
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
    }

    &.offer-stat .stat-value {
      color: #67c23a;
    }
  }
}

.skills-detail {
  .skills-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 20px;
  }
}

.guidance-detail {
  .guidance-actions {
    display: flex;
    gap: 12px;
  }

  .guidance-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .guidance-type {
      font-weight: 500;
      color: #409eff;
    }

    .guidance-author {
      font-size: 12px;
      color: #909399;
    }
  }
}

.resume-preview-container {
  height: 70vh;

  .resume-preview-iframe {
    width: 100%;
    height: 100%;
    border: none;
  }
}

.resume-empty {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
