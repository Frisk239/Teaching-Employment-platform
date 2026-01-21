package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.Exam;
import com.teaching.employment.entity.Position;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.ExamMapper;
import com.teaching.employment.service.CourseService;
import com.teaching.employment.service.ExamService;
import com.teaching.employment.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 考试Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    private final ExamMapper examMapper;
    private final CourseService courseService;
    private final PositionService positionService;

    @Override
    public IPage<Exam> getExamPage(Integer current, Integer size, String examType, Long refId, String status, String keyword) {
        Page<Exam> page = new Page<>(current, size);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(examType)) {
            wrapper.eq(Exam::getExamType, examType);
        }
        if (refId != null) {
            wrapper.eq(Exam::getRefId, refId);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Exam::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Exam::getExamName, keyword);
        }

        wrapper.orderByDesc(Exam::getCreateTime);
        IPage<Exam> examPage = examMapper.selectPage(page, wrapper);

        // 填充关联名称
        for (Exam exam : examPage.getRecords()) {
            if ("course".equals(exam.getExamType()) && exam.getRefId() != null) {
                Course course = courseService.getById(exam.getRefId());
                if (course != null) {
                    exam.setRefName(course.getCourseName());
                }
            } else if ("company".equals(exam.getExamType()) && exam.getRefId() != null) {
                Position position = positionService.getById(exam.getRefId());
                if (position != null) {
                    exam.setRefName(position.getPositionName());
                }
            }
        }

        return examPage;
    }

    @Override
    public List<Exam> getExamsByRefId(String examType, Long refId) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getExamType, examType)
               .eq(Exam::getRefId, refId);
        List<Exam> exams = examMapper.selectList(wrapper);

        // 填充关联名称
        for (Exam exam : exams) {
            if ("course".equals(exam.getExamType()) && exam.getRefId() != null) {
                Course course = courseService.getById(exam.getRefId());
                if (course != null) {
                    exam.setRefName(course.getCourseName());
                }
            } else if ("company".equals(exam.getExamType()) && exam.getRefId() != null) {
                Position position = positionService.getById(exam.getRefId());
                if (position != null) {
                    exam.setRefName(position.getPositionName());
                }
            }
        }

        return exams;
    }

    @Override
    public List<Exam> getPublishedExams() {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getStatus, "published")
               .ge(Exam::getEndTime, java.time.LocalDateTime.now())
               .le(Exam::getStartTime, java.time.LocalDateTime.now());
        List<Exam> exams = examMapper.selectList(wrapper);

        // 填充关联名称
        for (Exam exam : exams) {
            if ("course".equals(exam.getExamType()) && exam.getRefId() != null) {
                Course course = courseService.getById(exam.getRefId());
                if (course != null) {
                    exam.setRefName(course.getCourseName());
                }
            } else if ("company".equals(exam.getExamType()) && exam.getRefId() != null) {
                Position position = positionService.getById(exam.getRefId());
                if (position != null) {
                    exam.setRefName(position.getPositionName());
                }
            }
        }

        return exams;
    }

    @Override
    public List<Exam> getOngoingExams() {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getStatus, "published")
               .ge(Exam::getEndTime, java.time.LocalDateTime.now())
               .le(Exam::getStartTime, java.time.LocalDateTime.now());
        List<Exam> exams = examMapper.selectList(wrapper);

        // 填充关联名称
        for (Exam exam : exams) {
            if ("course".equals(exam.getExamType()) && exam.getRefId() != null) {
                Course course = courseService.getById(exam.getRefId());
                if (course != null) {
                    exam.setRefName(course.getCourseName());
                }
            } else if ("company".equals(exam.getExamType()) && exam.getRefId() != null) {
                Position position = positionService.getById(exam.getRefId());
                if (position != null) {
                    exam.setRefName(position.getPositionName());
                }
            }
        }

        return exams;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        if (!"draft".equals(exam.getStatus())) {
            throw new BusinessException("只有草稿状态的考试才能发布");
        }

        exam.setStatus("published");
        return examMapper.updateById(exam) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean endExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        exam.setStatus("ended");
        return examMapper.updateById(exam) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        if ("started".equals(exam.getStatus())) {
            throw new BusinessException("进行中的考试不能删除");
        }

        return examMapper.deleteById(examId) > 0;
    }
}
