package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Classroom;

import java.util.List;

/**
 * 教室Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface ClassroomService extends IService<Classroom> {

    /**
     * 分页查询教室列表
     */
    IPage<Classroom> getClassroomPage(Integer current, Integer size, Long schoolId, String keyword);

    /**
     * 查询所有教室(用于下拉框)
     */
    List<Classroom> getClassroomList(Long schoolId);

    /**
     * 获取教室使用统计
     *
     * @param classroomId 教室ID
     * @return 包含课程数量、总学员数等统计信息
     */
    java.util.Map<String, Object> getClassroomUsageStatistics(Long classroomId);

    /**
     * 获取教室的课程列表
     *
     * @param classroomId 教室ID
     * @return 课程列表
     */
    java.util.List<com.teaching.employment.entity.Course> getClassroomCourses(Long classroomId);

    /**
     * 查询可用教室
     *
     * @param schoolId 学校ID
     * @param minCapacity 最小容纳人数
     * @param hasProjector 是否需要投影仪
     * @param hasComputer 是否需要电脑
     * @return 可用教室列表
     */
    java.util.List<Classroom> getAvailableClassrooms(Long schoolId, Integer minCapacity,
                                                      Integer hasProjector, Integer hasComputer);
}
