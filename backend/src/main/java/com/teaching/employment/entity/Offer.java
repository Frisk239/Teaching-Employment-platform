package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Offer实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@TableName("t_offer")
@ApiModel(description = "Offer信息")
public class Offer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Offer ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "申请ID")
    private Long applicationId;

    @ApiModelProperty(value = "职位ID")
    private Long positionId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "企业ID")
    private Long companyId;

    @ApiModelProperty(value = "Offer编号")
    private String offerNo;

    @ApiModelProperty(value = "职位名称")
    private String positionName;

    @ApiModelProperty(value = "入职城市")
    private String city;

    @ApiModelProperty(value = "薪资")
    private BigDecimal salary;

    @ApiModelProperty(value = "薪资单位：month-按月 year-按年")
    private String salaryUnit;

    @ApiModelProperty(value = "入职日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate onboardDate;

    @ApiModelProperty(value = "Offer状态：pending-待接受 accepted-已接受 rejected-已拒绝 cancelled-已取消 expired-已过期")
    private String status;

    @ApiModelProperty(value = "接受截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate acceptDeadline;

    @ApiModelProperty(value = "接受时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime acceptTime;

    @ApiModelProperty(value = "邮件发送状态：pending-待发送 sent-已发送 failed-发送失败")
    private String emailStatus;

    @ApiModelProperty(value = "邮件发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime emailSendTime;

    @ApiModelProperty(value = "附件URL（Offer PDF）")
    private String attachmentUrl;

    @ApiModelProperty(value = "备注")
    private String remark;

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
     * 申请信息（关联查询时使用，不映射到数据库字段）
     */
    @TableField(exist = false)
    private JobApplication application;

    /**
     * 学生信息
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 企业信息
     */
    @TableField(exist = false)
    private Company company;

    /**
     * 学生姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 学生邮箱
     */
    @TableField(exist = false)
    private String studentEmail;

    /**
     * 企业名称
     */
    @TableField(exist = false)
    private String companyName;
}
