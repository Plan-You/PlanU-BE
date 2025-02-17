package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupScheduleCommentDAO;
import com.planu.group_meeting.dto.GroupScheduleCommentDTO;
import com.planu.group_meeting.entity.GroupScheduleComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupScheduleCommentService {
    private final GroupScheduleCommentDAO groupScheduleCommentDAO;

    public void create(
            Long userId,
            Long groupId,
            Long groupScheduleId,
            GroupScheduleCommentDTO.GroupScheduleCommentRequest groupScheduleComment
    ) {
        GroupScheduleComment comment = groupScheduleComment.toEntity(userId, groupId, groupScheduleId);
        groupScheduleCommentDAO.create(comment);
    }
}
