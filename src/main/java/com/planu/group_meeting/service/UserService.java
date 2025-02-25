package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dao.UserTermsDAO;
import com.planu.group_meeting.dto.TokenDto;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.entity.UserTerms;
import com.planu.group_meeting.entity.common.CertificationPurpose;
import com.planu.group_meeting.entity.common.ProfileStatus;
import com.planu.group_meeting.exception.user.*;
import com.planu.group_meeting.jwt.JwtUtil;
import com.planu.group_meeting.service.file.S3Uploader;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.planu.group_meeting.dto.UserDto.*;
import static com.planu.group_meeting.dto.UserTermsDto.TermsRequest;
import static com.planu.group_meeting.jwt.JwtUtil.REFRESH_TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
public class UserService{

    private static final String VERIFIED_EMAIL_KEY = "verifiedEmail : %s : %s";
    private static final String AUTH_CODE_KEY = "authCode : %s : %s";
    private static final long AUTH_CODE_EXPIRATION_TIME = 300000L; // 5분
    private static final String BASE_PROFILE_IMAGE = "https://planu-storage-main.s3.ap-northeast-2.amazonaws.com/defaultProfile.png";

    private final UserDAO userDAO;
    private final UserTermsDAO userTermsDAO;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;
    private final S3Uploader s3Uploader;

    public boolean isDuplicatedUsername(String username) {
        return userDAO.existsByUsername(username);
    }

    public boolean isDuplicatedEmail(String email) {
        return userDAO.existsByEmail(email);
    }

    @Transactional
    public void createUser(SignUpRequest userDto) {
        validateDuplicateUser(userDto.getUsername(), userDto.getEmail());
        validateEmailVerification(userDto.getEmail(), CertificationPurpose.REGISTER);

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDAO.insertUser(userDto.toEntity());
    }

    @Transactional
    public void createUserProfile(Long userId, UserRegistrationRequest registrationRequest, MultipartFile profileImage) {
        String profileImageUrl = BASE_PROFILE_IMAGE;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Uploader.uploadFile(profileImage);
        }
        User user = new User();
        user.updateProfile(profileImageUrl, registrationRequest.getGender(), registrationRequest.getBirthDate());
        userDAO.updateUserProfile(userId, user);

        TermsRequest termsRequest = registrationRequest.getTermsRequest();

        UserTerms userTerms = termsRequest.toEntity(userId);
        userTermsDAO.saveTerms(userTerms);
    }


    public String findUsername(EmailRequest emailRequest) {
        validateEmailVerification(emailRequest.getEmail(), CertificationPurpose.FIND_USERNAME);

        String username = userDAO.findUsernameByEmail(emailRequest.getEmail());
        if (username == null) {
            throw new NotFoundUserException();
        }
        return username;
    }

    @Transactional
    public void updatePassword(ChangePasswordRequest changePasswordRequest) {
        validateUsernameAndEmail(changePasswordRequest.getUsername(), changePasswordRequest.getEmail());
        validateEmailVerification(changePasswordRequest.getEmail(), CertificationPurpose.FIND_PASSWORD);

        String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        userDAO.updatePasswordByUsername(changePasswordRequest.getUsername(), encodedPassword);
    }

    public boolean isUserProfileCompleted(String username) {
        User user = userDAO.findByUsername(username);
        return ProfileStatus.COMPLETED.equals(user.getProfileStatus());
    }

    public TokenDto reissueAccessToken(String refresh) {
        validateRefreshToken(refresh);

        String username = jwtUtil.getUsername(refresh);
        String storedRefresh = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + username);
        if (storedRefresh == null || !storedRefresh.equals(refresh)) {
            System.out.println("리프레쉬 토큰 에러 발생2 ->" + refresh);
            throw new InvalidRefreshTokenException();
        }
        redisTemplate.delete(username);

        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createAccessToken(username, role);
        String newRefresh = jwtUtil.createRefreshToken(username, role);
        System.out.println(username + " 토큰 재발행 성공");
        return new TokenDto(newAccess, newRefresh);
    }

    public void sendCodeToEmail(EmailSendRequest emailDto) throws MessagingException {
        if(CertificationPurpose.REGISTER==emailDto.getPurpose() || CertificationPurpose.CHANGE_EMAIL==emailDto.getPurpose()
        && isDuplicatedEmail(emailDto.getEmail())){
            throw new DuplicatedEmailException();
        }

        String authCode = generateRandomCode();
        String key = String.format(AUTH_CODE_KEY, emailDto.getEmail(), emailDto.getPurpose());
        redisTemplate.opsForValue().set(key, authCode, AUTH_CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        mailService.sendVerificationCode(emailDto.getEmail(), authCode);
    }

    public void verifyEmailCode(EmailVerificationRequest emailVerificationDto) {
        String key = String.format(AUTH_CODE_KEY, emailVerificationDto.getEmail(), emailVerificationDto.getPurpose());
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new ExpiredAuthCodeException();
        }
        if (!storedCode.equals(emailVerificationDto.getVerificationCode())) {
            throw new InvalidAuthCodeException();
        }

        String verifiedKey = String.format(VERIFIED_EMAIL_KEY, emailVerificationDto.getEmail(), emailVerificationDto.getPurpose());
        redisTemplate.opsForValue().set(verifiedKey, "true");
        redisTemplate.delete(key);
    }

    public void logout(String refresh) {
        validateRefreshToken(refresh);
        String username = jwtUtil.getUsername(refresh);
        redisTemplate.delete(username);
    }

    private void validateDuplicateUser(String username, String email) {
        if (isDuplicatedUsername(username)) {
            throw new DuplicatedUsernameException();
        }
        if (isDuplicatedEmail(email)) {
            throw new DuplicatedEmailException();
        }
    }

    private void validateEmailVerification(String email, CertificationPurpose purpose) {
        String key = String.format(VERIFIED_EMAIL_KEY, email, purpose);
        String emailVerificationStatus = redisTemplate.opsForValue().get(key);

        if (!"true".equals(emailVerificationStatus)) {
            throw new UnverifiedEmailException();
        }
        redisTemplate.delete(key);
    }

    private void validateUsernameAndEmail(String username, String email) {
        if (!isDuplicatedUsername(username)) {
            throw new NotFoundUserException();
        }

        String storedUsername = userDAO.findUsernameByEmail(email);
        if (storedUsername == null || !storedUsername.equals(username)) {
            throw new EmailMismatchException();
        }
    }

    private String generateRandomCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private void validateRefreshToken(String refresh) {
        if (refresh == null || jwtUtil.isExpired(refresh) || !"refresh".equals(jwtUtil.getCategory(refresh))) {
            System.out.println("리프레쉬 토큰 에러 발생1 ->" + refresh);
            throw new InvalidRefreshTokenException();
        }
    }

    public UserInfoResponse getUserInfo(String username) {
        if (!isDuplicatedUsername(username)) {
            throw new NotFoundUserException();
        }
        User user = userDAO.findByUsername(username);
        return UserInfoResponse.builder()
                .name(user.getName())
                .profileImage(user.getProfileImgUrl())
                .username(user.getUsername())
                .email(user.getEmail())
                .birthday(user.getBirthDate())
                .build();
    }


    public void changeEmail(String username, EmailRequest emailRequest) {
        String newEmail = emailRequest.getEmail();
        validateEmailVerification(newEmail, CertificationPurpose.CHANGE_EMAIL);
        userDAO.updateEmailByUsername(username, newEmail);
    }
}

