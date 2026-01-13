package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.StudentExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学员考试记录Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface StudentExamRecordMapper extends BaseMapper<StudentExamRecord> {

    /**
     * 根据考试ID查询所有学员成绩
     */
    @Select("SELECT ser.*, s.student_number, u.username as student_name " +
            "FROM t_student_exam_record ser " +
            "LEFT JOIN t_student s ON ser.student_id = s.id " +
            "LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE ser.exam_id = #{examId} AND ser.is_deleted = 0 " +
            "ORDER BY ser.score DESC")
    List<StudentExamRecord> selectByExamId(@Param("examId") Long examId);

    /**
     * 根据学员ID查询考试记录
     */
    @Select("SELECT ser.*, e.exam_name " +
            "FROM t_student_exam_record ser " +
            "LEFT JOIN t_exam e ON ser.exam_id = e.id " +
            "WHERE ser.student_id = #{studentId} AND ser.is_deleted = 0 " +
            "ORDER BY ser.create_time DESC")
    List<StudentExamRecord> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询学员在指定考试中的记录
     */
    @Select("SELECT * FROM t_student_exam_record WHERE exam_id = #{examId} AND student_id = #{studentId} AND is_deleted = 0")
    StudentExamRecord selectByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);
}
