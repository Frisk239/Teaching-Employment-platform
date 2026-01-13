package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.CourseRecord;

import java.util.List;

/**
 * 课程学习记录Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface CourseRecordService extends IService<CourseRecord> {

    /**
     * 分页查询学习记录
     */
    IPage<CourseRecord> getRecordPage(Integer current, Integer size, Long courseId, Long studentId, String status);

    /**
     * 根据学员ID查询学习记录
     */
    List<CourseRecord> getRecordsByStudentId(Long studentId);

    /**
     * 根据课程ID查询学习记录
     */
    List<CourseRecord> getRecordsByCourseId(Long courseId);

    /**
     * 查询学员在指定课程的学习记录
     */
    CourseRecord getRecordByStudentAndCourse(Long studentId, Long courseId);

    /**
     * 更新学习进度
     */
    boolean updateProgress(Long studentId, Long courseId, Double progress);

    /**
     * 更新观看时长
     */
    boolean updateWatchedDuration(Long studentId, Long courseId, Long duration);

    /**
     * 更新平均作业分
     */
    boolean updateAvgHomeworkScore(Long studentId, Long courseId, Double score);

    /**
     * 更新考试成绩
     */
    boolean updateExamScore(Long studentId, Long courseId, Double score);

    /**
     * 开始学习课程
     */
    boolean startLearning(Long studentId, Long courseId, Integer totalChapters);

    /**
     * 完成课程学习
     */
    boolean completeCourse(Long studentId, Long courseId);

    /**
     * 获取课程学习进度统计
     *
     * @param courseId 课程ID
     * @return 包含总学员数、已完成人数、进行中人数等统计信息
     */
    java.util.Map<String, Object> getCourseProgressStatistics(Long courseId);

    /**
     * 获取学员学习进度统计
     *
     * @param studentId 学员ID
     * @return 包含总课程数、已完成课程数、总学习时长等统计信息
     */
    java.util.Map<String, Object> getStudentProgressStatistics(Long studentId);

    /**
     * 获取学习时长排行
     *
     * @param courseId 课程ID (可选,为null时统计所有课程)
     * @param limit 返回记录数
     * @return 学习时长排行列表
     */
    java.util.List<java.util.Map<String, Object>> getTopLearnersByDuration(Long courseId, Integer limit);
}
