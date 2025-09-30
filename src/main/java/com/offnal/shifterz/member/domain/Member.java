package com.offnal.shifterz.member.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;
    private String email;
    private String memberName;
    private String phoneNumber;

    private String profileImageUrl;

    public void updateMemberInfo(String email, String memberName, String phoneNumber, String profileImageUrl) {
        this.email = email;
        this.memberName = memberName;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;

    }


}
