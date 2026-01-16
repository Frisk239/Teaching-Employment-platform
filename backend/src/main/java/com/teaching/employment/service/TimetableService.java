package com.teaching.employment.service;

import com.teaching.employment.entity.Timetable;
import com.teaching.employment.entity.Course;
import com.teaching.employment.entity.Classroom;

import java.util.List;
import java.util.Map;

/**
 * 课程表Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-16
 */
public interface TimetableService {

    /**
     * 获取学生的课程表列表
     */
    List<Timetable> getTimetableByStudentId(Long studentId);

    /**
     * 获取学生的课程表（按学期和学年筛选）
     */
    List<Timetable> getTimetableByStudentIdAndTerm(Long studentId, String semester, String academicYear);

    /**
     * 获取学生的课程表（转换为二维数组格式，方便前端显示）
     * 返回格式：Map<Integer, Map<Integer, Timetable>>
     * 外层Map的key是星期几（1-7），内层Map的key是节次（1-12）
     */
    Map<Integer, Map<Integer, Timetable>> getTimetableGrid(Long studentId, String semester, String academicYear);

    /**
     * 添加课程表记录
     */
    boolean addTimetable(Timetable timetable);

    /**
     * 更新课程表记录
     */
    boolean updateTimetable(Timetable timetable);

    /**
     * 删除课程表记录
     */
    boolean deleteTimetable(Long id);

    /**
     * 批量添加课程表记录
     */
    boolean batchAddTimetable(List<Timetable> timetables);
}
