package com.planu.group_meeting.entity;

import com.planu.group_meeting.entity.common.Gender;
import com.planu.group_meeting.entity.common.ProfileStatus;
import com.planu.group_meeting.entity.common.Role;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.planu.group_meeting.dto.UserDto.UserProfileUpdateRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Role role;
    private Gender gender;
    private String profileImgUrl;
    private LocalDate birthDate;
    private ProfileStatus profileStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateProfile(String profileImgUrl, String gender, LocalDate birthDate) {
        this.profileImgUrl = profileImgUrl;
        this.gender = Gender.valueOf(gender);
        this.birthDate = birthDate;
        this.profileStatus = ProfileStatus.COMPLETED;
    }

    public void updateProfile(UserProfileUpdateRequest request, String profileImgUrl){
        this.name = request.getName();
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.birthDate = request.getBirthDate();
        this.profileImgUrl = profileImgUrl;
    }

}
