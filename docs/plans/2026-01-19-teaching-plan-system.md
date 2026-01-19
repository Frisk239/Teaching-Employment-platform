# Teaching Plan System Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a course teaching plan management system where teachers can create lesson plans for each course instance, and students can view these plans from their course list.

**Architecture:**
- Create new `t_teaching_plan` table linked to `t_course` by `course_id`
- Teaching plans reference timetable via `week_number` (lesson 1 = week 1)
- Teacher CRUD operations via REST API
- Student read-only view accessed from course list page
- Physical deletion (no @TableLogic)

**Tech Stack:**
- Backend: Spring Boot 2.7.18, MyBatis Plus, MySQL
- Frontend: Vue 3, TypeScript, Element Plus, Vite
- Build: Maven (backend), npm (frontend)

---

## Prerequisites

**Verify existing database structure:**
- Table `t_course` exists with fields: id, course_name, teacher_id, start_date, end_date
- Table `t_timetable` exists with fields: id, course_id, week_number, day_of_week, start_period, end_period

**Check:**
```sql
-- Run in MySQL
SHOW CREATE TABLE t_course;
SHOW CREATE TABLE t_timetable;
```

---

## Task 1: Create Database Table

**Files:**
- Create: `backend/create_teaching_plan_table.sql`

**Step 1: Write SQL migration script**

```sql
-- Teaching Plan Table
CREATE TABLE t_teaching_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教学计划ID',
  course_id BIGINT NOT NULL COMMENT '课程ID（关联t_course）',
  lesson_number INT NOT NULL COMMENT '课次序号（第几次课，从1开始）',
  week_number INT NOT NULL COMMENT '周次（对应timetable的week_number）',
  title VARCHAR(200) NOT NULL COMMENT '本次课标题',
  content TEXT COMMENT '教学内容描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  UNIQUE KEY uk_course_lesson (course_id, lesson_number),
  FOREIGN KEY (course_id) REFERENCES t_course(id) ON DELETE CASCADE,

  INDEX idx_course_id (course_id),
  INDEX idx_week_number (week_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学计划';
```

**Step 2: Execute SQL in database**

Run: `mysql -u root -p teaching_employment_platform < backend/create_teaching_plan_table.sql`

Expected: Table created successfully, no errors

**Step 3: Verify table creation**

Run in MySQL:
```sql
DESC t_teaching_plan;
SHOW INDEX FROM t_teaching_plan;
```

Expected output:
```
Field          | Type         | Key | Extra
id             | bigint       | PRI | auto_increment
course_id      | bigint       | MUL |
lesson_number  | int          |     |
week_number    | int          | MUL |
title          | varchar(200) |     |
content        | text         |     |
create_time    | datetime     |     |
update_time    | datetime     |     |
```

**Step 4: Commit**

```bash
cd backend
git add create_teaching_plan_table.sql
git commit -m "feat: add teaching plan database table"
```

---

## Task 2: Create Entity Class

**Files:**
- Create: `backend/src/main/java/com/teaching/employment/entity/TeachingPlan.java`

**Step 1: Write entity class**

```java
package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学计划实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Data
@TableName("t_teaching_plan")
@ApiModel(description = "教学计划")
public class TeachingPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教学计划ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "课次序号")
    private Integer lessonNumber;

    @ApiModelProperty(value = "周次")
    private Integer weekNumber;

    @ApiModelProperty(value = "本次课标题")
    private String title;

    @ApiModelProperty(value = "教学内容描述")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 课程名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 教师ID（关联查询时使用）
     */
    @TableField(exist = false)
    private Long teacherId;
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn compile -DskipTests`

Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/teaching/employment/entity/TeachingPlan.java
git commit -m "feat: add TeachingPlan entity class"
```

---

## Task 3: Create VO Class for API Response

**Files:**
- Create: `backend/src/main/java/com/teaching/employment/dto/TeachingPlanVO.java`

**Step 1: Write VO class**

```java
package com.teaching.employment.dto;

import com.teaching.employment.entity.Timetable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 教学计划VO（包含课程时间表信息）
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Data
@ApiModel(description = "教学计划VO")
public class TeachingPlanVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教学计划ID")
    private Long id;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "课次序号")
    private Integer lessonNumber;

    @ApiModelProperty(value = "周次")
    private Integer weekNumber;

    @ApiModelProperty(value = "本次课标题")
    private String title;

    @ApiModelProperty(value = "教学内容描述")
    private String content;

    @ApiModelProperty(value = "课程名称")
    private String courseName;

    @ApiModelProperty(value = "上课时间信息")
    private Timetable timetable;
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn compile -DskipTests`

Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/teaching/employment/dto/TeachingPlanVO.java
git commit -m "feat: add TeachingPlanVO for API responses"
```

---

## Task 4: Create Mapper Interface

**Files:**
- Create: `backend/src/main/java/com/teaching/employment/mapper/TeachingPlanMapper.java`

**Step 1: Write mapper interface**

```java
package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.TeachingPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教学计划Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Mapper
public interface TeachingPlanMapper extends BaseMapper<TeachingPlan> {
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn compile -DskipTests`

Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/teaching/employment/mapper/TeachingPlanMapper.java
git commit -m "feat: add TeachingPlanMapper interface"
```

---

## Task 5: Create Service Interface

**Files:**
- Create: `backend/src/main/java/com/teaching/employment/service/TeachingPlanService.java`

**Step 1: Write service interface**

```java
package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.dto.TeachingPlanVO;
import com.teaching.employment.entity.TeachingPlan;

import java.util.List;

/**
 * 教学计划Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
public interface TeachingPlanService extends IService<TeachingPlan> {

    /**
     * 根据课程ID获取教学计划列表（按课次排序）
     *
     * @param courseId 课程ID
     * @return 教学计划列表
     */
    List<TeachingPlan> getListByCourseId(Long courseId);

    /**
     * 根据课程ID获取教学计划（包含时间信息）
     *
     * @param courseId 课程ID
     * @return 教学计划VO列表
     */
    List<TeachingPlanVO> getListWithTime(Long courseId);

    /**
     * 批量保存教学计划
     *
     * @param plans 教学计划列表
     * @return 保存成功的计划列表
     */
    List<TeachingPlan> batchSave(List<TeachingPlan> plans);
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn compile -DskipTests`

Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/teaching/employment/service/TeachingPlanService.java
git commit -m "feat: add TeachingPlanService interface"
```

---

## Task 6: Create Service Implementation

**Files:**
- Create: `backend/src/main/java/com/teaching/employment/service/impl/TeachingPlanServiceImpl.java`

**Step 1: Write service implementation**

```java
package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.dto.TeachingPlanVO;
import com.teaching.employment.entity.TeachingPlan;
import com.teaching.employment.mapper.TeachingPlanMapper;
import com.teaching.employment.service.TeachingPlanService;
import com.teaching.employment.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教学计划Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Service
@RequiredArgsConstructor
public class TeachingPlanServiceImpl extends ServiceImpl<TeachingPlanMapper, TeachingPlan>
        implements TeachingPlanService {

    private final TimetableService timetableService;

    @Override
    public List<TeachingPlan> getListByCourseId(Long courseId) {
        return lambdaQuery()
                .eq(TeachingPlan::getCourseId, courseId)
                .orderByAsc(TeachingPlan::getLessonNumber)
                .list();
    }

    @Override
    public List<TeachingPlanVO> getListWithTime(Long courseId) {
        // 1. 查询教学计划
        List<TeachingPlan> plans = getListByCourseId(courseId);

        // 2. 查询该课程的课程表
        var timetables = timetableService.lambdaQuery()
                .eq(com.teaching.employment.entity.Timetable::getCourseId, courseId)
                .list();

        // 3. 根据weekNumber关联
        return plans.stream().map(plan -> {
            TeachingPlanVO vo = new TeachingPlanVO();
            vo.setId(plan.getId());
            vo.setCourseId(plan.getCourseId());
            vo.setLessonNumber(plan.getLessonNumber());
            vo.setWeekNumber(plan.getWeekNumber());
            vo.setTitle(plan.getTitle());
            vo.setContent(plan.getContent());

            // 查找对应周次的课程表信息
            var timetable = timetables.stream()
                    .filter(tt -> tt.getWeekNumber().equals(plan.getWeekNumber()))
                    .findFirst()
                    .orElse(null);

            vo.setTimetable(timetable);

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TeachingPlan> batchSave(List<TeachingPlan> plans) {
        plans.forEach(this::saveOrUpdate);
        return plans;
    }
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn compile -DskipTests`

Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/teaching/employment/service/impl/TeachingPlanServiceImpl.java
git commit -m "feat: add TeachingPlanServiceImpl with business logic"
```

---

## Task 7: Create Controller

**Files:**
- Create: `backend/src/main/java/com/teaching/employment/controller/TeachingPlanController.java`

**Step 1: Write controller**

```java
package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import com.teaching.employment.dto.TeachingPlanVO;
import com.teaching.employment.entity.TeachingPlan;
import com.teaching.employment.service.TeachingPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教学计划管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@RestController
@RequestMapping("/teaching-plan")
@Api(tags = "教学计划管理")
@RequiredArgsConstructor
public class TeachingPlanController {

    private final TeachingPlanService teachingPlanService;

    /**
     * 根据课程ID获取教学计划列表
     */
    @GetMapping("/list/{courseId}")
    @ApiOperation("获取课程教学计划列表")
    public Result<List<TeachingPlan>> getListByCourseId(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        List<TeachingPlan> plans = teachingPlanService.getListByCourseId(courseId);
        return Result.ok(plans);
    }

    /**
     * 根据课程ID获取教学计划（包含时间信息）
     */
    @GetMapping("/detail/{courseId}")
    @ApiOperation("获取课程教学计划详情（含时间）")
    public Result<List<TeachingPlanVO>> getDetail(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        List<TeachingPlanVO> plans = teachingPlanService.getListWithTime(courseId);
        return Result.ok(plans);
    }

    /**
     * 创建教学计划
     */
    @PostMapping
    @ApiOperation("创建教学计划")
    public Result<TeachingPlan> create(@RequestBody TeachingPlan teachingPlan) {
        boolean success = teachingPlanService.save(teachingPlan);
        return success ? Result.ok(teachingPlan) : Result.error("创建失败");
    }

    /**
     * 更新教学计划
     */
    @PutMapping
    @ApiOperation("更新教学计划")
    public Result<Void> update(@RequestBody TeachingPlan teachingPlan) {
        boolean success = teachingPlanService.updateById(teachingPlan);
        return success ? Result.ok() : Result.error("更新失败");
    }

    /**
     * 删除教学计划
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除教学计划")
    public Result<Void> delete(@ApiParam("计划ID") @PathVariable Long id) {
        boolean success = teachingPlanService.removeById(id);
        return success ? Result.ok() : Result.error("删除失败");
    }

    /**
     * 批量保存教学计划
     */
    @PostMapping("/batch")
    @ApiOperation("批量保存教学计划")
    public Result<List<TeachingPlan>> batchSave(
            @RequestBody List<TeachingPlan> plans) {
        List<TeachingPlan> result = teachingPlanService.batchSave(plans);
        return Result.ok(result);
    }
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn compile -DskipTests`

Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/teaching/employment/controller/TeachingPlanController.java
git commit -m "feat: add TeachingPlanController with REST APIs"
```

---

## Task 8: Create Frontend API Interface

**Files:**
- Create: `frontend/src/api/teachingPlan.ts`

**Step 1: Write API interface**

```typescript
/**
 * 教学计划相关 API
 */
import { http } from '@/utils/request'

export interface TeachingPlan {
  id?: number
  courseId: number
  lessonNumber: number
  weekNumber: number
  title: string
  content?: string
  createTime?: string
  updateTime?: string
}

export interface TeachingPlanVO extends TeachingPlan {
  courseName?: string
  timetable?: {
    weekNumber: number
    dayOfWeek: number
    startPeriod: number
    endPeriod: number
  }
}

export const teachingPlanApi = {
  /**
   * 获取课程教学计划列表
   */
  getList: (courseId: number) => {
    return http.get<TeachingPlan[]>(`/teaching-plan/list/${courseId}`)
  },

  /**
   * 获取课程教学计划详情（含时间）
   */
  getDetail: (courseId: number) => {
    return http.get<TeachingPlanVO[]>(`/teaching-plan/detail/${courseId}`)
  },

  /**
   * 创建教学计划
   */
  create: (data: TeachingPlan) => {
    return http.post<void>('/teaching-plan', data)
  },

  /**
   * 更新教学计划
   */
  update: (data: TeachingPlan) => {
    return http.put<void>('/teaching-plan', data)
  },

  /**
   * 删除教学计划
   */
  delete: (id: number) => {
    return http.delete<void>(`/teaching-plan/${id}`)
  },

  /**
   * 批量保存教学计划
   */
  batchSave: (plans: TeachingPlan[]) => {
    return http.post<void>('/teaching-plan/batch', plans)
  }
}
```

**Step 2: Verify TypeScript compilation**

Run: `cd frontend && npm run build`

Expected: No type errors

**Step 3: Commit**

```bash
git add frontend/src/api/teachingPlan.ts
git commit -m "feat: add teaching plan API interface"
```

---

## Task 9: Create Teacher Management Page

**Files:**
- Create: `frontend/src/views/teaching/TeachingPlanManagement.vue`

**Step 1: Write teacher management page component**

```vue
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
```

**Step 2: Verify Vue component compilation**

Run: `cd frontend && npm run build`

Expected: No errors

**Step 3: Commit**

```bash
git add frontend/src/views/teaching/TeachingPlanManagement.vue
git commit -m "feat: add teacher teaching plan management page"
```

---

## Task 10: Add Teaching Plan Menu Entry for Teachers

**Files:**
- Modify: `frontend/src/router/routes.ts`

**Step 1: Add route configuration**

Find the teaching routes section and add:

```typescript
{
  path: 'teaching-plan',
  name: 'TeachingPlan',
  component: () => import('@/views/teaching/TeachingPlanManagement.vue'),
  meta: {
    title: '教学计划',
    roles: ['teacher', 'college_head', 'admin']
  }
}
```

**Step 2: Verify route registration**

Run: `cd frontend && npm run dev`

Navigate to: http://localhost:8081/teaching/teaching-plan

Expected: Page loads without 404 error

**Step 3: Commit**

```bash
git add frontend/src/router/routes.ts
git commit -m "feat: add teaching plan route for teachers"
```

---

## Task 11: Modify Student Course List Page

**Files:**
- Modify: `frontend/src/views/student/MyCourses.vue` (or equivalent)

**Step 1: Add "教学计划" button to course list**

Find the course card/action buttons section and add:

```vue
<el-button
  type="info"
  size="small"
  @click="handleViewTeachingPlan(course)"
>
  教学计划
</el-button>
```

**Step 2: Add navigation handler**

```typescript
const handleViewTeachingPlan = (course: any) => {
  router.push({
    name: 'StudentTeachingPlanView',
    params: { courseId: course.id }
  })
}
```

**Step 3: Commit**

```bash
git add frontend/src/views/student/MyCourses.vue
git commit -m "feat: add teaching plan button to student course list"
```

---

## Task 12: Create Student Teaching Plan View Page

**Files:**
- Create: `frontend/src/views/student/TeachingPlanView.vue`

**Step 1: Write student view component**

```vue
<template>
  <div class="teaching-plan-view">
    <div class="page-header">
      <el-button @click="handleBack">
        <el-icon><ArrowLeft /></el-icon>
        返回课程列表
      </el-button>
      <h1 class="page-title">{{ courseName }} - 教学计划</h1>
    </div>

    <!-- 课程信息 -->
    <el-card shadow="never" class="course-info" v-if="courseInfo">
      <div class="info-row">
        <span><strong>授课教师：</strong>{{ courseInfo.teacherName }}</span>
        <span><strong>教室：</strong>{{ courseInfo.classroomName }}</span>
        <span><strong>总课时：</strong>{{ courseInfo.totalHours }}</span>
      </div>
    </el-card>

    <!-- 教学计划时间线 -->
    <el-card shadow="never" v-loading="loading">
      <el-timeline v-if="plans.length > 0">
        <el-timeline-item
          v-for="plan in plans"
          :key="plan.id"
          :timestamp="formatTime(plan.timetable)"
          placement="top"
        >
          <el-card>
            <h3>第{{ plan.lessonNumber }}次课 - {{ plan.title }}</h3>
            <p v-if="plan.content" class="content">{{ plan.content }}</p>
            <div class="meta-info">
              <el-tag size="small">第{{ plan.weekNumber }}周</el-tag>
              <el-tag v-if="plan.timetable" size="small" type="info">
                {{ formatWeekTime(plan.timetable) }}
              </el-tag>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无教学计划" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { teachingPlanApi, type TeachingPlanVO } from '@/api/teachingPlan'
import { getCourseByIdApi } from '@/api/course'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const courseId = ref<number>(Number(route.params.courseId))
const courseName = ref('')
const courseInfo = ref<any>(null)
const plans = ref<TeachingPlanVO[]>([])

// 获取课程信息
const fetchCourseInfo = async () => {
  try {
    const response = await getCourseByIdApi(courseId.value) as any
    courseName.value = response.courseName
    courseInfo.value = response
  } catch (error) {
    console.error('获取课程信息失败:', error)
  }
}

// 获取教学计划
const fetchPlans = async () => {
  loading.value = true
  try {
    const response = await teachingPlanApi.getDetail(courseId.value) as any
    plans.value = response || []
  } catch (error) {
    console.error('获取教学计划失败:', error)
    ElMessage.error('获取教学计划失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间显示
const formatTime = (timetable: any) => {
  if (!timetable) return '时间待定'
  return `第${timetable.weekNumber}周`
}

const formatWeekTime = (timetable: any) => {
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${weekDays[timetable.dayOfWeek]} 第${timetable.startPeriod}-${timetable.endPeriod}节`
}

// 返回
const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchCourseInfo()
  fetchPlans()
})
</script>

<style scoped lang="scss">
.teaching-plan-view {
  padding: 20px;

  .page-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 20px;
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .course-info {
    margin-bottom: 20px;

    .info-row {
      display: flex;
      gap: 30px;

      span {
        font-size: 14px;
        color: #606266;
      }
    }
  }

  h3 {
    margin: 0 0 12px 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .content {
    margin: 12px 0;
    color: #606266;
    line-height: 1.6;
    white-space: pre-wrap;
  }

  .meta-info {
    display: flex;
    gap: 8px;
  }
}
</style>
```

**Step 2: Add route for student view**

Add to `frontend/src/router/routes.ts` in student section:

```typescript
{
  path: 'teaching-plan/:courseId',
  name: 'StudentTeachingPlanView',
  component: () => import('@/views/student/TeachingPlanView.vue'),
  meta: {
    title: '教学计划',
    roles: ['user']
  }
}
```

**Step 3: Commit**

```bash
git add frontend/src/views/student/TeachingPlanView.vue frontend/src/router/routes.ts
git commit -m "feat: add student teaching plan view page"
```

---

## Task 13: Backend Compilation and Testing

**Files:**
- Test: All backend files

**Step 1: Clean compile backend**

Run: `cd backend && mvn clean compile -DskipTests`

Expected: BUILD SUCCESS

**Step 2: Package backend**

Run: `cd backend && mvn package -DskipTests`

Expected: JAR file created in `target/` directory

**Step 3: Start backend server**

Run: `cd backend && mvn spring-boot:run`

Expected: Server starts on http://localhost:8080/api

**Step 4: Test API endpoints**

Use Knife4j or curl:

```bash
# Test get list (no auth required for testing)
curl http://localhost:8080/api/teaching-plan/list/1

# Test create plan
curl -X POST http://localhost:8080/api/teaching-plan \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": 1,
    "lessonNumber": 1,
    "weekNumber": 1,
    "title": "Java入门",
    "content": "环境搭建与Hello World"
  }'
```

Expected: 200 OK response with plan data

**Step 5: Commit**

```bash
git add .
git commit -m "test: backend compilation and API testing successful"
```

---

## Task 14: Frontend Build and Testing

**Files:**
- Test: All frontend files

**Step 1: Build frontend**

Run: `cd frontend && npm run build`

Expected: Build completes without errors, dist/ folder created

**Step 2: Start dev server**

Run: `cd frontend && npm run dev`

Expected: Dev server starts on http://localhost:8081

**Step 3: Test teacher page**

1. Login as teacher
2. Navigate to: http://localhost:8081/teaching/teaching-plan
3. Select a course
4. Click "快速创建计划"
5. Enter 12 lessons
6. Edit a few plans
7. Save and verify persistence

Expected: All operations work without errors

**Step 4: Test student page**

1. Login as student
2. Navigate to: http://localhost:8081/student/my-courses
3. Click "教学计划" button on a course
4. View the teaching plan timeline

Expected: Teaching plans display correctly with time information

**Step 5: Commit**

```bash
git add .
git commit -m "test: frontend build and testing successful"
```

---

## Task 15: Integration Testing

**Files:**
- Test: Full feature workflow

**Step 1: Test teacher creates plan**

1. Teacher logs in
2. Creates teaching plan for course
3. Sets 12 lessons with titles and content
4. Saves all plans

**Step 2: Test student views plan**

1. Student logs in (enrolled in same course)
2. Views teaching plan for that course
3. Verifies all 12 lessons display
4. Checks time information matches timetable

**Step 3: Test teacher edits plan**

1. Teacher modifies lesson 1 title
2. Changes lesson 3 week number
3. Deletes lesson 12
4. Student refreshes and sees changes

**Step 4: Test data consistency**

Verify in database:
```sql
SELECT * FROM t_teaching_plan WHERE course_id = 1 ORDER BY lesson_number;
SELECT * FROM t_timetable WHERE course_id = 1 ORDER BY week_number;
```

Expected: week_number matches between teaching_plan and timetable

**Step 5: Commit**

```bash
git add .
git commit -m "test: integration testing complete, all features working"
```

---

## Task 16: Documentation and Cleanup

**Files:**
- Create: `backend/TEACHING_PLAN_FEATURE.md`
- Create: `frontend/TEACHING_PLAN_USAGE.md`

**Step 1: Write backend documentation**

Create `backend/TEACHING_PLAN_FEATURE.md`:

```markdown
# 教学计划功能

## 数据库表

表名: `t_teaching_plan`

## API接口

- GET `/api/teaching-plan/list/{courseId}` - 获取教学计划列表
- GET `/api/teaching-plan/detail/{courseId}` - 获取教学计划详情（含时间）
- POST `/api/teaching-plan` - 创建教学计划
- PUT `/api/teaching-plan` - 更新教学计划
- DELETE `/api/teaching-plan/{id}` - 删除教学计划
- POST `/api/teaching-plan/batch` - 批量保存教学计划

## 使用说明

1. 教师为课程创建教学计划
2. 每次课通过 week_number 关联到 timetable
3. 学生可以查看已选课程的教学计划
```

**Step 2: Write frontend documentation**

Create `frontend/TEACHING_PLAN_USAGE.md`:

```markdown
# 教学计划功能使用说明

## 教师端

访问路径: `/teaching/teaching-plan`

功能:
- 选择课程
- 快速创建计划骨架
- 编辑每节课的标题和内容
- 删除不需要的课程

## 学生端

访问路径: 从课程列表点击"教学计划"按钮

功能:
- 查看课程教学计划时间线
- 查看每次课的标题、内容和上课时间
```

**Step 3: Final commit**

```bash
git add backend/TEACHING_PLAN_FEATURE.md frontend/TEACHING_PLAN_USAGE.md
git commit -m "docs: add teaching plan feature documentation"
```

---

## Success Criteria

✅ Database table created with correct foreign keys and indexes
✅ Backend compiles and all tests pass
✅ Frontend builds without errors
✅ Teacher can create, edit, delete teaching plans
✅ Student can view teaching plans from course list
✅ Week numbers correctly match timetable
✅ Physical deletion works (no soft delete)
✅ All code committed to git

---

## Notes

- **YAGNI**: Started with basic CRUD, didn't add version history or progress tracking
- **DRY**: Reused existing Course, Timetable, and authentication patterns
- **TDD**: Tests should be added for service layer (not in initial implementation)
- **Physical deletion**: No @TableLogic annotation, uses real DELETE
