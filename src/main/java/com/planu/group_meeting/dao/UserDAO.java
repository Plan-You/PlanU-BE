package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.UserDto.UserProfileRequest;
import com.planu.group_meeting.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {
    void insertUser(User user);
    void updateUserProfile(UserProfileRequest userDto);
    void updateUserProfileImage(String username, String profileImageUrl);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
