package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.UserTerms;
import com.planu.group_meeting.entity.common.Gender;
import com.planu.group_meeting.entity.common.ProfileStatus;
import com.planu.group_meeting.entity.common.Role;
import com.planu.group_meeting.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignUpRequest {
        @NotBlank(message = "아이디를 입력해주세요")
        @Pattern(regexp = "^[a-z0-9]{5,12}$", message = "아이디는 5~12자의 영어 소문자와 숫자만 입력 가능합니다.")
        private String username;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;

        @NotBlank(message = "이름을 입력해주세요")
        @Pattern(regexp = "^[가-힣]{2,6}$", message = "이름은 2자 이상 6자 이하의 한글만 입력 가능합니다.")
        private String name;

        @NotBlank(message = "이메일 주소를 입력해주세요!")
        @Email(message = "올바른 이메일 주소를 입력해주세요")
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
    @Data
    @NoArgsConstructor
    public static class UserProfileRequest {
        @NotNull(message = "생년월일을 입력해주세요")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "생년월일은 오늘 이전 날짜여야 합니다.")
        private LocalDate birthDate;

        @NotNull(message = "성별을 입력해주세요.")
        @Pattern(regexp = "[MF]", message = "성별은 'M' 또는 'F'만 입력 가능합니다.")
        private String gender;

        private MultipartFile profileImage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserRegistrationRequest {
        @Valid
        private UserProfileRequest userProfileRequest;
        @Valid
        private UserTermsDto.TermsRequest termsRequest;
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
