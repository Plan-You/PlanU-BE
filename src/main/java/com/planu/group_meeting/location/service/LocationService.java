package com.planu.group_meeting.location.service;

import com.planu.group_meeting.dao.GroupDAO;
import com.planu.group_meeting.dao.GroupScheduleDAO;
import com.planu.group_meeting.dao.GroupScheduleParticipantDAO;
import com.planu.group_meeting.dao.GroupUserDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.exception.group.GroupNotFoundException;
import com.planu.group_meeting.exception.group.UnauthorizedAccessException;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
import com.planu.group_meeting.location.dto.request.LocationDTO;
import com.planu.group_meeting.location.dto.response.GroupMemberLocation;
import com.planu.group_meeting.location.dto.response.GroupMemberLocationResponse;
import com.planu.group_meeting.location.impl.LocationImpl;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationImpl locationImpl;
    private final GroupUserDAO groupUserDAO;
    private final GroupScheduleDAO groupScheduleDAO;
    private final GroupDAO groupDAO;
    private final GroupScheduleParticipantDAO groupScheduleParticipantDAO;
    private final UserDAO userDAO;

    public void updateLocation(String username, LocationDTO locationDTO) {
        try {
            locationImpl.save(userDAO.findIdByUsername(username), locationDTO);
        } catch(Exception e) {
            throw new IllegalArgumentException("위치 저장 실패");
        }
    }

    public GroupMemberLocationResponse getGroupMemberLocation(Long groupId, Long scheduleId)  {
        List<GroupMemberLocation> groupMemberLocations = new ArrayList<>();
        for(var participants : groupScheduleParticipantDAO.findByScheduleId(groupId, scheduleId)) {
            Long groupMemberId = participants.getUserId();
            try {
                LocationDTO locationDTO = locationImpl.findById(groupMemberId);
                groupMemberLocations.add(
                        GroupMemberLocation.builder()
                                .username(userDAO.findUsernameById(groupMemberId))
                                .name(userDAO.findNameById(groupMemberId))
                                .profileImage(userDAO.findProfileImageById(groupMemberId))
                                .latitude(locationDTO.getLatitude())
                                .longitude(locationDTO.getLongitude())
                                .build()
                );
            } catch(Exception e) {
                groupMemberLocations.add(
                        GroupMemberLocation.builder()
                                .username(userDAO.findUsernameById(groupMemberId))
                                .name(userDAO.findNameById(groupMemberId))
                                .profileImage(userDAO.findProfileImageById(groupMemberId))
                                .latitude(0.0)
                                .longitude(0.0)
                                .build()
                );
            }
        }
        return new GroupMemberLocationResponse(groupMemberLocations);
    }

    public GroupMemberLocationResponse getGroupMemberLocation(Long groupId, Long scheduleId, Long userId) {
        if(groupDAO.findGroupById(groupId) == null) {
            System.out.println("[실시간 위치 조회]: 해당 그룹이 없습니다.");
            throw new GroupNotFoundException("그룹이 없습니다.");
        }
        if(!groupUserDAO.isGroupMember(userId, groupId)) {
            System.out.println("[실시간 위치 조회]: 그룹원이 아닙니다.");
            throw new UnauthorizedAccessException("그룹원이 아닙니다.");
        }
        if(groupScheduleDAO.findById(groupId, scheduleId).isEmpty()) {
            System.out.println("[실시간 위치 조회]: 그룹 일정이 없습니다.");
            throw new ScheduleNotFoundException("그룹 일정이 없습니다.");
        }

        LocalDateTime startDateTime = groupScheduleDAO.findById(groupId, scheduleId).get().getStartDateTime();
        LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Seoul")));

        System.out.println("[실시간 위치 조회]: 일정 시작 시간은 "+ startDateTime);
        System.out.println("[실시간 위치 조회]: 현재 시간은 " + now);
        if (now.isBefore(startDateTime.minusHours(1)) || now.isAfter(startDateTime.plusHours(1))) {
            System.out.println("[실시간 위치 조회]: 일정 시작 1시간 전후일 때만 위치 공유를 할 수 있습니다.");
            throw new IllegalArgumentException("일정 시작 1시간 전후일 때만 위치 공유를 할 수 있습니다.");
        }

        boolean isAccessible = false;
        for(var participant : groupScheduleParticipantDAO.findByScheduleId(groupId, scheduleId)) {
            if (participant.getUserId().equals(userId)) {
                isAccessible = true;
                break;
            }
        }

        if(!isAccessible) {
            System.out.println("[실시간 위치 조회]: 해당 일정에 참석자가 아닙니다.");
            throw new IllegalArgumentException("해당 일정에 참석자가 아닙니다.");
        }

        var response =  getGroupMemberLocation(groupId, scheduleId);

        System.out.println("[실시간 위치 조회]: 데이터 조회 성공");
        System.out.println(response);

        return response;
    }
}
