package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 面试实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_interview")
@ApiModel(description = "面试信息")
public class Interview implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "面试ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "申请ID")
    private Long applicationId;

    @ApiModelProperty(value = "职位ID")
    private Long positionId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "企业ID")
    private Long companyId;

    @ApiModelProperty(value = "面试轮次：1-初试 2-复试 3-终试")
    private Integer round;

    @ApiModelProperty(value = "面试类型：online-线上 offline-现场 phone-电话")
    private String interviewType;

    @ApiModelProperty(value = "面试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime interviewTime;

    @ApiModelProperty(value = "会议链接（线上面试）")
    @TableField("meeting_link")
    private String meetingLink;

    @ApiModelProperty(value = "面试地点（线下面试）")
    private String location;

    @ApiModelProperty(value = "面试官")
    private String interviewer;

    @ApiModelProperty(value = "面试官联系方式")
    @TableField("interviewer_contact")
    private String interviewerContact;

    @ApiModelProperty(value = "面试结果：pending-待面试 passed-通过 failed-未通过 cancelled-已取消")
    private String result;

    @ApiModelProperty(value = "面试评分（1-100分）")
    private Integer score;

    @ApiModelProperty(value = "面试评价")
    private String comment;

    @ApiModelProperty(value = "面试状态：scheduled-已安排 completed-已完成 cancelled-已取消")
    private String status;

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

    /**
     * 企业名称
     */
    @TableField(exist = false)
    private String companyName;
}
