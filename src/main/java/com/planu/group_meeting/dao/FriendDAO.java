package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.FriendDto.FriendInfo;
import com.planu.group_meeting.entity.common.FriendStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendDAO {
    FriendStatus getFriendStatus(Long fromUserId, Long toUserId);

    void requestFriend(Long fromUserId, Long toUserId);

    void acceptFriend(Long fromUserId, Long toUserId);

    void deleteFriend(Long fromUserId, Long toUserId);

    List<FriendInfo> getFriendsInfo(@Param("userId") Long userId, @Param("friendStatus") FriendStatus friendStatus,
                                   @Param("keyword") String keyword);

    List<FriendInfo> getRecommendationFriendInfo(Long userId);
}
