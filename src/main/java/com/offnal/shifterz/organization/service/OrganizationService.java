package com.offnal.shifterz.organization.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.organization.converter.OrganizationConverter;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.dto.OrganizationRequestDto;
import com.offnal.shifterz.organization.dto.OrganizationResponseDto;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public OrganizationResponseDto.OrganizationDto createOrganization(OrganizationRequestDto.CreateDto request) {
        Member member = AuthService.getCurrentMember();

        Organization org = OrganizationConverter.toEntity(request, member);
        return OrganizationConverter.toDto(organizationRepository.save(org));
    }

    @Transactional(readOnly = true)
    public OrganizationResponseDto.OrganizationDto getOrganization(Long id) {
        Member member = AuthService.getCurrentMember();

        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND));

        if (!org.getOrganizationMember().getId().equals(member.getId())) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_ACCESS_DENIED);
        }

        return OrganizationConverter.toDto(org);
    }

    @Transactional
    public OrganizationResponseDto.OrganizationDto updateOrganization(Long id, OrganizationRequestDto.UpdateDto request) {
        Member member = AuthService.getCurrentMember();

        Organization org = organizationRepository.findById(id)
                        .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND));

        if (!org.getOrganizationMember().getId().equals(member.getId())) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_ACCESS_DENIED);
        }

        org.update(request);
        return OrganizationConverter.toDto(org);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        Member member = AuthService.getCurrentMember();

        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND));

        if (!org.getOrganizationMember().getId().equals(member.getId())) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_ACCESS_DENIED);
        }

        organizationRepository.deleteById(id);
    }


    @Getter
    @AllArgsConstructor
    public enum OrganizationErrorCode implements ErrorReason {
        ORGANIZATION_NOT_FOUND("ORG001", HttpStatus.NOT_FOUND, "소속 조직을 찾을 수 없습니다."),
        ORGANIZATION_SAVE_FAILED("ORG002", HttpStatus.INTERNAL_SERVER_ERROR, "조직 저장에 실패했습니다."),
        ORGANIZATION_ACCESS_DENIED("ORG003", HttpStatus.FORBIDDEN, "해당 조직에 접근 권한이 없습니다.");
        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
