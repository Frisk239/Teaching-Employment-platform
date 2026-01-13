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
 * 学员日报实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_daily_report")
@ApiModel(description = "学员日报信息")
public class DailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日报ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "日报日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    @ApiModelProperty(value = "今日学习内容")
    private String todayContent;

    @ApiModelProperty(value = "今日完成进度")
    private String todayProgress;

    @ApiModelProperty(value = "遇到的问题")
    private String problems;

    @ApiModelProperty(value = "明日计划")
    private String tomorrowPlan;

    @ApiModelProperty(value = "学习时长（小时）")
    private Integer studyHours;

    @ApiModelProperty(value = "代码行数")
    private Integer codeLines;

    @ApiModelProperty(value = "附件URL（截图、代码等）")
    private String attachmentUrl;

    @ApiModelProperty(value = "教师评语")
    private String teacherComment;

    @ApiModelProperty(value = "评分（1-5分）")
    private Integer rating;

    @ApiModelProperty(value = "状态：draft-草稿 submitted-已提交 reviewed-已批阅")
    private String status;

    @ApiModelProperty(value = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "批阅时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reviewTime;

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

    /**
     * 学生姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 学号
     */
    @TableField(exist = false)
    private String studentNo;
}
