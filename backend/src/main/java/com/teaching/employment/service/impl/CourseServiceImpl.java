package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.CourseStudent;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.CourseMapper;
import com.teaching.employment.service.CourseService;
import com.teaching.employment.service.CourseStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseStudentService courseStudentService;

    @Override
    public IPage<Course> getCoursePage(Integer current, Integer size, Long schoolId, Long teacherId, String status, String keyword) {
        Page<Course> page = new Page<>(current, size);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        if (schoolId != null) {
            wrapper.eq(Course::getSchoolId, schoolId);
        }
        if (teacherId != null) {
            wrapper.eq(Course::getTeacherId, teacherId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Course::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Course::getCourseName, keyword)
                    .or().like(Course::getCourseCode, keyword));
        }

        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Course> getCoursesByStudentId(Long studentId) {
        List<CourseStudent> courseStudents = courseStudentService.getCoursesByStudentId(studentId);
        return courseStudents.stream()
                .map(cs -> courseMapper.selectById(cs.getCourseId()))
                .filter(course -> course != null && course.getIsDeleted() == 0)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectList(wrapper);
    }

    @Override
    public List<Course> getCoursesByClassroomId(Long classroomId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getClassroomId, classroomId);
        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStudentToCourse(Long courseId, Long studentId) {
        return courseStudentService.enrollCourse(courseId, studentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddStudentsToCourse(Long courseId, List<Long> studentIds) {
        for (Long studentId : studentIds) {
            courseStudentService.enrollCourse(courseId, studentId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeStudentFromCourse(Long courseId, Long studentId) {
        return courseStudentService.dropCourse(courseId, studentId);
    }
}
