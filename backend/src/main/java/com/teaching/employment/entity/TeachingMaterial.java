package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学资料实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@Data
@TableName("t_teaching_material")
@ApiModel(description = "教学资料信息")
public class TeachingMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资料ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "资料名称")
    private String title;

    @ApiModelProperty(value = "资料类型：ppt-video-document-image-other")
    private String materialType;

    @ApiModelProperty(value = "文件URL")
    private String fileUrl;

    @ApiModelProperty(value = "文件大小(字节)")
    private Long fileSize;

    @ApiModelProperty(value = "关联课程ID")
    private Long courseId;

    @ApiModelProperty(value = "教师ID")
    private Long teacherId;

    @ApiModelProperty(value = "分类标签")
    private String category;

    @ApiModelProperty(value = "标签(逗号分隔)")
    private String tags;

    @ApiModelProperty(value = "资料描述")
    private String description;

    @ApiModelProperty(value = "是否对学员公开：0-私有 1-公开")
    private Integer isPublic;

    @ApiModelProperty(value = "下载次数")
    private Integer downloadCount;

    @ApiModelProperty(value = "查看次数")
    private Integer viewCount;

    @ApiModelProperty(value = "状态：active-激活 inactive-停用")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 关联查询字段（不映射到数据库） ====================

    @ApiModelProperty(value = "课程名称")
    @TableField(exist = false)
    private String courseName;

    @ApiModelProperty(value = "教师姓名")
    @TableField(exist = false)
    private String teacherName;

    @ApiModelProperty(value = "文件扩展名")
    @TableField(exist = false)
    private String fileExtension;
}
