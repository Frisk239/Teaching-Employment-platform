package com.teaching.employment.enums;

/**
 * 角色类型枚举
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public enum RoleTypeEnum {

    /**
     * 管理员
     */
    ADMIN(1, "admin", "管理员"),

    /**
     * 学院负责人
     */
    COLLEGE_HEAD(2, "college_head", "学院负责人"),

    /**
     * 教师
     */
    TEACHER(3, "teacher", "教师"),

    /**
     * 学员
     */
    STUDENT(4, "student", "学员"),

    /**
     * 企业对接人
     */
    ENTERPRISE_CONTACT(5, "enterprise_contact", "企业对接人");

    private final Integer code;
    private final String value;
    private final String description;

    RoleTypeEnum(Integer code, String value, String description) {
        this.code = code;
        this.value = value;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据value获取枚举
     */
    public static RoleTypeEnum getByValue(String value) {
        for (RoleTypeEnum roleTypeEnum : values()) {
            if (roleTypeEnum.getValue().equals(value)) {
                return roleTypeEnum;
            }
        }
        return null;
    }

    /**
     * 根据code获取枚举
     */
    public static RoleTypeEnum getByCode(Integer code) {
        for (RoleTypeEnum roleTypeEnum : values()) {
            if (roleTypeEnum.getCode().equals(code)) {
                return roleTypeEnum;
            }
        }
        return null;
    }
}
