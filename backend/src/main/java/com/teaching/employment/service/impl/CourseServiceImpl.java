package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Classroom;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.CourseStudent;
import com.teaching.employment.entity.School;
import com.teaching.employment.entity.Teacher;
import com.teaching.employment.entity.User;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.CourseMapper;
import com.teaching.employment.service.ClassroomService;
import com.teaching.employment.service.CourseService;
import com.teaching.employment.service.CourseStudentService;
import com.teaching.employment.service.SchoolService;
import com.teaching.employment.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final SchoolService schoolService;
    private final TeacherService teacherService;
    private final ClassroomService classroomService;

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
        IPage<Course> result = courseMapper.selectPage(page, wrapper);

        // 填充关联数据
        fillRelatedData(result.getRecords());

        return result;
    }

    /**
     * 填充课程的关联数据(学校、教师、教室)
     */
    private void fillRelatedData(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return;
        }

        // 收集所有需要查询的ID
        List<Long> schoolIds = courses.stream()
                .map(Course::getSchoolId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> teacherIds = courses.stream()
                .map(Course::getTeacherId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> classroomIds = courses.stream()
                .filter(c -> c.getClassroomId() != null)
                .map(Course::getClassroomId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询学校信息
        Map<Long, String> schoolMap = Map.of();
        if (!schoolIds.isEmpty()) {
            List<School> schools = schoolService.listByIds(schoolIds);
            schoolMap = schools.stream()
                    .collect(Collectors.toMap(School::getId, School::getSchoolName));
        }

        // 批量查询教师信息(需要关联用户表获取姓名)
        Map<Long, String> teacherMap = Map.of();
        if (!teacherIds.isEmpty()) {
            List<Teacher> teachers = teacherService.listByIds(teacherIds);
            teacherMap = teachers.stream()
                    .collect(Collectors.toMap(Teacher::getId, Teacher::getRealName));
        }

        // 批量查询教室信息
        Map<Long, String> classroomMap = Map.of();
        if (!classroomIds.isEmpty()) {
            List<Classroom> classrooms = classroomService.listByIds(classroomIds);
            classroomMap = classrooms.stream()
                    .collect(Collectors.toMap(Classroom::getId, Classroom::getClassroomName));
        }

        // 填充数据到课程对象
        for (Course course : courses) {
            course.setSchoolName(schoolMap.get(course.getSchoolId()));
            course.setTeacherName(teacherMap.get(course.getTeacherId()));
            if (course.getClassroomId() != null) {
                course.setClassroomName(classroomMap.get(course.getClassroomId()));
            }
        }
    }

    @Override
    public Course getCourseWithDetails(Long id) {
        Course course = courseMapper.selectById(id);
        if (course != null) {
            fillRelatedData(java.util.Collections.singletonList(course));
        }
        return course;
    }

    @Override
    public List<Course> getCoursesByStudentId(Long studentId) {
        List<CourseStudent> courseStudents = courseStudentService.getCoursesByStudentId(studentId);
        return courseStudents.stream()
                .map(cs -> courseMapper.selectById(cs.getCourseId()))
                .filter(course -> course != null)  // MyBatis Plus的@TableLogic会自动过滤已删除记录
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
