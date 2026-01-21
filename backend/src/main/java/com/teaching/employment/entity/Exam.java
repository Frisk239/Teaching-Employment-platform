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
 * 考试实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_exam")
@ApiModel(description = "试卷信息")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "试卷ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "考试名称")
    private String examName;

    @ApiModelProperty(value = "考试类型：course-课程考试 company-企业笔试")
    private String examType;

    @ApiModelProperty(value = "关联ID（课程ID或企业职位ID）")
    private Long refId;

    @ApiModelProperty(value = "考试时长（分钟）")
    private Integer duration;

    @ApiModelProperty(value = "考试开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "考试结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "及格分数")
    private BigDecimal passScore;

    @ApiModelProperty(value = "试卷总分")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "是否乱序题目：0-否 1-是")
    private Integer shuffleQuestions;

    @ApiModelProperty(value = "是否乱序选项：0-否 1-是")
    private Integer shuffleOptions;

    @ApiModelProperty(value = "状态：draft-草稿 published-已发布 ended-已结束")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联查询字段（不映射到数据库）
    @ApiModelProperty(value = "课程/职位名称")
    @TableField(exist = false)
    private String refName;
}
