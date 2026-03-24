package com.campusmarket.backend.domain.terms.entity;

import com.campusmarket.backend.domain.terms.constant.TermCode;
import com.campusmarket.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "회원약관동의")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTermAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "회원약관동의PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "회원PK", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "약관코드", nullable = false, length = 50)
    private TermCode termCode;

    @Column(name = "약관제목", nullable = false, length = 100)
    private String termTitle;

    @Column(name = "필수여부", nullable = false)
    private Boolean required;

    @Column(name = "동의여부", nullable = false)
    private Boolean agreed;

    @Column(name = "동의일시")
    private LocalDateTime agreedAt;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Column(name = "수정일")
    private LocalDateTime updatedAt;

    @Builder
    private MemberTermAgreement(
            Member member,
            TermCode termCode,
            String termTitle,
            Boolean required,
            Boolean agreed
    ) {
        this.member = member;
        this.termCode = termCode;
        this.termTitle = termTitle;
        this.required = required;
        this.agreed = agreed;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (Boolean.TRUE.equals(this.agreed)) {
            this.agreedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAgreement(Boolean agreed) {
        this.agreed = agreed;

        if (Boolean.TRUE.equals(agreed)) {
            this.agreedAt = LocalDateTime.now();
        } else {
            this.agreedAt = null;
        }
    }
}
