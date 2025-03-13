package com.planu.group_meeting.scheduler;

import com.planu.group_meeting.dao.FriendDAO;
import com.planu.group_meeting.dao.NotificationDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.UserDto;
import com.planu.group_meeting.entity.common.EventType;
import com.planu.group_meeting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.planu.group_meeting.dto.NotificationDTO.BirthdayFriendNotification;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final String BIRTHDAY_NOTIFICATION_MESSAGE = "오늘은 %s님의 생일입니다!";

    private final NotificationDAO notificationDAO;
    private final UserDAO userDAO;
    private final FriendDAO friendDAO;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteOldNotification() {
        notificationDAO.deleteOldNotification();
    }

    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void sendBirthdayNotifications(){
        // 오늘 생일자 조회
        LocalDate today = LocalDate.now();
        List<UserDto.BirthdayPerson> birthdayPeople = userDAO.findBirthdayPersonByDate(today);

        // 생일자 친구들 조회 후 알림 전송하기
        for(UserDto.BirthdayPerson birthdayPerson : birthdayPeople){
            Long birthdayUserId = birthdayPerson.getUserId();
            List<Long> friendOfUser = friendDAO.findFriendOfUser(birthdayUserId);
            sendNotification(friendOfUser, birthdayPerson.getName());
        }
    }

    private void sendNotification(List<Long> users, String birthdayPersonName){
        String contents = String.format(BIRTHDAY_NOTIFICATION_MESSAGE, birthdayPersonName);
        for(Long userId : users){
            BirthdayFriendNotification birthdayFriendNotification = new BirthdayFriendNotification(userId,contents);
            notificationService.sendNotification(EventType.BIRTHDAY, birthdayFriendNotification);
        }
    }




}
