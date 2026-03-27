package com.campusmarket.backend.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "차단대상관리",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_차단_회원_대상",
                columnNames = {"회원ID", "차단대상ID"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "블랙리스트PK")
    private Long id;

    // 차단한 회원
    @Column(name = "회원ID", nullable = false)
    private Long memberId;

    // 차단당한 회원
    @Column(name = "차단대상ID", nullable = false)
    private Long blockedId;

    @Column(name = "생성일", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private Block(Long memberId, Long blockedId) {
        this.memberId = memberId;
        this.blockedId = blockedId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
