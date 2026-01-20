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
 * 人才库实体类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@Data
@TableName("t_talent_pool")
@ApiModel(description = "人才库信息")
public class TalentPool implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "人才库ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "企业ID")
    private Long companyId;

    @ApiModelProperty(value = "学员ID")
    private Long studentId;

    @ApiModelProperty(value = "简历ID")
    private Long resumeId;

    @ApiModelProperty(value = "来源：manual-手动添加 application-求职申请 interview-面试推荐")
    private String source;

    @ApiModelProperty(value = "来源ID（如申请ID、面试ID）")
    private Long sourceId;

    @ApiModelProperty(value = "意向职位")
    private String positionName;

    @ApiModelProperty(value = "人才标签（多个标签用逗号分隔）")
    private String tags;

    @ApiModelProperty(value = "人才分类：frontend-前端 backend-后端 mobile-移动端 product-产品 design-设计 operation-运营 other-其他")
    private String category;

    @ApiModelProperty(value = "技能标签（JSON数组格式）")
    private String skillTags;

    @ApiModelProperty(value = "经验级别：junior-初级 middle-中级 senior-高级 expert-专家")
    private String experienceLevel;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "期望薪资最低")
    private BigDecimal expectedSalaryMin;

    @ApiModelProperty(value = "期望薪资最高")
    private BigDecimal expectedSalaryMax;

    @ApiModelProperty(value = "期望城市")
    private String expectedCity;

    @ApiModelProperty(value = "人才评分（1-5星）")
    private Integer rating;

    @ApiModelProperty(value = "优先级：low-低 normal-中 high-高 urgent-紧急")
    private String priority;

    @ApiModelProperty(value = "状态：active-激活 contacted-已联系 locked-已锁定 inactive-未激活")
    private String status;

    @ApiModelProperty(value = "最后联系日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastContactDate;

    @ApiModelProperty(value = "最后联系方式")
    private String lastContactMethod;

    @ApiModelProperty(value = "下次联系日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextContactDate;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "联系次数")
    private Integer contactCount;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 关联查询字段（不映射到数据库） ====================

    @ApiModelProperty(value = "学员姓名")
    @TableField(exist = false)
    private String studentName;

    @ApiModelProperty(value = "学员手机号")
    @TableField(exist = false)
    private String studentPhone;

    @ApiModelProperty(value = "学员邮箱")
    @TableField(exist = false)
    private String studentEmail;

    @ApiModelProperty(value = "学员头像")
    @TableField(exist = false)
    private String studentAvatar;

    @ApiModelProperty(value = "企业名称")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty(value = "学校名称")
    @TableField(exist = false)
    private String schoolName;

    @ApiModelProperty(value = "专业")
    @TableField(exist = false)
    private String major;

    @ApiModelProperty(value = "年级")
    @TableField(exist = false)
    private String grade;
}
