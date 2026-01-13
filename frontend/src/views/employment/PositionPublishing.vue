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
              <el-form-item label="职位名称" prop="name" class="form-item-group">
                <el-input
                  v-model="positionForm.name"
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

              <!-- 职位类别 -->
              <el-form-item label="职位类别" prop="category" class="form-item-group">
                <el-select
                  v-model="positionForm.category"
                  placeholder="请选择职位类别"
                  style="width: 100%;"
                >
                  <el-option label="技术研发" value="tech"></el-option>
                  <el-option label="产品设计" value="product"></el-option>
                  <el-option label="运营管理" value="operation"></el-option>
                  <el-option label="市场销售" value="marketing"></el-option>
                  <el-option label="职能支持" value="support"></el-option>
                </el-select>
              </el-form-item>

              <!-- 工作地点 -->
              <el-form-item label="工作地点" prop="location" class="form-item-group">
                <el-input
                  v-model="positionForm.location"
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
              <el-form-item label="薪资范围" prop="salaryRange" class="form-item-group">
                <el-input
                  v-model="positionForm.salaryRange"
                  placeholder="请输入薪资范围，如：10K-15K"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Coin /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <!-- 学历要求 -->
              <el-form-item label="学历要求" prop="education" class="form-item-group">
                <el-select
                  v-model="positionForm.education"
                  placeholder="请选择学历要求"
                  style="width: 100%;"
                >
                  <el-option label="不限" value="any"></el-option>
                  <el-option label="大专" value="college"></el-option>
                  <el-option label="本科" value="bachelor"></el-option>
                  <el-option label="硕士" value="master"></el-option>
                  <el-option label="博士" value="doctor"></el-option>
                </el-select>
              </el-form-item>

              <!-- 工作经验 -->
              <el-form-item label="工作经验" prop="experience" class="form-item-group">
                <el-select
                  v-model="positionForm.experience"
                  placeholder="请选择工作经验要求"
                  style="width: 100%;"
                >
                  <el-option label="不限" value="any"></el-option>
                  <el-option label="应届生" value="fresh"></el-option>
                  <el-option label="1年以下" value="1year"></el-option>
                  <el-option label="1-3年" value="1-3years"></el-option>
                  <el-option label="3-5年" value="3-5years"></el-option>
                  <el-option label="5年以上" value="5years+"></el-option>
                </el-select>
              </el-form-item>

              <!-- 所属企业 -->
              <el-form-item label="所属企业" prop="companyId" class="form-item-group">
                <el-select
                  v-model="positionForm.companyId"
                  placeholder="请选择所属企业"
                  filterable
                  style="width: 100%;"
                >
                  <el-option
                    v-for="company in companies"
                    :key="company.id"
                    :label="company.name"
                    :value="company.id"
                  >
                    <span style="float: left;">{{ company.name }}</span>
                    <span style="float: right; color: var(--text-muted); font-size: 13px;">{{ company.industry }}</span>
                  </el-option>
                </el-select>
              </el-form-item>

              <!-- 职位性质 -->
              <el-form-item label="职位性质" prop="jobType" class="form-item-group">
                <el-radio-group v-model="positionForm.jobType">
                  <el-radio value="fulltime">全职</el-radio>
                  <el-radio value="parttime">兼职</el-radio>
                  <el-radio value="intern">实习</el-radio>
                </el-radio-group>
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

            <!-- 福利待遇标签 -->
            <el-form-item label="福利待遇" prop="benefits" class="form-item-group">
              <div class="tag-input-container" @click="focusTagInput">
                <el-tag
                  v-for="tag in positionForm.benefits"
                  :key="tag"
                  closable
                  @close="removeBenefit(tag)"
                  style="margin: 0;"
                >
                  {{ tag }}
                </el-tag>
                <el-input
                  v-if="tagInputVisible"
                  ref="tagInputRef"
                  v-model="tagInputValue"
                  size="small"
                  style="width: 120px;"
                  @blur="handleTagInputConfirm"
                  @keyup.enter="handleTagInputConfirm"
                ></el-input>
                <el-button
                  v-else
                  size="small"
                  @click="showTagInput"
                >
                  + 添加福利
                </el-button>
              </div>
              <div style="margin-top: 0.5rem; color: var(--text-muted); font-size: 0.875rem;">
                常见福利：五险一金、带薪年假、员工旅游、节日福利、定期体检、餐补交通补等
              </div>
            </el-form-item>
          </div>
        </div>

        <!-- 发布设置卡片 -->
        <div class="form-card">
          <div class="form-card-header">
            <h2 class="form-card-title">
              <el-icon><Setting /></el-icon>
              发布设置
            </h2>
          </div>
          <div class="form-card-body">
            <div class="form-grid">
              <!-- 联系人 -->
              <el-form-item label="联系人" prop="contactName" class="form-item-group">
                <el-input
                  v-model="positionForm.contactName"
                  placeholder="请输入联系人姓名"
                  clearable
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <!-- 联系电话 -->
              <el-form-item label="联系电话" prop="contactPhone" class="form-item-group">
                <el-input
                  v-model="positionForm.contactPhone"
                  placeholder="请输入联系电话"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <!-- 联系邮箱 -->
              <el-form-item label="联系邮箱" prop="contactEmail" class="form-item-group" style="grid-column: 1 / -1;">
                <el-input
                  v-model="positionForm.contactEmail"
                  placeholder="请输入联系邮箱"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
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
            {{ previewData.name }}
          </h2>
          <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
            <el-tag type="primary">{{ previewData.location }}</el-tag>
            <el-tag type="success">{{ previewData.salaryRange }}</el-tag>
            <el-tag>{{ getEducationLabel(previewData.education) }}</el-tag>
            <el-tag>{{ getExperienceLabel(previewData.experience) }}</el-tag>
          </div>
        </div>

        <div style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1.125rem; font-weight: 600; color: var(--text-primary); margin-bottom: 0.75rem;">职位描述</h3>
          <div style="line-height: 1.8; color: var(--text-secondary);">{{ previewData.description }}</div>
        </div>

        <div style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1.125rem; font-weight: 600; color: var(--text-primary); margin-bottom: 0.75rem;">岗位要求</h3>
          <div style="line-height: 1.8; color: var(--text-secondary);">{{ previewData.requirements }}</div>
        </div>

        <div v-if="previewData.benefits && previewData.benefits.length > 0" style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1.125rem; font-weight: 600; color: var(--text-primary); margin-bottom: 0.75rem;">福利待遇</h3>
          <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
            <el-tag v-for="benefit in previewData.benefits" :key="benefit" type="success">{{ benefit }}</el-tag>
          </div>
        </div>

        <div style="padding: 1rem; background: oklch(0.98 0.005 240); border-radius: 0.5rem;">
          <div style="font-size: 0.875rem; color: var(--text-secondary);">
            <div><strong>联系人：</strong>{{ previewData.contactName }}</div>
            <div><strong>联系电话：</strong>{{ previewData.contactPhone }}</div>
            <div><strong>联系邮箱：</strong>{{ previewData.contactEmail }}</div>
          </div>
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
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { recruitmentApi } from '@/api'

// 表单引用
const positionFormRef = ref()
const tagInputRef = ref()

// 加载状态
const draftSaving = ref(false)
const publishing = ref(false)

// 标签输入
const tagInputVisible = ref(false)
const tagInputValue = ref('')

// 预览对话框
const previewDialogVisible = ref(false)
const previewData = ref(null)

// 职位表单数据
const positionForm = reactive({
  name: '',
  category: '',
  location: '',
  recruitCount: 1,
  salaryRange: '',
  education: '',
  experience: '',
  companyId: '',
  jobType: 'fulltime',
  description: '',
  requirements: '',
  benefits: [] as string[],
  contactName: '',
  contactPhone: '',
  contactEmail: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '职位名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择职位类别', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入工作地点', trigger: 'blur' }
  ],
  recruitCount: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' }
  ],
  salaryRange: [
    { required: true, message: '请输入薪资范围', trigger: 'blur' }
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
  jobType: [
    { required: true, message: '请选择职位性质', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入职位描述', trigger: 'blur' },
    { min: 20, message: '职位描述至少 20 个字符', trigger: 'blur' }
  ],
  requirements: [
    { required: true, message: '请输入岗位要求', trigger: 'blur' },
    { min: 20, message: '岗位要求至少 20 个字符', trigger: 'blur' }
  ],
  contactName: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 企业列表
const companies = ref([
  { id: 1, name: '阿里巴巴', industry: '互联网' },
  { id: 2, name: '腾讯科技', industry: '互联网' },
  { id: 3, name: '字节跳动', industry: '互联网' },
  { id: 4, name: '华为技术', industry: '通信技术' },
  { id: 5, name: '京东集团', industry: '电子商务' }
])

// 标签输入相关方法
const focusTagInput = () => {
  if (!tagInputVisible.value) {
    showTagInput()
  }
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagInputConfirm = () => {
  if (tagInputValue.value) {
    if (!positionForm.benefits.includes(tagInputValue.value)) {
      positionForm.benefits.push(tagInputValue.value)
    }
    tagInputValue.value = ''
  }
  tagInputVisible.value = false
}

const removeBenefit = (tag: string) => {
  const index = positionForm.benefits.indexOf(tag)
  if (index > -1) {
    positionForm.benefits.splice(index, 1)
  }
}

// 获取标签文本
const getEducationLabel = (value: string) => {
  const map: Record<string, string> = {
    'any': '不限',
    'college': '大专',
    'bachelor': '本科',
    'master': '硕士',
    'doctor': '博士'
  }
  return map[value] || value
}

const getExperienceLabel = (value: string) => {
  const map: Record<string, string> = {
    'any': '不限',
    'fresh': '应届生',
    '1year': '1年以下',
    '1-3years': '1-3年',
    '3-5years': '3-5年',
    '5years+': '5年以上'
  }
  return map[value] || value
}

// 保存草稿
const handleSaveDraft = async () => {
  try {
    draftSaving.value = true
    // TODO: 调用后端 API 保存草稿
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('草稿保存成功!')
  } catch (error) {
    ElMessage.error('保存草稿失败,请重试')
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

    // TODO: 调用后端 API 发布职位
    await new Promise(resolve => setTimeout(resolve, 1500))

    ElMessage.success('职位发布成功!')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败,请重试')
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
    positionForm.benefits = []
    ElMessage.info('已取消')
  } catch (error) {
    // 用户点击"继续编辑"
  }
}
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
