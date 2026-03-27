package com.campusmarket.backend.domain.chat.repository;

import com.campusmarket.backend.domain.chat.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByMemberIdAndBlockedId(Long memberId, Long blockedId);

    // 양방향 차단 여부 확인 (A→B 또는 B→A)
    default boolean isBlocked(Long memberId, Long targetId) {
        return existsByMemberIdAndBlockedId(memberId, targetId)
                || existsByMemberIdAndBlockedId(targetId, memberId);
    }

    Optional<Block> findByMemberIdAndBlockedId(Long memberId, Long blockedId);
}
