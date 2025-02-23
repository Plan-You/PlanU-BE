package com.planu.group_meeting.chat.handler;

import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.entity.GroupUser;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    public static final String DEFAULT_SUB_PATH = "/sub/chat/group/";
    public static final String DEFAULT_PUB_PATH = "/pub/chat/group/";

    private final JwtUtil jwtUtil;
    private final GroupDAO groupDAO;
    private final UserDAO userDAO;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String username = getUsernameByAuthorizationHeader(accessor.getFirstNativeHeader("Authorization"));

            setValue(accessor, "username", username);
        }

        else if (StompCommand.SUBSCRIBE.equals(command)) {
            String username = (String)getValue(accessor, "username");
            Long groupId = parseGroupIdFromSubPath(accessor);

            validateUserInGroup(username, groupId);
        }
        
        else if (StompCommand.SEND.equals(command)) {
            String username = (String)getValue(accessor, "username");
            Long groupId = parseGroupIdFromPubPath(accessor);

            validateUserInGroup(username, groupId);
        }

        else if (StompCommand.DISCONNECT.equals(command)) {
            String username = (String)getValue(accessor, "username");
        }

        return message;
    }

    private String getUsernameByAuthorizationHeader(String authHeaderValue) {
        String accessToken = getTokenByAuthorizationHeader(authHeaderValue);

        return jwtUtil.getUsername(accessToken);
    }

    private String getTokenByAuthorizationHeader(String authHeaderValue) {
        if (Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
            throw new IllegalArgumentException("authHeaderValue: " + authHeaderValue);
        }

        String acessToken = extractTokenFromAuthorizationHeader(authHeaderValue);
        jwtUtil.validateToken(acessToken);

        return acessToken;
    }

    private void validateUserInGroup(String username, Long groupId) {
        User user = userDAO.findByUsername(username);
        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(user.getId(), groupId);

        if(groupUser == null){
            throw new IllegalArgumentException("username : " + username + " groupId : " + groupId + " 로 조회된 결과가 없습니다.");
        }
        if(groupUser.getGroupState() == 0) {
            throw new IllegalArgumentException("username : " + username + " groupId : " + groupId + " 로 조회된 결과가 없습니다.");
        }
    }

    private Long parseGroupIdFromSubPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return Long.parseLong(destination.substring(DEFAULT_SUB_PATH.length()));
    }

    private Long parseGroupIdFromPubPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return Long.parseLong(destination.substring(DEFAULT_PUB_PATH.length()));
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(key + " 에 해당하는 값이 없습니다.");
        }

        return value;
    }

    private void setValue(StompHeaderAccessor accessor, String key, Object value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (Objects.isNull(sessionAttributes)) {
            throw new IllegalArgumentException("SessionAttributes가 null입니다.");
        }
        return sessionAttributes;
    }

    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or does not start with Bearer");
        }
        return authorizationHeader.substring(7);
    }
}
