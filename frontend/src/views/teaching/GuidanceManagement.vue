<template>
  <div class="guidance-management">
    <el-card shadow="never" class="header-card">
      <div class="header-content">
        <h2>就业指导管理</h2>
        <p class="description">管理学员的就业指导记录和职位推荐</p>
      </div>
    </el-card>

    <el-card shadow="never" class="tabs-card">
      <el-tabs v-model="activeTab">
        <!-- 指导记录管理 -->
        <el-tab-pane label="指导记录" name="guidance">
          <div class="tab-content">
            <div class="toolbar">
              <el-input
                v-model="guidanceKeyword"
                placeholder="搜索学员姓名..."
                clearable
                style="width: 280px"
                :prefix-icon="Search"
                @clear="loadGuidances"
                @keyup.enter="loadGuidances"
              />
              <el-button type="primary" :icon="Plus" @click="showAddGuidanceDialog">
                添加指导记录
              </el-button>
            </div>

            <el-table
              v-loading="guidanceLoading"
              :data="guidanceList"
              style="width: 100%"
              stripe
            >
              <el-table-column label="学员" width="120">
                <template #default="{ row }">
                  {{ row.studentName }}
                </template>
              </el-table-column>
              <el-table-column label="指导类型" width="120">
                <template #default="{ row }">
                  <el-tag :type="getGuidanceTypeColor(row.guidanceType)" size="small">
                    {{ row.guidanceTypeName || row.guidanceType }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="指导内容" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.content }}
                </template>
              </el-table-column>
              <el-table-column label="指导时间" width="160">
                <template #default="{ row }">
                  {{ formatDateTime(row.guidanceDate) }}
                </template>
              </el-table-column>
              <el-table-column label="指导教师" width="100">
                <template #default="{ row }">
                  {{ row.teacherName }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="viewGuidance(row)">
                    查看
                  </el-button>
                  <el-button link type="danger" size="small" @click="deleteGuidance(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-if="guidanceTotal > 0"
              v-model:current-page="guidancePagination.current"
              v-model:page-size="guidancePagination.size"
              :total="guidanceTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadGuidances"
              @current-change="loadGuidances"
              style="margin-top: 20px; justify-content: flex-end"
            />
          </div>
        </el-tab-pane>

        <!-- 职位推荐管理 -->
        <el-tab-pane label="职位推荐" name="recommendation">
          <div class="tab-content">
            <div class="toolbar">
              <el-input
                v-model="recommendationKeyword"
                placeholder="搜索学员姓名..."
                clearable
                style="width: 280px"
                :prefix-icon="Search"
                @clear="loadRecommendations"
                @keyup.enter="loadRecommendations"
              />
              <el-button type="primary" :icon="Plus" @click="showAddRecommendDialog">
                推荐职位
              </el-button>
            </div>

            <el-table
              v-loading="recommendationLoading"
              :data="recommendationList"
              style="width: 100%"
              stripe
            >
              <el-table-column label="学员" width="120">
                <template #default="{ row }">
                  {{ row.studentName }}
                </template>
              </el-table-column>
              <el-table-column label="推荐职位" width="150">
                <template #default="{ row }">
                  <strong>{{ row.positionName }}</strong>
                </template>
              </el-table-column>
              <el-table-column label="企业" width="150">
                <template #default="{ row }">
                  {{ row.companyName }}
                </template>
              </el-table-column>
              <el-table-column label="推荐理由" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.reason }}
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getRecommendationStatusType(row.status)" size="small">
                    {{ getRecommendationStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="推荐时间" width="160">
                <template #default="{ row }">
                  {{ formatDateTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="viewRecommendation(row)">
                    查看
                  </el-button>
                  <el-button link type="danger" size="small" @click="deleteRecommendation(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-if="recommendationTotal > 0"
              v-model:current-page="recommendationPagination.current"
              v-model:page-size="recommendationPagination.size"
              :total="recommendationTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadRecommendations"
              @current-change="loadRecommendations"
              style="margin-top: 20px; justify-content: flex-end"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加指导记录对话框 -->
    <el-dialog
      v-model="guidanceDialogVisible"
      title="添加指导记录"
      width="600px"
    >
      <el-form label-width="100px">
        <el-form-item label="学员" required>
          <el-select
            v-model="guidanceForm.studentId"
            placeholder="选择学员"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="student.realName"
              :value="student.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="指导教师" required>
          <el-select
            v-model="guidanceForm.teacherId"
            placeholder="选择指导教师"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.realName"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
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

    <!-- 推荐职位对话框 -->
    <el-dialog
      v-model="recommendDialogVisible"
      title="推荐职位"
      width="700px"
    >
      <el-form label-width="100px">
        <el-form-item label="学员" required>
          <el-select
            v-model="recommendForm.studentId"
            placeholder="选择学员"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="student.realName"
              :value="student.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职位" required>
          <el-select
            v-model="recommendForm.positionId"
            placeholder="选择职位"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="position in positionList"
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
        <el-button type="primary" @click="submitRecommend" :loading="recommendSubmitting">
          确认推荐
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="detailType === 'guidance' ? '指导记录详情' : '推荐职位详情'"
      width="600px"
    >
      <div v-if="detailType === 'guidance' && currentDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学员">{{ currentDetail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="指导类型">
            <el-tag :type="getGuidanceTypeColor(currentDetail.guidanceType)" size="small">
              {{ currentDetail.guidanceTypeName || currentDetail.guidanceType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="指导时间">{{ formatDateTime(currentDetail.guidanceDate) }}</el-descriptions-item>
          <el-descriptions-item label="指导地点">{{ currentDetail.location || '-' }}</el-descriptions-item>
          <el-descriptions-item label="指导教师">{{ currentDetail.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="指导内容">{{ currentDetail.content }}</el-descriptions-item>
          <el-descriptions-item label="后续跟进" v-if="currentDetail.nextAction">
            {{ currentDetail.nextAction }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-if="detailType === 'recommendation' && currentDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学员">{{ currentDetail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="推荐职位">{{ currentDetail.positionName }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ currentDetail.companyName }}</el-descriptions-item>
          <el-descriptions-item label="推荐状态">
            <el-tag :type="getRecommendationStatusType(currentDetail.status)" size="small">
              {{ getRecommendationStatusText(currentDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="推荐时间">{{ formatDateTime(currentDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="推荐理由" v-if="currentDetail.reason">
            {{ currentDetail.reason }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" v-if="currentDetail.remark">
            {{ currentDetail.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  getGuidancesByTeacherApi,
  createGuidanceApi,
  deleteGuidanceApi,
  getRecommendationsByTeacherApi,
  createRecommendationApi,
  deleteRecommendationApi
} from '@/api/guidance'
import { getStudentListApi } from '@/api/student'
import { getTeacherListApi } from '@/api/teacher'
import { getPositionListApi } from '@/api/position'

const authStore = useAuthStore()

const activeTab = ref('guidance')
const guidanceLoading = ref(false)
const recommendationLoading = ref(false)

const guidanceKeyword = ref('')
const recommendationKeyword = ref('')

const guidanceList = ref<any[]>([])
const recommendationList = ref<any[]>([])
const studentList = ref<any[]>([])
const teacherList = ref<any[]>([])
const positionList = ref<any[]>([])

const guidanceTotal = ref(0)
const recommendationTotal = ref(0)

const guidancePagination = reactive({
  current: 1,
  size: 10
})

const recommendationPagination = reactive({
  current: 1,
  size: 10
})

// 指导记录表单
const guidanceDialogVisible = ref(false)
const guidanceSubmitting = ref(false)
const guidanceForm = reactive({
  studentId: null,
  teacherId: null,
  guidanceType: '',
  content: '',
  nextAction: '',
  guidanceDate: '',
  location: ''
})

// 推荐职位表单
const recommendDialogVisible = ref(false)
const recommendSubmitting = ref(false)
const recommendForm = reactive({
  studentId: null,
  positionId: null,
  reason: '',
  remark: ''
})

// 详情对话框
const detailDialogVisible = ref(false)
const detailType = ref<'guidance' | 'recommendation'>('guidance')
const currentDetail = ref<any>(null)

const guidanceTypeOptions = [
  { label: '职业规划', value: 'career_planning' },
  { label: '简历指导', value: 'resume_guidance' },
  { label: '面试指导', value: 'interview_guidance' },
  { label: '技能提升', value: 'skill_improvement' },
  { label: '心理辅导', value: 'psychological_counseling' },
  { label: '其他', value: 'other' }
]

// 加载指导记录
const loadGuidances = async () => {
  try {
    guidanceLoading.value = true
    // 学院负责人查看所有指导记录（通过teacherId=0或者使用另一个接口）
    // 这里先用teacherId查询，实际可能需要调整API
    const teacherId = authStore.teacherId || 0
    const data: any = await getGuidancesByTeacherApi(teacherId, {
      current: guidancePagination.current,
      size: guidancePagination.size
    })
    guidanceList.value = data.records || []
    guidanceTotal.value = data.total || 0
  } catch (error) {
    console.error('加载指导记录失败', error)
    ElMessage.error('加载指导记录失败')
  } finally {
    guidanceLoading.value = false
  }
}

// 加载职位推荐
const loadRecommendations = async () => {
  try {
    recommendationLoading.value = true
    const teacherId = authStore.teacherId || 0
    const data: any = await getRecommendationsByTeacherApi(teacherId, {
      current: recommendationPagination.current,
      size: recommendationPagination.size
    })
    recommendationList.value = data.records || []
    recommendationTotal.value = data.total || 0
  } catch (error) {
    console.error('加载职位推荐失败', error)
    ElMessage.error('加载职位推荐失败')
  } finally {
    recommendationLoading.value = false
  }
}

// 加载学员列表
const loadStudents = async () => {
  try {
    const data: any = await getStudentListApi({ current: 1, size: 1000 })
    studentList.value = data.records || []
  } catch (error) {
    console.error('加载学员列表失败', error)
  }
}

// 加载教师列表
const loadTeachers = async () => {
  try {
    const data: any = await getTeacherListApi()
    teacherList.value = data || []
  } catch (error) {
    console.error('加载教师列表失败', error)
  }
}

// 加载职位列表
const loadPositions = async () => {
  try {
    positionList.value = await getPositionListApi()
  } catch (error) {
    console.error('加载职位列表失败', error)
  }
}

// 显示添加指导记录对话框
const showAddGuidanceDialog = () => {
  const now = new Date()
  guidanceForm.guidanceDate = now.toISOString().slice(0, 16)
  guidanceDialogVisible.value = true
}

// 提交指导记录
const submitGuidance = async () => {
  if (!guidanceForm.studentId) {
    ElMessage.warning('请选择学员')
    return
  }
  if (!guidanceForm.teacherId) {
    ElMessage.warning('请选择指导教师')
    return
  }
  if (!guidanceForm.guidanceType) {
    ElMessage.warning('请选择指导类型')
    return
  }
  if (!guidanceForm.content) {
    ElMessage.warning('请填写指导内容')
    return
  }

  try {
    guidanceSubmitting.value = true

    await createGuidanceApi({
      studentId: guidanceForm.studentId,
      teacherId: guidanceForm.teacherId,
      guidanceType: guidanceForm.guidanceType,
      content: guidanceForm.content,
      nextAction: guidanceForm.nextAction,
      guidanceDate: (guidanceForm.guidanceDate + ':00').replace('T', ' '),
      location: guidanceForm.location
    })

    ElMessage.success('添加成功')
    guidanceDialogVisible.value = false
    Object.assign(guidanceForm, {
      studentId: null,
      teacherId: null,
      guidanceType: '',
      content: '',
      nextAction: '',
      guidanceDate: '',
      location: ''
    })
    loadGuidances()
  } catch (error) {
    ElMessage.error('添加失败')
  } finally {
    guidanceSubmitting.value = false
  }
}

// 显示推荐职位对话框
const showAddRecommendDialog = () => {
  recommendDialogVisible.value = true
}

// 提交推荐
const submitRecommend = async () => {
  if (!recommendForm.studentId) {
    ElMessage.warning('请选择学员')
    return
  }
  if (!recommendForm.positionId) {
    ElMessage.warning('请选择职位')
    return
  }

  try {
    recommendSubmitting.value = true
    const teacherId = authStore.teacherId || 0

    await createRecommendationApi({
      studentId: recommendForm.studentId,
      positionId: recommendForm.positionId,
      teacherId: teacherId,
      reason: recommendForm.reason,
      remark: recommendForm.remark
    })

    ElMessage.success('推荐成功')
    recommendDialogVisible.value = false
    Object.assign(recommendForm, {
      studentId: null,
      positionId: null,
      reason: '',
      remark: ''
    })
    loadRecommendations()
  } catch (error) {
    ElMessage.error('推荐失败')
  } finally {
    recommendSubmitting.value = false
  }
}

// 查看指导详情
const viewGuidance = (row: any) => {
  detailType.value = 'guidance'
  currentDetail.value = row
  detailDialogVisible.value = true
}

// 查看推荐详情
const viewRecommendation = (row: any) => {
  detailType.value = 'recommendation'
  currentDetail.value = row
  detailDialogVisible.value = true
}

// 删除指导记录
const deleteGuidance = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条指导记录吗？', '提示', {
      type: 'warning'
    })
    await deleteGuidanceApi(id)
    ElMessage.success('删除成功')
    loadGuidances()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 删除推荐
const deleteRecommendation = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条推荐记录吗？', '提示', {
      type: 'warning'
    })
    await deleteRecommendationApi(id)
    ElMessage.success('删除成功')
    loadRecommendations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 获取指导类型颜色
const getGuidanceTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    career_planning: 'primary',
    resume_guidance: 'success',
    interview_guidance: 'warning',
    skill_improvement: 'info',
    psychological_counseling: 'danger',
    other: ''
  }
  return colorMap[type] || ''
}

// 获取推荐状态类型
const getRecommendationStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    pending: 'danger',
    viewed: 'warning',
    applied: 'success',
    rejected: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取推荐状态文本
const getRecommendationStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待查看',
    viewed: '已查看',
    applied: '已投递',
    rejected: '已拒绝'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadGuidances()
  loadRecommendations()
  loadStudents()
  loadTeachers()
  loadPositions()
})
</script>

<style lang="scss" scoped>
.guidance-management {
  padding: 20px;

  .header-card {
    margin-bottom: 20px;

    .header-content {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #303133;
      }

      .description {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .tabs-card {
    .tab-content {
      .toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
      }
    }
  }
}
</style>
