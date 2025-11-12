package com.offnal.shifterz.member.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.member.dto.MemberRequestDto;
import com.offnal.shifterz.member.dto.MemberResponseDto;
import com.offnal.shifterz.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description  = "User 관련 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 수정", description = "내 프로필 정보를 수정합니다.")
    @SuccessApiResponses.UpdateProfile
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    public SuccessResponse<MemberResponseDto.MemberUpdateResponseDto> updateProfile(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart("request") @Valid MemberRequestDto.MemberUpdateRequestDto request
    ) {
        MemberResponseDto.MemberUpdateResponseDto response = memberService.updateProfile(request, profileImage);
        return SuccessResponse.success(SuccessCode.PROFILE_UPDATED, response);
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 회원의 정보를 조회합니다.")
    @SuccessApiResponses.MyInfo
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @GetMapping("/profile")
    public SuccessResponse<MemberResponseDto.MemberUpdateResponseDto> getMyInfo() {
        return SuccessResponse.success(SuccessCode.MY_INFO_FETCHED, memberService.getMyInfo());
    }

    @Operation(summary = "회원 탈퇴", description = "사용자의 계정을 삭제합니다.")
    @SuccessApiResponses.Withdraw
    @ErrorApiResponses.Auth
    @ErrorApiResponses.Common
    @DeleteMapping("/withdraw")
    public SuccessResponse<Void> withdraw(HttpServletRequest request) {
        memberService.withdrawCurrentMember(request);
        return SuccessResponse.success(SuccessCode.MEMBER_DELETED);
    }


}
