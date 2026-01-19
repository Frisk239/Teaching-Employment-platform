-- 创建用户活动记录表
CREATE TABLE IF NOT EXISTS t_user_activity (
    id BIGINT AUTO_INCREMENT COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型：login-登录 logout-登出 profile_update-资料修改 password_change-密码修改 course_enroll-课程注册 homework_submit-作业提交 report_submit-日报提交 job_apply-职位申请 test_complete-笔试完成 interview_attend-面试通知 offer_received-收到offer',
    title VARCHAR(200) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    related_id BIGINT COMMENT '关联对象ID（如课程ID、作业ID等）',
    related_type VARCHAR(50) COMMENT '关联对象类型',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '活动时间',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户活动记录表';
