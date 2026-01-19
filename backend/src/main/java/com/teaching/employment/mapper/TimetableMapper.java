package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Timetable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程表Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-16
 */
@Mapper
public interface TimetableMapper extends BaseMapper<Timetable> {

    /**
     * 查询学生的课程表（按星期和节次排序）
     */
    @Select("SELECT * FROM t_timetable " +
            "WHERE student_id = #{studentId} " +
            "ORDER BY day_of_week, start_period")
    List<Timetable> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询学生的课程表（按学期和学年筛选）
     */
    @Select("SELECT * FROM t_timetable " +
            "WHERE student_id = #{studentId} " +
            "AND semester = #{semester} " +
            "AND school_year = #{academicYear} " +
            "ORDER BY day_of_week, start_period")
    List<Timetable> selectByStudentIdAndTerm(
            @Param("studentId") Long studentId,
            @Param("semester") String semester,
            @Param("academicYear") String academicYear
    );
}
