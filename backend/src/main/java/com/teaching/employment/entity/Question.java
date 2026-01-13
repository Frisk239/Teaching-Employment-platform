package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 题目实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_question")
@ApiModel(description = "考试题目信息")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "考试ID")
    private Long examId;

    @ApiModelProperty(value = "题目类型：single-单选题 multi-多选题 essay-简答题")
    private String questionType;

    @ApiModelProperty(value = "题目内容")
    private String content;

    @ApiModelProperty(value = "选项A")
    private String optionA;

    @ApiModelProperty(value = "选项B")
    private String optionB;

    @ApiModelProperty(value = "选项C")
    private String optionC;

    @ApiModelProperty(value = "选项D")
    private String optionD;

    @ApiModelProperty(value = "正确答案")
    private String correctAnswer;

    @ApiModelProperty(value = "分值")
    private Integer score;

    @ApiModelProperty(value = "题目排序")
    private Integer sortOrder;

    @ApiModelProperty(value = "题目解析")
    private String analysis;

    @ApiModelProperty(value = "难度级别：easy-简单 medium-中等 hard-困难")
    private String difficulty;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    /**
     * 考试信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Exam exam;
}
