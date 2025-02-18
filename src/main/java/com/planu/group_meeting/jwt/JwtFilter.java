package com.planu.group_meeting.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.exception.user.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDAO userDAO;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String EXPIRED_JWT_TOKEN_MESSAGE = "만료된 토큰입니다.";
    public static final String INVALID_JWT_TOKEN_MESSAGE = "유효하지 않은 토큰입니다.";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        if (accessToken == null || !accessToken.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        accessToken = accessToken.substring(BEARER_PREFIX.length());

        try {
            Claims claims = jwtUtil.parseToken(accessToken);
            String category = jwtUtil.getCategory(accessToken);
            if (!"access".equals(category)) {
                throw new InvalidTokenException(INVALID_JWT_TOKEN_MESSAGE);
            }
            User user = userDAO.findByUsername(jwtUtil.getUsername(accessToken));
            CustomUserDetails userDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            jwtExceptionHandler(response, EXPIRED_JWT_TOKEN_MESSAGE);
        } catch (InvalidTokenException | MalformedJwtException | SignatureException e) {
            jwtExceptionHandler(response, INVALID_JWT_TOKEN_MESSAGE);
        }
    }

    private void jwtExceptionHandler(HttpServletResponse response, String message) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String json = new ObjectMapper().writeValueAsString(
                    new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, message)
            );
            response.getWriter().write(json);
        } catch (IOException e) {
            log.error("Error writing response: " + e.getMessage());
        }
    }

    // 예외 응답 DTO
    @Getter
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final int resultCode;
        private final String resultMsg;
    }
}

