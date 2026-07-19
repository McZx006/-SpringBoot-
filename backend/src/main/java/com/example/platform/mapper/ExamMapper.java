package com.example.platform.mapper;

import com.example.platform.entity.ExamPaper;
import com.example.platform.entity.ExamQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExamMapper {
    List<Map<String, Object>> paperPage(@Param("offset") int offset, @Param("limit") int limit,
                                        @Param("keyword") String keyword, @Param("publicOnly") boolean publicOnly);

    long paperCount(@Param("keyword") String keyword, @Param("publicOnly") boolean publicOnly);

    Map<String, Object> paperDetail(@Param("paperId") Long paperId);

    List<Map<String, Object>> questionsByPaper(@Param("paperId") Long paperId);

    List<Map<String, Object>> questionPage(@Param("offset") int offset, @Param("limit") int limit,
                                           @Param("keyword") String keyword, @Param("paperId") Long paperId);

    long questionCount(@Param("keyword") String keyword, @Param("paperId") Long paperId);

    int insertRecord(@Param("paperId") Long paperId, @Param("userId") Long userId,
                     @Param("score") int score, @Param("answerJson") String answerJson,
                     @Param("correctCount") int correctCount, @Param("wrongCount") int wrongCount);

    Long lastInsertRecordId();

    int insertPaper(ExamPaper paper);

    int updatePaper(ExamPaper paper);

    int deletePapers(@Param("ids") List<Long> ids);

    int insertQuestion(ExamQuestion question);

    int updateQuestion(ExamQuestion question);

    int deleteQuestions(@Param("ids") List<Long> ids);

    List<Map<String, Object>> recordPage(@Param("offset") int offset, @Param("limit") int limit,
                                         @Param("keyword") String keyword, @Param("userId") Long userId);

    long recordCount(@Param("keyword") String keyword, @Param("userId") Long userId);

    Map<String, Object> recordDetail(@Param("id") Long id);
}
