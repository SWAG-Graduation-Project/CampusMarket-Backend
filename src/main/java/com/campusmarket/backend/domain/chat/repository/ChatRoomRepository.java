package com.campusmarket.backend.domain.chat.repository;

import com.campusmarket.backend.domain.chat.constant.ChatRoomStatus;
import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByProductIdAndBuyerId(Long productId, Long buyerId);

    List<ChatRoom> findAllByProductId(Long productId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.sellerId = :memberId OR cr.buyerId = :memberId")
    List<ChatRoom> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT cr FROM ChatRoom cr
            WHERE cr.sellerId = :sellerId
              AND cr.status <> :status
            ORDER BY
                CASE WHEN cr.lastMessageAt IS NULL THEN 0 ELSE 1 END DESC,
                cr.lastMessageAt DESC
            """)
    List<ChatRoom> findAllBySellerIdAndStatusNot(
            @Param("sellerId") Long sellerId,
            @Param("status") ChatRoomStatus status
    );

    @Query("""
            SELECT cr FROM ChatRoom cr
            WHERE cr.buyerId = :buyerId
              AND cr.status <> :status
            ORDER BY
                CASE WHEN cr.lastMessageAt IS NULL THEN 0 ELSE 1 END DESC,
                cr.lastMessageAt DESC
            """)
    List<ChatRoom> findAllByBuyerIdAndStatusNot(
            @Param("buyerId") Long buyerId,
            @Param("status") ChatRoomStatus status
    );
}
