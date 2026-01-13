package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.Homework;
import com.teaching.employment.entity.HomeworkSubmission;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.HomeworkMapper;
import com.teaching.employment.service.CourseService;
import com.teaching.employment.service.HomeworkService;
import com.teaching.employment.service.HomeworkSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private CourseService courseService;

    @Lazy
    @Autowired
    private HomeworkSubmissionService homeworkSubmissionService;

    @Override
    public IPage<Homework> getHomeworkPage(Integer current, Integer size, Long courseId, Long teacherId, String status, String keyword) {
        Page<Homework> page = new Page<>(current, size);
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();

        if (courseId != null) {
            wrapper.eq(Homework::getCourseId, courseId);
        }
        if (teacherId != null) {
            wrapper.eq(Homework::getTeacherId, teacherId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Homework::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Homework::getTitle, keyword);
        }

        wrapper.orderByDesc(Homework::getCreateTime);

        IPage<Homework> result = homeworkMapper.selectPage(page, wrapper);
        // 填充统计信息
        result.getRecords().forEach(this::fillStats);

        return result;
    }

    @Override
    public IPage<Homework> getHomeworkByCourseId(Integer current, Integer size, Long courseId) {
        return getHomeworkPage(current, size, courseId, null, null, null);
    }

    @Override
    public IPage<Homework> getHomeworkByStudentId(Integer current, Integer size, Long studentId) {
        // 查询学生所在课程的所有作业
        List<Course> courses = courseService.getCoursesByStudentId(studentId);

        if (courses.isEmpty()) {
            Page<Homework> emptyPage = new Page<>(current, size);
            emptyPage.setRecords(new java.util.ArrayList<>());
            emptyPage.setTotal(0);
            return emptyPage;
        }

        Page<Homework> page = new Page<>(current, size);
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();

        // 获取课程ID列表
        List<Long> courseIds = courses.stream()
                .map(Course::getId)
                .collect(java.util.stream.Collectors.toList());

        wrapper.in(Homework::getCourseId, courseIds);
        wrapper.eq(Homework::getStatus, "published"); // 只查询已发布的作业
        wrapper.orderByDesc(Homework::getCreateTime);

        IPage<Homework> result = homeworkMapper.selectPage(page, wrapper);

        // 填充学生提交状态
        result.getRecords().forEach(homework -> {
            fillStats(homework);
            // 查询学生是否已提交
            HomeworkSubmission submission = homeworkSubmissionService.getSubmissionByHomeworkAndStudent(
                    homework.getId(), studentId);
            // 初始化meta Map并设置提交状态
            if (homework.getMeta() == null) {
                homework.setMeta(new HashMap<>());
            }
            homework.getMeta().put("submitted", submission != null);
        });

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishHomework(Long homeworkId) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        if (!"draft".equals(homework.getStatus())) {
            throw new BusinessException("只有草稿状态的作业才能发布");
        }

        homework.setStatus("published");
        return homeworkMapper.updateById(homework) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeHomework(Long homeworkId) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        homework.setStatus("closed");
        return homeworkMapper.updateById(homework) > 0;
    }

    @Override
    public Map<String, Object> getHomeworkStats(Long homeworkId) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        Map<String, Object> stats = new HashMap<>();
        Long submissionCount = homeworkSubmissionService.count(
                new LambdaQueryWrapper<HomeworkSubmission>()
                        .eq(HomeworkSubmission::getHomeworkId, homeworkId));
        stats.put("submissionCount", submissionCount.intValue());

        Integer currentStudents = courseService.getById(homework.getCourseId()).getCurrentStudents();
        stats.put("pendingCount", currentStudents - submissionCount.intValue());

        Long gradedCount = homeworkSubmissionService.count(
                new LambdaQueryWrapper<HomeworkSubmission>()
                        .eq(HomeworkSubmission::getHomeworkId, homeworkId)
                        .eq(HomeworkSubmission::getStatus, "graded"));
        stats.put("gradedCount", gradedCount.intValue());

        return stats;
    }

    /**
     * 填充统计信息
     */
    private void fillStats(Homework homework) {
        Map<String, Object> stats = getHomeworkStats(homework.getId());
        homework.setSubmissionCount((Integer) stats.get("submissionCount"));
        homework.setPendingCount((Integer) stats.get("pendingCount"));
    }
}
