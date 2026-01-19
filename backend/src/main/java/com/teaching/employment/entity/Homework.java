package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 作业实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_homework")
@ApiModel(description = "作业信息")
public class Homework implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "作业ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @ApiModelProperty(value = "教师ID")
    private Long teacherId;

    @ApiModelProperty(value = "作业标题")
    private String title;

    @ApiModelProperty(value = "作业描述")
    private String description;

    @ApiModelProperty(value = "作业类型：assignment-作业 project-项目 other-其他")
    private String homeworkType;

    @ApiModelProperty(value = "附件URL（教师上传的作业文件）")
    private String attachmentUrl;

    @ApiModelProperty(value = "截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime deadline;

    @ApiModelProperty(value = "满分")
    private Integer maxScore;

    @ApiModelProperty(value = "状态：draft-草稿 published-已发布 closed-已截止")
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
     * 教师信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private Teacher teacher;

    /**
     * 提交人数（统计用）
     */
    @TableField(exist = false)
    private Integer submissionCount;

    /**
     * 未提交人数（统计用）
     */
    @TableField(exist = false)
    private Integer pendingCount;

    /**
     * 提交信息（学生查询作业时的提交记录）
     */
    @TableField(exist = false)
    private HomeworkSubmission submission;

    /**
     * 元数据（用于存储额外的运行时数据，如学生是否已提交）
     */
    @TableField(exist = false)
    private Map<String, Object> meta;
}
