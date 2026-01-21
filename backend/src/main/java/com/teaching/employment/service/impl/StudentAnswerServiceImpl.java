package com.teaching.employment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.employment.entity.StudentAnswer;
import com.teaching.employment.mapper.StudentAnswerMapper;
import com.teaching.employment.service.StudentAnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentAnswerServiceImpl extends ServiceImpl<StudentAnswerMapper, StudentAnswer>
        implements StudentAnswerService {

    @Override
    public List<StudentAnswer> getAnswersByExamRecordId(Long examRecordId) {
        LambdaQueryWrapper<StudentAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAnswer::getExamRecordId, examRecordId);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveAnswers(List<StudentAnswer> answers) {
        if (answers == null || answers.isEmpty()) {
            return false;
        }
        return this.saveBatch(answers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeAnswer(Long answerId, String comment, Integer isCorrect) {
        StudentAnswer answer = this.getById(answerId);
        if (answer == null) {
            return false;
        }

        answer.setComment(comment);
        answer.setIsCorrect(isCorrect);

        return this.updateById(answer);
    }

    @Override
    public IPage<StudentAnswer> getAnswerPage(Integer current, Integer size, Long examRecordId) {
        Page<StudentAnswer> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentAnswer> wrapper = new LambdaQueryWrapper<>();

        if (examRecordId != null) {
            wrapper.eq(StudentAnswer::getExamRecordId, examRecordId);
        }

        wrapper.orderByDesc(StudentAnswer::getCreateTime);

        return this.page(page, wrapper);
    }
}
