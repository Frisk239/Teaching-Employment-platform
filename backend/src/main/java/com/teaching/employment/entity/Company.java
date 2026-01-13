package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 企业实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_company")
@ApiModel(description = "企业信息")
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "企业ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "企业名称")
    private String companyName;

    @ApiModelProperty(value = "企业统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(value = "企业简称")
    private String shortName;

    @ApiModelProperty(value = "企业logo")
    private String logo;

    @ApiModelProperty(value = "行业")
    private String industry;

    @ApiModelProperty(value = "规模（人）：1-10人 11-50人 51-200人 201-500人 501-1000人 1000人以上")
    private String scale;

    @ApiModelProperty(value = "发展阶段：startup-初创期 growth-成长期 maturity-成熟期 listing-上市")
    private String stage;

    @ApiModelProperty(value = "所在城市")
    private String city;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "企业简介")
    private String description;

    @ApiModelProperty(value = "企业福利")
    private String benefits;

    @ApiModelProperty(value = "企业官网")
    private String website;

    @ApiModelProperty(value = "联系人姓名")
    private String contactName;

    @ApiModelProperty(value = "联系人职位")
    private String contactPosition;

    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;

    @ApiModelProperty(value = "联系人邮箱")
    private String contactEmail;

    @ApiModelProperty(value = "企业认证状态：pending-待认证 approved-已认证 rejected-已拒绝")
    private String verifyStatus;

    @ApiModelProperty(value = "认证时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime verifyTime;

    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    @ApiModelProperty(value = "状态：1-正常 0-禁用")
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
