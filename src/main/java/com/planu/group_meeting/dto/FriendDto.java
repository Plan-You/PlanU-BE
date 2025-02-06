package com.planu.group_meeting.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planu.group_meeting.entity.common.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FriendDto {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class FriendListResponse{
        private int totalFriends;
        private List<FriendInfo>friends;
    }

    @Getter
    @Setter
    public static class FriendInfo{
        @JsonIgnore
        private Long userId;
        private String name;
        private String username;
        private String profileImageUrl;
    }

    @Getter
    public static class FriendNotification{
        private EventType eventType;
        private Long fromUserId;
        private Long toUserId;
        private String contents;

        public FriendNotification(EventType eventType, Long fromUserId, Long toUserId, String contents){
            if(eventType==EventType.FRIEND_REQUEST){
                this.eventType = EventType.FRIEND_REQUEST;
                this.fromUserId = fromUserId;
                this.toUserId = toUserId;
                this.contents = contents;
            }
            else if(eventType==EventType.FRIEND_ACCEPT){
                this.eventType = EventType.FRIEND_ACCEPT;
                this.fromUserId = fromUserId;
                this.toUserId = toUserId;
                this.contents = contents;
            }
        }
    }

}
