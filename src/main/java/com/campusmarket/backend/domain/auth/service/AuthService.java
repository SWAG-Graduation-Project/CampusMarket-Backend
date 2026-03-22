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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final AuthMapper authMapper;

    @Transactional
    // 게스트 회원을 생성하거나, 이미 있으면 기존 회원을 반환
    public GuestCreateRespDto createGuestMember(GuestCreateReqDto reqDto){
        String guestUuid = reqDto.guestUuid();

        validateGuestUuid(guestUuid);

        Optional<Member> existingMember = memberRepository.findByGuestUuid(guestUuid);

        if (existingMember.isPresent()){
            return authMapper.toGuestCreateRespDto(existingMember.get(), false);
        }

        Member member = Member.builder()
                .guestUuid(guestUuid)
                .loginType(LoginType.GUEST)
                .status(MemberStatus.ACTIVE)
                .build();

        Member savedMember = memberRepository.save(member);

        return authMapper.toGuestCreateRespDto(savedMember, true);
    }

    public CurrentMemberInfoRespDto getCurrentMemberInfo(String guestUuid){
        validateGuestUuid(guestUuid);

        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(()-> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return authMapper.toCurrentMemberInfoRespDto(member);
    }

    private void validateGuestUuid(String guestUuid) {
        if (guestUuid == null || guestUuid.isBlank()) {
            throw new MemberException(MemberErrorCode.INVALID_GUEST_UUID);
        }
    }
}
