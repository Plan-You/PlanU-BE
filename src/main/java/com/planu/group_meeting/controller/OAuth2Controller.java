package com.planu.group_meeting.controller;

import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.service.OAuth2Service;
import com.planu.group_meeting.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.planu.group_meeting.jwt.JwtFilter.AUTHORIZATION_HEADER;
import static com.planu.group_meeting.jwt.JwtFilter.BEARER_PREFIX;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;
    @PostMapping("/oauth2-jwt-header")
    public ResponseEntity<BaseResponse>oauth2JwtHeader(HttpServletRequest request, HttpServletResponse response){
        String access = CookieUtil.getCookieValue(request,"access");
        boolean isNewUser = oAuth2Service.isNewUser(access);
        response.addCookie(CookieUtil.deleteCookie("access"));
        response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + access);
        response.addHeader("isNewUser", String.valueOf(isNewUser));
        return BaseResponse.toResponseEntity(HttpStatus.OK,"액세스 토큰 헤더 설정 성공");
    }

}
