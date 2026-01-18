package com.teaching.employment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.Internship;

import java.util.List;

/**
 * 实习经历Service接口
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-18
 */
public interface InternshipService extends IService<Internship> {

    /**
     * 根据用户ID查询实习经历列表
     *
     * @param userId 用户ID
     * @return 实习经历列表
     */
    List<Internship> getInternshipByUserId(Long userId);

    /**
     * 根据ID查询实习经历详情
     *
     * @param id 实习经历ID
     * @return 实习经历详情
     */
    Internship getInternshipById(Long id);

    /**
     * 创建实习经历
     *
     * @param internship 实习经历信息
     * @return 是否创建成功
     */
    boolean createInternship(Internship internship);

    /**
     * 更新实习经历
     *
     * @param internship 实习经历信息
     * @return 是否更新成功
     */
    boolean updateInternship(Internship internship);

    /**
     * 删除实习经历
     *
     * @param id 实习经历ID
     * @return 是否删除成功
     */
    boolean deleteInternship(Long id);

    /**
     * 验证实习经历是否属于指定用户
     *
     * @param id     实习经历ID
     * @param userId 用户ID
     * @return 是否属于该用户
     */
    boolean validateInternshipOwnership(Long id, Long userId);
}
