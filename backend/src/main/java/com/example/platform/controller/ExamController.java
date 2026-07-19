package com.example.platform.controller;

import com.example.platform.common.RoleConstants;
import com.example.platform.common.PageResult;
import com.example.platform.common.Result;
import com.example.platform.common.SecurityConstants;
import com.example.platform.entity.ExamPaper;
import com.example.platform.entity.ExamQuestion;
import com.example.platform.service.ExamService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/exampapers/page")
    public Result<PageResult<Map<String, Object>>> papers(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int limit,
                                                          @RequestParam(required = false) String keyword,
                                                          @RequestParam(defaultValue = "true") boolean publicOnly,
                                                          @RequestAttribute(value = "role", required = false) Object roleAttr) {
        boolean effectivePublicOnly = publicOnly || !RoleConstants.ADMIN.equals(String.valueOf(roleAttr));
        return Result.success(examService.papers(page, limit, keyword, effectivePublicOnly));
    }

    @GetMapping("/exam/start/{paperId}")
    public Result<Map<String, Object>> start(@PathVariable Long paperId) {
        return Result.success(examService.start(paperId));
    }

    @PostMapping("/exam/submit")
    public Result<Map<String, Object>> submit(@RequestBody Map<String, Object> request,
                                              @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        return Result.success(examService.submit(request, userId));
    }

    @PostMapping("/exampapers/save")
    public Result<String> savePaper(@RequestBody ExamPaper paper) {
        examService.savePaper(paper);
        return Result.success("save success");
    }

    @PutMapping("/exampapers/update")
    public Result<String> updatePaper(@RequestBody ExamPaper paper) {
        examService.updatePaper(paper);
        return Result.success("update success");
    }

    @DeleteMapping("/exampapers/delete")
    public Result<String> deletePapers(@RequestBody List<Long> ids) {
        examService.deletePapers(ids);
        return Result.success("delete success");
    }

    @GetMapping("/examquestions/page")
    public Result<PageResult<Map<String, Object>>> questions(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int limit,
                                                             @RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) Long paperId) {
        return Result.success(examService.questions(page, limit, keyword, paperId));
    }

    @PostMapping("/examquestions/save")
    public Result<String> saveQuestion(@RequestBody ExamQuestion question) {
        examService.saveQuestion(question);
        return Result.success("save success");
    }

    @PutMapping("/examquestions/update")
    public Result<String> updateQuestion(@RequestBody ExamQuestion question) {
        examService.updateQuestion(question);
        return Result.success("update success");
    }

    @DeleteMapping("/examquestions/delete")
    public Result<String> deleteQuestions(@RequestBody List<Long> ids) {
        examService.deleteQuestions(ids);
        return Result.success("delete success");
    }

    @GetMapping("/examrecords/page")
    public Result<PageResult<Map<String, Object>>> records(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int limit,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) Long userId,
                                                           @RequestAttribute(value = "userId", required = false) Object userIdAttr,
                                                           @RequestAttribute(value = "role", required = false) Object roleAttr) {
        Long queryUserId = resolveQueryUserId(userId, userIdAttr, roleAttr);
        return Result.success(examService.records(page, limit, keyword, queryUserId));
    }

    @GetMapping("/examrecords/{id}")
    public Result<Map<String, Object>> recordDetail(@PathVariable Long id,
                                                    @RequestAttribute(value = "userId", required = false) Object userIdAttr,
                                                    @RequestAttribute(value = "role", required = false) Object roleAttr) {
        Map<String, Object> detail = examService.recordDetail(id);
        if (!RoleConstants.ADMIN.equals(String.valueOf(roleAttr)) && userIdAttr != null) {
            Long currentUserId = Long.valueOf(String.valueOf(userIdAttr));
            Long recordUserId = Long.valueOf(String.valueOf(detail.get("userId")));
            if (!currentUserId.equals(recordUserId)) {
                throw new IllegalArgumentException("无权查看该考试记录");
            }
        }
        return Result.success(detail);
    }

    @GetMapping("/wrong-questions/page")
    public Result<PageResult<Map<String, Object>>> wrongQuestions(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int limit,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Long userId,
                                                                  @RequestAttribute(value = "userId", required = false) Object userIdAttr,
                                                                  @RequestAttribute(value = "role", required = false) Object roleAttr) {
        Long queryUserId = resolveQueryUserId(userId, userIdAttr, roleAttr);
        return Result.success(examService.records(page, limit, keyword, queryUserId));
    }

    private Long resolveQueryUserId(Long requestedUserId, Object currentUserId, Object role) {
        if ("admin".equals(String.valueOf(role))) {
            return requestedUserId;
        }
        if (currentUserId == null) {
            return requestedUserId;
        }
        return Long.valueOf(String.valueOf(currentUserId));
    }
}
