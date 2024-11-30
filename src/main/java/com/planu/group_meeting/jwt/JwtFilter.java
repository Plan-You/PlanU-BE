package com.planu.group_meeting.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.exception.user.ExpiredTokenException;
import com.planu.group_meeting.exception.user.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);

        if (accessToken == null || !accessToken.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        accessToken = accessToken.substring(BEARER_PREFIX.length());

        try {
            jwtUtil.validateSignature(accessToken);
            jwtUtil.isExpired(accessToken);

            String category = jwtUtil.getCategory(accessToken);
            if (!"access".equals(category)) {
                throw new InvalidTokenException("토큰 유형이 올바르지 않습니다.");
            }
            User user = userDAO.findByUsername(jwtUtil.getUsername(accessToken));
            CustomUserDetails userDetails = new CustomUserDetails(user);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredTokenException e) {
            jwtExceptionHandler(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (InvalidTokenException e) {
            jwtExceptionHandler(response, e.getMessage(), HttpServletResponse.SC_FORBIDDEN);
        }
//        catch (Exception e) {
//            jwtExceptionHandler(response, "서버 오류가 발생했습니다.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
    }

    private void jwtExceptionHandler(HttpServletResponse response, String message, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String json = new ObjectMapper().writeValueAsString(
                    new ErrorResponse(statusCode, message)
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
        private final int code;
        private final String message;
    }
}

