package com.campusmarket.backend.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "상품")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "상품PK")
    private Long id;

    @Column(name = "판매자ID", nullable = false)
    private Long sellerId;

    @Column(name = "대카테고리ID", nullable = false)
    private Long majorCategoryId;

    @Column(name = "소카테고리ID", nullable = false)
    private Long subCategoryId;

    @Column(name = "상품명", length = 200)
    private String name;

    @Column(name = "브랜드", length = 200)
    private String brand;

    @Column(name = "색상", length = 50)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "상품상태", length = 20)
    private ProductCondition productCondition;

    @Column(name = "상품설명", columnDefinition = "TEXT")
    private String description;

    @Column(name = "가격")
    private Integer price;

    @Column(name = "무료나눔여부", nullable = false)
    private Boolean isFree;

    @Enumerated(EnumType.STRING)
    @Column(name = "판매상태", nullable = false, length = 20)
    private ProductSaleStatus saleStatus;

    @Column(name = "조회수", nullable = false)
    private Integer viewCount;

    @Column(name = "찜수", nullable = false)
    private Integer wishCount;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Column(name = "수정일")
    private LocalDateTime updatedAt;

    @Column(name = "판매완료일")
    private LocalDateTime soldAt;

    @Column(name = "삭제일")
    private LocalDateTime deletedAt;

    @Builder
    public Product(
            Long sellerId,
            Long majorCategoryId,
            Long subCategoryId,
            String name,
            String brand,
            String color,
            ProductCondition productCondition,
            String description,
            Integer price,
            Boolean isFree,
            ProductSaleStatus saleStatus,
            Integer viewCount,
            Integer wishCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime soldAt,
            LocalDateTime deletedAt
    ) {
        this.sellerId = sellerId;
        this.majorCategoryId = majorCategoryId;
        this.subCategoryId = subCategoryId;
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.productCondition = productCondition;
        this.description = description;
        this.price = price;
        this.isFree = isFree;
        this.saleStatus = saleStatus;
        this.viewCount = viewCount;
        this.wishCount = wishCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.soldAt = soldAt;
        this.deletedAt = deletedAt;
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void update(
            String name,
            String brand,
            String color,
            ProductCondition condition,
            String description,
            Integer price,
            Boolean isFree
    ) {
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.productCondition = condition;
        this.description = description;
        this.price = price;
        this.isFree = isFree;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void completeSale() {
        this.saleStatus = ProductSaleStatus.SOLD;
        this.soldAt = LocalDateTime.now();
    }
}