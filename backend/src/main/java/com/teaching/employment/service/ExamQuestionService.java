package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.ExamQuestion;

import java.util.List;

public interface ExamQuestionService extends IService<ExamQuestion> {

    /**
     * 根据试卷ID获取题目列表
     */
    List<ExamQuestion> getQuestionsByExamId(Long examId);

    /**
     * 根据试卷ID获取题目ID列表
     *
     * @param examId 试卷ID
     * @return 题目ID列表
     */
    List<Long> getQuestionIdsByExamId(Long examId);

    /**
     * 添加题目到试卷
     */
    boolean addQuestionToExam(ExamQuestion examQuestion);

    /**
     * 批量添加试卷题目
     */
    boolean batchAddQuestions(Long examId, List<ExamQuestion> examQuestions);

    /**
     * 批量添加试卷题目（新方法）
     */
    boolean batchAddQuestions(List<ExamQuestion> examQuestions);

    /**
     * 根据试卷ID删除所有题目
     */
    boolean removeByExamId(Long examId);

    /**
     * 清空试卷的所有题目
     */
    boolean clearQuestionsByExamId(Long examId);
}
