package com.campusmarket.backend.domain.member.service;

import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.constant.RandomNicknameWordType;
import com.campusmarket.backend.domain.member.dto.response.NicknameCheckResDto;
import com.campusmarket.backend.domain.member.dto.response.RandomNicknameResDto;
import com.campusmarket.backend.domain.member.entity.RandomNickname;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberProfileRepository;
import com.campusmarket.backend.domain.member.repository.RandomNicknameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberProfileRepository memberProfileRepository;
    private final RandomNicknameRepository randomNicknameRepository;

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
    private String normalizeNickname(String rawNickname){
        if (rawNickname == null){
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
    public NicknameCheckResDto checkNickname(String rawNickname){
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


}
