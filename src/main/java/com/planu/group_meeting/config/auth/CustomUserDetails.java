package com.planu.group_meeting.config.auth;

import com.planu.group_meeting.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    //    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Arrays.stream(user.getRole().name().split(","))
//                .map(role -> "ROLE_" + role)  // 수정: ROLE_ 접두사 추가
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(new String[]{user.getRole().name()})  // Role을 하나의 값으로 처리
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
