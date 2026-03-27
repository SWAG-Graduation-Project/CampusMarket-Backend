package com.campusmarket.backend.domain.member.service;

import com.campusmarket.backend.domain.member.client.TimetableAiClient;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.dto.response.TimetableParseResDto;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimetableAiService {

    private final MemberRepository memberRepository;
    private final TimetableAiClient timetableAiClient;
    private final ObjectMapper objectMapper;

    // 시간표 이미지 → AI 파싱 → timetableData 저장 (이미지 저장 X)
    // AI 호출은 트랜잭션 밖에서 수행, DB 저장만 짧은 쓰기 트랜잭션으로 처리
    public TimetableParseResDto parseTimetableImage(String guestUuid, MultipartFile file) {
        Member member = getMemberByGuestUuid(guestUuid);

        String timetableData;
        try {
            byte[] imageBytes = file.getBytes();
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "timetable.jpg";
            String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

            TimetableAiClient.TimetableParseResponse aiResponse =
                    timetableAiClient.parseTimetable(imageBytes, fileName, contentType);

            List<?> classes = aiResponse.classes() != null ? aiResponse.classes() : List.of();
            timetableData = objectMapper.writeValueAsString(Map.of("classes", classes));

        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            log.warn("시간표 AI 파싱 실패 - memberId={}", member.getId(), e);
            throw new MemberException(MemberErrorCode.TIMETABLE_PARSE_FAILED);
        }

        saveTimetable(member.getId(), timetableData);
        return TimetableParseResDto.of(timetableData);
    }

    @Transactional
    public void saveTimetable(Long memberId, String timetableData) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.updateTimetable(timetableData);
    }

    private Member getMemberByGuestUuid(String guestUuid) {
        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        if (member.isWithdrawn()) {
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAWN);
        }
        return member;
    }
}
