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
    IPage<Exam> getExamPage(Integer current, Integer size, String examType, Long refId, String status, String keyword);

    /**
     * 根据关联ID查询考试列表（课程或企业职位）
     */
    List<Exam> getExamsByRefId(String examType, Long refId);

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
     * 结束考试
     */
    boolean endExam(Long examId);

    /**
     * 删除考试
     */
    boolean deleteExam(Long examId);
}
