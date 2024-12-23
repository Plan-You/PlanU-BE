package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.GroupScheduleParticipant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupScheduleParticipantDAO {
    void insert(List<GroupScheduleParticipant> participants);
}
