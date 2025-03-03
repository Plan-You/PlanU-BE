package com.planu.group_meeting.chat.service;

import com.planu.group_meeting.chat.dao.ChatDAO;
import com.planu.group_meeting.chat.dto.ChatInfo;
import com.planu.group_meeting.chat.dto.ChatMessage;
import com.planu.group_meeting.chat.dto.MessageStatus;
import com.planu.group_meeting.chat.dto.response.ChatRoomResponse;
import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.dto.GroupUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final UserDAO userDAO;
    private final ChatDAO chatDAO;
    private final GroupDAO groupDAO;

    @Transactional
    public void save(Long groupId, String username, String content){
        Long userId = userDAO.findIdByUsername(username);

        ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .groupId(groupId)
                .content(content)
                .build();

        chatDAO.saveChatMessage(chatMessage);

        saveMessageStatus(groupId, chatMessage.getId());
    }

    @Transactional
    private void saveMessageStatus(Long groupId, Long chatMessageId) {
        List<GroupUserDTO> users = groupDAO.findGroupsByGroupId(groupId);

        for (GroupUserDTO user : users) {
            MessageStatus messageStatus = MessageStatus.builder()
                    .messageId(chatMessageId)
                    .userId(user.getUserId())
                    .build();

            chatDAO.saveMessageStatus(messageStatus);
        }
    }

    @Transactional
    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<GroupResponseDTO> groupList = groupDAO.findGroupsByUserId(userId);

        return groupList.stream()
                .map(group -> {
                    ChatInfo chatInfo = chatDAO.getChatInfo(group.getGroupId());
                    return ChatRoomResponse.builder()
                            .groupId(group.getGroupId())
                            .groupName(group.getGroupName())
                            .groupImageUrl(group.getGroupImageUrl())
                            .participant(Long.parseLong(group.getParticipant()))
                            .isPin(group.getIsPin())
                            .lastChat(Optional.ofNullable(chatInfo).map(ChatInfo::getLastChat).orElse(""))
                            .lastChatDate(Optional.ofNullable(chatInfo).map(ChatInfo::getLastChatDate).orElse(""))
                            .lastChatTime(Optional.ofNullable(chatInfo).map(ChatInfo::getLastChatTime).orElse(""))
                            .unreadChats(chatDAO.countUnreadChatByUserAndGroup(userId, group.getGroupId()))
                            .build();
                })
                .sorted(Comparator.comparing(ChatRoomResponse::getIsPin)
                        .reversed()
                        .thenComparing(Comparator.comparing(ChatRoomResponse::getLastChatDate).reversed())
                        .thenComparing(Comparator.comparing(ChatRoomResponse::getLastChatTime).reversed()))
                .collect(Collectors.toList());

    }

    @Transactional
    public List<ChatRoomResponse> searchChatRooms(Long userId, String searchName) {
        if (searchName == null || searchName.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String keyword = searchName.trim().toLowerCase();

        List<GroupResponseDTO> groupList = groupDAO.findGroupsByUserId(userId);

        return groupList.stream()
                .filter(group -> group.getGroupName().toLowerCase().contains(keyword)) // 검색 필터 적용
                .map(group -> {
                    ChatInfo chatInfo = chatDAO.getChatInfo(group.getGroupId());
                    return ChatRoomResponse.builder()
                            .groupId(group.getGroupId())
                            .groupName(group.getGroupName())
                            .groupImageUrl(group.getGroupImageUrl())
                            .participant(Long.parseLong(group.getParticipant()))
                            .isPin(group.getIsPin())
                            .lastChat(Optional.ofNullable(chatInfo).map(ChatInfo::getLastChat).orElse(""))
                            .lastChatDate(Optional.ofNullable(chatInfo).map(ChatInfo::getLastChatDate).orElse(""))
                            .lastChatTime(Optional.ofNullable(chatInfo).map(ChatInfo::getLastChatTime).orElse(""))
                            .unreadChats(chatDAO.countUnreadChatByUserAndGroup(userId, group.getGroupId()))
                            .build();
                })
                .sorted(Comparator.comparing(ChatRoomResponse::getIsPin)
                        .reversed()
                        .thenComparing(Comparator.comparing(ChatRoomResponse::getLastChatDate).reversed())
                        .thenComparing(Comparator.comparing(ChatRoomResponse::getLastChatTime).reversed()))
                .collect(Collectors.toList());
    }

//    @Transactional
//    public int getUnreadCount(Long groupId, Long MessageId) {
//
//    }

    @Transactional
    public void expelChat(String username, Long groupId) {
        simpMessageSendingOperations.convertAndSend("/sub/chat/group/" + groupId, username + "님이 나가셨습니다.");

        simpMessageSendingOperations.convertAndSend("/sub/disconnect/" + username, "웹소켓 연결 종료요청 보내주세요.");
    }
}
