package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学员Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 根据学号查询学员（包含用户和学校信息）
     *
     * @param studentNo 学号
     * @return 学员信息
     */
    @Select("SELECT s.*, u.real_name, u.username, u.phone, u.email " +
            "FROM t_student s " +
            "LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE s.student_no = #{studentNo} AND s.is_deleted = 0")
    Student selectStudentWithDetailsByStudentNo(@Param("studentNo") String studentNo);
}
