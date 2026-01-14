package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Teacher;

/**
 * 教师Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 分页查询教师列表
     */
    IPage<Teacher> getTeacherPage(Integer current, Integer size, Long schoolId, String keyword);

    /**
     * 查询所有教师列表(用于下拉框)
     */
    java.util.List<Teacher> getTeacherList(Long schoolId);

    /**
     * 根据用户ID查询教师信息
     */
    Teacher getTeacherByUserId(Long userId);

    /**
     * 获取教师工作负载统计
     *
     * @param teacherId 教师ID
     * @return 包含课程数量、总课时等统计信息的Map
     */
    java.util.Map<String, Object> getTeacherWorkload(Long teacherId);

    /**
     * 获取教师的课程列表
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    java.util.List<com.teaching.employment.entity.Course> getTeacherCourses(Long teacherId);
}
