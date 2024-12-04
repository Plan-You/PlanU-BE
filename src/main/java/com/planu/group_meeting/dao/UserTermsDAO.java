package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.UserTerms;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserTermsDAO {
    void saveTerms(UserTerms userTerms);
}
