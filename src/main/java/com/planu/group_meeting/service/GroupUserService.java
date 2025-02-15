package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupUserDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.exception.group.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupUserService {
    private final GroupUserDAO groupUserDAO;
    private final UserDAO userDAO;

    @Transactional
    public void isGroupMember(Long userId, Long groupId) {
        if(!groupUserDAO.isGroupMember(userId, groupId)) {
            throw new UnauthorizedAccessException("접근 권한이 없습니다.");
        }
    }

    public String setStatusById(Long userId, Long groupId) {
        if(groupUserDAO.isGroupMember(userId, groupId)) {
            Short status = groupUserDAO.getState(userId, groupId);
            return (status == 0 ? "RECEIVE" : "GROUP");
        }
        return "NONE";
    }

    public List<String> getBirthdayByDate(Long groupId, LocalDate startDate) {
        if(startDate == null) {
            startDate = LocalDate.now();
        }

        List<String> birthdayPerson = new ArrayList<>();
        for(var memberId : groupUserDAO.getGroupMemberIds(groupId)) {
            LocalDate birthday = userDAO.findBirthdayById(memberId);

            if(startDate.getMonthValue() == birthday.getMonthValue() && startDate.getDayOfMonth() == birthday.getDayOfMonth()) {
                birthdayPerson.add(userDAO.findById(memberId).getName());
            }
        }

        return birthdayPerson;
    }
}
