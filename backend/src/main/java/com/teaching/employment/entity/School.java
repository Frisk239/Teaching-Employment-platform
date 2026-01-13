package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学校实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_school")
@ApiModel(description = "学校信息")
public class School implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学校ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学校名称")
    private String schoolName;

    @ApiModelProperty(value = "学校代码")
    private String schoolCode;

    @ApiModelProperty(value = "办学许可证号")
    private String licenseNumber;

    @ApiModelProperty(value = "许可证颁发日期")
    private LocalDate licenseIssueDate;

    @ApiModelProperty(value = "许可证有效期至")
    private LocalDate licenseExpiryDate;

    @ApiModelProperty(value = "许可证图片URL")
    private String licenseImageUrl;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "学校官网")
    private String website;

    @ApiModelProperty(value = "联系人")
    private String contactPerson;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "学校简介")
    private String description;

    @ApiModelProperty(value = "状态：1-正常 0-停用")
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
}
