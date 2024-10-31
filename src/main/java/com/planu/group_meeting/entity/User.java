package com.planu.group_meeting.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Role role;
    private String profileImgUrl;
    private String birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
