package com.planu.group_meeting.chat.service;

import com.planu.group_meeting.chat.dao.ChatDAO;
import com.planu.group_meeting.chat.dto.ChatMessage;
import com.planu.group_meeting.chat.dto.MessageStatus;
import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.GroupUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public void expelChat(String username, Long groupId) {
        simpMessageSendingOperations.convertAndSend("/sub/chat/group/" + groupId, username + "님이 나가셨습니다.");

        simpMessageSendingOperations.convertAndSend("/sub/disconnect/" + username, "웹소켓 연결 종료요청 보내주세요.");
    }
}
