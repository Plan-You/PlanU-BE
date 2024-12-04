package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.UserTerms;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserTermsDto {
    @Data
    @NoArgsConstructor
    public static class TermsRequest {
        @Pattern(regexp = "\\s*true\\s*", message = "개인정보 보호 정책에 동의해야 합니다.")
        private String isPrivacyPolicyAgreed;

        @Pattern(regexp = "\\s*true\\s*", message = "서비스 이용 약관에 동의해야 합니다.")
        private String isTermsOfServiceAgreed;

        @Pattern(regexp = "\\s*(true|false)\\s*", message = "SNS 수신 동의 여부는 'true' 또는 'false'만 입력 가능합니다.")
        private String isSnsReceiveAgreed;

        public UserTerms toEntity(Long userId) {
            return UserTerms.builder()
                    .userId(userId)
                    .isPrivacyPolicyAgreed(isPrivacyPolicyAgreed)
                    .isTermsOfServiceAgreed(isTermsOfServiceAgreed)
                    .isSnsReceiveAgreed(isSnsReceiveAgreed)
                    .build();
        }
    }


}
