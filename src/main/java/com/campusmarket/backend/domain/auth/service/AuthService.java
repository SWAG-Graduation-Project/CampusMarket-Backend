package com.campusmarket.backend.domain.auth.service;

import com.campusmarket.backend.domain.auth.dto.CurrentMemberInfoRespDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateReqDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateRespDto;
import com.campusmarket.backend.domain.auth.mapper.AuthMapper;
import com.campusmarket.backend.domain.member.constant.LoginType;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.constant.MemberStatus;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final AuthMapper authMapper;

    @Transactional
    public GuestCreateRespDto createGuestMember(GuestCreateReqDto reqDto) {
        String guestUuid = normalizeGuestUuid(reqDto.guestUuid());

        validateGuestUuid(guestUuid);

        try {
            Optional<Member> existingMember = memberRepository.findByGuestUuid(guestUuid);

            if (existingMember.isPresent()) {
                Member existing = existingMember.get();
                if (existing.isWithdrawn()) {
                    throw new MemberException(MemberErrorCode.MEMBER_WITHDRAWN);
                }
                return authMapper.toGuestCreateRespDto(existing, false);
            }

            Member member = Member.builder()
                    .guestUuid(guestUuid)
                    .loginType(LoginType.GUEST)
                    .status(MemberStatus.ACTIVE)
                    .build();

            Member savedMember = memberRepository.save(member);

            return authMapper.toGuestCreateRespDto(savedMember, true);

        } catch (DataIntegrityViolationException e) {
            Member member = memberRepository.findByGuestUuid(guestUuid)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            return authMapper.toGuestCreateRespDto(member, false);
        }
    }

    public CurrentMemberInfoRespDto getCurrentMemberInfo(String guestUuid){
        guestUuid = normalizeGuestUuid(guestUuid);

        validateGuestUuid(guestUuid);

        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.isWithdrawn()) {
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAWN);
        }

        return authMapper.toCurrentMemberInfoRespDto(member);
    }

    private void validateGuestUuid(String guestUuid) {
        if (guestUuid == null || guestUuid.isBlank()) {
            throw new MemberException(MemberErrorCode.INVALID_GUEST_UUID);
        }

        try {
            UUID.fromString(guestUuid);
        } catch (IllegalArgumentException exception) {
            throw new MemberException(MemberErrorCode.INVALID_GUEST_UUID_FORMAT);
        }
    }

    private String normalizeGuestUuid(String guestUuid) {
        if (guestUuid == null) {
            return null;
        }

        return guestUuid.trim().toLowerCase();
    }
}
