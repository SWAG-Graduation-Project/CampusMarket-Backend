package com.campusmarket.backend.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "멤버프로필")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile {

    @Id
    @Column(name = "회원ID")
    private Long memberId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "회원ID", nullable = false, unique = true)
    private Member member;

    @Column(name = "닉네임", unique = true)
    private String nickname;

    @Column(name = "프로필이미지URL", length = 500)
    private String profileImageUrl;

    @Column(name = "상점시작일")
    private LocalDateTime storeStartedAt;

    @Column(name = "판매거래횟수", nullable = false)
    private Integer saleCount = 0;

    @Column(name = "구매거래횟수", nullable = false)
    private Integer purchaseCount = 0;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Column(name = "수정일")
    private LocalDateTime updatedAt;
}
