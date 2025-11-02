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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    // 조직 생성
    @Transactional
    public OrganizationResponseDto.OrganizationDto createOrganization(OrganizationRequestDto.CreateDto request) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = AuthService.getCurrentMember();

        String name = normalize(request.getOrganizationName());
        String team = normalize(request.getTeam());

        assertNoDuplicate(memberId, name, team);

        Organization org = OrganizationConverter.toEntity(request, member);
        org.rename(name, team);

        Organization saved = organizationRepository.save(org);

        return OrganizationConverter.toDto(saved);
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
    public Organization getOrCreateByMemberAndNameAndTeam(String organizationName, String organizationTeam) {
        Long memberId = AuthService.getCurrentUserId();
        String name = normalize(organizationName);
        String team = normalize(organizationTeam);

        return findExisting(memberId, name, team)
                .orElseGet(() -> createOrFindOnUniqueConflict(memberId, name, team));
    }


    // 조직 수정
    @Transactional
    public OrganizationResponseDto.OrganizationDto updateOrganization(String organizationName, String team, OrganizationRequestDto.UpdateDto request) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOwnedOrganizationOrThrow(memberId, organizationName, team);

        String newName = normalize(defaultIfNull(request.getOrganizationName(), org.getOrganizationName()));
        String newTeam = normalize(defaultIfNull(request.getTeam(), org.getTeam()));

        if (noChange(org, newName, newTeam)) {
            return OrganizationConverter.toDto(org);
        }

        assertNoDuplicate(memberId, newName, newTeam);

        org.rename(newName, newTeam);

        try {
            Organization saved = organizationRepository.save(org);
            return OrganizationConverter.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_DUPLICATE_NAME);
        }
    }

    // 조직 삭제
    @Transactional
    public void deleteOrganization(String organizationName, String team) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOwnedOrganizationOrThrow(memberId, organizationName, team);

        organizationRepository.delete(org);
    }

    private Organization findOwnedOrganizationOrThrow(Long memberId, String organizationName, String organizationTeam) {
        String name = organizationName.trim();
        String team = organizationTeam.trim();

        Organization org = organizationRepository
                .findByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, name, team)
                .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND));

        if (!org.getOrganizationMember().getId().equals(memberId)) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_ACCESS_DENIED);
        }
        return org;
    }

    private Optional<Organization> findExisting(Long memberId, String organizationName, String team) {
        return organizationRepository.findByOrganizationMember_IdAndOrganizationNameAndTeam(
                memberId, organizationName, team);
    }

    private Organization createOrFindOnUniqueConflict(Long memberId, String organizationName, String team){
        OrganizationRequestDto.CreateDto request = buildCreateDto(organizationName, team);
        Member member = AuthService.getCurrentMember();

        Organization entity = OrganizationConverter.toEntity(request, member);

        try {
            return organizationRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            return findExisting(memberId, organizationName, team)
                    .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_SAVE_FAILED));
        }
    }

    private OrganizationRequestDto.CreateDto buildCreateDto(String organizationName, String team) {
        return OrganizationRequestDto.CreateDto.builder()
                .organizationName(organizationName)
                .team(team)
                .build();
    }

    private boolean noChange(Organization org, String newName, String newTeam) {
        return org.getOrganizationName().equals(newName) && org.getTeam().equals(newTeam);
    }

    // 공백 방지
    private String normalize(String s) {
        return s == null ? null : s.trim();
    }

    private String defaultIfNull(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private void assertNoDuplicate(Long memberId, String name, String team) {
        if (organizationRepository.existsByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, name, team)) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_DUPLICATE_NAME);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum OrganizationErrorCode implements ErrorReason {
        ORGANIZATION_NOT_FOUND("ORG001", HttpStatus.NOT_FOUND, "소속 조직을 찾을 수 없습니다."),
        ORGANIZATION_SAVE_FAILED("ORG002", HttpStatus.INTERNAL_SERVER_ERROR, "조직 저장에 실패했습니다."),
        ORGANIZATION_ACCESS_DENIED("ORG003", HttpStatus.FORBIDDEN, "해당 조직에 접근 권한이 없습니다."),
        ORGANIZATION_DUPLICATE_NAME("ORG004", HttpStatus.CONFLICT, "동일한 이름/팀의 조직이 이미 존재합니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
