package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 笔试实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_written_test")
@ApiModel(description = "笔试信息")
public class WrittenTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "笔试ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "申请ID")
    private Long applicationId;

    @ApiModelProperty(value = "职位ID")
    private Long positionId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "考试链接或试卷URL")
    private String testUrl;

    @ApiModelProperty(value = "考试时长（分钟）")
    private Integer duration;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @ApiModelProperty(value = "总分")
    private Integer totalScore;

    @ApiModelProperty(value = "考试状态：pending-待考试 ongoing-考试中 completed-已完成 missed-缺席")
    private String status;

    @ApiModelProperty(value = "评语")
    private String comment;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 申请信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private JobApplication application;

    /**
     * 职位信息
     */
    @TableField(exist = false)
    private Position position;

    /**
     * 学生信息
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 学生姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 职位名称
     */
    @TableField(exist = false)
    private String positionName;
}
