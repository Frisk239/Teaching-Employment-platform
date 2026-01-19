package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Classroom;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.School;
import com.teaching.employment.mapper.ClassroomMapper;
import com.teaching.employment.mapper.CourseMapper;
import com.teaching.employment.service.ClassroomService;
import com.teaching.employment.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final CourseMapper courseMapper;
    private final SchoolService schoolService;

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
        IPage<Classroom> result = classroomMapper.selectPage(page, wrapper);

        // 获取所有学校ID
        List<Long> schoolIds = result.getRecords().stream()
                .map(Classroom::getSchoolId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询学校信息
        if (!schoolIds.isEmpty()) {
            List<School> schools = schoolService.listByIds(schoolIds);
            Map<Long, String> schoolMap = schools.stream()
                    .collect(Collectors.toMap(School::getId, School::getSchoolName));

            // 填充学校名称
            result.getRecords().forEach(classroom ->
                classroom.setSchoolName(schoolMap.get(classroom.getSchoolId()))
            );
        }

        return result;
    }

    @Override
    public List<Classroom> getClassroomList(Long schoolId) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        if (schoolId != null) {
            wrapper.eq(Classroom::getSchoolId, schoolId);
        }
        wrapper.orderByAsc(Classroom::getClassroomNo);
        List<Classroom> list = classroomMapper.selectList(wrapper);

        // 填充学校名称
        if (!list.isEmpty()) {
            List<Long> schoolIds = list.stream()
                    .map(Classroom::getSchoolId)
                    .distinct()
                    .collect(Collectors.toList());

            List<School> schools = schoolService.listByIds(schoolIds);
            Map<Long, String> schoolMap = schools.stream()
                    .collect(Collectors.toMap(School::getId, School::getSchoolName));

            list.forEach(classroom ->
                classroom.setSchoolName(schoolMap.get(classroom.getSchoolId()))
            );
        }

        return list;
    }

    @Override
    public Classroom getById(Serializable id) {
        Classroom classroom = classroomMapper.selectById(id);
        if (classroom != null && classroom.getSchoolId() != null) {
            School school = schoolService.getById(classroom.getSchoolId());
            if (school != null) {
                classroom.setSchoolName(school.getSchoolName());
            }
        }
        return classroom;
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

        // 使用CourseMapper直接查询教室的所有课程
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getClassroomId, classroomId);
        List<Course> courses = courseMapper.selectList(wrapper);

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
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getClassroomId, classroomId);
        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectList(wrapper);
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
