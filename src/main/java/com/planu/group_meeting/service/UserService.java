package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.UserDto;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.exception.user.*;
import com.planu.group_meeting.jwt.JwtUtil;
import com.planu.group_meeting.util.CookieUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

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

    public void createUserProfile(UserDto.UserProfileRequest userDto) {
        String username = userDto.getUsername();
        User user = userDAO.findByUsername(username);
        user.setProfileImgUrl(userDto.getProfileImgUrl());
        user.setBirthDate(String.valueOf(userDto.getBirthDate()));
        userDAO.updateUserProfile(user);
    }

    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }
        if (refresh == null || jwtUtil.isExpired(refresh) || !jwtUtil.getCategory(refresh).equals("refresh")) {
            throw new InvalidTokenException();
        }
        String username = jwtUtil.getUsername(refresh);
        String storedRefresh = redisTemplate.opsForValue().get(username);
        if (storedRefresh == null || !storedRefresh.equals(refresh)) {
            throw new InvalidTokenException();
        }
        redisTemplate.delete(username);

        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createAccessToken(username, role, 30 * 60 * 1000L);
        String newRefresh = jwtUtil.createRefreshToken(username, role, 7 * 24 * 60 * 60 * 1000L);

        response.setHeader("access", newAccess);
        response.addCookie(CookieUtil.createCookie("refresh", newRefresh));
        response.setStatus(HttpStatus.OK.value());
    }

    public void sendCodeToEmail(String email) throws MessagingException {
        if (isDuplicatedEmail(email)) {
            throw new DuplicatedEmailException();
        }
        String authCode = generateRandomCode();
        Long expirationTime = 300000L; // 5분
        redisTemplate.opsForValue().set("authCode: " + email, authCode, expirationTime, TimeUnit.MILLISECONDS);
        mailService.sendVerificationCode(email, authCode);
    }

    public void verifyEmailCode(String email, String authCode) {
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

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }


}
