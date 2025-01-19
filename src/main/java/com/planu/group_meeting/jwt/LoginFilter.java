package com.planu.group_meeting.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.UserDto;
import com.planu.group_meeting.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.planu.group_meeting.jwt.JwtFilter.AUTHORIZATION_HEADER;
import static com.planu.group_meeting.jwt.JwtFilter.BEARER_PREFIX;


@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/users/login"); // 로그인 엔드포인트 변경
    }
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
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        String access = jwtUtil.createAccessToken(username, role);
        String refresh = jwtUtil.createRefreshToken(username, role);
        System.out.println(role);

        response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + access);
        CookieUtil.createCookie(response,"refresh",refresh);
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

}
