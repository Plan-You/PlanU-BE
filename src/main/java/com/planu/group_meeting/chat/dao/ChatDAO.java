package com.planu.group_meeting.chat.dao;

import com.planu.group_meeting.chat.dto.ChatInfo;
import com.planu.group_meeting.chat.dto.ChatMessage;
import com.planu.group_meeting.chat.dto.MessageStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ChatDAO {

    void saveChatMessage(ChatMessage chatMessage);
    void saveMessageStatus(MessageStatus messageStatus);

    ChatInfo getChatInfo(Long groupId);

    int countUnreadChatByUserAndGroup(Long userId, Long groupId);

    int existsByIdAndGroupId(@Param("messageId") Long messageId, @Param("groupId") Long groupId);

    void markAsRead(@Param("userId") Long userId, @Param("messageId") Long messageId);

    int countUnreadByMessageId(@Param("messageId") Long messageId);

    int countUnreadByUserId(@Param("userId") Long userId);

    ChatMessage findById(@Param("messageId") Long messageId);

    List<ChatMessage> findChatMessages(
            @Param("groupId") Long groupId,
            @Param("messageId") Long messageId,
            @Param("limit") int limit);

    List<Long> getMessageIdsByGroupAndRange(@Param("groupId") Long groupId,
                                                 @Param("startId") Long startId,
                                                 @Param("endId") Long endId);

    void updateMessageStatusAsRead(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
