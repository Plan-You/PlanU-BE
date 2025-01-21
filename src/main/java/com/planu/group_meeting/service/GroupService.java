package com.planu.group_meeting.service;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dao.*;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatio;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatios;
import com.planu.group_meeting.dto.FriendDto.FriendInfo;
import com.planu.group_meeting.dto.GroupDTO.*;
import com.planu.group_meeting.dto.GroupInviteResponseDTO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.entity.common.FriendStatus;
import com.planu.group_meeting.exception.group.GroupNotFoundException;
import com.planu.group_meeting.exception.group.UnauthorizedAccessException;
import com.planu.group_meeting.service.file.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class GroupService {
    private final GroupDAO groupDAO;
    private final UserDAO userDAO;
    private final S3Uploader s3Uploader;
    private final GroupUserDAO groupUserDAO;
    private final FriendDAO friendDAO;
    private final AvailableDateDAO availableDateDAO;

    @Autowired
    public GroupService(GroupDAO groupDAO, UserDAO userDAO, S3Uploader s3Uploader, GroupUserDAO groupUserDAO, FriendDAO friendDAO, AvailableDateDAO availableDateDAO) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
        this.s3Uploader = s3Uploader;
        this.groupUserDAO = groupUserDAO;
        this.friendDAO = friendDAO;
        this.availableDateDAO = availableDateDAO;
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

        groupDAO.updateGroupUserGroupStatus(customUserDetails.getId(), groupId);

    }

    @Transactional
    public List<GroupResponseDTO> getGroupInviteList(Long userId) {
        return groupDAO.getGroupInviteList(userId);
    }

    @Transactional
    public List<GroupResponseDTO> getGroupList(Long userId) {
        return groupDAO.findGroupsByUserId(userId);
    }

    @Transactional
    public void leaveGroup(Long userId, Long groupId) {
        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(userId, groupId);
        if(groupUser == null){
            throw new IllegalArgumentException("이미 그룹에 속하지 않습니다.");
        }
        if(groupUser.getGroupState() == 0){
            throw new IllegalArgumentException("초대 수락/거절을 먼저 수행하십시오.");
        }
        if(groupUser.getGroupRole() == GroupUser.GroupRole.LEADER){
            throw new IllegalArgumentException("그룹 리더는 그룹을 떠날 수 없습니다.");
        }

        groupDAO.deleteGroupUserByUserIdAndGroupId(userId, groupId);

    }

    @Transactional
    public void deleteGroup(Long userId, Long groupId) {
        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(userId, groupId);
        Group group = groupDAO.findGroupById(groupId);

        if (group == null) {
            throw new IllegalArgumentException("해당 그룹이 존재하지 않습니다.");
        }
        if (groupUser == null){
            throw new IllegalArgumentException("그룹을 삭제할 권한이 없습니다.");
        }
        if (groupUser.getGroupRole() != GroupUser.GroupRole.LEADER) {
            throw new IllegalArgumentException("그룹을 삭제할 권한이 없습니다.");
        }


        groupDAO.deleteGroupScheduleParticipant(groupId);
        groupDAO.deleteGroupScheduleUnregisteredParticipant(groupId);
        groupDAO.deleteGroupScheduleComment(groupId);
        groupDAO.deleteGroupUser(groupId);
        groupDAO.deleteChatMessage(groupId);
        groupDAO.deleteGroupSchedule(groupId);
        groupDAO.deleteGroup(groupId);
    }

    @Transactional
    public void declineInvitation(Long userId, Long groupId) {
        Group group = groupDAO.findGroupById(groupId);

        if (group == null) {
            throw new IllegalArgumentException("해당 그룹이 존재하지 않습니다.");
        }
        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(userId, groupId);
        if (groupUser == null) {
            throw new IllegalArgumentException("초대 받지 않았습니다.");
        }
        if (groupUser.getGroupState() == 1) {
            throw new IllegalArgumentException("이미 그룹에 속해 있습니다.");
        }
        groupDAO.deleteGroupUserByUserIdAndGroupId(userId, groupId);
    }

    @Transactional
    public void forceExpelMember(Long leaderId, Long groupId, String username) {
        Long userId = groupDAO.findUserIdByUserName(username);
        GroupUser leaderGroupUser = groupDAO.findGroupUserByUserIdAndGroupId(leaderId, groupId);
        if(Objects.equals(leaderId, userId)){
            throw new IllegalArgumentException("자기자신을 퇴출시킬 수 없습니다.");
        }
        if(leaderGroupUser == null){
            throw new IllegalArgumentException("강제 퇴출시킬 권한이 없습니다.");
        }
        if (leaderGroupUser.getGroupRole() != GroupUser.GroupRole.LEADER) {
            throw new IllegalArgumentException("강제 퇴출시킬 권한이 없습니다.");
        }
        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(userId, groupId);
        if (groupUser == null || groupUser.getGroupState() == 0) {
            throw new IllegalArgumentException("그룹에 속해 있지 않은 멤버입니다.");
        }

        groupDAO.deleteGroupUserByUserIdAndGroupId(userId, groupId);
    }

    @Transactional
    public String findNameByGroupId(Long groupId) {
        return groupDAO.findNameByGroupId(groupId);
    }

    @Transactional
    public Boolean isGroupMember(Long userId, Long groupId) {
        return groupUserDAO.isGroupMember(userId, groupId);
    }

    @Transactional
    public List<Member> findGroupMembers(Long groupId) {
        return groupDAO.findGroupMembers(groupId);
    }

    public void checkAccessPermission(Long groupId, Long id) throws UnauthorizedAccessException {
        if (!groupUserDAO.isGroupMember(id, groupId)) {
            throw new UnauthorizedAccessException("접근 권한이 없습니다.");
        }
    }

    public NonGroupFriendsResponse getMemberInviteList(Long groupId, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);
        List<FriendInfo> friendInfos = friendDAO.getFriendsInfo(userId, FriendStatus.FRIEND);
        List<NonGroupFriend> nonGroupFriends = new ArrayList<>();
        for (var friendInfo : friendInfos) {
            String status = "NONE";
            if (groupUserDAO.isGroupMember(friendInfo.getUserId(), groupId)) {
                Short state = groupUserDAO.getState(friendInfo.getUserId(), groupId);
                if (state == 1) {
                    continue;
                }
                status = "RECEIVE";
            }

            nonGroupFriends.add(new NonGroupFriend(
                    friendInfo.getName(),
                    friendInfo.getUsername(),
                    friendInfo.getProfileImageUrl(),
                    status
            ));
        }

        return new NonGroupFriendsResponse(nonGroupFriends);
    }

    public AvailableDateRatios findAvailableDateRatios(Long groupId, YearMonth yearMonth, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        List<Long> groupMemberIds = groupUserDAO.getGroupMemberIds(groupId);
        HashMap<LocalDate, Double> availableDateRatio = new HashMap<>();
        for (var groupMemberId : groupMemberIds) {
            List<LocalDate> availableDates = availableDateDAO.findAvailableDatesByUserIdInRange(groupMemberId, startOfCalendar, endOfCalendar);
            for (var availableDate : availableDates) {
                if (!availableDateRatio.containsKey(availableDate)) {
                    availableDateRatio.put(availableDate, 1.0);
                    continue;
                }
                availableDateRatio.put(availableDate, availableDateRatio.get(availableDate) + 1.0);
            }
        }

        List<AvailableDateRatio> availableDateRatios = new ArrayList<>();
        Double countOfGroupMember = (double) groupMemberIds.size();
        for (var availableDate : availableDateRatio.entrySet()) {
            availableDateRatios.add(new AvailableDateRatio(
                    availableDate.getKey().toString(),
                    availableDate.getValue() / countOfGroupMember * 100.0));
        }

        return new AvailableDateRatios(availableDateRatios);
    }

    public List<String> findAvailableMembers(Long groupId, LocalDate date, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);

        List<Long> groupMemberIds = groupUserDAO.getGroupMemberIds(groupId);
        List<String> availableMemberNames = new ArrayList<>();
        for (var memberId : groupMemberIds) {
            if (availableDateDAO.contains(memberId, date)) {
                availableMemberNames.add(userDAO.findNameById(memberId));
            }
        }
        return availableMemberNames;
    }

    public AvailableMemberInfos getAvailableMemberInfos(Long groupId, YearMonth yearMonth, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        List<Long> groupMemberIds = groupUserDAO.getGroupMemberIds(groupId);
        List<AvailableMemberInfo> availableMemberInfos = new ArrayList<>();
        for (var memberId : groupMemberIds) {
            String name = userDAO.findNameById(memberId);
            String profileImage = userDAO.findProfileImageById(memberId);
            List<String> availableDates = new ArrayList<>();
            for (var availableDate : availableDateDAO.findAvailableDatesByUserIdInRange(memberId, startOfCalendar, endOfCalendar)) {
                availableDates.add(availableDate.toString());
            }
            availableMemberInfos.add(new AvailableMemberInfo(name, profileImage, availableDates));
        }

        return new AvailableMemberInfos(availableMemberInfos);
    }
}
