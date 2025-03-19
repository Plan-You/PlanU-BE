package com.planu.group_meeting.location.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.location.dto.request.LocationDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LocationImpl {

    private static final String REDIS_KEY_PREFIX = "location:{%d}";

    private final StringRedisTemplate redisTemplate;
    private final UserDAO userDAO;

    private ValueOperations<String, String> valOps;
    private ObjectMapper objMapper;

    @PostConstruct
    public void init() {
        valOps = redisTemplate.opsForValue();
        objMapper = new ObjectMapper();
    }

    public void save(Long userId, LocationDTO location) throws JsonProcessingException {
        String locationJson = objMapper.writeValueAsString(location);
        String locationKey = getKeyByUserId(userId);
        valOps.set(locationKey, locationJson);
    }

    public LocationDTO findById(Long userId) throws JsonProcessingException {
        String locationKey = getKeyByUserId(userId);
        String locationJson = valOps.get(locationKey);
        if(locationJson == null) {
            return new LocationDTO();
        }
        return objMapper.readValue(locationJson, LocationDTO.class);
    }

    private String getKeyByUserId(Long userId) {
        return String.format(REDIS_KEY_PREFIX, userId);
    }
}
