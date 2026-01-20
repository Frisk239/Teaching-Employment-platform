package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 职位推荐实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-20
 */
@Data
@TableName("t_position_recommendation")
@ApiModel(description = "职位推荐")
public class PositionRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学员ID")
    private Long studentId;

    @ApiModelProperty(value = "职位ID")
    private Long positionId;

    @ApiModelProperty(value = "教师ID")
    private Long teacherId;

    @ApiModelProperty(value = "推荐理由")
    private String reason;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "状态：pending-待查看，viewed-已查看，applied-已投递，rejected-已拒绝")
    private String status;

    @ApiModelProperty(value = "学员查看时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime viewTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 学员信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 职位信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Position position;

    /**
     * 教师信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Teacher teacher;

    /**
     * 学员姓名（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 职位名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String positionName;

    /**
     * 企业名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 教师姓名（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String teacherName;
}
