package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学员考试记录实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_student_exam_record")
@ApiModel(description = "学员考试记录")
public class StudentExamRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "考试ID")
    private Long examId;

    @ApiModelProperty(value = "学员ID")
    private Long studentId;

    @ApiModelProperty(value = "答题内容(JSON格式存储)")
    private String answers;

    @ApiModelProperty(value = "得分")
    private BigDecimal score;

    @ApiModelProperty(value = "是否及格：1-是 0-否")
    private Integer isPassed;

    @ApiModelProperty(value = "考试开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "考试结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "用时(秒)")
    private Integer duration;

    @ApiModelProperty(value = "考试状态：not_started-未开始 in_progress-进行中 submitted-已提交 graded-已批改")
    private String status;

    @ApiModelProperty(value = "评语")
    private String comment;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 考试信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Exam exam;

    /**
     * 学员信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 学员姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 考试名称
     */
    @TableField(exist = false)
    private String examName;
}
