<!--
  QuestionBankSelector 组件使用示例

  这个组件用于从题库中选择题目并添加到试卷中。

  使用方法：
  1. 在父组件中导入组件
  2. 使用 v-model 绑定 visible 状态
  3. 监听 confirm 和 close 事件
-->

<template>
  <div class="example-container">
    <el-card>
      <h2>QuestionBankSelector 组件使用示例</h2>

      <!-- 打开选择器按钮 -->
      <el-button type="primary" @click="openSelector">
        <el-icon><Plus /></el-icon>
        从题库选择题目
      </el-button>

      <!-- 显示已选择的题目 -->
      <div v-if="selectedQuestions.length > 0" class="selected-questions">
        <h3>已选择的题目：</h3>
        <ul>
          <li v-for="question in selectedQuestions" :key="question.id">
            {{ question.questionText }}
          </li>
        </ul>
      </div>

      <!-- 题库选择器组件 -->
      <QuestionBankSelector
        :visible="selectorVisible"
        :exam-id="currentExamId"
        @confirm="handleConfirm"
        @close="handleClose"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import QuestionBankSelector from '@/components/QuestionBankSelector.vue'
import type { QuestionBank } from '@/api/question'

// 选择器显示状态
const selectorVisible = ref(false)

// 当前试卷ID（可选，用于获取已有题目）
const currentExamId = ref<number>()

// 已选择的题目列表
const selectedQuestions = ref<QuestionBank[]>([])

// 打开选择器
const openSelector = () => {
  selectorVisible.value = true
}

// 确认选择
const handleConfirm = async (questionIds: number[]) => {
  console.log('选中的题目ID:', questionIds)

  // 在这里调用后端API将题目添加到试卷
  // 例如：await addQuestionsToExam(currentExamId.value, questionIds)

  ElMessage.success(`成功添加 ${questionIds.length} 道题目`)

  // 更新已选择的题目列表（可选）
  // 这里需要根据 questionIds 查询题目详情
}

// 关闭选择器
const handleClose = () => {
  selectorVisible.value = false
}
</script>

<style scoped lang="scss">
.example-container {
  padding: 20px;

  .selected-questions {
    margin-top: 20px;
    padding: 16px;
    background: var(--muted);
    border-radius: 8px;

    h3 {
      margin: 0 0 12px 0;
      font-size: 16px;
      font-weight: 600;
    }

    ul {
      margin: 0;
      padding-left: 20px;

      li {
        margin: 8px 0;
        line-height: 1.6;
      }
    }
  }
}
</style>

<!--
  在 ExamManagement.vue 中集成示例：

  1. 在模板中添加组件：
     <QuestionBankSelector
       :visible="questionBankSelectorVisible"
       :exam-id="formData.id"
       @confirm="handleAddQuestionsFromBank"
       @close="questionBankSelectorVisible = false"
     />

  2. 在 script 中添加状态和方法：
     const questionBankSelectorVisible = ref(false)

     const handleShowQuestionBank = () => {
       questionBankSelectorVisible.value = true
     }

     const handleAddQuestionsFromBank = async (questionIds: number[]) => {
       try {
         // 调用后端API批量添加题目
         await addQuestionsToExamApi(formData.id, questionIds)
         ElMessage.success('添加成功')
         // 重新加载试卷题目列表
         await loadExamQuestions()
       } catch (error) {
         console.error('添加题目失败:', error)
         ElMessage.error('添加题目失败')
       }
     }

  3. 修改"从题库添加"按钮的点击事件：
     <el-button type="primary" @click="handleShowQuestionBank">
       <el-icon><Plus /></el-icon>
       从题库添加
     </el-button>

  API 说明：
  - 需要实现批量添加题目到试卷的API
  - 可以参考 ExamQuestionController 中的接口
-->
