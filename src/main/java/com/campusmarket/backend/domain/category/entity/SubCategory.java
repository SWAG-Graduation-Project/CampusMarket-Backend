package com.campusmarket.backend.domain.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "소카테고리")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "소카테고리PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "대카테고리ID", nullable = false)
    private MajorCategory majorCategory;

    @Column(name = "카테고리명", nullable = false, length = 50)
    private String name;

    @Column(name = "아이콘URL", length = 500)
    private String iconUrl;

    @Column(name = "정렬순서", nullable = false)
    private Integer sortOrder;

    @Column(name = "생성일", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public SubCategory(
            MajorCategory majorCategory,
            String name,
            String iconUrl,
            Integer sortOrder,
            LocalDateTime createdAt
    ) {
        this.majorCategory = majorCategory;
        this.name = name;
        this.iconUrl = iconUrl;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }
}