package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.School;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.User;
import com.teaching.employment.mapper.CourseMapper;
import com.teaching.employment.mapper.TeacherMapper;
import com.teaching.employment.mapper.UserMapper;
import com.teaching.employment.service.SchoolService;
import com.teaching.employment.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final SchoolService schoolService;

    @Override
    public List<Teacher> listByIds(Collection<? extends Serializable> idList) {
        List<Teacher> teachers = super.listByIds(idList);

        if (teachers == null || teachers.isEmpty()) {
            return teachers;
        }

        // 填充学校名称和用户姓名
        fillRelatedData(teachers);

        return teachers;
    }

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
        IPage<Teacher> result = teacherMapper.selectPage(page, wrapper);

        // 填充学校名称和用户姓名
        fillRelatedData(result.getRecords());

        return result;
    }

    /**
     * 填充教师的关联数据(学校名称、用户姓名)
     */
    private void fillRelatedData(List<Teacher> teachers) {
        if (teachers == null || teachers.isEmpty()) {
            return;
        }

        // 收集所有需要查询的学校ID
        List<Long> schoolIds = teachers.stream()
                .map(Teacher::getSchoolId)
                .distinct()
                .collect(Collectors.toList());

        // 收集所有需要查询的用户ID
        List<Long> userIds = teachers.stream()
                .map(Teacher::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询学校信息
        final java.util.Map<Long, String> schoolMap;
        if (!schoolIds.isEmpty()) {
            List<School> schools = schoolService.listByIds(schoolIds);
            schoolMap = schools.stream()
                    .collect(Collectors.toMap(School::getId, School::getSchoolName));
        } else {
            schoolMap = java.util.Map.of();
        }

        // 批量查询用户信息
        final java.util.Map<Long, String> userMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getRealName));
        } else {
            userMap = java.util.Map.of();
        }

        // 填充数据到教师对象
        teachers.forEach(teacher -> {
            teacher.setSchoolName(schoolMap.get(teacher.getSchoolId()));
            teacher.setRealName(userMap.get(teacher.getUserId()));
        });
    }

    @Override
    public List<Teacher> getTeacherList(Long schoolId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (schoolId != null) {
            wrapper.eq(Teacher::getSchoolId, schoolId);
        }
        wrapper.orderByDesc(Teacher::getCreateTime);
        List<Teacher> list = teacherMapper.selectList(wrapper);

        // 填充学校名称和用户姓名
        fillRelatedData(list);

        return list;
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

        // 使用CourseMapper直接查询教师的所有课程
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getTeacherId, teacherId);
        List<Course> courses = courseMapper.selectList(courseWrapper);

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
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectList(wrapper);
    }
}
