package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作业提交实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_homework_submission")
@ApiModel(description = "作业提交信息")
public class HomeworkSubmission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提交ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "作业ID")
    private Long homeworkId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "提交内容（文本回答）")
    private String content;

    @ApiModelProperty(value = "附件URL（学生上传的作业文件）")
    private String attachmentUrl;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @ApiModelProperty(value = "评语")
    private String comment;

    @ApiModelProperty(value = "提交状态：submitted-已提交 graded-已批评 returned-已退回")
    private String status;

    @ApiModelProperty(value = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "批改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime gradeTime;

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
     * 作业信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Homework homework;

    /**
     * 学生信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 学生姓名（来自学生和用户表）
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 是否逾期提交
     */
    @TableField(exist = false)
    private Boolean isLate;
}
