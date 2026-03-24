package com.campusmarket.backend.domain.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "대카테고리")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MajorCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "대카테고리PK")
    private Long id;

    @Column(name = "카테고리명", nullable = false, length = 50)
    private String name;

    @Column(name = "아이콘URL", length = 500)
    private String iconUrl;

    @Column(name = "정렬순서", nullable = false)
    private Integer sortOrder;

    @Column(name = "생성일", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "majorCategory")
    private List<SubCategory> subCategories = new ArrayList<>();

    @Builder
    public MajorCategory(
            String name,
            String iconUrl,
            Integer sortOrder,
            LocalDateTime createdAt
    ) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }
}
