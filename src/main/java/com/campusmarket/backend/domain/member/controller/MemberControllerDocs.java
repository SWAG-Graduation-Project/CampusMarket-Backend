package com.campusmarket.backend.domain.member.controller;

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
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "랜덤 닉네임 추천", description = "가입 시 사용할 랜덤 닉네임을 추천합니다.")
    ApiResponse<RandomNicknameResDto> getRandomNickname();

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임의 사용 가능 여부를 확인합니다.")
    ApiResponse<NicknameCheckResDto> checkNickname(
            @Parameter(description = "중복 확인할 닉네임", required = true)
            @RequestParam String nickname
    );

    @Operation(summary = "회원 프로필 최초 입력", description = "회원 온보딩 과정에서 프로필 정보를 최초 저장합니다.")
    ApiResponse<MemberProfileResDto> createProfile(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileCreateReqDto reqDto
    );

    @Operation(summary = "회원 프로필 수정", description = "회원 프로필 정보를 수정합니다.")
    ApiResponse<MemberProfileResDto> updateProfile(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileUpdateReqDto reqDto
    );

    @Operation(summary = "내 회원 프로필 조회", description = "현재 회원의 프로필 정보를 조회합니다.")
    ApiResponse<MemberProfileResDto> getProfile(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "회원 온보딩 상태 조회", description = "현재 회원의 온보딩 완료 여부를 조회합니다.")
    ApiResponse<OnboardingStatusResDto> getOnboardingStatus(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "회원 온보딩 스킵", description = "현재 회원의 온보딩을 스킵 처리합니다.")
    ApiResponse<OnboardingStatusResDto> skipOnboarding(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "내 사물함 조회", description = "등록된 사물함 위치 정보를 조회")
    ApiResponse<LockerResDto> getLocker(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "사물함 위치 저장/수정", description = "내 사물함 위치를 저장하거나 수정")
    ApiResponse<LockerResDto> updateLocker(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody LockerUpdateReqDto reqDto
    );

    @Operation(summary = "사물함 해제", description = "등록된 사물함 정보를 삭제")
    ApiResponse<Void> deleteLocker(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "회원 탈퇴", description = "회원 상태를 탈퇴로 변경. 데이터는 삭제 X.")
    ApiResponse<Void> withdraw(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "내 시간표 조회", description = "저장된 시간표 이미지 URL과 파싱 데이터를 조회")
    ApiResponse<TimetableParseResDto> getTimetable(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "시간표 특정 수업 조회", description = "classes 배열에서 해당 인덱스의 수업 정보를 조회")
    ApiResponse<TimetableClassResDto> getTimetableClass(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Parameter(description = "조회할 수업의 배열 인덱스 (0부터 시작)", required = true)
            @PathVariable int classIndex
    );

    @Operation(summary = "시간표 특정 수업 수정", description = "classes 배열에서 해당 인덱스의 수업 정보를 수정 (null 필드는 유지)")
    ApiResponse<TimetableParseResDto> updateTimetableClass(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Parameter(description = "수정할 수업의 배열 인덱스 (0부터 시작)", required = true)
            @PathVariable int classIndex,
            @RequestBody TimetableClassUpdateReqDto reqDto
    );

    @Operation(summary = "시간표 특정 수업 삭제", description = "classes 배열에서 해당 인덱스의 수업을 삭제")
    ApiResponse<TimetableParseResDto> deleteTimetableClass(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Parameter(description = "삭제할 수업의 배열 인덱스 (0부터 시작)", required = true)
            @PathVariable int classIndex
    );

    @Operation(summary = "시간표 사진 AI 파싱", description = "시간표 이미지를 업로드하면 AI가 수업 정보를 파싱하여 저장.")
    ApiResponse<TimetableParseResDto> parseTimetable(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Parameter(description = "시간표 이미지 파일", required = true) MultipartFile file
    );
}
