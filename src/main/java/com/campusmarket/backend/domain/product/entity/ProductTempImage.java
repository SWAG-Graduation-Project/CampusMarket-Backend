package com.campusmarket.backend.domain.product.entity;

import com.campusmarket.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "상품임시이미지")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductTempImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "상품임시이미지PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "회원ID", nullable = false)
    private Member member;

    @Column(name = "원본이미지URL", nullable = false, length = 500)
    private String originalImageUrl;

    @Column(name = "배경제거이미지URL", length = 500)
    private String backgroundRemovedImageUrl;

    @Column(name = "배경제거여부", nullable = false)
    private Boolean backgroundRemoved;

    @Column(name = "표시순서", nullable = false)
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "상태", nullable = false, length = 30)
    private ProductTempImageStatus status;

    @Column(name = "생성일", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "수정일", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "삭제일")
    private LocalDateTime deletedAt;

    @Builder
    private ProductTempImage(
            Member member,
            String originalImageUrl,
            String backgroundRemovedImageUrl,
            Boolean backgroundRemoved,
            Integer displayOrder,
            ProductTempImageStatus status
    ) {
        this.member = member;
        this.originalImageUrl = originalImageUrl;
        this.backgroundRemovedImageUrl = backgroundRemovedImageUrl;
        this.backgroundRemoved = backgroundRemoved;
        this.displayOrder = displayOrder;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.backgroundRemoved == null) {
            this.backgroundRemoved = false;
        }

        if (this.status == null) {
            this.status = ProductTempImageStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBackgroundRemovedImage(String backgroundRemovedImageUrl) {
        this.backgroundRemovedImageUrl = backgroundRemovedImageUrl;
        this.backgroundRemoved = true;
    }

    public void replaceOriginalImage(String newOriginalImageUrl) {
        this.originalImageUrl = newOriginalImageUrl;
        this.backgroundRemovedImageUrl = null;
        this.backgroundRemoved = false;
    }

    public void clearBackgroundRemovedImage() {
        this.backgroundRemovedImageUrl = null;
        this.backgroundRemoved = false;
    }

    public void softDelete() {
        this.status = ProductTempImageStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}