package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teaching.employment.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 考试Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    /**
     * 根据课程ID查询考试列表
     */
    @Select("SELECT * FROM t_exam WHERE course_id = #{courseId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<Exam> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询已发布的考试列表
     */
    @Select("SELECT * FROM t_exam WHERE status = 'published' AND is_deleted = 0 ORDER BY start_time DESC")
    List<Exam> selectPublishedExams();

    /**
     * 查询进行中的考试列表
     */
    @Select("SELECT * FROM t_exam WHERE status = 'started' AND is_deleted = 0")
    List<Exam> selectOngoingExams();
}
