package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.GroupDTO.Member;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupDAO {

    void insertGroup(Group group);

    void insertGroupUser(GroupUser groupUser);

    Group findGroupById(Long groupId);

    Long findUserIdByUserName(String userName);

    GroupUser findGroupUserByUserIdAndGroupId(Long userId, Long groupId);

    int deleteGroupUserByUserIdAndGroupId(Long userId, Long groupId);

    void updateGroupUserGroupStatus(Long userId, Long groupId);

    String findNameByGroupId(Long groupId);

    List<GroupResponseDTO> findGroupsByUserId(Long userId);

    void deleteGroup(@Param("groupId") Long groupId);

    List<GroupResponseDTO> getGroupInviteList(Long userId);

    List<Member> findGroupMembers(Long groupId);
}
