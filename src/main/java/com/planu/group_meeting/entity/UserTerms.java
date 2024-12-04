package com.planu.group_meeting.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class UserTerms {
    private Long id;
    private Long userId;
    private String isPrivacyPolicyAgreed;
    private String isTermsOfServiceAgreed;
    private String isSnsReceiveAgreed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
