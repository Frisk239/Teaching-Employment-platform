package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题库实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Data
@TableName("t_question_bank")
@ApiModel(description = "题库信息")
public class QuestionBank implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态常量
     */
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_DRAFT = "draft";

    @ApiModelProperty(value = "题目ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "题型：single_choice-单选 multiple_choice-多选 true_false-判断 short_answer-简答")
    private String questionType;

    @ApiModelProperty(value = "知识点分类")
    private String knowledgePoint;

    @ApiModelProperty(value = "难度：easy-简单 medium-中等 hard-困难")
    private String difficulty;

    @ApiModelProperty(value = "题干")
    private String questionText;

    @ApiModelProperty(value = "选项（JSON格式，仅客观题）")
    private String options;

    @ApiModelProperty(value = "正确答案")
    private String correctAnswer;

    @ApiModelProperty(value = "答案解析")
    private String analysis;

    @ApiModelProperty(value = "所属企业ID（null表示基础题库）")
    private Long companyId;

    @ApiModelProperty(value = "状态：active-激活 draft-草稿")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 企业名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String companyName;
}
