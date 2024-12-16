package com.planu.group_meeting.service;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.GroupInviteResponseDTO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.service.file.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {
    private final GroupDAO groupDAO;
    private final UserDAO userDAO;
    private final S3Uploader s3Uploader;

    @Autowired
    public GroupService(GroupDAO groupDAO, UserDAO userDAO, S3Uploader s3Uploader) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
        this.s3Uploader = s3Uploader;
    }

    @Transactional
    public GroupResponseDTO createGroup(String userName, String groupName, MultipartFile groupImage) {
        String imageUrl = s3Uploader.uploadFile(groupImage);

        Group group = Group.builder()
                .name(groupName)
                .groupImageUrl(imageUrl)
                .build();

        groupDAO.insertGroup(group);


        groupDAO.insertGroupUser(GroupUser.builder()
                .userId(groupDAO.findUserIdByUserName(userName))
                .groupId(group.getId())
                .groupRole(GroupUser.GroupRole.LEADER)
                .groupState(1)
                .build());


        return GroupResponseDTO.builder()
                .groupId(group.getId())
                .groupName(groupName)
                .leaderUserName(userName)
                .groupImageUrl(imageUrl)
                .build();
    }

    @Transactional
    public GroupInviteResponseDTO inviteUser(CustomUserDetails customUserDetails, String userName, Long groupId) {
        if (groupDAO.findGroupUserByUserIdAndGroupId(customUserDetails.getId(), groupId) == null) {
            throw new IllegalArgumentException("초대 권한이 없습니다.");
        }

        Group group = groupDAO.findGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("해당 그룹이 존재하지 않습니다.");
        }

        User user = userDAO.findByUsername(userName);
        if (user == null) {
            throw new IllegalArgumentException("사용자 '" + userName + "'을 찾을 수 없습니다.");
        }

        groupDAO.insertGroupUser(GroupUser.builder()
                .groupId(groupId)
                .userId(user.getId())
                .groupRole(GroupUser.GroupRole.PARTICIPANT)
                .groupState(0)
                .build());

        return GroupInviteResponseDTO.builder()
                .invitedUserName(userName)
                .groupId(groupId)
                .groupName(group.getName())
                .groupImageUrl(group.getGroupImageUrl())
                .build();
    }

    @Transactional
    public void joinGroup(CustomUserDetails customUserDetails, Long groupId) {
        Group group = groupDAO.findGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("해당 그룹이 존재하지 않습니다.");
        }

        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(customUserDetails.getId(), groupId);
        if (groupUser == null) {
            throw new IllegalArgumentException("초대 받지 않았습니다.");
        }

        if (groupUser.getGroupState() == 1) {
            throw new IllegalArgumentException("이미 그룹에 속해 있습니다.");
        }

        groupDAO.UpdateGroupUserGroupStatus(customUserDetails.getId(), groupId);

    }

    @Transactional
    public List<GroupResponseDTO> getGroupList(Long userId) {
        return groupDAO.findGroupsByUserId(userId);
    }
    @Transactional
    public String findNameByGroupId(Long groupId) {
        return groupDAO.findNameByGroupId(groupId);
    }
}
