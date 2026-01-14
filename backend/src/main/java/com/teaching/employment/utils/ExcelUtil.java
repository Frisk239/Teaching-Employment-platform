package com.teaching.employment.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入导出工具类
 * 使用阿里巴巴EasyExcel组件
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public class ExcelUtil {

    /**
     * 导出Excel
     *
     * @param response HTTP响应
     * @param filename 文件名
     * @param dataList 数据列表
     * @param clazz    实体类Class对象
     * @param <T>      泛型
     */
    public static <T> void export(HttpServletResponse response, String filename, List<T> dataList, Class<T> clazz) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFilename + ".xlsx");

        EasyExcel.write(response.getOutputStream(), clazz)
                .sheet("Sheet1")
                .doWrite(dataList);
    }

    /**
     * 导入Excel
     *
     * @param file 上传的文件
     * @param clazz 实体类Class对象
     * @param <T>   泛型
     * @return 数据列表
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        return EasyExcel.read(file.getInputStream())
                .head(clazz)
                .sheet()
                .doReadSync();
    }

    /**
     * 学员导入Excel模板类
     */
    @Data
    @ContentRowHeight(20)
    @HeadRowHeight(30)
    @ColumnWidth(20)
    public static class StudentImportTemplate {

        @ExcelProperty(value = "学号", index = 0)
        private String studentNo;

        @ExcelProperty(value = "姓名", index = 1)
        private String realName;

        @ExcelProperty(value = "性别", index = 2)
        private String gender;

        @ExcelProperty(value = "身份证号", index = 3)
        private String idCard;

        @ExcelProperty(value = "手机号", index = 4)
        private String phone;

        @ExcelProperty(value = "邮箱", index = 5)
        private String email;

        @ExcelProperty(value = "学校", index = 6)
        private String schoolName;

        @ExcelProperty(value = "班级", index = 7)
        private String className;

        @ExcelProperty(value = "专业", index = 8)
        private String major;

        @ExcelProperty(value = "年级", index = 9)
        private String grade;

        @ExcelProperty(value = "家庭地址", index = 10)
        private String address;

        @ExcelProperty(value = "入学日期", index = 11)
        private String enrollmentDate;

        @ExcelProperty(value = "毕业日期", index = 12)
        private String graduationDate;

        @ExcelProperty(value = "政治面貌", index = 13)
        private String politicalStatus;

        @ExcelProperty(value = "民族", index = 14)
        private String nation;

        @ExcelProperty(value = "监护人姓名", index = 15)
        private String guardianName;

        @ExcelProperty(value = "监护人电话", index = 16)
        private String guardianPhone;
    }

    /**
     * 生成学员导入模板
     *
     * @param response HTTP响应
     */
    public static void downloadStudentTemplate(HttpServletResponse response) throws IOException {
        List<StudentImportTemplate> data = new ArrayList<>();
        // 添加示例数据
        StudentImportTemplate example = new StudentImportTemplate();
        example.setStudentNo("2024001");
        example.setRealName("张三");
        example.setGender("男");
        example.setIdCard("350102200001011234");
        example.setPhone("13800138000");
        example.setEmail("zhangsan@example.com");
        example.setSchoolName("福建师范大学");
        example.setClassName("计算机科学与技术1班");
        example.setMajor("计算机科学与技术");
        example.setGrade("2024级");
        example.setAddress("福建省福州市鼓楼区XX路XX号");
        example.setEnrollmentDate("2024-09-01");
        example.setGraduationDate("2028-06-30");
        example.setPoliticalStatus("共青团员");
        example.setNation("汉族");
        example.setGuardianName("张父");
        example.setGuardianPhone("13900139000");
        data.add(example);

        export(response, "学员导入模板", data, StudentImportTemplate.class);
    }

    /**
     * 学员导出Excel模板类
     */
    @Data
    @ContentRowHeight(20)
    @HeadRowHeight(30)
    @ColumnWidth(20)
    public static class StudentExportTemplate {

        @ExcelProperty(value = "学号", index = 0)
        private String studentNo;

        @ExcelProperty(value = "姓名", index = 1)
        private String realName;

        @ExcelProperty(value = "性别", index = 2)
        private String gender;

        @ExcelProperty(value = "出生日期", index = 3)
        private String birthDate;

        @ExcelProperty(value = "身份证号", index = 4)
        private String idCard;

        @ExcelProperty(value = "手机号", index = 5)
        private String phone;

        @ExcelProperty(value = "邮箱", index = 6)
        private String email;

        @ExcelProperty(value = "学校", index = 7)
        private String schoolName;

        @ExcelProperty(value = "班级", index = 8)
        private String className;

        @ExcelProperty(value = "专业", index = 9)
        private String major;

        @ExcelProperty(value = "年级", index = 10)
        private String grade;

        @ExcelProperty(value = "家庭地址", index = 11)
        private String address;

        @ExcelProperty(value = "入学日期", index = 12)
        private String enrollmentDate;

        @ExcelProperty(value = "毕业日期", index = 13)
        private String graduationDate;

        @ExcelProperty(value = "政治面貌", index = 14)
        private String politicalStatus;

        @ExcelProperty(value = "民族", index = 15)
        private String nation;

        @ExcelProperty(value = "监护人姓名", index = 16)
        private String guardianName;

        @ExcelProperty(value = "监护人电话", index = 17)
        private String guardianPhone;
    }
}
