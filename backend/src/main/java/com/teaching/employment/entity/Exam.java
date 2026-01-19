package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_exam")
@ApiModel(description = "考试信息")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考试ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "考试名称")
    private String examName;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "考试类型：final-期末考试 mid-期中考试 quiz-随堂测试")
    private String examType;

    @ApiModelProperty(value = "考试时长(分钟)")
    private Integer duration;

    @ApiModelProperty(value = "总分")
    private Integer totalScore;

    @ApiModelProperty(value = "及格分")
    private Integer passingScore;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "考试说明")
    private String description;

    @ApiModelProperty(value = "是否随机出题：1-是 0-否")
    private Integer isRandom;

    @ApiModelProperty(value = "题目数量")
    private Integer questionCount;

    @ApiModelProperty(value = "状态：draft-草稿 published-已发布 started-已进行中 ended-已结束")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 课程信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Course course;

    /**
     * 课程名称
     */
    @TableField(exist = false)
    private String courseName;
}
