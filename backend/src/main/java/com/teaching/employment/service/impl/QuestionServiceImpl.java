package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Question;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.QuestionMapper;
import com.teaching.employment.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 题目Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final QuestionMapper questionMapper;

    @Override
    public List<Question> getQuestionsByExamId(Long examId) {
        return questionMapper.selectByExamId(examId);
    }

    @Override
    public List<Question> getRandomQuestions(Long examId, Integer count) {
        return questionMapper.selectRandomQuestions(examId, count);
    }

    @Override
    public Integer countByExamId(Long examId) {
        return questionMapper.countByExamId(examId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new BusinessException("题目列表不能为空");
        }

        for (Question question : questions) {
            questionMapper.insert(question);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestionsByExamId(Long examId) {
        // 逻辑删除所有该考试的题目
        List<Question> questions = getQuestionsByExamId(examId);
        for (Question question : questions) {
            question.setIsDeleted(1);
            questionMapper.updateById(question);
        }
        return true;
    }
}
