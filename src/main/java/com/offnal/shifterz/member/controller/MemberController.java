package com.offnal.shifterz.member.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.global.util.S3Service;
import com.offnal.shifterz.global.util.dto.PresignedUrlResponse;
import com.offnal.shifterz.member.dto.MemberRequestDto;
import com.offnal.shifterz.member.dto.MemberResponseDto;
import com.offnal.shifterz.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description  = "User 관련 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final S3Service s3Service;

    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @Operation(summary = "S3 업로드용 Presigned URL 발급",
            description = "프로필 이미지를 S3에 업로드하기 위한 URL을 발급합니다.\n\n" +
                        "발급된 key는 회원의 프로필 이미지로 자동 반영됩니다.\n\n" +
                        "✅ 반환값:\n" +
                        "- **uploadUrl**: 이미지를 직접 업로드할 S3 주소\n" +
                        "- **key**: 업로드된 파일의 S3 경로\n\n")
    @GetMapping("/profile/upload-url")
    public SuccessResponse<PresignedUrlResponse> generateUploadUrl() {
        PresignedUrlResponse response = s3Service.generateUploadPresignedUrl();
        memberService.updateProfileImage(response.getKey());
        return SuccessResponse.success(SuccessCode.PROFILE_UPLOAD_URL_CREATED, response);
    }

    @Operation(summary = "회원 정보 수정", description = "회원의 이름, 이메일, 전화번호 등 일반 정보를 수정합니다.")
    @PatchMapping("/profile")
    @SuccessApiResponses.UpdateProfile
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    public SuccessResponse<MemberResponseDto.MemberUpdateResponseDto> updateMemberInfo(
            @RequestBody @Valid MemberRequestDto.MemberUpdateRequestDto request
    ) {
        MemberResponseDto.MemberUpdateResponseDto response = memberService.updateMemberInfo(request);
        return SuccessResponse.success(SuccessCode.PROFILE_UPDATED, response);
    }

    @Operation(
            summary = "프로필 이미지 삭제",
            description = "회원의 프로필 이미지를 삭제합니다.\n\n" +
                    "✅ 동작:\n" +
                    "- S3에 저장된 기존 프로필 이미지를 삭제하고,\n" +
                    "- 회원 DB의 profileImageKey를 null로 초기화합니다."
    )
    @DeleteMapping("/profile/image")
    @SuccessApiResponses.UpdateProfile
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    public SuccessResponse<Void> deleteProfileImage() {
        memberService.deleteProfileImage();
        return SuccessResponse.success(SuccessCode.PROFILE_IMAGE_DELETED);
    }

    @Operation(summary = "내 정보 조회",
            description = "로그인한 회원의 정보를 조회합니다.\n\n" +
                    "✅ 반환값:\n" +
                    "- **profileImageKey**: S3에 저장된 이미지 key\n" +
                    "- **profileImageUrl**: 실제 이미지를 표시할 수 있는 S3 조회용 presigned URL (10분 유효)\n\n")
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
