package com.planu.group_meeting.chat.dao;

import com.planu.group_meeting.chat.dto.ChatMessage;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ChatDAO {

    public void save(ChatMessage chatMessage);
}
