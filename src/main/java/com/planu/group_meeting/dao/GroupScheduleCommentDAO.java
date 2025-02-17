package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.GroupScheduleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupScheduleCommentDAO {

    void create(GroupScheduleComment comment);
}
