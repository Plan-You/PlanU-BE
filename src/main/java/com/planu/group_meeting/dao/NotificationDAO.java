package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationDAO {

    void save(NotificationDTO notification);

    List<NotificationDTO> findAllByUserId(Long userId);

    void deleteOldNotification();

}
