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
                    <el-option label="51-100人" value="51-100人" />
                    <el-option label="101-500人" value="101-500人" />
                    <el-option label="501-1000人" value="501-1000人" />
                    <el-option label="1000人以上" value="1000人以上" />
                    <el-option label="大型企业" value="大型企业" />
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

            <el-form-item label="所在城市" prop="city">
              <el-input v-model="formData.city" placeholder="请输入所在城市" />
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
      </el-col>

      <!-- 右侧：联系信息 -->
      <el-col :span="8">
        <!-- 联系信息 -->
        <el-card class="contact-card" shadow="never">
          <template #header>
            <span class="card-title">联系信息</span>
          </template>

          <el-form
            ref="contactFormRef"
            :model="formData"
            :rules="contactRules"
            label-width="100px"
            :disabled="!isEditing"
          >
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="formData.contactPerson" placeholder="请输入联系人姓名" />
            </el-form-item>
            <el-form-item label="联系职位" prop="contactPosition">
              <el-input v-model="formData.contactPosition" placeholder="请输入联系职位" />
            </el-form-item>
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="formData.contactEmail" placeholder="请输入联系邮箱" />
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

      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores'

// 企业信息
const companyInfo = ref<any>({})

// 表单
const formRef = ref<FormInstance>()
const contactFormRef = ref<FormInstance>()
const isEditing = ref(false)
const submitting = ref(false)

const formData = reactive({
  companyName: '',
  shortName: '',
  industry: '',
  scale: '',
  stage: '',
  website: '',
  city: '',
  address: '',
  description: '',
  benefits: '',
  contactPerson: '',
  contactPosition: '',
  contactPhone: '',
  contactEmail: ''
})

const formRules: FormRules = {
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业', trigger: 'change' }],
  scale: [{ required: true, message: '请选择规模', trigger: 'change' }]
}

const contactRules: FormRules = {
  contactPerson: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  contactEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 加载企业信息
const loadCompanyInfo = async () => {
  try {
    const authStore = useAuthStore()
    const companyId = authStore.companyId

    if (!companyId) {
      ElMessage.error('未找到企业信息')
      return
    }

    const response: any = await request.get(`/company/${companyId}`)
    companyInfo.value = response

    // 填充表单
    Object.assign(formData, {
      companyName: response.companyName,
      shortName: response.shortName,
      industry: response.industry,
      scale: response.scale,
      stage: response.stage,
      website: response.website,
      city: response.city,
      address: response.address,
      description: response.description,
      benefits: response.benefits,
      contactPerson: response.contactPerson,
      contactPosition: response.contactPosition,
      contactPhone: response.contactPhone,
      contactEmail: response.contactEmail
    })
  } catch (error) {
    console.error('加载企业信息失败', error)
  }
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
  if (!formRef.value || !contactFormRef.value) return

  // 同时验证两个表单
  const form1Valid = await formRef.value.validate().catch(() => false)
  const form2Valid = await contactFormRef.value.validate().catch(() => false)

  if (!form1Valid || !form2Valid) return

  submitting.value = true
  try {
    // 将id包含在提交数据中
    const submitData = {
      id: companyInfo.value.id,
      ...formData
    }
    await request.put(`/company/${companyInfo.value.id}`, submitData)
    ElMessage.success('保存成功')
    isEditing.value = false
    loadCompanyInfo()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    submitting.value = false
  }
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
}
</style>
