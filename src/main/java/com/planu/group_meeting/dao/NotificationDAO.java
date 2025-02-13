package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationDAO {

    void save(NotificationDTO notification);

    Optional<NotificationDTO> findById(Long userId, Long notificationId);

    List<NotificationDTO> findAllByUserId(Long userId);

    void updateIsRead(Long notificationId);

    void deleteOldNotification();

}
