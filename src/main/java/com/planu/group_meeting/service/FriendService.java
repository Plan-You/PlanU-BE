package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.FriendDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.FriendDto.FriendInfo;
import com.planu.group_meeting.dto.FriendDto.FriendListResponse;
import com.planu.group_meeting.dto.GroupDTO.GroupMembersResponse;
import com.planu.group_meeting.dto.GroupDTO.Member;
import com.planu.group_meeting.entity.common.EventType;
import com.planu.group_meeting.entity.common.FriendStatus;
import com.planu.group_meeting.exception.user.DuplicatedRequestException;
import com.planu.group_meeting.exception.user.FriendRequestNotFoundException;
import com.planu.group_meeting.exception.user.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.planu.group_meeting.dto.NotificationDTO.FriendNotification;


@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {
    private final FriendDAO friendDAO;
    private final UserDAO userDAO;
    private final NotificationService notificationService;

    @Transactional
    public void requestFriend(Long userId, String toUsername) {
        if (!userDAO.existsByUsername(toUsername)) {
            throw new NotFoundUserException();
        }
        if(userDAO.findById(userId).getUsername().equals(toUsername)){
            throw new IllegalArgumentException("본인에게 친구 요청을 보낼 수 없습니다.");
        }

        Long toUserId = userDAO.findByUsername(toUsername).getId();
        FriendStatus friendStatus = friendDAO.getFriendStatus(userId, toUserId);
        log.info("friendStatus={}", friendStatus);
        log.info("myId={}, toId={}", userId, toUserId);
        switch (friendStatus) {
            case NONE:
                friendDAO.requestFriend(userId, toUserId);
                break;
            case REQUEST:
                throw new DuplicatedRequestException("이미 친구요청을 보냈습니다.");
            case RECEIVE:
                throw new DuplicatedRequestException("친구 요청을 받아주세요");
            case FRIEND:
                throw new DuplicatedRequestException("이미 친구입니다");
            default:
                throw new IllegalStateException("알 수 없는 상태입니다.");
        }

        FriendNotification friendNotification = new FriendNotification(EventType.FRIEND_REQUEST, userId, toUserId,
                userDAO.findNameById(userId) + "님이 친구요청을 보냈습니다.");

        notificationService.sendNotification(EventType.FRIEND_REQUEST, friendNotification);
    }

    public void acceptFriend(Long userId, String fromUsername) {
        if (!userDAO.existsByUsername(fromUsername)) {
            throw new NotFoundUserException();
        }
        Long fromUserId = userDAO.findByUsername(fromUsername).getId();

        if (friendDAO.getFriendStatus(fromUserId, userId) != FriendStatus.REQUEST) {
            throw new FriendRequestNotFoundException();
        }
        FriendNotification friendNotification = new FriendNotification(EventType.FRIEND_ACCEPT, userId, fromUserId,
                userDAO.findNameById(userId) + "님이 친구요청을 수락하였습니다.");
        notificationService.sendNotification(EventType.FRIEND_ACCEPT, friendNotification);
        friendDAO.acceptFriend(fromUserId, userId);
    }

    public void rejectFriend(Long userId, String fromUsername) {
        if (!userDAO.existsByUsername(fromUsername)) {
            throw new NotFoundUserException();
        }
        Long fromUserId = userDAO.findByUsername(fromUsername).getId();
        if (friendDAO.getFriendStatus(fromUserId, userId) != FriendStatus.REQUEST) {
            throw new FriendRequestNotFoundException();
        }
        friendDAO.deleteFriend(fromUserId, userId);
    }

    public void cancelFriendRequest(Long userId, String toUsername) {
        if (!userDAO.existsByUsername(toUsername)) {
            throw new NotFoundUserException();
        }
        Long toUserId = userDAO.findByUsername(toUsername).getId();
        if (friendDAO.getFriendStatus(userId, toUserId) != FriendStatus.REQUEST) {
            throw new IllegalStateException("잘못된 요청입니다.");
        }
        friendDAO.deleteFriend(toUserId, userId);
    }

    public void deleteFriend(Long userId, String toUsername) {
        if (!userDAO.existsByUsername(toUsername)) {
            throw new NotFoundUserException();
        }
        Long toUserId = userDAO.findByUsername(toUsername).getId();
        if (friendDAO.getFriendStatus(userId, toUserId) != FriendStatus.FRIEND) {
            throw new IllegalStateException("잘못된 요청입니다.");
        }
        friendDAO.deleteFriend(toUserId, userId);
    }

    public FriendListResponse getFriendList(Long userId, String keyword) {
        List<FriendInfo> friendInfos = friendDAO.getFriendsInfo(userId, FriendStatus.FRIEND, keyword);
        return new FriendListResponse(friendInfos.size(), friendInfos);
    }

    public FriendListResponse getFriendRequestList(Long userId, String keyword) {
        List<FriendInfo> friendsInfo = friendDAO.getFriendsInfo(userId, FriendStatus.REQUEST, keyword);
        return new FriendListResponse(friendsInfo.size(), friendsInfo);
    }

    public FriendListResponse getFriendReceiveList(Long userId, String keyword) {
        List<FriendInfo> friendsInfo = friendDAO.getFriendsInfo(userId, FriendStatus.RECEIVE, keyword);
        return new FriendListResponse(friendsInfo.size(), friendsInfo);
    }

    public void setFriendStatus(Long userId, GroupMembersResponse groupMembers, String username) {
        for (Member member : groupMembers.getMembers()) {
            if (member.getUsername().equals(username)) {
                member.setFriendStatus("ME");
                continue;
            }
            Long toUserId = userDAO.findByUsername(member.getUsername()).getId();
            FriendStatus friendStatus = friendDAO.getFriendStatus(userId, toUserId);
            member.setFriendStatus(friendStatus.name());
        }
    }

    public FriendListResponse getRecommendationFriends(Long userId){
         List<FriendInfo> friendInfo = friendDAO.getRecommendationFriendInfo(userId);
         return new FriendListResponse(friendInfo.size(), friendInfo);
    }
}
