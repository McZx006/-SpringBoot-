package com.example.platform.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ResourceTypeMapper {
    List<Map<String, Object>> list();

    Map<String, Object> findByName(@Param("name") String name);

    List<Map<String, Object>> page(@Param("offset") int offset, @Param("limit") int limit,
                                   @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    int insert(@Param("name") String name, @Param("sort") Integer sort);

    int update(@Param("id") Long id, @Param("name") String name, @Param("sort") Integer sort);

    int deleteBatch(@Param("ids") List<Long> ids);
}
