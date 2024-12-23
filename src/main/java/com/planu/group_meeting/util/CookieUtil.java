package com.planu.group_meeting.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

public class CookieUtil {
    public static void createCookie(HttpServletResponse response, String key, String value) {
        ResponseCookie cookie = ResponseCookie.from(key,value)
                .maxAge(24 * 60 * 60)
                .secure(true)
                .path("/")
                .sameSite("None")
                .domain("https://localhost:5173")
                .httpOnly(true)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static Cookie deleteCookie(String cookieName){
        Cookie cookie = new Cookie(cookieName,null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // https에서만 동작
        return cookie;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
