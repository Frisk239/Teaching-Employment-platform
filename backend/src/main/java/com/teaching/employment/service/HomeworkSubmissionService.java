package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.HomeworkSubmission;

/**
 * 作业提交Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface HomeworkSubmissionService extends IService<HomeworkSubmission> {

    /**
     * 分页查询作业提交列表
     */
    IPage<HomeworkSubmission> getSubmissionPage(Integer current, Integer size, Long homeworkId, Long studentId, String status);

    /**
     * 学生提交作业
     */
    boolean submitHomework(Long homeworkId, Long studentId, String content, String attachmentUrl);

    /**
     * 教师批改作业
     */
    boolean gradeSubmission(Long submissionId, Integer score, String comment);

    /**
     * 教师退回作业
     */
    boolean returnSubmission(Long submissionId, String comment);

    /**
     * 学生查询自己的提交记录
     */
    HomeworkSubmission getSubmissionByHomeworkAndStudent(Long homeworkId, Long studentId);
}
