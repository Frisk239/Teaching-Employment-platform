<template>
  <div class="classroom-list">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="教室名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入教室名称"
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
              :label="school.name"
              :value="school.id"
            />
          </el-select>
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
          v-permission="'teaching:classroom:add'"
          type="primary"
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增教室
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
        <el-table-column prop="name" label="教室名称" min-width="150" />
        <el-table-column prop="schoolName" label="所属学校" width="180" />
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="capacity" label="容量" width="80">
          <template #default="{ row }">
            {{ row.capacity }} 人
          </template>
        </el-table-column>
        <el-table-column prop="equipment" label="设备" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'teaching:classroom:edit'"
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-permission="'teaching:classroom:delete'"
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="教室名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入教室名称" />
        </el-form-item>
        <el-form-item label="所属学校" prop="schoolId">
          <el-select
            v-model="formData.schoolId"
            placeholder="请选择学校"
            style="width: 100%"
          >
            <el-option
              v-for="school in schoolList"
              :key="school.id"
              :label="school.name"
              :value="school.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋" prop="building">
          <el-input v-model="formData.building" placeholder="如: A栋" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="formData.floor" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="formData.roomNumber" placeholder="如: 101" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="formData.capacity" :min="1" :max="500" style="width: 100%" />
          <span style="margin-left: 10px">人</span>
        </el-form-item>
        <el-form-item label="设备" prop="equipment">
          <el-input
            v-model="formData.equipment"
            type="textarea"
            :rows="3"
            placeholder="请输入教室设备信息,如: 投影仪、电脑、音响等"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getClassroomPageApi, getClassroomByIdApi, createClassroomApi, updateClassroomApi, deleteClassroomApi } from '@/api/classroom'
import { getSchoolListApi } from '@/api/school'
import type { School } from '@/api/school'

// 搜索表单
const searchForm = reactive({
  name: '',
  schoolId: undefined as number | undefined,
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
const tableData = ref([])

// 学校列表
const schoolList = ref<School[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑教室' : '新增教室'))
const formRef = ref<FormInstance>()
const formData = reactive({
  id: undefined,
  name: '',
  schoolId: undefined,
  building: '',
  floor: 1,
  roomNumber: '',
  capacity: 50,
  equipment: '',
  remark: '',
  status: 1
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入教室名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  schoolId: [
    { required: true, message: '请选择学校', trigger: 'change' }
  ],
  building: [
    { required: true, message: '请输入楼栋', trigger: 'blur' }
  ],
  floor: [
    { required: true, message: '请输入楼层', trigger: 'blur' }
  ],
  roomNumber: [
    { required: true, message: '请输入房间号', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入容量', trigger: 'blur' }
  ]
}

// 获取学校列表
const fetchSchoolList = async () => {
  try {
    const response = await getSchoolListApi()
    if (response.code === 200) {
      schoolList.value = response.data
    }
  } catch (error) {
    console.error('获取学校列表失败:', error)
  }
}

// 获取教室列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      name: searchForm.name || undefined,
      schoolId: searchForm.schoolId,
      status: searchForm.status
    }
    const response = await getClassroomPageApi(params)
    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取教室列表失败')
    }
  } catch (error) {
    console.error('获取教室列表失败:', error)
    ElMessage.error('获取教室列表失败')
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
  searchForm.name = ''
  searchForm.schoolId = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    name: '',
    schoolId: undefined,
    building: '',
    floor: 1,
    roomNumber: '',
    capacity: 50,
    equipment: '',
    remark: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: any) => {
  try {
    const response = await getClassroomByIdApi(row.id)
    if (response.code === 200) {
      Object.assign(formData, response.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取教室信息失败')
    }
  } catch (error) {
    console.error('获取教室信息失败:', error)
    ElMessage.error('获取教室信息失败')
  }
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该教室吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await deleteClassroomApi(row.id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          fetchData()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = formData.id ? updateClassroomApi : createClassroomApi
        const response = await api(formData)
        if (response.code === 200) {
          ElMessage.success(formData.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
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

// 页面加载时获取数据
onMounted(() => {
  fetchSchoolList()
  fetchData()
})
</script>

<style scoped lang="scss">
.classroom-list {
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
}
</style>
