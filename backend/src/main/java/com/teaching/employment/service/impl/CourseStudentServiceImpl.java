package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.CourseStudent;
import com.teaching.employment.entity.Student;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.CourseStudentMapper;
import com.teaching.employment.service.CourseService;
import com.teaching.employment.service.CourseStudentService;
import com.teaching.employment.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程学员Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class CourseStudentServiceImpl extends ServiceImpl<CourseStudentMapper, CourseStudent> implements CourseStudentService {

    @Autowired
    private CourseStudentMapper courseStudentMapper;

    @Autowired
    private StudentService studentService;

    @Lazy
    @Autowired
    private CourseService courseService;

    @Override
    public IPage<CourseStudent> getCourseStudentPage(Integer current, Integer size, Long courseId, Long studentId, Integer status) {
        Page<CourseStudent> page = new Page<>(current, size);
        return courseStudentMapper.selectPage(page, new LambdaQueryWrapper<CourseStudent>()
                .eq(courseId != null, CourseStudent::getCourseId, courseId)
                .eq(studentId != null, CourseStudent::getStudentId, studentId)
                .eq(status != null, CourseStudent::getStatus, status)
                .orderByDesc(CourseStudent::getCreateTime));
    }

    @Override
    public List<CourseStudent> getStudentsByCourseId(Long courseId) {
        return courseStudentMapper.selectStudentsByCourseId(courseId);
    }

    @Override
    public List<CourseStudent> getCoursesByStudentId(Long studentId) {
        return courseStudentMapper.selectCoursesByStudentId(studentId);
    }

    @Override
    public Integer countStudentsByCourseId(Long courseId) {
        return courseStudentMapper.countStudentsByCourseId(courseId);
    }

    @Override
    public boolean checkEnrollment(Long courseId, Long studentId) {
        Integer count = courseStudentMapper.checkEnrollment(courseId, studentId);
        return count != null && count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enrollCourse(Long courseId, Long studentId) {
        // 1. 检查课程是否存在
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 2. 检查学生是否存在
        Student student = studentService.getById(studentId);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }

        // 3. 检查是否已选课
        if (checkEnrollment(courseId, studentId)) {
            throw new BusinessException("该学员已选过此课程");
        }

        // 4. 检查课程是否已满
        Integer currentCount = countStudentsByCourseId(courseId);
        if (course.getMaxStudents() != null && currentCount >= course.getMaxStudents()) {
            throw new BusinessException("课程人数已满");
        }

        // 5. 创建选课记录
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourseId(courseId);
        courseStudent.setStudentId(studentId);
        courseStudent.setEnrollmentDate(LocalDateTime.now());
        courseStudent.setStatus(1); // 在读
        courseStudent.setProgress(java.math.BigDecimal.ZERO);
        int result = courseStudentMapper.insert(courseStudent);

        if (result > 0) {
            // 同步更新课程的当前学员数
            Course courseToUpdate = courseService.getById(courseId);
            if (courseToUpdate != null) {
                Integer currentStudents = courseToUpdate.getCurrentStudents() != null
                    ? courseToUpdate.getCurrentStudents() : 0;
                courseToUpdate.setCurrentStudents(currentStudents + 1);
                courseService.updateById(courseToUpdate);
            }
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dropCourse(Long courseId, Long studentId) {
        // 1. 检查是否已选课
        if (!checkEnrollment(courseId, studentId)) {
            throw new BusinessException("该学员未选此课程");
        }

        // 2. 更新选课记录状态为退课
        LambdaQueryWrapper<CourseStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getStudentId, studentId);

        CourseStudent courseStudent = courseStudentMapper.selectOne(wrapper);
        if (courseStudent != null) {
            courseStudent.setStatus(0); // 已退课
            boolean result = courseStudentMapper.updateById(courseStudent) > 0;

            if (result) {
                // 同步更新课程的当前学员数
                Course courseToUpdate = courseService.getById(courseId);
                if (courseToUpdate != null && courseToUpdate.getCurrentStudents() != null
                    && courseToUpdate.getCurrentStudents() > 0) {
                    courseToUpdate.setCurrentStudents(courseToUpdate.getCurrentStudents() - 1);
                    courseService.updateById(courseToUpdate);
                }
            }

            return result;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProgress(Long courseId, Long studentId, Double progress) {
        LambdaQueryWrapper<CourseStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getStudentId, studentId);

        CourseStudent courseStudent = courseStudentMapper.selectOne(wrapper);
        if (courseStudent != null) {
            courseStudent.setProgress(java.math.BigDecimal.valueOf(progress));
            return courseStudentMapper.updateById(courseStudent) > 0;
        }

        return false;
    }
}
