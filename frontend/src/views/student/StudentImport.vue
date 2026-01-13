<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">批量导入学生</h1>
    </div>

    <!-- 导入向导 -->
    <div class="import-wizard">
      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step title="下载模板" description="下载Excel模板文件"></el-step>
        <el-step title="上传文件" description="填写完成后上传"></el-step>
        <el-step title="数据预览" description="预览导入数据"></el-step>
        <el-step title="完成导入" description="确认并导入数据"></el-step>
      </el-steps>

      <!-- 步骤1: 下载模板 -->
      <div v-if="currentStep === 0" class="step-content">
        <el-card class="template-card">
          <template #header>
            <div class="card-header">
              <el-icon><Download /></el-icon>
              <span>下载导入模板</span>
            </div>
          </template>

          <div class="template-info">
            <p>请先下载Excel模板文件，按照模板格式填写学生信息。</p>
            <ul>
              <li>姓名：必填，2-20个字符</li>
              <li>手机号：必填，11位手机号码</li>
              <li>邮箱：必填，有效的邮箱地址</li>
              <li>班级：必填，选择系统中已存在的班级</li>
            </ul>
          </div>

          <div class="template-actions">
            <el-button type="primary" size="large" @click="downloadTemplate">
              <el-icon><Download /></el-icon>
              下载模板
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 步骤2: 上传文件 -->
      <div v-if="currentStep === 1" class="step-content">
        <el-card class="upload-card">
          <template #header>
            <div class="card-header">
              <el-icon><Upload /></el-icon>
              <span>上传填写好的文件</span>
            </div>
          </template>

          <el-upload
            ref="uploadRef"
            class="upload-area"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            accept=".xlsx,.xls"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只支持 .xlsx 或 .xls 格式的Excel文件，文件大小不超过10MB
              </div>
            </template>
          </el-upload>

          <div v-if="uploadedFile" class="uploaded-file">
            <el-icon><Document /></el-icon>
            <span>{{ uploadedFile.name }}</span>
            <el-button type="danger" link @click="removeFile">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 步骤3: 数据预览 -->
      <div v-if="currentStep === 2" class="step-content">
        <el-card class="preview-card">
          <template #header>
            <div class="card-header">
              <el-icon><View /></el-icon>
              <span>数据预览</span>
            </div>
          </template>

          <el-alert
            title="共读取到 {{ previewData.length }} 条数据，请确认信息无误"
            type="info"
            :closable="false"
            style="margin-bottom: 1rem;"
          ></el-alert>

          <el-table
            :data="previewData.slice(0, 10)"
            border
            max-height="400"
            style="width: 100%"
          >
            <el-table-column type="index" label="序号" width="60"></el-table-column>
            <el-table-column prop="name" label="姓名" width="120"></el-table-column>
            <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
            <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
            <el-table-column prop="className" label="班级" width="150"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.valid" type="success" size="small">有效</el-tag>
                <el-tag v-else type="danger" size="small">无效</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="error" label="错误信息" min-width="200">
              <template #default="{ row }">
                <span v-if="row.error" style="color: var(--danger);">{{ row.error }}</span>
                <span v-else style="color: var(--text-muted);">-</span>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="previewData.length > 10" style="margin-top: 1rem; text-align: center; color: var(--text-secondary);">
            仅显示前10条数据，导入时将处理全部数据
          </div>
        </el-card>
      </div>

      <!-- 步骤4: 完成导入 -->
      <div v-if="currentStep === 3" class="step-content">
        <el-card class="result-card">
          <template #header>
            <div class="card-header">
              <el-icon><Select /></el-icon>
              <span>导入结果</span>
            </div>
          </template>

          <div v-if="importResult" class="import-result">
            <el-result
              :icon="importResult.success ? 'success' : 'error'"
              :title="importResult.success ? '导入完成' : '导入失败'"
            >
              <template #sub-title>
                <div class="result-stats">
                  <div class="stat-item">
                    <div class="stat-value">{{ importResult.total }}</div>
                    <div class="stat-label">总数据</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value success">{{ importResult.successCount }}</div>
                    <div class="stat-label">成功导入</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value error">{{ importResult.failCount }}</div>
                    <div class="stat-label">导入失败</div>
                  </div>
                </div>
              </template>
              <template #extra>
                <el-button type="primary" @click="handleReset">重新导入</el-button>
                <el-button @click="goToList">查看学生列表</el-button>
              </template>
            </el-result>
          </div>
        </el-card>
      </div>

      <!-- 操作按钮 -->
      <div class="step-actions">
        <el-button v-if="currentStep > 0 && currentStep < 3" @click="prevStep">
          上一步
        </el-button>
        <el-button
          v-if="currentStep < 2"
          type="primary"
          @click="nextStep"
          :disabled="!canNextStep"
        >
          下一步
        </el-button>
        <el-button
          v-if="currentStep === 2"
          type="primary"
          @click="handleImport"
          :loading="importing"
        >
          {{ importing ? '导入中...' : '开始导入' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { UploadFile } from 'element-plus'

const router = useRouter()

// 当前步骤
const currentStep = ref(0)

// 上传文件
const uploadRef = ref()
const uploadedFile = ref<UploadFile | null>(null)

// 预览数据
const previewData = ref([])

// 导入结果
const importResult = ref(null)
const importing = ref(false)

// 计算属性
const canNextStep = computed(() => {
  switch (currentStep.value) {
    case 1:
      return uploadedFile.value !== null
    case 2:
      return previewData.value.some((item: any) => item.valid)
    default:
      return true
  }
})

// 方法
const downloadTemplate = () => {
  ElMessage.success('模板下载成功!')
  // TODO: 实现模板下载
  // window.location.href = '/templates/student_import_template.xlsx'
}

const handleFileChange = (file: UploadFile) => {
  uploadedFile.value = file

  // TODO: 读取Excel文件内容
  // const reader = new FileReader()
  // reader.onload = (e) => {
  //   const data = new Uint8Array(e.target.result as ArrayBuffer)
  //   const workbook = XLSX.read(data, { type: 'array' })
  //   const firstSheet = workbook.Sheets[workbook.SheetNames[0]]
  //   const jsonData = XLSX.utils.sheet_to_json(firstSheet)
  //   validateData(jsonData)
  // }
  // reader.readAsArrayBuffer(file.raw)

  // 模拟数据验证
  setTimeout(() => {
    previewData.value = [
      { name: '张三', phone: '13800138000', email: 'zhangsan@example.com', className: 'Java开发班', valid: true, error: '' },
      { name: '李四', phone: '13900139000', email: 'lisi@example.com', className: 'Vue.js前端班', valid: true, error: '' },
      { name: '王五', phone: '13700137000', email: 'invalid-email', className: 'Python班', valid: false, error: '邮箱格式错误' }
    ]
  }, 500)
}

const removeFile = () => {
  uploadedFile.value = null
  previewData.value = []
  uploadRef.value?.clearFiles()
}

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const handleImport = async () => {
  importing.value = true
  try {
    // TODO: 调用后端 API 导入数据
    // const validData = previewData.value.filter(item => item.valid)
    // await studentApi.importStudents(validData)

    await new Promise(resolve => setTimeout(resolve, 2000))

    const total = previewData.value.length
    const successCount = previewData.value.filter((item: any) => item.valid).length
    const failCount = total - successCount

    importResult.value = {
      success: true,
      total,
      successCount,
      failCount
    }

    currentStep.value = 3
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importing.value = false
  }
}

const handleReset = () => {
  currentStep.value = 0
  uploadedFile.value = null
  previewData.value = []
  importResult.value = null
  uploadRef.value?.clearFiles()
}

const goToList = () => {
  router.push('/student/list')
}
</script>

<style scoped lang="scss">
.page-header {
  margin-bottom: 2rem;

  .page-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0;
    color: var(--text-primary);
  }
}

.import-wizard {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: 2rem;
}

.step-content {
  margin-top: 2rem;
  min-height: 400px;
}

.template-card,
.upload-card,
.preview-card,
.result-card {
  .card-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-weight: 600;
  }
}

.template-info {
  padding: 1rem 0;

  p {
    margin-bottom: 1rem;
    color: var(--text-secondary);
  }

  ul {
    list-style: none;
    padding: 0;

    li {
      padding: 0.5rem 0;
      color: var(--text-secondary);
      position: relative;
      padding-left: 1.5rem;

      &:before {
        content: '•';
        position: absolute;
        left: 0.5rem;
        color: var(--primary);
        font-weight: 700;
      }
    }
  }
}

.template-actions {
  text-align: center;
  padding: 1rem 0;
}

.upload-area {
  margin: 1rem 0;

  :deep(.el-upload-dragger) {
    padding: 3rem;
  }
}

.uploaded-file {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: oklch(0.99 0.005 240);
  border-radius: var(--radius-md);
  margin-top: 1rem;

  span {
    flex: 1;
    font-weight: 500;
  }
}

.import-result {
  .result-stats {
    display: flex;
    justify-content: center;
    gap: 3rem;
    margin-top: 2rem;

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 2.5rem;
        font-weight: 700;
        margin-bottom: 0.5rem;

        &.success {
          color: var(--success);
        }

        &.error {
          color: var(--danger);
        }
      }

      .stat-label {
        color: var(--text-secondary);
        font-size: 0.875rem;
      }
    }
  }
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid var(--border);
}
</style>
