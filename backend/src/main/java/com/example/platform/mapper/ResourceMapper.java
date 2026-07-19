package com.example.platform.mapper;

import com.example.platform.entity.LearningResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ResourceMapper {
    List<Map<String, Object>> page(@Param("offset") int offset, @Param("limit") int limit,
                                   @Param("keyword") String keyword, @Param("typeId") Long typeId,
                                   @Param("publicOnly") boolean publicOnly);

    long count(@Param("keyword") String keyword, @Param("typeId") Long typeId,
               @Param("publicOnly") boolean publicOnly);

    Map<String, Object> findById(@Param("id") Long id);

    int increaseViewCount(@Param("id") Long id);

    int increaseDownloadCount(@Param("id") Long id);

    List<Map<String, Object>> commentList(@Param("resourceId") Long resourceId);

    int insertComment(@Param("resourceId") Long resourceId, @Param("userId") Long userId,
                      @Param("content") String content);

    int insert(LearningResource resource);

    int update(LearningResource resource);

    int deleteBatch(@Param("ids") List<Long> ids);
}
