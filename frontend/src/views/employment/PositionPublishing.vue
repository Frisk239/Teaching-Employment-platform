<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">发布职位</h1>
      <div class="page-actions">
        <el-button @click="handleSaveDraft" :loading="draftSaving">
          <el-icon><Document /></el-icon>
          保存草稿
        </el-button>
        <el-button type="primary" @click="handlePreview">
          <el-icon><View /></el-icon>
          预览
        </el-button>
      </div>
    </div>

    <!-- 表单容器 -->
    <div class="form-container">
      <el-form :model="positionForm" :rules="formRules" ref="positionFormRef" label-position="top">
        <!-- 基本信息卡片 -->
        <div class="form-card">
          <div class="form-card-header">
            <h2 class="form-card-title">
              <el-icon><InfoFilled /></el-icon>
              基本信息
            </h2>
            <span class="required-badge">必填</span>
          </div>
          <div class="form-card-body">
            <div class="form-grid">
              <!-- 职位名称 -->
              <el-form-item label="职位名称" prop="positionName" class="form-item-group">
                <el-input
                  v-model="positionForm.positionName"
                  placeholder="请输入职位名称，如：Java开发工程师"
                  clearable
                  maxlength="50"
                  show-word-limit
                >
                  <template #prefix>
                    <el-icon><Briefcase /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <!-- 职位性质 -->
              <el-form-item label="职位性质" prop="positionType" class="form-item-group">
                <el-select
                  v-model="positionForm.positionType"
                  placeholder="请选择职位性质"
                  style="width: 100%;"
                >
                  <el-option label="全职" value="fulltime"></el-option>
                  <el-option label="兼职" value="parttime"></el-option>
                  <el-option label="实习" value="internship"></el-option>
                </el-select>
              </el-form-item>

              <!-- 工作地点 -->
              <el-form-item label="工作地点" prop="city" class="form-item-group">
                <el-input
                  v-model="positionForm.city"
                  placeholder="请输入工作地点，如：北京市朝阳区"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Location /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <!-- 招聘人数 -->
              <el-form-item label="招聘人数" prop="recruitCount" class="form-item-group">
                <el-input-number
                  v-model="positionForm.recruitCount"
                  :min="1"
                  :max="999"
                  :step="1"
                  style="width: 100%;"
                />
              </el-form-item>

              <!-- 薪资范围 -->
              <el-form-item label="最低薪资" prop="salaryMin" class="form-item-group">
                <el-input-number
                  v-model="positionForm.salaryMin"
                  :min="0"
                  :step="1000"
                  style="width: 100%;"
                />
              </el-form-item>

              <!-- 最高薪资 -->
              <el-form-item label="最高薪资" prop="salaryMax" class="form-item-group">
                <el-input-number
                  v-model="positionForm.salaryMax"
                  :min="0"
                  :step="1000"
                  style="width: 100%;"
                />
              </el-form-item>

              <!-- 学历要求 -->
              <el-form-item label="学历要求" prop="education" class="form-item-group">
                <el-select
                  v-model="positionForm.education"
                  placeholder="请选择学历要求"
                  style="width: 100%;"
                >
                  <el-option label="不限" value="unlimited"></el-option>
                  <el-option label="大专" value="junior_college"></el-option>
                  <el-option label="本科" value="bachelor"></el-option>
                  <el-option label="硕士" value="master"></el-option>
                  <el-option label="博士" value="doctor"></el-option>
                </el-select>
              </el-form-item>

              <!-- 工作经验 -->
              <el-form-item label="工作经验" prop="experience" class="form-item-group">
                <el-input
                  v-model="positionForm.experience"
                  placeholder="如：1-3年、3-5年、不限"
                  clearable
                />
              </el-form-item>

              <!-- 所属企业 -->
              <el-form-item label="所属企业" prop="companyId" class="form-item-group" style="grid-column: 1 / -1;">
                <el-select
                  v-model="positionForm.companyId"
                  placeholder="请选择所属企业"
                  filterable
                  style="width: 100%;"
                >
                  <el-option
                    v-for="company in companies"
                    :key="company.id"
                    :label="company.companyName"
                    :value="company.id"
                  >
                    <span style="float: left;">{{ company.companyName }}</span>
                    <span style="float: right; color: var(--text-muted); font-size: 13px;">{{ company.industry }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
          </div>
        </div>

        <!-- 职位详情卡片 -->
        <div class="form-card">
          <div class="form-card-header">
            <h2 class="form-card-title">
              <el-icon><Document /></el-icon>
              职位详情
            </h2>
          </div>
          <div class="form-card-body">
            <!-- 职位描述 -->
            <el-form-item label="职位描述" prop="description" class="form-item-group" style="margin-bottom: 1.5rem;">
              <el-input
                v-model="positionForm.description"
                type="textarea"
                :rows="6"
                placeholder="请详细描述职位职责、工作内容等信息..."
                maxlength="2000"
                show-word-limit
              ></el-input>
            </el-form-item>

            <!-- 岗位要求 -->
            <el-form-item label="岗位要求" prop="requirements" class="form-item-group" style="margin-bottom: 1.5rem;">
              <el-input
                v-model="positionForm.requirements"
                type="textarea"
                :rows="6"
                placeholder="请详细列出岗位所需的技能、能力、经验等要求..."
                maxlength="2000"
                show-word-limit
              ></el-input>
            </el-form-item>

            <!-- 技术栈 -->
            <el-form-item label="技术栈" prop="techStack" class="form-item-group">
              <el-input
                v-model="positionForm.techStack"
                type="textarea"
                :rows="3"
                placeholder="请列出职位所需的技术栈，如：Java, Spring Boot, MySQL, Redis等（用逗号分隔）"
                maxlength="500"
                show-word-limit
              ></el-input>
            </el-form-item>
          </div>
        </div>
      </el-form>

      <!-- 底部操作栏 -->
      <div class="form-footer">
        <el-button size="large" @click="handleCancel">
          取消
        </el-button>
        <el-button size="large" @click="handleSaveDraft" :loading="draftSaving">
          <el-icon><Document /></el-icon>
          保存草稿
        </el-button>
        <el-button size="large" @click="handlePreview">
          <el-icon><View /></el-icon>
          预览
        </el-button>
        <el-button type="primary" size="large" @click="handlePublish" :loading="publishing">
          <el-icon><Position /></el-icon>
          {{ publishing ? '发布中...' : '发布职位' }}
        </el-button>
      </div>
    </div>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="职位预览"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="previewData" style="padding: 1rem;">
        <div style="margin-bottom: 1.5rem;">
          <h2 style="font-size: 1.5rem; font-weight: 700; color: var(--text-primary); margin-bottom: 0.5rem;">
            {{ previewData.positionName }}
          </h2>
          <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
            <el-tag type="primary">{{ previewData.city }}</el-tag>
            <el-tag type="success">¥{{ previewData.salaryMin }}-{{ previewData.salaryMax }} / {{ previewData.salaryUnit === 'month' ? '月' : previewData.salaryUnit }}</el-tag>
            <el-tag>{{ getEducationLabel(previewData.education) }}</el-tag>
            <el-tag>{{ previewData.experience }}</el-tag>
          </div>
        </div>

        <div style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1.125rem; font-weight: 600; color: var(--text-primary); margin-bottom: 0.75rem;">职位描述</h3>
          <div style="line-height: 1.8; color: var(--text-secondary); white-space: pre-wrap;">{{ previewData.description }}</div>
        </div>

        <div style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1.125rem; font-weight: 600; color: var(--text-primary); margin-bottom: 0.75rem;">岗位要求</h3>
          <div style="line-height: 1.8; color: var(--text-secondary); white-space: pre-wrap;">{{ previewData.requirements }}</div>
        </div>

        <div v-if="previewData.techStack" style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1.125rem; font-weight: 600; color: var(--text-primary); margin-bottom: 0.75rem;">技术栈</h3>
          <div style="line-height: 1.8; color: var(--text-secondary);">{{ previewData.techStack }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handlePublishFromPreview">确认发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createPositionApi } from '@/api/position'
import { getCompanyListApi, type Company } from '@/api/company'

// 表单引用
const positionFormRef = ref()

// 加载状态
const draftSaving = ref(false)
const publishing = ref(false)

// 预览对话框
const previewDialogVisible = ref(false)
const previewData = ref(null)

// 企业列表
const companies = ref<Company[]>([])

// 职位表单数据
const positionForm = reactive({
  positionName: '',
  positionType: 'fulltime',
  city: '',
  recruitCount: 1,
  salaryMin: undefined as number | undefined,
  salaryMax: undefined as number | undefined,
  salaryUnit: 'month',
  education: '',
  experience: '',
  companyId: undefined as number | undefined,
  description: '',
  requirements: '',
  techStack: '',
  status: 'active'
})

// 表单验证规则
const formRules = {
  positionName: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '职位名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入工作地点', trigger: 'blur' }
  ],
  recruitCount: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' }
  ],
  salaryMin: [
    { required: true, message: '请输入最低薪资', trigger: 'blur' }
  ],
  salaryMax: [
    { required: true, message: '请输入最高薪资', trigger: 'blur' }
  ],
  education: [
    { required: true, message: '请选择学历要求', trigger: 'change' }
  ],
  experience: [
    { required: true, message: '请选择工作经验', trigger: 'change' }
  ],
  companyId: [
    { required: true, message: '请选择所属企业', trigger: 'change' }
  ],
  positionType: [
    { required: true, message: '请选择职位性质', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入职位描述', trigger: 'blur' },
    { min: 20, message: '职位描述至少 20 个字符', trigger: 'blur' }
  ],
  requirements: [
    { required: true, message: '请输入岗位要求', trigger: 'blur' },
    { min: 20, message: '岗位要求至少 20 个字符', trigger: 'blur' }
  ]
}

// 加载企业列表
const loadCompanies = async () => {
  try {
    const data: any = await getCompanyListApi({ status: 1 })
    companies.value = data || []
  } catch (error) {
    console.error('加载企业列表失败', error)
  }
}

// 获取标签文本
const getEducationLabel = (value: string) => {
  const map: Record<string, string> = {
    'unlimited': '不限',
    'junior_college': '大专',
    'bachelor': '本科',
    'master': '硕士',
    'doctor': '博士'
  }
  return map[value] || value
}

// 保存草稿
const handleSaveDraft = async () => {
  try {
    await positionFormRef.value?.validate()
    draftSaving.value = true

    // 设置为草稿状态
    const draftData = { ...positionForm, status: 'draft' }

    await createPositionApi(draftData)
    ElMessage.success('草稿保存成功!')

    // 重置表单
    positionFormRef.value?.resetFields()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('保存草稿失败: ' + (error.message || '未知错误'))
    }
  } finally {
    draftSaving.value = false
  }
}

// 预览
const handlePreview = async () => {
  try {
    await positionFormRef.value?.validate()
    previewData.value = { ...positionForm }
    previewDialogVisible.value = true
  } catch (error) {
    ElMessage.warning('请完善必填信息后再预览')
  }
}

// 发布职位
const handlePublish = async () => {
  try {
    await positionFormRef.value?.validate()

    await ElMessageBox.confirm(
      '确认发布该职位吗?发布后将对求职者可见。',
      '确认发布',
      {
        confirmButtonText: '确认发布',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    publishing.value = true

    // 调用后端 API 发布职位
    await createPositionApi(positionForm)

    ElMessage.success('职位发布成功!')

    // 重置表单
    positionFormRef.value?.resetFields()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败: ' + (error.message || '未知错误'))
    }
  } finally {
    publishing.value = false
  }
}

// 从预览发布
const handlePublishFromPreview = async () => {
  previewDialogVisible.value = false
  await handlePublish()
}

// 取消
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消吗?未保存的内容将丢失。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '继续编辑',
        type: 'warning'
      }
    )

    positionFormRef.value?.resetFields()
    ElMessage.info('已取消')
  } catch (error) {
    // 用户点击"继续编辑"
  }
}

// 页面加载时获取企业列表
onMounted(() => {
  loadCompanies()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;

  .page-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0;
    color: var(--text-primary);
  }

  .page-actions {
    display: flex;
    gap: 0.75rem;
  }
}

.form-container {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: 1.5rem;
}

.form-card {
  margin-bottom: 1.5rem;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  overflow: hidden;

  &:last-child {
    margin-bottom: 0;
  }

  .form-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    background: oklch(0.99 0.005 240);
    border-bottom: 1px solid var(--border);

    .form-card-title {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin: 0;
      font-size: 1.125rem;
      font-weight: 600;
      color: var(--text-primary);
    }

    .required-badge {
      font-size: 0.75rem;
      padding: 0.125rem 0.5rem;
      background: var(--danger);
      color: white;
      border-radius: 9999px;
    }
  }

  .form-card-body {
    padding: 1.5rem;
  }
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.tag-input-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  min-height: 42px;
  cursor: text;

  &:hover {
    border-color: var(--primary);
  }
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border);
  margin-top: 1.5rem;
}
</style>
