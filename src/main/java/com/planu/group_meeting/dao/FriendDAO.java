package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.common.FriendStatus;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FriendDAO {
    FriendStatus getFriendStatus(Long fromUserId, Long toUserId);
    void requestFriend(Long fromUserId, Long toUserId);

    void acceptFriend(Long fromUserId, Long toUserId);
}
