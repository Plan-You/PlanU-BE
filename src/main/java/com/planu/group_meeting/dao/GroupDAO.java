package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupDAO {

    void insertGroup(Group group);

    void insertGroupUser(GroupUser groupUser);

    Group findGroupById(Long groupId);

    Long findUserIdByUserName(String userName);

    GroupUser findGroupUserByUserIdAndGroupId(Long userId, Long groupId);
}
