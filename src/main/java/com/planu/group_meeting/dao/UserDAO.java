package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserDAO {
    void insertUser(User user);

    void updateUserProfile(@Param("userId") Long userId, @Param("user") User user);

    User findByUsername(String username);

    String findUsernameByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void updatePasswordByUsername(@Param("username") String username, @Param("newPassword") String newPassword);

}
