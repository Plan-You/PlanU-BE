package com.planu.group_meeting.dao;
import com.planu.group_meeting.entity.AvailableDate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AvailableDateDAO {
    void insertAvailableDates(List<AvailableDate> availableDates);

    void deleteAvailableDates(@Param("userId")Long userId, @Param("datesToDelete") List<LocalDate>datesToDelete);

    List<LocalDate> findAvailableDatesByUserId(Long userId);

}
