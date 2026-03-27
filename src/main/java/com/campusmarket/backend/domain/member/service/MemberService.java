package com.campusmarket.backend.domain.member.service;

import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.constant.RandomNicknameWordType;
import com.campusmarket.backend.domain.member.dto.request.LockerUpdateReqDto;
import com.campusmarket.backend.domain.member.dto.request.MemberProfileCreateReqDto;
import com.campusmarket.backend.domain.member.dto.request.MemberProfileUpdateReqDto;
import com.campusmarket.backend.domain.member.dto.request.TimetableClassUpdateReqDto;
import com.campusmarket.backend.domain.member.dto.response.TimetableClassResDto;
import com.campusmarket.backend.domain.member.dto.response.LockerResDto;
import com.campusmarket.backend.domain.member.dto.response.MemberProfileResDto;
import com.campusmarket.backend.domain.member.dto.response.NicknameCheckResDto;
import com.campusmarket.backend.domain.member.dto.response.OnboardingStatusResDto;
import com.campusmarket.backend.domain.member.dto.response.RandomNicknameResDto;
import com.campusmarket.backend.domain.member.dto.response.TimetableParseResDto;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.entity.RandomNickname;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberProfileRepository;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.member.repository.RandomNicknameRepository;
import com.campusmarket.backend.domain.chat.service.ChatSystemMessageService;
import com.campusmarket.backend.global.file.FileStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberProfileRepository memberProfileRepository;
    private final RandomNicknameRepository randomNicknameRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;
    private final ChatSystemMessageService chatSystemMessageService;

    // 후보 생성 후 셔플하여 순회
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,12}$");
    private static final int MIN_SUFFIX_NUMBER = 10;
    private static final int MAX_SUFFIX_NUMBER = 99;

    /**
     * 랜덤 닉네임 추천
     * 형용사 + 명사 + 두 자리 숫자 조합으로 닉네임 생성
     * 예: 아름다운덕새27
     */
    public RandomNicknameResDto getRandomNickname() {
        List<RandomNickname> adjectives =
                randomNicknameRepository.findByWordTypeAndActiveTrue(RandomNicknameWordType.ADJECTIVE);
        List<RandomNickname> nouns =
                randomNicknameRepository.findByWordTypeAndActiveTrue(RandomNicknameWordType.NOUN);

        if (adjectives.isEmpty() || nouns.isEmpty()) {
            throw new MemberException(MemberErrorCode.RANDOM_NICKNAME_WORD_NOT_FOUND);
        }

        List<String> candidates = buildValidNicknameCandidates(adjectives, nouns);

        if (candidates.isEmpty()) {
            throw new MemberException(MemberErrorCode.RANDOM_NICKNAME_GENERATION_FAILED);
        }

        Collections.shuffle(candidates, new SecureRandom());

        for (String nickname : candidates) {
            if (!memberProfileRepository.existsByNicknameIgnoreCase(nickname)) {
                return RandomNicknameResDto.from(nickname);
            }
        }

        throw new MemberException(MemberErrorCode.RANDOM_NICKNAME_GENERATION_FAILED);
    }

    /**
     * 닉네임 입력값 정리
     * null 체크 + trim 처리
     */
    private String normalizeNickname(String rawNickname) {
        if (rawNickname == null) {
            throw new MemberException(MemberErrorCode.INVALID_NICKNAME);
        }
        return rawNickname.trim();
    }

    //닉네임 형식 검증
    private void validateNickname(String nickname) {
        if (!isValidNicknameFormat(nickname)) {
            throw new MemberException(MemberErrorCode.INVALID_NICKNAME);
        }
    }

    /**
     * 사용자가 입력한 닉네임 검사
     * 1. 공백 정리
     * 2. 형식 검사
     * 3. 중복 검사
     */
    public NicknameCheckResDto checkNickname(String rawNickname) {
        String nickname = normalizeNickname(rawNickname);
        validateNickname(nickname);

        boolean available = !memberProfileRepository.existsByNicknameIgnoreCase(nickname);
        return NicknameCheckResDto.of(nickname, available);
    }

    private boolean isValidNicknameFormat(String nickname) {
        return nickname != null
                && !nickname.isBlank()
                && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    private List<String> buildValidNicknameCandidates(List<RandomNickname> adjectives,
                                                      List<RandomNickname> nouns) {
        List<String> candidates = new ArrayList<>();

        for (RandomNickname adjective : adjectives) {
            for (RandomNickname noun : nouns) {
                for (int number = MIN_SUFFIX_NUMBER; number <= MAX_SUFFIX_NUMBER; number++) {
                    String nickname = adjective.getWord() + noun.getWord() + number;

                    if (isValidNicknameFormat(nickname)) {
                        candidates.add(nickname);
                    }
                }
            }
        }

        return candidates;
    }

    /**
     * 회원 프로필 최초 입력
     */
    @Transactional
    public MemberProfileResDto createProfile(String guestUuid, MemberProfileCreateReqDto reqDto){
        Member member = findMemberByGuestUuid(guestUuid);

        if (Boolean.TRUE.equals(member.getProfileCompleted())){
            throw new MemberException(MemberErrorCode.PROFILE_ALREADY_COMPLETED);
        }

        String nickname = validateAvailableNickname(reqDto.nickname(), member.getId());

        member.createProfile(
                nickname,
                reqDto.profileImageUrl(),
                reqDto.lockerName(),
                reqDto.timetableData()
        );

        return toMemberProfileResDto(member);
    }

    /**
     * 회원 프로필 수정
     */
    @Transactional
    public MemberProfileResDto updateProfile(String guestUuid, MemberProfileUpdateReqDto reqDto) {
        Member member = findMemberByGuestUuid(guestUuid);

        if (reqDto.nickname() == null
                && reqDto.profileImageUrl() == null
                && reqDto.lockerName() == null
                && reqDto.timetableData() == null) {
            throw new MemberException(MemberErrorCode.INVALID_PROFILE_UPDATE_REQUEST);
        }

        String nickname = reqDto.nickname();

        if (nickname != null) {
            nickname = validateAvailableNickname(nickname, member.getId());
        }

        // 새 프로필 이미지로 교체 시 기존 S3 이미지 삭제
        if (reqDto.profileImageUrl() != null && !reqDto.profileImageUrl().equals(member.getProfileImageUrl())) {
            fileStorageService.deleteByUrl(member.getProfileImageUrl());
        }

        member.updateProfile(
                nickname,
                reqDto.profileImageUrl(),
                reqDto.lockerName(),
                reqDto.timetableData()
        );

        return toMemberProfileResDto(member);
    }

    /**
     * 내 회원 프로필 조회
     */
    public MemberProfileResDto getProfile(String guestUuid) {
        Member member = findMemberByGuestUuid(guestUuid);
        return toMemberProfileResDto(member);
    }

    /**
     * 사물함 위치 저장/수정
     */
    @Transactional
    public LockerResDto updateLocker(String guestUuid, LockerUpdateReqDto reqDto) {
        Member member = findMemberByGuestUuid(guestUuid);
        String lockerName = reqDto.building() + " " + reqDto.floor() + " " + reqDto.major()
                + " " + reqDto.lockerGroup() + "/" + reqDto.row() + "/" + reqDto.col();
        member.updateLocker(lockerName, reqDto.building(), reqDto.floor(),
                reqDto.major(), reqDto.lockerGroup(), reqDto.row(), reqDto.col());
        return LockerResDto.from(member);
    }

    /**
     * 사물함 조회
     */
    public LockerResDto getLocker(String guestUuid) {
        Member member = findMemberByGuestUuid(guestUuid);
        return LockerResDto.from(member);
    }

    /**
     * 사물함 해제
     */
    @Transactional
    public void deleteLocker(String guestUuid) {
        Member member = findMemberByGuestUuid(guestUuid);
        member.deleteLocker();
    }

    /**
     * 시간표 조회
     */
    public TimetableParseResDto getTimetable(String guestUuid) {
        Member member = findMemberByGuestUuid(guestUuid);
        return TimetableParseResDto.of(member.getTimetableData());
    }

    /**
     * 시간표 특정 수업 조회 (배열 인덱스 기반)
     */
    public TimetableClassResDto getTimetableClass(String guestUuid, int classIndex) {
        Member member = findMemberByGuestUuid(guestUuid);

        String timetableData = member.getTimetableData();
        if (timetableData == null || timetableData.isBlank()) {
            throw new MemberException(MemberErrorCode.TIMETABLE_NOT_FOUND);
        }

        try {
            JsonNode root = objectMapper.readTree(timetableData);
            ArrayNode classes = (ArrayNode) root.get("classes");

            if (classes == null || classIndex < 0 || classIndex >= classes.size()) {
                throw new MemberException(MemberErrorCode.TIMETABLE_CLASS_NOT_FOUND);
            }

            return objectMapper.treeToValue(classes.get(classIndex), TimetableClassResDto.class);

        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.TIMETABLE_PARSE_FAILED);
        }
    }

    /**
     * 시간표 특정 수업 수정 (배열 인덱스 기반)
     */
    @Transactional
    public TimetableParseResDto updateTimetableClass(String guestUuid, int classIndex, TimetableClassUpdateReqDto reqDto) {
        Member member = findMemberByGuestUuid(guestUuid);

        String timetableData = member.getTimetableData();
        if (timetableData == null || timetableData.isBlank()) {
            throw new MemberException(MemberErrorCode.TIMETABLE_NOT_FOUND);
        }

        try {
            JsonNode root = objectMapper.readTree(timetableData);
            ArrayNode classes = (ArrayNode) root.get("classes");

            if (classes == null || classIndex < 0 || classIndex >= classes.size()) {
                throw new MemberException(MemberErrorCode.TIMETABLE_CLASS_NOT_FOUND);
            }

            ObjectNode target = (ObjectNode) classes.get(classIndex);

            // 수정 후 최종 상태 계산 (null이면 기존 값 유지)
            String finalDay       = reqDto.day()       != null ? reqDto.day()       : target.path("day").asText(null);
            String finalStartTime = reqDto.startTime() != null ? reqDto.startTime() : target.path("start_time").asText(null);
            String finalEndTime   = reqDto.endTime()   != null ? reqDto.endTime()   : target.path("end_time").asText(null);

            // 시간 겹침 검사
            if (finalDay != null && finalStartTime != null && finalEndTime != null) {
                LocalTime newStart = LocalTime.parse(finalStartTime);
                LocalTime newEnd   = LocalTime.parse(finalEndTime);

                for (int i = 0; i < classes.size(); i++) {
                    if (i == classIndex) continue;
                    JsonNode other = classes.get(i);
                    if (!finalDay.equals(other.path("day").asText())) continue;

                    String otherStart = other.path("start_time").asText(null);
                    String otherEnd   = other.path("end_time").asText(null);
                    if (otherStart == null || otherEnd == null) continue;

                    if (newStart.isBefore(LocalTime.parse(otherEnd)) && newEnd.isAfter(LocalTime.parse(otherStart))) {
                        throw new MemberException(MemberErrorCode.TIMETABLE_CLASS_TIME_CONFLICT);
                    }
                }
            }

            if (reqDto.name() != null)      target.put("name", reqDto.name());
            if (reqDto.day() != null)       target.put("day", reqDto.day());
            if (reqDto.startTime() != null) target.put("start_time", reqDto.startTime());
            if (reqDto.endTime() != null)   target.put("end_time", reqDto.endTime());
            if (reqDto.location() != null)  target.put("location", reqDto.location());

            ObjectNode updated = objectMapper.createObjectNode();
            updated.set("classes", classes);
            String updatedData = objectMapper.writeValueAsString(updated);

            member.updateTimetable(updatedData);
            return TimetableParseResDto.of(updatedData);

        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.TIMETABLE_PARSE_FAILED);
        }
    }

    /**
     * 시간표 특정 수업 삭제 (배열 인덱스 기반)
     */
    @Transactional
    public TimetableParseResDto deleteTimetableClass(String guestUuid, int classIndex) {
        Member member = findMemberByGuestUuid(guestUuid);

        String timetableData = member.getTimetableData();
        if (timetableData == null || timetableData.isBlank()) {
            throw new MemberException(MemberErrorCode.TIMETABLE_NOT_FOUND);
        }

        try {
            JsonNode root = objectMapper.readTree(timetableData);
            ArrayNode classes = (ArrayNode) root.get("classes");

            if (classes == null || classIndex < 0 || classIndex >= classes.size()) {
                throw new MemberException(MemberErrorCode.TIMETABLE_CLASS_NOT_FOUND);
            }

            classes.remove(classIndex);

            ObjectNode updated = objectMapper.createObjectNode();
            updated.set("classes", classes);
            String updatedData = objectMapper.writeValueAsString(updated);

            member.updateTimetable(updatedData);
            return TimetableParseResDto.of(updatedData);

        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            throw new MemberException(MemberErrorCode.TIMETABLE_PARSE_FAILED);
        }
    }

    /**
     * 회원 탈퇴 - 상태를 WITHDRAWN으로 변경 (데이터 삭제 없음)
     */
    @Transactional
    public void withdraw(String guestUuid) {
        Member member = findMemberByGuestUuid(guestUuid);
        chatSystemMessageService.sendWithdrawnUserMessage(member.getId());
        member.withdraw();
    }

    /**
     * guestUuid로 회원 찾기 (탈퇴 회원 차단)
     */
    private Member findMemberByGuestUuid(String guestUuid) {
        if (guestUuid == null || guestUuid.isBlank()) {
            throw new MemberException(MemberErrorCode.INVALID_GUEST_UUID);
        }

        try {
            UUID.fromString(guestUuid);
        } catch (IllegalArgumentException e) {
            throw new MemberException(MemberErrorCode.INVALID_GUEST_UUID_FORMAT);
        }

        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        if (member.isWithdrawn()) {
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAWN);
        }
        return member;
    }

    /**
     * 저장 직전 닉네임 최종 검증
     */
    private String validateAvailableNickname(String rawNickname, Long currentMemberId) {
        String nickname = normalizeNickname(rawNickname);
        validateNickname(nickname);

        memberRepository.findByNicknameIgnoreCase(nickname)
                .ifPresent(foundMember -> {
                    if (!foundMember.getId().equals(currentMemberId)) {
                        throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
                    }
                });

        return nickname;
    }

    /**
     * 프로필 응답 DTO 변환
     */
    private MemberProfileResDto toMemberProfileResDto(Member member) {
        return MemberProfileResDto.of(
                member.getId(),
                member.getGuestUuid(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getLockerName(),
                member.getTimetableData(),
                member.getProfileCompleted()
        );
    }

    /**
     * 온보딩 상태 조회
     */
    public OnboardingStatusResDto getOnboardingStatus(String guestUuid){
        Member member = findMemberByGuestUuid(guestUuid);

        return toOnboardingStatusResDto(member);
    }

    /**
     * 온보딩 스킵 처리
     */
    @Transactional
    public OnboardingStatusResDto skipOnboarding(String guestUuid){
        Member member = findMemberByGuestUuid(guestUuid);

        member.skipOnboarding();

        return toOnboardingStatusResDto(member);
    }

    private OnboardingStatusResDto toOnboardingStatusResDto(Member member) {
        boolean profileCompleted = Boolean.TRUE.equals(member.getProfileCompleted());
        boolean termsCompleted = Boolean.TRUE.equals(member.getTermsCompleted());
        boolean onboardingSkipped = Boolean.TRUE.equals(member.getOnboardingSkipped());

        boolean canEnterMain = (profileCompleted && termsCompleted) || onboardingSkipped;

        return OnboardingStatusResDto.of(
                profileCompleted,
                termsCompleted,
                canEnterMain
        );
    }


}