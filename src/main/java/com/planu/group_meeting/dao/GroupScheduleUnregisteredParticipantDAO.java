package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.GroupScheduleUnregisteredParticipant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupScheduleUnregisteredParticipantDAO {
    void insert(List<GroupScheduleUnregisteredParticipant> unregisteredParticipants);
}
