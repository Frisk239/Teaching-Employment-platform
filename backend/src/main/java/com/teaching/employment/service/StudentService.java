package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 学员Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学员列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param schoolId 学校ID（可选）
     * @param keyword  关键词（可选）
     * @return 学员分页列表
     */
    IPage<Student> getStudentPage(Integer current, Integer size, Long schoolId, String keyword);

    /**
     * Excel批量导入学员
     *
     * @param file Excel文件
     * @return 导入结果
     */
    String importStudents(MultipartFile file) throws IOException;

    /**
     * 下载学员导入模板
     *
     * @param response HTTP响应
     */
    void downloadTemplate(HttpServletResponse response) throws IOException;

    /**
     * 根据学号查询学员
     *
     * @param studentNo 学号
     * @return 学员信息
     */
    Student getStudentByStudentNo(String studentNo);

    /**
     * 根据ID查询学员详情(包含关联信息)
     *
     * @param id 学员ID
     * @return 学员详情
     */
    Student getStudentWithDetails(Long id);
}
