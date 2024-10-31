package com.planu.group_meeting.config.auth;

import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDAO userDAO;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다");
        }
        return new CustomUserDetails(user);
    }
}
