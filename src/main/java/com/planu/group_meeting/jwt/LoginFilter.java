package com.planu.group_meeting.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.UserDto;
import com.planu.group_meeting.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper(); // 내부에서 ObjectMapper 인스턴스 생성

            // JSON 본문에서 사용자 이름과 비밀번호 추출
            UserDto.SignUpRequest userDTO = objectMapper.readValue(request.getInputStream(), UserDto.SignUpRequest.class);
            String username = userDTO.getUsername();
            String password = userDTO.getPassword();

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);
            return authenticationManager.authenticate(token);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // 권한 이름을 추출
                .findFirst()  // 첫 번째 권한을 가져옴 (다중 권한 사용 시 변경 가능)
                .orElse(null);  // 없을 경우 null 반환

        String access = jwtUtil.createAccessToken(username, role, 30 * 60 * 1000L); //30분
        String refresh = jwtUtil.createRefreshToken(username, role, 7 * 24 * 60 * 60 * 1000L); // 7일

        response.addHeader("access", access);
        response.addCookie(CookieUtil.createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }


}
