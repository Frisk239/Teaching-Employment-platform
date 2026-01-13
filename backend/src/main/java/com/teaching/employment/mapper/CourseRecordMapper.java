package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.CourseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程学习记录Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface CourseRecordMapper extends BaseMapper<CourseRecord> {

    /**
     * 根据学员ID查询学习记录
     */
    @Select("SELECT cr.*, c.course_name, s.student_no, u.username as student_name " +
            "FROM t_course_record cr " +
            "LEFT JOIN t_course c ON cr.course_id = c.id " +
            "LEFT JOIN t_student s ON cr.student_id = s.id " +
            "LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE cr.student_id = #{studentId} AND cr.is_deleted = 0")
    List<CourseRecord> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据课程ID查询学习记录
     */
    @Select("SELECT cr.*, s.student_no, u.username as student_name " +
            "FROM t_course_record cr " +
            "LEFT JOIN t_student s ON cr.student_id = s.id " +
            "LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE cr.course_id = #{courseId} AND cr.is_deleted = 0")
    List<CourseRecord> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询学员在指定课程的学习记录
     */
    @Select("SELECT * FROM t_course_record WHERE student_id = #{studentId} AND course_id = #{courseId} AND is_deleted = 0")
    CourseRecord selectByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
