package com.example.platform.service.impl;

import com.example.platform.common.PageResult;
import com.example.platform.entity.ExamPaper;
import com.example.platform.entity.ExamQuestion;
import com.example.platform.mapper.ExamMapper;
import com.example.platform.service.ExamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExamServiceImpl implements ExamService {
    private final ExamMapper examMapper;
    private final ObjectMapper objectMapper;

    public ExamServiceImpl(ExamMapper examMapper, ObjectMapper objectMapper) {
        this.examMapper = examMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageResult<Map<String, Object>> papers(int page, int limit, String keyword, boolean publicOnly) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                examMapper.paperPage(offset, limit, keyword, publicOnly),
                examMapper.paperCount(keyword, publicOnly),
                page,
                limit
        );
    }

    @Override
    public Map<String, Object> start(Long paperId) {
        Map<String, Object> paper = examMapper.paperDetail(paperId);
        if (paper == null) {
            throw new IllegalArgumentException("试卷不存在");
        }
        if (!Integer.valueOf(1).equals(paper.get("status"))) {
            throw new IllegalArgumentException("试卷暂未发布");
        }
        List<Map<String, Object>> questions = examMapper.questionsByPaper(paperId);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("当前试卷还没有试题");
        }
        for (Map<String, Object> question : questions) {
            question.remove("answer");
            question.remove("analysis");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("paper", paper);
        data.put("questions", questions);
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> submit(Map<String, Object> request, Long userId) {
        Long paperId = Long.valueOf(String.valueOf(request.get("paperId")));
        Map<String, Object> paper = examMapper.paperDetail(paperId);
        if (paper == null || !Integer.valueOf(1).equals(paper.get("status"))) {
            throw new IllegalArgumentException("试卷不存在或未发布");
        }
        List<Map<String, Object>> submittedAnswers = (List<Map<String, Object>>) request.get("answers");
        if (submittedAnswers == null) {
            submittedAnswers = Collections.emptyList();
        }

        Map<Long, String> answerMap = new HashMap<>();
        for (Map<String, Object> item : submittedAnswers) {
            answerMap.put(Long.valueOf(String.valueOf(item.get("questionId"))),
                    normalizeAnswer(String.valueOf(item.getOrDefault("answer", ""))));
        }

        List<Map<String, Object>> questions = examMapper.questionsByPaper(paperId);
        int score = 0;
        int correctCount = 0;
        int wrongCount = 0;
        for (Map<String, Object> question : questions) {
            Long questionId = Long.valueOf(String.valueOf(question.get("id")));
            String correctAnswer = normalizeAnswer(String.valueOf(question.get("answer")));
            String userAnswer = answerMap.getOrDefault(questionId, "");
            if (correctAnswer.equals(userAnswer)) {
                score += Integer.parseInt(String.valueOf(question.get("score")));
                correctCount++;
            } else {
                wrongCount++;
            }
        }

        String answerJson = toJson(submittedAnswers);
        examMapper.insertRecord(paperId, userId, score, answerJson, correctCount, wrongCount);
        Long recordId = examMapper.lastInsertRecordId();

        Map<String, Object> data = new HashMap<>();
        data.put("recordId", recordId);
        data.put("score", score);
        data.put("totalScore", totalScore(questions));
        data.put("correctCount", correctCount);
        data.put("wrongCount", wrongCount);
        return data;
    }

    @Override
    public void savePaper(ExamPaper paper) {
        validatePaper(paper, false);
        if (paper.getStatus() == null) {
            paper.setStatus(1);
        }
        examMapper.insertPaper(paper);
    }

    @Override
    public void updatePaper(ExamPaper paper) {
        validatePaper(paper, true);
        examMapper.updatePaper(paper);
    }

    @Override
    public void deletePapers(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            examMapper.deletePapers(ids);
        }
    }

    @Override
    public PageResult<Map<String, Object>> questions(int page, int limit, String keyword, Long paperId) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                examMapper.questionPage(offset, limit, keyword, paperId),
                examMapper.questionCount(keyword, paperId),
                page,
                limit
        );
    }

    @Override
    public void saveQuestion(ExamQuestion question) {
        normalizeQuestionDefaults(question);
        validateQuestion(question, false);
        examMapper.insertQuestion(question);
    }

    @Override
    public void updateQuestion(ExamQuestion question) {
        normalizeQuestionDefaults(question);
        validateQuestion(question, true);
        examMapper.updateQuestion(question);
    }

    @Override
    public void deleteQuestions(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            examMapper.deleteQuestions(ids);
        }
    }

    @Override
    public PageResult<Map<String, Object>> records(int page, int limit, String keyword, Long userId) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                examMapper.recordPage(offset, limit, keyword, userId),
                examMapper.recordCount(keyword, userId),
                page,
                limit
        );
    }

    @Override
    public Map<String, Object> recordDetail(Long id) {
        Map<String, Object> record = examMapper.recordDetail(id);
        if (record == null) {
            throw new IllegalArgumentException("考试记录不存在");
        }
        Long paperId = Long.valueOf(String.valueOf(record.get("paperId")));
        List<Map<String, Object>> questions = examMapper.questionsByPaper(paperId);
        Map<Long, String> userAnswerMap = parseAnswerMap(String.valueOf(record.getOrDefault("answerJson", "[]")));
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> question : questions) {
            Map<String, Object> item = new HashMap<>(question);
            Long questionId = Long.valueOf(String.valueOf(question.get("id")));
            String correctAnswer = normalizeAnswer(String.valueOf(question.getOrDefault("answer", "")));
            String userAnswer = userAnswerMap.getOrDefault(questionId, "");
            item.put("userAnswer", userAnswer);
            item.put("correct", correctAnswer.equals(userAnswer));
            items.add(item);
        }
        record.put("items", items);
        record.put("totalScore", totalScore(questions));
        return record;
    }

    private void normalizeQuestionDefaults(ExamQuestion question) {
        if (question.getScore() == null) {
            question.setScore(5);
        }
        if (question.getSort() == null) {
            question.setSort(0);
        }
        if (question.getOptionsJson() == null || question.getOptionsJson().trim().isEmpty()) {
            question.setOptionsJson("[]");
        }
    }

    private int totalScore(List<Map<String, Object>> questions) {
        int total = 0;
        for (Map<String, Object> question : questions) {
            total += Integer.parseInt(String.valueOf(question.get("score")));
        }
        return total;
    }

    private String normalizeAnswer(String answer) {
        String[] parts = answer.replace("，", ",").split(",");
        List<String> cleaned = new ArrayList<>();
        for (String part : parts) {
            String value = part.trim();
            if (!value.isEmpty()) {
                cleaned.add(value);
            }
        }
        Collections.sort(cleaned);
        return String.join(",", cleaned);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("答案格式错误");
        }
    }

    private Map<Long, String> parseAnswerMap(String answerJson) {
        try {
            List<Map<String, Object>> answers = objectMapper.readValue(answerJson, new TypeReference<List<Map<String, Object>>>() {});
            Map<Long, String> answerMap = new HashMap<>();
            for (Map<String, Object> answer : answers) {
                Long questionId = Long.valueOf(String.valueOf(answer.get("questionId")));
                String value = normalizeAnswer(String.valueOf(answer.getOrDefault("answer", "")));
                answerMap.put(questionId, value);
            }
            return answerMap;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private void validatePaper(ExamPaper paper, boolean requireId) {
        if (requireId && paper.getId() == null) {
            throw new IllegalArgumentException("试卷ID不能为空");
        }
        if (paper.getName() == null || paper.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("试卷名称不能为空");
        }
        if (paper.getDescription() == null || paper.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("试卷说明不能为空");
        }
        if (paper.getDuration() == null || paper.getDuration() <= 0) {
            throw new IllegalArgumentException("考试时长必须大于0");
        }
        if (paper.getTotalScore() == null || paper.getTotalScore() <= 0) {
            throw new IllegalArgumentException("试卷总分必须大于0");
        }
    }

    private void validateQuestion(ExamQuestion question, boolean requireId) {
        if (requireId && question.getId() == null) {
            throw new IllegalArgumentException("试题ID不能为空");
        }
        if (question.getPaperId() == null) {
            throw new IllegalArgumentException("所属试卷不能为空");
        }
        if (question.getQuestionName() == null || question.getQuestionName().trim().isEmpty()) {
            throw new IllegalArgumentException("题目不能为空");
        }
        if (question.getQuestionType() == null || question.getQuestionType().trim().isEmpty()) {
            throw new IllegalArgumentException("题型不能为空");
        }
        if (question.getAnswer() == null || question.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("答案不能为空");
        }
        if (question.getAnalysis() == null || question.getAnalysis().trim().isEmpty()) {
            throw new IllegalArgumentException("解析不能为空");
        }
        if (question.getScore() == null || question.getScore() <= 0) {
            throw new IllegalArgumentException("试题分值必须大于0");
        }
        validateQuestionTypeRule(question);
    }

    private void validateQuestionTypeRule(ExamQuestion question) {
        List<Map<String, Object>> options;
        try {
            options = objectMapper.readValue(question.getOptionsJson(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("选项JSON格式不正确");
        }
        String type = question.getQuestionType();
        String answer = normalizeAnswer(question.getAnswer());
        if ("single".equals(type) || "multiple".equals(type)) {
            if (options == null || options.size() < 2) {
                throw new IllegalArgumentException("选择题至少需要两个选项");
            }
            Set<String> keys = new HashSet<>();
            for (Map<String, Object> option : options) {
                keys.add(String.valueOf(option.getOrDefault("key", "")).trim());
            }
            for (String key : answer.split(",")) {
                if (!key.isEmpty() && !keys.contains(key)) {
                    throw new IllegalArgumentException("答案必须来自已有选项");
                }
            }
            if ("single".equals(type) && answer.contains(",")) {
                throw new IllegalArgumentException("单选题只能有一个答案");
            }
        }
        if ("judge".equals(type) && (options == null || options.size() != 2)) {
            throw new IllegalArgumentException("判断题必须有两个选项");
        }
        if ("fill".equals(type) && !question.getOptionsJson().trim().equals("[]") && !question.getOptionsJson().trim().isEmpty()) {
            throw new IllegalArgumentException("填空题选项应为空数组");
        }
    }
}
