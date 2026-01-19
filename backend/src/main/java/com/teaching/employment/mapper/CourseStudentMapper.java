package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.CourseStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程-学员关联Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface CourseStudentMapper extends BaseMapper<CourseStudent> {

    /**
     * 根据课程ID查询学员列表
     */
    @Select("SELECT cs.*, s.student_number, u.username as student_name " +
            "FROM t_course_student cs " +
            "LEFT JOIN t_student s ON cs.student_id = s.id " +
            "LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE cs.course_id = #{courseId}")
    List<CourseStudent> selectStudentsByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学员ID查询课程列表
     */
    @Select("SELECT cs.*, c.course_name " +
            "FROM t_course_student cs " +
            "LEFT JOIN t_course c ON cs.course_id = c.id " +
            "WHERE cs.student_id = #{studentId}")
    List<CourseStudent> selectCoursesByStudentId(@Param("studentId") Long studentId);

    /**
     * 统计课程学员数量
     */
    @Select("SELECT COUNT(*) FROM t_course_student WHERE course_id = #{courseId}")
    Integer countStudentsByCourseId(@Param("courseId") Long courseId);

    /**
     * 检查学员是否已选课
     */
    @Select("SELECT COUNT(*) FROM t_course_student WHERE course_id = #{courseId} AND student_id = #{studentId}")
    Integer checkEnrollment(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
