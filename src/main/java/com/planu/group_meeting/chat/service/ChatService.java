package com.planu.group_meeting.chat.service;

import com.planu.group_meeting.dao.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final UserDAO userDAO;
//    private final ChatDAO chatDAO;


    public void save(Long groupId, String username, String content){
        Long userId = userDAO.findIdByUsername(username);

//        Long messageId = chatDAO.save(userId, groupId, content);

        //message status 저장로직
    }


    public void expelChat(String username, Long groupId) {
        simpMessageSendingOperations.convertAndSend("/sub/chat/group/" + groupId, username + "님이 나가셨습니다.");

        simpMessageSendingOperations.convertAndSendToUser(username, "/sub/disconnect", "웹소켓 연결 종료요청 보내주세요.");
    }
}
