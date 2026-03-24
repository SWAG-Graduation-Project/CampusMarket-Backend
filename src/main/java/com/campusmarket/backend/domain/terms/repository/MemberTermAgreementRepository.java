package com.campusmarket.backend.domain.terms.repository;

import com.campusmarket.backend.domain.terms.constant.TermCode;
import com.campusmarket.backend.domain.terms.entity.MemberTermAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberTermAgreementRepository extends JpaRepository<MemberTermAgreement, Long> {

    List<MemberTermAgreement> findByMemberId(Long memberId);

    Optional<MemberTermAgreement> findByMemberIdAndTermCode(Long memberId, TermCode termCode);

}
