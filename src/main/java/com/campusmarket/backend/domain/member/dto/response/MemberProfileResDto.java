package com.campusmarket.backend.domain.member.dto.response;

public record MemberProfileResDto(
        Long memberId,
        String guestUuid,
        String nickname,
        String profileImageUrl,
        String lockerName,
        String timetableImageUrl,
        String timetableData,
        Boolean profileCompleted
) {
    public static MemberProfileResDto of(
            Long memberId,
            String guestUuid,
            String nickname,
            String profileImageUrl,
            String lockerName,
            String timetableImageUrl,
            String timetableData,
            Boolean profileCompleted
    ){
        return new MemberProfileResDto(
                memberId,
                guestUuid,
                nickname,
                profileImageUrl,
                lockerName,
                timetableImageUrl,
                timetableData,
                profileCompleted
        );
    }
}
