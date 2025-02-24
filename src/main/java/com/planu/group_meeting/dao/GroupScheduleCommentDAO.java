package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.GroupScheduleComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupScheduleCommentDAO {

    void create(GroupScheduleComment comment);

    // 그룹 일정의 모든 댓글 정보를 조회
    List<GroupScheduleComment> getAllByGroupScheduleId(Long groupId, Long groupScheduleId);

    void deleteCommentById(Long groupId, Long groupScheduleId, Long commentId);
}
