package com.teaching.employment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.employment.entity.*;
import com.teaching.employment.exception.BusinessException;
import com.teaching.employment.mapper.TimetableMapper;
import com.teaching.employment.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 课程表Service实现类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {

    private final TimetableMapper timetableMapper;
    private final CourseService courseService;
    private final ClassroomService classroomService;
    private final TeacherService teacherService;
    private final UserService userService;

    @Override
    public List<Timetable> getTimetableByStudentId(Long studentId) {
        List<Timetable> timetables = timetableMapper.selectByStudentId(studentId);

        // 填充关联信息
        timetables.forEach(this::fillRelatedInfo);

        return timetables;
    }

    @Override
    public List<Timetable> getTimetableByStudentIdAndTerm(Long studentId, String semester, String academicYear) {
        List<Timetable> timetables = timetableMapper.selectByStudentIdAndTerm(studentId, semester, academicYear);

        // 填充关联信息
        timetables.forEach(this::fillRelatedInfo);

        return timetables;
    }

    @Override
    public Map<Integer, Map<Integer, Timetable>> getTimetableGrid(Long studentId, String semester, String academicYear) {
        List<Timetable> timetables;

        if (semester != null && academicYear != null) {
            timetables = getTimetableByStudentIdAndTerm(studentId, semester, academicYear);
        } else {
            timetables = getTimetableByStudentId(studentId);
        }

        // 转换为二维数组格式：Map<星期几, Map<节次, 课程表记录>>
        Map<Integer, Map<Integer, Timetable>> grid = new HashMap<>();

        for (int day = 1; day <= 7; day++) {
            Map<Integer, Timetable> dayMap = new HashMap<>();
            grid.put(day, dayMap);
        }

        for (Timetable timetable : timetables) {
            Integer dayOfWeek = timetable.getDayOfWeek();
            Integer startPeriod = timetable.getStartPeriod();

            if (dayOfWeek != null && startPeriod != null && dayOfWeek >= 1 && dayOfWeek <= 7) {
                Map<Integer, Timetable> dayMap = grid.get(dayOfWeek);
                if (dayMap != null) {
                    dayMap.put(startPeriod, timetable);
                }
            }
        }

        return grid;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTimetable(Timetable timetable) {
        validateTimetable(timetable);
        return timetableMapper.insert(timetable) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTimetable(Timetable timetable) {
        if (timetable.getId() == null) {
            throw new BusinessException("课程表ID不能为空");
        }

        validateTimetable(timetable);
        return timetableMapper.updateById(timetable) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTimetable(Long id) {
        return timetableMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddTimetable(List<Timetable> timetables) {
        if (timetables == null || timetables.isEmpty()) {
            return false;
        }

        for (Timetable timetable : timetables) {
            validateTimetable(timetable);
        }

        for (Timetable timetable : timetables) {
            timetableMapper.insert(timetable);
        }

        return true;
    }

    /**
     * 填充关联信息（课程、教室、教师）
     */
    private void fillRelatedInfo(Timetable timetable) {
        // 填充课程信息
        if (timetable.getCourseId() != null) {
            Course course = courseService.getById(timetable.getCourseId());
            timetable.setCourse(course);

            // 填充教师信息（从课程中获取）
            if (course != null && course.getTeacherId() != null) {
                Teacher teacher = teacherService.getById(course.getTeacherId());
                // 填充教师的真实姓名
                if (teacher != null && teacher.getUserId() != null) {
                    User user = userService.getById(teacher.getUserId());
                    if (user != null) {
                        teacher.setRealName(user.getRealName());
                    }
                }
                timetable.setTeacher(teacher);
            }
        }

        // 填充教室信息
        if (timetable.getClassroomId() != null) {
            Classroom classroom = classroomService.getById(timetable.getClassroomId());
            timetable.setClassroom(classroom);
        }
    }

    /**
     * 验证课程表数据
     */
    private void validateTimetable(Timetable timetable) {
        if (timetable.getCourseId() == null) {
            throw new BusinessException("课程ID不能为空");
        }

        if (timetable.getDayOfWeek() == null || timetable.getDayOfWeek() < 1 || timetable.getDayOfWeek() > 7) {
            throw new BusinessException("星期几必须在1-7之间");
        }

        if (timetable.getStartPeriod() == null || timetable.getStartPeriod() < 1 || timetable.getStartPeriod() > 12) {
            throw new BusinessException("开始节次必须在1-12之间");
        }

        if (timetable.getEndPeriod() == null || timetable.getEndPeriod() < 1 || timetable.getEndPeriod() > 12) {
            throw new BusinessException("结束节次必须在1-12之间");
        }

        if (timetable.getStartPeriod() > timetable.getEndPeriod()) {
            throw new BusinessException("开始节次不能大于结束节次");
        }

        // 验证课程是否存在
        Course course = courseService.getById(timetable.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 验证教室是否存在（如果指定了教室）
        if (timetable.getClassroomId() != null) {
            Classroom classroom = classroomService.getById(timetable.getClassroomId());
            if (classroom == null) {
                throw new BusinessException("教室不存在");
            }
        }
    }
}
