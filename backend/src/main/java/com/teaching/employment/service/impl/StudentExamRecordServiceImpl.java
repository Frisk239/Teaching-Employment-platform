package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Exam;
import com.teaching.employment.entity.Question;
import com.teaching.employment.entity.StudentExamRecord;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.StudentExamRecordMapper;
import com.teaching.employment.service.ExamService;
import com.teaching.employment.service.QuestionService;
import com.teaching.employment.service.StudentExamRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学员考试记录Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class StudentExamRecordServiceImpl extends ServiceImpl<StudentExamRecordMapper, StudentExamRecord> implements StudentExamRecordService {

    private final StudentExamRecordMapper studentExamRecordMapper;
    private final ExamService examService;
    private final QuestionService questionService;

    @Override
    public IPage<StudentExamRecord> getRecordPage(Integer current, Integer size, Long examId, Long studentId, String status) {
        Page<StudentExamRecord> page = new Page<>(current, size);
        return studentExamRecordMapper.selectPage(page, new LambdaQueryWrapper<StudentExamRecord>()
                .eq(examId != null, StudentExamRecord::getExamId, examId)
                .eq(studentId != null, StudentExamRecord::getStudentId, studentId)
                .eq(StrUtil.isNotBlank(status), StudentExamRecord::getStatus, status)
                .orderByDesc(StudentExamRecord::getCreateTime));
    }

    @Override
    public List<StudentExamRecord> getRecordsByExamId(Long examId) {
        return studentExamRecordMapper.selectByExamId(examId);
    }

    @Override
    public List<StudentExamRecord> getRecordsByStudentId(Long studentId) {
        return studentExamRecordMapper.selectByStudentId(studentId);
    }

    @Override
    public StudentExamRecord getRecordByExamAndStudent(Long examId, Long studentId) {
        return studentExamRecordMapper.selectByExamAndStudent(examId, studentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startExam(Long examId, Long studentId) {
        // 1. 检查考试是否存在
        Exam exam = examService.getById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        // 2. 检查考试状态
        if (!"published".equals(exam.getStatus()) && !"started".equals(exam.getStatus())) {
            throw new BusinessException("考试未开始");
        }

        // 3. 检查是否已经参加过考试
        StudentExamRecord existingRecord = getRecordByExamAndStudent(examId, studentId);
        if (existingRecord != null && !"not_started".equals(existingRecord.getStatus())) {
            throw new BusinessException("已经参加过该考试");
        }

        // 4. 创建考试记录
        StudentExamRecord record = new StudentExamRecord();
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setStartTime(LocalDateTime.now());
        record.setStatus("in_progress");

        // 如果已有记录,更新;否则创建新记录
        if (existingRecord != null) {
            record.setId(existingRecord.getId());
            return studentExamRecordMapper.updateById(record) > 0;
        } else {
            return studentExamRecordMapper.insert(record) > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitAnswer(Long examId, Long studentId, String answers) {
        // 1. 获取考试记录
        StudentExamRecord record = getRecordByExamAndStudent(examId, studentId);
        if (record == null) {
            throw new BusinessException("考试记录不存在");
        }

        // 2. 检查考试状态
        if (!"in_progress".equals(record.getStatus())) {
            throw new BusinessException("考试已提交或未开始");
        }

        // 3. 更新答案和状态
        record.setAnswers(answers);
        record.setEndTime(LocalDateTime.now());
        record.setStatus("submitted");

        // 计算用时(秒)
        if (record.getStartTime() != null) {
            long duration = java.time.Duration.between(record.getStartTime(), record.getEndTime()).getSeconds();
            record.setDuration((int) duration);
        }

        return studentExamRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeExam(Long recordId) {
        // 1. 获取考试记录
        StudentExamRecord record = studentExamRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("考试记录不存在");
        }

        // 2. 获取考试和题目信息
        Exam exam = examService.getById(record.getExamId());
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        List<Question> questions = questionService.getQuestionsByExamId(record.getExamId());
        if (questions.isEmpty()) {
            throw new BusinessException("题目不存在");
        }

        // 3. 判卷逻辑(简化版)
        double totalScore = 0.0;
        String studentAnswers = record.getAnswers();

        // 这里需要解析学生答案并判分
        // 简化处理: 假设answers是JSON格式,包含每道题的答案
        // 实际应该根据question_type判分
        // TODO: 实现完整的判分逻辑

        // 临时设置分数
        record.setScore(BigDecimal.valueOf(totalScore));

        // 4. 判断是否及格
        if (exam.getPassScore() != null) {
            record.setIsPassed(totalScore >= exam.getPassScore().intValue() ? 1 : 0);
        }

        // 5. 更新状态
        record.setStatus("graded");

        return studentExamRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchGradeExams(Long examId) {
        List<StudentExamRecord> records = getRecordsByExamId(examId);

        for (StudentExamRecord record : records) {
            if ("submitted".equals(record.getStatus())) {
                gradeExam(record.getId());
            }
        }

        return true;
    }
}
