package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import com.planu.group_meeting.service.file.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GroupService {
    private final GroupDAO groupDAO;
    private final S3Uploader s3Uploader;

    @Autowired
    public GroupService(GroupDAO groupDAO, S3Uploader s3Uploader){
        this.groupDAO = groupDAO;
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

}
