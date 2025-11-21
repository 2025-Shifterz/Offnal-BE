package com.offnal.shifterz.oauth;

import lombok.Data;

@Data
public class AppleLoginRequest {
    private String identityToken;
    private String user;  // Apple user ID
    private String email;
    private FullName fullName;

    @Data
    public static class FullName {
        private String givenName;
        private String familyName;

        public String getFullName() {
            if (givenName == null && familyName == null) {
                return null;
            }
            return (familyName != null ? familyName : "") + (givenName != null ? givenName : "");
        }
    }
}
