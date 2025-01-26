package com.planu.group_meeting.chat.config;

import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.jwt.JwtUtil;
import com.planu.group_meeting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private GroupDAO groupDAO;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            String username = jwtUtil.getUsername(token);

            Long userId = groupDAO.findUserIdByUserName(username);

            attributes.put("userId", userId);
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception e){

    }
}
