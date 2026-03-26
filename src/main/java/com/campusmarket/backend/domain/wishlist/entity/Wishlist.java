package com.campusmarket.backend.domain.wishlist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "찜테이블")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "찜PK")
    private Long id;

    @Column(name = "상품ID", nullable = false)
    private Long productId;

    @Column(name = "찜한회원ID", nullable = false)
    private Long memberId;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Builder
    public Wishlist(
            Long productId,
            Long memberId,
            LocalDateTime createdAt
    ) {
        this.productId = productId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }
}