package com.campusmarket.backend.domain.member.repository;

import com.campusmarket.backend.domain.member.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    boolean existsByNickname(String nickname);
    boolean existsByNicknameIgnoreCase(String nickname);
}
