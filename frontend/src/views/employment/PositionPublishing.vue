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

            <!-- 技术栈要求 -->
            <el-form-item label="技术栈要求" class="form-item-group">
              <div class="tech-stack-editor">
                <!-- 技术栈模板选择 -->
                <div class="template-selector">
                  <el-icon magic="MagicStick" style="margin-right: 8px;"></el-icon>
                  <span style="margin-right: 12px;">快速应用模板：</span>
                  <el-select
                    v-model="selectedTemplateId"
                    placeholder="请选择技术栈模板"
                    style="width: 300px;"
                    @change="handleTemplateChange"
                    clearable
                  >
                    <el-option
                      v-for="template in techStackTemplates"
                      :key="template.id"
                      :label="template.templateName"
                      :value="template.id"
                    />
                  </el-select>
                </div>

                <!-- 技能维度编辑器和雷达图预览 -->
                <div v-if="capabilityDimensions.length > 0" class="dimension-section">
                  <div class="dimension-editor">
                    <h4 style="margin: 0 0 1rem 0; font-size: 0.875rem; font-weight: 600; color: var(--text-primary);">
                      <el-icon><Edit /></el-icon>
                      设置能力要求（0-{{ capabilityDimensions[0]?.max || 100 }}）
                    </h4>
                    <div class="dimension-list">
                      <div v-for="(dim, index) in capabilityDimensions" :key="index" class="dimension-item">
                        <div class="dimension-header">
                          <span class="dimension-name">{{ dim.name }}</span>
                          <el-input-number
                            v-model="dim.value"
                            :min="0"
                            :max="dim.max"
                            :step="5"
                            size="small"
                            @change="handleDimensionValueChange"
                          />
                        </div>
                        <el-slider
                          v-model="dim.value"
                          :min="0"
                          :max="dim.max"
                          :step="5"
                          show-stops
                          :show-tooltip="true"
                          @change="handleDimensionValueChange"
                        />
                      </div>
                    </div>
                  </div>

                  <div class="radar-preview">
                    <h4 style="margin: 0 0 1rem 0; font-size: 0.875rem; font-weight: 600; color: var(--text-primary);">
                      <el-icon><View /></el-icon>
                      能力雷达图预览
                    </h4>
                    <TechStackRadar
                      :data="radarData"
                      :series="radarSeries"
                      width="100%"
                      height="350px"
                    />
                  </div>
                </div>

                <!-- 无模板时的提示 -->
                <div v-else class="empty-template-tip">
                  <el-empty
                    description="请选择技术栈模板开始设置职位能力要求"
                    :image-size="100"
                  />
                </div>
              </div>
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
import { ref, reactive, nextTick, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createPositionApi } from '@/api/position'
import { getCompanyListApi, type Company } from '@/api/company'
import {
  getTechStackTemplateListApi,
  type TechStackTemplate,
  type TechStackDimension,
  type CapabilityRadarData,
  parseTemplateDimensions,
  stringifyCapabilityRadar,
  parseCapabilityRadar
} from '@/api/tech-stack'
import TechStackRadar from '@/components/TechStackRadar.vue'
import type { TechStackData, TechStackSeries } from '@/components/TechStackRadar.vue'

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

// 技术栈模板列表
const techStackTemplates = ref<TechStackTemplate[]>([])

// 选中的技术栈模板 ID
const selectedTemplateId = ref<number | undefined>(undefined)

// 能力维度数据
const capabilityDimensions = ref<TechStackDimension[]>([])

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
  capabilityRadar: undefined as string | undefined,
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

// 计算雷达图数据
const radarData = computed<TechStackData[]>(() => {
  return capabilityDimensions.value.map(dim => ({
    name: dim.name,
    max: dim.max,
    value: dim.value || 0
  }))
})

// 计算雷达图系列数据
const radarSeries = computed<TechStackSeries[]>(() => {
  if (capabilityDimensions.value.length === 0) return []

  return [{
    name: '能力要求',
    data: capabilityDimensions.value.map(dim => dim.value || 0)
  }]
})

// 加载技术栈模板列表
const loadTechStackTemplates = async () => {
  try {
    const data = await getTechStackTemplateListApi()
    techStackTemplates.value = data || []
  } catch (error) {
    console.error('加载技术栈模板失败', error)
  }
}

// 处理技术栈模板选择
const handleTemplateChange = (templateId: number) => {
  const template = techStackTemplates.value.find(t => t.id === templateId)
  if (template) {
    // 解析模板的维度数据
    capabilityDimensions.value = parseTemplateDimensions(template.dimensions)

    // 生成技术栈字符串（逗号分隔的维度名称）
    positionForm.techStack = capabilityDimensions.value
      .map(dim => dim.name)
      .join(', ')

    // 更新能力雷达图数据
    updateCapabilityRadar()
  }
}

// 更新能力雷达图数据
const updateCapabilityRadar = () => {
  if (capabilityDimensions.value.length > 0) {
    const radarData: CapabilityRadarData = {
      dimensions: capabilityDimensions.value.map(dim => ({
        name: dim.name,
        max: dim.max,
        value: dim.value || 50
      }))
    }
    positionForm.capabilityRadar = stringifyCapabilityRadar(radarData)
  }
}

// 处理技能维度值变化
const handleDimensionValueChange = () => {
  updateCapabilityRadar()
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

// 页面加载时获取企业列表和技术栈模板
onMounted(() => {
  loadCompanies()
  loadTechStackTemplates()
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

.tech-stack-editor {
  width: 100%;

  .template-selector {
    display: flex;
    align-items: center;
    padding: 1rem;
    background: oklch(0.98 0.005 240);
    border: 1px solid var(--border);
    border-radius: var(--radius-md);
    margin-bottom: 1.5rem;
  }

  .dimension-section {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
  }

  .dimension-editor {
    .dimension-list {
      display: flex;
      flex-direction: column;
      gap: 1.25rem;
    }

    .dimension-item {
      padding: 1rem;
      background: oklch(0.99 0.005 240);
      border: 1px solid var(--border);
      border-radius: var(--radius-md);
      transition: all 0.3s ease;

      &:hover {
        border-color: var(--primary);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      }

      .dimension-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.75rem;
      }

      .dimension-name {
        font-size: 0.875rem;
        font-weight: 500;
        color: var(--text-primary);
      }
    }
  }

  .radar-preview {
    display: flex;
    flex-direction: column;
    padding: 1rem;
    background: oklch(0.99 0.005 240);
    border: 1px solid var(--border);
    border-radius: var(--radius-md);
  }

  .empty-template-tip {
    padding: 2rem;
    text-align: center;
  }
}

@media (max-width: 1200px) {
  .tech-stack-editor {
    .dimension-section {
      grid-template-columns: 1fr;
    }
  }
}
</style>
