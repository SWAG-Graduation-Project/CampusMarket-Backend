package com.campusmarket.backend.domain.member.entity;

import com.campusmarket.backend.domain.member.constant.RandomNicknameWordType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "랜덤닉네임생성")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RandomNickname {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "닉네임아이디")
    private Long id;

    @Column(name = "단어", nullable = false, length = 20, unique = true)
    private String word;

    @Enumerated(EnumType.STRING)
    @Column(name = "단어유형", nullable = false, length = 50)
    private RandomNicknameWordType wordType;

    @Column(name = "사용여부", nullable = false)
    private Boolean active;

    @Column(name = "생성일")
    private LocalDateTime createdAt;
}
