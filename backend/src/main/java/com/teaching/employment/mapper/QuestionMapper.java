package com.teaching.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.employment.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目Mapper接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 根据考试ID查询题目列表
     */
    @Select("SELECT * FROM t_question WHERE exam_id = #{examId} AND is_deleted = 0 ORDER BY sort_order ASC")
    List<Question> selectByExamId(@Param("examId") Long examId);

    /**
     * 随机查询指定数量的题目
     */
    @Select("SELECT * FROM t_question WHERE exam_id = #{examId} AND is_deleted = 0 ORDER BY RAND() LIMIT #{count}")
    List<Question> selectRandomQuestions(@Param("examId") Long examId, @Param("count") Integer count);

    /**
     * 统计题目数量
     */
    @Select("SELECT COUNT(*) FROM t_question WHERE exam_id = #{examId} AND is_deleted = 0")
    Integer countByExamId(@Param("examId") Long examId);
}
