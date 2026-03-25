package com.campusmarket.backend.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "찜테이블")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "찜PK")
    private Long id;

    @Column(name = "상품ID", nullable = false)
    private Long productId;

    @Column(name = "찜한회원ID", nullable = false)
    private Long wishedMemberId;

    @Column(name = "생성일")
    private LocalDateTime createdAt;
}