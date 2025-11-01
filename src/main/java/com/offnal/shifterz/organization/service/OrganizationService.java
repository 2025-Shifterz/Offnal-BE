package com.offnal.shifterz.organization.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.converter.OrganizationConverter;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.dto.OrganizationRequestDto;
import com.offnal.shifterz.organization.dto.OrganizationResponseDto;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    // 조직 생성
    @Transactional
    public OrganizationResponseDto.OrganizationDto createOrganization(OrganizationRequestDto.CreateDto request) {
        Long memberId = AuthService.getCurrentUserId();

        String name = request.getOrganizationName().trim();
        String team = request.getTeam().trim();


        if (organizationRepository.existsByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, name, team)){
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_DUPLICATE_NAME);
        }

        Organization org = OrganizationConverter.toEntity(request, memberId);

        return OrganizationConverter.toDto(organizationRepository.save(org));
    }

    // 조직 하나 조회
    @Transactional(readOnly = true)
    public OrganizationResponseDto.OrganizationDto getOrganization(String organizationName, String team) {
        Long memberId = AuthService.getCurrentUserId();
        Organization org = findOwnedOrganizationOrThrow(memberId, organizationName, team);
        return OrganizationConverter.toDto(org);
    }

    // 회원의 조직 전체 조회
    @Transactional(readOnly = true)
    public List<OrganizationResponseDto.OrganizationDto> getAllOrganizations() {
        Long memberId = AuthService.getCurrentUserId();

        List<Organization> organizations = organizationRepository.findAllByOrganizationMember_Id(memberId);

        return organizations.stream()
                .map(OrganizationConverter::toDto)
                .toList();
    }

    // 없으면 조직 생성, 있으면 조직 조회
    @Transactional
    public Organization getOrCreateByMemberAndNameAndTeam(String organizationName, String team) {
        Long memberId = AuthService.getCurrentUserId();

        return organizationRepository.findByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, organizationName, team)
                .orElseGet(() -> {
                    OrganizationRequestDto.CreateDto request = OrganizationRequestDto.CreateDto.builder()
                            .organizationName(organizationName)
                            .team(team)
                            .build();
                    Organization newOrg = OrganizationConverter.toEntity(request, memberId);
                    try {
                        return organizationRepository.save(newOrg);
                    } catch (DataIntegrityViolationException e) {
                        return organizationRepository.findByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, organizationName, team)
                                .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_SAVE_FAILED));
                    }
                });
    }


    @Transactional
    public OrganizationResponseDto.OrganizationDto updateOrganization(String organizationName, String team, OrganizationRequestDto.UpdateDto request) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOwnedOrganizationOrThrow(memberId, organizationName, team);

        String newName = request.getOrganizationName() == null ? org.getOrganizationName() : request.getOrganizationName().trim();
        String newTeam = request.getTeam() == null ? org.getTeam() : request.getTeam().trim();

        boolean renameOrReteam = !org.getOrganizationName().equals(newName) || !org.getTeam().equals(newTeam);
        if (renameOrReteam) {
            boolean exists = organizationRepository
                    .existsByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, newName, newTeam);
            if (exists) {
                throw new CustomException(OrganizationErrorCode.ORGANIZATION_DUPLICATE_NAME);
            }
        }

        org.update(request);
        return OrganizationConverter.toDto(org);
    }

    @Transactional
    public void deleteOrganization(String organizationName, String team) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOwnedOrganizationOrThrow(memberId, organizationName, team);

        if (!org.getOrganizationMember().getId().equals(memberId)) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_ACCESS_DENIED);
        }

        organizationRepository.delete(org);
    }

    private Organization findOwnedOrganizationOrThrow(Long memberId, String organizationName, String team) {
        String name = organizationName.trim();
        String t = team.trim();

        Organization org = organizationRepository
                .findByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, name, t)
                .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND));

        if (!org.getOrganizationMember().getId().equals(memberId)) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_ACCESS_DENIED);
        }
        return org;
    }


    @Getter
    @AllArgsConstructor
    public enum OrganizationErrorCode implements ErrorReason {
        ORGANIZATION_NOT_FOUND("ORG001", HttpStatus.NOT_FOUND, "소속 조직을 찾을 수 없습니다."),
        ORGANIZATION_SAVE_FAILED("ORG002", HttpStatus.INTERNAL_SERVER_ERROR, "조직 저장에 실패했습니다."),
        ORGANIZATION_ACCESS_DENIED("ORG003", HttpStatus.FORBIDDEN, "해당 조직에 접근 권한이 없습니다."),
        ORGANIZATION_DUPLICATE_NAME("ORG004", HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 조직입니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
