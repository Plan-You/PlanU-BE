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
import java.time.LocalDate;
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
            throw new GroupNotFoundException("그룹이 없습니다.");
        }
        if(!groupUserDAO.isGroupMember(userId, groupId)) {
            throw new UnauthorizedAccessException("그룹원이 아닙니다.");
        }
        if(groupScheduleDAO.findById(groupId, scheduleId).isEmpty()) {
            throw new ScheduleNotFoundException("그룹 일정이 없습니다.");
        }

        LocalDateTime startDateTime = groupScheduleDAO.findById(groupId, scheduleId).get().getStartDateTime();
        LocalDateTime endDateTime = groupScheduleDAO.findById(groupId, scheduleId).get().getEndDateTime();

        LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        boolean isAllDaySchedule = isAllDaySchedule(startDateTime, endDateTime);

        if(isAllDaySchedule) {
            LocalDate scheduleDate = startDateTime.toLocalDate();
            LocalDate currentDate = now.toLocalDate();

            if(!currentDate.equals(scheduleDate)) {
                throw new IllegalArgumentException("종일 일정은 해당 일정 당일에만 위치 공유를 할 수 있습니다.");
            }
        }
        else if(now.isBefore(startDateTime.minusHours(1)) || now.isAfter(startDateTime.plusHours(1))) {
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
            throw new IllegalArgumentException("해당 일정에 참석자가 아닙니다.");
        }

        var response =  getGroupMemberLocation(groupId, scheduleId);
        return response;
    }

    private boolean isAllDaySchedule(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if(startDateTime == null || endDateTime == null) {
            return false;
        }

        boolean sameDayCheck = startDateTime.toLocalDate().equals(endDateTime.toLocalDate());
        boolean startTimeCheck = startDateTime.getHour() == 0 && startDateTime.getMinute() == 0;
        boolean endTimeCheck = endDateTime.getHour() == 23 && endDateTime.getMinute() == 59;

        return sameDayCheck && startTimeCheck && endTimeCheck;
    }
}
