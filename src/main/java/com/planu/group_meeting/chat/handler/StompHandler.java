package com.planu.group_meeting.chat.handler;

import com.planu.group_meeting.chat.dao.ChatDAO;
import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dao.GroupScheduleDAO;
import com.planu.group_meeting.dao.GroupScheduleParticipantDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.entity.GroupSchedule;
import com.planu.group_meeting.entity.GroupUser;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.jwt.JwtUtil;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    public static final String DEFAULT_SUB_PATH = "/sub/chat/group/";
    public static final String DEFAULT_PUB_PATH = "/pub/chat/group/";
    public static final String DEFAULT_READ_PUB_PATH = "/pub/chat/read/";

    public static final String DEFAULT_DISCONNECT_SUB_PATH = "/sub/disconnect/";

    public static final String GROUP_LOCATION_SUB_PATH = "/sub/location/groups/";
    public static final String GROUP_LOCATION_PUB_PATH = "/pub/location/groups/";

    private final JwtUtil jwtUtil;
    private final GroupDAO groupDAO;
    private final GroupScheduleDAO groupScheduleDAO;
    private final GroupScheduleParticipantDAO groupScheduleParticipantDAO;
    private final UserDAO userDAO;
    private final ChatDAO chatDAO;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompCommand command = null;
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            command = accessor.getCommand();

            if (StompCommand.CONNECT.equals(command)) {
                String username = getUsernameByAuthorizationHeader(accessor.getFirstNativeHeader("Authorization"));

                setValue(accessor, "username", username);
            } else if (StompCommand.SUBSCRIBE.equals(command)) {
                String username = (String) getValue(accessor, "username");
                String destination = accessor.getDestination();
                if (destination.startsWith(DEFAULT_SUB_PATH)) {
                    Long groupId = parseGroupIdFromSubPath(accessor);
                    validateUserInGroup(username, groupId);
                } else if (destination.startsWith(GROUP_LOCATION_SUB_PATH)) {
                    System.out.println("[실시간 위치 구독]: 엔드포인트 " + destination);
                    System.out.println("[실시간 위치 구독]: 엔드포인트 경로에서 정보 추출 시작");
                    Long[] ids = parseGroupAndShceduleIdByPath(destination, GROUP_LOCATION_SUB_PATH);
                    Long groupId = ids[0];
                    Long scheduleId = ids[1];
                    System.out.println("[실시간 위치 구독]: 엔드포인트 경로 추출 결과");
                    System.out.println("[실시간 위치 구독]: 그룹 아이디 " + ids[0]);
                    System.out.println("[실시간 위치 구독]: 일정 아이디 " + ids[1]);
                    System.out.println("[실시간 위치 구독]: 데이터 검증 시작");
                    validateUserInGroupsSchedules(username, groupId, scheduleId);
                    System.out.println("[실시간 위치 구독]: 데이터 검증 성공");
                } else {
                    validateUsername(accessor, username);
                }
            } else if (StompCommand.SEND.equals(command)) {
                String username = (String) getValue(accessor, "username");
                String destination = accessor.getDestination();
                if (destination.startsWith(DEFAULT_PUB_PATH)) {
                    Long groupId = parseGroupIdFromPubPath(accessor);
                    validateUserInGroup(username, groupId);
                } else if (destination.startsWith(GROUP_LOCATION_PUB_PATH)) {
                    Long[] ids = parseGroupAndShceduleIdByPath(destination, GROUP_LOCATION_PUB_PATH);
                    Long groupId = ids[0];
                    Long scheduleId = ids[1];
                    System.out.println("groupId: " + groupId);
                    System.out.println("scheduleId: " + scheduleId);
                    validateUserInGroupsSchedules(username, groupId, scheduleId);
                } else {
                    validateReadPubPath(accessor, username);
                }

            } else if (StompCommand.DISCONNECT.equals(command)) {
                String username = (String) getValue(accessor, "username");
            }
        } catch (Exception e) {
            System.out.println("[웹소켓 에러 발생]");
            System.out.println(command);
            System.out.println(e.getMessage());
            System.out.println(LocalDateTime.now());
            throw e;
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

    private void validateUsername(StompHeaderAccessor accessor, String username) {
        String usernameFromDestination = parseUsernameFromSubpath(accessor);
        if(!Objects.equals(username, usernameFromDestination)) {
            throw new IllegalArgumentException("username : " + usernameFromDestination + "이 저장된 정보와 다릅니다.");
        }
    }

    private Long parseGroupIdByPath(String path, String prefix) {
        return Long.parseLong(path.substring(prefix.length()));
    }

    private Long parseGroupIdFromSubPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return Long.parseLong(destination.substring(DEFAULT_SUB_PATH.length()));
    }

    private Long parseGroupIdFromPubPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return Long.parseLong(destination.substring(DEFAULT_PUB_PATH.length()));
    }

    private void validateReadPubPath(StompHeaderAccessor accessor, String username) {
        Map<String, Long> values = parseMessageIdAndGroupId(accessor.getDestination());

        if(chatDAO.existsByIdAndGroupId(values.get("messageId"), values.get("groupId")) == 0){
            throw new IllegalArgumentException();
        }

        validateUserInGroup(username, values.get("groupId"));
    }

    private Map<String, Long> parseMessageIdAndGroupId(String destination) {
        Map<String, Long> result = new HashMap<>();
        String values = destination.substring(DEFAULT_READ_PUB_PATH.length());
        String[] parts = values.split("/");
        result.put("messageId", Long.parseLong(parts[0]));
        result.put("groupId", Long.parseLong(parts[1]));

        return result;
    }

    private String parseUsernameFromSubpath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return destination.substring(DEFAULT_DISCONNECT_SUB_PATH.length());
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

    private Long[] parseGroupAndShceduleIdByPath(String path, String prefix) {
        try {
            String[] parts = path.substring(prefix.length()).split("/");
            return new Long[]{Long.parseLong(parts[0]), Long.parseLong(parts[1])};
        } catch(Exception e) {
            throw new IllegalArgumentException("올바르지 않은 경로 형식입니다.");
        }
    }

    private void validateUserInGroupsSchedules(String username, Long groupId, Long scheduleId) {
        System.out.println("[실시간 위치 구독]:" + username +"이 " + groupId + "의 그룹원인지 검증 시작");
        validateUserInGroup(username, groupId);
        System.out.println("[실시간 위치 구독]: 그룹원 검증 성공");

        System.out.println("[실시간 위치 구독]: " + scheduleId + "의 일정이 있는지 검증 시작");
        GroupSchedule groupSchedule = groupScheduleDAO.findById(groupId, scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹 일정을 찾을 수 없습니다."));
        System.out.println("[실시간 위치 구독]: 그룹 일정이 있음 검증 성공");

        LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        LocalDateTime startDateTime = groupSchedule.getStartDateTime();

        System.out.println("[실시간 위치 구독]: 구독이 가능한 시간대인지 검증 시작");
        System.out.println("[실시간 위치 구독]: 그룹 일정 시작 시간: " + startDateTime);
        System.out.println("[실시간 위치 구독]: 구독 시도 시간: " + now);
        if (now.isBefore(startDateTime.minusHours(1)) || now.isAfter(startDateTime.plusHours(1))) {
            throw new IllegalArgumentException("일정 시작 1시간 전후일 때만 위치 공유를 할 수 있습니다.");
        }
        System.out.println("[실시간 위치 구독]: " + username + "이 일정에 참여중인지 검증 시작");
        for(var participantsInfo : groupScheduleParticipantDAO.findByScheduleId(groupId, scheduleId)) {
            System.out.println("[실시간 위치 구독]: 참석자 아이디: " + participantsInfo.getUsername());
            if(participantsInfo.getUsername().equals(username)) {
                System.out.println("[실시간 위치 구독]: 참석 중임 검증 성공");
                return;
            }
        }

        throw new IllegalArgumentException("해당 일정에 참여하고 있지않습니다.");
    }
}
