package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.QuestionBank;
import com.teaching.employment.mapper.QuestionBankMapper;
import com.teaching.employment.service.QuestionBankService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
        implements QuestionBankService {

    @Override
    public IPage<QuestionBank> getQuestionPage(Integer current, Integer size,
                                               String knowledgePoint, String[] questionType,
                                               String keyword, String difficulty, Long companyId) {
        Page<QuestionBank> page = new Page<>(current, size);
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();

        // 知识点筛选（精确匹配）
        if (StrUtil.isNotBlank(knowledgePoint)) {
            wrapper.eq(QuestionBank::getKnowledgePoint, knowledgePoint);
        }

        // 题型筛选（支持多选，使用 IN 查询）
        if (questionType != null && questionType.length > 0) {
            wrapper.in(QuestionBank::getQuestionType, questionType);
        }

        // 关键词筛选（模糊搜索题目内容）
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(QuestionBank::getQuestionText, keyword);
        }

        // 难度筛选（精确匹配）
        if (StrUtil.isNotBlank(difficulty)) {
            wrapper.eq(QuestionBank::getDifficulty, difficulty);
        }

        // 企业筛选
        if (companyId != null) {
            wrapper.eq(QuestionBank::getCompanyId, companyId);
        }

        // 只查询激活状态的题目
        wrapper.eq(QuestionBank::getStatus, QuestionBank.STATUS_ACTIVE);

        // 按创建时间倒序排列
        wrapper.orderByDesc(QuestionBank::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<String> getAllKnowledgePoints() {
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(QuestionBank::getKnowledgePoint);
        wrapper.eq(QuestionBank::getStatus, QuestionBank.STATUS_ACTIVE);
        wrapper.groupBy(QuestionBank::getKnowledgePoint);
        return this.listObjs(wrapper, obj -> (String) obj);
    }
}
