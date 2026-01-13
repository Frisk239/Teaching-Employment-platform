package com.teaching.employment.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学生Excel导入导出实体
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Data
@HeadRowHeight(20)
@ContentRowHeight(18)
public class StudentExcel {

    @ExcelProperty(value = "学号", index = 0)
    @ColumnWidth(15)
    private String studentNo;

    @ExcelProperty(value = "姓名", index = 1)
    @ColumnWidth(12)
    private String realName;

    @ExcelProperty(value = "性别", index = 2)
    @ColumnWidth(8)
    private String gender;

    @ExcelProperty(value = "身份证号", index = 3)
    @ColumnWidth(18)
    private String idCard;

    @ExcelProperty(value = "手机号", index = 4)
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 5)
    @ColumnWidth(20)
    private String email;

    @ExcelProperty(value = "学校", index = 6)
    @ColumnWidth(20)
    private String schoolName;

    @ExcelProperty(value = "班级", index = 7)
    @ColumnWidth(15)
    private String className;

    @ExcelProperty(value = "专业", index = 8)
    @ColumnWidth(15)
    private String major;

    @ExcelProperty(value = "年级", index = 9)
    @ColumnWidth(10)
    private String grade;

    @ExcelProperty(value = "出生日期", index = 10)
    @ColumnWidth(15)
    private String birthDate;

    @ExcelProperty(value = "入学日期", index = 11)
    @ColumnWidth(15)
    private String enrollmentDate;

    @ExcelProperty(value = "预计毕业日期", index = 12)
    @ColumnWidth(15)
    private String graduationDate;

    @ExcelProperty(value = "政治面貌", index = 13)
    @ColumnWidth(12)
    private String politicalStatus;

    @ExcelProperty(value = "民族", index = 14)
    @ColumnWidth(10)
    private String nation;

    @ExcelProperty(value = "家庭地址", index = 15)
    @ColumnWidth(25)
    private String address;

    @ExcelProperty(value = "监护人姓名", index = 16)
    @ColumnWidth(12)
    private String guardianName;

    @ExcelProperty(value = "监护人电话", index = 17)
    @ColumnWidth(15)
    private String guardianPhone;
}
