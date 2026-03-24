package com.campusmarket.backend.domain.member.dto.response;

public record OnboardingStatusResDto(
        Boolean profileCompleted,
        Boolean termsCompleted,
        Boolean canEnterMain
) {
    public static OnboardingStatusResDto of(
            Boolean profileCompleted,
            Boolean termsCompleted,
            Boolean canEnterMain
    ){
        return new OnboardingStatusResDto(
                profileCompleted,
                termsCompleted,
                canEnterMain
        );
    }
}
