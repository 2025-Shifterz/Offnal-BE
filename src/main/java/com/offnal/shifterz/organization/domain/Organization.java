package com.offnal.shifterz.organization.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.dto.OrganizationRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organizationName;
    private String team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member organizationMember;

    public void update(OrganizationRequestDto.UpdateDto request) {
        if(request.getOrganizationName() != null) this.organizationName = request.getOrganizationName();
        if(request.getTeam() != null) this.team = request.getTeam();
    }
}

