<template>
  <div class="student-profile">
    <el-row :gutter="20">
      <!-- 左侧：基本信息卡片 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" size="small" @click="editBasicInfo">编辑</el-button>
            </div>
          </template>
          <div class="user-info">
            <el-avatar :size="100" :src="userInfo.avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <h2>{{ userInfo.realName || userInfo.username }}</h2>
            <p class="student-number" v-if="userInfo.studentNumber">
              学号: {{ userInfo.studentNumber }}
            </p>
            <p class="user-email" v-if="userInfo.email">{{ userInfo.email }}</p>
            <p class="user-phone" v-if="userInfo.phone">{{ userInfo.phone }}</p>
          </div>
        </el-card>

        <!-- 简历上传卡片 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <span>个人简历</span>
          </template>
          <div class="resume-upload">
            <el-upload
              class="upload-demo"
              :action="uploadAction"
              :headers="uploadHeaders"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              :show-file-list="false"
              accept=".pdf,.doc,.docx"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon> 上传简历
              </el-button>
            </el-upload>
            <div class="resume-info" v-if="userInfo.resumeUrl">
              <el-tag type="success" closable @close="handleRemoveResume">
                已上传简历
              </el-tag>
              <el-button type="text" @click="viewResume">查看</el-button>
            </div>
            <div class="upload-tip">支持 PDF、Word 格式，最大 5MB</div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：教育经历和实习经历 -->
      <el-col :span="16">
        <!-- 教育经历 -->
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>教育经历</span>
              <el-button type="primary" size="small" @click="addEducation">
                <el-icon><Plus /></el-icon> 添加
              </el-button>
            </div>
          </template>
          <el-timeline v-if="educationList.length > 0">
            <el-timeline-item
              v-for="edu in educationList"
              :key="edu.id"
              :timestamp="`${edu.startDate} ~ ${edu.endDate || '至今'}`"
              placement="top"
            >
              <el-card>
                <div class="timeline-item-header">
                  <h4>{{ edu.schoolName }} - {{ edu.major }}</h4>
                  <div class="actions">
                    <el-button type="primary" link size="small" @click="editEducation(edu)">
                      编辑
                    </el-button>
                    <el-button type="danger" link size="small" @click="deleteEducation(edu.id!)">
                      删除
                    </el-button>
                  </div>
                </div>
                <el-tag size="small">{{ edu.degree }}</el-tag>
                <p class="description" v-if="edu.description">{{ edu.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无教育经历" />
        </el-card>

        <!-- 实习经历 -->
        <el-card shadow="never" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>实习经历</span>
              <el-button type="primary" size="small" @click="addInternship">
                <el-icon><Plus /></el-icon> 添加
              </el-button>
            </div>
          </template>
          <el-timeline v-if="internshipList.length > 0">
            <el-timeline-item
              v-for="intern in internshipList"
              :key="intern.id"
              :timestamp="`${intern.startDate} ~ ${intern.endDate || '至今'}`"
              placement="top"
            >
              <el-card>
                <div class="timeline-item-header">
                  <h4>{{ intern.companyName }} - {{ intern.position }}</h4>
                  <div class="actions">
                    <el-button type="primary" link size="small" @click="editInternship(intern)">
                      编辑
                    </el-button>
                    <el-button type="danger" link size="small" @click="deleteInternship(intern.id!)">
                      删除
                    </el-button>
                  </div>
                </div>
                <el-tag size="small" v-if="intern.department">{{ intern.department }}</el-tag>
                <p class="description" v-if="intern.description">{{ intern.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无实习经历" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 基本信息编辑对话框 -->
    <el-dialog v-model="basicInfoVisible" title="编辑基本信息" width="600px">
      <el-form :model="basicForm" label-width="100px">
        <el-form-item label="学号">
          <el-input v-model="basicForm.studentNumber" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="basicForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="basicForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="basicForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="basicInfoVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBasicInfo">保存</el-button>
      </template>
    </el-dialog>

    <!-- 教育经历编辑对话框 -->
    <el-dialog v-model="educationVisible" :title="educationTitle" width="600px">
      <el-form :model="educationForm" label-width="100px">
        <el-form-item label="学校名称" required>
          <el-input v-model="educationForm.schoolName" placeholder="请输入学校名称" />
        </el-form-item>
        <el-form-item label="专业" required>
          <el-input v-model="educationForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="学历" required>
          <el-select v-model="educationForm.degree" placeholder="请选择学历">
            <el-option label="专科" value="专科" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker
            v-model="educationForm.startDate"
            type="date"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="educationForm.endDate"
            type="date"
            placeholder="选择结束时间（空表示至今）"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="educationForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="educationVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEducation">保存</el-button>
      </template>
    </el-dialog>

    <!-- 实习经历编辑对话框 -->
    <el-dialog v-model="internshipVisible" :title="internshipTitle" width="600px">
      <el-form :model="internshipForm" label-width="100px">
        <el-form-item label="公司名称" required>
          <el-input v-model="internshipForm.companyName" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="职位" required>
          <el-input v-model="internshipForm.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="internshipForm.department" placeholder="请输入部门" />
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker
            v-model="internshipForm.startDate"
            type="date"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="internshipForm.endDate"
            type="date"
            placeholder="选择结束时间（空表示至今）"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="工作描述">
          <el-input
            v-model="internshipForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入工作描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="internshipVisible = false">取消</el-button>
        <el-button type="primary" @click="saveInternship">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Plus, Upload } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import type { User } from '@/api/types'
import {
  getEducationByUserIdApi,
  createEducationApi,
  updateEducationApi,
  deleteEducationApi,
  type Education,
} from '@/api/education'
import {
  getInternshipByUserIdApi,
  createInternshipApi,
  updateInternshipApi,
  deleteInternshipApi,
  type Internship,
} from '@/api/internship'

const authStore = useAuthStore()

// 用户信息
const userInfo = ref<User>({})

// 教育经历列表
const educationList = ref<Education[]>([])
const educationVisible = ref(false)
const educationTitle = computed(() => (educationForm.id ? '编辑教育经历' : '添加教育经历'))
const educationForm = reactive<Education>({
  schoolName: '',
  major: '',
  degree: '',
  startDate: '',
  endDate: '',
  description: '',
})

// 实习经历列表
const internshipList = ref<Internship[]>([])
const internshipVisible = ref(false)
const internshipTitle = computed(() => (internshipForm.id ? '编辑实习经历' : '添加实习经历'))
const internshipForm = reactive<Internship>({
  companyName: '',
  position: '',
  department: '',
  startDate: '',
  endDate: '',
  description: '',
})

// 基本信息编辑
const basicInfoVisible = ref(false)
const basicForm = reactive({
  studentNumber: '',
  realName: '',
  phone: '',
  email: '',
})

// 文件上传配置
const uploadAction = import.meta.env.VITE_API_BASE_URL + '/api/file/upload'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`,
}))

// 初始化
onMounted(async () => {
  await loadUserInfo()
  await loadEducationList()
  await loadInternshipList()
})

// 加载用户信息
async function loadUserInfo() {
  try {
    const res = await authApi.getCurrentUser()
    userInfo.value = res.data.user
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

// 加载教育经历列表
async function loadEducationList() {
  try {
    const res = await getEducationByUserIdApi(userInfo.value.id!)
    educationList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载教育经历失败')
  }
}

// 加载实习经历列表
async function loadInternshipList() {
  try {
    const res = await getInternshipByUserIdApi(userInfo.value.id!)
    internshipList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载实习经历失败')
  }
}

// 编辑基本信息
function editBasicInfo() {
  basicForm.studentNumber = userInfo.value.studentNumber || ''
  basicForm.realName = userInfo.value.realName || ''
  basicForm.phone = userInfo.value.phone || ''
  basicForm.email = userInfo.value.email || ''
  basicInfoVisible.value = true
}

// 保存基本信息
async function saveBasicInfo() {
  try {
    await authApi.updateStudentProfile(basicForm)
    ElMessage.success('保存成功')
    basicInfoVisible.value = false
    await loadUserInfo()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

// 添加教育经历
function addEducation() {
  Object.assign(educationForm, {
    id: undefined,
    schoolName: '',
    major: '',
    degree: '',
    startDate: '',
    endDate: '',
    description: '',
  })
  educationVisible.value = true
}

// 编辑教育经历
function editEducation(edu: Education) {
  Object.assign(educationForm, edu)
  educationVisible.value = true
}

// 保存教育经历
async function saveEducation() {
  if (!educationForm.schoolName || !educationForm.major || !educationForm.degree || !educationForm.startDate) {
    ElMessage.warning('请填写必填项')
    return
  }

  try {
    if (educationForm.id) {
      await updateEducationApi(educationForm)
      ElMessage.success('更新成功')
    } else {
      educationForm.userId = userInfo.value.id
      await createEducationApi(educationForm)
      ElMessage.success('添加成功')
    }
    educationVisible.value = false
    await loadEducationList()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

// 删除教育经历
async function deleteEducation(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除这条教育经历吗？', '提示', {
      type: 'warning',
    })
    await deleteEducationApi(id)
    ElMessage.success('删除成功')
    await loadEducationList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 添加实习经历
function addInternship() {
  Object.assign(internshipForm, {
    id: undefined,
    companyName: '',
    position: '',
    department: '',
    startDate: '',
    endDate: '',
    description: '',
  })
  internshipVisible.value = true
}

// 编辑实习经历
function editInternship(intern: Internship) {
  Object.assign(internshipForm, intern)
  internshipVisible.value = true
}

// 保存实习经历
async function saveInternship() {
  if (!internshipForm.companyName || !internshipForm.position || !internshipForm.startDate) {
    ElMessage.warning('请填写必填项')
    return
  }

  try {
    if (internshipForm.id) {
      await updateInternshipApi(internshipForm)
      ElMessage.success('更新成功')
    } else {
      internshipForm.userId = userInfo.value.id
      await createInternshipApi(internshipForm)
      ElMessage.success('添加成功')
    }
    internshipVisible.value = false
    await loadInternshipList()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

// 删除实习经历
async function deleteInternship(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除这条实习经历吗？', '提示', {
      type: 'warning',
    })
    await deleteInternshipApi(id)
    ElMessage.success('删除成功')
    await loadInternshipList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 文件上传前校验
function beforeUpload(file: File) {
  const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  const isValidSize = file.size <= 5 * 1024 * 1024

  if (!isValidType) {
    ElMessage.error('只支持上传 PDF、Word 格式的文件')
    return false
  }
  if (!isValidSize) {
    ElMessage.error('文件大小不能超过 5MB')
    return false
  }
  return true
}

// 上传成功回调
function handleUploadSuccess(response: any) {
  if (response.code === 200) {
    ElMessage.success('简历上传成功')
    userInfo.value.resumeUrl = response.data
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 查看简历
function viewResume() {
  if (userInfo.value.resumeUrl) {
    window.open(userInfo.value.resumeUrl, '_blank')
  }
}

// 删除简历
async function handleRemoveResume() {
  try {
    await ElMessageBox.confirm('确定要删除简历吗？', '提示', {
      type: 'warning',
    })
    await authApi.updateStudentProfile({ resumeUrl: '' })
    ElMessage.success('删除成功')
    userInfo.value.resumeUrl = ''
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}
</script>

<style scoped lang="scss">
.student-profile {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .user-info {
    text-align: center;

    .el-avatar {
      margin-bottom: 15px;
    }

    h2 {
      margin: 10px 0;
      font-size: 24px;
    }

    p {
      margin: 8px 0;
      color: var(--el-text-color-secondary);
    }
  }

  .resume-upload {
    text-align: center;

    .resume-info {
      margin-top: 15px;
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 10px;
    }

    .upload-tip {
      margin-top: 10px;
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
  }

  .timeline-item-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    h4 {
      margin: 0 0 8px 0;
      font-size: 16px;
    }

    .actions {
      display: flex;
      gap: 8px;
    }
  }

  .description {
    margin: 8px 0 0 0;
    color: var(--el-text-color-secondary);
    white-space: pre-wrap;
  }
}
</style>
