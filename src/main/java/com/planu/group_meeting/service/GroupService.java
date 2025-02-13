package com.planu.group_meeting.service;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dao.*;
import com.planu.group_meeting.dto.AvailableDateDto;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatio;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatios;
import com.planu.group_meeting.dto.FriendDto.FriendInfo;
import com.planu.group_meeting.dto.GroupDTO.*;
import com.planu.group_meeting.dto.GroupInviteResponseDTO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.dto.NotificationDTO;
import com.planu.group_meeting.entity.Group;
import com.planu.group_meeting.entity.GroupUser;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.entity.common.EventType;
import com.planu.group_meeting.entity.common.FriendStatus;
import com.planu.group_meeting.exception.group.GroupNotFoundException;
import com.planu.group_meeting.exception.group.UnauthorizedAccessException;
import com.planu.group_meeting.service.file.S3Uploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@Slf4j
public class GroupService {
    private final GroupDAO groupDAO;
    private final UserDAO userDAO;
    private final S3Uploader s3Uploader;
    private final GroupUserDAO groupUserDAO;
    private final FriendDAO friendDAO;
    private final AvailableDateDAO availableDateDAO;
    private final NotificationService notificationService;

    @Autowired
    public GroupService(GroupDAO groupDAO, UserDAO userDAO, S3Uploader s3Uploader, GroupUserDAO groupUserDAO, FriendDAO friendDAO, AvailableDateDAO availableDateDAO, NotificationService notificationService) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
        this.s3Uploader = s3Uploader;
        this.groupUserDAO = groupUserDAO;
        this.friendDAO = friendDAO;
        this.availableDateDAO = availableDateDAO;
        this.notificationService = notificationService;
    }

    @Transactional
    public GroupResponseDTO createGroup(String username, String groupName, MultipartFile groupImage) {
        String imageUrl = s3Uploader.uploadFile(groupImage);

        Group group = Group.builder()
                .name(groupName)
                .groupImageUrl(imageUrl)
                .build();

        groupDAO.insertGroup(group);


        groupDAO.insertGroupUser(GroupUser.builder()
                .userId(groupDAO.findUserIdByUsername(username))
                .groupId(group.getId())
                .groupRole(GroupUser.GroupRole.LEADER)
                .groupState(1)
                .isPin(false)
                .build());


        return GroupResponseDTO.builder()
                .groupId(group.getId())
                .groupName(groupName)
                .leaderUsername(username)
                .groupImageUrl(imageUrl)
                .build();
    }

    @Transactional
    public GroupInviteResponseDTO inviteUser(CustomUserDetails customUserDetails, String username, Long groupId) {
        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(customUserDetails.getId(), groupId);

        if(groupUser.getGroupRole() != GroupUser.GroupRole.LEADER){
            throw new IllegalArgumentException("초대 권한이 없습니다.");
        }

        Group group = groupDAO.findGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("해당 그룹이 존재하지 않습니다.");
        }

        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자 '" + username + "'을 찾을 수 없습니다.");
        }

        groupDAO.insertGroupUser(GroupUser.builder()
                .groupId(groupId)
                .userId(user.getId())
                .groupRole(GroupUser.GroupRole.PARTICIPANT)
                .groupState(0)
                .isPin(false)
                .build());

        return GroupInviteResponseDTO.builder()
                .invitedUsername(username)
                .groupId(groupId)
                .groupName(group.getName())
                .groupImageUrl(group.getGroupImageUrl())
                .build();
    }

    @Transactional
    public void inviteUserCancel(Long leaderId, String username, Long groupId){
        GroupUser groupUserLeader = groupDAO.findGroupUserByUserIdAndGroupId(leaderId, groupId);

        if(groupUserLeader.getGroupRole() != GroupUser.GroupRole.LEADER){
            throw new IllegalArgumentException("초대 권한이 없습니다.");
        }

        Group group = groupDAO.findGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("해당 그룹이 존재하지 않습니다.");
        }

        Long userId = groupDAO.findUserIdByUsername(username);

        GroupUser groupUserUser = groupDAO.findGroupUserByUserIdAndGroupId(userId, groupId);
        if (groupUserUser == null) {
            throw new IllegalArgumentException("사용자 '" + username + "'을 찾을 수 없습니다.");
        }

        if (groupUserUser.getGroupState() == 1){
            throw new IllegalArgumentException("사용자 '" + username + "'은 이미 그룹의 멤버입니다.");
        }

        groupDAO.deleteGroupUserByUserIdAndGroupId(userId, groupId);
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

        createGroupDeleteNotification(groupId, group);

        groupDAO.deleteGroupScheduleParticipant(groupId);
        groupDAO.deleteGroupScheduleComment(groupId);
        groupDAO.deleteGroupUser(groupId);
        groupDAO.deleteChatMessage(groupId);
        groupDAO.deleteGroupSchedule(groupId);
        groupDAO.deleteGroup(groupId);
    }

    private void createGroupDeleteNotification(Long groupId, Group group) {
        List<Long> groupUserIds = groupDAO.findUserIdsByGroupId(groupId);
        log.info("groupUserIds={}", groupUserIds);
        for(Long groupUserId : groupUserIds){
            log.info("groupUserId={}", groupUserId);
            NotificationDTO.GroupDeleteNotification groupDeleteNotification =
                    new NotificationDTO.GroupDeleteNotification(groupUserId, group.getName() + " 그룹이 삭제되었습니다.");
            notificationService.sendNotification(EventType.GROUP_DELETE, groupDeleteNotification);
        }
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
        Long userId = groupDAO.findUserIdByUsername(username);
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
    public void pinnedGroup(Long userId, Long groupId) {
        Group group = groupDAO.findGroupById(groupId);
        GroupUser groupUser = groupDAO.findGroupUserByUserIdAndGroupId(userId, groupId);

        if (group == null) {
            throw new IllegalArgumentException("해당 그룹이 존재하지 않습니다.");
        }

        if (groupUser == null) {
            throw new IllegalArgumentException("해당 그룹의 그룹원이 아닙니다.");
        }

        int updatedRows;
        if (groupUser.getIsPin()) {
            updatedRows = groupDAO.updateGroupUnpin(userId, groupId, groupUser.getVersion());
        } else {
            updatedRows = groupDAO.updateGroupPin(userId, groupId, groupUser.getVersion());
        }

        if (updatedRows == 0) {
            throw new ConcurrentModificationException("다른 사용자가 데이터를 먼저 변경했습니다. 다시 시도하세요.");
        }
    }



    @Transactional
    public String findNameByGroupId(Long groupId, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);
        return groupDAO.findNameByGroupId(groupId);
    }

    @Transactional
    public Boolean isGroupMember(Long userId, Long groupId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);
        return groupUserDAO.isGroupMember(userId, groupId);
    }

    @Transactional
    public List<Member> findGroupMembers(Long groupId, Long userId, String keyword) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);
        return groupDAO.findGroupMembers(groupId, keyword);
    }

    public void checkAccessPermission(Long groupId, Long id) throws UnauthorizedAccessException {
        if (!groupUserDAO.isGroupMember(id, groupId)) {
            throw new UnauthorizedAccessException("접근 권한이 없습니다.");
        }
    }

    @Transactional
    public NonGroupFriendsResponse getMemberInviteList(Long groupId, Long userId, String keyword) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);

        List<FriendInfo> friendInfos = friendDAO.getFriendsInfo(userId, FriendStatus.FRIEND, keyword);

        List<NonGroupFriend> nonGroupFriends = new ArrayList<>();
        for (var friendInfo : friendInfos) {
            String status = "NONE";
            if (groupUserDAO.isExistsGroupUser(friendInfo.getUserId(), groupId)) {
                Short state = groupUserDAO.getState(friendInfo.getUserId(), groupId);
                if (state == 1) {
                    continue;
                }
                status = "PROGRESS";
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

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
    public AvailableDateInfos getAvailableDateInfos(Long groupId, YearMonth yearMonth, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        List<Long> groupMembers = groupUserDAO.getGroupMemberIds(groupId);
        HashMap<LocalDate, List<String>> membersByAvailableDate = new HashMap<>();
        for(var memberId : groupMembers) {
            for (var availableDate : availableDateDAO.findAvailableDatesByUserIdInRange(memberId, startOfCalendar, endOfCalendar)) {
                if (!membersByAvailableDate.containsKey(availableDate)) {
                    membersByAvailableDate.put(availableDate, new ArrayList<>());
                }
                membersByAvailableDate.get(availableDate).add(userDAO.findNameById(memberId));
            }
        }
        AvailableDateInfos availableDateInfos = new AvailableDateInfos(new ArrayList<>());

        for (var dateInfos : membersByAvailableDate.entrySet()) {
            availableDateInfos
                    .getAvailableDateInfos()
                    .add(new AvailableDateInfo(dateInfos.getKey().toString(), dateInfos.getValue()));
        }

        return availableDateInfos;
    }

    @Transactional
    public List<AvailableDateDto.AvailableDateRanks> getAvailableDateRanks(Long groupId, YearMonth yearMonth, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        List<Long> groupMemberIds = groupUserDAO.getGroupMemberIds(groupId);
        Map<LocalDate, Integer> countOfAvailableDate = new HashMap<>();
        for(var memberId : groupMemberIds) {
            for (var availableDate : availableDateDAO.findAvailableDatesByUserIdInRange(memberId, startOfCalendar, endOfCalendar)) {
                if (!countOfAvailableDate.containsKey(availableDate)) {
                    countOfAvailableDate.put(availableDate, 0);
                }
                countOfAvailableDate.put(availableDate, countOfAvailableDate.get(availableDate) + 1);
            }
        }

        List<Map.Entry<LocalDate, Integer>> availableDateRanks = new ArrayList<>(countOfAvailableDate.entrySet());

        availableDateRanks.sort(
                (entryA, entryB) -> {
                    int countComparison = entryB.getValue().compareTo(entryA.getValue());
                    if(countComparison != 0) {
                        return countComparison;
                    }
                    return entryA.getKey().compareTo(entryB.getKey());
                }
        );

        List<AvailableDateDto.AvailableDateRanks> response = new ArrayList<>();
        var ranks = 1L;
        var previousCount = availableDateRanks.get(0).getValue();
        for(var entry : availableDateRanks) {
            if(!previousCount.equals(entry.getValue())) {
                ranks++;
                previousCount = entry.getValue();
            }
            response.add(new AvailableDateDto.AvailableDateRanks(ranks, entry.getKey().toString()));
        }

        return response;
    }

    @Transactional
    public GroupInfo getGroupDetails(Long groupId, Long userId) {
        if (groupDAO.findGroupById(groupId) == null) {
            throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
        }
        checkAccessPermission(groupId, userId);

        Group group = groupDAO.findGroupById(groupId);
        Boolean isPin = groupUserDAO.getPinById(groupId, userId);

        return new GroupInfo(group.getName(), isPin);
    }
}
