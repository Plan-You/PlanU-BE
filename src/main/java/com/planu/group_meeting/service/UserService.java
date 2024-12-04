package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dao.UserTermsDAO;
import com.planu.group_meeting.dto.TokenDto;
import com.planu.group_meeting.dto.UserDto;
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

    public void createUser(UserDto.SignUpRequest userDto) {
        if (isDuplicatedUsername(userDto.getUsername())) {
            throw new DuplicatedUsernameException();
        }

        if (isDuplicatedEmail(userDto.getEmail())) {
            throw new DuplicatedEmailException();
        }

        String emailVerificationStatus = redisTemplate.opsForValue().get("verifiedEmail: " + userDto.getEmail());
        if (emailVerificationStatus == null || !emailVerificationStatus.equals("true")) {
            throw new UnverifiedEmailException();
        }
        redisTemplate.delete("VerifiedEmail: " + userDto.getEmail());
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


    public boolean isUserProfileCompleted(String username) {
        User user = userDAO.findByUsername(username);
        return user.getProfileStatus().equals(ProfileStatus.COMPLETED);
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

    public void sendCodeToEmail(UserDto.EmailRequest emailDto) throws MessagingException {
        String email = emailDto.getEmail();
        if (isDuplicatedEmail(email)) {
            throw new DuplicatedEmailException();
        }
        String authCode = generateRandomCode();
        Long expirationTime = 300000L; // 5분
        redisTemplate.opsForValue().set("authCode: " + email, authCode, expirationTime, TimeUnit.MILLISECONDS);
        mailService.sendVerificationCode(email, authCode);
    }

    public void verifyEmailCode(UserDto.EmailVerificationRequest emailVerificationDto) {
        String email = emailVerificationDto.getEmail();
        String authCode = emailVerificationDto.getVerificationCode();
        String storedCode = redisTemplate.opsForValue().get("authCode: " + email);
        if (storedCode == null) {
            throw new ExpiredAuthCodeException();
        }
        if (!storedCode.equals(authCode)) {
            throw new InvalidAuthCodeException();
        }
        redisTemplate.opsForValue().set("verifiedEmail: " + email, "true");
        redisTemplate.delete("authCode:" + email);
    }

    public void logout(String refresh) {
        validateRefreshToken(refresh);
        String username = jwtUtil.getUsername(refresh);
        redisTemplate.delete(username);
    }

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void validateRefreshToken(String refresh) {
        if (refresh == null || jwtUtil.isExpired(refresh) || !jwtUtil.getCategory(refresh).equals("refresh")) {
            throw new InvalidTokenException();
        }
    }


}
