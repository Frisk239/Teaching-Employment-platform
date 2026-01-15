-- 更新笔试表结构,添加缺失字段
USE teaching_employment_platform;

-- 先删除旧表重建
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS t_written_test;

CREATE TABLE t_written_test (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '笔试ID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    position_id BIGINT NOT NULL COMMENT '职位ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    test_url VARCHAR(500) COMMENT '考试链接或试卷URL',
    duration INT COMMENT '考试时长(分钟)',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    score INT COMMENT '得分',
    total_score INT COMMENT '总分',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态:pending-待考试 ongoing-考试中 completed-已完成 missed-缺席',
    comment TEXT COMMENT '评语',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除:1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (application_id) REFERENCES t_job_application(id),
    INDEX idx_application_id (application_id),
    INDEX idx_position_id (position_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔试表';

SET FOREIGN_KEY_CHECKS = 1;

-- 验证表结构
DESCRIBE t_written_test;
