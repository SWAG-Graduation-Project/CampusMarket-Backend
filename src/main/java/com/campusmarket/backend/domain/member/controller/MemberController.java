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
import com.campusmarket.backend.domain.member.service.MemberService;
import com.campusmarket.backend.domain.member.service.TimetableAiService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final TimetableAiService timetableAiService;

    @Override
    @GetMapping("/random-nickname")
    public ApiResponse<RandomNicknameResDto> getRandomNickname() {
        return ApiResponse.success(memberService.getRandomNickname());
    }

    @Override
    @GetMapping("/nickname/check")
    public ApiResponse<NicknameCheckResDto> checkNickname(@RequestParam String nickname) {
        return ApiResponse.success(memberService.checkNickname(nickname));
    }

    @Override
    @PostMapping("/profile")
    public ApiResponse<MemberProfileResDto> createProfile(
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileCreateReqDto reqDto
    ) {
        return ApiResponse.success(memberService.createProfile(guestUuid, reqDto));
    }

    @Override
    @PatchMapping("/profile")
    public ApiResponse<MemberProfileResDto> updateProfile(
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileUpdateReqDto reqDto
    ) {
        return ApiResponse.success(memberService.updateProfile(guestUuid, reqDto));
    }

    @Override
    @GetMapping("/profile")
    public ApiResponse<MemberProfileResDto> getProfile(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(memberService.getProfile(guestUuid));
    }

    @Override
    @GetMapping("/onboarding-status")
    public ApiResponse<OnboardingStatusResDto> getOnboardingStatus(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(memberService.getOnboardingStatus(guestUuid));
    }

    @Override
    @PostMapping("/onboarding/skip")
    public ApiResponse<OnboardingStatusResDto> skipOnboarding(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(memberService.skipOnboarding(guestUuid));
    }

    @Override
    @GetMapping("/locker")
    public ApiResponse<LockerResDto> getLocker(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(memberService.getLocker(guestUuid));
    }

    @Override
    @PatchMapping("/locker")
    public ApiResponse<LockerResDto> updateLocker(
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody LockerUpdateReqDto reqDto
    ) {
        return ApiResponse.success(memberService.updateLocker(guestUuid, reqDto));
    }

    @Override
    @DeleteMapping("/locker")
    public ApiResponse<Void> deleteLocker(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        memberService.deleteLocker(guestUuid);
        return ApiResponse.success(null);
    }

    @Override
    @DeleteMapping
    public ApiResponse<Void> withdraw(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        memberService.withdraw(guestUuid);
        return ApiResponse.success(null);
    }

    @Override
    @GetMapping("/timetable")
    public ApiResponse<TimetableParseResDto> getTimetable(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(memberService.getTimetable(guestUuid));
    }

    @Override
    @GetMapping("/timetable/classes/{classIndex}")
    public ApiResponse<TimetableClassResDto> getTimetableClass(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable int classIndex
    ) {
        return ApiResponse.success(memberService.getTimetableClass(guestUuid, classIndex));
    }

    @Override
    @PatchMapping("/timetable/classes/{classIndex}")
    public ApiResponse<TimetableParseResDto> updateTimetableClass(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable int classIndex,
            @RequestBody TimetableClassUpdateReqDto reqDto
    ) {
        return ApiResponse.success(memberService.updateTimetableClass(guestUuid, classIndex, reqDto));
    }

    @Override
    @DeleteMapping("/timetable/classes/{classIndex}")
    public ApiResponse<TimetableParseResDto> deleteTimetableClass(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable int classIndex
    ) {
        return ApiResponse.success(memberService.deleteTimetableClass(guestUuid, classIndex));
    }

    @Override
    @PostMapping(value = "/timetable/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<TimetableParseResDto> parseTimetable(
            @RequestHeader("guestUuid") String guestUuid,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.success(timetableAiService.parseTimetableImage(guestUuid, file));
    }
}
