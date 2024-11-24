package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dto.GroupDTO;
import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import com.planu.group_meeting.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GroupService {

    private final JwtUtil jwtUtil;
    private final GroupDAO groupDAO;

    @Autowired
    public GroupService(JwtUtil jwtUtil, GroupDAO groupDAO){
        this.jwtUtil = jwtUtil;
        this.groupDAO = groupDAO;
    }

    @Transactional
    public GroupDTO createGroup(String userName, String groupName, MultipartFile groupImage) {
        String imageUrl = "";

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


        return GroupDTO.builder()
                .groupId(group.getId())
                .groupName(groupName)
                .leaderUserName(userName)
                .groupImageUrl(imageUrl)
                .build();
    }

}
