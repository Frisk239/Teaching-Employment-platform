package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Teacher;

/**
 * 教师Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 分页查询教师列表
     */
    IPage<Teacher> getTeacherPage(Integer current, Integer size, Long schoolId, String keyword, String department);

    /**
     * 获取教师统计数据
     *
     * @return 包含教师总数、覆盖学校数、涉及部门数、开设课程数的Map
     */
    java.util.Map<String, Object> getTeacherStatistics();

    /**
     * 查询所有教师列表(用于下拉框)
     */
    java.util.List<Teacher> getTeacherList(Long schoolId);

    /**
     * 根据用户ID查询教师信息
     */
    Teacher getTeacherByUserId(Long userId);

    /**
     * 获取教师工作负载统计
     *
     * @param teacherId 教师ID
     * @return 包含课程数量、总课时等统计信息的Map
     */
    java.util.Map<String, Object> getTeacherWorkload(Long teacherId);

    /**
     * 获取教师的课程列表
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    java.util.List<com.teaching.employment.entity.Course> getTeacherCourses(Long teacherId);

    /**
     * 导出教师数据到Excel
     *
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportTeachers(javax.servlet.http.HttpServletResponse response) throws java.io.IOException;

    /**
     * 导入教师数据
     *
     * @param file Excel文件
     * @return 导入结果消息
     * @throws IOException IO异常
     */
    String importTeachers(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException;

    /**
     * 下载教师导入模板
     *
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void downloadTemplate(javax.servlet.http.HttpServletResponse response) throws java.io.IOException;

    /**
     * 填充教师的关联数据(学校名称、用户姓名、手机号、邮箱)
     *
     * @param teachers 教师列表
     */
    void fillRelatedData(java.util.List<Teacher> teachers);
}
