package com.planu.group_meeting.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupUserDAO {
    Boolean isGroupMember(Long userId, Long groupId);

    Boolean isLeader(Long userId, Long groupId);

    Short getState(Long userId, Long groupId);
}
