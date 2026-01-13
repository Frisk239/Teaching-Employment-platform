<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-header">
      <h1 class="page-title">热门课程</h1>
      <div class="page-actions">
        <el-button @click="loadCourses" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 课程统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-header">
          <div class="stat-icon blue">
            <el-icon><Reading /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ totalCourses }}</div>
        <p class="stat-label">课程总数</p>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <div class="stat-icon green">
            <el-icon><User /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ totalStudents }}</div>
        <p class="stat-label">学习人数</p>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <div class="stat-icon orange">
            <el-icon><Star /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ avgRating }}</div>
        <p class="stat-label">平均评分</p>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <div class="stat-icon purple">
            <el-icon><TrendCharts /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ completionRate }}%</div>
        <p class="stat-label">完课率</p>
      </div>
    </div>

    <!-- 课程列表 -->
    <div class="courses-grid">
      <div
        v-for="course in popularCourses"
        :key="course.id"
        class="course-card"
      >
        <div class="course-cover">
          <img v-if="course.cover" :src="course.cover" :alt="course.name" />
          <div v-else class="course-cover-placeholder">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="course-badge">{{ course.category }}</div>
        </div>

        <div class="course-body">
          <h3 class="course-title">{{ course.name }}</h3>
          <p class="course-description">{{ course.description }}</p>

          <div class="course-info">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ course.teacher }}</span>
            </div>
            <div class="info-item">
              <el-icon><UserFilled /></el-icon>
              <span>{{ course.students }}人</span>
            </div>
          </div>

          <div class="course-meta">
            <div class="rating">
              <el-rate v-model="course.rating" disabled show-score text-color="#ff9900"></el-rate>
            </div>
            <div class="course-status" :class="`status-${course.status}`">
              {{ course.statusText }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 加载状态
const loading = ref(false)

// 热门课程列表
const popularCourses = ref([])

// 统计数据
const totalCourses = computed(() => popularCourses.value.length)
const totalStudents = computed(() => {
  return popularCourses.value.reduce((sum: number, course: any) => sum + course.students, 0)
})
const avgRating = computed(() => {
  if (popularCourses.value.length === 0) return 0
  const total = popularCourses.value.reduce((sum: number, course: any) => sum + course.rating, 0)
  return (total / popularCourses.value.length).toFixed(1)
})
const completionRate = computed(() => {
  return 85
})

// 加载热门课程
const loadCourses = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    // const data = await courseApi.getPopularCourses()
    // popularCourses.value = data

    // 模拟数据
    popularCourses.value = [
      {
        id: 1,
        name: 'Java 程序设计',
        description: '学习 Java 基础语法和面向对象编程',
        teacher: '张老师',
        students: 120,
        rating: 4.8,
        category: '后端开发',
        status: 'ongoing',
        statusText: '进行中',
        cover: ''
      },
      {
        id: 2,
        name: 'Vue.js 前端开发',
        description: '掌握 Vue.js 框架开发单页应用',
        teacher: '李老师',
        students: 98,
        rating: 4.9,
        category: '前端开发',
        status: 'ongoing',
        statusText: '进行中',
        cover: ''
      },
      {
        id: 3,
        name: 'Python 数据分析',
        description: '使用 Python 进行数据分析和可视化',
        teacher: '王老师',
        students: 85,
        rating: 4.7,
        category: '数据科学',
        status: 'ongoing',
        statusText: '进行中',
        cover: ''
      },
      {
        id: 4,
        name: 'Spring Boot 实战',
        description: '使用 Spring Boot 开发企业级应用',
        teacher: '赵老师',
        students: 76,
        rating: 4.6,
        category: '后端开发',
        status: 'not_started',
        statusText: '未开始',
        cover: ''
      },
      {
        id: 5,
        name: '数据结构与算法',
        description: '深入理解常用数据结构和算法',
        teacher: '刘老师',
        students: 92,
        rating: 4.8,
        category: '计算机基础',
        status: 'ongoing',
        statusText: '进行中',
        cover: ''
      },
      {
        id: 6,
        name: 'React Native 开发',
        description: '使用 React Native 开发跨平台应用',
        teacher: '陈老师',
        students: 68,
        rating: 4.5,
        category: '移动开发',
        status: 'ongoing',
        statusText: '进行中',
        cover: ''
      }
    ]
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCourses()
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;

  .stat-card {
    background: white;
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    padding: 1.5rem;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: var(--shadow-md);
      transform: translateY(-2px);
    }

    .stat-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1rem;

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: var(--radius-md);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.5rem;
        color: white;

        &.blue {
          background: var(--gradient-blue);
        }

        &.green {
          background: var(--gradient-green);
        }

        &.orange {
          background: var(--gradient-orange);
        }

        &.purple {
          background: var(--gradient-purple);
        }
      }
    }

    .stat-value {
      font-size: 2rem;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 0.5rem;
    }

    .stat-label {
      margin: 0;
      color: var(--text-secondary);
      font-size: 0.875rem;
    }
  }
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;

  .course-card {
    background: white;
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    overflow: hidden;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: var(--shadow-lg);
      transform: translateY(-4px);
    }

    .course-cover {
      position: relative;
      height: 180px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .course-cover-placeholder {
        width: 100%;
        height: 100%;
        background: var(--gradient-blue);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 3rem;
      }

      .course-badge {
        position: absolute;
        top: 1rem;
        right: 1rem;
        padding: 0.25rem 0.75rem;
        background: rgba(255, 255, 255, 0.95);
        border-radius: 9999px;
        font-size: 0.75rem;
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .course-body {
      padding: 1.5rem;

      .course-title {
        font-size: 1.125rem;
        font-weight: 600;
        margin: 0 0 0.75rem 0;
        color: var(--text-primary);
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .course-description {
        font-size: 0.875rem;
        color: var(--text-secondary);
        margin: 0 0 1rem 0;
        line-height: 1.6;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .course-info {
        display: flex;
        gap: 1rem;
        margin-bottom: 1rem;

        .info-item {
          display: flex;
          align-items: center;
          gap: 0.25rem;
          font-size: 0.875rem;
          color: var(--text-secondary);
        }
      }

      .course-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .rating {
          flex: 1;
        }

        .course-status {
          padding: 0.25rem 0.75rem;
          border-radius: 9999px;
          font-size: 0.75rem;
          font-weight: 600;

          &.status-ongoing {
            background: oklch(0.92 0.04 150);
            color: oklch(0.55 0.15 150);
          }

          &.status-not_started {
            background: oklch(0.92 0.04 220);
            color: oklch(0.55 0.15 220);
          }

          &.status-finished {
            background: oklch(0.95 0.02 45);
            color: oklch(0.60 0.18 45);
          }
        }
      }
    }
  }
}
</style>
