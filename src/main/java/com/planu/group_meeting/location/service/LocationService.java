package com.planu.group_meeting.location.service;

import com.planu.group_meeting.dao.GroupUserDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.location.dto.request.LocationDTO;
import com.planu.group_meeting.location.dto.response.GroupMemberLocation;
import com.planu.group_meeting.location.dto.response.GroupMemberLocationResponse;
import com.planu.group_meeting.location.impl.LocationImpl;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationImpl locationImpl;
    private final GroupUserDAO groupUserDAO;
    private final UserDAO userDAO;

    public void updateLocation(String username, LocationDTO locationDTO) {
        try {
            locationImpl.save(userDAO.findIdByUsername(username), locationDTO);
        } catch(Exception e) {
            throw new IllegalArgumentException("위치 저장 실패");
        }
    }

    public GroupMemberLocationResponse getGroupMemberLocation(Long groupId)  {
        List<GroupMemberLocation> groupMemberLocations = new ArrayList<>();
        for(Long groupMemberId : groupUserDAO.getGroupMemberIds(groupId)) {
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
}
