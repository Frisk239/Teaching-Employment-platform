package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Homework;
import com.teaching.employment.entity.HomeworkSubmission;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.HomeworkSubmissionMapper;
import com.teaching.employment.service.HomeworkService;
import com.teaching.employment.service.HomeworkSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 作业提交Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class HomeworkSubmissionServiceImpl extends ServiceImpl<HomeworkSubmissionMapper, HomeworkSubmission>
        implements HomeworkSubmissionService {

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Lazy
    @Autowired
    private HomeworkService homeworkService;

    @Override
    public IPage<HomeworkSubmission> getSubmissionPage(Integer current, Integer size, Long homeworkId, Long studentId, String status) {
        Page<HomeworkSubmission> page = new Page<>(current, size);
        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();

        if (homeworkId != null) {
            wrapper.eq(HomeworkSubmission::getHomeworkId, homeworkId);
        }
        if (studentId != null) {
            wrapper.eq(HomeworkSubmission::getStudentId, studentId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(HomeworkSubmission::getStatus, status);
        }

        wrapper.orderByDesc(HomeworkSubmission::getSubmitTime);

        IPage<HomeworkSubmission> result = homeworkSubmissionMapper.selectPage(page, wrapper);

        // 填充作业信息和判断是否逾期
        result.getRecords().forEach(submission -> {
            Homework homework = homeworkService.getById(submission.getHomeworkId());
            submission.setHomework(homework);

            // 判断是否逾期提交
            if (submission.getSubmitTime() != null && homework.getDeadline() != null) {
                submission.setIsLate(submission.getSubmitTime().isAfter(homework.getDeadline()));
            }
        });

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitHomework(Long homeworkId, Long studentId, String content, String attachmentUrl) {
        // 检查作业是否存在
        Homework homework = homeworkService.getById(homeworkId);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        // 检查作业是否已发布
        if (!"published".equals(homework.getStatus())) {
            throw new BusinessException("作业未发布，无法提交");
        }

        // 检查是否已截止
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            throw new BusinessException("作业已截止，无法提交");
        }

        // 检查是否已经提交过
        HomeworkSubmission existing = getSubmissionByHomeworkAndStudent(homeworkId, studentId);
        if (existing != null && !"returned".equals(existing.getStatus())) {
            throw new BusinessException("您已提交过该作业，请等待批改");
        }

        // 创建提交记录
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setContent(content);
        submission.setAttachmentUrl(attachmentUrl);
        submission.setStatus("submitted");
        submission.setSubmitTime(LocalDateTime.now());

        return homeworkSubmissionMapper.insert(submission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeSubmission(Long submissionId, Integer score, String comment) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }

        if (!"submitted".equals(submission.getStatus()) && !"returned".equals(submission.getStatus())) {
            throw new BusinessException("该提交已被批改或无效");
        }

        // 获取作业满分
        Homework homework = homeworkService.getById(submission.getHomeworkId());
        if (homework.getMaxScore() != null && score > homework.getMaxScore()) {
            throw new BusinessException("分数不能超过满分");
        }

        submission.setScore(score);
        submission.setComment(comment);
        submission.setStatus("graded");
        submission.setGradeTime(LocalDateTime.now());

        return homeworkSubmissionMapper.updateById(submission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnSubmission(Long submissionId, String comment) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }

        if (!"submitted".equals(submission.getStatus())) {
            throw new BusinessException("只能退回已提交的作业");
        }

        submission.setComment(comment);
        submission.setStatus("returned");

        return homeworkSubmissionMapper.updateById(submission) > 0;
    }

    @Override
    public HomeworkSubmission getSubmissionByHomeworkAndStudent(Long homeworkId, Long studentId) {
        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmission::getHomeworkId, homeworkId);
        wrapper.eq(HomeworkSubmission::getStudentId, studentId);
        wrapper.orderByDesc(HomeworkSubmission::getSubmitTime);
        wrapper.last("LIMIT 1");

        return homeworkSubmissionMapper.selectOne(wrapper);
    }
}
