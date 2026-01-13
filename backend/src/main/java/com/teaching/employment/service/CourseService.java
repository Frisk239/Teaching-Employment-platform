package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Course;

import java.util.List;

/**
 * 课程Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程列表
     */
    IPage<Course> getCoursePage(Integer current, Integer size, Long schoolId, Long teacherId, String status, String keyword);

    /**
     * 根据学生ID查询课程表(学生端查看课程)
     */
    List<Course> getCoursesByStudentId(Long studentId);

    /**
     * 根据教师ID查询课程列表(教师端查看自己的课程)
     */
    List<Course> getCoursesByTeacherId(Long teacherId);

    /**
     * 根据教室ID查询课程列表
     *
     * @param classroomId 教室ID
     * @return 课程列表
     */
    List<Course> getCoursesByClassroomId(Long classroomId);

    /**
     * 添加学员到课程
     */
    boolean addStudentToCourse(Long courseId, Long studentId);

    /**
     * 批量添加学员到课程
     */
    boolean batchAddStudentsToCourse(Long courseId, List<Long> studentIds);

    /**
     * 从课程中移除学员
     */
    boolean removeStudentFromCourse(Long courseId, Long studentId);
}
