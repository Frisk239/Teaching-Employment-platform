package com.teaching.employment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.employment.entity.QuestionBank;

import java.util.List;

public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 分页查询题库
     *
     * @param current 当前页
     * @param size 每页大小
     * @param knowledgePoint 知识点（可选）
     * @param questionType 题型数组（可选，支持多选）
     * @param keyword 关键词（可选，用于搜索题目内容）
     * @param difficulty 难度（可选）
     * @param companyId 企业ID（可选）
     * @return 分页结果
     */
    IPage<QuestionBank> getQuestionPage(Integer current, Integer size,
                                        String knowledgePoint, String[] questionType,
                                        String keyword, String difficulty, Long companyId);

    /**
     * 获取所有知识点分类
     */
    List<String> getAllKnowledgePoints();
}
