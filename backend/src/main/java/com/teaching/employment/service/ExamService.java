package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Exam;

import java.util.List;

/**
 * 考试Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface ExamService extends IService<Exam> {

    /**
     * 分页查询考试列表
     */
    IPage<Exam> getExamPage(Integer current, Integer size, Long courseId, String examType, String status, String keyword);

    /**
     * 根据课程ID查询考试列表
     */
    List<Exam> getExamsByCourseId(Long courseId);

    /**
     * 查询已发布的考试列表
     */
    List<Exam> getPublishedExams();

    /**
     * 查询进行中的考试列表
     */
    List<Exam> getOngoingExams();

    /**
     * 发布考试
     */
    boolean publishExam(Long examId);

    /**
     * 开始考试
     */
    boolean startExam(Long examId);

    /**
     * 结束考试
     */
    boolean endExam(Long examId);

    /**
     * 删除考试(软删除)
     */
    boolean deleteExam(Long examId);
}
