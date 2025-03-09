package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dao.UserTermsDAO;
import com.planu.group_meeting.dto.TokenDto;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.entity.common.CertificationPurpose;
import com.planu.group_meeting.entity.common.ProfileStatus;
import com.planu.group_meeting.exception.user.*;
import com.planu.group_meeting.jwt.JwtUtil;
import com.planu.group_meeting.service.file.S3Uploader;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.planu.group_meeting.dto.UserDto.*;
import static com.planu.group_meeting.jwt.JwtUtil.REFRESH_TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String VERIFIED_EMAIL_KEY = "verifiedEmail : %s : %s";
    private static final String VERIFIED_PASSWORD_KEY = "verifiedPassword : %s";
    private static final String PASSWORD_CHANGE_VERIFIED_KEY = "passwordChangeVerified:%s";
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
        String profileImageUrl = (profileImage != null && !profileImage.isEmpty()) ? s3Uploader.uploadFile(profileImage) : BASE_PROFILE_IMAGE;
        User user = userDAO.findById(userId);
        user.updateProfile(profileImageUrl, registrationRequest.getGender(), registrationRequest.getBirthDate());
        userDAO.updateUserProfile(user);
        userTermsDAO.saveTerms(registrationRequest.getTermsRequest().toEntity(userId));
    }

    @Transactional
    public void updateUserProfile(Long userId, UserProfileUpdateRequest request, MultipartFile profileImage) {
        User user = userDAO.findById(userId);
        validateEmailChange(user, request.getEmail());
        validatePasswordChange(user, request.getPassword());

        String newProfileImageUrl = getUpdateProfileImage(user.getProfileImgUrl(), profileImage);
        String newPassword = getUpdatedPassword(user.getPassword(), request.getPassword());

        request.setPassword(newPassword);
        user.updateProfile(request, newProfileImageUrl);
        userDAO.updateUserProfile(user);
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

    public String findUsername(EmailRequest emailRequest) {
        validateEmailVerification(emailRequest.getEmail(), CertificationPurpose.FIND_USERNAME);
        String username = userDAO.findUsernameByEmail(emailRequest.getEmail());
        if (username == null) {
            throw new NotFoundUserException();
        }
        return username;
    }

    public void verifyPassword(Long userId, PasswordRequest passwordRequest) {
        User currentUser = userDAO.findById(userId);
        validatePasswordMatch(currentUser.getPassword(), passwordRequest.getPassword());
        String key = getPasswordVerificationKey(currentUser.getUsername());
        redisTemplate.opsForValue().set(key, "true", AUTH_CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    public void validateNewPassword(Long userId, ChangePasswordRequest passwordRequest) {
        User user = userDAO.findById(userId);
        String passwordVerificationStatus = redisTemplate.opsForValue().get(getPasswordVerificationKey(user.getUsername()));
        if (!"true".equals(passwordVerificationStatus)) {
            throw new UnverifiedPasswordException();
        }

        if (!passwordRequest.getConfirmPassword().equals(passwordRequest.getNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }
        String passwordChangeKey = getPasswordChangeKey(user.getUsername());
        redisTemplate.opsForValue().set(passwordChangeKey, "true", AUTH_CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        redisTemplate.delete(getPasswordVerificationKey(user.getUsername()));
    }

    private void validateEmailChange(User user, String newEmail) {
        if (!user.getEmail().equals(newEmail) && newEmail!=null) {
            validateEmailVerification(newEmail, CertificationPurpose.CHANGE_EMAIL);
        }
    }

    private void validatePasswordMatch(String storedPassword, String inputPassword) {
        if (!passwordEncoder.matches(inputPassword, storedPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private String getPasswordVerificationKey(String username) {
        return String.format(VERIFIED_PASSWORD_KEY, username);
    }

    private String getUpdateProfileImage(String currentImageUrl, MultipartFile newImage) {
        if(newImage!=null && !newImage.isEmpty()){
            s3Uploader.deleteFile(currentImageUrl);
            return s3Uploader.uploadFile(newImage);
        }
        return currentImageUrl;
    }

    private String getUpdatedPassword(String currentPassword, String newPassword) {
        return (newPassword!=null) ? passwordEncoder.encode(newPassword) : currentPassword;
    }

    @Transactional
    public void updatePassword(FindPasswordRequest findPasswordRequest) {
        validateUsernameAndEmail(findPasswordRequest.getUsername(), findPasswordRequest.getEmail());
        validateEmailVerification(findPasswordRequest.getEmail(), CertificationPurpose.FIND_PASSWORD);

        String encodedPassword = passwordEncoder.encode(findPasswordRequest.getNewPassword());
        userDAO.updatePasswordByUsername(findPasswordRequest.getUsername(), encodedPassword);
    }

    public boolean isUserProfileCompleted(String username) {
        User user = userDAO.findByUsername(username);
        return ProfileStatus.COMPLETED.equals(user.getProfileStatus());
    }

    public TokenDto reissueAccessToken(String refresh) {
        validateRefreshToken(refresh);
        String username = jwtUtil.getUsername(refresh);
        String storedRefresh = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + username);
        if (!refresh.equals(storedRefresh)) {
            throw new InvalidRefreshTokenException();
        }
        redisTemplate.delete(username);
        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createAccessToken(username, role);
        String newRefresh = jwtUtil.createRefreshToken(username, role);
        return new TokenDto(newAccess, newRefresh);
    }

    public void sendCodeToEmail(EmailSendRequest emailDto) throws MessagingException {
        if ((CertificationPurpose.REGISTER == emailDto.getPurpose() || CertificationPurpose.CHANGE_EMAIL == emailDto.getPurpose())
                && isDuplicatedEmail(emailDto.getEmail())) {
            throw new DuplicatedEmailException();
        }

        String authCode = generateRandomCode();
        String key = String.format(AUTH_CODE_KEY, emailDto.getEmail(), emailDto.getPurpose());
        redisTemplate.opsForValue().set(key, authCode, AUTH_CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        mailService.sendVerificationCode(emailDto.getEmail(), authCode);
    }

    public void verifyEmailCode(EmailVerificationRequest request) {
        String key = String.format(AUTH_CODE_KEY, request.getEmail(), request.getPurpose());
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new ExpiredAuthCodeException();
        }
        if (!storedCode.equals(request.getVerificationCode())) {
            throw new InvalidAuthCodeException();
        }

        String verifiedKey = String.format(VERIFIED_EMAIL_KEY, request.getEmail(), request.getPurpose());
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
            throw new InvalidRefreshTokenException();
        }
    }

    private void validatePasswordChange(User user, String newPassword) {
        if (newPassword!=null && !passwordEncoder.matches(newPassword, user.getPassword())) {
            String passwordChangeKey = getPasswordChangeKey(user.getUsername());
            if (!"true".equals(redisTemplate.opsForValue().get(passwordChangeKey))) {
                throw new UnverifiedPasswordException();
            }
        }
    }

    private String getPasswordChangeKey(String username) {
        return String.format(PASSWORD_CHANGE_VERIFIED_KEY, username);
    }

}

