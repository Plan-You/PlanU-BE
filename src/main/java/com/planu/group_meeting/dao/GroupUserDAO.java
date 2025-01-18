package com.planu.group_meeting.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupUserDAO {
    Boolean isGroupMember(Long userId, Long groupId);

    Boolean isLeader(Long userId, Long groupId);

    Short getState(Long userId, Long groupId);

    List<Long> getGroupMemberIds(Long groupId);
}
