package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import static com.planu.group_meeting.dto.ScheduleDto.BirthdayPerson;

@Mapper
public interface UserDAO {
    void insertUser(User user);

    void updateUserProfile(@Param("userId") Long userId, @Param("user") User user);

    User findByUsername(String username);

    Long findIdByUsername(String username);

    User findById(Long id);

    String findUsernameByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void updatePasswordByUsername(@Param("username") String username, @Param("newPassword") String newPassword);

    boolean existsBirthdayByDate(Long userId, LocalDate date);

    List<BirthdayPerson> findBirthdayByDate(Long userId, LocalDate startDate, LocalDate endDate);

    String findNameById(Long userId);

    String findProfileImageById(Long userId);

    LocalDate findBirthdayById(Long groupMemberIds);

    String findUsernameById(Long userId);
}
