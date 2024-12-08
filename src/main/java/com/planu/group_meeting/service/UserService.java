package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dao.UserTermsDAO;
import com.planu.group_meeting.dto.TokenDto;
import com.planu.group_meeting.dto.UserDto;
import com.planu.group_meeting.dto.UserDto.ChangePasswordRequest;
import com.planu.group_meeting.dto.UserDto.EmailRequest;
import com.planu.group_meeting.dto.UserTermsDto;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.entity.UserTerms;
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

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.planu.group_meeting.jwt.JwtUtil.REFRESH_TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String VERIFIED_EMAIL_KEY = "verifiedEmail : %s : %s";
    private static final String AUTH_CODE_KEY = "authCode : %s : %s";
    private static final long AUTH_CODE_EXPIRATION_TIME = 300000L; // 5분

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
    public void createUser(UserDto.SignUpRequest userDto) {
        validateDuplicateUser(userDto.getUsername(), userDto.getEmail());
        validateEmailVerification(userDto.getEmail(), "register");

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDAO.insertUser(userDto.toEntity());
    }

    @Transactional
    public void createUserProfile(Long userId, UserDto.UserProfileRequest userProfileRequest,
                                  UserTermsDto.TermsRequest termsRequest) {
        String profileImageUrl = s3Uploader.uploadFile(userProfileRequest.getProfileImage());
        User user = new User();
        user.updateProfile(profileImageUrl, userProfileRequest.getGender(), userProfileRequest.getBirthDate());
        userDAO.updateUserProfile(userId, user);

        UserTerms userTerms = termsRequest.toEntity(userId);
        userTermsDAO.saveTerms(userTerms);
    }

    public String findUsername(EmailRequest emailRequest) {
        validateEmailVerification(emailRequest.getEmail(), "findUsername");

        String username = userDAO.findUsernameByEmail(emailRequest.getEmail());
        if (username == null) {
            throw new NotFoundUserException();
        }
        return username;
    }

    @Transactional
    public void updatePassword(ChangePasswordRequest changePasswordRequest) {
        validateUsernameAndEmail(changePasswordRequest.getUsername(), changePasswordRequest.getEmail());
        validateEmailVerification(changePasswordRequest.getEmail(), "findPassword");

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
            throw new InvalidRefreshTokenException();
        }
        redisTemplate.delete(username);

        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createAccessToken(username, role);
        String newRefresh = jwtUtil.createRefreshToken(username, role);

        return new TokenDto(newAccess, newRefresh);
    }

    public void sendCodeToEmail(UserDto.EmailSendRequest emailDto) throws MessagingException {
        if ("register".equals(emailDto.getPurpose()) && isDuplicatedEmail(emailDto.getEmail())) {
            throw new DuplicatedEmailException();
        }

        String authCode = generateRandomCode();
        String key = String.format(AUTH_CODE_KEY, emailDto.getEmail(), emailDto.getPurpose());
        redisTemplate.opsForValue().set(key, authCode, AUTH_CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        mailService.sendVerificationCode(emailDto.getEmail(), authCode);
    }

    public void verifyEmailCode(UserDto.EmailVerificationRequest emailVerificationDto) {
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

    private void validateEmailVerification(String email, String purpose) {
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
            throw new InvalidTokenException();
        }
    }
}

