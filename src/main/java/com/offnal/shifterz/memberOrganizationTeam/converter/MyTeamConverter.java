package com.offnal.shifterz.memberOrganizationTeam.converter;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.organization.domain.Organization;

public class MyTeamConverter {

    public static MemberOrganizationTeam toEntity(Member member, Organization org, String myTeam) {
        return MemberOrganizationTeam.builder()
                .member(member)
                .organization(org)
                .team(myTeam)
                .build();
    }
}

