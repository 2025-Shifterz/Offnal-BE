package com.offnal.shifterz.organization.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memo.repository.MemoRepository;
import com.offnal.shifterz.organization.converter.OrganizationConverter;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.dto.OrganizationRequestDto;
import com.offnal.shifterz.organization.dto.OrganizationResponseDto;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import com.offnal.shifterz.todo.repository.TodoRepository;
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
    private final MemoRepository memoRepository;
    private final TodoRepository todoRepository;
    private static final String ORG_CONSTRAINT_NAME = "uk_org_member_name_team";


    // 조직 생성
    @Transactional
    public OrganizationResponseDto.OrganizationDto createOrganization(OrganizationRequestDto.CreateDto request) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = AuthService.getCurrentMember();

        String name = normalize(request.getOrganizationName());
        String team = normalize(request.getTeam());

        validateRequiredFields(name, team);

        assertNoDuplicate(memberId, name, team);

        Organization org = OrganizationConverter.toEntity(request, member);
        org.rename(name, team);

        try {
            Organization saved = organizationRepository.save(org);
            return OrganizationConverter.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_DUPLICATE_NAME);
        }
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

        validateRequiredFields(name, team);

        return findExisting(memberId, name, team)
                .orElseGet(() -> createOrFindOnUniqueConflict(name, team));
    }


    // 조직 수정
    @Transactional
    public OrganizationResponseDto.OrganizationDto updateOrganization(String organizationName, String team, OrganizationRequestDto.UpdateDto request) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOwnedOrganizationOrThrow(memberId, organizationName, team);

        String newName = normalize(defaultIfNull(request.getOrganizationName(), org.name()));
        String newTeam = normalize(defaultIfNull(request.getTeam(), org.team()));

        validateRequiredFields(newName, newTeam);

        if (org.isNamed(newName, newTeam)) {
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

        memoRepository.deleteAllByOrganization(org);
        todoRepository.deleteAllByOrganization(org);

        organizationRepository.delete(org);
    }

    private Organization findOwnedOrganizationOrThrow(Long memberId, String organizationName, String organizationTeam) {
        String name = normalize(organizationName);
        String team = normalize(organizationTeam);

        validateRequiredFields(name, team);

        Organization org = organizationRepository
                .findByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, name, team)
                .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND));

        return org;
    }

    private Optional<Organization> findExisting(Long memberId, String organizationName, String team) {
        return organizationRepository.findByOrganizationMember_IdAndOrganizationNameAndTeam(
                memberId, organizationName, team);
    }

    private Organization createOrFindOnUniqueConflict(String organizationName, String team){
        Member member = AuthService.getCurrentMember();

        NormalizedOrg org = normalizeAndValidate(organizationName, team);
        Organization entity = buildOrganizationEntity(member, org);

        return saveOrFindExisting(entity, org.organizationName(), org.team());
    }

    private record NormalizedOrg(String organizationName, String team) {}

    private NormalizedOrg normalizeAndValidate(String organizationName, String team){
        String name = normalize(organizationName);
        String teamName = normalize(team);
        validateRequiredFields(name, teamName);

        return new NormalizedOrg(name, teamName);
    }

    private Organization buildOrganizationEntity(Member member, NormalizedOrg org) {
        OrganizationRequestDto.CreateDto request = OrganizationConverter.toCreateDto(org.organizationName(), org.team());
        return OrganizationConverter.toEntity(request, member);
    }

    private Organization saveOrFindExisting(Organization entity, String organizationName, String team) {
        try {
            return organizationRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            return handleUniqueConflict(e, organizationName, team);
        }
    }

    private Organization handleUniqueConflict(DataIntegrityViolationException e, String organizationName, String team) {
        Long memberId = AuthService.getCurrentUserId();

        if (isUniqueViolation(e)) {
            return findExisting(memberId, organizationName, team)
                    .orElseThrow(() -> new CustomException(OrganizationErrorCode.ORGANIZATION_SAVE_FAILED));
        }

        throw new CustomException(OrganizationErrorCode.ORGANIZATION_SAVE_FAILED);
    }

    private boolean isUniqueViolation(DataIntegrityViolationException e) {
        Throwable cause = e.getCause();
        return cause != null
                && cause.getMessage() != null
                && cause.getMessage().contains(ORG_CONSTRAINT_NAME);
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

    private void validateRequiredFields(String organizationName, String team){
        if (organizationName == null || organizationName.isEmpty() || team == null || team.isEmpty()) {
            throw new CustomException(OrganizationErrorCode.ORGANIZATION_NOT_VALIDATE);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum OrganizationErrorCode implements ErrorReason {
        ORGANIZATION_NOT_FOUND("ORG001", HttpStatus.NOT_FOUND, "소속 조직을 찾을 수 없습니다."),
        ORGANIZATION_SAVE_FAILED("ORG002", HttpStatus.INTERNAL_SERVER_ERROR, "조직 저장에 실패했습니다."),
        ORGANIZATION_ACCESS_DENIED("ORG003", HttpStatus.FORBIDDEN, "해당 조직에 접근 권한이 없습니다."),
        ORGANIZATION_DUPLICATE_NAME("ORG004", HttpStatus.CONFLICT, "동일한 이름/팀의 조직이 이미 존재합니다."),
        ORGANIZATION_NOT_VALIDATE("ORG005", HttpStatus.BAD_REQUEST, "유효하지 않은 필드입니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
