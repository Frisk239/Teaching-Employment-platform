package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Question;

import java.util.List;

/**
 * 题目Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface QuestionService extends IService<Question> {

    /**
     * 根据考试ID查询题目列表
     */
    List<Question> getQuestionsByExamId(Long examId);

    /**
     * 随机获取题目
     */
    List<Question> getRandomQuestions(Long examId, Integer count);

    /**
     * 统计考试题目数量
     */
    Integer countByExamId(Long examId);

    /**
     * 批量添加题目
     */
    boolean batchAddQuestions(List<Question> questions);

    /**
     * 批量删除题目(根据考试ID)
     */
    boolean deleteQuestionsByExamId(Long examId);
}
