package com.planu.group_meeting.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupUser {

    private Long userId;
    private Long groupId;
    private GroupRole groupRole;  // Enum을 사용
    private Integer groupState;   // 상태를 나타내는 Integer로 매핑
    private Boolean isPin;
    private Long version;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Getter
    public enum GroupRole {
        LEADER("LEADER"),
        PARTICIPANT("PARTICIPANT");

        private String role;

        GroupRole(String role) {
            this.role = role;
        }

        public static GroupRole fromString(String role) {
            for (GroupRole groupRole : GroupRole.values()) {
                if (groupRole.getRole().equals(role)) {
                    return groupRole;
                }
            }
            return PARTICIPANT; // 기본값 설정
        }
    }
}


