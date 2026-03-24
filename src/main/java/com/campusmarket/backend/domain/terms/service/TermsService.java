package com.campusmarket.backend.domain.terms.service;

import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.terms.constant.TermCode;
import com.campusmarket.backend.domain.terms.constant.TermsErrorCode;
import com.campusmarket.backend.domain.terms.dto.request.TermAgreementItemReqDto;
import com.campusmarket.backend.domain.terms.dto.request.TermsAgreementCreateReqDto;
import com.campusmarket.backend.domain.terms.dto.response.*;
import com.campusmarket.backend.domain.terms.entity.MemberTermAgreement;
import com.campusmarket.backend.domain.terms.exception.TermsException;
import com.campusmarket.backend.domain.terms.repository.MemberTermAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

    private final MemberRepository memberRepository;
    private final MemberTermAgreementRepository memberTermAgreementRepository;

    private List<TermItemResDto> getFixedTerms() {
        return List.of(
                TermItemResDto.of(
                        TermCode.SERVICE_USE,
                        "이용약관 동의",
                        "캠퍼스 중고마켓 이벤트 참여를 위한 이용약관입니다.",
                        true
                ),
                TermItemResDto.of(
                        TermCode.PRIVACY_COLLECTION,
                        "개인정보 수집 및 이용 동의",
                        "이벤트 참여를 위한 개인정보 수집 및 이용 동의 약관입니다.",
                        true
                )
        );
    }

    public TermsListResDto getTerms(){
        return TermsListResDto.of(getFixedTerms());
    }

    @Transactional
    public TermsAgreementSaveResDto saveAgreements(String guestUuid, TermsAgreementCreateReqDto reqDto) {
        Member member = findMemberByGuestUuid(guestUuid);

        if (reqDto.agreements() == null || reqDto.agreements().isEmpty()) {
            throw new TermsException(TermsErrorCode.TERM_AGREEMENTS_EMPTY);
        }

        Map<TermCode, TermItemResDto> termMap = getFixedTerms().stream()
                .collect(Collectors.toMap(TermItemResDto::termCode, term -> term));

        for (TermAgreementItemReqDto item : reqDto.agreements()) {
            if (item.termCode() == null || !termMap.containsKey(item.termCode())) {
                throw new TermsException(TermsErrorCode.INVALID_TERM_CODE);
            }

            TermItemResDto fixedTerm = termMap.get(item.termCode());

            MemberTermAgreement agreement = memberTermAgreementRepository
                    .findByMemberIdAndTermCode(member.getId(), item.termCode())
                    .orElseGet(() -> MemberTermAgreement.builder()
                            .member(member)
                            .termCode(item.termCode())
                            .termTitle(fixedTerm.title())
                            .required(fixedTerm.required())
                            .agreed(Boolean.FALSE)
                            .build());

            agreement.updateAgreement(Boolean.TRUE.equals(item.agreed()));
            memberTermAgreementRepository.save(agreement);
        }

        boolean termsCompleted = isAllRequiredTermsAgreed(member.getId(), termMap);
        member.updateTermsCompleted(termsCompleted);

        return TermsAgreementSaveResDto.of(termsCompleted);
    }

    public MyTermsAgreementResDto getMyAgreements(String guestUuid) {
        Member member = findMemberByGuestUuid(guestUuid);

        Map<TermCode, MemberTermAgreement> savedAgreementMap = memberTermAgreementRepository.findByMemberId(member.getId())
                .stream()
                .collect(Collectors.toMap(MemberTermAgreement::getTermCode, agreement -> agreement));

        List<MyTermAgreementItemResDto> results = getFixedTerms().stream()
                .map(term -> {
                    MemberTermAgreement saved = savedAgreementMap.get(term.termCode());

                    return MyTermAgreementItemResDto.of(
                            term.termCode(),
                            term.title(),
                            term.required(),
                            saved != null && Boolean.TRUE.equals(saved.getAgreed()),
                            saved != null ? saved.getAgreedAt() : null
                    );
                })
                .toList();

        return MyTermsAgreementResDto.of(results);
    }

    private Member findMemberByGuestUuid(String guestUuid) {
        return memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private boolean isAllRequiredTermsAgreed(Long memberId, Map<TermCode, TermItemResDto> termMap) {
        for (TermItemResDto term : termMap.values()) {
            if (Boolean.TRUE.equals(term.required())) {
                MemberTermAgreement agreement = memberTermAgreementRepository
                        .findByMemberIdAndTermCode(memberId, term.termCode())
                        .orElse(null);

                if (agreement == null || !Boolean.TRUE.equals(agreement.getAgreed())) {
                    return false;
                }
            }
        }

        return true;
    }
}
