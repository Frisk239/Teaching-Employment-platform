package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.StudentExamRecord;

import java.util.List;

/**
 * 学员考试记录Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface StudentExamRecordService extends IService<StudentExamRecord> {

    /**
     * 分页查询考试记录
     */
    IPage<StudentExamRecord> getRecordPage(Integer current, Integer size, Long examId, Long studentId, String status);

    /**
     * 根据考试ID查询所有学员成绩
     */
    List<StudentExamRecord> getRecordsByExamId(Long examId);

    /**
     * 根据学员ID查询考试记录
     */
    List<StudentExamRecord> getRecordsByStudentId(Long studentId);

    /**
     * 查询学员在指定考试中的记录
     */
    StudentExamRecord getRecordByExamAndStudent(Long examId, Long studentId);

    /**
     * 开始考试
     */
    boolean startExam(Long examId, Long studentId);

    /**
     * 提交答案
     */
    boolean submitAnswer(Long examId, Long studentId, String answers);

    /**
     * 批改试卷(自动判分)
     */
    boolean gradeExam(Long recordId);

    /**
     * 批量批改试卷
     */
    boolean batchGradeExams(Long examId);
}
