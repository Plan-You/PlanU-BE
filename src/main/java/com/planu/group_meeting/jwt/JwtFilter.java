package com.planu.group_meeting.jwt;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.entity.Role;
import com.planu.group_meeting.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("access");
        if(accessToken==null){
            filterChain.doFilter(request,response);
            return;
        }
        try{
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e){
            PrintWriter printWriter = response.getWriter();
            printWriter.print("access token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String category = jwtUtil.getCategory(accessToken);
        if(!category.equals("access")){
            PrintWriter printWriter = response.getWriter();
            printWriter.print("access token invalid");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.valueOf(role));
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request,response);
    }
}
