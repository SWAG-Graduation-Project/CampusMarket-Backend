package com.campusmarket.backend.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "상품이미지")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "상품이미지PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "상품ID", nullable = false)
    private Product product;

    @Column(name = "이미지URL", length = 500)
    private String imageUrl;

    @Column(name = "원본이미지URL", length = 500)
    private String originalImageUrl;

    @Column(name = "배경제거여부", nullable = false)
    private Boolean backgroundRemoved;

    @Column(name = "표시순서")
    private Integer displayOrder;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Builder
    public ProductImage(
            Product product,
            String imageUrl,
            String originalImageUrl,
            Boolean backgroundRemoved,
            Integer displayOrder,
            LocalDateTime createdAt
    ) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.originalImageUrl = originalImageUrl;
        this.backgroundRemoved = backgroundRemoved;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }
}