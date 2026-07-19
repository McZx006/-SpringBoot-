package com.example.platform.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

public interface TokenMapper {
    int insert(@Param("userId") Long userId, @Param("token") String token, @Param("expireTime") Date expireTime);

    Map<String, Object> findByToken(@Param("token") String token);

    int deleteByToken(@Param("token") String token);
}

