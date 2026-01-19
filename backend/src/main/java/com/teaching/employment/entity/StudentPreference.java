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
 * 学员求职偏好实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Data
@TableName("t_student_preference")
@ApiModel(description = "学员求职偏好")
public class StudentPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学员ID")
    private Long studentId;

    @ApiModelProperty(value = "期望城市(JSON数组)")
    private String expectedCities;

    @ApiModelProperty(value = "期望职位(JSON数组)")
    private String expectedPositions;

    @ApiModelProperty(value = "期望最低薪资")
    private BigDecimal salaryMin;

    @ApiModelProperty(value = "期望最高薪资")
    private BigDecimal salaryMax;

    @ApiModelProperty(value = "薪资单位(month/year)")
    private String salaryUnit;

    @ApiModelProperty(value = "工作性质(多个用逗号分隔)")
    private String jobTypes;

    @ApiModelProperty(value = "企业规模偏好(多个用逗号分隔)")
    private String companySizes;

    @ApiModelProperty(value = "求职状态(actively/passively/not_seeking/employed)")
    private String seekingStatus;

    @ApiModelProperty(value = "自我评价")
    private String selfEvaluation;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
