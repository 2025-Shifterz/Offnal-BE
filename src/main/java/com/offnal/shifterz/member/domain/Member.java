package com.offnal.shifterz.member.domain;

import com.offnal.shifterz.work.domain.WorkCalendar;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "members")
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", unique = true)
    private Long kakaoId;

    @Column(name = "kakao_nickname")
    private String kakaoNickname;

    @Column(name = "kakao_email")
    private String email;

    @Column(name = "kakao_profile_image_url")
    private String profileImageUrl;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "workCalendar_id")
    private List<WorkCalendar> workCalendars;
}
