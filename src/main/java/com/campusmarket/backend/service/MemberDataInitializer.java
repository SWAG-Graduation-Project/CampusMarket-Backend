package com.campusmarket.backend.service;

import com.campusmarket.backend.model.Member;
import com.campusmarket.backend.model.MemberRole;
import com.campusmarket.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MemberDataInitializer implements CommandLineRunner {
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        if (memberRepository.count() == 0) {
            memberRepository.save(Member.builder()
                    .nickname("guest_mj")
                    .role(MemberRole.GUEST)
                    .createdAt(LocalDateTime.now())
                    .build());

            memberRepository.save(Member.builder()
                    .nickname("guest_jh")
                    .role(MemberRole.GUEST)
                    .createdAt(LocalDateTime.now())
                    .build());

            memberRepository.save(Member.builder()
                    .nickname("guest_sb")
                    .role(MemberRole.GUEST)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }
}