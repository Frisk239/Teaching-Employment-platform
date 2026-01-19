package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学计划实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-19
 */
@Data
@TableName("t_teaching_plan")
@ApiModel(description = "教学计划")
public class TeachingPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教学计划ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "课次序号")
    private Integer lessonNumber;

    @ApiModelProperty(value = "周次")
    private Integer weekNumber;

    @ApiModelProperty(value = "本次课标题")
    private String title;

    @ApiModelProperty(value = "教学内容描述")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 课程名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 教师ID（关联查询时使用）
     */
    @TableField(exist = false)
    private Long teacherId;
}
