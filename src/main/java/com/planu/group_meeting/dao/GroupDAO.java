package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.GroupDTO.Member;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.dto.GroupUserDTO;
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

    Long findUserIdByUsername(String username);

    GroupUser findGroupUserByUserIdAndGroupId(Long userId, Long groupId);

    void deleteGroupUserByUserIdAndGroupId(Long userId, Long groupId);

    int updateGroupPin(@Param("userId") Long userId, @Param("groupId") Long groupId, @Param("version") Long version);

    int updateGroupUnpin(@Param("userId") Long userId, @Param("groupId") Long groupId, @Param("version") Long version);

    void updateGroupUserGroupStatus(Long userId, Long groupId);

    String findNameByGroupId(Long groupId);

    List<GroupResponseDTO> findGroupsByUserId(Long userId);

    // 1. GROUP_SCHEDULE_PARTICIPANT 삭제
    void deleteGroupScheduleParticipant(@Param("groupId") Long groupId);

    // 3. GROUP_SCHEDULE_COMMENT 삭제
    void deleteGroupScheduleComment(@Param("groupId") Long groupId);

    // 4. GROUP_USER 삭제
    void deleteGroupUser(@Param("groupId") Long groupId);

    // 5. CHAT_MESSAGE 삭제
    void deleteChatMessage(@Param("groupId") Long groupId);

    // 6. GROUP_SCHEDULE 삭제
    void deleteGroupSchedule(@Param("groupId") Long groupId);

    // 7. GROUP_ 삭제
    void deleteGroup(@Param("groupId") Long groupId);

    List<GroupResponseDTO> getGroupInviteList(Long userId);

    List<Member> findGroupMembers(Long groupId, String keyword);

    List<Long> findUserIdsByGroupId(Long groupId);

    List<GroupUserDTO> findGroupsByGroupId(Long groupId);
}
