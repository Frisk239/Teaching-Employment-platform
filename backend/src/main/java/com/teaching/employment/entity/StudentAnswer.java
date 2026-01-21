package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_student_answer")
@ApiModel(description = "学生答题")
public class StudentAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "答题ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "考试记录ID")
    private Long examRecordId;

    @ApiModelProperty(value = "试卷题目ID")
    @TableField(exist = false)
    private Long examQuestionId;

    @ApiModelProperty(value = "题目ID")
    private Long questionId;

    @ApiModelProperty(value = "题目类型")
    private String questionType;

    @ApiModelProperty(value = "学生答案")
    private String studentAnswer;

    @ApiModelProperty(value = "得分")
    private BigDecimal score;

    @ApiModelProperty(value = "教师评语")
    private String comment;

    @ApiModelProperty(value = "是否正确：0-错误 1-正确 null-待批改")
    private Integer isCorrect;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联查询字段
    @ApiModelProperty(value = "题干")
    @TableField(exist = false)
    private String questionText;

    @ApiModelProperty(value = "正确答案")
    @TableField(exist = false)
    private String correctAnswer;

    @ApiModelProperty(value = "答案解析")
    @TableField(exist = false)
    private String analysis;
}
