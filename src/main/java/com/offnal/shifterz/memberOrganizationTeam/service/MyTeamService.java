package com.offnal.shifterz.memberOrganizationTeam.service;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memberOrganizationTeam.converter.MyTeamConverter;
import com.offnal.shifterz.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.memberOrganizationTeam.repository.MemberOrganizationTeamRepository;
import com.offnal.shifterz.organization.domain.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyTeamService {

    private final MemberOrganizationTeamRepository memberOrganizationTeamRepository;

    @Transactional
    public void saveOrUpdateMyTeam(Member member, Organization organization, String myTeam) {

        if (myTeam == null || myTeam.isBlank()) {
            return;
        }

        memberOrganizationTeamRepository.findByMemberAndOrganization(member, organization)
                .ifPresentOrElse(
                        mot -> updateTeam(mot, myTeam),
                        () -> createNewTeam(member, organization, myTeam)
                );
    }

    private void updateTeam(MemberOrganizationTeam mot, String myTeam) {
        mot.updateTeam(myTeam);
    }

    private void createNewTeam(Member member, Organization organization, String myTeam) {
        MemberOrganizationTeam newEntity =
                MyTeamConverter.toEntity(member, organization, myTeam);
        memberOrganizationTeamRepository.save(newEntity);
    }

    @Transactional(readOnly = true)
    public String getMyTeamForOrganization(Member member, String organizationName) {

        return memberOrganizationTeamRepository
                .findByMemberAndOrganizationName(member.getId(), organizationName)
                .map(MemberOrganizationTeam::getTeam)
                .orElse(null);
    }

}
