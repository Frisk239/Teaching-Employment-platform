package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.dto.StudentAnswerRequest;
import com.teaching.employment.entity.ExamRecord;

import java.util.List;

/**
 * 考试记录Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
public interface ExamRecordService extends IService<ExamRecord> {

    /**
     * 分页查询考试记录
     */
    IPage<ExamRecord> getExamRecordPage(Integer current, Integer size,
                                         Long examId, Long studentId,
                                         String gradingStatus, String status);

    /**
     * 开始考试
     */
    ExamRecord startExam(Long examId, Long studentId);

    /**
     * 提交试卷
     */
    boolean submitExam(Long examRecordId);

    /**
     * 自动评阅客观题
     */
    boolean autoGradeObjectiveQuestions(Long examRecordId);

    /**
     * 教师评阅主观题
     */
    boolean gradeSubjectiveQuestions(Long examRecordId, Long graderId);

    /**
     * 批量保存学生答案
     * @param examRecordId 考试记录ID
     * @param answers 答案列表
     * @return 保存的答案数量
     */
    int saveAnswers(Long examRecordId, List<StudentAnswerRequest> answers);
}
