/**
 * 权限常量定义
 * 用于定义系统中所有的权限点
 */

// 系统管理权限
export const SYSTEM_PERMISSIONS = {
  // 用户管理
  USER_VIEW: 'system:user:view',
  USER_ADD: 'system:user:add',
  USER_EDIT: 'system:user:edit',
  USER_DELETE: 'system:user:delete',
  USER_EXPORT: 'system:user:export',

  // 角色管理
  ROLE_VIEW: 'system:role:view',
  ROLE_ADD: 'system:role:add',
  ROLE_EDIT: 'system:role:edit',
  ROLE_DELETE: 'system:role:delete',
  ROLE_ASSIGN_PERMISSION: 'system:role:assign:permission',

  // 权限管理
  PERMISSION_VIEW: 'system:permission:view',
  PERMISSION_ADD: 'system:permission:add',
  PERMISSION_EDIT: 'system:permission:edit',
  PERMISSION_DELETE: 'system:permission:delete',

  // 菜单管理
  MENU_VIEW: 'system:menu:view',
  MENU_ADD: 'system:menu:add',
  MENU_EDIT: 'system:menu:edit',
  MENU_DELETE: 'system:menu:delete',
}

// 教学管理权限
export const TEACHING_PERMISSIONS = {
  // 学校管理
  SCHOOL_VIEW_ALL: 'teaching:school:view:all', // 查看所有学校（管理员）
  SCHOOL_VIEW_OWN: 'teaching:school:view:own', // 查看本校（学院负责人）
  SCHOOL_EDIT: 'teaching:school:edit',
  SCHOOL_ADD: 'teaching:school:add',
  SCHOOL_DELETE: 'teaching:school:delete',

  // 教室管理
  CLASSROOM_VIEW_ALL: 'teaching:classroom:view:all',
  CLASSROOM_VIEW_SCHOOL: 'teaching:classroom:view:school',
  CLASSROOM_BOOK: 'teaching:classroom:book', // 预约教室（教师）
  CLASSROOM_EDIT: 'teaching:classroom:edit',
  CLASSROOM_ADD: 'teaching:classroom:add',
  CLASSROOM_DELETE: 'teaching:classroom:delete',

  // 课程管理
  COURSE_VIEW_ALL: 'teaching:course:view:all', // 查看所有课程（管理员）
  COURSE_VIEW_SCHOOL: 'teaching:course:view:school', // 查看本校课程（学院负责人）
  COURSE_VIEW_OWN: 'teaching:course:view:own', // 查看自己的课程（教师）
  COURSE_VIEW_SELECTED: 'teaching:course:view:selected', // 查看已选课程（学员）
  COURSE_EDIT: 'teaching:course:edit',
  COURSE_ADD: 'teaching:course:add',
  COURSE_DELETE: 'teaching:course:delete',
  COURSE_PUBLISH: 'teaching:course:publish',
  COURSE_SELECT: 'teaching:course:select', // 学员选课

  // 学员管理
  STUDENT_VIEW_ALL: 'teaching:student:view:all', // 查看所有学员（管理员）
  STUDENT_VIEW_SCHOOL: 'teaching:student:view:school', // 查看本校学员（学院负责人）
  STUDENT_VIEW_CLASS: 'teaching:student:view:class', // 查看班级学员（教师）
  STUDENT_VIEW_OWN: 'teaching:student:view:own', // 查看自己（学员）
  STUDENT_EDIT: 'teaching:student:edit',
  STUDENT_ADD: 'teaching:student:add',
  STUDENT_DELETE: 'teaching:student:delete',
  STUDENT_EXPORT: 'teaching:student:export',

  // 教师管理
  TEACHER_VIEW_ALL: 'teaching:teacher:view:all', // 查看所有教师（管理员）
  TEACHER_VIEW_SCHOOL: 'teaching:teacher:view:school', // 查看本校教师（学院负责人）
  TEACHER_EDIT: 'teaching:teacher:edit',
  TEACHER_ADD: 'teaching:teacher:add',
  TEACHER_DELETE: 'teaching:teacher:delete',

  // 作业批改
  HOMEWORK_VIEW_ALL: 'teaching:homework:view:all', // 查看所有作业（管理员）
  HOMEWORK_VIEW_SCHOOL: 'teaching:homework:view:school', // 查看本校作业（学院负责人）
  HOMEWORK_VIEW_OWN: 'teaching:homework:view:own', // 查看自己的作业（教师、学员）
  HOMEWORK_GRADE_ALL: 'teaching:homework:grade:all', // 批改所有作业（管理员）
  HOMEWORK_GRADE_OWN: 'teaching:homework:grade:own', // 批改自己的作业（教师）
  HOMEWORK_SUBMIT: 'teaching:homework:submit', // 提交作业（学员）
  HOMEWORK_EDIT: 'teaching:homework:edit',
  HOMEWORK_DELETE: 'teaching:homework:delete',

  // 作业/考试发布
  HOMEWORK_PUBLISH_ALL: 'teaching:homework:publish:all', // 发布所有作业（管理员、学院负责人）
  HOMEWORK_PUBLISH_OWN: 'teaching:homework:publish:own', // 发布自己的作业（教师）
  EXAM_PUBLISH_ALL: 'teaching:exam:publish:all', // 发布所有考试
  EXAM_PUBLISH_OWN: 'teaching:exam:publish:own', // 发布自己的考试

  // 日报管理
  DAILY_REPORT_VIEW_ALL: 'teaching:dailyreport:view:all', // 查看所有日报（管理员）
  DAILY_REPORT_VIEW_SCHOOL: 'teaching:dailyreport:view:school', // 查看本校日报（学院负责人）
  DAILY_REPORT_SUBMIT: 'teaching:dailyreport:submit', // 提交日报（教师、学员）
  DAILY_REPORT_REVIEW: 'teaching:dailyreport:review', // 审阅日报（学院负责人）
  DAILY_REPORT_DELETE: 'teaching:dailyreport:delete',
}

// 就业管理权限
export const EMPLOYMENT_PERMISSIONS = {
  // 企业管理
  COMPANY_VIEW_ALL: 'employment:company:view:all', // 查看所有企业（管理员）
  COMPANY_VIEW_OWN: 'employment:company:view:own', // 查看自己的企业（企业对接人）
  COMPANY_EDIT: 'employment:company:edit',
  COMPANY_ADD: 'employment:company:add',
  COMPANY_DELETE: 'employment:company:delete',
  COMPANY_VERIFY: 'employment:company:verify', // 企业认证（管理员）

  // 岗位管理
  POSITION_VIEW_ALL: 'employment:position:view:all', // 查看所有岗位（管理员、学院负责人）
  POSITION_VIEW_OWN: 'employment:position:view:own', // 查看自己的岗位（企业对接人）
  POSITION_VIEW_AVAILABLE: 'employment:position:view:available', // 查看可投递岗位（学员）
  POSITION_EDIT: 'employment:position:edit',
  POSITION_ADD: 'employment:position:add',
  POSITION_DELETE: 'employment:position:delete',
  POSITION_APPLY: 'employment:position:apply', // 投递岗位（学员）
  POSITION_RECOMMEND: 'employment:position:recommend', // 岗位推荐

  // 求职申请管理
  APPLICATION_VIEW_ALL: 'employment:application:view:all', // 查看所有申请（管理员）
  APPLICATION_VIEW_SCHOOL: 'employment:application:view:school', // 查看本校申请（学院负责人）
  APPLICATION_VIEW_OWN: 'employment:application:view:own', // 查看自己的申请（学员）
  APPLICATION_VIEW_COMPANY: 'employment:application:view:company', // 查看本企业申请（企业对接人）
  APPLICATION_PROCESS: 'employment:application:process', // 处理申请（企业对接人）
  APPLICATION_ACCEPT: 'employment:application:accept', // 接受申请
  APPLICATION_REJECT: 'employment:application:reject', // 拒绝申请
  APPLICATION_DELETE: 'employment:application:delete',

  // 笔试管理
  WRITTEN_TEST_VIEW_ALL: 'employment:writtentest:view:all', // 查看所有笔试（管理员）
  WRITTEN_TEST_VIEW_OWN: 'employment:writtentest:view:own', // 查看自己的笔试（学员、企业对接人）
  WRITTEN_TEST_MANAGE: 'employment:writtentest:manage', // 管理笔试（企业对接人）
  WRITTEN_TEST_TAKE: 'employment:writtentest:take', // 参加笔试（学员）
  WRITTEN_TEST_EDIT: 'employment:writtentest:edit',
  WRITTEN_TEST_DELETE: 'employment:writtentest:delete',

  // 面试管理
  INTERVIEW_VIEW_ALL: 'employment:interview:view:all', // 查看所有面试（管理员）
  INTERVIEW_VIEW_SCHOOL: 'employment:interview:view:school', // 查看本校面试（学院负责人）
  INTERVIEW_VIEW_OWN: 'employment:interview:view:own', // 查看自己的面试（学员）
  INTERVIEW_VIEW_COMPANY: 'employment:interview:view:company', // 查看本企业面试（企业对接人）
  INTERVIEW_ARRANGE: 'employment:interview:arrange', // 安排面试（企业对接人）
  INTERVIEW_ATTEND: 'employment:interview:attend', // 参加面试（学员）
  INTERVIEW_EVALUATE: 'employment:interview:evaluate', // 面试评价
  INTERVIEW_EDIT: 'employment:interview:edit',
  INTERVIEW_DELETE: 'employment:interview:delete',

  // 统计
  STATISTICS_VIEW_GLOBAL: 'employment:statistics:view:global', // 查看全局统计（管理员）
  STATISTICS_VIEW_SCHOOL: 'employment:statistics:view:school', // 查看本校统计（学院负责人）
  STATISTICS_VIEW_COMPANY: 'employment:statistics:view:company', // 查看企业统计（企业对接人）
  STATISTICS_EXPORT: 'employment:statistics:export', // 导出统计
}

// 个人中心权限（所有角色都有）
export const PROFILE_PERMISSIONS = {
  VIEW: 'profile:view',
  EDIT: 'profile:edit',
  CHANGE_PASSWORD: 'profile:change:password',
  UPLOAD_AVATAR: 'profile:upload:avatar',
  VIEW_NOTIFICATIONS: 'profile:view:notifications',
}

// 所有权限导出
export const ALL_PERMISSIONS = {
  ...SYSTEM_PERMISSIONS,
  ...TEACHING_PERMISSIONS,
  ...EMPLOYMENT_PERMISSIONS,
  ...PROFILE_PERMISSIONS,
}

// 角色权限映射
export const ROLE_PERMISSIONS: Record<string, string[]> = {
  // 管理员：拥有所有权限
  admin: ['*'],

  // 学院负责人：学校级别权限
  college_head: [
    // 学校管理（只能查看和管理本校）
    TEACHING_PERMISSIONS.SCHOOL_VIEW_OWN,
    TEACHING_PERMISSIONS.SCHOOL_EDIT,

    // 教室管理（本校教室）
    TEACHING_PERMISSIONS.CLASSROOM_VIEW_SCHOOL,
    TEACHING_PERMISSIONS.CLASSROOM_EDIT,
    TEACHING_PERMISSIONS.CLASSROOM_ADD,
    TEACHING_PERMISSIONS.CLASSROOM_DELETE,

    // 课程管理（本校课程）
    TEACHING_PERMISSIONS.COURSE_VIEW_SCHOOL,
    TEACHING_PERMISSIONS.COURSE_EDIT,
    TEACHING_PERMISSIONS.COURSE_ADD,
    TEACHING_PERMISSIONS.COURSE_DELETE,
    TEACHING_PERMISSIONS.COURSE_PUBLISH,

    // 学员管理（本校学员）
    TEACHING_PERMISSIONS.STUDENT_VIEW_SCHOOL,
    TEACHING_PERMISSIONS.STUDENT_EDIT,
    TEACHING_PERMISSIONS.STUDENT_EXPORT,

    // 教师管理（本校教师）
    TEACHING_PERMISSIONS.TEACHER_VIEW_SCHOOL,

    // 作业批改（查看所有本校作业）
    TEACHING_PERMISSIONS.HOMEWORK_VIEW_SCHOOL,
    TEACHING_PERMISSIONS.HOMEWORK_GRADE_ALL,

    // 作业/考试发布（发布全校作业考试）
    TEACHING_PERMISSIONS.HOMEWORK_PUBLISH_ALL,
    TEACHING_PERMISSIONS.EXAM_PUBLISH_ALL,

    // 日报管理（查看和审阅本校日报）
    TEACHING_PERMISSIONS.DAILY_REPORT_VIEW_SCHOOL,
    TEACHING_PERMISSIONS.DAILY_REPORT_REVIEW,
    TEACHING_PERMISSIONS.DAILY_REPORT_DELETE,

    // 就业管理（查看本校数据）
    EMPLOYMENT_PERMISSIONS.POSITION_VIEW_ALL,
    EMPLOYMENT_PERMISSIONS.APPLICATION_VIEW_SCHOOL,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_VIEW_SCHOOL,
    EMPLOYMENT_PERMISSIONS.STATISTICS_VIEW_SCHOOL,
    EMPLOYMENT_PERMISSIONS.STATISTICS_EXPORT,

    // 个人中心
    PROFILE_PERMISSIONS.VIEW,
    PROFILE_PERMISSIONS.EDIT,
    PROFILE_PERMISSIONS.CHANGE_PASSWORD,
    PROFILE_PERMISSIONS.UPLOAD_AVATAR,
    PROFILE_PERMISSIONS.VIEW_NOTIFICATIONS,
  ],

  // 教师：自己的课程和班级
  teacher: [
    // 教室管理（查看和预约）
    TEACHING_PERMISSIONS.CLASSROOM_VIEW_SCHOOL,
    TEACHING_PERMISSIONS.CLASSROOM_BOOK,

    // 课程管理（自己的课程）
    TEACHING_PERMISSIONS.COURSE_VIEW_OWN,
    TEACHING_PERMISSIONS.COURSE_EDIT,
    TEACHING_PERMISSIONS.COURSE_PUBLISH,

    // 学员管理（自己的班级学员）
    TEACHING_PERMISSIONS.STUDENT_VIEW_CLASS,
    TEACHING_PERMISSIONS.STUDENT_EDIT,

    // 作业批改（批改自己的作业）
    TEACHING_PERMISSIONS.HOMEWORK_VIEW_OWN,
    TEACHING_PERMISSIONS.HOMEWORK_GRADE_OWN,
    TEACHING_PERMISSIONS.HOMEWORK_EDIT,
    TEACHING_PERMISSIONS.HOMEWORK_DELETE,

    // 作业/考试发布（发布自己的作业考试）
    TEACHING_PERMISSIONS.HOMEWORK_PUBLISH_OWN,
    TEACHING_PERMISSIONS.EXAM_PUBLISH_OWN,

    // 日报管理（提交日报）
    TEACHING_PERMISSIONS.DAILY_REPORT_SUBMIT,
    TEACHING_PERMISSIONS.DAILY_REPORT_VIEW_OWN,

    // 就业管理（查看班级数据）
    EMPLOYMENT_PERMISSIONS.APPLICATION_VIEW_CLASS,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_VIEW_OWN,
    EMPLOYMENT_PERMISSIONS.STATISTICS_VIEW_SCHOOL,

    // 个人中心
    PROFILE_PERMISSIONS.VIEW,
    PROFILE_PERMISSIONS.EDIT,
    PROFILE_PERMISSIONS.CHANGE_PASSWORD,
    PROFILE_PERMISSIONS.UPLOAD_AVATAR,
    PROFILE_PERMISSIONS.VIEW_NOTIFICATIONS,
  ],

  // 学员：学习相关
  user: [
    // 课程管理（查看和选择课程）
    TEACHING_PERMISSIONS.COURSE_VIEW_SELECTED,
    TEACHING_PERMISSIONS.COURSE_SELECT,

    // 学员管理（查看自己）
    TEACHING_PERMISSIONS.STUDENT_VIEW_OWN,
    TEACHING_PERMISSIONS.STUDENT_EDIT,

    // 作业管理（查看和提交自己的作业）
    TEACHING_PERMISSIONS.HOMEWORK_VIEW_OWN,
    TEACHING_PERMISSIONS.HOMEWORK_SUBMIT,

    // 日报管理（提交日报）
    TEACHING_PERMISSIONS.DAILY_REPORT_SUBMIT,
    TEACHING_PERMISSIONS.DAILY_REPORT_VIEW_OWN,

    // 就业管理（查看和投递岗位）
    EMPLOYMENT_PERMISSIONS.COMPANY_VIEW_ALL,
    EMPLOYMENT_PERMISSIONS.POSITION_VIEW_AVAILABLE,
    EMPLOYMENT_PERMISSIONS.POSITION_APPLY,
    EMPLOYMENT_PERMISSIONS.POSITION_RECOMMEND,
    EMPLOYMENT_PERMISSIONS.APPLICATION_VIEW_OWN,
    EMPLOYMENT_PERMISSIONS.WRITTEN_TEST_VIEW_OWN,
    EMPLOYMENT_PERMISSIONS.WRITTEN_TEST_TAKE,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_VIEW_OWN,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_ATTEND,

    // 个人中心
    PROFILE_PERMISSIONS.VIEW,
    PROFILE_PERMISSIONS.EDIT,
    PROFILE_PERMISSIONS.CHANGE_PASSWORD,
    PROFILE_PERMISSIONS.UPLOAD_AVATAR,
    PROFILE_PERMISSIONS.VIEW_NOTIFICATIONS,
  ],

  // 企业对接人：企业招聘相关
  enterprise_contact: [
    // 企业管理（管理自己的企业）
    EMPLOYMENT_PERMISSIONS.COMPANY_VIEW_OWN,
    EMPLOYMENT_PERMISSIONS.COMPANY_EDIT,

    // 岗位管理（管理本企业的岗位）
    EMPLOYMENT_PERMISSIONS.POSITION_VIEW_OWN,
    EMPLOYMENT_PERMISSIONS.POSITION_EDIT,
    EMPLOYMENT_PERMISSIONS.POSITION_ADD,
    EMPLOYMENT_PERMISSIONS.POSITION_DELETE,

    // 求职申请管理（处理本企业的申请）
    EMPLOYMENT_PERMISSIONS.APPLICATION_VIEW_COMPANY,
    EMPLOYMENT_PERMISSIONS.APPLICATION_PROCESS,
    EMPLOYMENT_PERMISSIONS.APPLICATION_ACCEPT,
    EMPLOYMENT_PERMISSIONS.APPLICATION_REJECT,

    // 笔试管理（管理本企业的笔试）
    EMPLOYMENT_PERMISSIONS.WRITTEN_TEST_VIEW_OWN,
    EMPLOYMENT_PERMISSIONS.WRITTEN_TEST_MANAGE,
    EMPLOYMENT_PERMISSIONS.WRITTEN_TEST_EDIT,
    EMPLOYMENT_PERMISSIONS.WRITTEN_TEST_DELETE,

    // 面试管理（管理本企业的面试）
    EMPLOYMENT_PERMISSIONS.INTERVIEW_VIEW_COMPANY,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_ARRANGE,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_EVALUATE,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_EDIT,
    EMPLOYMENT_PERMISSIONS.INTERVIEW_DELETE,

    // 统计（查看企业统计）
    EMPLOYMENT_PERMISSIONS.STATISTICS_VIEW_COMPANY,
    EMPLOYMENT_PERMISSIONS.STATISTICS_EXPORT,

    // 个人中心
    PROFILE_PERMISSIONS.VIEW,
    PROFILE_PERMISSIONS.EDIT,
    PROFILE_PERMISSIONS.CHANGE_PASSWORD,
    PROFILE_PERMISSIONS.UPLOAD_AVATAR,
    PROFILE_PERMISSIONS.VIEW_NOTIFICATIONS,
  ],
}

/**
 * 检查角色是否有某个权限
 * @param role 角色
 * @param permission 权限
 * @returns 是否有权限
 */
export function hasPermission(role: string, permission: string): boolean {
  const permissions = ROLE_PERMISSIONS[role] || []

  // 管理员拥有所有权限
  if (permissions.includes('*')) {
    return true
  }

  return permissions.includes(permission)
}

/**
 * 获取角色的所有权限
 * @param role 角色
 * @returns 权限列表
 */
export function getRolePermissions(role: string): string[] {
  return ROLE_PERMISSIONS[role] || []
}
