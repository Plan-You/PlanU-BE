package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserDAO {
    void insertUser(User user);

    void updateUserProfile(@Param("userId") Long userId, @Param("user") User user);

    //void updateUserProfileImage(@Param("username")String username, @Param("profileImageUrl")String profileImageUrl);

    //void updateUserTerms(@Param("username")String username, @Param("termsRequest")UserDto.TermsRequest termsRequest);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
