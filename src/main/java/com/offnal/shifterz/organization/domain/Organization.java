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
@Table(
        name = "organization",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_org_member_name_team",
                        columnNames = {
                                "member_id", "organization_name", "team"
                        }
                )
        }
)
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "team", nullable = false)
    private String team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member organizationMember;

    public void update(OrganizationRequestDto.UpdateDto request) {
        if(request.getOrganizationName() != null) this.organizationName = request.getOrganizationName();
        if(request.getTeam() != null) this.team = request.getTeam();
    }

    public void assignOwner(Member member) {
        this.organizationMember = member;
    }
}

