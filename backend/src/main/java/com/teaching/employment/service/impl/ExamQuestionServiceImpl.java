package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.ExamQuestion;
import com.teaching.employment.entity.QuestionBank;
import com.teaching.employment.mapper.ExamQuestionMapper;
import com.teaching.employment.service.ExamQuestionService;
import com.teaching.employment.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion>
        implements ExamQuestionService {

    private final QuestionBankService questionBankService;

    @Override
    public List<ExamQuestion> getQuestionsByExamId(Long examId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        wrapper.orderByAsc(ExamQuestion::getSortOrder);
        List<ExamQuestion> examQuestions = this.list(wrapper);

        // 填充题目详情
        if (!examQuestions.isEmpty()) {
            List<Long> questionIds = examQuestions.stream()
                    .map(ExamQuestion::getQuestionId)
                    .collect(Collectors.toList());

            List<QuestionBank> questions = questionBankService.listByIds(questionIds);
            Map<Long, QuestionBank> questionMap = questions.stream()
                    .collect(Collectors.toMap(QuestionBank::getId, q -> q));

            examQuestions.forEach(eq -> {
                QuestionBank question = questionMap.get(eq.getQuestionId());
                if (question != null) {
                    eq.setQuestionText(question.getQuestionText());
                    eq.setQuestionType(question.getQuestionType());
                    eq.setKnowledgePoint(question.getKnowledgePoint());
                    eq.setDifficulty(question.getDifficulty());
                    eq.setOptions(question.getOptions());
                    eq.setCorrectAnswer(question.getCorrectAnswer());
                    eq.setAnalysis(question.getAnalysis());
                }
            });
        }

        return examQuestions;
    }

    @Override
    public List<Long> getQuestionIdsByExamId(Long examId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        wrapper.select(ExamQuestion::getQuestionId);
        return this.listObjs(wrapper, obj -> (Long) obj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addQuestionToExam(ExamQuestion examQuestion) {
        return this.save(examQuestion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddQuestions(Long examId, List<ExamQuestion> examQuestions) {
        if (examQuestions == null || examQuestions.isEmpty()) {
            return false;
        }

        // 设置试卷ID
        examQuestions.forEach(q -> q.setExamId(examId));

        return this.saveBatch(examQuestions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddQuestions(List<ExamQuestion> examQuestions) {
        if (examQuestions == null || examQuestions.isEmpty()) {
            return false;
        }
        return this.saveBatch(examQuestions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByExamId(Long examId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        return this.remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearQuestionsByExamId(Long examId) {
        return removeByExamId(examId);
    }
}

