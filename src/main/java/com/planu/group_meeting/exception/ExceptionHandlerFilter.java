package com.planu.group_meeting.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planu.group_meeting.dto.BaseResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            log.error("Unhandled exception occurred: ", ex);
            setErrorResponse(HttpStatus.BAD_REQUEST, response, "유효하지 않은 요청입니다.");
        } catch (Exception ex) {
            log.error("Exception occurred: ", ex);
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, "서버 내부 오류가 발생했습니다.");
        }
    }

    /**
     * 에러 응답을 설정하는 메서드
     *
     * @param status   HTTP 상태 코드
     * @param response HttpServletResponse 객체
     * @param message  에러 메시지
     */
    private void setErrorResponse(HttpStatus status, HttpServletResponse response, String message) {
        try {
            BaseResponse errorResponse = BaseResponse.toEntity(status, message);
            response.setStatus(status.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), errorResponse);
        } catch (IOException e) {
            log.error("Failed to write error response", e);
        }
    }
}