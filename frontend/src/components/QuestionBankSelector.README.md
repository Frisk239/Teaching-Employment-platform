# QuestionBankSelector 组件

## 概述

题库选择器对话框组件，用于从题库中选择题目并添加到试卷中。

## 功能特性

- ✅ 支持按知识点筛选（单选）
- ✅ 支持按题型筛选（多选：单选、多选、判断、填空、简答）
- ✅ 支持按难度筛选（单选：简单、中等、困难）
- ✅ 支持关键词搜索
- ✅ 显示已添加的题目（禁用选择状态）
- ✅ 分页显示题目列表（每页20条）
- ✅ 实时显示已选择题目数量

## Props

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| visible | boolean | 是 | 控制对话框显示/隐藏 |
| examId | number | 否 | 当前试卷ID，用于获取已有题目 |

## Events

| 事件名 | 参数 | 说明 |
|--------|------|------|
| confirm | questionIds: number[] | 确认选择时触发，返回选中的题目ID数组 |
| close | - | 关闭对话框时触发 |

## 使用示例

```vue
<template>
  <div>
    <el-button type="primary" @click="openSelector">
      从题库选择题目
    </el-button>

    <QuestionBankSelector
      :visible="selectorVisible"
      :exam-id="currentExamId"
      @confirm="handleConfirm"
      @close="selectorVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import QuestionBankSelector from '@/components/QuestionBankSelector.vue'

const selectorVisible = ref(false)
const currentExamId = ref<number>(1)

const openSelector = () => {
  selectorVisible.value = true
}

const handleConfirm = async (questionIds: number[]) => {
  console.log('选中的题目ID:', questionIds)
  // 调用后端API将题目添加到试卷
  // await addQuestionsToExam(currentExamId.value, questionIds)
}
</script>
```

## API 依赖

组件依赖以下API：

1. **GET /api/question-bank/knowledge-points** - 获取知识点列表
2. **GET /api/question-bank/page** - 分页查询题目
3. **GET /api/exam-question/exam/{examId}/question-ids** - 获取试卷已有题目ID

## 布局结构

```
┌─────────────────────────────────────────────────┐
│  题库选择器                            [×]      │
├──────────────┬──────────────────────────────────┤
│              │  已选择 X 道题                    │
│  ┌────────┐  │  ┌────────────────────────────┐  │
│  │ 筛选区 │  │  │ 题目列表表格               │  │
│  │        │  │  │                            │  │
│  │ • 知识点│  │  │ □ 题干  题型  知识点  难度│  │
│  │ • 题型 │  │  │ □ ...                     │  │
│  │ • 难度 │  │  │ □ ...                     │  │
│  │ • 关键词│  │  └────────────────────────────┘  │
│  │ • 按钮 │  │  分页控件                       │
│  └────────┘  │                                  │
└──────────────┴──────────────────────────────────┘
│              [取消]  [确定(X道题)]               │
└─────────────────────────────────────────────────┘
```

## 样式说明

- 使用项目统一的 OKLCH 颜色系统
- 左侧筛选区宽度：280px
- 对话框宽度：1200px
- 响应式设计：小屏幕下筛选区移至顶部

## 注意事项

1. 已添加到试卷的题目会显示"已添加"标签并禁用选择
2. 确认时会检查是否至少选择了一道题目
3. 对话框关闭时会自动重置所有筛选条件
4. 组件会在打开时自动加载知识点和已有题目列表
