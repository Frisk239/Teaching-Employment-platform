<template>
  <div class="teaching-plan-management">
    <div class="page-header">
      <h1 class="page-title">教学计划管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleQuickCreate">
          <el-icon><Plus /></el-icon>
          快速创建计划
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 课程选择 -->
    <el-card shadow="never" class="course-selector">
      <el-form inline>
        <el-form-item label="选择课程">
          <el-select
            v-model="selectedCourseId"
            placeholder="请选择课程"
            filterable
            @change="handleCourseChange"
            style="width: 300px"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 教学计划列表 -->
    <el-card shadow="never" v-loading="loading">
      <el-empty v-if="!selectedCourseId" description="请先选择课程" />
      <el-empty v-else-if="plans.length === 0" description="暂无教学计划，点击上方按钮创建" />

      <el-table v-else :data="plans" stripe>
        <el-table-column prop="lessonNumber" label="课次" width="100">
          <template #default="{ row }">
            第{{ row.lessonNumber }}次课
          </template>
        </el-table-column>
        <el-table-column prop="weekNumber" label="周次" width="100">
          <template #default="{ row }">
            第{{ row.weekNumber }}周
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="教学内容" min-width="300" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 快速创建对话框 -->
    <el-dialog
      v-model="quickCreateDialogVisible"
      title="快速创建教学计划"
      width="500px"
    >
      <el-form :model="quickCreateForm" label-width="100px">
        <el-form-item label="总课次数">
          <el-input-number
            v-model="quickCreateForm.totalLessons"
            :min="1"
            :max="20"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="quickCreateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleQuickCreateConfirm">
          生成计划骨架
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑教学计划"
      width="600px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="课次序号">
          <el-input-number v-model="editForm.lessonNumber" :min="1" disabled />
        </el-form-item>
        <el-form-item label="周次">
          <el-input-number v-model="editForm.weekNumber" :min="1" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="editForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="教学内容">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入教学内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditConfirm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { teachingPlanApi, type TeachingPlan } from '@/api/teachingPlan'
import { getCoursesByTeacherApi } from '@/api/course'
import type { FormInstance } from 'element-plus'

const loading = ref(false)
const courses = ref<any[]>([])
const selectedCourseId = ref<number>()
const plans = ref<TeachingPlan[]>([])
const quickCreateDialogVisible = ref(false)
const editDialogVisible = ref(false)

const quickCreateForm = reactive({
  totalLessons: 12
})

const editForm = reactive<Partial<TeachingPlan>>({
  id: undefined,
  courseId: 0,
  lessonNumber: 1,
  weekNumber: 1,
  title: '',
  content: ''
})

// 获取教师课程列表
const fetchCourses = async () => {
  try {
    // TODO: 从authStore获取teacherId
    const teacherId = 1 // 临时硬编码
    const response = await getCoursesByTeacherApi(teacherId) as any
    courses.value = response || []
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  }
}

// 获取教学计划列表
const fetchPlans = async () => {
  if (!selectedCourseId.value) return

  loading.value = true
  try {
    const response = await teachingPlanApi.getList(selectedCourseId.value) as any
    plans.value = response || []
  } catch (error) {
    console.error('获取教学计划失败:', error)
    ElMessage.error('获取教学计划失败')
  } finally {
    loading.value = false
  }
}

// 课程切换
const handleCourseChange = () => {
  fetchPlans()
}

// 快速创建
const handleQuickCreate = () => {
  quickCreateForm.totalLessons = 12
  quickCreateDialogVisible.value = true
}

const handleQuickCreateConfirm = async () => {
  if (!selectedCourseId.value) {
    ElMessage.warning('请先选择课程')
    return
  }

  const newPlans: TeachingPlan[] = []
  for (let i = 1; i <= quickCreateForm.totalLessons; i++) {
    newPlans.push({
      courseId: selectedCourseId.value,
      lessonNumber: i,
      weekNumber: i,
      title: `第${i}次课`,
      content: ''
    })
  }

  try {
    await teachingPlanApi.batchSave(newPlans)
    ElMessage.success('创建成功')
    quickCreateDialogVisible.value = false
    fetchPlans()
  } catch (error: any) {
    console.error('创建失败:', error)
    ElMessage.error(error.message || '创建失败')
  }
}

// 编辑
const handleEdit = (row: TeachingPlan) => {
  Object.assign(editForm, row)
  editDialogVisible.value = true
}

const handleEditConfirm = async () => {
  try {
    if (editForm.id) {
      await teachingPlanApi.update(editForm as TeachingPlan)
      ElMessage.success('更新成功')
    } else {
      await teachingPlanApi.create(editForm as TeachingPlan)
      ElMessage.success('创建成功')
    }
    editDialogVisible.value = false
    fetchPlans()
  } catch (error: any) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  }
}

// 删除
const handleDelete = async (row: TeachingPlan) => {
  try {
    await ElMessageBox.confirm(`确定删除"第${row.lessonNumber}次课"的计划吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await teachingPlanApi.delete(row.id!)
    ElMessage.success('删除成功')
    fetchPlans()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 刷新
const handleRefresh = () => {
  fetchPlans()
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped lang="scss">
.teaching-plan-management {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }

  .course-selector {
    margin-bottom: 20px;
  }
}
</style>
