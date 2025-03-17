package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

import static com.planu.group_meeting.dto.NotificationDTO.*;

@Mapper
public interface NotificationDAO {

    void save(NotificationDTO notification);

    Optional<NotificationDTO> findById(Long userId, Long notificationId);

    List<NotificationDTO> findAllByUserId(Long userId);

    void updateIsRead(Long notificationId);

    void updateAllIsRead(Long userId);

    void deleteOldNotification();

    UnreadNotificationResponse hasUnreadNotification(Long userId);

}
