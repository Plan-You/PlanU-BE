package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupUserDAO {
    Boolean isGroupMember(Long userId, Long groupId);

    Boolean isLeader(Long userId, Long groupId);

    Short getState(Long userId, Long groupId);

    List<Long> getGroupMemberIds(Long groupId);

    Boolean getPinById(Long groupId, Long userId);

    Boolean isExistsGroupUser(Long userId, Long groupId);

    User findLeaderByGroupId(Long groupId);
}
