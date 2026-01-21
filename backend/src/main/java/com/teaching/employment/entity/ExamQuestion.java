package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_exam_question")
@ApiModel(description = "试卷题目关联")
public class ExamQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "试卷ID")
    private Long examId;

    @ApiModelProperty(value = "题目ID")
    private Long questionId;

    @ApiModelProperty(value = "题目分值")
    private BigDecimal questionScore;

    @ApiModelProperty(value = "排序")
    private Integer sortOrder;

    @ApiModelProperty(value = "生成方式：manual-手动 auto-自动")
    private String generateType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "题目内容")
    @TableField(exist = false)
    private String questionText;

    @ApiModelProperty(value = "题目类型")
    @TableField(exist = false)
    private String questionType;

    @ApiModelProperty(value = "知识点")
    @TableField(exist = false)
    private String knowledgePoint;

    @ApiModelProperty(value = "难度")
    @TableField(exist = false)
    private String difficulty;

    @ApiModelProperty(value = "选项")
    @TableField(exist = false)
    private String options;

    @ApiModelProperty(value = "正确答案")
    @TableField(exist = false)
    private String correctAnswer;

    @ApiModelProperty(value = "答案解析")
    @TableField(exist = false)
    private String analysis;
}
