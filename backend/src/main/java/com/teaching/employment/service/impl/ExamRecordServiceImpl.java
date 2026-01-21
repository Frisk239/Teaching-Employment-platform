package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.dto.StudentAnswerRequest;
import com.teaching.employment.entity.Exam;
import com.teaching.employment.entity.ExamRecord;
import com.teaching.employment.entity.ExamQuestion;
import com.teaching.employment.entity.QuestionBank;
import com.teaching.employment.entity.StudentAnswer;
import com.teaching.employment.mapper.ExamRecordMapper;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.service.ExamQuestionService;
import com.teaching.employment.service.ExamRecordService;
import com.teaching.employment.service.ExamService;
import com.teaching.employment.service.QuestionBankService;
import com.teaching.employment.service.StudentAnswerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 考试记录Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Service
@RequiredArgsConstructor
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    // 常量定义
    private static final Integer SHUFFLE_ENABLED = 1;
    private static final Integer PASSED_NO = 0;
    private static final String GRADING_STATUS_PENDING = "pending";
    private static final String EXAM_STATUS_TAKING = "taking";
    private static final String EXAM_STATUS_PUBLISHED = "published";

    private final ExamService examService;
    private final ExamQuestionService examQuestionService;
    private final StudentAnswerService studentAnswerService;
    private final QuestionBankService questionBankService;
    private final ObjectMapper objectMapper;

    @Override
    public IPage<ExamRecord> getExamRecordPage(Integer current, Integer size,
                                                Long examId, Long studentId,
                                                String gradingStatus, String status) {
        Page<ExamRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();

        if (examId != null) {
            wrapper.eq(ExamRecord::getExamId, examId);
        }
        if (studentId != null) {
            wrapper.eq(ExamRecord::getStudentId, studentId);
        }
        if (gradingStatus != null) {
            wrapper.eq(ExamRecord::getGradingStatus, gradingStatus);
        }
        if (status != null) {
            wrapper.eq(ExamRecord::getStatus, status);
        }

        wrapper.orderByDesc(ExamRecord::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecord startExam(Long examId, Long studentId) {
        // 检查考试是否存在且有效
        Exam exam = examService.getById(examId);
        if (exam == null) {
            throw new BusinessException("试卷不存在");
        }
        if (!EXAM_STATUS_PUBLISHED.equals(exam.getStatus())) {
            throw new BusinessException("试卷未发布");
        }
        if (exam.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("考试尚未开始");
        }
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("考试已结束");
        }

        // 检查是否已经参加考试
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
               .eq(ExamRecord::getStudentId, studentId)
               .in(ExamRecord::getStatus, EXAM_STATUS_TAKING, "submitted");
        ExamRecord existingRecord = this.getOne(wrapper);
        if (existingRecord != null) {
            throw new BusinessException("已经参加过该考试");
        }

        // 获取试卷题目
        LambdaQueryWrapper<ExamQuestion> eqWrapper = new LambdaQueryWrapper<>();
        eqWrapper.eq(ExamQuestion::getExamId, examId)
                 .orderByAsc(ExamQuestion::getSortOrder);
        List<ExamQuestion> examQuestions = examQuestionService.list(eqWrapper);

        if (examQuestions.isEmpty()) {
            throw new BusinessException("试卷中没有题目");
        }

        // 批量查询题库详情
        List<Long> questionIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .collect(Collectors.toList());

        // 验证所有题目都存在
        long existCount = questionBankService.count(
                new LambdaQueryWrapper<QuestionBank>().in(QuestionBank::getId, questionIds)
        );

        if (existCount != questionIds.size()) {
            throw new BusinessException("试卷中包含已删除的题目");
        }

        // 批量获取题目详情
        List<QuestionBank> questionBanks = questionBankService.list(
                new LambdaQueryWrapper<QuestionBank>().in(QuestionBank::getId, questionIds)
        );

        // 构建题目Map以提高查询效率
        Map<Long, QuestionBank> questionMap = questionBanks.stream()
                .collect(Collectors.toMap(QuestionBank::getId, Function.identity()));

        // 生成题目快照
        List<Map<String, Object>> questionsSnapshot = new ArrayList<>();
        int displayOrder = 1;

        for (ExamQuestion examQuestion : examQuestions) {
            QuestionBank questionBank = questionMap.get(examQuestion.getQuestionId());

            Map<String, Object> questionData = new HashMap<>();
            questionData.put("id", questionBank.getId());
            questionData.put("questionType", questionBank.getQuestionType());
            questionData.put("questionText", questionBank.getQuestionText());
            questionData.put("score", examQuestion.getQuestionScore());
            questionData.put("order", displayOrder++);

            // 仅客观题有选项，需要转换为对象格式以便前端正确解析
            if (questionBank.getOptions() != null && !questionBank.getOptions().isEmpty()) {
                try {
                    // 尝试将options从数组格式转换为对象格式
                    // 例如：["A. xxx", "B. xxx"] -> {"A": "xxx", "B": "xxx"}
                    String optionsStr = questionBank.getOptions();
                    if (optionsStr.startsWith("[")) {
                        // 数组格式，需要转换
                        List<String> optionsList = objectMapper.readValue(optionsStr, List.class);
                        Map<String, String> optionsMap = new LinkedHashMap<>();
                        for (String option : optionsList) {
                            // 提取字母和内容，例如 "A. xxx" -> key="A", value="xxx"
                            if (option != null && option.matches("^[A-Z]\\..+")) {
                                String[] parts = option.split("\\.", 2);
                                if (parts.length == 2) {
                                    optionsMap.put(parts[0].trim(), parts[1].trim());
                                }
                            }
                        }
                        questionData.put("options", objectMapper.writeValueAsString(optionsMap));
                    } else {
                        // 已经是对象格式，直接使用
                        questionData.put("options", optionsStr);
                    }
                } catch (Exception e) {
                    // 转换失败，使用原始值
                    questionData.put("options", questionBank.getOptions());
                }
            }

            // 安全：不包含正确答案和答案解析（防作弊）
            questionsSnapshot.add(questionData);
        }

        // 根据试卷设置决定是否打乱题目顺序
        if (SHUFFLE_ENABLED.equals(exam.getShuffleQuestions())) {
            Collections.shuffle(questionsSnapshot);

            // 重新设置显示顺序
            for (int i = 0; i < questionsSnapshot.size(); i++) {
                questionsSnapshot.get(i).put("order", i + 1);
            }
        }

        // 构建题目快照JSON
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("questions", questionsSnapshot);

        String questionsSnapshotJson;
        try {
            questionsSnapshotJson = objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new BusinessException("生成题目快照失败", e);
        }

        // 创建考试记录
        ExamRecord examRecord = new ExamRecord();
        examRecord.setExamId(examId);
        examRecord.setStudentId(studentId);
        examRecord.setStartTime(LocalDateTime.now());
        examRecord.setObjectiveScore(BigDecimal.ZERO);
        examRecord.setSubjectiveScore(BigDecimal.ZERO);
        examRecord.setTotalScore(BigDecimal.ZERO);
        examRecord.setPassed(PASSED_NO);
        examRecord.setGradingStatus(GRADING_STATUS_PENDING);
        examRecord.setStatus(EXAM_STATUS_TAKING);
        examRecord.setQuestionsSnapshot(questionsSnapshotJson);

        this.save(examRecord);
        return examRecord;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitExam(Long examRecordId) {
        ExamRecord examRecord = this.getById(examRecordId);
        if (examRecord == null) {
            throw new BusinessException("考试记录不存在");
        }
        if (!EXAM_STATUS_TAKING.equals(examRecord.getStatus())) {
            throw new BusinessException("试卷已提交");
        }

        examRecord.setSubmitTime(LocalDateTime.now());
        examRecord.setStatus("submitted");

        // 自动评阅客观题
        autoGradeObjectiveQuestions(examRecordId);

        return this.updateById(examRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoGradeObjectiveQuestions(Long examRecordId) {
        ExamRecord examRecord = this.getById(examRecordId);
        if (examRecord == null) {
            throw new BusinessException("考试记录不存在");
        }

        // 解析题目快照，获取每道题的分数
        Map<Long, BigDecimal> questionScoreMap = new HashMap<>();
        try {
            String snapshotJson = examRecord.getQuestionsSnapshot();
            if (snapshotJson != null && !snapshotJson.isEmpty()) {
                Map<String, Object> snapshot = objectMapper.readValue(snapshotJson, Map.class);
                List<Map<String, Object>> questions = (List<Map<String, Object>>) snapshot.get("questions");
                if (questions != null) {
                    for (Map<String, Object> q : questions) {
                        Integer id = (Integer) q.get("id");
                        Double score = (Double) q.get("score");
                        if (id != null && score != null) {
                            questionScoreMap.put(id.longValue(), new BigDecimal(score.toString()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败，继续使用默认逻辑
        }

        // 获取学生的所有答案
        LambdaQueryWrapper<StudentAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAnswer::getExamRecordId, examRecordId);
        List<StudentAnswer> studentAnswers = studentAnswerService.list(wrapper);

        BigDecimal objectiveScore = BigDecimal.ZERO;
        boolean hasSubjective = false;

        for (StudentAnswer answer : studentAnswers) {
            // 从题库获取题目详情
            QuestionBank question = questionBankService.getById(answer.getQuestionId());
            if (question == null) {
                continue;
            }

            String questionType = question.getQuestionType();

            // 获取该题在试卷中的分值（优先使用快照中的分值）
            BigDecimal questionScore = questionScoreMap.get(answer.getQuestionId());
            if (questionScore == null || questionScore.compareTo(BigDecimal.ZERO) <= 0) {
                // 如果快照中没有分值，使用默认5分
                questionScore = new BigDecimal("5");
            }

            // 客观题自动评分
            if ("single_choice".equals(questionType) ||
                "true_false".equals(questionType)) {

                if (answer.getStudentAnswer().equals(question.getCorrectAnswer())) {
                    // 使用实际分值
                    answer.setScore(questionScore);
                    answer.setIsCorrect(1);
                } else {
                    answer.setScore(BigDecimal.ZERO);
                    answer.setIsCorrect(0);
                }
                studentAnswerService.updateById(answer);
                objectiveScore = objectiveScore.add(answer.getScore());
            } else if ("multiple_choice".equals(questionType)) {
                // 多选题评分逻辑（完全正确才得分）
                if (answer.getStudentAnswer().equals(question.getCorrectAnswer())) {
                    answer.setScore(questionScore);
                    answer.setIsCorrect(1);
                } else {
                    answer.setScore(BigDecimal.ZERO);
                    answer.setIsCorrect(0);
                }
                studentAnswerService.updateById(answer);
                objectiveScore = objectiveScore.add(answer.getScore());
            } else if ("short_answer".equals(questionType)) {
                // 主观题需要教师评阅
                hasSubjective = true;
            }
        }

        // 更新考试记录
        examRecord.setObjectiveScore(objectiveScore);

        if (hasSubjective) {
            // 有主观题，等待教师评阅
            examRecord.setGradingStatus(GRADING_STATUS_PENDING);
            examRecord.setTotalScore(objectiveScore);
        } else {
            // 全部是客观题，直接完成评阅
            examRecord.setSubjectiveScore(BigDecimal.ZERO);
            examRecord.setTotalScore(objectiveScore);
            examRecord.setGradingStatus("graded");
            examRecord.setGradingTime(LocalDateTime.now());

            // 判断是否及格
            Exam exam = examService.getById(examRecord.getExamId());
            if (exam != null && exam.getPassScore() != null && objectiveScore.compareTo(exam.getPassScore()) >= 0) {
                examRecord.setPassed(1);
            } else {
                examRecord.setPassed(PASSED_NO);
            }
        }

        return this.updateById(examRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeSubjectiveQuestions(Long examRecordId, Long graderId) {
        ExamRecord examRecord = this.getById(examRecordId);
        if (examRecord == null) {
            throw new BusinessException("考试记录不存在");
        }

        // 获取所有主观题答案
        LambdaQueryWrapper<StudentAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAnswer::getExamRecordId, examRecordId)
               .eq(StudentAnswer::getQuestionType, "short_answer")
               .isNotNull(StudentAnswer::getScore);
        List<StudentAnswer> studentAnswers = studentAnswerService.list(wrapper);

        // 检查是否所有主观题都已评阅
        if (studentAnswers.isEmpty()) {
            // 没有主观题，直接更新状态
            examRecord.setGradingStatus("graded");
            examRecord.setGradingTime(LocalDateTime.now());
            examRecord.setGraderId(graderId);
            return this.updateById(examRecord);
        }

        // 计算主观题总分
        BigDecimal subjectiveScore = studentAnswers.stream()
            .map(StudentAnswer::getScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 更新考试记录
        examRecord.setSubjectiveScore(subjectiveScore);
        examRecord.setTotalScore(examRecord.getObjectiveScore().add(subjectiveScore));
        examRecord.setGradingStatus("graded");
        examRecord.setGradingTime(LocalDateTime.now());
        examRecord.setGraderId(graderId);

        // 判断是否及格
        Exam exam = examService.getById(examRecord.getExamId());
        if (exam != null && examRecord.getTotalScore().compareTo(exam.getPassScore()) >= 0) {
            examRecord.setPassed(1);
        } else {
            examRecord.setPassed(PASSED_NO);
        }

        return this.updateById(examRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveAnswers(Long examRecordId, List<StudentAnswerRequest> answers) {
        // 1. 验证考试记录是否存在且状态为"taking"
        ExamRecord examRecord = this.getById(examRecordId);
        if (examRecord == null) {
            throw new BusinessException("考试记录不存在");
        }
        if (!EXAM_STATUS_TAKING.equals(examRecord.getStatus())) {
            throw new BusinessException("考试已提交，无法保存答案");
        }

        // 2. 批量查询已存在的答案
        List<Long> questionIds = answers.stream()
            .map(StudentAnswerRequest::getQuestionId)
            .collect(Collectors.toList());

        LambdaQueryWrapper<StudentAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAnswer::getExamRecordId, examRecordId)
               .in(StudentAnswer::getQuestionId, questionIds);
        List<StudentAnswer> existingAnswers = studentAnswerService.list(wrapper);

        // 构建已存在答案的Map (examRecordId + questionId -> StudentAnswer)
        Map<String, StudentAnswer> existingMap = existingAnswers.stream()
            .collect(Collectors.toMap(
                ans -> ans.getExamRecordId() + "_" + ans.getQuestionId(),
                Function.identity()
            ));

        int savedCount = 0;

        // 3. 遍历要保存的答案
        for (StudentAnswerRequest answerReq : answers) {
            String key = examRecordId + "_" + answerReq.getQuestionId();
            StudentAnswer existing = existingMap.get(key);

            if (existing != null) {
                // 更新已存在的答案
                existing.setStudentAnswer(answerReq.getStudentAnswer());
                existing.setUpdateTime(LocalDateTime.now());
                studentAnswerService.updateById(existing);
            } else {
                // 创建新答案
                StudentAnswer newAnswer = new StudentAnswer();
                newAnswer.setExamRecordId(examRecordId);
                newAnswer.setQuestionId(answerReq.getQuestionId());
                newAnswer.setQuestionType(answerReq.getQuestionType());
                newAnswer.setStudentAnswer(answerReq.getStudentAnswer());
                newAnswer.setCreateTime(LocalDateTime.now());
                newAnswer.setUpdateTime(LocalDateTime.now());
                studentAnswerService.save(newAnswer);
            }
            savedCount++;
        }

        return savedCount;
    }
}
