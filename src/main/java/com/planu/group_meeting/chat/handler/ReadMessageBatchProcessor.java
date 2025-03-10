package com.planu.group_meeting.chat.handler;

import com.planu.group_meeting.chat.dto.response.ChatMessageResponse;
import com.planu.group_meeting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ReadMessageBatchProcessor {

    private final Map<Long, Long> pendingReadUpdates = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public synchronized void addReadRequest(Long messageId, Long groupId) {
        pendingReadUpdates.put(messageId, groupId);
    }

    @Scheduled(fixedDelay = 100)
    public synchronized void processReadUpdates() {
        if (pendingReadUpdates.isEmpty()) return;

        Map<Long, Long> updatesToSend = new HashMap<>(pendingReadUpdates);
        pendingReadUpdates.clear();

        for (Map.Entry<Long, Long> entry : updatesToSend.entrySet()) {
            Long messageId = entry.getKey();
            Long groupId = entry.getValue();

            int unReadCount = chatService.getUnreadCountforMessage(messageId);

            sendUnreadMessage(groupId, messageId, unReadCount);
        }
    }

    private void sendUnreadMessage(Long groupId, Long messageId, int unReadCount) {
        ChatMessageResponse chatMessage = ChatMessageResponse.builder()
                                            .messageId(messageId)
                                            .type(4)
                                            .unReadCount(unReadCount)
                                            .build();

        messagingTemplate.convertAndSend("/sub/chat/group/" + groupId, chatMessage);
    }
}
