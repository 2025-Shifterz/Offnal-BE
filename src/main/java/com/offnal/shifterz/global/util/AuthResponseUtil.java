package com.offnal.shifterz.global.util;

import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.member.dto.AuthResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

public class AuthResponseUtil {

    public static ResponseEntity<?> buildAuthResponse(AuthResponseDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + dto.getAccessToken());
        headers.set("Refresh-Token", dto.getRefreshToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, dto));
    }
}
