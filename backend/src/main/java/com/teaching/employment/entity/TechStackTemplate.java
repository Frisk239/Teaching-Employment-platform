package com.teaching.employment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_tech_stack_template")
@ApiModel(description = "技术栈模板")
public class TechStackTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "技能维度（JSON格式）")
    private String dimensions;

    @ApiModelProperty(value = "适用岗位类型")
    private String positionType;

    @ApiModelProperty(value = "是否默认模板：0-否 1-是")
    private Integer isDefault;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
