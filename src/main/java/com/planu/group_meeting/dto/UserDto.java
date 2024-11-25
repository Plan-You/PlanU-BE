package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.Role;
import com.planu.group_meeting.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignUpRequest {
        @NotBlank(message = "아이디를 입력해주세요!")
        private String username;

        @NotBlank(message = "비밀번호를 입력해주세요!")
        private String password;

        @NotBlank(message = "이름을 입력해주세요!")
        private String name;

        @NotBlank(message = "이메일 주소를 입력해주세요!")
        @Email(message = "올바른 이메일 주소를 입력해주세요!")
        private String email;

        public User toEntity() {
            return User.builder()
                    .username(this.username)
                    .password(this.password)
                    .name(this.name)
                    .email(this.email)
                    .role(Role.ROLE_USER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    public static class UserProfileRequest {
        private String username;

        private String profileImgUrl;

        private LocalDateTime birthDate;

    }
    @Getter
    public static class EmailRequest{
        private String email;
    }
    @Getter
    public static class EmailVerificationRequest{
        private String email;
        private String verificationCode;
    }

}
