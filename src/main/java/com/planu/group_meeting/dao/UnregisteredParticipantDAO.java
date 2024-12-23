package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.UnregisteredParticipant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UnregisteredParticipantDAO {
    void insertUnregisteredParticipants(List<UnregisteredParticipant> unregisteredParticipants);

    void deleteAllParticipantsByScheduleId(Long scheduleId);
}
