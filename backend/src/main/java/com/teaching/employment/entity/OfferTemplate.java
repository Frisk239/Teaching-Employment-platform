package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Offer模板实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@Data
@TableName("t_offer_template")
@ApiModel(description = "Offer模板信息")
public class OfferTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "企业ID")
    private Long companyId;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "模板类型：general-通用 internship-实习 campus-校园社招 social-社招")
    private String templateType;

    @ApiModelProperty(value = "模板内容（支持变量占位符）")
    private String templateContent;

    @ApiModelProperty(value = "模板变量定义（JSON格式）")
    private String templateVariables;

    @ApiModelProperty(value = "是否默认模板：1-是 0-否")
    private Integer isDefault;

    @ApiModelProperty(value = "状态：1-启用 0-停用")
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

    @ApiModelProperty(value = "企业名称")
    @TableField(exist = false)
    private String companyName;
}
