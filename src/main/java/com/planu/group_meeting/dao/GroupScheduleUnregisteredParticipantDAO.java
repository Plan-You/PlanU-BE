package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.GroupScheduleDTO;
import com.planu.group_meeting.entity.GroupScheduleUnregisteredParticipant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupScheduleUnregisteredParticipantDAO {
    void insert(List<GroupScheduleUnregisteredParticipant> unregisteredParticipants);

    List<GroupScheduleDTO.ParticipantsResponse> findByScheduleId(Long groupId, Long scheduleId);
}
