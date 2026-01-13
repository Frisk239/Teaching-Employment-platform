package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.CourseStudent;

import java.util.List;

/**
 * 课程学员Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface CourseStudentService extends IService<CourseStudent> {

    /**
     * 分页查询课程学员列表
     */
    IPage<CourseStudent> getCourseStudentPage(Integer current, Integer size, Long courseId, Long studentId, Integer status);

    /**
     * 根据课程ID查询学员列表
     */
    List<CourseStudent> getStudentsByCourseId(Long courseId);

    /**
     * 根据学员ID查询课程列表
     */
    List<CourseStudent> getCoursesByStudentId(Long studentId);

    /**
     * 统计课程学员数量
     */
    Integer countStudentsByCourseId(Long courseId);

    /**
     * 检查学员是否已选课
     */
    boolean checkEnrollment(Long courseId, Long studentId);

    /**
     * 学员选课
     */
    boolean enrollCourse(Long courseId, Long studentId);

    /**
     * 学员退课
     */
    boolean dropCourse(Long courseId, Long studentId);

    /**
     * 更新学习进度
     */
    boolean updateProgress(Long courseId, Long studentId, Double progress);
}
