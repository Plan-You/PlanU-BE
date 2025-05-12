package com.planu.group_meeting.service;

import com.planu.group_meeting.entity.common.CertificationPurpose;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email, String code, CertificationPurpose purpose) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);

            String subject;
            String content;

            switch (purpose) {
                case REGISTER:
                    subject = "[회원가입] 이메일 인증 코드";
                    content = "<p>회원가입을 위한 인증 코드입니다.</p>"
                            + "<p>아래 인증 코드를 입력해주세요:</p>"
                            + "<h2>" + code + "</h2>";
                    break;

                case FIND_PASSWORD:
                    subject = "[비밀번호 찾기] 이메일 인증 코드";
                    content = "<p>비밀번호 재설정을 위한 인증 코드입니다.</p>"
                            + "<p>아래 인증 코드를 입력해주세요:</p>"
                            + "<h2>" + code + "</h2>";
                    break;

                case FIND_USERNAME:
                    subject = "[아이디 찾기] 이메일 인증 코드";
                    content = "<p>아이디 확인을 위한 인증 코드입니다.</p>"
                            + "<p>아래 인증 코드를 입력해주세요:</p>"
                            + "<h2>" + code + "</h2>";
                    break;

                case CHANGE_EMAIL:
                    subject = "[이메일 변경] 인증 코드";
                    content = "<p>이메일 변경을 위한 인증 코드입니다.</p>"
                            + "<p>아래 인증 코드를 입력해주세요:</p>"
                            + "<h2>" + code + "</h2>";
                    break;

                default:
                    throw new IllegalArgumentException("지원하지 않는 인증 목적입니다: " + purpose);
            }

            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다", e);
        }
    }

}
