package com.campusmarket.backend.domain.chat.repository;

import com.campusmarket.backend.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            SELECT cm FROM ChatMessage cm
            WHERE cm.chatRoom.id = :chatRoomId
              AND cm.deletedAt IS NULL
            ORDER BY cm.createdAt ASC
            """)
    List<ChatMessage> findByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    @Query("""
            SELECT COUNT(cm) FROM ChatMessage cm
            WHERE cm.chatRoom.id = :chatRoomId
              AND cm.deletedAt IS NULL
            """)
    long countByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.id IN :ids")
    List<ChatMessage> findAllByIdIn(@Param("ids") List<Long> ids);
}
