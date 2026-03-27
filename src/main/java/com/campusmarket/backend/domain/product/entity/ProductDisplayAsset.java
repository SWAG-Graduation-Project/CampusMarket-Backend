package com.campusmarket.backend.domain.product.entity;

import com.campusmarket.backend.domain.category.entity.MajorCategory;
import com.campusmarket.backend.domain.category.entity.SubCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "상품대표에셋")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDisplayAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "상품대표에셋PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "대카테고리ID", nullable = false)
    private MajorCategory majorCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "소카테고리ID")
    private SubCategory subCategory;

    @Column(name = "색상", length = 30)
    private String color;

    @Column(name = "키워드", length = 50)
    private String keyword;

    @Column(name = "대표이미지URL", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "썸네일이미지URL", length = 500)
    private String thumbnailUrl;

    @Column(name = "활성여부", nullable = false)
    private Boolean active;

    @Column(name = "정렬순서", nullable = false)
    private Integer sortOrder;

    @Column(name = "생성일", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "수정일", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ProductDisplayAsset(
            MajorCategory majorCategory,
            SubCategory subCategory,
            String color,
            String keyword,
            String imageUrl,
            String thumbnailUrl,
            Boolean active,
            Integer sortOrder
    ) {
        this.majorCategory = majorCategory;
        this.subCategory = subCategory;
        this.color = color;
        this.keyword = keyword;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.active = active;
        this.sortOrder = sortOrder;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.active == null) {
            this.active = true;
        }
        if (this.sortOrder == null) {
            this.sortOrder = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}