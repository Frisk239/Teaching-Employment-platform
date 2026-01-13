package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Classroom;
import com.teaching.employment.entity.Course;
import com.teaching.employment.mapper.ClassroomMapper;
import com.teaching.employment.service.ClassroomService;
import com.teaching.employment.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教室Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl extends ServiceImpl<ClassroomMapper, Classroom> implements ClassroomService {

    private final ClassroomMapper classroomMapper;
    private final CourseService courseService;

    @Override
    public IPage<Classroom> getClassroomPage(Integer current, Integer size, Long schoolId, String keyword) {
        Page<Classroom> page = new Page<>(current, size);
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();

        if (schoolId != null) {
            wrapper.eq(Classroom::getSchoolId, schoolId);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Classroom::getClassroomName, keyword)
                    .or().like(Classroom::getClassroomNo, keyword));
        }

        wrapper.orderByDesc(Classroom::getCreateTime);
        return classroomMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Classroom> getClassroomList(Long schoolId) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        if (schoolId != null) {
            wrapper.eq(Classroom::getSchoolId, schoolId);
        }
        wrapper.orderByAsc(Classroom::getClassroomNo);
        return classroomMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getClassroomUsageStatistics(Long classroomId) {
        Map<String, Object> statistics = new HashMap<>();

        // 获取教室信息
        Classroom classroom = getById(classroomId);
        if (classroom != null) {
            statistics.put("classroomId", classroomId);
            statistics.put("classroomName", classroom.getClassroomName());
            statistics.put("classroomNo", classroom.getClassroomNo());
            statistics.put("capacity", classroom.getCapacity());
        }

        // 获取教室的所有课程
        List<Course> courses = courseService.getCoursesByClassroomId(classroomId);

        // 统计课程数量
        int totalCourses = courses.size();

        // 统计不同状态的课程数量
        long pendingCourses = courses.stream()
                .filter(course -> "pending".equals(course.getStatus()))
                .count();

        long ongoingCourses = courses.stream()
                .filter(course -> "ongoing".equals(course.getStatus()))
                .count();

        long completedCourses = courses.stream()
                .filter(course -> "completed".equals(course.getStatus()))
                .count();

        // 统计总学员数
        int totalStudents = courses.stream()
                .mapToInt(course -> course.getCurrentStudents() != null ? course.getCurrentStudents() : 0)
                .sum();

        // 计算使用率
        double utilizationRate = 0.0;
        if (classroom != null && classroom.getCapacity() != null && classroom.getCapacity() > 0) {
            // 按课程数计算使用率(假设一个教室每天最多8节课)
            utilizationRate = (double) totalCourses / 8.0 * 100;
            if (utilizationRate > 100) {
                utilizationRate = 100.0;
            }
        }

        statistics.put("totalCourses", totalCourses);
        statistics.put("pendingCourses", pendingCourses);
        statistics.put("ongoingCourses", ongoingCourses);
        statistics.put("completedCourses", completedCourses);
        statistics.put("totalStudents", totalStudents);
        statistics.put("utilizationRate", utilizationRate);

        return statistics;
    }

    @Override
    public List<Course> getClassroomCourses(Long classroomId) {
        return courseService.getCoursesByClassroomId(classroomId);
    }

    @Override
    public List<Classroom> getAvailableClassrooms(Long schoolId, Integer minCapacity,
                                                   Integer hasProjector, Integer hasComputer) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();

        // 只查询可用状态的教室
        wrapper.eq(Classroom::getStatus, 1);

        // 按学校筛选
        if (schoolId != null) {
            wrapper.eq(Classroom::getSchoolId, schoolId);
        }

        // 按容量筛选
        if (minCapacity != null && minCapacity > 0) {
            wrapper.ge(Classroom::getCapacity, minCapacity);
        }

        // 按投影仪筛选
        if (hasProjector != null) {
            wrapper.eq(Classroom::getHasProjector, hasProjector);
        }

        // 按电脑筛选
        if (hasComputer != null) {
            wrapper.eq(Classroom::getHasComputer, hasComputer);
        }

        wrapper.orderByAsc(Classroom::getClassroomNo);
        return classroomMapper.selectList(wrapper);
    }
}
