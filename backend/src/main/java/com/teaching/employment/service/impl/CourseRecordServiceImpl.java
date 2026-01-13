package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.CourseRecord;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.CourseRecordMapper;
import com.teaching.employment.service.CourseRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程学习记录Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Service
@RequiredArgsConstructor
public class CourseRecordServiceImpl extends ServiceImpl<CourseRecordMapper, CourseRecord> implements CourseRecordService {

    private final CourseRecordMapper courseRecordMapper;

    @Override
    public IPage<CourseRecord> getRecordPage(Integer current, Integer size, Long courseId, Long studentId, String status) {
        Page<CourseRecord> page = new Page<>(current, size);
        return courseRecordMapper.selectPage(page, new LambdaQueryWrapper<CourseRecord>()
                .eq(courseId != null, CourseRecord::getCourseId, courseId)
                .eq(studentId != null, CourseRecord::getStudentId, studentId)
                .eq(StrUtil.isNotBlank(status), CourseRecord::getStatus, status)
                .orderByDesc(CourseRecord::getUpdateTime));
    }

    @Override
    public java.util.List<CourseRecord> getRecordsByStudentId(Long studentId) {
        return courseRecordMapper.selectByStudentId(studentId);
    }

    @Override
    public java.util.List<CourseRecord> getRecordsByCourseId(Long courseId) {
        return courseRecordMapper.selectByCourseId(courseId);
    }

    @Override
    public CourseRecord getRecordByStudentAndCourse(Long studentId, Long courseId) {
        return courseRecordMapper.selectByStudentAndCourse(studentId, courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProgress(Long studentId, Long courseId, Double progress) {
        CourseRecord record = getRecordByStudentAndCourse(studentId, courseId);
        if (record == null) {
            throw new BusinessException("学习记录不存在");
        }

        record.setProgress(BigDecimal.valueOf(progress));

        // 判断学习状态
        if (progress >= 100.0) {
            record.setStatus("completed");
        } else if (progress > 0.0) {
            record.setStatus("in_progress");
        }

        return courseRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWatchedDuration(Long studentId, Long courseId, Long duration) {
        CourseRecord record = getRecordByStudentAndCourse(studentId, courseId);
        if (record == null) {
            throw new BusinessException("学习记录不存在");
        }

        record.setWatchedDuration((record.getWatchedDuration() == null ? 0L : record.getWatchedDuration()) + duration);
        return courseRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAvgHomeworkScore(Long studentId, Long courseId, Double score) {
        CourseRecord record = getRecordByStudentAndCourse(studentId, courseId);
        if (record == null) {
            throw new BusinessException("学习记录不存在");
        }

        record.setAvgHomeworkScore(BigDecimal.valueOf(score));
        return courseRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateExamScore(Long studentId, Long courseId, Double score) {
        CourseRecord record = getRecordByStudentAndCourse(studentId, courseId);
        if (record == null) {
            throw new BusinessException("学习记录不存在");
        }

        record.setExamScore(BigDecimal.valueOf(score));
        return courseRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startLearning(Long studentId, Long courseId, Integer totalChapters) {
        // 检查是否已有学习记录
        CourseRecord existingRecord = getRecordByStudentAndCourse(studentId, courseId);
        if (existingRecord != null) {
            throw new BusinessException("学习记录已存在");
        }

        // 创建学习记录
        CourseRecord record = new CourseRecord();
        record.setStudentId(studentId);
        record.setCourseId(courseId);
        record.setProgress(BigDecimal.ZERO);
        record.setWatchedDuration(0L);
        record.setCompletedChapters(0);
        record.setTotalChapters(totalChapters);
        record.setStatus("not_started");

        return courseRecordMapper.insert(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeCourse(Long studentId, Long courseId) {
        CourseRecord record = getRecordByStudentAndCourse(studentId, courseId);
        if (record == null) {
            throw new BusinessException("学习记录不存在");
        }

        record.setProgress(BigDecimal.valueOf(100.0));
        record.setCompletedChapters(record.getTotalChapters());
        record.setStatus("completed");

        return courseRecordMapper.updateById(record) > 0;
    }

    @Override
    public Map<String, Object> getCourseProgressStatistics(Long courseId) {
        Map<String, Object> statistics = new HashMap<>();

        // 获取课程的所有学习记录
        List<CourseRecord> records = getRecordsByCourseId(courseId);

        // 统计总学员数
        int totalStudents = records.size();

        // 统计不同状态的学员数
        long notStartedCount = records.stream()
                .filter(record -> "not_started".equals(record.getStatus()))
                .count();

        long inProgressCount = records.stream()
                .filter(record -> "in_progress".equals(record.getStatus()))
                .count();

        long completedCount = records.stream()
                .filter(record -> "completed".equals(record.getStatus()))
                .count();

        // 计算平均进度
        double avgProgress = records.stream()
                .filter(record -> record.getProgress() != null)
                .mapToDouble(record -> record.getProgress().doubleValue())
                .average()
                .orElse(0.0);

        // 计算总观看时长
        long totalWatchedDuration = records.stream()
                .filter(record -> record.getWatchedDuration() != null)
                .mapToLong(CourseRecord::getWatchedDuration)
                .sum();

        // 计算平均作业分
        double avgHomeworkScore = records.stream()
                .filter(record -> record.getAvgHomeworkScore() != null)
                .mapToDouble(record -> record.getAvgHomeworkScore().doubleValue())
                .average()
                .orElse(0.0);

        // 计算平均考试分
        double avgExamScore = records.stream()
                .filter(record -> record.getExamScore() != null)
                .mapToDouble(record -> record.getExamScore().doubleValue())
                .average()
                .orElse(0.0);

        statistics.put("courseId", courseId);
        statistics.put("totalStudents", totalStudents);
        statistics.put("notStartedCount", notStartedCount);
        statistics.put("inProgressCount", inProgressCount);
        statistics.put("completedCount", completedCount);
        statistics.put("completionRate", totalStudents > 0 ? (double) completedCount / totalStudents * 100 : 0.0);
        statistics.put("avgProgress", avgProgress);
        statistics.put("totalWatchedDuration", totalWatchedDuration);
        statistics.put("avgHomeworkScore", avgHomeworkScore);
        statistics.put("avgExamScore", avgExamScore);

        return statistics;
    }

    @Override
    public Map<String, Object> getStudentProgressStatistics(Long studentId) {
        Map<String, Object> statistics = new HashMap<>();

        // 获取学员的所有学习记录
        List<CourseRecord> records = getRecordsByStudentId(studentId);

        // 统计总课程数
        int totalCourses = records.size();

        // 统计不同状态的课程数
        long notStartedCount = records.stream()
                .filter(record -> "not_started".equals(record.getStatus()))
                .count();

        long inProgressCount = records.stream()
                .filter(record -> "in_progress".equals(record.getStatus()))
                .count();

        long completedCount = records.stream()
                .filter(record -> "completed".equals(record.getStatus()))
                .count();

        // 计算总观看时长
        long totalWatchedDuration = records.stream()
                .filter(record -> record.getWatchedDuration() != null)
                .mapToLong(CourseRecord::getWatchedDuration)
                .sum();

        // 计算总学习时长(转换为小时)
        double totalHours = totalWatchedDuration / 3600.0;

        // 计算平均作业分
        double avgHomeworkScore = records.stream()
                .filter(record -> record.getAvgHomeworkScore() != null)
                .mapToDouble(record -> record.getAvgHomeworkScore().doubleValue())
                .average()
                .orElse(0.0);

        // 计算平均考试分
        double avgExamScore = records.stream()
                .filter(record -> record.getExamScore() != null)
                .mapToDouble(record -> record.getExamScore().doubleValue())
                .average()
                .orElse(0.0);

        statistics.put("studentId", studentId);
        statistics.put("totalCourses", totalCourses);
        statistics.put("notStartedCount", notStartedCount);
        statistics.put("inProgressCount", inProgressCount);
        statistics.put("completedCount", completedCount);
        statistics.put("totalWatchedDuration", totalWatchedDuration);
        statistics.put("totalHours", totalHours);
        statistics.put("avgHomeworkScore", avgHomeworkScore);
        statistics.put("avgExamScore", avgExamScore);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getTopLearnersByDuration(Long courseId, Integer limit) {
        List<CourseRecord> records;

        if (courseId != null) {
            // 获取指定课程的学习记录
            records = getRecordsByCourseId(courseId);
        } else {
            // 获取所有学习记录
            records = courseRecordMapper.selectList(new LambdaQueryWrapper<>());
        }

        // 按学员分组并统计学习时长
        Map<Long, Long> studentDurationMap = records.stream()
                .filter(record -> record.getWatchedDuration() != null)
                .collect(Collectors.groupingBy(
                        CourseRecord::getStudentId,
                        Collectors.summingLong(CourseRecord::getWatchedDuration)
                ));

        // 转换为列表并排序
        return studentDurationMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit != null ? limit : 10)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("studentId", entry.getKey());
                    result.put("totalWatchedDuration", entry.getValue());
                    result.put("totalHours", entry.getValue() / 3600.0);
                    return result;
                })
                .collect(Collectors.toList());
    }
}
