package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupUserDAO;
import com.planu.group_meeting.exception.group.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupUserService {
    private final GroupUserDAO groupUserDAO;

    @Transactional
    public void isGroupMember(Long userId, Long groupId) {
        if(!groupUserDAO.isGroupMember(userId, groupId)) {
            throw new UnauthorizedAccessException("접근 권한이 없습니다.");
        }
    }

    public String setStatusById(Long userId, Long groupId) {
        if(groupUserDAO.isGroupMember(userId, groupId)) {
            Short status = groupUserDAO.getState(userId, groupId);
            return (status == 0 ? "RECEIVE" : "GROUP");
        }
        return "NONE";
    }
}
