package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.StudentAnswer;

import java.util.List;

public interface StudentAnswerService extends IService<StudentAnswer> {

    /**
     * 根据考试记录ID获取答题列表
     */
    List<StudentAnswer> getAnswersByExamRecordId(Long examRecordId);

    /**
     * 批量保存学生答案
     */
    boolean batchSaveAnswers(List<StudentAnswer> answers);

    /**
     * 批改学生答案
     */
    boolean gradeAnswer(Long answerId, String comment, Integer isCorrect);

    /**
     * 分页查询学生答题记录
     */
    IPage<StudentAnswer> getAnswerPage(Integer current, Integer size, Long examRecordId);
}
