package com.planu.group_meeting.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 쿠키의 만료 시간을 1일로 설정
        // cookie.setSecure(true); // 보안 쿠키로 설정할 경우 주석 해제
        cookie.setPath("/"); // 쿠키의 경로 설정 (기본값은 현재 경로)
        cookie.setHttpOnly(true); // 자바스크립트에서 접근 금지
        return cookie;
    }
}
