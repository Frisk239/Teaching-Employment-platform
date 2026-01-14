package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教室实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_classroom")
@ApiModel(description = "教室信息")
public class Classroom implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教室ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "教室编号")
    private String classroomNo;

    @ApiModelProperty(value = "教室名称")
    private String classroomName;

    @ApiModelProperty(value = "所属学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "所在楼栋")
    private String building;

    @ApiModelProperty(value = "楼层")
    private String floor;

    @ApiModelProperty(value = "容纳人数")
    private Integer capacity;

    @ApiModelProperty(value = "教室类型")
    private String classroomType;

    @ApiModelProperty(value = "是否有投影仪：1-是 0-否")
    private Integer hasProjector;

    @ApiModelProperty(value = "是否有电脑：1-是 0-否")
    private Integer hasComputer;

    @ApiModelProperty(value = "是否多媒体教室：1-是 0-否")
    private Integer hasMultimedia;

    @ApiModelProperty(value = "设备描述")
    private String equipment;

    @ApiModelProperty(value = "状态：1-可用 0-不可用")
    private Integer status;

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
     * 学校信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private School school;

    /**
     * 学校名称（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private String schoolName;
}
