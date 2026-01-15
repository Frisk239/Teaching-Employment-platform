package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 求职申请实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_job_application")
@ApiModel(description = "求职申请信息")
public class JobApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "申请ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "职位ID")
    private Long positionId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "简历ID")
    private Long resumeId;

    @ApiModelProperty(value = "申请状态：pending-待处理 screened-已筛选 test_passed-笔试通过 interview_passed-面试通过 test_failed-笔试失败 interview_failed-面试失败 rejected-已拒绝 offered-已发offer hired-已入职 declined-已拒绝offer")
    private String status;

    @ApiModelProperty(value = "当前阶段：resume-简历筛选 test-笔试 interview-面试 offer-offer发放 hired-入职")
    private String currentStage;

    @ApiModelProperty(value = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime applicationTime;

    @ApiModelProperty(value = "HR备注")
    private String hrRemark;

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
     * 职位信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Position position;

    /**
     * 学生信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 简历信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Resume resume;

    /**
     * 职位名称
     */
    @TableField(exist = false)
    private String positionName;

    /**
     * 学生姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 学生电话
     */
    @TableField(exist = false)
    private String studentPhone;

    /**
     * 企业名称
     */
    @TableField(exist = false)
    private String companyName;
}
