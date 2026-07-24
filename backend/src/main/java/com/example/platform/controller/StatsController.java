package com.example.platform.controller;

import com.example.platform.common.Result;
import com.example.platform.mapper.ContentMapper;
import com.example.platform.mapper.ExamMapper;
import com.example.platform.mapper.ResourceMapper;
import com.example.platform.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final ResourceMapper resourceMapper;
    private final ExamMapper examMapper;
    private final ContentMapper contentMapper;
    private final UserMapper userMapper;

    public StatsController(ResourceMapper resourceMapper, ExamMapper examMapper,
                          ContentMapper contentMapper, UserMapper userMapper) {
        this.resourceMapper = resourceMapper;
        this.examMapper = examMapper;
        this.contentMapper = contentMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("resourceCount", resourceMapper.count(null, null, true));
        stats.put("examCount", examMapper.paperCount(null, true));
        stats.put("forumCount", contentMapper.forumCount(null, true));
        stats.put("userCount", userMapper.countAll());
        return Result.success(stats);
    }
}
