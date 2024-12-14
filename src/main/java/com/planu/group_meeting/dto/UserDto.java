package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.UserTerms;
import com.planu.group_meeting.entity.common.Gender;
import com.planu.group_meeting.entity.common.ProfileStatus;
import com.planu.group_meeting.entity.common.Role;
import com.planu.group_meeting.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
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

        @NotBlank(message = "이메일 주소를 입력해주세요")
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
//    @Data
//    @NoArgsConstructor
//    public static class UserProfileRequest {
//        @NotNull(message = "생년월일을 입력해주세요")
//        @DateTimeFormat(pattern = "yyyy-MM-dd")
//        @Past(message = "생년월일은 오늘 이전 날짜여야 합니다.")
//        private LocalDate birthDate;
//
//        @NotNull(message = "성별을 입력해주세요.")
//        @Pattern(regexp = "[MF]", message = "성별은 'M' 또는 'F'만 입력 가능합니다.")
//        private String gender;
//
//        private MultipartFile profileImage;
//    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserRegistrationRequest {
        @NotNull(message = "생년월일을 입력해주세요")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = "생년월일은 오늘 이전 날짜여야 합니다.")
        private LocalDate birthDate;

        @NotNull(message = "성별을 입력해주세요.")
        @Pattern(regexp = "[MF]", message = "성별은 'M' 또는 'F'만 입력 가능합니다.")
        private String gender;

        //private MultipartFile profileImage;

        @Valid
        private UserTermsDto.TermsRequest termsRequest;
    }
    @Getter
    public static class EmailSendRequest{
        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "올바른 이메일 주소를 입력해주세요")
        private String email;

        @NotBlank(message = "이메일 인증 목적을 입력해주세요. (register, findUsername, findPassword)")
        private String purpose;
    }
    @Getter
    public static class EmailVerificationRequest{
        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "올바른 이메일 주소를 입력해주세요")
        private String email;

        @NotBlank(message = "인증코드를 입력해주세요")
        private String verificationCode;

        @NotBlank(message = "이메일 인증 목적을 입력해주세요. (register, findUsername, findPassword)")
        private String purpose;
    }

    @Getter
    public static class EmailRequest{
        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "이메일 주소 형식이 아닙니다")
        private String email;
    }

    @Getter
    public static class ChangePasswordRequest{
        @NotBlank(message = "아이디를 입력해주세요")
        private String username;

        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "이메일 주소 형식이 아닙니다")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String newPassword;
    }

    @Getter
    @Builder
    public static class UserInfoResponse{
        private String name;
        private String profileImage;
        private String username;
        private String email;
        private LocalDate birthday;
    }


}
