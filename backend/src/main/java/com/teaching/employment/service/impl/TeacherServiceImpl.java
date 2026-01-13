package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.mapper.TeacherMapper;
import com.teaching.employment.service.CourseService;
import com.teaching.employment.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final CourseService courseService;

    @Override
    public IPage<Teacher> getTeacherPage(Integer current, Integer size, Long schoolId, String keyword) {
        Page<Teacher> page = new Page<>(current, size);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();

        if (schoolId != null) {
            wrapper.eq(Teacher::getSchoolId, schoolId);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Teacher::getTeacherNo, keyword)
                    .or().like(Teacher::getRealName, keyword));
        }

        wrapper.orderByDesc(Teacher::getCreateTime);
        return teacherMapper.selectPage(page, wrapper);
    }

    @Override
    public Teacher getTeacherByUserId(Long userId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUserId, userId);
        return teacherMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getTeacherWorkload(Long teacherId) {
        Map<String, Object> workload = new HashMap<>();

        // 获取教师的所有课程
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);

        // 统计课程数量
        int totalCourses = courses.size();

        // 统计总课时
        int totalHours = courses.stream()
                .mapToInt(course -> course.getTotalHours() != null ? course.getTotalHours() : 0)
                .sum();

        // 统计不同状态的课程数量
        long ongoingCourses = courses.stream()
                .filter(course -> "ongoing".equals(course.getStatus()))
                .count();

        long completedCourses = courses.stream()
                .filter(course -> "completed".equals(course.getStatus()))
                .count();

        long pendingCourses = courses.stream()
                .filter(course -> "pending".equals(course.getStatus()))
                .count();

        // 统计总学员数
        int totalStudents = courses.stream()
                .mapToInt(course -> course.getCurrentStudents() != null ? course.getCurrentStudents() : 0)
                .sum();

        workload.put("teacherId", teacherId);
        workload.put("totalCourses", totalCourses);
        workload.put("totalHours", totalHours);
        workload.put("ongoingCourses", ongoingCourses);
        workload.put("completedCourses", completedCourses);
        workload.put("pendingCourses", pendingCourses);
        workload.put("totalStudents", totalStudents);

        return workload;
    }

    @Override
    public List<Course> getTeacherCourses(Long teacherId) {
        return courseService.getCoursesByTeacherId(teacherId);
    }
}
