package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationDAO {

    void save(NotificationDTO notification);

}
