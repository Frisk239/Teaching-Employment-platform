<template>
  <div class="student-list">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="姓名/学号/手机号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="所属学校">
          <el-select
            v-model="searchForm.schoolId"
            placeholder="请选择学校"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="school in schoolList"
              :key="school.id"
              :label="school.schoolName"
              :value="school.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-input
            v-model="searchForm.grade"
            placeholder="如: 2024"
            clearable
            style="width: 120px"
          />
        </el-form-item>
        <el-form-item label="专业">
          <el-input
            v-model="searchForm.major"
            placeholder="请输入专业"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button
          v-permission="'teaching:student:add'"
          type="primary"
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增学生
        </el-button>
        <el-button
          v-permission="'teaching:student:import'"
          type="success"
          @click="handleImport"
        >
          <el-icon><Upload /></el-icon>
          Excel导入
        </el-button>
        <el-button
          v-permission="'teaching:student:export'"
          type="info"
          @click="downloadTemplate"
        >
          <el-icon><Download /></el-icon>
          下载模板
        </el-button>
        <el-button
          v-permission="'teaching:student:export'"
          type="warning"
          @click="handleExport"
        >
          <el-icon><Download /></el-icon>
          Excel导出
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :size="50" :src="row.avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="schoolName" label="学校" width="180" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="major" label="专业" min-width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="enrollmentDate" label="入学日期" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'teaching:student:edit'"
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-permission="'teaching:student:delete'"
              type="danger"
              size="small"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="username">
              <el-input v-model="formData.username" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学校" prop="schoolId">
              <el-select
                v-model="formData.schoolId"
                placeholder="请选择学校"
                style="width: 100%"
              >
                <el-option
                  v-for="school in schoolList"
                  :key="school.id"
                  :label="school.schoolName"
                  :value="school.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-input v-model="formData.grade" placeholder="如: 2024" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="formData.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="formData.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="入学日期" prop="enrollmentDate">
          <el-date-picker
            v-model="formData.enrollmentDate"
            type="date"
            placeholder="选择入学日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            :action="uploadAction"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- Excel导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="Excel导入学生"
      width="600px"
    >
      <el-alert
        title="导入说明"
        type="info"
        :closable="false"
        style="margin-bottom: 20px"
      >
        <p>1. 请下载模板文件,按照模板格式填写学生信息</p>
        <p>2. 支持.xlsx和.xls格式文件</p>
        <p>3. 单次最多导入1000条数据</p>
      </el-alert>

      <el-button type="primary" @click="downloadTemplate" style="margin-bottom: 20px">
        <el-icon><Download /></el-icon>
        下载模板
      </el-button>

      <el-divider />

      <el-upload
        ref="uploadRef"
        class="upload-demo"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :before-upload="beforeImport"
        :limit="1"
        accept=".xlsx,.xls"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处,或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件,且不超过 10MB
          </div>
        </template>
      </el-upload>

      <template #footer>
        <el-button @click="handleCancelImport">取消</el-button>
        <el-button
          type="primary"
          @click="handleConfirmImport"
          :loading="importing"
          :disabled="!selectedFile"
        >
          开始导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Download, Upload, UserFilled, UploadFilled } from '@element-plus/icons-vue'
import { getStudentPageApi, getStudentByIdApi, createStudentApi, updateStudentApi, deleteStudentApi, exportStudentsApi, downloadStudentTemplateApi } from '@/api/student'
import { getSchoolListApi } from '@/api/school'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  schoolId: undefined as number | undefined,
  grade: '',
  major: '',
  status: undefined as number | undefined
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([])

// 学校列表
const schoolList = ref<any[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑学生' : '新增学生'))
const formRef = ref<FormInstance>()
const formData = reactive({
  id: undefined as number | undefined,
  username: '',
  realName: '',
  schoolId: undefined as number | undefined,
  grade: '',
  major: '',
  className: '',
  phone: '',
  email: '',
  enrollmentDate: '',
  avatar: '',
  status: 1
})

// 导入对话框
const importDialogVisible = ref(false)

// 上传组件引用
const uploadRef = ref()

// 选中的文件
const selectedFile = ref<File | null>(null)

// 导入中状态
const importing = ref(false)

// 上传地址
const uploadAction = computed(() => {
  return import.meta.env.VITE_API_BASE_URL + '/student/import'
})

// 上传请求头(包含token)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

// 表单验证规则
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^[0-9]+$/, message: '学号只能包含数字', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  schoolId: [
    { required: true, message: '请选择学校', trigger: 'change' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' },
    { pattern: /^[0-9]{4}$/, message: '年级格式为4位数字,如: 2024', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 获取学校列表
const fetchSchoolList = async () => {
  try {
    const response = await getSchoolListApi()
    // http工具已经自动提取了data
    schoolList.value = response || []
  } catch (error) {
    console.error('获取学校列表失败:', error)
  }
}

// 获取学生列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      schoolId: searchForm.schoolId,
      grade: searchForm.grade || undefined,
      major: searchForm.major || undefined,
      status: searchForm.status
    }
    const response = await getStudentPageApi(params)
    // http工具已经自动提取了data,所以response直接就是分页数据
    tableData.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.schoolId = undefined
  searchForm.grade = ''
  searchForm.major = ''
  searchForm.status = undefined
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    username: '',
    realName: '',
    schoolId: undefined,
    grade: '',
    major: '',
    className: '',
    phone: '',
    email: '',
    enrollmentDate: '',
    avatar: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: any) => {
  try {
    const response = await getStudentByIdApi(row.id)
    // http工具已经自动提取了data
    // 映射字段: studentNo -> username
    const editData = {
      ...response,
      username: response.studentNo
    }
    Object.assign(formData, editData)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取学生信息失败:', error)
    ElMessage.error('获取学生信息失败')
  }
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该学生吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteStudentApi(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 状态变更
const handleStatusChange = async (row: any) => {
  try {
    await updateStudentApi(row)
    ElMessage.success('状态更新成功')
    fetchData()
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1 // 回滚
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = formData.id ? updateStudentApi : createStudentApi
        // 构建提交数据,移除undefined字段
        const submitData: any = { ...formData }
        await api(submitData)
        ElMessage.success(formData.id ? '更新成功' : '新增成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 头像上传成功
const handleAvatarSuccess = (response: any) => {
  if (response.code === 200) {
    formData.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 导入
const handleImport = () => {
  importDialogVisible.value = true
  selectedFile.value = null
  importing.value = false
}

// 文件选择变化
const handleFileChange = (file: any) => {
  selectedFile.value = file.raw
}

// 取消导入
const handleCancelImport = () => {
  importDialogVisible.value = false
  selectedFile.value = null
  importing.value = false
  // 清空上传文件列表
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 确认导入
const handleConfirmImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择要导入的文件')
    return
  }

  importing.value = true

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)

    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    const response = await fetch(import.meta.env.VITE_API_BASE_URL + '/student/import', {
      method: 'POST',
      headers: {
        Authorization: token ? `Bearer ${token}` : ''
      },
      body: formData
    })

    const result = await response.text()

    // 解析导入结果
    const lines = result.split('\n').filter(line => line.trim())
    const summaryLine = lines[0] || ''
    const errorLines = lines.slice(1).filter(line => line.includes('第'))

    // 提取成功和失败数量
    const successMatch = summaryLine.match(/成功：(\d+)条/)
    const failMatch = summaryLine.match(/失败：(\d+)条/)
    const successCount = successMatch ? parseInt(successMatch[1]) : 0
    const failCount = failMatch ? parseInt(failMatch[1]) : 0

    // 格式化错误信息
    const errorMessages = errorLines.map(line => {
      // 移除"第X行："前缀,只保留实际错误信息
      return line.replace(/第\d+行：/, '')
    }).filter(msg => msg.trim())

    // 构建HTML格式的结果消息
    let resultHtml = `<div style="text-align: left;">`

    // 添加汇总信息
    if (successCount > 0 || failCount > 0) {
      resultHtml += `
        <div style="margin-bottom: 15px;">
          <h3 style="margin: 0 0 10px 0;">导入完成</h3>
          <p style="margin: 5px 0; font-size: 14px;">
            <span style="color: #67c23a; font-weight: bold;">成功：${successCount} 条</span>
            ${failCount > 0 ? `<span style="color: #f56c6c; font-weight: bold; margin-left: 20px;">失败：${failCount} 条</span>` : ''}
          </p>
        </div>
      `
    }

    // 添加错误详情
    if (errorMessages.length > 0) {
      resultHtml += `
        <div style="margin-top: 15px;">
          <h4 style="margin: 0 0 10px 0; color: #f56c6c;">失败原因：</h4>
          <ul style="margin: 0; padding-left: 20px; font-size: 13px; line-height: 1.8;">
            ${errorMessages.map(msg => `<li>${msg}</li>`).join('')}
          </ul>
        </div>
      `
    }

    resultHtml += `</div>`

    // 显示导入结果
    ElMessageBox.alert(resultHtml, '导入结果', {
      confirmButtonText: '确定',
      dangerouslyUseHTMLString: true,
      type: failCount > 0 ? 'warning' : 'success',
      customClass: 'import-result-messagebox'
    })

    // 刷新数据并关闭对话框
    fetchData()
    importDialogVisible.value = false
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败,请检查文件格式是否正确')
  } finally {
    importing.value = false
    // 清空上传文件列表
    if (uploadRef.value) {
      uploadRef.value.clearFiles()
    }
    selectedFile.value = null
  }
}

// 下载模板
const downloadTemplate = async () => {
  try {
    const response = await downloadStudentTemplateApi() as any
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '学员导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('模板下载成功')
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('模板下载失败')
  }
}

// 导入前验证
const beforeImport = (file: File) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                  file.type === 'application/vnd.ms-excel'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

// 导入成功
const handleImportSuccess = (response: any) => {
  // 后端返回的是纯文本,不是JSON
  console.log('导入响应:', response)

  // 显示导入结果
  ElMessageBox.alert(response, '导入结果', {
    confirmButtonText: '确定',
    type: response.includes('失败') ? 'warning' : 'success'
  })

  // 刷新数据并关闭对话框
  fetchData()
  importDialogVisible.value = false
}

// 导入失败
const handleImportError = () => {
  ElMessage.error('导入失败,请检查文件格式是否正确')
}

// 导出
const handleExport = async () => {
  try {
    // 后端会导出所有学员数据，不需要传递参数
    const response = await exportStudentsApi({
      current: 1,
      size: 10,
      keyword: '',
      schoolId: undefined,
      grade: '',
      major: '',
      status: undefined
    }) as any
    // response.data 才是实际的 Blob 数据
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `学员数据_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchSchoolList()
  fetchData()
})
</script>

<style scoped lang="scss">
.student-list {
  padding: 20px;

  .search-form {
    margin-bottom: 20px;
  }

  .table-operations {
    margin-bottom: 20px;
  }

  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;
  }

  .avatar-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
  }
}

// 导入结果弹窗样式
:deep(.import-result-messagebox) {
  .el-message-box__content {
    padding: 20px 0;

    .el-message-box__message {
      p {
        margin: 5px 0;
      }

      h3 {
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 10px;
      }

      h4 {
        font-size: 15px;
        font-weight: 600;
        margin-bottom: 10px;
      }

      ul {
        list-style: disc;
      }
    }
  }
}
</style>
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
  }

  .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }

  .upload-demo {
    text-align: center;
  }
}
</style>
