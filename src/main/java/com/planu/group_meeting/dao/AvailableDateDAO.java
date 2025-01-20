package com.planu.group_meeting.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AvailableDateDAO {
    void insertAvailableDates(@Param("userId")Long userId, @Param("availableDates") List<LocalDate>availableDates);

    void deleteAllAvailableDates(Long userId);

    List<LocalDate> findAvailableDatesByUserIdInRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    Boolean contains(Long userId, LocalDate date);
}
