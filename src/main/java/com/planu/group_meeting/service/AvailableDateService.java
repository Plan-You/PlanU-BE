package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.AvailableDateDAO;
import com.planu.group_meeting.dto.AvailableDateDto;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDatesRequest;
import com.planu.group_meeting.entity.AvailableDate;
import com.planu.group_meeting.exception.schedule.PastDateValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailableDateService {

    private final AvailableDateDAO availableDateDAO;

    @Transactional
    public void createAvailableDates(Long userId, AvailableDatesRequest availableDatesDto) {
        List<LocalDate>requestDates = availableDatesDto.getAvailableDates();
        validateAvailableDates(requestDates);
        List<LocalDate>availableDateList = availableDateDAO.findAvailableDatesByUserId(userId);

        List<AvailableDate>datesToInsert = requestDates.stream()
                .filter(date -> !availableDateList.contains(date))
                .map(date -> new AvailableDate(userId, date))
                .toList();

        List<LocalDate>datesToDelete = requestDates.stream()
                .filter(availableDateList::contains)
                .toList();

        if(!datesToInsert.isEmpty()){
            availableDateDAO.insertAvailableDates(datesToInsert);
        }
        if(!datesToDelete.isEmpty()){
            availableDateDAO.deleteAvailableDates(userId,datesToDelete);
        }
    }

    private void validateAvailableDates(List<LocalDate> requestDates) {
        LocalDate today = LocalDate.now();
        for (LocalDate date : requestDates) {
            if (date.isBefore(today)) {
                throw new PastDateValidationException();
            }
        }
    }

    public List<LocalDate>findAvailableDates(Long userId, LocalDate startDate, LocalDate endDate){
        return availableDateDAO.findAvailableDatesByUserIdInRange(userId, startDate, endDate);
    }

}
