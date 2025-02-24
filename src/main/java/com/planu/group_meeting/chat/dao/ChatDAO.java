package com.planu.group_meeting.chat.dao;

import com.planu.group_meeting.chat.dto.ChatMessage;
import com.planu.group_meeting.chat.dto.MessageStatus;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ChatDAO {

    void saveChatMessage(ChatMessage chatMessage);

    void saveMessageStatus(MessageStatus messageStatus);
}
