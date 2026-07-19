package com.example.platform.service;

import com.example.platform.common.PageResult;
import com.example.platform.entity.ExamPaper;
import com.example.platform.entity.ExamQuestion;

import java.util.List;
import java.util.Map;

public interface ExamService {
    PageResult<Map<String, Object>> papers(int page, int limit, String keyword, boolean publicOnly);

    Map<String, Object> start(Long paperId);

    Map<String, Object> submit(Map<String, Object> request, Long userId);

    void savePaper(ExamPaper paper);

    void updatePaper(ExamPaper paper);

    void deletePapers(List<Long> ids);

    PageResult<Map<String, Object>> questions(int page, int limit, String keyword, Long paperId);

    void saveQuestion(ExamQuestion question);

    void updateQuestion(ExamQuestion question);

    void deleteQuestions(List<Long> ids);

    PageResult<Map<String, Object>> records(int page, int limit, String keyword, Long userId);

    Map<String, Object> recordDetail(Long id);
}
