package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Homework;

import java.util.Map;

/**
 * 作业Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface HomeworkService extends IService<Homework> {

    /**
     * 分页查询作业列表
     */
    IPage<Homework> getHomeworkPage(Integer current, Integer size, Long courseId, Long teacherId, String status, String keyword);

    /**
     * 根据课程ID查询作业列表
     */
    IPage<Homework> getHomeworkByCourseId(Integer current, Integer size, Long courseId);

    /**
     * 根据学生ID查询作业列表（学生端查看待提交作业）
     */
    IPage<Homework> getHomeworkByStudentId(Integer current, Integer size, Long studentId);

    /**
     * 发布作业
     */
    boolean publishHomework(Long homeworkId);

    /**
     * 关闭作业（截止）
     */
    boolean closeHomework(Long homeworkId);

    /**
     * 获取作业统计信息（提交数、未提交数）
     */
    Map<String, Object> getHomeworkStats(Long homeworkId);
}
