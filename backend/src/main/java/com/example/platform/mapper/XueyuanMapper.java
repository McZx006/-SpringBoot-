package com.example.platform.mapper;

import com.example.platform.entity.Xueyuan;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface XueyuanMapper {
    List<Map<String, Object>> page(@Param("offset") int offset, @Param("limit") int limit,
                                   @Param("xuehao") String xuehao, @Param("name") String name);

    long count(@Param("xuehao") String xuehao, @Param("name") String name);

    Map<String, Object> findById(@Param("id") Long id);

    Map<String, Object> findByUserId(@Param("userId") Long userId);

    Map<String, Object> findByXuehao(@Param("xuehao") String xuehao);

    int insert(Xueyuan xueyuan);

    int update(Xueyuan xueyuan);

    int deleteBatch(@Param("ids") List<Long> ids);
}
