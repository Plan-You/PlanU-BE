package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.FriendDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.FriendDto.FriendInfo;
import com.planu.group_meeting.dto.FriendDto.FriendListResponse;
import com.planu.group_meeting.entity.common.FriendStatus;
import com.planu.group_meeting.exception.user.DuplicatedRequestException;
import com.planu.group_meeting.exception.user.FriendRequestNotFoundException;
import com.planu.group_meeting.exception.user.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendDAO friendDAO;
    private final UserDAO userDAO;

    public void requestFriend(Long userId, String toUsername) {
        if(!userDAO.existsByUsername(toUsername)){
            throw new NotFoundUserException();
        }
        Long toUserId = userDAO.findByUsername(toUsername).getId();
        FriendStatus friendStatus = friendDAO.getFriendStatus(userId, toUserId);
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
    }

    public void acceptFriend(Long userId, String fromUsername) {
        Long fromUserId = userDAO.findByUsername(fromUsername).getId();
        if (friendDAO.getFriendStatus(fromUserId, userId) != FriendStatus.REQUEST) {
            throw new FriendRequestNotFoundException();
        }
        friendDAO.acceptFriend(fromUserId, userId);
    }

    public FriendListResponse getFriendList(Long userId){
        List<FriendInfo> friendsInfo = friendDAO.getFriendsInfo(userId);
        return new FriendListResponse(friendsInfo.size(), friendsInfo);
    }
}
