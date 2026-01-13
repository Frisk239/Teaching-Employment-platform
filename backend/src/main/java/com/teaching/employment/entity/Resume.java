package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 简历实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_resume")
@ApiModel(description = "简历信息")
public class Resume implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "简历ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别：1-男 2-女")
    private Integer gender;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "求职意向")
    private String jobIntention;

    @ApiModelProperty(value = "期望城市")
    private String expectCity;

    @ApiModelProperty(value = "期望薪资最低")
    private Integer expectSalaryMin;

    @ApiModelProperty(value = "期望薪资最高")
    private Integer expectSalaryMax;

    @ApiModelProperty(value = "教育经历（JSON数组）")
    private String education;

    @ApiModelProperty(value = "项目经历（JSON数组）")
    private String projects;

    @ApiModelProperty(value = "技能特长（JSON数组）")
    private String skills;

    @ApiModelProperty(value = "自我评价")
    private String selfEvaluation;

    @ApiModelProperty(value = "附件URL（简历PDF）")
    private String attachmentUrl;

    @ApiModelProperty(value = "简历完整度（百分比）")
    private Integer completeness;

    @ApiModelProperty(value = "状态：draft-草稿 active-公开 inactive-隐藏")
    private String status;

    @ApiModelProperty(value = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastUpdateTime;

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
     * 学生信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Student student;
}
