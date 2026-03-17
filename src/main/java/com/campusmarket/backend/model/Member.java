package com.campusmarket.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Member(String nickname, MemberRole role, LocalDateTime createdAt){
        this.nickname = nickname;
        this.role = role;
        this.createdAt = createdAt;
    }
}
