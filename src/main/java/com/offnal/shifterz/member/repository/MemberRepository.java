package com.offnal.shifterz.member.repository;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}
