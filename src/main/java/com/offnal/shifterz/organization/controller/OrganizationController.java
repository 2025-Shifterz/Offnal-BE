package com.offnal.shifterz.organization.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.organization.dto.OrganizationRequestDto;
import com.offnal.shifterz.organization.dto.OrganizationResponseDto;
import com.offnal.shifterz.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Organization", description = "Organization(조직) 관련 API")
@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    /**
     * Organization 생성
     */
    @Operation(
            summary = "조직 생성",
            description = "새로운 조직을 생성합니다.\n\n" +
                    "✅ 요청 본문에 포함할 수 있는 값:\n" +
                    "- organizatonName: 조직 이름 (String)\n" +
                    "- team: 조 이름 (String)\n"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "조직 생성 요청 예시",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "조직 생성 요청",
                                    value = """
                                            {
                                                "organizationName" : "옾날 병원",
                                                "team" : "1조"
                                            }
                                            """
                            )
                    }
            )

    )
    @SuccessApiResponses.OrganizationCreate
    @ErrorApiResponses.Common
    @PostMapping
    public SuccessResponse<OrganizationResponseDto.OrganizationDto> createOrganization(
            @RequestBody @Valid OrganizationRequestDto.CreateDto request
    ){
        return SuccessResponse.success(SuccessCode.ORGANIZATION_CREATED, organizationService.createOrganization(request));
    }

    /**
     * Organization 수정
     */
    @Operation(summary = "조직 수정", description = "조직 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "조직 수정 요청 예시",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "조직 수정 요청",
                                    value = """
                                            {
                                                "organizationName" : "오프날 병원",
                                                "team" : "2조"
                                            }
                                            """
                            )
                    }
            )

    )
    @SuccessApiResponses.OrganizationUpdate
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @PatchMapping("/{organizationName}/{team}")
    public SuccessResponse<OrganizationResponseDto.OrganizationDto> updateOrganization(
            @PathVariable String organizationName,
            @PathVariable String team,
            @RequestBody @Valid OrganizationRequestDto.UpdateDto request
    ){
        return SuccessResponse.success(SuccessCode.ORGANIZATION_UPDATED, organizationService.updateOrganization(organizationName, team, request));
    }

    /**
     * 특정 Organization 조회
     */
    @Operation(summary = "특정 조직 조회", description = "특정 조직의 정보를 조회합니다.")
    @SuccessApiResponses.OrganizationGet
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @GetMapping("/{organizationName}/{team}")
    public SuccessResponse<OrganizationResponseDto.OrganizationDto> getOrganization(
            @PathVariable String organizationName,
            @PathVariable String team
    ){
        return SuccessResponse.success(SuccessCode.ORGANIZATION_FETCHED, organizationService.getOrganization(organizationName, team));
    }

    /**
     * 전체 Organization 조회
     */
    @Operation(summary = "회원의 전체 조직 조회", description = "회원의 모든 조직의 정보를 조회합니다.")
    @SuccessApiResponses.AllOrganizationGet
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @GetMapping
    public SuccessResponse<List<OrganizationResponseDto.OrganizationDto>> getAllOrganization(){
        List<OrganizationResponseDto.OrganizationDto> orgs = organizationService.getAllOrganizations();
        return SuccessResponse.success(SuccessCode.ORGANIZATION_FETCHED, orgs);
    }

    /**
     * Organization 삭제
     */
    @Operation(summary = "조직 삭제", description = "조직을 삭제합니다.")
    @SuccessApiResponses.OrganizationDelete
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @DeleteMapping("/{organizationName}/{team}")
    public SuccessResponse<Void> deleteOrganization(
            @PathVariable String organizationName,
            @PathVariable String team
    ){
        organizationService.deleteOrganization(organizationName, team);
        return SuccessResponse.success(SuccessCode.ORGANIZATION_DELETED, null);
    }
}
