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
 * 课程学习记录实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_course_record")
@ApiModel(description = "课程学习记录")
public class CourseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学员ID")
    private Long studentId;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "学习进度(%)")
    private BigDecimal progress;

    @ApiModelProperty(value = "已观看时长(秒)")
    private Long watchedDuration;

    @ApiModelProperty(value = "平均作业分")
    private BigDecimal avgHomeworkScore;

    @ApiModelProperty(value = "考试成绩")
    private BigDecimal examScore;

    @ApiModelProperty(value = "已完成章节数")
    private Integer completedChapters;

    @ApiModelProperty(value = "总章节数")
    private Integer totalChapters;

    @ApiModelProperty(value = "学习状态：not_started-未开始 in_progress-学习中 completed-已完成")
    private String status;

    @ApiModelProperty(value = "是否删除：1-已删除 0-未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 课程信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Course course;

    /**
     * 学员信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 课程名称
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 学员姓名
     */
    @TableField(exist = false)
    private String studentName;
}
