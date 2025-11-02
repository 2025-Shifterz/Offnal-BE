package com.offnal.shifterz.organization.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.dto.OrganizationRequestDto;
import com.offnal.shifterz.work.domain.WorkCalendar;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "organization",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Builder.Default
    private List<WorkCalendar> calendars = new ArrayList<>();

    public void rename(String name, String team) {
        this.organizationName = name;
        this.team = team;
    }

}

