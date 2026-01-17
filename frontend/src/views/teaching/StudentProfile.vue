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
            <el-option label="软件工程" value="软件工程" />
            <el-option label="计算机科学" value="计算机科学" />
            <el-option label="信息安全" value="信息安全" />
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

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const profileDialogVisible = ref(false)
const recommendDialogVisible = ref(false)
const recommending = ref(false)

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

// 加载学员列表
const loadStudentList = async () => {
  try {
    loading.value = true

    // TODO: 根据筛选条件加载学员数据
    const mockData = [
      {
        id: 1,
        realName: '张三',
        studentNo: '2021001',
        major: '软件工程',
        grade: '2021级',
        className: '软工2101',
        gender: 1,
        birthDate: '2003-05-15',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        idCard: '110101200305151234',
        enrollmentDate: '2021-09-01',
        employmentStatus: 'employed',
        applicationCount: 15,
        testCount: 5,
        interviewCount: 3,
        offerCount: 2,
        skills: ['Java', 'Spring Boot', 'MySQL', 'Vue.js'],
        projects: [
          {
            name: '电商管理系统',
            description: '基于Spring Boot和Vue的电商后台管理系统',
            period: '2023.09 - 2023.12',
            role: '后端开发',
            technologies: ['Java', 'Spring Boot', 'MyBatis', 'MySQL']
          }
        ],
        guidanceRecords: []
      },
      {
        id: 2,
        realName: '李四',
        studentNo: '2021002',
        major: '计算机科学',
        grade: '2021级',
        className: '计科2101',
        gender: 2,
        phone: '13800138002',
        email: 'lisi@example.com',
        employmentStatus: 'seeking',
        applicationCount: 8,
        testCount: 3,
        interviewCount: 1,
        offerCount: 0
      },
      {
        id: 3,
        realName: '王五',
        studentNo: '2021003',
        major: '软件工程',
        grade: '2021级',
        className: '软工2102',
        gender: 1,
        phone: '13800138003',
        email: 'wangwu@example.com',
        employmentStatus: 'admitted',
        applicationCount: 12,
        testCount: 4,
        interviewCount: 2,
        offerCount: 1
      }
    ]

    studentList.value = mockData
    total.value = mockData.length

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
    // TODO: 加载学员详细信息
    if (currentStudent.value) {
      // 模拟添加Offer数据
      currentStudent.value.offers = [
        {
          companyName: '腾讯科技',
          positionName: 'Java后端工程师',
          salary: '18000',
          salaryUnit: 'month',
          city: '深圳',
          status: 'accepted'
        },
        {
          companyName: '阿里巴巴',
          positionName: '全栈工程师',
          salary: '20000',
          salaryUnit: 'month',
          city: '杭州',
          status: 'rejected'
        }
      ]
    }
  } catch (error) {
    console.error('加载学员详情失败', error)
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
  ElMessage.info('简历查看功能开发中')
}

// 推荐职位
const recommendPosition = (row: any) => {
  currentStudent.value = row
  // 加载可推荐的职位
  availablePositions.value = [
    {
      id: 13,
      companyName: '腾讯科技',
      positionName: 'Java后端工程师',
      city: '深圳',
      salaryMin: 15000,
      salaryMax: 22000
    },
    {
      id: 14,
      companyName: '字节跳动',
      positionName: '前端开发工程师',
      city: '北京',
      salaryMin: 18000,
      salaryMax: 25000
    }
  ]
  recommendDialogVisible.value = true
}

// 提交推荐
const submitRecommend = async () => {
  if (!recommendForm.positionId) {
    ElMessage.warning('请选择推荐职位')
    return
  }

  try {
    recommending.value = true
    // TODO: 调用推荐API
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
  ElMessage.info('添加指导记录功能开发中')
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

// 身份证脱敏
const maskIdCard = (idCard: string) => {
  if (!idCard || idCard.length < 8) return idCard
  return idCard.substring(0, 6) + '********' + idCard.substring(14)
}

onMounted(() => {
  loadStudentList()
})
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
</style>
