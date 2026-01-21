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
 * 考试记录实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Data
@TableName("t_exam_record")
@ApiModel(description = "考试记录")
public class ExamRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "试卷ID")
    private Long examId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "开始答题时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "客观题得分")
    private BigDecimal objectiveScore;

    @ApiModelProperty(value = "主观题得分")
    private BigDecimal subjectiveScore;

    @ApiModelProperty(value = "总分")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "及格状态：0-不及格 1-及格")
    private Integer passed;

    @ApiModelProperty(value = "评阅状态：pending-待评阅 graded-已评阅")
    private String gradingStatus;

    @ApiModelProperty(value = "评阅时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gradingTime;

    @ApiModelProperty(value = "评阅人ID")
    private Long graderId;

    @ApiModelProperty(value = "题目快照（JSON格式，防止考试期间题目被修改）")
    @TableField("question_snapshot")
    private String questionsSnapshot;

    @ApiModelProperty(value = "状态：taking-答题中 submitted-已提交")
    @TableField("exam_status")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联查询字段（不映射到数据库）
    @ApiModelProperty(value = "学生姓名")
    @TableField(exist = false)
    private String studentName;

    @ApiModelProperty(value = "考试名称")
    @TableField(exist = false)
    private String examName;

    @ApiModelProperty(value = "评阅人姓名")
    @TableField(exist = false)
    private String graderName;
}
