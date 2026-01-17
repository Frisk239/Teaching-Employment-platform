<template>
  <div class="talent-pool-management">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCount || 0 }}</div>
              <div class="stat-label">总人才数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="28"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeCount || 0 }}</div>
              <div class="stat-label">激活人才</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              <el-icon :size="28"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.contactedCount || 0 }}</div>
              <div class="stat-label">已联系</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="28"><Flag /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.highPriorityCount || 0 }}</div>
              <div class="stat-label">高优先级</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            添加人才
          </el-button>
          <el-button :icon="Delete" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            批量删除
          </el-button>
        </div>

        <div class="toolbar-right">
          <el-select
            v-model="queryParams.category"
            placeholder="人才分类"
            clearable
            style="width: 130px; margin-right: 10px"
            @change="loadTalentPool"
          >
            <el-option label="前端" value="frontend" />
            <el-option label="后端" value="backend" />
            <el-option label="移动端" value="mobile" />
            <el-option label="产品" value="product" />
            <el-option label="设计" value="design" />
            <el-option label="运营" value="operation" />
            <el-option label="其他" value="other" />
          </el-select>

          <el-select
            v-model="queryParams.status"
            placeholder="状态"
            clearable
            style="width: 120px; margin-right: 10px"
            @change="loadTalentPool"
          >
            <el-option label="激活" value="active" />
            <el-option label="已联系" value="contacted" />
            <el-option label="已锁定" value="locked" />
          </el-select>

          <el-select
            v-model="queryParams.priority"
            placeholder="优先级"
            clearable
            style="width: 120px; margin-right: 10px"
            @change="loadTalentPool"
          >
            <el-option label="紧急" value="urgent" />
            <el-option label="高" value="high" />
            <el-option label="中" value="normal" />
            <el-option label="低" value="low" />
          </el-select>

          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索姓名、技能、标签"
            clearable
            style="width: 280px"
            :prefix-icon="Search"
            @clear="loadTalentPool"
            @keyup.enter="loadTalentPool"
          />

          <el-button :icon="Refresh" @click="loadTalentPool" />
        </div>
      </div>
    </el-card>

    <!-- 人才库列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="talentList"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column label="候选人" width="220">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 12px">
              <el-avatar :size="48" :src="row.studentAvatar">
                {{ row.studentName?.charAt(0) }}
              </el-avatar>
              <div>
                <div style="font-weight: 500; margin-bottom: 4px">{{ row.studentName }}</div>
                <div style="font-size: 12px; color: #909399">
                  {{ row.schoolName }} · {{ row.major }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="positionName" label="意向职位" width="140" />

        <el-table-column label="分类" width="100">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)" size="small">
              {{ getCategoryText(row.category) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="tags" label="标签" min-width="200">
          <template #default="{ row }">
            <el-tag
              v-for="(tag, index) in (row.tags || '').split(',').filter(t => t)"
              :key="index"
              size="small"
              style="margin-right: 4px; margin-bottom: 4px"
            >
              {{ tag }}
            </el-tag>
            <el-button
              v-if="!row.tags"
              link
              type="primary"
              size="small"
              @click="handleEditTags(row)"
            >
              添加标签
            </el-button>
          </template>
        </el-table-column>

        <el-table-column label="评分" width="140">
          <template #default="{ row }">
            <el-rate
              v-model="row.rating"
              :disabled="false"
              size="small"
              @change="handleRatingChange(row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="最后联系" width="120">
          <template #default="{ row }">
            <div v-if="row.lastContactDate" style="font-size: 12px">
              <div>{{ row.lastContactDate }}</div>
              <div style="color: #909399">{{ getContactMethodText(row.lastContactMethod) }}</div>
            </div>
            <span v-else style="color: #909399; font-size: 12px">未联系</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              详情
            </el-button>
            <el-button link type="primary" size="small" @click="handleEditTags(row)">
              标签
            </el-button>
            <el-button link type="primary" size="small" @click="handleContact(row)">
              联系
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadTalentPool"
        @current-change="loadTalentPool"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="学员" prop="studentId">
          <el-select
            v-model="formData.studentId"
            placeholder="选择学员"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="student in studentOptions"
              :key="student.id"
              :label="`${student.realName} - ${student.major}`"
              :value="student.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="意向职位" prop="positionName">
          <el-input v-model="formData.positionName" placeholder="如：前端工程师" />
        </el-form-item>

        <el-form-item label="人才分类" prop="category">
          <el-select v-model="formData.category" placeholder="选择分类" style="width: 100%">
            <el-option label="前端" value="frontend" />
            <el-option label="后端" value="backend" />
            <el-option label="移动端" value="mobile" />
            <el-option label="产品" value="product" />
            <el-option label="设计" value="design" />
            <el-option label="运营" value="operation" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="技能标签" prop="skillTags">
          <el-input
            v-model="formData.skillTags"
            type="textarea"
            :rows="3"
            placeholder="多个标签用逗号分隔，如：Vue,React,TypeScript"
          />
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="formData.priority">
            <el-radio label="urgent">紧急</el-radio>
            <el-radio label="high">高</el-radio>
            <el-radio label="normal">中</el-radio>
            <el-radio label="low">低</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 标签编辑对话框 -->
    <el-dialog
      v-model="tagsDialogVisible"
      title="编辑标签"
      width="500px"
    >
      <el-form label-width="80px">
        <el-form-item label="当前标签">
          <div style="margin-bottom: 12px">
            <el-tag
              v-for="(tag, index) in currentTags"
              :key="index"
              closable
              @close="handleRemoveTag(index)"
              style="margin-right: 8px; margin-bottom: 8px"
            >
              {{ tag }}
            </el-tag>
            <span v-if="currentTags.length === 0" style="color: #909399">暂无标签</span>
          </div>
        </el-form-item>

        <el-form-item label="添加标签">
          <el-input
            v-model="newTag"
            placeholder="输入标签名称"
            @keyup.enter="handleAddTag"
          >
            <template #append>
              <el-button @click="handleAddTag">添加</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="常用标签">
          <div>
            <el-tag
              v-for="tag in commonTags"
              :key="tag"
              @click="handleAddCommonTag(tag)"
              style="cursor: pointer; margin-right: 8px; margin-bottom: 8px"
            >
              + {{ tag }}
            </el-tag>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="tagsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTags">保存</el-button>
      </template>
    </el-dialog>

    <!-- 联系记录对话框 -->
    <el-dialog
      v-model="contactDialogVisible"
      title="标记联系记录"
      width="500px"
    >
      <el-form
        ref="contactFormRef"
        :model="contactForm"
        label-width="100px"
      >
        <el-form-item label="联系方式" prop="contactMethod">
          <el-radio-group v-model="contactForm.contactMethod">
            <el-radio label="email">邮件</el-radio>
            <el-radio label="phone">电话</el-radio>
            <el-radio label="message">站内信</el-radio>
            <el-radio label="interview">面试</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="下次联系" prop="nextContactDate">
          <el-date-picker
            v-model="contactForm.nextContactDate"
            type="date"
            placeholder="选择下次联系日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="contactDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveContact">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus, Refresh, Search, Delete, User, Star, ChatDotRound, Flag
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores'

const authStore = useAuthStore()

// 统计数据
const stats = ref({
  totalCount: 0,
  activeCount: 0,
  contactedCount: 0,
  highPriorityCount: 0
})

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  category: '',
  status: '',
  priority: '',
  keyword: ''
})

// 人才列表
const talentList = ref([])
const total = ref(0)
const loading = ref(false)

// 选中的ID
const selectedIds = ref<number[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加人才')
const tagsDialogVisible = ref(false)
const contactDialogVisible = ref(false)
const submitting = ref(false)

// 表单
const formRef = ref<FormInstance>()
const formData = reactive({
  studentId: null,
  positionName: '',
  category: '',
  skillTags: '',
  priority: 'normal',
  remark: ''
})

const formRules: FormRules = {
  studentId: [{ required: true, message: '请选择学员', trigger: 'change' }],
  category: [{ required: true, message: '请选择人才分类', trigger: 'change' }]
}

// 标签
const currentTalentId = ref<number | null>(null)
const currentTags = ref<string[]>([])
const newTag = ref('')
const commonTags = ['Vue', 'React', 'TypeScript', 'Node.js', 'Java', 'Python', '应届生', '有经验']

// 联系
const contactFormRef = ref<FormInstance>()
const contactForm = reactive({
  contactMethod: 'email',
  nextContactDate: ''
})

// 学员选项
const studentOptions = ref([])

// 加载人才库列表
const loadTalentPool = async () => {
  loading.value = true
  try {
    const response: any = await request.get('/talent-pool/page', {
      params: {
        ...queryParams,
        companyId: authStore.user?.companyId
      }
    })
    talentList.value = response.records || []
    total.value = response.total || 0
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 加载统计
const loadStats = async () => {
  try {
    const companyId = authStore.user?.companyId
    if (!companyId) return
    const response: any = await request.get(`/talent-pool/stats/company/${companyId}`)
    stats.value = response
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

// 加载学员选项
const loadStudentOptions = async () => {
  try {
    const response: any = await request.get('/student/list', {
      params: { size: 1000 }
    })
    studentOptions.value = response.records || []
  } catch (error) {
    console.error('加载学员失败', error)
  }
}

// 分类类型
const getCategoryType = (category: string) => {
  const typeMap: Record<string, string> = {
    frontend: 'primary',
    backend: 'success',
    mobile: 'warning',
    product: 'danger',
    design: 'info'
  }
  return typeMap[category] || ''
}

const getCategoryText = (category: string) => {
  const textMap: Record<string, string> = {
    frontend: '前端',
    backend: '后端',
    mobile: '移动端',
    product: '产品',
    design: '设计',
    operation: '运营',
    other: '其他'
  }
  return textMap[category] || category
}

// 优先级类型
const getPriorityType = (priority: string) => {
  const typeMap: Record<string, string> = {
    urgent: 'danger',
    high: 'warning',
    normal: 'info',
    low: ''
  }
  return typeMap[priority] || ''
}

const getPriorityText = (priority: string) => {
  const textMap: Record<string, string> = {
    urgent: '紧急',
    high: '高',
    normal: '中',
    low: '低'
  }
  return textMap[priority] || priority
}

// 状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    active: 'success',
    contacted: 'warning',
    locked: 'danger',
    inactive: 'info'
  }
  return typeMap[status] || ''
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    active: '激活',
    contacted: '已联系',
    locked: '已锁定',
    inactive: '未激活'
  }
  return textMap[status] || status
}

const getContactMethodText = (method: string) => {
  const textMap: Record<string, string> = {
    email: '邮件',
    phone: '电话',
    message: '站内信',
    interview: '面试'
  }
  return textMap[method] || method
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 添加
const handleAdd = () => {
  dialogTitle.value = '添加人才'
  dialogVisible.value = true
  loadStudentOptions()
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await request.post('/talent-pool', {
        ...formData,
        companyId: authStore.user?.companyId
      })
      ElMessage.success('添加成功')
      dialogVisible.value = false
      resetForm()
      loadTalentPool()
      loadStats()
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置
const resetForm = () => {
  formData.studentId = null
  formData.positionName = ''
  formData.category = ''
  formData.skillTags = ''
  formData.priority = 'normal'
  formData.remark = ''
  formRef.value?.resetFields()
}

// 查看
const handleView = (row: any) => {
  ElMessage.info('详情功能开发中')
}

// 编辑标签
const handleEditTags = (row: any) => {
  currentTalentId.value = row.id
  currentTags.value = row.tags ? row.tags.split(',').filter((t: string) => t) : []
  tagsDialogVisible.value = true
}

// 添加标签
const handleAddTag = () => {
  if (!newTag.value.trim()) return
  if (currentTags.value.includes(newTag.value.trim())) {
    ElMessage.warning('标签已存在')
    return
  }
  currentTags.value.push(newTag.value.trim())
  newTag.value = ''
}

// 移除标签
const handleRemoveTag = (index: number) => {
  currentTags.value.splice(index, 1)
}

// 添加常用标签
const handleAddCommonTag = (tag: string) => {
  if (currentTags.value.includes(tag)) {
    ElMessage.warning('标签已存在')
    return
  }
  currentTags.value.push(tag)
}

// 保存标签
const handleSaveTags = async () => {
  try {
    await request.put(`/talent-pool/${currentTalentId.value}/tags`, null, {
      params: { tags: currentTags.value.join(',') }
    })
    ElMessage.success('保存成功')
    tagsDialogVisible.value = false
    loadTalentPool()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  }
}

// 评分变化
const handleRatingChange = async (row: any) => {
  try {
    await request.put(`/talent-pool/${row.id}/rating`, null, {
      params: { rating: row.rating }
    })
    ElMessage.success('评分更新成功')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  }
}

// 联系
const handleContact = (row: any) => {
  currentTalentId.value = row.id
  contactForm.contactMethod = 'email'
  contactForm.nextContactDate = ''
  contactDialogVisible.value = true
}

// 保存联系
const handleSaveContact = async () => {
  try {
    await request.post(`/talent-pool/${currentTalentId.value}/mark-contacted`, null, {
      params: contactForm
    })
    ElMessage.success('标记成功')
    contactDialogVisible.value = false
    loadTalentPool()
    loadStats()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '标记失败')
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这个人才吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/talent-pool/${row.id}`)
    ElMessage.success('删除成功')
    loadTalentPool()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个人才吗？`, '提示', {
      type: 'warning'
    })
    await request.delete('/talent-pool/batch', {
      data: selectedIds.value
    })
    ElMessage.success('删除成功')
    loadTalentPool()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadTalentPool()
  loadStats()
})
</script>

<style scoped lang="scss">
.talent-pool-management {
  padding: 20px;

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
            line-height: 1;
            margin-bottom: 8px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .toolbar-card {
    margin-bottom: 20px;

    .toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .toolbar-left,
      .toolbar-right {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }
  }

  .table-card {
    :deep(.el-table) {
      .el-avatar {
        background-color: #ecf5ff;
        color: #409eff;
      }
    }
  }
}
</style>
