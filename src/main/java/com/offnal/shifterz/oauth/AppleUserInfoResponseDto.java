package com.offnal.shifterz.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleUserInfoResponseDto {

    @JsonProperty("sub")
    private String sub; // Apple 고유 사용자 ID

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("is_private_email")
    private Boolean isPrivateEmail;

    // 실명은 최초 로그인시에만 제공됨
    private Name name;

    @Getter
    @NoArgsConstructor
    public static class Name {
        @JsonProperty("firstName")
        private String firstName;

        @JsonProperty("lastName")
        private String lastName;

        public String getFullName() {
            if (firstName == null && lastName == null) {
                return null;
            }
            return (lastName != null ? lastName : "") + (firstName != null ? firstName : "");
        }
    }
}