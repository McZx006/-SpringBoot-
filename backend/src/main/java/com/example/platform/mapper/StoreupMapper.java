package com.example.platform.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StoreupMapper {
    List<Map<String, Object>> page(@Param("offset") int offset, @Param("limit") int limit,
                                   @Param("userId") Long userId, @Param("keyword") String keyword);

    long count(@Param("userId") Long userId, @Param("keyword") String keyword);

    int insert(@Param("userId") Long userId, @Param("refId") Long refId,
               @Param("type") String type, @Param("title") String title);

    int delete(@Param("userId") Long userId, @Param("refId") Long refId, @Param("type") String type);

    int deleteBatch(@Param("ids") List<Long> ids);
}

