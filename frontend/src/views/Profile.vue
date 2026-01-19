<template>
  <div class="profile-page">
    <el-card shadow="hover" class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人档案</span>
          <el-button type="primary" @click="editMode = !editMode">
            {{ editMode ? '取消编辑' : '编辑资料' }}
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            ref="basicFormRef"
            :model="basicForm"
            :rules="basicRules"
            label-width="100px"
            :disabled="!editMode"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="basicForm.realName" placeholder="请输入姓名" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="学号" prop="studentNo">
                      <el-input v-model="basicForm.studentNo" disabled />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="性别" prop="gender">
                      <el-radio-group v-model="basicForm.gender">
                        <el-radio label="男">男</el-radio>
                        <el-radio label="女">女</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="出生日期" prop="birthDate">
                      <el-date-picker
                        v-model="basicForm.birthDate"
                        type="date"
                        placeholder="选择日期"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="身份证号" prop="idCard">
                      <el-input v-model="basicForm.idCard" placeholder="请输入身份证号" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="民族" prop="nation">
                      <el-input v-model="basicForm.nation" placeholder="请输入民族" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="政治面貌" prop="politicalStatus">
                      <el-select v-model="basicForm.politicalStatus" placeholder="请选择" style="width: 100%">
                        <el-option label="群众" value="群众" />
                        <el-option label="团员" value="团员" />
                        <el-option label="党员" value="党员" />
                        <el-option label="其他" value="其他" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="健康状况" prop="healthStatus">
                      <el-select v-model="basicForm.healthStatus" placeholder="请选择" style="width: 100%">
                        <el-option label="健康" value="健康" />
                        <el-option label="良好" value="良好" />
                        <el-option label="一般" value="一般" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>

            <el-divider />

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="basicForm.phone" placeholder="请输入手机号码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="电子邮箱" prop="email">
                  <el-input v-model="basicForm.email" placeholder="请输入电子邮箱" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="QQ号码" prop="qq">
                  <el-input v-model="basicForm.qq" placeholder="请输入QQ号码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="微信" prop="wechat">
                  <el-input v-model="basicForm.wechat" placeholder="请输入微信号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="家庭住址" prop="address">
              <el-input v-model="basicForm.address" placeholder="请输入家庭住址" />
            </el-form-item>

            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="basicForm.bio"
                type="textarea"
                :rows="3"
                placeholder="请输入个人简介"
              />
            </el-form-item>

            <el-form-item v-if="editMode">
              <el-button type="primary" @click="saveBasicInfo">保存基本信息</el-button>
              <el-button @click="resetBasicForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 教育经历 -->
        <el-tab-pane label="教育经历" name="education">
          <div class="education-section">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="学校">
                {{ educationInfo.schoolName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="学院">
                {{ educationInfo.college || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="专业">
                {{ educationInfo.major || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="年级">
                {{ educationInfo.grade || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="班级">
                {{ educationInfo.className || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="学历">
                {{ educationInfo.education || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="入学时间">
                {{ educationInfo.enrollmentDate || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="预计毕业时间">
                {{ educationInfo.graduationDate || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="学制">
                {{ educationInfo.duration || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="在校状态">
                <el-tag :type="educationInfo.status === '在校' ? 'success' : 'info'">
                  {{ educationInfo.status || '-' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <el-divider />

            <div class="section-header">
              <h4>主修课程</h4>
              <el-button v-if="editMode" size="small" @click="addCourse">添加课程</el-button>
            </div>

            <el-table :data="courses" stripe>
              <el-table-column prop="name" label="课程名称" />
              <el-table-column prop="score" label="成绩" width="100" align="center" />
              <el-table-column prop="semester" label="学期" width="120" />
              <el-table-column v-if="editMode" label="操作" width="100" align="center">
                <template #default="{ row, $index }">
                  <el-button link type="danger" size="small" @click="removeCourse($index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-form-item v-if="editMode" style="margin-top: 20px">
              <el-button type="primary" @click="saveEducation">保存教育经历</el-button>
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 技能标签 -->
        <el-tab-pane label="技能标签" name="skills">
          <div class="skills-section">
            <div class="section-header">
              <h4>专业技能</h4>
              <el-button v-if="editMode" size="small" @click="showSkillDialog = true">添加技能</el-button>
            </div>

            <div class="skills-list">
              <el-tag
                v-for="skill in skills"
                :key="skill.id"
                closable
                @close="removeSkill(skill.id)"
                :type="getSkillTagType(skill.level)"
                size="large"
                style="margin: 5px"
              >
                {{ skill.name }} ({{ skill.level }})
              </el-tag>
              <el-empty v-if="skills.length === 0" description="暂无技能标签，点击添加" :image-size="80" />
            </div>

            <el-divider />

            <div class="section-header">
              <h4>项目经验</h4>
              <el-button v-if="editMode" size="small" @click="showProjectDialog = true">添加项目</el-button>
            </div>

            <el-timeline>
              <el-timeline-item
                v-for="project in projects"
                :key="project.id"
                :timestamp="project.period"
                placement="top"
              >
                <el-card>
                  <div class="project-header">
                    <h4>{{ project.name }}</h4>
                    <div v-if="editMode">
                      <el-button link type="primary" size="small" @click="editProject(project)">
                        编辑
                      </el-button>
                      <el-button link type="danger" size="small" @click="removeProject(project.id)">
                        删除
                      </el-button>
                    </div>
                  </div>
                  <p class="project-role">角色：{{ project.role }}</p>
                  <p class="project-description">{{ project.description }}</p>
                  <div class="project-tech">
                    <el-tag
                      v-for="tech in project.technologies"
                      :key="tech"
                      size="small"
                      style="margin: 2px"
                    >
                      {{ tech }}
                    </el-tag>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>

            <el-empty v-if="projects.length === 0" description="暂无项目经验" :image-size="80" />
          </div>
        </el-tab-pane>

        <!-- 实习经历 -->
        <el-tab-pane label="实习经历" name="internship">
          <div class="internship-section">
            <el-button v-if="editMode" type="primary" @click="showInternshipDialog = true">
              添加实习经历
            </el-button>

            <el-timeline style="margin-top: 20px">
              <el-timeline-item
                v-for="internship in internships"
                :key="internship.id"
                :timestamp="internship.period"
                placement="top"
              >
                <el-card>
                  <div class="internship-header">
                    <h4>{{ internship.company }}</h4>
                    <div v-if="editMode">
                      <el-button link type="primary" size="small" @click="editInternship(internship)">
                        编辑
                      </el-button>
                      <el-button link type="danger" size="small" @click="removeInternship(internship.id)">
                        删除
                      </el-button>
                    </div>
                  </div>
                  <p class="internship-position">职位：{{ internship.position }}</p>
                  <p class="internship-description">{{ internship.description }}</p>
                  <el-descriptions :column="2" size="small" style="margin-top: 10px">
                    <el-descriptions-item label="部门">
                      {{ internship.department }}
                    </el-descriptions-item>
                    <el-descriptions-item label="导师">
                      {{ internship.mentor }}
                    </el-descriptions-item>
                  </el-descriptions>
                </el-card>
              </el-timeline-item>
            </el-timeline>

            <el-empty v-if="internships.length === 0" description="暂无实习经历" :image-size="80" />
          </div>
        </el-tab-pane>

        <!-- 求职偏好 -->
        <el-tab-pane label="求职偏好" name="preferences">
          <el-form
            ref="preferencesFormRef"
            :model="preferencesForm"
            label-width="100px"
            :disabled="!editMode"
          >
            <el-form-item label="期望城市">
              <el-select
                v-model="preferencesForm.expectedCities"
                multiple
                placeholder="请选择期望工作城市"
                style="width: 100%"
              >
                <el-option label="北京" value="北京" />
                <el-option label="上海" value="上海" />
                <el-option label="广州" value="广州" />
                <el-option label="深圳" value="深圳" />
                <el-option label="杭州" value="杭州" />
                <el-option label="成都" value="成都" />
                <el-option label="南京" value="南京" />
                <el-option label="武汉" value="武汉" />
                <el-option label="西安" value="西安" />
              </el-select>
            </el-form-item>

            <el-form-item label="期望职位">
              <el-select
                v-model="preferencesForm.expectedPositions"
                multiple
                placeholder="请选择期望职位"
                style="width: 100%"
              >
                <el-option label="前端开发工程师" value="前端开发工程师" />
                <el-option label="后端开发工程师" value="后端开发工程师" />
                <el-option label="全栈开发工程师" value="全栈开发工程师" />
                <el-option label="Java开发工程师" value="Java开发工程师" />
                <el-option label="Python开发工程师" value="Python开发工程师" />
                <el-option label="算法工程师" value="算法工程师" />
                <el-option label="数据分析师" value="数据分析师" />
                <el-option label="产品经理" value="产品经理" />
                <el-option label="UI设计师" value="UI设计师" />
              </el-select>
            </el-form-item>

            <el-form-item label="期望薪资">
              <el-col :span="11">
                <el-input-number
                  v-model="preferencesForm.salaryMin"
                  :min="0"
                  :step="1000"
                  placeholder="最低薪资"
                />
              </el-col>
              <el-col :span="2" style="text-align: center"> - </el-col>
              <el-col :span="11">
                <el-input-number
                  v-model="preferencesForm.salaryMax"
                  :min="0"
                  :step="1000"
                  placeholder="最高薪资"
                />
              </el-col>
            </el-form-item>

            <el-form-item label="薪资单位">
              <el-radio-group v-model="preferencesForm.salaryUnit">
                <el-radio label="month">月薪</el-radio>
                <el-radio label="year">年薪</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="工作性质">
              <el-checkbox-group v-model="preferencesForm.jobTypes">
                <el-checkbox label="全职">全职</el-checkbox>
                <el-checkbox label="实习">实习</el-checkbox>
                <el-checkbox label="兼职">兼职</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="企业规模">
              <el-checkbox-group v-model="preferencesForm.companySizes">
                <el-checkbox label="初创团队">初创团队</el-checkbox>
                <el-checkbox label="小型企业">小型企业</el-checkbox>
                <el-checkbox label="中型企业">中型企业</el-checkbox>
                <el-checkbox label="大型企业">大型企业</el-checkbox>
                <el-checkbox label="上市公司">上市公司</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="求职状态">
              <el-radio-group v-model="preferencesForm.seekingStatus">
                <el-radio label="actively">正在求职</el-radio>
                <el-radio label="passively">观望中</el-radio>
                <el-radio label="not_seeking">暂不求职</el-radio>
                <el-radio label="employed">已就业</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="自我评价">
              <el-input
                v-model="preferencesForm.selfEvaluation"
                type="textarea"
                :rows="4"
                placeholder="请输入自我评价，包括个人优势、职业规划等"
              />
            </el-form-item>

            <el-form-item v-if="editMode">
              <el-button type="primary" @click="savePreferences">保存求职偏好</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 简历管理 -->
        <el-tab-pane label="简历管理" name="resume">
          <div class="resume-section">
            <el-alert
              title="简历说明"
              type="info"
              :closable="false"
              style="margin-bottom: 20px"
            >
              支持上传 PDF、Word 格式的简历，文件大小不超过 10MB
            </el-alert>

            <el-upload
              class="resume-upload"
              drag
              action="/api/file/upload?type=resume"
              :on-success="handleResumeSuccess"
              :before-upload="beforeResumeUpload"
              :file-list="resumeList"
              :headers="uploadHeaders"
              accept=".pdf,.doc,.docx"
            >
              <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
              <div class="el-upload__text">
                将简历拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 PDF、Word 格式，文件大小不超过 10MB
                </div>
              </template>
            </el-upload>

            <el-divider />

            <div class="section-header">
              <h4>我的简历</h4>
            </div>

            <el-table :data="resumes" stripe>
              <el-table-column prop="name" label="简历名称" />
              <el-table-column prop="uploadTime" label="上传时间" width="180" />
              <el-table-column prop="size" label="文件大小" width="120" />
              <el-table-column label="操作" width="150" align="center">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="previewResume(row)">
                    预览
                  </el-button>
                  <el-button link type="primary" size="small" @click="downloadResume(row)">
                    下载
                  </el-button>
                  <el-button link type="danger" size="small" @click="deleteResume(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="resumes.length === 0" description="暂无简历，请上传" :image-size="80" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加技能对话框 -->
    <el-dialog v-model="showSkillDialog" title="添加技能" width="500px">
      <el-form :model="skillForm" label-width="80px">
        <el-form-item label="技能名称">
          <el-input v-model="skillForm.name" placeholder="请输入技能名称" />
        </el-form-item>
        <el-form-item label="熟练程度">
          <el-select v-model="skillForm.level" placeholder="请选择" style="width: 100%">
            <el-option label="了解" value="了解" />
            <el-option label="入门" value="入门" />
            <el-option label="熟悉" value="熟悉" />
            <el-option label="掌握" value="掌握" />
            <el-option label="精通" value="精通" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSkillDialog = false">取消</el-button>
        <el-button type="primary" @click="addSkill">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加项目对话框 -->
    <el-dialog v-model="showProjectDialog" title="添加项目" width="600px">
      <el-form :model="projectForm" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="担任角色">
          <el-input v-model="projectForm.role" placeholder="如：项目负责人、核心开发等" />
        </el-form-item>
        <el-form-item label="项目时间">
          <el-date-picker
            v-model="projectForm.periodRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="使用技术">
          <el-input v-model="projectForm.technologiesInput" placeholder="多个技术用逗号分隔，如：Vue,Java,MySQL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showProjectDialog = false">取消</el-button>
        <el-button type="primary" @click="addProject">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加实习经历对话框 -->
    <el-dialog v-model="showInternshipDialog" title="添加实习经历" width="600px">
      <el-form :model="internshipForm" label-width="100px">
        <el-form-item label="公司名称">
          <el-input v-model="internshipForm.company" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="internshipForm.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="internshipForm.department" placeholder="请输入部门" />
        </el-form-item>
        <el-form-item label="实习时间">
          <el-date-picker
            v-model="internshipForm.periodRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="导师">
          <el-input v-model="internshipForm.mentor" placeholder="请输入导师姓名" />
        </el-form-item>
        <el-form-item label="实习描述">
          <el-input
            v-model="internshipForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入实习工作内容和收获"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showInternshipDialog = false">取消</el-button>
        <el-button type="primary" @click="addInternship">确定</el-button>
      </template>
    </el-dialog>

    <!-- PDF预览对话框 -->
    <el-dialog
      v-model="showPdfDialog"
      title="简历预览"
      width="80%"
      :close-on-click-modal="false"
      @close="closePdfDialog"
    >
      <div v-loading="pdfLoading" element-loading-text="加载中..." style="min-height: 600px">
        <div v-if="pdfError" style="text-align: center; padding: 50px">
          <el-empty description="PDF加载失败"></el-empty>
          <el-button type="primary" @click="closePdfDialog">关闭</el-button>
        </div>
        <VuePdfEmbed
          v-if="currentPdfUrl && !pdfError"
          :source="currentPdfUrl"
          @loaded="handlePdfLoaded"
          @error="handlePdfError"
          style="width: 100%; height: 700px"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores'
import request from '@/utils/request'
import {
  getStudentSkillsApi,
  getStudentProjectsApi,
  getStudentPreferenceApi,
  updateStudentPreferenceApi,
  getStudentResumesApi,
  getStudentCoursesApi,
  getStudentEducationApi
} from '@/api/student'
import VuePdfEmbed from 'vue-pdf-embed'

const authStore = useAuthStore()

// PDF预览相关
const showPdfDialog = ref(false)
const currentPdfUrl = ref('')
const pdfLoading = ref(true)
const pdfError = ref(false)

// 上传请求头（包含认证token）
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

// 当前激活的标签页
const activeTab = ref('basic')
// 是否处于编辑模式
const editMode = ref(false)

// 基本信息表单
const basicFormRef = ref()
const basicForm = ref({
  realName: '',
  studentNo: '',
  gender: '男',
  birthDate: '',
  idCard: '',
  nation: '汉族',
  politicalStatus: '群众',
  healthStatus: '健康',
  phone: '',
  email: '',
  qq: '',
  wechat: '',
  address: '',
  bio: ''
})

const basicRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的电子邮箱', trigger: 'blur' }]
}

// 教育经历
const educationInfo = ref({
  schoolName: '',
  college: '',
  major: '',
  grade: '',
  className: '',
  education: '',
  enrollmentDate: '',
  graduationDate: '',
  duration: '',
  status: '在校'
})

// 课程列表
const courses = ref([])

// 技能标签
const skills = ref([])
const showSkillDialog = ref(false)
const skillForm = ref({
  name: '',
  level: '熟悉'
})

// 项目经验
const projects = ref([])
const showProjectDialog = ref(false)
const projectForm = ref({
  name: '',
  role: '',
  periodRange: [],
  description: '',
  technologiesInput: ''
})

// 实习经历
const internships = ref([])
const showInternshipDialog = ref(false)
const internshipForm = ref({
  company: '',
  position: '',
  department: '',
  periodRange: [],
  mentor: '',
  description: ''
})

// 求职偏好
const preferencesFormRef = ref()
const preferencesForm = ref({
  expectedCities: [],
  expectedPositions: [],
  salaryMin: undefined,
  salaryMax: undefined,
  salaryUnit: 'month',
  jobTypes: [],
  companySizes: [],
  seekingStatus: 'actively',
  selfEvaluation: ''
})

// 简历管理
const resumeList = ref([])
const resumes = ref([])

// 加载基本信息
const loadBasicInfo = async () => {
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) {
      ElMessage.warning('未找到学员信息')
      return
    }

    const response: any = await request.get(`/student/${studentId}`)

    // 将gender从数字转换为字符串
    const genderMap: Record<number, string> = { 1: '男', 2: '女' }
    const genderStr = response.gender ? genderMap[response.gender] || '男' : '男'

    basicForm.value = {
      ...basicForm.value,
      ...response,
      gender: genderStr,
      birthDate: response.birthDate || ''
    }
  } catch (error) {
    console.error('加载基本信息失败', error)
  }
}

// 加载教育经历
const loadEducation = async () => {
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) return

    // 从API获取教育经历数据
    const eduResponse: any = await getStudentEducationApi(studentId)
    if (eduResponse) {
      educationInfo.value = {
        schoolName: eduResponse.schoolName || '',
        college: eduResponse.college || '',
        major: eduResponse.major || '',
        grade: eduResponse.grade || '',
        className: eduResponse.className || '',
        education: eduResponse.education || '',
        enrollmentDate: eduResponse.enrollmentDate || '',
        graduationDate: eduResponse.graduationDate || '',
        duration: eduResponse.duration || '',
        status: eduResponse.status || '在校'
      }
    }

    // 从API获取课程成绩数据
    const courseResponse: any = await getStudentCoursesApi(studentId)
    if (courseResponse && Array.isArray(courseResponse)) {
      courses.value = courseResponse.map((course: any) => ({
        name: course.courseName,
        score: course.score,
        semester: course.semester
      }))
    } else {
      courses.value = []
    }
  } catch (error) {
    console.error('加载教育经历失败', error)
    educationInfo.value = {
      schoolName: '',
      college: '',
      major: '',
      grade: '',
      className: '',
      education: '',
      enrollmentDate: '',
      graduationDate: '',
      duration: '',
      status: '在校'
    }
    courses.value = []
  }
}

// 加载技能和项目
const loadSkillsAndProjects = async () => {
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) return

    // 从API获取技能数据
    const skillsResponse: any = await getStudentSkillsApi(studentId)
    if (skillsResponse && Array.isArray(skillsResponse)) {
      skills.value = skillsResponse.map((skill: any) => ({
        id: skill.id,
        name: skill.skillName,
        level: skill.skillLevel
      }))
    } else {
      skills.value = []
    }

    // 从API获取项目数据
    const projectsResponse: any = await getStudentProjectsApi(studentId)
    if (projectsResponse && Array.isArray(projectsResponse)) {
      projects.value = projectsResponse.map((project: any) => {
        // 格式化时间范围
        let period = ''
        if (project.startDate && project.endDate) {
          const start = new Date(project.startDate).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit' }).replace(/\//g, '.')
          const end = new Date(project.endDate).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit' }).replace(/\//g, '.')
          period = `${start} - ${end}`
        }

        // 解析技术标签
        const technologies = project.technologies ? project.technologies.split(',') : []

        return {
          id: project.id,
          name: project.projectName,
          role: project.role,
          period,
          description: project.description,
          technologies
        }
      })
    } else {
      projects.value = []
    }
  } catch (error) {
    console.error('加载技能和项目失败', error)
    skills.value = []
    projects.value = []
  }
}

// 加载实习经历
const loadInternships = async () => {
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) return

    // 模拟数据
    internships.value = [
      {
        id: 1,
        company: '某某科技有限公司',
        position: 'Java开发实习生',
        department: '研发部',
        period: '2024.01 - 2024.03',
        mentor: '张工程师',
        description: '参与了公司项目的后端开发工作，负责用户模块和订单模块的开发，使用Spring Boot、MyBatis等技术栈'
      }
    ]
  } catch (error) {
    console.error('加载实习经历失败', error)
  }
}

// 加载求职偏好
const loadPreferences = async () => {
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) return

    // 从API获取求职偏好数据
    const response: any = await getStudentPreferenceApi(studentId)
    if (response) {
      preferencesForm.value = {
        expectedCities: response.expectedCities ? JSON.parse(response.expectedCities) : [],
        expectedPositions: response.expectedPositions ? JSON.parse(response.expectedPositions) : [],
        salaryMin: response.salaryMin,
        salaryMax: response.salaryMax,
        salaryUnit: response.salaryUnit || 'month',
        jobTypes: response.jobTypes ? response.jobTypes.split(',') : [],
        companySizes: response.companySizes ? response.companySizes.split(',') : [],
        seekingStatus: response.seekingStatus || 'actively',
        selfEvaluation: response.selfEvaluation || ''
      }
    } else {
      // 如果没有数据，设置默认值
      preferencesForm.value = {
        expectedCities: [],
        expectedPositions: [],
        salaryMin: undefined,
        salaryMax: undefined,
        salaryUnit: 'month',
        jobTypes: [],
        companySizes: [],
        seekingStatus: 'actively',
        selfEvaluation: ''
      }
    }
  } catch (error) {
    console.error('加载求职偏好失败', error)
  }
}

// 加载简历列表
const loadResumes = async () => {
  try {
    const studentId = authStore.user?.studentId
    if (!studentId) return

    // 从API获取简历数据
    const response: any = await getStudentResumesApi(studentId)
    if (response && Array.isArray(response)) {
      resumes.value = response.map((resume: any) => ({
        id: resume.id,
        name: resume.resumeName,
        uploadTime: resume.uploadTime,
        size: resume.fileSize,
        url: resume.fileUrl
      }))
    } else {
      resumes.value = []
    }
  } catch (error) {
    console.error('加载简历列表失败', error)
    resumes.value = []
  }
}

// 保存基本信息
const saveBasicInfo = async () => {
  try {
    await basicFormRef.value.validate()

    const studentId = authStore.user?.studentId

    // 将gender从字符串转换为数字
    const genderMap: Record<string, number> = { '男': 1, '女': 2 }
    const genderNum = basicForm.value.gender ? genderMap[basicForm.value.gender] || 1 : 1

    await request.put('/student', {
      ...basicForm.value,
      id: studentId,
      gender: genderNum
    })

    ElMessage.success('保存成功')
    editMode.value = false
    loadBasicInfo()
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败，请重试')
  }
}

// 重置基本信息表单
const resetBasicForm = () => {
  loadBasicInfo()
}

// 添加课程
const addCourse = () => {
  courses.value.push({
    name: '',
    score: '',
    semester: ''
  })
}

// 添加技能
const addSkill = () => {
  if (!skillForm.value.name) {
    ElMessage.warning('请输入技能名称')
    return
  }

  skills.value.push({
    id: Date.now(),
    name: skillForm.value.name,
    level: skillForm.value.level
  })

  showSkillDialog.value = false
  skillForm.value = { name: '', level: '熟悉' }
  ElMessage.success('添加成功')
}

// 移除技能
const removeSkill = (id: number) => {
  skills.value = skills.value.filter((s) => s.id !== id)
  ElMessage.success('删除成功')
}

// 获取技能标签类型
const getSkillTagType = (level: string) => {
  const typeMap: Record<string, string> = {
    了解: 'info',
    入门: '',
    熟悉: 'warning',
    掌握: 'success',
    精通: 'danger'
  }
  return typeMap[level] || ''
}

// 添加项目
const addProject = () => {
  if (!projectForm.value.name) {
    ElMessage.warning('请输入项目名称')
    return
  }

  const period = projectForm.value.periodRange
    ? `${projectForm.value.periodRange[0]} - ${projectForm.value.periodRange[1]}`
    : ''

  const technologies = projectForm.value.technologiesInput
    ? projectForm.value.technologiesInput.split(',').map((t) => t.trim())
    : []

  projects.value.push({
    id: Date.now(),
    name: projectForm.value.name,
    role: projectForm.value.role,
    period,
    description: projectForm.value.description,
    technologies
  })

  showProjectDialog.value = false
  projectForm.value = {
    name: '',
    role: '',
    periodRange: [],
    description: '',
    technologiesInput: ''
  }
  ElMessage.success('添加成功')
}

// 编辑项目
const editProject = (project: any) => {
  projectForm.value = {
    name: project.name,
    role: project.role,
    periodRange: project.period.split(' - '),
    description: project.description,
    technologiesInput: project.technologies.join(', ')
  }
  showProjectDialog.value = true
}

// 移除项目
const removeProject = (id: number) => {
  projects.value = projects.value.filter((p) => p.id !== id)
  ElMessage.success('删除成功')
}

// 添加实习经历
const addInternship = () => {
  if (!internshipForm.value.company) {
    ElMessage.warning('请输入公司名称')
    return
  }

  const period = internshipForm.value.periodRange
    ? `${internshipForm.value.periodRange[0]} - ${internshipForm.value.periodRange[1]}`
    : ''

  internships.value.push({
    id: Date.now(),
    company: internshipForm.value.company,
    position: internshipForm.value.position,
    department: internshipForm.value.department,
    period,
    mentor: internshipForm.value.mentor,
    description: internshipForm.value.description
  })

  showInternshipDialog.value = false
  internshipForm.value = {
    company: '',
    position: '',
    department: '',
    periodRange: [],
    mentor: '',
    description: ''
  }
  ElMessage.success('添加成功')
}

// 编辑实习经历
const editInternship = (internship: any) => {
  internshipForm.value = {
    company: internship.company,
    position: internship.position,
    department: internship.department,
    periodRange: internship.period.split(' - '),
    mentor: internship.mentor,
    description: internship.description
  }
  showInternshipDialog.value = true
}

// 移除实习经历
const removeInternship = (id: number) => {
  internships.value = internships.value.filter((i) => i.id !== id)
  ElMessage.success('删除成功')
}

// 保存教育经历
const saveEducation = () => {
  ElMessage.success('保存成功')
}

// 保存求职偏好
const savePreferences = async () => {
  try {
    const studentId = authStore.user?.studentId
    await request.put(`/student/${studentId}/preferences`, preferencesForm.value)

    ElMessage.success('保存成功')
    editMode.value = false
  } catch (error) {
    console.error('保存失败', error)
  }
}

// 简历上传前校验
const beforeResumeUpload = (file: File) => {
  const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只能上传 PDF 或 Word 格式的文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB')
    return false
  }
  return true
}

// 简历上传成功
const handleResumeSuccess = async (response: any, file: any) => {
  try {
    // 检查响应是否成功
    if (response.code !== 200) {
      ElMessage.error(response.message || '上传失败')
      return
    }

    const studentId = authStore.user?.studentId
    if (!studentId) {
      ElMessage.error('未找到学员信息')
      return
    }

    // 保存简历记录到数据库
    await request.post(`/student/${studentId}/resume`, {
      resumeName: file.name,
      fileUrl: response.data.url,
      fileSize: response.data.fileSize
    })

    ElMessage.success('简历上传成功')
    loadResumes()
  } catch (error) {
    console.error('保存简历记录失败', error)
    ElMessage.error('保存简历记录失败')
  }
}

// 预览简历
const previewResume = (resume: any) => {
  // 检查文件类型
  const fileName = resume.name || resume.resumeName || ''
  const isPdf = fileName.toLowerCase().endsWith('.pdf')

  if (isPdf) {
    // PDF文件使用对话框预览
    currentPdfUrl.value = resume.url
    showPdfDialog.value = true
    pdfLoading.value = true
    pdfError.value = false
  } else {
    // Word文档直接下载
    const link = document.createElement('a')
    link.href = resume.url
    link.download = resume.name
    link.click()
    ElMessage.info('Word文档暂不支持在线预览，已开始下载')
  }
}

// PDF加载完成
const handlePdfLoaded = () => {
  pdfLoading.value = false
}

// PDF加载失败
const handlePdfError = () => {
  pdfLoading.value = false
  pdfError.value = true
  ElMessage.error('PDF加载失败')
}

// 关闭PDF对话框
const closePdfDialog = () => {
  showPdfDialog.value = false
  currentPdfUrl.value = ''
  pdfLoading.value = true
  pdfError.value = false
}

// 下载简历
const downloadResume = (resume: any) => {
  const link = document.createElement('a')
  link.href = resume.url
  link.download = resume.name
  link.click()
}

// 删除简历
const deleteResume = async (id: number) => {
  try {
    const studentId = authStore.user?.studentId
    await request.delete(`/student/${studentId}/resume/${id}`)
    ElMessage.success('删除成功')
    loadResumes()
  } catch (error) {
    console.error('删除失败', error)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadBasicInfo()
  loadEducation()
  loadSkillsAndProjects()
  loadInternships()
  loadPreferences()
  loadResumes()
})
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 20px;
}

.profile-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .avatar-section {
    text-align: center;
    padding: 20px;

    .avatar-upload {
      margin-top: 10px;
    }
  }
}

.education-section,
.skills-section,
.internship-section,
.resume-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
    }
  }
}

.skills-section {
  .skills-list {
    min-height: 100px;
    padding: 20px;
    background-color: #f5f7fa;
    border-radius: 4px;
  }

  .project-header,
  .internship-header {
    display: flex;
    justify-content: space-between;
    align-items: start;

    h4 {
      margin: 0 0 10px 0;
      font-size: 15px;
      font-weight: 500;
    }
  }

  .project-role,
  .internship-position {
    margin: 5px 0;
    font-size: 13px;
    color: #606266;
  }

  .project-description,
  .internship-description {
    margin: 10px 0;
    font-size: 13px;
    color: #909399;
    line-height: 1.6;
  }

  .project-tech {
    margin-top: 10px;
  }
}

.resume-section {
  .resume-upload {
    margin-bottom: 20px;
  }
}
</style>
