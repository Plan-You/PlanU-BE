package com.planu.group_meeting.service;

import com.planu.group_meeting.entity.common.Role;
import com.planu.group_meeting.exception.user.InvalidTokenException;
import com.planu.group_meeting.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final JwtUtil jwtUtil;
    public boolean isNewUser(String access){
        if(access==null){
            throw new InvalidTokenException();
        }
        return Role.valueOf(jwtUtil.getRole(access)) == Role.ROLE_GUEST;

    }


}
