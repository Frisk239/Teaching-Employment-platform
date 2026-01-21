/**
 * 试卷题目关联 API
 */
import { http } from '@/utils/request'

/**
 * 获取试卷已添加的题目ID列表
 */
export function getExamQuestionIdsApi(examId: number) {
  return http.get<number[]>(`/exam-question/exam/${examId}/question-ids`)
}

/**
 * 批量添加题目到试卷
 */
export function batchAddQuestionsApi(questions: any[]) {
  return http.post('/exam-question/batch', questions)
}
