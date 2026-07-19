package com.example.platform.mapper;

import com.example.platform.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User findByUsernameAndRole(@Param("username") String username, @Param("role") String role);

    User findById(@Param("id") Long id);

    int insert(User user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
