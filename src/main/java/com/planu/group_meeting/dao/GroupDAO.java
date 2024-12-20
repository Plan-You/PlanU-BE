package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupDAO {

    void insertGroup(Group group);

    void insertGroupUser(GroupUser groupUser);

    Group findGroupById(Long groupId);

    Long findUserIdByUserName(String userName);

    GroupUser findGroupUserByUserIdAndGroupId(Long userId, Long groupId);

    void UpdateGroupUserGroupStatus(Long userId, Long groupId);

    String findNameByGroupId(Long groupId);

    List<GroupResponseDTO> findGroupsByUserId(Long userId);
}
